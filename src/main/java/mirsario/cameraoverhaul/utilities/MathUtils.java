// Copyright 2020-2026 Mirsario & Contributors.
// Released under the GNU General Public License 3.0.
// See LICENSE.md for details.

package mirsario.cameraoverhaul.utilities;

public final class MathUtils {
	public static final float DEG_TO_RAD = (float)(Math.PI / 180f);
	public static final float RAD_TO_DEG = 180f / (float)Math.PI;
	// Clamp

	public static float clamp(float value, float min, float max) {
		return value < min ? min : (value > max ? max : value);
	}
	public static double clamp(double value, double min, double max) {
		return value < min ? min : (value > max ? max : value);
	}
	public static float clamp01(float value) {
		return value < 0f ? 0f : (value > 1f ? 1f : value);
	}
	public static double clamp01(double value) {
		return value < 0d ? 0d : (value > 1d ? 1d : value);
	}

	// Lerp

	public static float lerp(float a, float b, float time) {
		return a + (b - a) * clamp01(time);
	}
	public static double lerp(double a, double b, double time) {
		return a + (b - a) * clamp01(time);
	}

	// Framerate-agnostic smoothing.
	// https://www.rorydriscoll.com/2016/03/07/frame-rate-independent-damping-using-lerp

	public static float damp(float source, float destination, float smoothing, float dt) {
		return lerp(source, destination, 1f - (float)Math.pow(smoothing * smoothing, dt));
	}
	public static double damp(double source, double destination, double smoothing, double dt) {
		return lerp(source, destination, 1d - Math.pow(smoothing * smoothing, dt));
	}

	// Step towards

	public static double stepTowards(double current, double target, double step) {
		if (current < target) {
			return Math.min(current + step, target);
		} else if (current > target) {
			return Math.max(current - step, target);
		}

		return current;
	}

	// Unwrap a single angular step into (-180, 180) so seam crossings don't spike by 360
	public static double unwrapStep(double d) {
		d = d % 360.0;
		if (d <= -180.0) d += 360.0;
		else if (d > 180.0) d -= 360.0;
		return d;
	}

	// Wrap any angle difference to (-180, 180) for stable per-frame offsets
	public static double wrapDiff(double d) {
		d = d % 360.0;
		if (d <= -180.0) d += 360.0;
		else if (d > 180.0) d -= 360.0;
		return d;
	}
}
