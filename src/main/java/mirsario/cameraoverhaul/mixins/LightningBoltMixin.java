// Copyright 2020-2024 Mirsario & Contributors.
// Released under the GNU General Public License 3.0.
// See LICENSE.md for details.

package mirsario.cameraoverhaul.mixins;

import mirsario.cameraoverhaul.*;
import mirsario.cameraoverhaul.configuration.*;
import net.minecraft.world.entity.*;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.*;
//? if >=1.16 {
import net.minecraft.world.entity.LightningBolt;
//?} else {
/*import net.minecraft.world.entity.global.LightningBolt;
*///?}

// Adds screenshakes to lightning bolt strikes.
@Mixin(LightningBolt.class)
@SuppressWarnings("UnusedMixin")
public abstract class LightningBoltMixin {

	@Inject(method = "spawnFire", at = @At("RETURN"))
	private void spawnFire(int num, CallbackInfo ci) {
		var entity = (Entity)(Object)this;
		var pos = entity.position();

		var explosion = ScreenShakes.createDirect();
		explosion.position.set(pos.x, pos.y, pos.z);
		explosion.radius = 16f;
		explosion.trauma = (float)Configuration.get().general.explosionTrauma;
		explosion.lengthInSeconds = 3f;

		var thunder = ScreenShakes.createDirect();
		thunder.position.set(pos.x, pos.y, pos.z);
		thunder.radius = 192f;
		thunder.trauma = (float)Configuration.get().general.thunderTrauma;
		thunder.frequency = 0.5f;
		thunder.lengthInSeconds = 7f;
	}
}