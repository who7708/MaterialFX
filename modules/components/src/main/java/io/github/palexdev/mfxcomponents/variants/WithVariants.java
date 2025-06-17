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

package io.github.palexdev.mfxcomponents.variants;


import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

/// A simple interface indicating that a component supports variants.
///
/// They are handled internally by the [VariantsHandler] class.
public interface WithVariants {

    /// @return a [Set] containing all the applied variants
    Map<Class<?>, Variant> getAppliedVariants();

    /// @return whether the given variant is contained in [#getAppliedVariants()]
    default boolean isVariantApplied(Variant variant) {
        return Optional.ofNullable(getAppliedVariants().get(variant.getClass()))
            .map(v -> Objects.equals(v, variant))
            .orElse(false);
    }
}
