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

import io.github.palexdev.mfxcomponents.controls.base.MFXButtonBase;
import io.github.palexdev.mfxcore.behavior.BehaviorBase;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;

/// Generic base behavior for all buttons extending from [MFXButtonBase].
public class MFXButtonBehaviorBase<B extends MFXButtonBase<?>> extends BehaviorBase<B> {

    //================================================================================
    // Constructors
    //================================================================================
    public MFXButtonBehaviorBase(B button) {
        super(button);
    }

    //================================================================================
    // Overridden Methods
    //================================================================================

    /// Acquires focus.
    @Override
    public void mousePressed(MouseEvent e, Consumer<MouseEvent> callback) {
        getNode().requestFocus();
        super.mousePressed(e, callback);
    }

    /// Responsible for calling [MFXButtonBase#trigger()] if the clicked mouse button was [MouseButton#PRIMARY].
    @Override
    public void mouseClicked(MouseEvent e, Consumer<MouseEvent> callback) {
        if (e.getButton() == MouseButton.PRIMARY) getNode().trigger();
        super.mouseClicked(e, callback);
    }

    /// Responsible for calling [MFXButtonBase#trigger()] if the pressed key was [KeyCode#ENTER].
    @Override
    public void keyPressed(KeyEvent e, Consumer<KeyEvent> callback) {
        if (e.getCode() == KeyCode.ENTER) getNode().trigger();
        super.keyPressed(e, callback);
    }
}
