// Copyright 2020-2026 Mirsario & Contributors.
// Released under the GNU General Public License 3.0.
// See LICENSE.md for details.

package mirsario.cameraoverhaul;

// It's easier to calculate this on our own than to rely on MC's internals.
public final class TimeSystem {
	private static long previous;
	private static long current;
	private static double deltaTime;
	private static double accumulatedTime;

	public static double getTime() {
		return accumulatedTime;
	}

	public static double getDeltaTime() {
		return deltaTime;
	}

	public static void update() {
		previous = current;
		current = System.nanoTime();

		if (previous <= 0d) {
			previous = current - 1;
		}

		deltaTime = (current - previous) / 1_000_000_000d;
		accumulatedTime += deltaTime;
	}
}
