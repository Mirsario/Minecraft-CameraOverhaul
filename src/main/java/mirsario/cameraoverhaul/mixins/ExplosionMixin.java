// Copyright 2020-2024 Mirsario & Contributors.
// Released under the GNU General Public License 3.0.
// See LICENSE.md for details.

package mirsario.cameraoverhaul.mixins;

// Adds screenshakes to explosions.

import mirsario.cameraoverhaul.*;
import mirsario.cameraoverhaul.configuration.*;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.*;

//? if >=1.21.2 {
import net.minecraft.client.multiplayer.*;
import net.minecraft.network.protocol.game.*;

@Mixin(ClientPacketListener.class)
@SuppressWarnings("UnusedMixin")
public abstract class ExplosionMixin {
	@Inject(method = "handleExplosion", at = @At("RETURN"))
	private void handleExplosion(ClientboundExplodePacket packet, CallbackInfo ci) {
		var pos = packet.center();
		var shake = ScreenShakes.createDirect();
		shake.position.set(pos.x, pos.y, pos.z);
		shake.radius = 32f;
		shake.trauma = (float)Configuration.get().general.explosionTrauma;
		shake.lengthInSeconds = 2f;
	}
}
//?} else {
/*import net.minecraft.world.level.*;

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
*///?}