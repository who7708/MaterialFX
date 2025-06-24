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

package io.github.palexdev.mfxeffects.beans.properties.styleable;

import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.github.palexdev.mfxeffects.beans.Size;
import javafx.css.*;
import javafx.scene.text.Font;

/// Convenience [StyleableObjectProperty] for [Size], settable via CSS thanks to the [SizeConverter].
public class StyleableSizeProperty extends SimpleStyleableObjectProperty<Size> {

    //================================================================================
    // Constructors
    //================================================================================
    public StyleableSizeProperty(CssMetaData<? extends Styleable, Size> cssMetaData) {
        super(cssMetaData);
    }

    public StyleableSizeProperty(CssMetaData<? extends Styleable, Size> cssMetaData, Size initialValue) {
        super(cssMetaData, initialValue);
    }

    public StyleableSizeProperty(CssMetaData<? extends Styleable, Size> cssMetaData, Object bean, String name) {
        super(cssMetaData, bean, name);
    }

    public StyleableSizeProperty(CssMetaData<? extends Styleable, Size> cssMetaData, Object bean, String name, Size initialValue) {
        super(cssMetaData, bean, name, initialValue);
    }

    //================================================================================
    // Methods
    //================================================================================
    public void setSize(double width, double height) {
        set(Size.of(width, height));
    }

    @Override
    public void applyStyle(StyleOrigin origin, Size v) {
        if (v == null) return;
        super.applyStyle(origin, v);
    }

    public static <S extends Styleable> CssMetaData<S, Size> metaDataFor(
        String propId, Function<S, StyleableSizeProperty> property, Size initialValue
    ) {
        return new CssMetaData<>(propId, SizeConverter.getInstance(), initialValue) {
            @Override
            public boolean isSettable(S styleable) {
                return !property.apply(styleable).isBound();
            }

            @Override
            public StyleableProperty<Size> getStyleableProperty(S styleable) {
                return property.apply(styleable);
            }
        };
    }

    //================================================================================
    // Overridden Methods
    //================================================================================
    @Override
    public StyleOrigin getStyleOrigin() {
        return StyleOrigin.USER_AGENT;
    }

    //================================================================================
    // Internal Classes
    //================================================================================

    /// Style converter implementation to make [Size] settable via CSS.
    /// The related property is [StyleableSizeProperty].
    ///
    /// For this to properly work, you must use a specific format. The converter expects a string value,
    /// with two double numbers which will be in order the width and the height for the new `Size`, so:
    /// `.node{-fx-property-name: "100 30";}`
    public static class SizeConverter extends StyleConverter<String, Size> {

        // lazy, thread-safe instantiation
        private static class Holder {
            static final SizeConverter INSTANCE = new SizeConverter();
        }

        /// Gets the `SizeConverter` instance.
        ///
        /// @return the `SizeConverter` instance
        public static StyleConverter<String, Size> getInstance() {
            return Holder.INSTANCE;
        }

        private SizeConverter() {
            super();
        }

        @Override
        public Size convert(ParsedValue<String, Size> value, Font font) {
            String sVal = value.getValue();
            if (sVal == null || sVal.isBlank()) return Size.zero();

            String[] sVals = sVal.split(" ");
            double[] vals = convertSize(sVals);
            return Size.of(vals[0], vals[1]);
        }

        private double[] convertSize(String[] sVals) {
            double[] sizes = new double[]{0, 0};
            Pattern pattern = Pattern.compile("([+-]?\\d*\\.?\\d+)([a-zA-Z%]+)");
            for (int i = 0; i < Math.min(2, sVals.length); i++) {
                Matcher matcher = pattern.matcher(sVals[i].trim());
                if (!matcher.matches())
                    throw new IllegalArgumentException("Invalid size format: " + sVals[i]);

                double val = Double.parseDouble(matcher.group(1));
                SizeUnits units = SizeUnits.valueOf(matcher.group(2).toUpperCase());
                sizes[i] = new javafx.css.Size(val, units).pixels();
            }
            return sizes;
        }
    }
}
