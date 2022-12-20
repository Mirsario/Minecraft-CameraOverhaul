package mirsario.cameraoverhaul.fabric;

import java.lang.reflect.*;
import java.util.function.*;

import mirsario.cameraoverhaul.common.CameraOverhaul;
import net.fabricmc.loader.api.*;
import net.minecraft.text.*;

public final class TextUtils
{
	// Text construction has changed in 1.19.
	// Since this mod aims to have one universal release, we have to use reflection for this.
	private static final Function<String, Text> createTranslatableText;

	static
	{
		Function<String, Text> function;

		try {
			// >=1.19
			String mappedTextTranslatableMethodName = FabricLoader
				.getInstance()
				.getMappingResolver()
				.mapMethodName("intermediary", "net.minecraft.class_2561", "method_43471", "(Ljava/lang/String;)Lnet/minecraft/text/MutableText;");
			
			CameraOverhaul.Logger.info("Mapped Text.translatable() name: " + mappedTextTranslatableMethodName);
			
			Method textTranslatableMethod = Text.class.getMethod(mappedTextTranslatableMethodName, String.class);

			function = (text) -> {
				try {
					return (Text)textTranslatableMethod.invoke(null, text);
				}
				catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
					e.printStackTrace();
					return null;
				}
			};

			function.apply("test");
			CameraOverhaul.Logger.info("Using >=1.19 translatable text construction.");
		} catch (Exception firstException) {
			// <1.19
			try {
				Constructor<?> translatableTextConstructor = Text.class.getConstructor(String.class);
				
				function = (text) -> {
					try {
						return (Text)translatableTextConstructor.newInstance(text);
					} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
						e.printStackTrace();
						return null;
					}
				};

				function.apply("test");
				CameraOverhaul.Logger.info("Using <1.19 translatable text construction.");
			} catch (Exception secondException) {
				CameraOverhaul.Logger.error("Failed to setup version-agnostic translatable text construction!");
				CameraOverhaul.Logger.error("... >=1.19 attempt failed with: " + firstException.toString());
				CameraOverhaul.Logger.error("... <1.19 attempt failed with: " + secondException.toString());

				throw new RuntimeException();
			}
		}
		
		createTranslatableText = function;
	}

	public static Text CreateTranslatableText(String key)
	{
		return createTranslatableText.apply(key);
	}
}
