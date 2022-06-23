package mirsario.cameraoverhaul.fabric.mixins.legacy;

import net.fabricmc.api.*;
import net.minecraft.client.render.*;
import net.minecraft.entity.*;
import net.minecraft.util.math.*;
import net.minecraft.world.*;
import org.lwjgl.opengl.*;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.*;
import mirsario.cameraoverhaul.core.callbacks.*;
import mirsario.cameraoverhaul.core.structures.*;

//Legacy mixin, to be used in versions prior to 1.15.

@Mixin(Camera.class)
@Environment(EnvType.CLIENT)
public abstract class LegacyCameraMixin
{
	@Shadow abstract float getYaw();
	@Shadow abstract float getPitch();
	@Shadow abstract Vec3d getPos();
	@Shadow abstract void setRotation(float yaw, float pitch);

	@Inject(method = "update", at = @At("RETURN"))
	private void OnCameraUpdate(BlockView area, Entity focusedEntity, boolean thirdPerson, boolean inverseView, float tickDelta, CallbackInfo ci)
	{
		Transform cameraTransform = new Transform(getPos(), new Vec3d(getPitch(), getYaw(), 0d));

		//Undo multiplications.
		GL11.glRotatef((float)cameraTransform.eulerRot.y + 180.0f, 0f, -1f, 0f);
		GL11.glRotatef((float)cameraTransform.eulerRot.x, -1f, 0f, 0f);

		CameraUpdateCallback.EVENT.Invoker().OnCameraUpdate(focusedEntity, (Camera)(Object)this, cameraTransform, tickDelta);

		cameraTransform = ModifyCameraTransformCallback.EVENT.Invoker().ModifyCameraTransform((Camera)(Object)this, cameraTransform);

		setRotation((float)cameraTransform.eulerRot.y, (float)cameraTransform.eulerRot.x);

		//And now redo them.
		GL11.glRotatef((float)cameraTransform.eulerRot.z, 0f, 0f, 1f);
		GL11.glRotatef((float)cameraTransform.eulerRot.x, 1f, 0f, 0f);
		GL11.glRotatef((float)cameraTransform.eulerRot.y + 180f, 0f, 1f, 0f);
	}
}