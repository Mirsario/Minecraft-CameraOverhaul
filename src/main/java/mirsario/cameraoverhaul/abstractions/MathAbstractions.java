// Copyright 2020-2026 Mirsario & Contributors.
// Released under the GNU General Public License 3.0.
// See LICENSE.md for details.

package mirsario.cameraoverhaul.abstractions;

public final class MathAbstractions {
	//? if MC_RELEASE && >=1.15 {
	public static void rotateMatrixByAxis(com.mojang.blaze3d.vertex.PoseStack matrix, double axisX, double axisY, double axisZ, double rotation) {
		//? if >=1.19.3 {
			matrix.mulPose(com.mojang.math.Axis.of(new org.joml.Vector3f((float)axisX, (float)axisY, (float)axisZ)).rotationDegrees((float)rotation));
		//?} else
			/*matrix.mulPose(new com.mojang.math.Vector3f((float)axisX, (float)axisY, (float)axisZ).rotationDegrees((float)rotation));*/
	}
	//?}
}
