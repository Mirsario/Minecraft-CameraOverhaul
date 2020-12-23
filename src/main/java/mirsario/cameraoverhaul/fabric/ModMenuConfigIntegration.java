package mirsario.cameraoverhaul.fabric;

import io.github.prospector.modmenu.api.ConfigScreenFactory;
import io.github.prospector.modmenu.api.ModMenuApi;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import mirsario.cameraoverhaul.common.CameraOverhaul;
import mirsario.cameraoverhaul.common.configuration.ConfigData;
import mirsario.cameraoverhaul.core.configuration.Configuration;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.TranslatableText;

public class ModMenuConfigIntegration implements ModMenuApi {
	@Override
	public ConfigScreenFactory<?> getModConfigScreenFactory() {
		return screen -> getConfigBuilder().build();
	}
	
	@SuppressWarnings("resource") //MinecraftClient.getInstance() isn't a resource
	public static ConfigBuilder getConfigBuilder() {
		ConfigData config = CameraOverhaul.instance.config;
		
		ConfigBuilder builder = ConfigBuilder.create()
				.setParentScreen(MinecraftClient.getInstance().currentScreen)
				.setTitle(new TranslatableText("cameraoverhaul.config.title"))
				.transparentBackground()
				.setSavingRunnable(()-> {
					Configuration.SaveConfig(CameraOverhaul.instance.config, CameraOverhaul.configFileName, ConfigData.ConfigVersion);
				});
		
		ConfigCategory general = builder.getOrCreateCategory(new TranslatableText("cameraoverhaul.config.category.general"));
		ConfigEntryBuilder entryBuilder = builder.entryBuilder();
		
		// Enabled
		general.addEntry(entryBuilder
				.startBooleanToggle(new TranslatableText("cameraoverhaul.config.enabled.name"), config.enabled)
					.setDefaultValue(true)
					.setTooltip(new TranslatableText("cameraoverhaul.config.enabled.tooltip"))
					.setSaveConsumer(newValue -> config.enabled = newValue)
					.build());
		
		// strafingRollFactor
		general.addEntry(entryBuilder
					.startIntSlider(new TranslatableText("cameraoverhaul.config.strafingrollfactor.name"), (int) (config.strafingRollFactor*100), 0, 1000)
					.setDefaultValue(100)
					.setTooltip(new TranslatableText("cameraoverhaul.config.strafingrollfactor.tooltip"))
					.setSaveConsumer(newValue -> config.strafingRollFactor = newValue/100F)
					.build());
		
		// yawDeltaRollFactor
		general.addEntry(entryBuilder
				.startIntSlider(new TranslatableText("cameraoverhaul.config.yawdeltarollfactor.name"), (int) (config.yawDeltaRollFactor*100F), 0, 1000)
					.setDefaultValue(100)
					.setTooltip(new TranslatableText("cameraoverhaul.config.yawdeltarollfactor.tooltip"))
					.setSaveConsumer(newValue -> config.yawDeltaRollFactor = newValue/100F)
					.build());
		
		// verticalVelocityPitchFactor
		general.addEntry(entryBuilder
				.startIntSlider(new TranslatableText("cameraoverhaul.config.verticalvelocitypitchfactor.name"), (int) (config.verticalVelocityPitchFactor*100), 0, 1000)
					.setDefaultValue(100)
					.setTooltip(new TranslatableText("cameraoverhaul.config.verticalvelocitypitchfactor.tooltip"))
					.setSaveConsumer(newValue -> config.verticalVelocityPitchFactor = newValue/100F)
					.build());
		
		// forwardVelocityPitchFactor
		general.addEntry(entryBuilder
				.startIntSlider(new TranslatableText("cameraoverhaul.config.forwardvelocitypitchfactor.name"), (int) (config.forwardVelocityPitchFactor*100), 0, 1000)
					.setDefaultValue(100)
					.setTooltip(new TranslatableText("cameraoverhaul.config.forwardvelocitypitchfactor.tooltip"))
					.setSaveConsumer(newValue -> config.forwardVelocityPitchFactor = newValue/100F)
					.build());
		
		return builder;
	}
}
