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

package io.github.palexdev.mfxresources.fonts;

import java.util.Map;

/// Public API used to describe a [Font Icon](https://fonts.google.com/knowledge/glossary/icon_font).
///
/// A font icon is identified by: a unique name, which is usually prefixed by a string representing the vendor of the font
/// and/or the class; and it's corresponding Unicode character which is also unique.
public interface IconDescriptor {

    /// @return the name of an icon inside the font resource
    String getDescription();

    /// @return the code of the icon inside the font resource
    char getCode();

    /// @return the descriptor associated with the given description or `null` if none is found
    default IconDescriptor findByDescription(String description) {
        return null;
    }

    /// Optionally, a class(especially enums) implementing `IconDescriptor` can choose to offer
    /// a `Map` which holds its icons as `[description -> code]`.
    ///
    /// This makes the search of an icon by its description much faster.
    default Map<String, Character> getCache() {
        return Map.of();
    }
}
