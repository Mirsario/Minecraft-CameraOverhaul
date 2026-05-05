// Copyright 2020-2026 Mirsario & Contributors.
// Released under the GNU General Public License 3.0.
// See LICENSE.md for details.

package mirsario.cameraoverhaul.abstractions;

//? if MC_RELEASE {
import net.minecraft.network.chat.*;

public final class TextAbstractions {
	public static Component getText(String key) {
		//? if >=1.19 {
			return Component.translatable(key);
		//?} else
			/*return new TranslatableComponent(key);*/
	}

	public static String getTextValue(String key) { return getText(key).getString(); }
}
//?}
