// Copyright 2020-2024 Mirsario & Contributors.
// Released under the GNU General Public License 3.0.
// See LICENSE.md for details.

//? if >=1.15 {
package mirsario.cameraoverhaul.mixins;

import mirsario.cameraoverhaul.*;
import mirsario.cameraoverhaul.abstractions.*;
import mirsario.cameraoverhaul.utilities.*;
import net.minecraft.client.*;
import net.minecraft.client.renderer.*;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.*;
import com.mojang.blaze3d.vertex.*;
import org.joml.*;

@Mixin(GameRenderer.class)
@SuppressWarnings("UnusedMixin")
public abstract class GameRendererMixin {
	@Shadow @Final private Camera mainCamera;

	@Inject(method = "bobHurt", at = @At("HEAD"))
	private void postCameraUpdate(PoseStack matrices, float f, CallbackInfo ci) {
		Transform cameraTransform = new Transform(
			VectorUtils.toJoml(mainCamera.getPosition()),
			new Vector3d(mainCamera.getXRot(), mainCamera.getYRot(), 0)
		);

		CameraOverhaul.camera.modifyCameraTransform(cameraTransform);

		//matrix.multiply(Vec3f.POSITIVE_Z.getDegreesQuaternion((float)cameraTransform.eulerRot.z));
		MathAbstractions.rotateMatrixByAxis(matrices, 0f, 0f, 1f, cameraTransform.eulerRot.z);
	}
}
//?}
