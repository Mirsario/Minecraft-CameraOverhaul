// Copyright 2020-2026 Mirsario & Contributors.
// Released under the GNU General Public License 3.0.
// See LICENSE.md for details.

package mirsario.cameraoverhaul;

import mirsario.cameraoverhaul.configuration.*;
import mirsario.cameraoverhaul.utilities.*;
import org.joml.SimplexNoise;
import org.joml.Vector3d;

@SuppressWarnings("unused")
public final class CameraSystem {
	private ConfigData cfg;
	private ConfigData.Contextual ctxCfg;
	private final Vector3d prevCameraEulerRot = new Vector3d();
	private final Vector3d prevEntityVelocity = new Vector3d();
	private double lastActionTime;
	private CameraContext.Perspective prevCameraPerspective;
	private final Transform offsetTransform = new Transform();

	public void notifyOfPlayerAction() {
		lastActionTime = TimeSystem.getTime();
	}

	public void onCameraUpdate(CameraContext context, double deltaTime) {
		var time = TimeSystem.getTime();
		cfg = Configuration.get();

		if (context.isRidingVehicle) ctxCfg = cfg.vehicles;
		else if (context.isRidingMount) ctxCfg = cfg.mounts;
		else if (context.isSwimming) ctxCfg = cfg.swimming;
		else if (context.isFlying) ctxCfg = cfg.flying;
		else if (context.isSprinting) ctxCfg = cfg.sprinting;
		else ctxCfg = cfg.walking;

		// Reset the offset transform
		offsetTransform.position = new Vector3d(0, 0, 0);
		offsetTransform.eulerRot = new Vector3d(0, 0, 0);

		if (!cfg.general.enabled
		|| (!cfg.general.enableInThirdPerson && context.perspective != CameraContext.Perspective.FIRST_PERSON)) {
			return;
		}

		ScreenShakes.onCameraUpdate(context, deltaTime);

		if (!context.velocity.equals(prevEntityVelocity)
		|| !context.transform.eulerRot.equals(prevCameraEulerRot)) {
			notifyOfPlayerAction();
		}

		// XY
		mouseSmoothingOffset(context, offsetTransform, deltaTime);
		noiseOffset(context, offsetTransform, deltaTime);
		// X
		verticalVelocityPitchOffset(context, offsetTransform, deltaTime);
		forwardVelocityPitchOffset(context, offsetTransform, deltaTime);
		// Z
		turningRollOffset(context, offsetTransform, deltaTime);
		strafingRollOffset(context, offsetTransform, deltaTime);

		prevEntityVelocity.set(context.velocity);
		prevCameraEulerRot.set(context.transform.eulerRot);
		prevCameraPerspective = context.perspective;
	}

	public void modifyCameraTransform(Transform transform) {
		transform.position.add(offsetTransform.position);
		transform.eulerRot.add(offsetTransform.eulerRot);

		ScreenShakes.modifyCameraTransform(transform);
	}

	private static final double BASE_VERTICAL_PITCH_SMOOTHING = 0.00004;
	private double prevVerticalVelocityPitchOffset;

	private void verticalVelocityPitchOffset(
		CameraContext context,
		Transform outputTransform,
		double deltaTime
	) {
		double multiplier = ctxCfg.verticalVelocityPitchFactor;
		double smoothing = BASE_VERTICAL_PITCH_SMOOTHING * ctxCfg.verticalVelocitySmoothingFactor;

		double targetOffset = context.velocity.y * multiplier;
		double currentOffset = MathUtils.damp(
			prevVerticalVelocityPitchOffset,
			targetOffset,
			smoothing,
			deltaTime
		);

		outputTransform.eulerRot.x += currentOffset;
		prevVerticalVelocityPitchOffset = currentOffset;
	}

	private static final double BASE_FORWARD_PITCH_SMOOTHING = 0.008;
	private double prevForwardVelocityPitchOffset;

