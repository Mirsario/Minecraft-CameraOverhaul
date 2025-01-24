// Copyright 2020-2024 Mirsario & Contributors.
// Released under the GNU General Public License 3.0.
// See LICENSE.md for details.

package mirsario.cameraoverhaul.mixins;

import mirsario.cameraoverhaul.*;
import mirsario.cameraoverhaul.configuration.*;
import net.minecraft.client.player.*;
import net.minecraft.world.*;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.*;

// Adds screenshakes to hand swings.
@Mixin(LocalPlayer.class)
@SuppressWarnings("UnusedMixin")
public abstract class LocalPlayerMixin {
	@Unique private static long shakeHandle;

	@Shadow public abstract boolean isLocalPlayer();

	@Inject(method = "swing", at = @At("RETURN"))
	private void swing(InteractionHand interactionHand, CallbackInfo ci) {
		if (!isLocalPlayer()) return;

		shakeHandle = ScreenShakes.recreate(shakeHandle);
		var shake = ScreenShakes.get(shakeHandle);
		shake.trauma = (float)Configuration.get().general.handSwingTrauma;
		shake.frequency = 0.5f;
		shake.lengthInSeconds = 0.5f;

		CameraOverhaul.camera.notifyOfPlayerAction();
	}
}