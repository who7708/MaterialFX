/*
 * Copyright (C) 2022 Parisi Alessandro - alessandro.parisi406@gmail.com
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

package io.github.palexdev.mfxcore.behavior;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

import io.github.palexdev.mfxcore.events.WhenEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.input.TouchEvent;

/// Base class to implement behavioral code for any kind of control. In the MVC pattern, the behavior would be the
/// equivalent of the controller. This offers methods that cover most types of input events.
///
/// The component's view (the skin) adds the necessary [EventHandlers][EventHandler] on the component itself or its children,
/// and delegates to the behavior class.
///
/// Thanks to the encapsulation offered by this pattern, the skin and the behavior of a component can be easily changed
/// with ease at any time, and it would still be functional.
///
/// Actions are taken in the form of [WhenEvent] constructs, they can be added by wrapping them in a
/// [#register(WhenEvent[])] call. The constructs are added into a list and can be deactivated/disposed by invoking
/// [#dispose()].
public abstract class BehaviorBase<N extends Node> {
    //================================================================================
    // Properties
    //================================================================================
    private N node;
    private final List<DisposableAction> actions = new ArrayList<>();

    //================================================================================
    // Constructors
    //================================================================================
    public BehaviorBase(N node) {
        this.node = node;
    }

    //================================================================================
    // Methods
    //================================================================================

    /// Behaviors can specify a set of actions to initialize themselves if needed.
    public void init() {}

    /// The behavior API registers input actions in the form of [WhenEvent] constructs. This method adds them
    /// to a list (which will be used for disposal, avoiding memory leaks when calling [#dispose()]).
    ///
    /// Also note that if the constructs were not activated before by invoking [WhenEvent#register()], this method
    /// will do it for you automatically.
    @SafeVarargs
    public final <T extends Event> void register(WhenEvent<T>... wes) {
        for (WhenEvent<T> w : wes) {
            if (!w.isActive()) w.register();
            actions.add(w);
        }
    }

    /// Calls [#dispose()] on all the registered actions, then clears
    /// the list and sets the node field to `null` making this behavior not usable anymore.
    public void dispose() {
        actions.forEach(DisposableAction::dispose);
        actions.clear();
        node = null;
    }

    //================================================================================
    // Events Specific Methods
    //================================================================================

    // Mouse

    /// Should be used by subclasses to handle [MouseEvent#MOUSE_PRESSED] events.
    ///
    /// The caller can use the callback to register additional actions to perform after the behavior code.
    /// Behaviors should not assume a valid callback, in other words, a null callback is valid and will simply be ignored.
    public void mousePressed(MouseEvent e, Consumer<MouseEvent> callback) {
        if (callback != null) callback.accept(e);
    }

    /// Convenience delegate method for [#mousePressed(MouseEvent, Consumer)], invoked with a `null` callback.
    public void mousePressed(MouseEvent e) {
        mousePressed(e, null);
    }

    /// Should be used by subclasses to handle [MouseEvent#MOUSE_RELEASED] events.
    ///
    /// The caller can use the callback to register additional actions to perform after the behavior code.
    /// Behaviors should not assume a valid callback, in other words, a null callback is valid and will simply be ignored.
    public void mouseReleased(MouseEvent e, Consumer<MouseEvent> callback) {
        if (callback != null) callback.accept(e);
    }

    /// Convenience delegate method for [#mouseReleased(MouseEvent, Consumer)], invoked with a `null` callback.
    public void mouseReleased(MouseEvent e) {
        mouseReleased(e, null);
    }

    /// Should be used by subclasses to handle [MouseEvent#MOUSE_CLICKED] events.
    ///
    /// The caller can use the callback to register additional actions to perform after the behavior code.
    /// Behaviors should not assume a valid callback, in other words, a null callback is valid and will simply be ignored.
    public void mouseClicked(MouseEvent e, Consumer<MouseEvent> callback) {
        if (callback != null) callback.accept(e);
    }

    /// Convenience delegate method for [#mouseClicked(MouseEvent, Consumer)], invoked with a `null` callback.
    public void mouseClicked(MouseEvent e) {
        mouseClicked(e, null);
    }

    /// Should be used by subclasses to handle [MouseEvent#MOUSE_MOVED] events.
    ///
    /// The caller can use the callback to register additional actions to perform after the behavior code.
    /// Behaviors should not assume a valid callback, in other words, a null callback is valid and will simply be ignored.
    public void mouseMoved(MouseEvent e, Consumer<MouseEvent> callback) {
        if (callback != null) callback.accept(e);
    }

    /// Convenience delegate method for [#mouseMoved(MouseEvent, Consumer)], invoked with a `null` callback.
    public void mouseMoved(MouseEvent e) {
        mouseMoved(e, null);
    }

    /// Should be used by subclasses to handle [MouseEvent#MOUSE_DRAGGED] events.
    ///
    /// The caller can use the callback to register additional actions to perform after the behavior code.
    /// Behaviors should not assume a valid callback, in other words, a null callback is valid and will simply be ignored.
    public void mouseDragged(MouseEvent e, Consumer<MouseEvent> callback) {
        if (callback != null) callback.accept(e);
    }

    /// Convenience delegate method for [#mouseDragged(MouseEvent, Consumer)], invoked with a `null` callback.
    public void mouseDragged(MouseEvent e) {
        mouseDragged(e, null);
    }

    /// Should be used by subclasses to handle [MouseEvent#MOUSE_ENTERED] events.
    ///
    /// The caller can use the callback to register additional actions to perform after the behavior code.
    /// Behaviors should not assume a valid callback, in other words, a null callback is valid and will simply be ignored.
    public void mouseEntered(MouseEvent e, Consumer<MouseEvent> callback) {
        if (callback != null) callback.accept(e);
    }

    /// Convenience delegate method for [#mouseEntered(MouseEvent, Consumer)], invoked with a `null` callback.
    public void mouseEntered(MouseEvent e) {
        mouseEntered(e, null);
    }

    /// Should be used by subclasses to handle [MouseEvent#MOUSE_EXITED] events.
    ///
    /// The caller can use the callback to register additional actions to perform after the behavior code.
    /// Behaviors should not assume a valid callback, in other words, a null callback is valid and will simply be ignored.
    public void mouseExited(MouseEvent e, Consumer<MouseEvent> callback) {
        if (callback != null) callback.accept(e);
    }

    /// Convenience delegate method for [#mouseExited(MouseEvent, Consumer)], invoked with a `null` callback.
    public void mouseExited(MouseEvent e) {
        mouseExited(e, null);
    }

    // Keys

    /// Should be used by subclasses to handle [KeyEvent#KEY_PRESSED] events.
    ///
    /// The caller can use the callback to register additional actions to perform after the behavior code.
    /// Behaviors should not assume a valid callback, in other words, a null callback is valid and will simply be ignored.
    public void keyPressed(KeyEvent e, Consumer<KeyEvent> callback) {
        if (callback != null) callback.accept(e);
    }

    /// Convenience delegate method for [#keyPressed(KeyEvent,Consumer)], invoked with a `null` callback.
    public void keyPressed(KeyEvent e) {
        keyPressed(e, null);
    }

    /// Should be used by subclasses to handle [KeyEvent#KEY_RELEASED] events.
    ///
    /// The caller can use the callback to register additional actions to perform after the behavior code.
    /// Behaviors should not assume a valid callback, in other words, a null callback is valid and will simply be ignored.
    public void keyReleased(KeyEvent e, Consumer<KeyEvent> callback) {
        if (callback != null) callback.accept(e);
    }

    /// Convenience delegate method for [#keyReleased(KeyEvent,Consumer)], invoked with a `null` callback.
    public void keyReleased(KeyEvent e) {
        keyReleased(e, null);
    }

    /// Should be used by subclasses to handle [KeyEvent#KEY_TYPED] events.
    ///
    /// The caller can use the callback to register additional actions to perform after the behavior code.
    /// Behaviors should not assume a valid callback, in other words, a null callback is valid and will simply be ignored.
    public void keyTyped(KeyEvent e, Consumer<KeyEvent> callback) {
        if (callback != null) callback.accept(e);
    }

    /// Convenience delegate method for [#keyTyped(KeyEvent,Consumer)], invoked with a `null` callback.
    public void keyTyped(KeyEvent e) {
        keyTyped(e, null);
    }

    // Touch

    /// Should be used by subclasses to handle [TouchEvent#TOUCH_PRESSED] events.
    ///
    /// The caller can use the callback to register additional actions to perform after the behavior code.
    /// Behaviors should not assume a valid callback, in other words, a null callback is valid and will simply be ignored.
    public void touchPressed(TouchEvent e, Consumer<TouchEvent> callback) {
        if (callback != null) callback.accept(e);
    }

    /// Convenience delegate method for [#touchPressed(TouchEvent, Consumer)], invoked with a `null` callback.
    public void touchPressed(TouchEvent e) {
        touchPressed(e, null);
    }

    /// Should be used by subclasses to handle [TouchEvent#TOUCH_RELEASED] events.
    ///
    /// The caller can use the callback to register additional actions to perform after the behavior code.
    /// Behaviors should not assume a valid callback, in other words, a null callback is valid and will simply be ignored.
    public void touchReleased(TouchEvent e, Consumer<TouchEvent> callback) {
        if (callback != null) callback.accept(e);
    }

    /// Convenience delegate method for [#touchReleased(TouchEvent, Consumer)], invoked with a `null` callback.
    public void touchReleased(TouchEvent e) {
        touchReleased(e, null);
    }

    /// Should be used by subclasses to handle [TouchEvent#TOUCH_MOVED] events.
    ///
    /// The caller can use the callback to register additional actions to perform after the behavior code.
    /// Behaviors should not assume a valid callback, in other words, a null callback is valid and will simply be ignored.
    public void touchMoved(TouchEvent e, Consumer<TouchEvent> callback) {
        if (callback != null) callback.accept(e);
    }

    /// Convenience delegate method for [#touchMoved(TouchEvent, Consumer)], invoked with a `null` callback.
    public void touchMoved(TouchEvent e) {
        touchMoved(e, null);
    }

    /// Should be used by subclasses to handle [TouchEvent#TOUCH_STATIONARY] events.
    ///
    /// The caller can use the callback to register additional actions to perform after the behavior code.
    /// Behaviors should not assume a valid callback, in other words, a null callback is valid and will simply be ignored.
    public void touchStationary(TouchEvent e, Consumer<TouchEvent> callback) {
        if (callback != null) callback.accept(e);
    }

    /// Convenience delegate method for [#touchStationary(TouchEvent, Consumer)], invoked with a `null` callback.
    public void touchStationary(TouchEvent e) {
        touchStationary(e, null);
    }

    // Scroll

    /// Should be used by subclasses to handle [ScrollEvent#SCROLL] events.
    ///
    /// The caller can use the callback to register additional actions to perform after the behavior code.
    /// Behaviors should not assume a valid callback, in other words, a null callback is valid and will simply be ignored.
    public void scroll(ScrollEvent e, Consumer<ScrollEvent> callback) {
        if (callback != null) callback.accept(e);
    }

    /// Convenience delegate method for [#scroll(ScrollEvent, Consumer)], invoked with a `null` callback.
    public void scroll(ScrollEvent e) {
        scroll(e, null);
    }

    //================================================================================
    // Getters/Setters
    //================================================================================

    /// @return the node on which this behavior is applied
    public N getNode() {
        return node;
    }

    /// @return the list of registered actions as an unmodifiable list
    public List<DisposableAction> getActions() {
        return Collections.unmodifiableList(actions);
    }
}
