package net.petercashel.commonlib.event.core;

public class Event {

	public final String getEventName()
    {
        return getClass().getSimpleName();
    }
}
