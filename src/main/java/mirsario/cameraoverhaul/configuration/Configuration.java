// Copyright 2020-2024 Mirsario & Contributors.
// Released under the GNU General Public License 3.0.
// See LICENSE.md for details.

package mirsario.cameraoverhaul.configuration;

import com.moandjiezana.toml.*;
import java.io.*;
import java.lang.reflect.*;
import java.nio.file.*;
import mirsario.cameraoverhaul.*;
import mirsario.cameraoverhaul.abstractions.*;

public final class Configuration {
	//? if FABRIC
	private static final Path CONFIG_DIR = net.fabricmc.loader.api.FabricLoader.getInstance().getConfigDir();
	//? if FORGE
	/*private static final Path CONFIG_DIR = net.minecraftforge.fml.loading.FMLPaths.CONFIGDIR.get();*/
	//? if NEOFORGE
	/*private static final Path CONFIG_DIR = net.neoforged.fml.loading.FMLPaths.CONFIGDIR.get();*/

	private static final Path CONFIG_PATH = CONFIG_DIR.resolve(CameraOverhaul.MOD_ID + ".toml");
	private static final String CONFIG_ENTRIES_PREFIX = "cameraoverhaul.config";
	private static final Toml TOML = new Toml();
	private static final ConfigData configDefault = new ConfigData();
	private static ConfigData configCurrent;

	public static ConfigData getDefault() { return configDefault; }
	public static ConfigData get() {
		if (configCurrent == null) loadConfig();
		return configCurrent;
	}

	public static void loadConfig() {
		var file = CONFIG_PATH.toFile();

		try {
			Files.createDirectories(CONFIG_DIR);

			if (file.exists()) {
				configCurrent = TOML.read(file).to(ConfigData.class);
			} else {
				configCurrent = new ConfigData();
			}
		} catch (Exception e) {
			configCurrent = new ConfigData();
			CameraOverhaul.LOGGER.error("Failed to load config file", e);
		}

		saveConfig();
	}
	
	public static void saveConfig() {
		configCurrent.configVersion = ConfigData.CONFIG_VERSION;

		try (BufferedWriter writer = Files.newBufferedWriter(CONFIG_PATH)) {
			writeCommentedFieldsToml(writer, configCurrent, configDefault, 0);
		} catch (Exception e) {
			CameraOverhaul.LOGGER.error("Failed to save config file", e);
		}
	}

	public static String getNameKey(String identifier) { return CONFIG_ENTRIES_PREFIX + "." + identifier + ".name"; }
	public static String getDescKey(String identifier) { return CONFIG_ENTRIES_PREFIX + "." + identifier + ".desc"; }

	// Very basic, made for one purpose.
	private static void writeCommentedFieldsToml(BufferedWriter writer, Object objCurrent, Object objDefault, int indentation) throws IllegalAccessException, IOException {
		for (var field : objCurrent.getClass().getFields()) {
			if (Modifier.isStatic(field.getModifiers())) continue;

			if (!field.getType().isPrimitive() && field.getType() != String.class) {
				writer.write("\r\n");
				writer.write("[");
				writer.write(field.getName());
				writer.write("]");
				writer.write("\r\n");
				writer.write("\r\n");
				writeCommentedFieldsToml(writer, field.get(objCurrent), field.get(objDefault), indentation + 1);
				continue;
			}

			indent(writer, indentation);
			writer.write("# ");
			writer.write(TextAbstractions.getTextValue(getDescKey(field.getName())));
			newLine(writer, indentation);
			writer.write("# Default: ");
			writer.write(field.get(objDefault).toString());
			newLine(writer, indentation);
			writer.write(field.getName());
			writer.write(" = ");
			writer.write(field.get(objCurrent).toString());
			newLine(writer, 0);
		}
	}
	private static void indent(BufferedWriter writer, int indentation) throws IOException {
		for (int i = 0; i < indentation; i++) writer.write("\t");
	}
	private static void newLine(BufferedWriter writer, int indentation) throws IOException {
		writer.write("\r\n");
		indent(writer, indentation);
	}
}