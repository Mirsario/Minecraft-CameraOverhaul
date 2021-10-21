package mirsario.cameraoverhaul.fabric.mixins;

import org.objectweb.asm.tree.*;
import org.spongepowered.asm.mixin.extensibility.*;
import mirsario.cameraoverhaul.common.*;
import java.util.*;
import net.fabricmc.loader.api.*;
import net.fabricmc.loader.api.metadata.*;

public class MixinPlugin implements IMixinConfigPlugin
{
	public static String BaseMixinPath = "mirsario.cameraoverhaul.fabric.mixins.";

	@Override
	public boolean shouldApplyMixin(String targetClassName, String mixinClassName)
	{
		if (!mixinClassName.startsWith(BaseMixinPath)) {
			return true;
		}

		String relativeName = mixinClassName.substring(BaseMixinPath.length());

		boolean isLegacy;

		if (relativeName.startsWith("legacy.")) {
			isLegacy = true;
		} else if (relativeName.startsWith("modern.")) {
			isLegacy = false;
		} else {
			return true;
		}

		ModMetadata metadata = FabricLoader.getInstance().getModContainer("minecraft").get().getMetadata();
		boolean legacyGameVersion;

		try {
			legacyGameVersion = VersionPredicateParser.matches(metadata.getVersion(), "<1.15");
		} catch (VersionParsingException e) {
			e.printStackTrace();

			legacyGameVersion = true;
		}

		boolean result = legacyGameVersion == isLegacy;

		CameraOverhaul.Logger.info((result ? "Using" : "Skipping") + " " + (isLegacy ? "legacy" : "modern") + " mixin '" + mixinClassName + "'.");

		return result;
    }

    @Override
    public void onLoad(String mixinPackage) { }

    @Override
    public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) { }

    @Override
    public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) { }

    @Override
    public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) { }

    @Override
    public String getRefMapperConfig()
    {
        return null;
    }

    @Override
    public List<String> getMixins()
    {
        return null;
	}
}