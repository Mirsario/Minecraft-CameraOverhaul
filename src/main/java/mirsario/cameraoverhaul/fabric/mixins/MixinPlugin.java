package mirsario.cameraoverhaul.fabric.mixins;

import org.objectweb.asm.tree.*;
import org.spongepowered.asm.mixin.extensibility.*;
import mirsario.cameraoverhaul.common.*;
import mirsario.cameraoverhaul.fabric.*;
import java.util.*;

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

		boolean legacyGameVersion = VersionUtils.MinecraftVersionMatches("<1.15");
		boolean result = legacyGameVersion == isLegacy;

		CameraOverhaul.Logger.info("CameraOverhaul: " + (result ? "Using" : "Skipping") + " " + (isLegacy ? "legacy" : "modern") + " mixin '" + mixinClassName + "'.");

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