	private void forwardVelocityPitchOffset(
		CameraContext context,
		Transform outputTransform,
		double deltaTime
	) {
		double multiplier = ctxCfg.forwardVelocityPitchFactor;
		double smoothing = BASE_FORWARD_PITCH_SMOOTHING * ctxCfg.horizontalVelocitySmoothingFactor;

		double targetOffset = context.getForwardRelativeVelocity().z * multiplier;
		double currentOffset = MathUtils.damp(
			prevForwardVelocityPitchOffset,
			targetOffset,
			smoothing,
			deltaTime
		);

		outputTransform.eulerRot.x += currentOffset;
		prevForwardVelocityPitchOffset = currentOffset;
	}

	private static final double BASE_TURNING_ROLL_ACCUMULATION = 0.0048;
	private static final double BASE_TURNING_ROLL_INTENSITY = 1.25;
	private static final double BASE_TURNING_ROLL_SMOOTHING = 0.0825;
	private double turningRollTargetOffset;

	private void turningRollOffset(
		CameraContext context,
		Transform outputTransform,
		double deltaTime
	) {
		double decaySmoothing = BASE_TURNING_ROLL_SMOOTHING * cfg.general.turningRollSmoothing;
		double intensity = BASE_TURNING_ROLL_INTENSITY * cfg.general.turningRollIntensity;
		double accumulation = BASE_TURNING_ROLL_ACCUMULATION * cfg.general.turningRollAccumulation;
		double yawDelta = prevCameraEulerRot.y - context.transform.eulerRot.y;

		// Don't spazz out when switching perspectives.
		if (context.perspective != prevCameraPerspective) yawDelta = 0.0;

		// Decay
		turningRollTargetOffset = MathUtils.damp(
			turningRollTargetOffset,
			0,
			decaySmoothing,
			deltaTime
		);
		// Accumulation
		turningRollTargetOffset = MathUtils.clamp(
			turningRollTargetOffset + (yawDelta * accumulation),
			-1.0,
			1.0
		);
		// Apply
		var turningRollOffset =
			MathUtils.clamp01(turningEasing(Math.abs(turningRollTargetOffset))) *
			intensity *
			Math.signum(turningRollTargetOffset);
		outputTransform.eulerRot.z += turningRollOffset;
	}

	private static double turningEasing(double x) {
		// https://easings.net/#easeInOutCubic
		return x < 0.5 ? (4 * x * x * x) : (1 - Math.pow(-2 * x + 2, 3) / 2);
	}

	private static final double BASE_STRAFING_ROLL_SMOOTHING = 0.008;
	private double prevStrafingRollOffset;

	private void strafingRollOffset(
		CameraContext context,
		Transform outputTransform,
		double deltaTime
	) {
		double multiplier = ctxCfg.strafingRollFactor;
		double smoothing = BASE_STRAFING_ROLL_SMOOTHING * ctxCfg.horizontalVelocitySmoothingFactor;

		double target = -context.getForwardRelativeVelocity().x * multiplier;
		double offset = MathUtils.damp(prevStrafingRollOffset, target, smoothing, deltaTime);

		outputTransform.eulerRot.z += offset;
		prevStrafingRollOffset = offset;
	}

	private static final double CAMERASWAY_FADING_SMOOTHNESS = 3.0;
	private double cameraSwayFactor;
	private double cameraSwayFactorTarget;

