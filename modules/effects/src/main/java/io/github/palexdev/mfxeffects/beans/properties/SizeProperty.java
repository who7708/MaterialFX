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

package io.github.palexdev.mfxeffects.beans.properties;

import java.util.Optional;

import io.github.palexdev.mfxeffects.beans.Size;
import javafx.beans.property.ReadOnlyObjectWrapper;

/// Simple extension of [ReadOnlyObjectWrapper] for [Size] objects.
public class SizeProperty extends ReadOnlyObjectWrapper<Size> {

    //================================================================================
    // Constructors
    //================================================================================
    public SizeProperty() {}

    public SizeProperty(Size initialValue) {
        super(initialValue);
    }

    public SizeProperty(Object bean, String name) {
        super(bean, name);
    }

    public SizeProperty(Object bean, String name, Size initialValue) {
        super(bean, name, initialValue);
    }

    //================================================================================
    // Methods
    //================================================================================

    /// Convenience method to create a new [Size] object with the given parameters and set it
    /// as the new value of this property.
    public void setSize(double w, double h) {
        set(Size.of(w, h));
    }

    /// Convenience method to set only the width using [Size#withWidth(double)]
    public void setWidth(double w) {
        set(
            Optional.ofNullable(get())
                .map(s -> s.withWidth(w))
                .orElseGet(() -> Size.of(w, 0.0))
        );
    }

    /// Convenience method to set only the height using [Size#withHeight(double)].
    public void setHeight(double h) {
        set(
            Optional.ofNullable(get())
                .map(s -> s.withHeight(h))
                .orElseGet(() -> Size.of(0.0, h))
        );
    }

    /// Null-safe alternative to `get().width()`, if the value is `null` returns an invalid width of 0.0.
    public double getWidth() {
        return Optional.ofNullable(get())
            .map(Size::width)
            .orElse(0.0);
    }

    /// Null-safe alternative to `get().height()`, if the value is `null` returns an invalid height of 0.0.
    public double getHeight() {
        return Optional.ofNullable(get())
            .map(Size::height)
            .orElse(0.0);
    }
}
