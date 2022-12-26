package mirsario.cameraoverhaul.common.configuration;

import mirsario.cameraoverhaul.core.configuration.*;

public class ConfigData extends BaseConfigData
{
	public static final int ConfigVersion = 1;

	public boolean enabled = true;
	// Roll factors
	public float strafingRollFactor = 1.0f;
	public float strafingRollFactorWhenFlying = -1.0f;
	public float strafingRollFactorWhenSwimming = -1.0f;
	public float yawDeltaRollFactor = 1.0f;
	// Pitch factors
	public float verticalVelocityPitchFactor = 1.0f;
	public float forwardVelocityPitchFactor = 1.0f;
	// Smoothing factors
	public float horizontalVelocitySmoothingFactor = 0.8f;
	public float verticalVelocitySmoothingFactor = 0.8f;
	public float yawDeltaSmoothingFactor = 0.8f;
	public float yawDeltaDecayFactor = 0.5f;
}