package mirsario.cameraoverhaul.core.callbacks;

import mirsario.cameraoverhaul.core.events.*;
import mirsario.cameraoverhaul.core.structures.*;
import net.minecraft.client.render.*;
import net.minecraft.entity.Entity;

public interface CameraUpdateCallback
{
	Event<CameraUpdateCallback> EVENT = EventHelper.CreateEvent(CameraUpdateCallback.class,
		(listeners) -> (focusedEntity, camera, transform, deltaTime) -> {
			for(CameraUpdateCallback listener : listeners) {
				listener.OnCameraUpdate(focusedEntity, camera, transform, deltaTime);
			}
		}
	);
	
	void OnCameraUpdate(Entity focusedEntity, Camera camera, Transform cameraTransform, float deltaTime);
}