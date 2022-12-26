package mirsario.cameraoverhaul.fabric.abstractions;

import java.lang.reflect.*;

import mirsario.cameraoverhaul.core.utils.*;
import net.minecraft.text.*;

public final class TextAbstractions
{
	private interface CreateTextFunction
	{
		Text invoke(String text);
	}

	private static final CreateTextFunction createText;

	static
	{
		// Text construction has changed in 1.19.
		// Since this mod aims to have one universal release, we have to use reflection for this.
		createText = Utilities.GetAndLogFirstResult(
			"CreateText",
			() -> GetCreateText_V2(),
			() -> GetCreateText_V1()
		);
	}

	// >= 1.19
	private static CreateTextFunction GetCreateText_V2()
	{
		Method textTranslatableMethod = ReflectionUtils.TryGetObfuscatedMethod(
			"net.minecraft.class_2561", "method_43471", "(Ljava/lang/String;)Lnet/minecraft/text/MutableText;",
			new Class<?>[] { String.class }
		);

		if (textTranslatableMethod == null) {
			return null;
		}

		return (text) -> {
			return (Text)ReflectionUtils.TryInvokeMethod(textTranslatableMethod, null, text);
		};
	}
	
	// <= 1.18.2
	private static CreateTextFunction GetCreateText_V1()
	{
		return (text) -> {
			return new TranslatableText(text);
		};
	}

	public static Text CreateText(String key)
	{
		return createText.invoke(key);
	}
}
