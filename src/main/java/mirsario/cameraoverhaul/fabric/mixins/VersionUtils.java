package mirsario.cameraoverhaul.fabric.mixins;

import net.fabricmc.loader.api.*;
import net.fabricmc.loader.api.metadata.version.*;

public final class VersionUtils
{
	public static boolean Matches(Version version, String versionText) throws Exception
	{
		if (version instanceof SemanticVersion) {
			return VersionPredicate.parse(versionText).test(version);
		} else {
			throw new Exception("Unknown minecraft version type!");
		}
	}
}