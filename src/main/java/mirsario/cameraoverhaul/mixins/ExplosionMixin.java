// Copyright 2020-2024 Mirsario & Contributors.
// Released under the GNU General Public License 3.0.
// See LICENSE.md for details.

//? if FABRIC {
package mirsario.cameraoverhaul.mixins;

import mirsario.cameraoverhaul.*;
import mirsario.cameraoverhaul.configuration.Configuration;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.*;
import net.minecraft.world.level.*;

// Adds screenshakes to explosions.
@Mixin(Explosion.class)
@SuppressWarnings("UnusedMixin")
public abstract class ExplosionMixin {
	@Shadow @Final private double x;
	@Shadow @Final private double y;
	@Shadow @Final private double z;

	@Shadow @Final private float radius;

	@Inject(method = "finalizeExplosion", at = @At("RETURN"))
	private void finalizeExplosion(boolean spawnParticles, CallbackInfo ci) {
		var shake = ScreenShakes.createDirect();
		shake.position.set(this.x, this.y, this.z);
		shake.radius = this.radius * 10f;
		shake.trauma = (float)Configuration.get().general.explosionTrauma;
		shake.lengthInSeconds = 2f;
	}
}
//?}