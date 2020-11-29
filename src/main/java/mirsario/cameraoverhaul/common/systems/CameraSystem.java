package mirsario.cameraoverhaul.common.systems;

import net.minecraft.client.render.*;
import net.minecraft.util.math.*;
import mirsario.cameraoverhaul.common.*;
import mirsario.cameraoverhaul.common.configuration.*;
import mirsario.cameraoverhaul.core.callbacks.*;
import mirsario.cameraoverhaul.core.structures.*;
import mirsario.cameraoverhaul.core.utils.*;

public final class CameraSystem implements CameraUpdateCallback
{
	private static double prevForwardVelocityPitchOffset;
	private static double prevVerticalVelocityPitchOffset;
	private static double prevStrafingRollOffset;
	private static double prevCameraYaw;
	//private static double prevYawDeltaRollOffset;
	private static double yawDeltaRollOffset;
	private static double yawDeltaRollTargetOffset;
	private static double lerpSpeed = 1d;

	public CameraSystem()
	{
		CameraUpdateCallback.EVENT.Register(this);
		CameraOverhaul.Logger.info("CameraOverhaul - CameraSystem is ready.");
	}

	@Override
	public Transform OnCameraUpdate(Camera camera, Transform cameraTransform, float deltaTime)
	{
		ConfigData config = CameraOverhaul.instance.config;

		if(!config.enabled) {
			return cameraTransform;
		}

		Vec3d velocity = camera.getFocusedEntity().getVelocity();
		Vec2f relativeXZVelocity = Vec2fUtils.Rotate(new Vec2f((float)velocity.x, (float)velocity.z), 360f - (float)cameraTransform.eulerRot.y);

		//X
		VerticalVelocityPitchOffset(cameraTransform, velocity, relativeXZVelocity, deltaTime, config.verticalVelocityPitchFactor);
		ForwardVelocityPitchOffset(cameraTransform, velocity, relativeXZVelocity, deltaTime, config.forwardVelocityPitchFactor);
		//Z
		YawDeltaRollOffset(cameraTransform, velocity, relativeXZVelocity, deltaTime, config.yawDeltaRollFactor);
		StrafingRollOffset(cameraTransform, velocity, relativeXZVelocity, deltaTime, config.strafingRollFactor);

		prevCameraYaw = cameraTransform.eulerRot.y;
		
		return cameraTransform;
	}

	private void VerticalVelocityPitchOffset(Transform transform, Vec3d velocity, Vec2f relativeXZVelocity, double deltaTime, float intensity)
	{
		double verticalVelocityPitchOffset = velocity.y * 2.75d;

		if(velocity.y < 0f) {
			verticalVelocityPitchOffset *= 2.25d;
		}

		prevVerticalVelocityPitchOffset = verticalVelocityPitchOffset = MathUtils.Lerp(prevVerticalVelocityPitchOffset, verticalVelocityPitchOffset, deltaTime * lerpSpeed);
		
		transform.eulerRot = transform.eulerRot.add(verticalVelocityPitchOffset * intensity, 0d, 0d);
	}

	private void ForwardVelocityPitchOffset(Transform transform, Vec3d velocity, Vec2f relativeXZVelocity, double deltaTime, float intensity)
	{
		double forwardVelocityPitchOffset = relativeXZVelocity.y * 5d;

		prevForwardVelocityPitchOffset = forwardVelocityPitchOffset = MathUtils.Lerp(prevForwardVelocityPitchOffset, forwardVelocityPitchOffset, deltaTime * lerpSpeed);
		
		transform.eulerRot = transform.eulerRot.add(forwardVelocityPitchOffset * intensity, 0d, 0d);
	}

	private void YawDeltaRollOffset(Transform transform, Vec3d velocity, Vec2f relativeXZVelocity, double deltaTime, float intensity)
	{
		double yawDelta = prevCameraYaw - transform.eulerRot.y;

		if(yawDelta > 180) {
			yawDelta = 360 - yawDelta;
		} else if(yawDelta < -180) {
			yawDelta = -360 - yawDelta;
		}

		yawDeltaRollTargetOffset += yawDelta * 0.07d;
		yawDeltaRollOffset = MathUtils.Lerp(yawDeltaRollOffset, yawDeltaRollTargetOffset, (double)deltaTime * lerpSpeed * 10d);

		transform.eulerRot = transform.eulerRot.add(0d, 0d, yawDeltaRollOffset * intensity);
		
		yawDeltaRollTargetOffset = MathUtils.Lerp(yawDeltaRollTargetOffset, 0d, deltaTime * 0.35d);
	}

	private void StrafingRollOffset(Transform transform, Vec3d velocity, Vec2f relativeXZVelocity, double deltaTime, float intensity)
	{
		double strafingRollOffset = -relativeXZVelocity.x * 15d;
		
		prevStrafingRollOffset = strafingRollOffset = MathUtils.Lerp(prevStrafingRollOffset, strafingRollOffset, (double)deltaTime * lerpSpeed);
		
		transform.eulerRot = transform.eulerRot.add(0d, 0d, strafingRollOffset * intensity);
	}
}
