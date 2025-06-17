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

import io.github.palexdev.mfxcomponents.controls.MFXButton;
import io.github.palexdev.mfxcore.selection.SelectionGroup;

/// Specialization of [MFXSelectableBehavior] for [MFXButton] which by design can act both as a standard button and as a toggle.
public class MFXButtonBehavior extends MFXSelectableBehavior<MFXButton> {

    //================================================================================
    // Constructors
    //================================================================================
    public MFXButtonBehavior(MFXButton button) {
        super(button);
    }

    //================================================================================
    // Overridden Methods
    //================================================================================

    /// {@inheritDoc}
    ///
    /// Overridden to take into account the [MFXButton#toggleableProperty()].
    /// The selection state will be switched only if the button is toggleable or is in a [SelectionGroup].
    ///
    /// In the latter case, the [MFXButton#toggleableProperty()] will always be set to `true` here.
    @Override
    protected void handleSelection() {
        MFXButton btn = getNode();
        boolean shouldToggle = btn.isToggleable() || btn.getSelectionGroup() != null;
        if (!shouldToggle) {
            btn.trigger();
            return;
        }

        btn.setToggleable(true); // Make sure it's toggleable
        super.handleSelection();
    }
}
