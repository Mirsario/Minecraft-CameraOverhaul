package mirsario.cameraoverhaul.core.configuration;

import com.google.gson.*;
import java.io.*;
import java.nio.file.*;

public final class Configuration
{
    private static Gson gson = new GsonBuilder().setPrettyPrinting().create();

	public static <T extends BaseConfigData> T LoadConfig(Class<T> tClass, String configName, int configVersion)
	{
		T configData = null;
		
        try {
			Path configDir;
			
            configDir = Paths.get("", "config", configName + ".json");
			
			boolean saveConfig = false;

			if(Files.exists(configDir)) {
				FileReader fileReader = new FileReader(configDir.toFile());
				configData = gson.fromJson(fileReader, tClass);
				
				//Save the config on first runs of new versions.
                if(configData.configVersion < configVersion) {
					saveConfig = true;
				}
			} else {
				try {
					configData = (T)tClass.newInstance();
				}
				catch(Exception e) {
					e.printStackTrace();
				}

				saveConfig = true;
			}

			if(saveConfig) {
				Paths.get("", "config").toFile().mkdirs();
				
				configData.configVersion = configVersion;
				
				BufferedWriter writer = new BufferedWriter(new FileWriter(configDir.toFile()));
				
				writer.write(gson.toJson(configData));
				writer.close();
			}
        } catch(Exception e) {
            e.printStackTrace();
		}
		
        return configData;
    }
}