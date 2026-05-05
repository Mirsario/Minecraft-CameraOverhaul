// Copyright 2020-2026 Mirsario & Contributors.
// Released under the GNU General Public License 3.0.
// See LICENSE.md for details.

package mirsario.cameraoverhaul;

import org.apache.logging.log4j.*;

public final class CameraOverhaul {
	public static final String MOD_ID = "cameraoverhaul";
	public static final Logger LOGGER = LogManager.getLogger("CameraOverhaul");

	public static CameraSystem camera;

	public static void onInitializeClient() {
		camera = new CameraSystem();
	}
}
