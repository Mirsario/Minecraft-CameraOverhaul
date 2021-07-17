package mirsario.cameraoverhaul.common.configuration;

import mirsario.cameraoverhaul.core.configuration.*;

public class ConfigData extends BaseConfigData
{
	public static final int ConfigVersion = 1;

	public boolean enabled = true;
	public float strafingRollFactor = 1.0f;
	public float strafingRollFactorWhenFlying = -1.0f;
	public float strafingRollFactorWhenSwimming = -1.0f;
	public float yawDeltaRollFactor = 1.0f;
	public float verticalVelocityPitchFactor = 1.0f;
	public float forwardVelocityPitchFactor = 1.0f;
}