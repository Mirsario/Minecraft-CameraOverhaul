// Copyright 2020-2026 Mirsario & Contributors.
// Released under the GNU General Public License 3.0.
// See LICENSE.md for details.

package mirsario.cameraoverhaul;

import mirsario.cameraoverhaul.configuration.*;
import mirsario.cameraoverhaul.utilities.*;
import org.joml.SimplexNoise;
import org.joml.Vector3d;
import org.joml.Math;

@SuppressWarnings("unused")
public final class ScreenShakes {
	public static final class Slot {
		public float trauma;
		public float radius;
		public float frequency;
		public float lengthInSeconds;
		public final Vector3d position = new Vector3d(Double.POSITIVE_INFINITY); // Infinity for positionless.
		double startTime;
		short version = 1;

		public boolean hasPosition() { return position.isFinite(); }

		private Slot() { }

		public void setDefaults() {
			trauma = 0.5f;
			radius = 10.0f;
			frequency = 1.0f;
			lengthInSeconds = 2.0f;
			position.set(Double.POSITIVE_INFINITY);
			startTime = TimeSystem.getTime();
		}
	}

	private static final Slot dummyInstance = new Slot();
	private static final Vector3d calculatedOffset = new Vector3d();
	private static final Slot[] instances = new Slot[64];
	private static long instanceMask;

	private ScreenShakes() {}

	public static void onCameraUpdate(CameraContext context, double deltaTime) {
		var cfg = Configuration.get();

		getNoiseAtPosition(context.transform.position, calculatedOffset);
		calculatedOffset.mul(cfg.general.screenShakesMaxIntensity);
	}
	public static void modifyCameraTransform(Transform transform) {
		transform.eulerRot.add(calculatedOffset);
	}

	private static int extractIndex(long handle) { return (int)handle; }
	private static int extractVersion(long handle) { return (int)(handle >> 32); }
	private static long constructHandle(int index, int version) { return ((long)index) | ((long)version << 32); }
	private static boolean isHandleValid(long handle) { return handle != 0L && instances[extractIndex(handle)].version == extractVersion(handle); }

	public static Slot get(long handle) { return isHandleValid(handle) ? instances[extractIndex(handle)] : dummyInstance; }
	public static Slot createDirect() { return get(create()); }
	public static long create() {
		if (instanceMask == Long.MAX_VALUE) return 0L;

		int index = Long.numberOfTrailingZeros(~instanceMask);
		if (instances[index] == null) instances[index] = new Slot();
		int version = instances[index].version;

		instanceMask |= 1L << index;
		instances[index].setDefaults();

		return constructHandle(index, version);
	}
	public static long validate(long handle) { return !isHandleValid(handle) ? create() : handle; }
	public static long recreate(long handle) {
		if (!isHandleValid(handle)) return create();
		get(handle).setDefaults();
		return handle;
	}

	public static void getNoiseAtPosition(Vector3d position, Vector3d noise) {
		var cfg = Configuration.get();
		var mask = instanceMask;
		double time = TimeSystem.getTime();
		float sampleBase = (float)(time * cfg.general.screenShakesMaxFrequency);

		float total = 0f;
		noise.set(0.0);

		while (mask != 0L) {
			int index = Long.numberOfTrailingZeros(mask);
			mask &= ~(1L << index);

			var ss = instances[index];
			float progress = ss.lengthInSeconds > 0.0 ? (float)Math.clamp((time - ss.startTime) / ss.lengthInSeconds, 0.0, 1.0) : 1f;
			if (progress >= 1f) {
				// Free stale entry.
				instanceMask &= ~(1L << index);
				ss.version++;
				continue;
			}

			float decay = 1f - progress;
			float intensity = Math.clamp(ss.trauma, 0f, 1f) * (decay * decay);

			if (ss.hasPosition()) {
				float distance = (float)position.distance(ss.position);
				float distanceFactor = 1f - Math.min(1f, distance / ss.radius);
				intensity *= (distanceFactor * distanceFactor);
			}

			if (intensity <= 0f || !Float.isFinite(intensity)) continue;

			float sampleStep = sampleBase * ss.frequency;
			noise.add(
				SimplexNoise.noise(sampleStep, -69) * intensity,
				SimplexNoise.noise(sampleStep, -420) * intensity,
				SimplexNoise.noise(sampleStep, -1337) * intensity
			);
			total += intensity;
		}

		// Don't go overboard.
		if (total > 1.0) noise.div(total);
	}
}
