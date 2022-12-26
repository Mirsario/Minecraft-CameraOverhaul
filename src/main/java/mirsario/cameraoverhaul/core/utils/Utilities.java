package mirsario.cameraoverhaul.core.utils;

import java.util.function.*;
import mirsario.cameraoverhaul.common.*;

public final class Utilities
{
	@SafeVarargs
	public static <T> T GetAndLogFirstResult(String methodName, Supplier<T>... suppliers)
	{
		for (int i = 0; i < suppliers.length; i++) {
			T result;

			try {
				result = suppliers[i].get();
			}
			catch (Exception e) {
				continue;
			}

			if (result != null) {
				CameraOverhaul.Logger.info("CameraOverhaul: Using implementation #" + i + " for abstraction '" + methodName + "'.");
				return result;
			}
		}
		
		return null;
	}
}
