// Copyright 2020-2024 Mirsario & Contributors.
// Released under the GNU General Public License 3.0.
// See LICENSE.md for details.

//? if FABRIC && MC_RELEASE {
package mirsario.cameraoverhaul.configuration;

import mirsario.cameraoverhaul.*;
// Beyond annoying.
//? if <=1.17 {
/*import io.github.prospector.modmenu.api.*;
import net.minecraft.client.gui.screens.*;
*///?} else {
import com.terraformersmc.modmenu.api.*;
//?}

@SuppressWarnings({ "deprecation", "unused" })
public class ModMenuConfigIntegration implements ModMenuApi {
//? if <=1.17 {
	/*public String getModId() {
		return CameraOverhaul.MOD_ID;
	}

	public java.util.function.Function<Screen, ? extends Screen> getConfigScreenFactory() {
		return screen -> ConfigScreen.getConfigBuilder(null).build();
	}
*///?}

	@Override
	public ConfigScreenFactory<?> getModConfigScreenFactory() {
		return screen -> ConfigScreen.getConfigBuilder(null).build();
	}
}
//?}