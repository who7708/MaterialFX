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
import io.github.palexdev.mfxcore.selection.Selectable;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;

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
    /// It's crucial to understand how this method works. There's a big difference between `MaterialFX` selectable components
    /// and JavaFX ones.
    ///
    /// For starters, not all of 'MaterialFX' selectable components can be selected by default. For example, basic buttons
    /// and icon buttons can act both as standard buttons and toggle buttons.
    /// This method toggles the selection state only if [Selectable#isSelectable()] returns `true` and the property is not
    /// bound.
    ///
    /// However, there's a distinction between the click state and the selection state. In fact, this method will always
    /// invoke the [MFXSelectable#trigger()] method, and comes after updating the selection state.
    ///
    /// ### TL;DR
    ///
    /// If you strictly want to perform some action when the selection state changes, you should add a listener on the
    /// [Selectable#selectedProperty()] or use [MFXSelectable#setOnSelectionChanged(Consumer)].
    ///
    /// Otherwise, you can use the [MFXSelectable#onActionProperty()] and still query the selection state if needed.
    protected void handleSelection() {
        S selectable = getNode();

        if (selectable.isSelectable() && !selectable.selectedProperty().isBound()) {
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

    /// Calls [#handleSelection()] if the pressed key was [KeyCode#ENTER].
    @Override
    public void keyPressed(KeyEvent e, Consumer<KeyEvent> callback) {
        if (e.getCode() == KeyCode.ENTER) handleSelection();
        if (callback != null) callback.accept(e);
    }
}
