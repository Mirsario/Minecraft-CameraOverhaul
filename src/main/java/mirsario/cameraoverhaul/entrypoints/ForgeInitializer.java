// Copyright 2020-2024 Mirsario & Contributors.
// Released under the GNU General Public License 3.0.
// See LICENSE.md for details.

//? if FORGE {
/*package mirsario.cameraoverhaul.entrypoints;

import mirsario.cameraoverhaul.*;
import mirsario.cameraoverhaul.configuration.*;
import net.minecraftforge.fml.*;
import net.minecraftforge.fml.common.*;
//? if >=1.19.0 {
import net.minecraftforge.client.*;
//?} else {
/^import net.minecraftforge.fmlclient.*;
^///?}

@Mod(CameraOverhaul.MOD_ID)
@SuppressWarnings("unused")
public class ForgeInitializer {
	public ForgeInitializer() {
		CameraOverhaul.onInitializeClient();

		//? if >=1.19.0 {
		ModLoadingContext.get().registerExtensionPoint(
			ConfigScreenHandler.ConfigScreenFactory.class,
			() -> new ConfigScreenHandler.ConfigScreenFactory((mc, parentScreen) -> ConfigScreen.getConfigScreen(parentScreen))
		);
		//?} else {
		/^ModLoadingContext.get().registerExtensionPoint(
			ConfigGuiHandler.ConfigGuiFactory.class,
			() -> new ConfigGuiHandler.ConfigGuiFactory((mc, parentScreen) -> ConfigScreen.getConfigScreen(parentScreen))
		);
		^///?}
	}
}
*///?}