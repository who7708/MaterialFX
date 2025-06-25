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

package io.github.palexdev.mfxcomponents.behaviors;

import java.util.function.Consumer;

import io.github.palexdev.mfxcomponents.controls.base.MFXSelectable;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;

/// Defines the common behavior for all [MFXSelectable] components and overrides some methods from [MFXButtonBehaviorBase]
/// to redirect to [#handleSelection()].
public class MFXSelectableBehavior<S extends MFXSelectable<?>> extends MFXButtonBehaviorBase<S> {

    //================================================================================
    // Constructors
    //================================================================================
    public MFXSelectableBehavior(S selectable) {
        super(selectable);
    }

    //================================================================================
    // Methods
    //================================================================================

    /// Toggles the selection state of the [MFXSelectable] node.
    ///
    /// Because [MFXSelectable] inherits all properties and behaviors from a generic button, we have to make a distinction
    /// between the action and selection states. By design, the triggering of an action simply indicates user interaction with
    /// the component but does not necessarily mean that the selection state has changed.
    ///
    /// So, if you strictly want to perform some action when the selection state changes, you can add a listener on the
    /// [MFXSelectable#selectedProperty()] or use [MFXSelectable#setOnSelectionChanged(Consumer)].
    ///
    /// Otherwise, you can use the [MFXSelectable#onActionProperty()] and still query the selection state if needed.
    protected void handleSelection() {
        S selectable = getNode();
        if (!selectable.selectedProperty().isBound()) {
            selectable.setSelected(!selectable.isSelected());
        }
        selectable.trigger();
    }

    //================================================================================
    // Overridden Methods
    //================================================================================

    /// Calls [#handleSelection()] if the clicked mouse button was [MouseButton#PRIMARY].
    @Override
    public void mouseClicked(MouseEvent e, Consumer<MouseEvent> callback) {
        if (e.getButton() == MouseButton.PRIMARY) handleSelection();
        if (callback != null) callback.accept(e);
    }

    /// Calls [#handleSelection()] if the pressed key was [KeyCode#ENTER] or [KeyCode#SPACE].
    @Override
    public void keyPressed(KeyEvent e, Consumer<KeyEvent> callback) {
        if (e.getCode() == KeyCode.ENTER || e.getCode() == KeyCode.SPACE)
            handleSelection();
        if (callback != null) callback.accept(e);
    }
}
