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

package io.github.palexdev.mfxcore.controls;

import io.github.palexdev.mfxcore.base.properties.functional.SupplierProperty;
import io.github.palexdev.mfxcore.behavior.BehaviorBase;
import io.github.palexdev.mfxcore.behavior.WithBehavior;
import javafx.scene.Node;
import javafx.scene.control.Skin;

/// Base class that can be used as a starting point to implement text-based UI components that perfectly integrate with the
/// new Behavior and Skin APIs, see [WithBehavior] and [MFXSkinnable].
///
/// The integration with the new Behavior API is achieved by having a specific property, [#behaviorProviderProperty()],
/// which allows changing at any time the component's behavior. The property automatically handles initialization and disposal
/// of behaviors. A reference to the current built behavior object is kept to be retrieved via [#getBehavior()].
///
///
/// Enforces the use of [SkinBase] instances as Skin implementations and makes the [#createDefaultSkin()] method final,
/// thus denying users to override it. Similar to the behavior, to set custom skins, you can:
///  - Use the provider property, [#skinProviderProperty()]
///  - Override [#buildSkin()] **(not recommended)**
///  - Call [#setSkin(Skin)] directly **(absolutely not recommended)**
///
/// The skin provider is more of a convenience to the user that does not need to inline-override the method responsible for
/// creating the skin. The new mechanism is much more flexible and automatically integrates with the behavior API.
///
/// As a consequence, components that inherit from this do not support the "-fx-skin" CSS property. You'll have to do it in code.
///
/// @param <B> the behavior type used by the component
@SuppressWarnings({"rawtypes", "unchecked"})
public abstract class Labeled<B extends BehaviorBase<? extends Node>>
    extends javafx.scene.control.Labeled implements WithBehavior<B>, MFXSkinnable<SkinBase<?, ?>> {
    //================================================================================
    // Properties
    //================================================================================
    private B behavior;
    private final SupplierProperty<B> behaviorProvider = new SupplierProperty<>() {
        @Override
        protected void invalidated() {
            if (behavior != null) {
                behavior.dispose();
            }
            behavior = get().get();
            SkinBase skin = (SkinBase) getSkin();
            if (skin != null && behavior != null) skin.initBehavior(behavior);
        }
    };
    private final SupplierProperty<SkinBase<?, ?>> skinProvider = new SupplierProperty<>() {
        @Override
        protected void invalidated() {
            // Do not run if createDefaultSkin() has not been called yet.
            // The downside of this is that if setSkin(...) is called before, then the provider is ignored
            if (getSkin() != null)
                setSkin(buildSkin());

        }
    };

    //================================================================================
    // Constructors
    //================================================================================
    public Labeled() {}

    public Labeled(String text) {
        super(text);
    }

    public Labeled(String text, Node graphic) {
        super(text, graphic);
    }

    {
        setDefaultBehaviorProvider();
        setDefaultSkinProvider();
    }

    //================================================================================
    // Methods
    //================================================================================

    /// This is the core method responsible for creating the component's skin when the [#skinProviderProperty()]
    /// changes. Does not allow `null` skins and automatically call [SkinBase#initBehavior(BehaviorBase)]
    /// with the current behavior.
    ///
    /// Note that the very first skin instance is created by JavaFX with the usual [#createDefaultSkin()].
    protected SkinBase<?, ?> buildSkin() {
        SkinBase skin = getSkinProvider().get();
        if (skin == null)
            throw new IllegalArgumentException("The new skin cannot be null!");
        skin.initBehavior(behavior);
        return skin;
    }

    //================================================================================
    // Overridden Methods
    //================================================================================

    /// {@inheritDoc}
    ///
    ///
    /// Overridden to be final and to delegate to [#buildSkin()]. We still need this to initialize the component.
    @Override
    protected final SkinBase<?, ?> createDefaultSkin() {
        return buildSkin();
    }

    //================================================================================
    // Getters/Setters
    //================================================================================
    @Override
    public B getBehavior() {
        return behavior;
    }

    @Override
    public SupplierProperty<B> behaviorProviderProperty() {
        return behaviorProvider;
    }

    @Override
    public SupplierProperty<SkinBase<?, ?>> skinProviderProperty() {
        return skinProvider;
    }
}
