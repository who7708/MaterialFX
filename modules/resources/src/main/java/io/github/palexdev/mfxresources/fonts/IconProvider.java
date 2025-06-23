/*
 * Copyright (C) 2023 Parisi Alessandro - alessandro.parisi406@gmail.com
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

package io.github.palexdev.mfxresources.fonts;

import java.io.InputStream;
import java.util.function.Function;

import javafx.scene.text.Font;

/// Public API to implement an `IconProvider`. These contain important information about an icon font.
/// In particular, they should expose:
///  - The path to the font
///  - A converter function that maps an icon description/name to its Unicode character, see [IconDescriptor]
///  - A way to load the icon font as an [InputStream]
public interface IconProvider {
    /// @return the path to the icon font resource
    String getFontPath();

    /// @return the function that will be used to convert the icon description/name to its corresponding Unicode character
    Function<String, Character> getConverter();

    /// This should load the font resource as an [InputStream].
    InputStream load();

    /// @return the result of [#load()] as a [Font] using [Font#loadFont(String, double)],
    /// the default used size is 16.0
    default Font loadFont() {
        return loadFont(16.0);
    }

    /// @return the result of [#load()] as a [Font] using [Font#loadFont(String, double)]
    /// with the given size
    default Font loadFont(double size) {
        return Font.loadFont(load(), size);
    }
}
