// Copyright 2020-2026 Mirsario & Contributors.
// Released under the GNU General Public License 3.0.
// See LICENSE.md for details.

//? if >=1.15 {
package mirsario.cameraoverhaul.mixins;

import mirsario.cameraoverhaul.*;
import mirsario.cameraoverhaul.abstractions.*;
import mirsario.cameraoverhaul.utilities.*;
import net.minecraft.client.*;
import net.minecraft.client.renderer.*;
//?if >=26.1
import net.minecraft.client.renderer.state.level.*;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.*;
import com.mojang.blaze3d.vertex.*;
import org.joml.*;

/// Applies roll (Z-axis) rotations.
@Mixin(GameRenderer.class)
@SuppressWarnings("UnusedMixin")
public abstract class GameRendererMixin {
	@Shadow @Final private Camera mainCamera;

	@Inject(method = "bobHurt", at = @At("HEAD"))
	//?if >=26.1 {
	private void postCameraUpdate(final CameraRenderState cameraState, final PoseStack matrices, CallbackInfo ci)
	//?} else
	//private void postCameraUpdate(PoseStack matrices, float f, CallbackInfo ci)
	{
		//? if >=26.1 {
		Transform cameraTransform = new Transform(VectorUtils.toJoml(cameraState.pos), new Vector3d(cameraState.xRot, cameraState.yRot, 0));
		//?} else if >=1.21.11 {
		/*Transform cameraTransform = new Transform(VectorUtils.toJoml(mainCamera.position()), new Vector3d(mainCamera.xRot(), mainCamera.yRot(), 0));
		*///?} else
		/*Transform cameraTransform = new Transform(VectorUtils.toJoml(mainCamera.getPosition()), new Vector3d(mainCamera.getXRot(), mainCamera.getYRot(), 0));*/

		CameraOverhaul.camera.modifyCameraTransform(cameraTransform);

		MathAbstractions.rotateMatrixByAxis(matrices, 0f, 0f, 1f, cameraTransform.eulerRot.z);
	}
}
//?}
