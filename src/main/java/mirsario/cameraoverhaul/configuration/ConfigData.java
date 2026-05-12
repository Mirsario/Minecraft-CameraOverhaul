// Copyright 2020-2026 Mirsario & Contributors.
// Released under the GNU General Public License 3.0.
// See LICENSE.md for details.

package mirsario.cameraoverhaul.configuration;

public final class ConfigData {
	public static final class General {
		public boolean enabled = true;
		public boolean enableInThirdPerson = true;
		// Turning Roll
		public double turningRollAccumulation = 1.0;
		public double turningRollIntensity = 1.25;
		public double turningRollSmoothing = 1.0;
		// Sway
		public double cameraSwayIntensity = 0.60;
		public double cameraSwayFrequency = 0.16;
		public double cameraSwayFadeInDelay = 0.15;
		public double cameraSwayFadeInLength = 5.0;
		public double cameraSwayFadeOutLength = 0.75;
		// ScreenShakes
		public double screenShakesMaxIntensity = 2.5;
		public double screenShakesMaxFrequency = 6.0;
		public double explosionTrauma = 1.00;
		public double thunderTrauma = 0.05;
		public double handSwingTrauma = 0.03;
	}
	public static final class Contextual {
		public double strafingRollFactor = 10.0;
		public double forwardVelocityPitchFactor = 7.0;
		public double verticalVelocityPitchFactor = 2.5;
		public double horizontalVelocitySmoothingFactor = 1.0;
		public double verticalVelocitySmoothingFactor = 1.0;
		// Mouse smoothing
		public double mouseSmoothing = 0.0;
	}

	public static final int CONFIG_VERSION = 2;

	public int configVersion;
	public General general = new General();
	public Contextual walking = new Contextual();
	public Contextual sprinting = new Contextual();
	public Contextual swimming = new Contextual();
	public Contextual flying = new Contextual();
	public Contextual mounts = new Contextual();
	public Contextual vehicles = new Contextual();

	public ConfigData() {
		// Sprinting
		sprinting.strafingRollFactor = 12.5;
		sprinting.forwardVelocityPitchFactor = 9.5;
		// Flying
		flying.strafingRollFactor *= -1.0;
		// Swimming
		swimming.strafingRollFactor *= -3.0;
		swimming.forwardVelocityPitchFactor *= 3.0;
		swimming.verticalVelocityPitchFactor *= 3.0;
		// Mounts
		mounts.strafingRollFactor *= 2.0;
		mounts.forwardVelocityPitchFactor *= 0.5;
		// Vehicles
		vehicles.strafingRollFactor *= 0.5;
		vehicles.forwardVelocityPitchFactor *= 0.5;
		vehicles.verticalVelocityPitchFactor *= 2.0;
		// Mouse smoothing
		sprinting.mouseSmoothing = 0.8;
		swimming.mouseSmoothing = 1.5;
		flying.mouseSmoothing = 1.0;
		mounts.mouseSmoothing = 1.0;
		vehicles.mouseSmoothing = 0.0;
	}
}
