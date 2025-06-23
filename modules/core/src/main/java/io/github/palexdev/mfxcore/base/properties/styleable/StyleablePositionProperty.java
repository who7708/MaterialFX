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

package io.github.palexdev.mfxcore.base.properties.styleable;

import java.util.Arrays;
import java.util.function.Function;

import io.github.palexdev.mfxcore.base.beans.Position;
import javafx.css.*;
import javafx.scene.text.Font;

/// Convenience [StyleableObjectProperty] for [Position], settable via CSS thanks to [StyleablePositionProperty.PositionConverter].
// TODO add support for units
public class StyleablePositionProperty extends StyleableObjectProperty<Position> {

    //================================================================================
    // Constructors
    //================================================================================
    public StyleablePositionProperty(CssMetaData<? extends Styleable, Position> cssMetaData) {
        super(cssMetaData);
    }

    public StyleablePositionProperty(CssMetaData<? extends Styleable, Position> cssMetaData, Position initialValue) {
        super(cssMetaData, initialValue);
    }

    public StyleablePositionProperty(CssMetaData<? extends Styleable, Position> cssMetaData, Object bean, String name) {
        super(cssMetaData, bean, name);
    }

    public StyleablePositionProperty(CssMetaData<? extends Styleable, Position> cssMetaData, Object bean, String name, Position initialValue) {
        super(cssMetaData, bean, name, initialValue);
    }

    //================================================================================
    // Methods
    //================================================================================
    public void setPosition(double x, double y) {
        set(Position.of(x, y));
    }

    @Override
    public void applyStyle(StyleOrigin origin, Position v) {
        if (v == null) return;
        super.applyStyle(origin, v);
    }

    public static <S extends Styleable> CssMetaData<S, Position> metaDataFor(
        String propId, Function<S, StyleablePositionProperty> property, Position initialValue
    ) {
        return new CssMetaData<>(propId, PositionConverter.getInstance(), initialValue) {
            @Override
            public boolean isSettable(S styleable) {
                return !property.apply(styleable).isBound();
            }

            @Override
            public StyleableProperty<Position> getStyleableProperty(S styleable) {
                return property.apply(styleable);
            }
        };
    }

    //================================================================================
    // Internal Classes
    //================================================================================

    /// Style converter implementation to make [Position] settable via CSS.
    /// The related property is [StyleablePositionProperty].
    ///
    /// For this to properly work, you must use a specific format. The converter expects a string value,
    /// with two double numbers which will be in order the x and the y for the new `Position`, so:
    /// `.node{-fx-property-name: "100 30";}`
    public static class PositionConverter extends StyleConverter<String, Position> {

        // lazy, thread-safe instantiation
        private static class Holder {
            static final PositionConverter INSTANCE = new PositionConverter();
        }

        /// Gets the `SizeConverter` instance.
        ///
        /// @return the `SizeConverter` instance
        public static StyleConverter<String, Position> getInstance() {
            return Holder.INSTANCE;
        }

        private PositionConverter() {
            super();
        }

        @Override
        public Position convert(ParsedValue<String, Position> value, Font font) {
            try {
                double[] sizes = Arrays.stream(value.getValue().split(" "))
                    .mapToDouble(Double::parseDouble)
                    .toArray();
                return Position.of(sizes[0], sizes[1]);
            } catch (Exception ex) {
                System.out.println(ex.getMessage());
                return null;
            }
        }

        @Override
        public String toString() {
            return "PositionConverter";
        }
    }
}
