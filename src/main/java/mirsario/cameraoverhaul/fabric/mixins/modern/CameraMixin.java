package mirsario.cameraoverhaul.fabric.mixins.modern;

import net.minecraft.client.render.*;
import net.minecraft.entity.*;
import net.minecraft.util.math.*;
import net.minecraft.world.*;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.*;
import mirsario.cameraoverhaul.core.callbacks.*;
import mirsario.cameraoverhaul.core.structures.*;

@Mixin(Camera.class)
public abstract class CameraMixin
{
	@Shadow abstract float getYaw();
	@Shadow abstract float getPitch();
	@Shadow abstract Vec3d getPos();
	@Shadow abstract void setRotation(float yaw, float pitch);

	@Inject(method = "update", at = @At("RETURN"))
	private void OnCameraUpdate(BlockView area, Entity focusedEntity, boolean thirdPerson, boolean inverseView, float tickDelta, CallbackInfo ci)
	{
		Transform cameraTransform = new Transform(getPos(), new Vec3d(getPitch(), getYaw(), 0d));

		CameraUpdateCallback.EVENT.Invoker().OnCameraUpdate(focusedEntity, (Camera)(Object)this, cameraTransform, tickDelta);

		cameraTransform = ModifyCameraTransformCallback.EVENT.Invoker().ModifyCameraTransform((Camera)(Object)this, cameraTransform);

		setRotation((float)cameraTransform.eulerRot.y, (float)cameraTransform.eulerRot.x);
	}
}