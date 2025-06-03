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

package io.github.palexdev.mfxcomponents.controls.base;

import io.github.palexdev.mfxcomponents.behaviors.MFXButtonBehaviorBase;
import io.github.palexdev.mfxcore.base.properties.EventHandlerProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;

/// Base class for all `MaterialFX` buttons. Extends [MFXLabeled] since _most_ of them can display text.
/// Uses behaviors that inherit from [MFXButtonBehaviorBase].
///
/// Implements the most basic properties and behaviors of each button, such as the [#onActionProperty()] and the
/// [#trigger()] method (JavaFX counterpart is fire()).
///
/// Since it's a base class, it has no style classes.
public abstract class MFXButtonBase<B extends MFXButtonBehaviorBase<?>> extends MFXLabeled<B> {
    //================================================================================
    // Properties
    //================================================================================
    private final EventHandlerProperty<ActionEvent> onAction = new EventHandlerProperty<>() {
        @Override
        protected void invalidated() {
            setEventHandler(ActionEvent.ACTION, get());
        }
    };

    //================================================================================
    // Constructors
    //================================================================================
    public MFXButtonBase() {}

    public MFXButtonBase(String text) {
        super(text);
    }

    public MFXButtonBase(String text, Node graphic) {
        super(text, graphic);
    }

    //================================================================================
    // Methods
    //================================================================================

    /// If the button is not disabled, fires a new [ActionEvent], triggering the [EventHandler] specified
    /// by the [#onActionProperty()].
    public void trigger() {
        if (!isDisabled()) fireEvent(new ActionEvent());
    }

    //================================================================================
    // Getters/Setters
    //================================================================================
    public EventHandler<ActionEvent> getOnAction() {
        return onAction.get();
    }

    /// Specifies the action to execute when an [ActionEvent] is fired on this button.
    public EventHandlerProperty<ActionEvent> onActionProperty() {
        return onAction;
    }

    public void setOnAction(EventHandler<ActionEvent> onAction) {
        this.onAction.set(onAction);
    }
}