	private void noiseOffset(CameraContext context, Transform outputTransform, double deltaTime) {
		double time = TimeSystem.getTime();
		float noiseX = (float)(time * cfg.general.cameraSwayFrequency);

		// Fade out if the player turns, moves, or does an interaction.
		if ((time - lastActionTime) < cfg.general.cameraSwayFadeInDelay) {
			cameraSwayFactorTarget = 0; // Fade-out
		}
		// Only start a fade-in after the last fade-out has ended.
		else if (cameraSwayFactor == cameraSwayFactorTarget) {
			cameraSwayFactorTarget = 1; // Fade-in
		}

		var cameraSwayFactorFadeLength =
			cameraSwayFactorTarget > 0
				? cfg.general.cameraSwayFadeInLength
				: cfg.general.cameraSwayFadeOutLength;
		var cameraSwayFactorFadeStep =
			cameraSwayFactorFadeLength > 0.0 ? deltaTime / cameraSwayFactorFadeLength : 1.0;
		cameraSwayFactor = MathUtils.stepTowards(
			cameraSwayFactor,
			cameraSwayFactorTarget,
			cameraSwayFactorFadeStep
		);

		var scaledIntensity =
			cfg.general.cameraSwayIntensity *
			Math.pow(cameraSwayFactor, CAMERASWAY_FADING_SMOOTHNESS);
		var target = new Vector3d(scaledIntensity, scaledIntensity, 0.0);
		var noise = new Vector3d(
			SimplexNoise.noise(noiseX, 420),
			SimplexNoise.noise(noiseX, 1337),
			SimplexNoise.noise(noiseX, 6969)
		);

		outputTransform.eulerRot.add(noise.mul(target));
	}

	private boolean msInit = false;
	private double prevYawNorm, prevPitchNorm; // last frame's normalized (vanilla) angles
	private double contYaw, contPitch; // continuous, unwrapped stream
	private double smYaw, smPitch; // actual angle
	private double smoothedMouseSmoothing;

	private static final double BASE_MOUSE_SMOOTHING = 16.0;
	private static final double MOUSE_SMOOTHING_INCREASE_SMOOTHING = 0.35;
	private static final double MOUSE_SMOOTHING_DECREASE_SMOOTHING = 0.08;

	private void mouseSmoothingOffset(
		CameraContext context,
		Transform outputTransform,
		double deltaTime
	) {
		final double mouseSmoothingTarget = Math.max(0.0, ctxCfg.mouseSmoothing);

		final double yawNow = context.transform.eulerRot.y;
		final double pitchNow = context.transform.eulerRot.x;

		// Don't spazz out
		if (!msInit || context.perspective != prevCameraPerspective) {
			prevYawNorm = yawNow;
			prevPitchNorm = pitchNow;
			contYaw = yawNow;
			contPitch = pitchNow;
			smYaw = yawNow;
			smPitch = pitchNow;
			smoothedMouseSmoothing = mouseSmoothingTarget;
			msInit = true;
			return;
		}

		// Preserve real mouse movement
		final double stepYaw = MathUtils.unwrapStep(yawNow - prevYawNorm);
		final double stepPitch = MathUtils.unwrapStep(pitchNow - prevPitchNorm);
		prevYawNorm = yawNow;
		prevPitchNorm = pitchNow;

		contYaw += stepYaw;
		contPitch += stepPitch;

		final double smoothing =
			mouseSmoothingTarget > smoothedMouseSmoothing
				? MOUSE_SMOOTHING_INCREASE_SMOOTHING
				: MOUSE_SMOOTHING_DECREASE_SMOOTHING;
		smoothedMouseSmoothing = MathUtils.damp(
			smoothedMouseSmoothing,
			mouseSmoothingTarget,
			smoothing,
			deltaTime
		);

		if (smoothedMouseSmoothing <= 0.0) {
			smYaw = contYaw;
			smPitch = contPitch;
			return;
		}

		final double k = BASE_MOUSE_SMOOTHING / smoothedMouseSmoothing;
		final double step = 1.0 - Math.exp(-k * Math.max(0.0, deltaTime));

		final double dYaw = contYaw - smYaw;
		final double dPitch = contPitch - smPitch;

		smYaw += dYaw * step;
		smPitch += dPitch * step;

		// Apply an offset so base + offset == smoothed angles this frame
		final double yawOffset = MathUtils.wrapDiff(smYaw - yawNow);
		final double pitchOffset = MathUtils.wrapDiff(smPitch - pitchNow);

		outputTransform.eulerRot.y += yawOffset;
		outputTransform.eulerRot.x += pitchOffset;
	}
}
