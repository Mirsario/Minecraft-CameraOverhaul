// Copyright 2020-2024 Mirsario & Contributors.
// Released under the GNU General Public License 3.0.
// See LICENSE.md for details.

//? if FABRIC {
package mirsario.cameraoverhaul.entrypoints;

import mirsario.cameraoverhaul.*;
import net.fabricmc.api.*;

@SuppressWarnings("unused")
public class FabricClientModInitializer implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		CameraOverhaul.onInitializeClient();
	}
}
//?}