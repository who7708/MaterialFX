/*
 * Copyright (C) 2025 Parisi Alessandro - alessandro.parisi406@gmail.com
 * This file is part of MaterialFX (https://github.com/palexdev/MaterialFX)
 *
 * MaterialFX is free software: you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 3 of the License,
 * or (at your option) any later version.
 *
 * MaterialFX is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with MaterialFX. If not, see <http://www.gnu.org/licenses/>.
 */

package io.github.palexdev.mfxcore.events.bus;

import io.github.palexdev.mfxcore.events.Event;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.WeakHashMap;
import java.util.function.Consumer;

/// As the name suggests, this class groups multiple [EventBus] in a "network". The goal is to implement event listening
/// and delivering on and to specific buses through a very simple tagging system. The idea is to build this on top of the
/// existing [EventBus] API and [SimpleEventBus] implementation.
///
/// There can be only one network, therefore, this is a singleton. The network always has one bus, which is a general purpose
/// bus, the `root`, that can be used for all untagged events.
///
/// With every [BusTag] are associated one and one only [EventBus]. When subscribing for a tagged event, the bus is created
/// automatically. When unsubscribing, the bus is cleared and removed if it has no subscribers left. Additionally, it's
/// possible to entirely clear and remove a tagged bus through [#removeBus(BusTag)].
///
/// It's also possible at any time to retrieve the names/tags of the currently active buses through [#getActiveBuses()].
public class EventBusNetwork {
    //================================================================================
    // Singleton
    //================================================================================
    private static EventBusNetwork instance;

    public static EventBusNetwork instance() {
        if (instance == null) instance = new EventBusNetwork();
        return instance;
    }

    //================================================================================
    // Properties
    //================================================================================
    private final SimpleEventBus root = new SimpleEventBus();
    private final Map<BusTag, EventBus> network = new HashMap<>();

    //================================================================================
    // Constructors
    //================================================================================
    private EventBusNetwork() {
        network.put(BusTag.ROOT, root);
    }

    //================================================================================
    // Methods
    //================================================================================

    /// Publishes the given event on the `root` bus.
    public <E extends Event> EventBusNetwork publish(E event) {
        root.publish(event);
        return this;
    }

    /// Publishes the given event on the bus associated with the given tag if present, otherwise does nothing.
    public <E extends Event> EventBusNetwork publish(BusTag tag, E event) {
        Optional.ofNullable(network.get(tag))
            .ifPresent(b -> b.publish(event));
        return this;
    }

    /// Adds a subscription on the `root` bus.
    public <E extends Event> EventBusNetwork subscribe(Class<E> eventType, Subscriber<E> subscriber) {
        root.subscribe(eventType, subscriber);
        return this;
    }

    /// Adds a subscription with the given priority on the `root` bus.
    public <E extends Event> EventBusNetwork subscribe(Class<E> eventType, Consumer<E> subscriber, int priority) {
        root.subscribe(eventType, subscriber, priority);
        return this;
    }

    /// Creates or retrieves the bus associated to the given tag, then register a subscription on it.
    public <E extends Event> EventBusNetwork subscribe(BusTag tag, Class<E> eventType, Subscriber<E> subscriber) {
        EventBus bus = network.computeIfAbsent(tag, _ -> new SimpleEventBus());
        bus.subscribe(eventType, subscriber);
        return this;
    }

    /// Creates or retrieves the bus associated to the given tag, then register a subscription with the given priority on it.
    public <E extends Event> EventBusNetwork subscribe(BusTag tag, Class<E> eventType, Consumer<E> subscriber, int priority) {
        EventBus bus = network.computeIfAbsent(tag, _ -> new SimpleEventBus());
        bus.subscribe(eventType, subscriber, priority);
        return this;
    }

    /// Removes the given subscription for the given event type from the `root` bus.
    public <E extends Event> EventBusNetwork unsubscribe(Class<E> eventType, Subscriber<E> subscriber) {
        root.unsubscribe(eventType, subscriber);
        return this;
    }

    /// Removes the given subscription for the given event type from bus associated to the given tag.
    public <E extends Event> EventBusNetwork unsubscribe(BusTag tag, Class<E> eventType, Subscriber<E> subscriber) {
        Optional.ofNullable(network.get(tag))
            .ifPresent(b -> {
                b.unsubscribe(eventType, subscriber);
                if (!b.hasSubscribers()) network.remove(tag);
            });
        return this;
    }

    /// Removes the bus associated to the given tag from the network and if present removes all subscriptions with [EventBus#clear()].
    public void removeBus(BusTag tag) {
        if (tag == BusTag.ROOT) return;
        EventBus bus = network.remove(tag);
        if (bus != null) bus.clear();
    }

    /// @return an unmodifiable map containing all the currently active buses
    /// (at least one subscriber, the `root` being the exception)
    public Map<BusTag, EventBus> getActiveBuses() {
        return Collections.unmodifiableMap(network);
    }

    //================================================================================
    // Inner Classes
    //================================================================================

    /// Simple record to associate an [EventBus] to a certain name/id/tag, whatever you want to call it.
    ///
    /// Ideally, buses should have been associated to something that is constant, stable, like enums. But that would have
    /// killed flexibility. So, I opted for records, a generic name as a string, and static factories.<br >
    /// The recommended way to create a tag is to use the latter:
    /// - [#tag(Object)] generates a tag using the given object as the key. The string is the hash code returned by
    /// [System#identityHashCode(Object)]
    /// - [#tag(Enum)] generates a tag from the name of an enum constant. You could make an enumerator which constants
    /// define the domain/context of different event buses.
    ///
    /// Both factories will create the tag object only the first time and retrieve it from a cache for later calls.
    public record BusTag(String name) {

        /// This tag identifies the `root` event bus in the network. To be fair, this is not even necessary since methods
        /// that require the `root` operate directly on its instance rather than retrieving it from the map.
        public static final BusTag ROOT = new BusTag();

        private static final Map<Object, BusTag> CACHE = new WeakHashMap<>();

        private BusTag() {
            this(null);
        }

        /// @throws IllegalArgumentException if the name is `null`, only the `root` is allowed to have that
        public BusTag {
            if (name == null && ROOT != null)
                throw new IllegalArgumentException("BusTag name cannot be null");
        }

        public static BusTag of(String name) {
            return CACHE.computeIfAbsent(name, _ -> new BusTag(name));
        }

        public static BusTag tag(Object o) {
            return CACHE.computeIfAbsent(o, _ -> new BusTag(String.valueOf(System.identityHashCode(o))));
        }

        public static <E extends Enum<E>> BusTag tag(E enumValue) {
            return CACHE.computeIfAbsent(enumValue, _ -> new BusTag(enumValue.name()));
        }
    }
}
