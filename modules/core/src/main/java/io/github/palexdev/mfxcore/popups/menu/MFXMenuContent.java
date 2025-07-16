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

package io.github.palexdev.mfxcore.popups.menu;

import java.util.List;
import java.util.function.Supplier;

import io.github.palexdev.mfxcore.controls.Control;
import io.github.palexdev.mfxcore.controls.MFXStyleable;
import io.github.palexdev.mfxcore.controls.SkinBase;

/// This class represents the base and preset type of content for any [MFXMenu]. Extends [Control], and has [MFXMenuContentBehavior]
/// and [MFXMenuContentSkin] as its default behavior and skin implementations. It also implements [MFXStyleable], the
/// default style class to select this from CSS is: `.menu-content` (**Note:** the style class is actually not applied on
/// the control itself but on a container in the default skin. This design choice was made to make CSS styling more straightforward.
/// See [MFXMenuContentSkin]!)
public class MFXMenuContent extends Control<MFXMenuContentBehavior> implements MFXStyleable {
    //================================================================================
    // Properties
    //================================================================================
    private final MFXMenu menu;

    //================================================================================
    // Constructors
    //================================================================================
    public MFXMenuContent(MFXMenu menu) {
        this.menu = menu;
    }

    //================================================================================
    // Overridden Methods
    //================================================================================
    @Override
    public Supplier<MFXMenuContentBehavior> defaultBehaviorProvider() {
        return () -> new MFXMenuContentBehavior(this);
    }

    @Override
    public Supplier<SkinBase<?, ?>> defaultSkinProvider() {
        return () -> new MFXMenuContentSkin(this);
    }

    @Override
    public List<String> defaultStyleClasses() {
        return MFXStyleable.styleClasses("menu-content");
    }

    //================================================================================
    // Getters
    //================================================================================

    /// @return the [MFXMenu] instance to which this content is associated to.
    public MFXMenu getMenu() {
        return menu;
    }
}
