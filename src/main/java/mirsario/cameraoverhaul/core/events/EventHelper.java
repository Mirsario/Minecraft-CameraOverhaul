package mirsario.cameraoverhaul.core.events;

import java.util.*;
import java.util.function.*;

public final class EventHelper
{
	private static final List<Event<?>> Events = new ArrayList<>();

	private EventHelper() { }

	public static void Update()
	{
		Events.forEach(Event::Update);
	}

	public static <T> Event<T> CreateEvent(Class<? super T> type, Function<T[], T> invokerFactory)
	{
		return CreateEvent(type, null, invokerFactory);
	}

	public static <T> Event<T> CreateEvent(Class<? super T> type, T emptyInvoker, Function<T[], T> invokerFactory)
	{
		Event<T> event = new Event<>(type, emptyInvoker, invokerFactory);

		Events.add(event);

		return event;
	}
}