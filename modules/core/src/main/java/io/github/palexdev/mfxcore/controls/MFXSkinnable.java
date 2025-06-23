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

package io.github.palexdev.mfxcore.controls;

import java.util.function.Supplier;

import io.github.palexdev.mfxcore.base.properties.functional.SupplierProperty;

/// Public API for all components that want to integrate with the new Skin API.
///
/// @param <S> the type of skin the component will use
/// @see SkinBase
public interface MFXSkinnable<S extends SkinBase<?, ?>> {

    /// @return a [Supplier] that is the provider for the default skin used by the component.
    Supplier<S> defaultSkinProvider();

    default Supplier<S> getSkinProvider() {
        return skinProviderProperty().get();
    }

    /// Specifies the [Supplier] used to produce a skin object for the component.
    SupplierProperty<S> skinProviderProperty();

    default void setSkinProvider(Supplier<S> provider) {
        skinProviderProperty().set(provider);
    }

    /// Restores the component's skin to the default one using [#defaultSkinProvider()] and [#setSkinProvider(Supplier)].
    default void setDefaultSkinProvider() {
        setSkinProvider(defaultSkinProvider());
    }
}
