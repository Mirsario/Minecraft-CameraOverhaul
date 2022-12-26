package mirsario.cameraoverhaul.fabric.mixins.modern;

import net.minecraft.client.render.*;
import net.minecraft.client.util.math.*;
import net.minecraft.util.math.*;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.*;
import mirsario.cameraoverhaul.core.callbacks.*;
import mirsario.cameraoverhaul.core.structures.*;
import mirsario.cameraoverhaul.fabric.abstractions.*;

@Mixin(GameRenderer.class)
public abstract class GameRendererMixin
{
	@Shadow
	@Final
	private Camera camera;

	@Inject(method = "renderWorld", at = @At(
		value = "INVOKE",
		// Inject before the call to Camera.update()
		target = "Lnet/minecraft/client/render/Camera;update(Lnet/minecraft/world/BlockView;Lnet/minecraft/entity/Entity;ZZF)V",
		shift = At.Shift.BEFORE
	))
	private void PostCameraUpdate(float tickDelta, long limitTime, MatrixStack matrix, CallbackInfo ci)
	{
		Transform cameraTransform = new Transform(camera.getPos(), new Vec3d(camera.getPitch(), camera.getYaw(), 0d));

		cameraTransform = ModifyCameraTransformCallback.EVENT.Invoker().ModifyCameraTransform(camera, cameraTransform);

		//matrix.multiply(Vec3f.POSITIVE_Z.getDegreesQuaternion((float)cameraTransform.eulerRot.z));
		MathAbstractions.RotateMatrixByAxis(matrix, 0f, 0f, 1f, (float)cameraTransform.eulerRot.z);
	}
}
