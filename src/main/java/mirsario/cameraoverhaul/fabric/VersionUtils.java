package mirsario.cameraoverhaul.fabric;

import java.text.*;
import net.fabricmc.loader.api.*;
import net.fabricmc.loader.api.metadata.*;
import net.fabricmc.loader.api.metadata.version.*;
import mirsario.cameraoverhaul.common.*;

public final class VersionUtils
{
	public static final ModMetadata MinecraftMetadata = FabricLoader.getInstance().getModContainer("minecraft").get().getMetadata();
	public static final Version MinecraftVersion = MinecraftMetadata.getVersion();
	
	public static boolean MinecraftVersionMatches(String versionExpression)
	{
		return Matches(MinecraftVersion, versionExpression);
	}

	public static boolean Matches(Version version, String versionExpression)
	{
		if (!(version instanceof SemanticVersion)) {
			CameraOverhaul.Logger.error("Unknown minecraft version type!");
			return false;
		}

		try {
			return VersionPredicate.parse(versionExpression).test(version);
		} catch (VersionParsingException e) {
			CameraOverhaul.Logger.error(MessageFormat.format("Error parsing version expression '{0}': {1}", versionExpression, e.getMessage()));
			
			return false;
		}
	}
}