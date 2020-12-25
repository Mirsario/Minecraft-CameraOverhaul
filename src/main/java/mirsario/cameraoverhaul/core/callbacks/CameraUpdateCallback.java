package mirsario.cameraoverhaul.core.callbacks;

import mirsario.cameraoverhaul.core.events.*;
import mirsario.cameraoverhaul.core.structures.*;
import net.minecraft.client.render.*;

public interface CameraUpdateCallback
{
	Event<CameraUpdateCallback> EVENT = EventHelper.CreateEvent(CameraUpdateCallback.class,
		(listeners) -> (camera, transform, deltaTime) -> {
			for(CameraUpdateCallback listener : listeners) {
				listener.OnCameraUpdate(camera, transform, deltaTime);
			}
		}
	);
	
	void OnCameraUpdate(Camera camera, Transform cameraTransform, float deltaTime);
}