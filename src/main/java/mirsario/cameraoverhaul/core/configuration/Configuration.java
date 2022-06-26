package mirsario.cameraoverhaul.core.configuration;

import mirsario.cameraoverhaul.common.*;
import net.fabricmc.loader.api.*;
import java.io.*;
import java.nio.file.*;
import com.google.gson.*;

public final class Configuration
{
	private static Gson gson = new GsonBuilder().setPrettyPrinting().create();
	
	private static final Path configPath = FabricLoader.getInstance().getConfigDir();

	public static <T extends BaseConfigData> T LoadConfig(Class<T> tClass, String configName, int configVersion)
	{
		T configData = null;
		Path configFile = configPath.resolve(configName + ".json");
		boolean saveConfig = false;
		
		try {
			Files.createDirectories(configPath);

			if(Files.exists(configFile)) {
				BufferedReader fileReader = Files.newBufferedReader(configFile);
				configData = gson.fromJson(fileReader, tClass);
				fileReader.close();
				
				// Save the config on first runs of new versions.
				if(configData.configVersion < configVersion) {
					saveConfig = true;
				}
			} else {
				configData = (T)tClass.getDeclaredConstructor().newInstance();
				saveConfig = true;
			}
		} catch(Exception e) {
			CameraOverhaul.Logger.error("Error when initializing config", e);
		}
		
		if(saveConfig) {
			SaveConfig(configData, configName, configVersion);
		}
		
		return configData;
	}
	
	public static <T extends BaseConfigData> void SaveConfig(T configData, String configName, int configVersion) {
		Path configFile = configPath.resolve(configName+".json");
		
		configData.configVersion = configVersion;
		
		try (BufferedWriter writer = Files.newBufferedWriter(configFile)){
			writer.write(gson.toJson(configData));
		} catch (IOException e) {
			CameraOverhaul.Logger.error("Couldn't save config file", e);
		}
	}
}