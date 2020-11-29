package mirsario.cameraoverhaul.fabric;

import net.fabricmc.api.*;
import mirsario.cameraoverhaul.common.*;

public class FabricClientModInitializer implements ClientModInitializer
{
	@Override
	public void onInitializeClient()
	{
		if(CameraOverhaul.instance == null) {
			CameraOverhaul.instance = new CameraOverhaul();
		}

		CameraOverhaul.instance.onInitializeClient();
	}
}
