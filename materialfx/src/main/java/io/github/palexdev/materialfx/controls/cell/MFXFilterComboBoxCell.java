/*
 * Copyright (C) 2022 Parisi Alessandro
 * This file is part of MaterialFX (https://github.com/palexdev/MaterialFX).
 *
 * MaterialFX is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * MaterialFX is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with MaterialFX.  If not, see <http://www.gnu.org/licenses/>.
 */

package io.github.palexdev.materialfx.controls.cell;

import io.github.palexdev.materialfx.collections.RefineList;
import io.github.palexdev.materialfx.controls.base.MFXCombo;
import io.github.palexdev.mfxcore.builders.bindings.BooleanBindingBuilder;
import io.github.palexdev.virtualizedfx.base.VFXContainer;

/**
 * Extends {@link MFXComboBoxCell} to modify the {@link #updateIndex(int)} method.
 */
public class MFXFilterComboBoxCell<T> extends MFXComboBoxCell<T> {
    //================================================================================
    // Properties
    //================================================================================
    private final RefineList<T> refineList;

    //================================================================================
    // Constructors
    //================================================================================
    public MFXFilterComboBoxCell(MFXCombo<T> combo, RefineList<T> refineList, T data) {
        super(combo, data);
        this.refineList = refineList;
    }

    //================================================================================
    // Overridden Methods
    //================================================================================


    @Override
    public void onCreated(VFXContainer<T> container) {
        super.onCreated(container);

        getSelectionModel().ifPresent(sm ->
            selected.bind(BooleanBindingBuilder.build()
                .setMapper(() -> {
                    int index = getIndex();
                    if (refineList.getPredicate() != null) {
                        try {
                            int toSource = refineList.viewToSource(index);
                            return sm.contains(toSource);
                        } catch (IndexOutOfBoundsException ex) {
                            return false;
                        }
                    } else {
                        return sm.contains(index);
                    }
                })
                .addSources(sm.selection(), indexProperty(), refineList.getView())
                .get()
            )
        );
    }
}
