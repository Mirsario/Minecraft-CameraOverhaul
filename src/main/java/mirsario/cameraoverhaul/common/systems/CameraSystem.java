package mirsario.cameraoverhaul.common.systems;

import net.minecraft.client.render.*;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.*;
import net.minecraft.util.math.*;
import mirsario.cameraoverhaul.common.*;
import mirsario.cameraoverhaul.common.configuration.*;
import mirsario.cameraoverhaul.core.callbacks.*;
import mirsario.cameraoverhaul.core.structures.*;
import mirsario.cameraoverhaul.core.utils.*;

public final class CameraSystem implements CameraUpdateCallback, ModifyCameraTransformCallback
{
	private static double prevForwardVelocityPitchOffset;
	private static double prevVerticalVelocityPitchOffset;
	private static double prevStrafingRollOffset;
	private static double prevCameraYaw;
	//private static double prevYawDeltaRollOffset;
	private static double yawDeltaRollOffset;
	private static double yawDeltaRollTargetOffset;
	private static Transform offsetTransform = new Transform();

	public CameraSystem()
	{
		CameraUpdateCallback.EVENT.Register(this);
		ModifyCameraTransformCallback.EVENT.Register(this);

		CameraOverhaul.Logger.info("CameraOverhaul: CameraSystem is ready.");
	}

	@Override
	public void OnCameraUpdate(Entity focusedEntity, Camera camera, Transform cameraTransform, float deltaTime)
	{
		boolean isFlying = false;
		boolean isSwimming = false;
		
		// Update entity info
		if (focusedEntity instanceof PlayerEntity) {
			PlayerEntity playerEntity = (PlayerEntity)focusedEntity;
			
			isFlying = playerEntity.isFallFlying();
			isSwimming = playerEntity.isSwimming();
		}
		
		// Reset the offset transform
		offsetTransform.position = new Vec3d(0d, 0d, 0d);
		offsetTransform.eulerRot = new Vec3d(0d, 0d, 0d);

		ConfigData config = CameraOverhaul.instance.config;

		if(!config.enabled) {
			return;
		}

		float strafingRollFactorToUse = config.strafingRollFactor;
		
		if(isFlying) {
			strafingRollFactorToUse = config.strafingRollFactorWhenFlying;
		} else if(isSwimming) {
			strafingRollFactorToUse = config.strafingRollFactorWhenSwimming;
		}

		Vec3d velocity = camera.getFocusedEntity().getVelocity();
		Vec2f relativeXZVelocity = Vec2fUtils.Rotate(new Vec2f((float)velocity.x, (float)velocity.z), 360f - (float)cameraTransform.eulerRot.y);

		// X
		VerticalVelocityPitchOffset(cameraTransform, offsetTransform, velocity, relativeXZVelocity, deltaTime, config.verticalVelocityPitchFactor, config.verticalVelocityInterpolationSpeed);
		ForwardVelocityPitchOffset(cameraTransform, offsetTransform, velocity, relativeXZVelocity, deltaTime, config.forwardVelocityPitchFactor, config.horizontalVelocityInterpolationSpeed);
		// Z
		YawDeltaRollOffset(cameraTransform, offsetTransform, velocity, relativeXZVelocity, deltaTime, config.yawDeltaRollFactor, config.yawDeltaInterpolationSpeed);
		StrafingRollOffset(cameraTransform, offsetTransform, velocity, relativeXZVelocity, deltaTime, strafingRollFactorToUse, config.horizontalVelocityInterpolationSpeed);

		prevCameraYaw = cameraTransform.eulerRot.y;
	}

	@Override
	public Transform ModifyCameraTransform(Camera camera, Transform transform)
	{
		return new Transform(
			transform.position.add(offsetTransform.position),
			transform.eulerRot.add(offsetTransform.eulerRot)
		);
	}

	private void VerticalVelocityPitchOffset(Transform inputTransform, Transform outputTransform, Vec3d velocity, Vec2f relativeXZVelocity, double deltaTime, float intensity, float lerpSpeed)
	{
		double verticalVelocityPitchOffset = velocity.y * 2.75d;

		if(velocity.y < 0f) {
			verticalVelocityPitchOffset *= 2.25d;
		}

		prevVerticalVelocityPitchOffset = verticalVelocityPitchOffset = MathUtils.Lerp(prevVerticalVelocityPitchOffset, verticalVelocityPitchOffset, deltaTime * lerpSpeed);
		
		outputTransform.eulerRot = outputTransform.eulerRot.add(verticalVelocityPitchOffset * intensity, 0d, 0d);
	}

	private void ForwardVelocityPitchOffset(Transform inputTransform, Transform outputTransform, Vec3d velocity, Vec2f relativeXZVelocity, double deltaTime, float intensity, float lerpSpeed)
	{
		double forwardVelocityPitchOffset = relativeXZVelocity.y * 5d;

		prevForwardVelocityPitchOffset = forwardVelocityPitchOffset = MathUtils.Lerp(prevForwardVelocityPitchOffset, forwardVelocityPitchOffset, deltaTime * lerpSpeed);
		
		outputTransform.eulerRot = outputTransform.eulerRot.add(forwardVelocityPitchOffset * intensity, 0d, 0d);
	}

	private void YawDeltaRollOffset(Transform inputTransform, Transform outputTransform, Vec3d velocity, Vec2f relativeXZVelocity, double deltaTime, float intensity, float lerpSpeed)
	{
		double yawDelta = prevCameraYaw - inputTransform.eulerRot.y;

		if(yawDelta > 180) {
			yawDelta = 360 - yawDelta;
		} else if(yawDelta < -180) {
			yawDelta = -360 - yawDelta;
		}

		yawDeltaRollTargetOffset += yawDelta * 0.07d;
		yawDeltaRollOffset = MathUtils.Lerp(yawDeltaRollOffset, yawDeltaRollTargetOffset, (double)deltaTime * lerpSpeed * 10d);

		outputTransform.eulerRot = outputTransform.eulerRot.add(0d, 0d, yawDeltaRollOffset * intensity);
		
		yawDeltaRollTargetOffset = MathUtils.Lerp(yawDeltaRollTargetOffset, 0d, deltaTime * 0.35d);
	}

	private void StrafingRollOffset(Transform inputTransform, Transform outputTransform, Vec3d velocity, Vec2f relativeXZVelocity, double deltaTime, float intensity, float lerpSpeed)
	{
		double strafingRollOffset = -relativeXZVelocity.x * 15d;
		
		prevStrafingRollOffset = strafingRollOffset = MathUtils.Lerp(prevStrafingRollOffset, strafingRollOffset, (double)deltaTime * lerpSpeed);
		
		outputTransform.eulerRot = outputTransform.eulerRot.add(0d, 0d, strafingRollOffset * intensity);
	}
}
