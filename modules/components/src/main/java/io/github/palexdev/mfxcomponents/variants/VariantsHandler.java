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

import java.util.*;

import javafx.css.Styleable;

/// Utility class that handles variants for those components that feature them. Favors composition over inheritance
/// and allows hiding the API from the user if the component needs a specific combination of variants.
public class VariantsHandler<S extends Styleable, V extends Variant> {
    //================================================================================
    // Properties
    //================================================================================
    private final S styleable;
    private final SequencedSet<V> variants;

    //================================================================================
    // Constructors
    //================================================================================
    public VariantsHandler(S styleable) {
        this.styleable = styleable;
        this.variants = new LinkedHashSet<>();
    }

    //================================================================================
    // Methods
    //================================================================================

    /// Adds all the given variants to the component and automatically handles duplicates (ignored).
    @SafeVarargs
    public final void addVariants(V... variants) {
        styleable.getStyleClass().addAll(
            Arrays.stream(variants)
                .filter(this.variants::add)
                .map(Variant::variantStyleClass)
                .toArray(String[]::new)
        );
    }

    /// Removes any previously applied variants and adds the new given ones.
    ///
    /// To make the operation appear "atomic" to JavaFX, the removal and addition
    /// are performed on a copy of the style class list.
    @SafeVarargs
    public final void setVariants(V... variants) {
        // Remove previous
        List<String> copy = new ArrayList<>(styleable.getStyleClass());
        this.variants.forEach(v -> copy.remove(v.variantStyleClass()));
        this.variants.clear();

        // Add new
        for (V v : variants) {
            copy.add(v.variantStyleClass());
        }
        styleable.getStyleClass().setAll(copy);
    }

    /// Removes all the given variants from the component.
    @SafeVarargs
    public final void removeVariants(V... variants) {
        styleable.getStyleClass().removeAll(
            Arrays.stream(variants)
                .filter(this.variants::remove)
                .map(Variant::variantStyleClass)
                .toArray(String[]::new)
        );
    }

    /// Removes all the variants currently applied to the component.
    public final void clearVariants() {
        styleable.getStyleClass().removeAll(
            variants.stream()
                .map(Variant::variantStyleClass)
                .toArray(String[]::new)
        );
    }

    /// @return the set of variants currently applied to the component.
    public SequencedSet<V> getAppliedVariants() {
        return variants;

    }

    /// @return whether the given variant is currently applied to the component.
    public boolean isVariantApplied(V variant) {
        return variants.contains(variant);
    }
}
