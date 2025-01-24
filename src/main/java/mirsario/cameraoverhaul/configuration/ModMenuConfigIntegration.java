// Copyright 2020-2024 Mirsario & Contributors.
// Released under the GNU General Public License 3.0.
// See LICENSE.md for details.

//? if FABRIC && MC_RELEASE {
package mirsario.cameraoverhaul.configuration;

import java.lang.reflect.*;
import me.shedaniel.clothconfig2.api.*;
import mirsario.cameraoverhaul.*;
import mirsario.cameraoverhaul.abstractions.*;
import net.minecraft.client.*;

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
		return screen -> getConfigBuilder().build();
	}
*///?}

	@Override
	public ConfigScreenFactory<?> getModConfigScreenFactory() {
		return screen -> getConfigBuilder().build();
	}

	public static ConfigBuilder getConfigBuilder() {
		CameraOverhaul.LOGGER.info("Opening config screen.");

		var builder = (ConfigBuilder.create()
			.setParentScreen(Minecraft.getInstance().screen)
			.setTitle(TextAbstractions.getText("cameraoverhaul.config.title"))
			.transparentBackground()
			.setSavingRunnable(Configuration::saveConfig)
		);
		var general = builder.getOrCreateCategory(TextAbstractions.getText("cameraoverhaul.config.category.general"));

		try { addEntriesFromObject(builder, general, Configuration.get(), Configuration.getDefault()); }
		catch (Exception e) { CameraOverhaul.LOGGER.trace(e); }

		return builder;
	}

	private static void addEntriesFromObject(ConfigBuilder builder, ConfigCategory category, Object objCurrent, Object objDefault) throws IllegalAccessException {
		var entryBuilder = builder.entryBuilder();

		for (var field : objCurrent.getClass().getFields()) {
			var fieldName = field.getName();
			var fieldType = field.getType();
			var name = TextAbstractions.getText(Configuration.getNameKey(fieldName));
			var desc = TextAbstractions.getText(Configuration.getDescKey(fieldName));
			AbstractConfigListEntry<?> entry = null;

			if (!fieldType.isPrimitive()) {
				var thisCategory = builder.getOrCreateCategory(TextAbstractions.getText("cameraoverhaul.config.category." + field.getName()));
				addEntriesFromObject(builder, thisCategory, field.get(objCurrent), field.get(objDefault));
				continue;
			}

			if (fieldType == double.class) {
				entry = (entryBuilder.startDoubleField(name, (double)field.get(objCurrent))
					.setDefaultValue((double)field.get(objDefault))
					.setTooltip(desc)
					.setSaveConsumer(newValue -> trySetFieldValue(field, objCurrent, newValue))
					.build()
				);
			} else if (fieldType == boolean.class) {
				entry = (entryBuilder.startBooleanToggle(name, (boolean)field.get(objCurrent))
					.setDefaultValue((boolean)field.get(objDefault))
					.setTooltip(desc)
					.setSaveConsumer(newValue -> trySetFieldValue(field, objCurrent, newValue))
					.build()
				);
			}

			if (entry != null) category.addEntry(entry);
		}
	}
	private static void trySetFieldValue(Field field, Object obj, Object value) {
		try { field.set(obj, value); }
		catch (Exception e) { CameraOverhaul.LOGGER.trace(e); }
	}
}
//?}