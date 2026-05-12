// Copyright 2020-2026 Mirsario & Contributors.
// Released under the GNU General Public License 3.0.
// See LICENSE.md for details.

package mirsario.cameraoverhaul.mixins;

import mirsario.cameraoverhaul.*;
import mirsario.cameraoverhaul.utilities.*;
import net.minecraft.client.*;
import net.minecraft.util.*;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.animal.*;
import net.minecraft.world.entity.vehicle.*;
import net.minecraft.world.level.*;
import net.minecraft.world.phys.*;
import org.joml.*;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.*;

//? if <1.15
/*import org.lwjgl.opengl.*;*/

/// Applies pitch and yaw (X & Y axes) rotations.
@Mixin(Camera.class)
@SuppressWarnings("UnusedMixin")
public abstract class CameraMixin {
	//? if >=26.1
	@Shadow public abstract Entity entity();
	//? if >=1.21.11 {
	@Shadow public abstract float xRot();
	@Shadow public abstract float yRot();
	@Shadow public abstract Vec3 position();
	//?} else {
	/*@Shadow public abstract float getXRot();
	@Shadow public abstract float getYRot();
	@Shadow public abstract Vec3 getPosition();
	*/ //?}

	//? if >=1.15 {
	@Shadow public abstract Quaternionf rotation();
	//?} else
	/*@Shadow protected abstract void setRotation(float yaw, float pitch);*/

	//? if >=26.1 {
	@Inject(method = "alignWithEntity", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Camera;getMaxZoom(F)F"))
	private void thirdPersonUpdate(float tickDelta, CallbackInfo ci)
	//?} else if >=1.21.11 {
	/*@Inject(method = "setup", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Camera;getMaxZoom(F)F"))
	private void thirdPersonUpdate(Level area, Entity entity, boolean thirdPerson, boolean inverseView, float tickDelta, CallbackInfo ci)
	*///?} else {
	/*@Inject(method = "setup", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Camera;getMaxZoom(D)D"))
	private void thirdPersonUpdate(BlockGetter area, Entity entity, boolean thirdPerson, boolean inverseView, float tickDelta, CallbackInfo ci)
	*///?}
	{
		//? if >=26.1 {
		var entity = entity();
		var thirdPerson = !Minecraft.getInstance().options.getCameraType().isFirstPerson();
		var inverseView = Minecraft.getInstance().options.getCameraType().isMirrored();
		//?}

		if (!thirdPerson) return;

		applyCameraEffects(entity, thirdPerson, inverseView);
	}

	//? if >=26.1 {
	@Inject(method = "alignWithEntity", at = @At("RETURN"))
	private void firstPersonUpdate(float tickDelta, CallbackInfo ci)
	//?} else if >=1.21.11 {
	/*@Inject(method = "setup", at = @At("RETURN"))
	private void firstPersonUpdate(Level area, Entity entity, boolean thirdPerson, boolean inverseView, float tickDelta, CallbackInfo ci)
	*///?} else {
	/*@Inject(method = "setup", at = @At("RETURN"))
	private void firstPersonUpdate(BlockGetter area, Entity entity, boolean thirdPerson, boolean inverseView, float tickDelta, CallbackInfo ci)
	*///?}
	{
		//? if >=26.1 {
		var entity = entity();
		var thirdPerson = !Minecraft.getInstance().options.getCameraType().isFirstPerson();
		var inverseView = Minecraft.getInstance().options.getCameraType().isMirrored();
		//?}

		if (thirdPerson) return;

		// In 1.14.x the camera rotates GL state directly before this mixin returns.
		// Undo vanilla pitch/yaw so first-person effects can replace them cleanly.
		//? if <1.15 {
		/*GL11.glRotatef(getYRot() + 180.0f, 0f, -1f, 0f);
		GL11.glRotatef(getXRot(), -1f, 0f, 0f);
		var transform = applyCameraEffects(entity, thirdPerson, inverseView);
		*/ //?}

		//? if >=1.15
		applyCameraEffects(entity, thirdPerson, inverseView);

		// Reapply vanilla pitch/yaw plus our roll after updating the camera transform.
		//? if <1.15 {
		/*GL11.glRotatef((float)transform.eulerRot.z, 0f, 0f, 1f);
		GL11.glRotatef((float)transform.eulerRot.x, 1f, 0f, 0f);
		GL11.glRotatef((float)transform.eulerRot.y + 180f, 0f, 1f, 0f);
		*/ //?}
	}

	private Transform applyCameraEffects(Entity entity, boolean thirdPerson, boolean inverseView) {
		var system = CameraOverhaul.camera;
		var vehicle = entity.getVehicle();
		var controlledEntity = vehicle != null ? vehicle : entity;

		var context = new CameraContext();
		context.isRiding = vehicle != null;
		context.isRidingMount = vehicle instanceof Animal;
		//? if >=1.20.3 {
		context.isRidingVehicle = vehicle instanceof VehicleEntity;
		//?} else
		/*context.isRidingVehicle = vehicle instanceof Boat || vehicle instanceof AbstractMinecart;*/

		context.velocity = VectorUtils.toJoml(controlledEntity.getDeltaMovement());
		//? if >=1.21.11 {
		var oldXRot = xRot();
		var oldYRot = yRot();
		var oldPos = position();
		//?} else {
		/*var oldXRot = getXRot();
		var oldYRot = getYRot();
		var oldPos = getPosition();
		*///?}
		context.transform = new Transform(VectorUtils.toJoml(oldPos), new Vector3d(oldXRot, oldYRot, 0));

		context.perspective = thirdPerson
			? (inverseView ? CameraContext.Perspective.THIRD_PERSON_REVERSE : CameraContext.Perspective.THIRD_PERSON)
			: CameraContext.Perspective.FIRST_PERSON;

		if (entity instanceof LivingEntity) {
			context.isFlying = ((LivingEntity)entity).isFallFlying();
			context.isSwimming = entity.isSwimming();
			context.isSprinting = entity.isSprinting();
		}

		TimeSystem.update();
		system.onCameraUpdate(context, TimeSystem.getDeltaTime());
		system.modifyCameraTransform(context.transform);

		// If possible, do not use setRotation, as doing so for some reason messes up other mods' camera behaviors.
		// e.g.: Create Aeronatics' vehicle seats' camera movements used to break even from setRotation(currentYaw, currentPitch).
		//? if >=1.15 {
		addRotation((float)context.transform.eulerRot.x - oldXRot, (float)context.transform.eulerRot.y - oldYRot);
		//?} else
		/*setRotation((float)context.transform.eulerRot.y, (float)context.transform.eulerRot.x);*/

		return context.transform;
	}

	//? if >=1.15 {
	private void addRotation(float pitch, float yaw) {
		var quat = rotation();
		quat.rotateAxis(pitch * MathUtils.DEG_TO_RAD, -1, 0, 0);
		quat.rotateAxis(yaw * MathUtils.DEG_TO_RAD, 0, -1, 0);
	}
	//?}
}
