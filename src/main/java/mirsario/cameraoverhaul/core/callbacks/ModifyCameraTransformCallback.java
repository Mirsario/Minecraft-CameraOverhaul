package mirsario.cameraoverhaul.core.callbacks;

import mirsario.cameraoverhaul.core.events.*;
import mirsario.cameraoverhaul.core.structures.*;
import net.minecraft.client.render.*;

public interface ModifyCameraTransformCallback
{
	Event<ModifyCameraTransformCallback> EVENT = EventHelper.CreateEvent(ModifyCameraTransformCallback.class,
		(listeners) -> (camera, transform) -> {
			for(ModifyCameraTransformCallback listener : listeners) {
				transform = listener.ModifyCameraTransform(camera, transform);
			}

			return transform;
		}
	);
	
	Transform ModifyCameraTransform(Camera camera, Transform transform);
}