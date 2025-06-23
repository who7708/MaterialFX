package io.github.palexdev.mfxcore.events;

import io.github.palexdev.mfxcore.events.bus.IEvent;
import io.github.palexdev.mfxcore.events.bus.Subscriber;

/// Basic implementation of [IEvent] and starting point to implement real events.
///
/// Many frameworks with similar capabilities use events that can carry some data. While this is not strictly necessary
/// for an event bus, it is indeed nice to have. The data being carried can provide valuable information to the [Subscriber].
/// It's not limited solely to context-explaining data; it can also be used to transmit instances throughout an application,
/// for example.
///
/// @see IEvent
public abstract class Event implements IEvent {
    //================================================================================
    // Properties
    //================================================================================
    private final Object data;

    //================================================================================
    // Constructors
    //================================================================================
    protected Event() {
        this(null);
    }

    public Event(Object data) {
        this.data = data;
    }

    //================================================================================
    // Overridden Methods
    //================================================================================
    @Override
    public Object data() {
        return data;
    }
}
