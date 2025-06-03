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

import io.github.palexdev.mfxcore.behavior.BehaviorBase;
import io.github.palexdev.mfxcore.controls.Control;
import io.github.palexdev.mfxcore.controls.SkinBase;
import io.github.palexdev.mfxcore.controls.Styleable;
import javafx.scene.Node;

/// Extension of [Control] and base class for all `MaterialFX` components. The goal is to have a separate hierarchy of
/// controls from the JavaFX one that perfectly integrates with the new behavior and theming APIs.
///
/// In addition to the features brought by [Control], this also implements [Styleable] and makes size computation methods
/// public.
///
/// **Note:** the correct way to change the skin is to call [#changeSkin(SkinBase)].
public abstract class MFXControl<B extends BehaviorBase<? extends Node>> extends Control<B> implements Styleable {


    //================================================================================
    // Overridden Methods
    //================================================================================
    @Override
    protected void init() {
        defaultStyleClasses(this);
        super.init();
    }

    @Override
    public double computeMinWidth(double height) {
        return super.computeMinWidth(height);
    }

    @Override
    public double computeMinHeight(double width) {
        return super.computeMinHeight(width);
    }

    @Override
    public double computePrefWidth(double height) {
        return super.computePrefWidth(height);
    }

    @Override
    public double computePrefHeight(double width) {
        return super.computePrefHeight(width);
    }

    @Override
    public double computeMaxWidth(double height) {
        return super.computeMaxWidth(height);
    }

    @Override
    public double computeMaxHeight(double width) {
        return super.computeMaxHeight(width);
    }
}
