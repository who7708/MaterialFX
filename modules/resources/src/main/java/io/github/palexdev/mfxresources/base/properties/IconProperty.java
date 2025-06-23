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

package io.github.palexdev.mfxresources.base.properties;

import io.github.palexdev.mfxresources.fonts.IconDescriptor;
import io.github.palexdev.mfxresources.fonts.MFXFontIcon;
import javafx.beans.property.ReadOnlyObjectWrapper;

/// Simple extension of [ReadOnlyObjectWrapper] to be used for [MFXFontIcon] objects,
/// also offers a series of convenient methods to manipulate the icon with fluent API.
public class IconProperty extends ReadOnlyObjectWrapper<MFXFontIcon> {

    //================================================================================
    // Constructors
    //================================================================================
    public IconProperty() {
    }

    public IconProperty(MFXFontIcon initialValue) {
        super(initialValue);
    }

    public IconProperty(Object bean, String name) {
        super(bean, name);
    }

    public IconProperty(Object bean, String name, MFXFontIcon initialValue) {
        super(bean, name, initialValue);
    }

    //================================================================================
    // Setters
    //================================================================================

    /// Creates and sets a new [MFXFontIcon] with the given icon description.
    public IconProperty setDescription(String description) {
        set(new MFXFontIcon(description));
        return this;
    }

    /// Creates and sets a new [MFXFontIcon] with the given icon description.
    public IconProperty setDescription(IconDescriptor description) {
        set(new MFXFontIcon(description));
        return this;
    }
}