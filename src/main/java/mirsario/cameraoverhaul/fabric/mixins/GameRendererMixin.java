package mirsario.cameraoverhaul.fabric.mixins;

import net.minecraft.client.render.*;
import net.minecraft.client.util.math.*;
import net.minecraft.util.math.*;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.*;
import mirsario.cameraoverhaul.core.callbacks.*;
import mirsario.cameraoverhaul.core.structures.*;

@Mixin(GameRenderer.class)
public abstract class GameRendererMixin
{
	@Shadow
	@Final
	private Camera camera;

	@Inject(method = "renderWorld", at = @At(
		value = "INVOKE",
		//Inject before the call to WorldRenderer.render()
		target = "Lnet/minecraft/client/render/WorldRenderer;render(Lnet/minecraft/client/util/math/MatrixStack;FJZLnet/minecraft/client/render/Camera;Lnet/minecraft/client/render/GameRenderer;Lnet/minecraft/client/render/LightmapTextureManager;Lnet/minecraft/util/math/Matrix4f;)V",
		shift = At.Shift.BEFORE
	))
	private void PostCameraUpdate(float tickDelta, long limitTime, MatrixStack matrix, CallbackInfo ci)
	{
		Transform cameraTransform = new Transform(camera.getPos(), new Vec3d(camera.getPitch(), camera.getYaw(), 0d));

		cameraTransform = CameraUpdateCallback.EVENT.Invoker().OnCameraUpdate(camera, cameraTransform, tickDelta);

		//Undo multiplications.
		matrix.multiply(Vector3f.NEGATIVE_Y.getDegreesQuaternion(camera.getYaw() + 180.0F));
		matrix.multiply(Vector3f.NEGATIVE_X.getDegreesQuaternion(camera.getPitch()));

		//And now redo them.
		matrix.multiply(Vector3f.POSITIVE_Z.getDegreesQuaternion((float)cameraTransform.eulerRot.z));
		matrix.multiply(Vector3f.POSITIVE_X.getDegreesQuaternion((float)cameraTransform.eulerRot.x));
		matrix.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion((float)cameraTransform.eulerRot.y + 180f));
	}
}
