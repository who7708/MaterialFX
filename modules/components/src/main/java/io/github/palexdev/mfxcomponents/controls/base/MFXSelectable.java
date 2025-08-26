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

import java.util.function.Consumer;

import io.github.palexdev.mfxcomponents.behaviors.MFXSelectableBehavior;
import io.github.palexdev.mfxcore.selection.Selectable;
import io.github.palexdev.mfxcore.selection.SelectionGroup;
import io.github.palexdev.mfxcore.selection.SelectionGroupProperty;
import io.github.palexdev.mfxcore.selection.SelectionProperty;
import io.github.palexdev.mfxcore.utils.fx.PseudoClasses;
import javafx.scene.Node;

/// Base class for all `MaterialFX` components which behave like buttons and are selectable.
/// Extends [MFXButtonBase] and implements the selection API defined by the [Selectable] interface.
///
/// Expects behaviors of type [MFXSelectableBehavior].
///
/// @see SelectionProperty
/// @see SelectionGroupProperty
/// @see SelectionGroup
public abstract class MFXSelectable<B extends MFXSelectableBehavior<?>> extends MFXButtonBase<B> implements Selectable {
    //================================================================================
    // Properties
    //================================================================================
    private final SelectionGroupProperty selectionGroup = new SelectionGroupProperty(this);
    private final SelectionProperty selected = new SelectionProperty(this) {
        @Override
        protected void invalidated() {
            super.invalidated();
            onSelectionChanged(get());
        }
    };
    private Consumer<Boolean> onSelectionChanged;

    //================================================================================
    // Constructors
    //================================================================================
    public MFXSelectable() {}

    public MFXSelectable(String text) {
        super(text);
    }

    public MFXSelectable(String text, Node graphic) {
        super(text, graphic);
    }

    //================================================================================
    // Methods
    //================================================================================

    /// This is automatically called by [#selectedProperty()] after the selection has changed and has become invalid.
    ///
    /// Allows executing any action given the new selection state by either overriding this or using [#setOnSelectionChanged(Consumer)].
    ///
    /// By default, de-/activates the ':selected' pseudo-class on the component and calls the user-specified action.
    ///
    /// @see MFXSelectableBehavior
    protected void onSelectionChanged(boolean state) {
        PseudoClasses.SELECTED.setOn(this, state);
        if (onSelectionChanged != null) onSelectionChanged.accept(state);
    }

    //================================================================================
    // Getters/Setters
    //================================================================================
    @Override
    public SelectionGroupProperty selectionGroupProperty() {
        return selectionGroup;
    }

    @Override
    public SelectionProperty selectedProperty() {
        return selected;
    }

    /// @return the action to execute when the selection state changes.
    public Consumer<Boolean> getOnSelectionChanged() {
        return onSelectionChanged;
    }

    /// Sets the action to execute when the selection state changes.
    public void setOnSelectionChanged(Consumer<Boolean> onSelectionChanged) {
        this.onSelectionChanged = onSelectionChanged;
    }
}
