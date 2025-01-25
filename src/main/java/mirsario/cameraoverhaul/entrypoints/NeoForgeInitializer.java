// Copyright 2020-2024 Mirsario & Contributors.
// Released under the GNU General Public License 3.0.
// See LICENSE.md for details.

//? if NEOFORGE {
/*package mirsario.cameraoverhaul.entrypoints;

import mirsario.cameraoverhaul.*;
import mirsario.cameraoverhaul.configuration.*;
import net.neoforged.fml.*;
import net.neoforged.fml.common.*;
import net.neoforged.neoforge.client.gui.*;

@Mod(CameraOverhaul.MOD_ID)
@SuppressWarnings("unused")
public class NeoForgeInitializer {
	public NeoForgeInitializer() {
		CameraOverhaul.onInitializeClient();

        ModLoadingContext.get().registerExtensionPoint(IConfigScreenFactory.class, () -> (mc, p) -> ConfigScreen.getConfigScreen(p));
	}
}
*///?}