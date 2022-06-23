package mirsario.cameraoverhaul.common;

import org.apache.logging.log4j.*;
import mirsario.cameraoverhaul.core.configuration.*;
import mirsario.cameraoverhaul.common.configuration.*;
import mirsario.cameraoverhaul.common.systems.*;

public class CameraOverhaul
{
	public static final Logger Logger = LogManager.getLogger("CameraOverhaul");

	public static CameraOverhaul instance;
	
	public static final String Id = "cameraoverhaul";
	
	public CameraSystem cameraSystem;
	public ConfigData config;

	public void onInitializeClient()
	{
		config = Configuration.LoadConfig(ConfigData.class, Id, ConfigData.ConfigVersion);

		cameraSystem = new CameraSystem();
	}
}
