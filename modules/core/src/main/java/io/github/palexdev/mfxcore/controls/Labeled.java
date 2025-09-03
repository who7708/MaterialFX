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

import io.github.palexdev.mfxcore.base.properties.functional.SupplierProperty;
import io.github.palexdev.mfxcore.behavior.BehaviorBase;
import io.github.palexdev.mfxcore.behavior.WithBehavior;
import javafx.scene.Node;
import javafx.scene.control.Skin;

/// Base class that can be used as a starting point to implement text-based UI components that perfectly integrate with the
/// new Behavior and Skin APIs, see [WithBehavior] and [MFXSkinnable].
///
/// The integration with the new Behavior API is achieved by having a specific property, [#behaviorFactoryProperty()],
/// which allows changing at any time the component's behavior. The property automatically handles initialization and disposal
/// of behaviors. A reference to the current built behavior object is kept to be retrieved via [#getBehavior()].
///
///
/// Enforces the use of [SkinBase] instances as Skin implementations and makes the [#createDefaultSkin()] method final,
/// thus denying users to override it. Similar to the behavior, to set custom skins, you can:
///  - Use the factory property, [#skinFactoryProperty()]
///  - Override [#buildSkin()] **(not recommended)**
///  - Call [#setSkin(Skin)] directly **(absolutely not recommended)**
///
/// The skin factory is more of a convenience to the user that does not need to inline-override the method responsible for
/// creating the skin. The new mechanism is much more flexible and automatically integrates with the behavior API.<br >
/// As a consequence, components that inherit from this do not support the "-fx-skin" CSS property. You'll have to do it in code.
public abstract class Labeled extends javafx.scene.control.Labeled implements WithBehavior, MFXSkinnable {
    //================================================================================
    // Properties
    //================================================================================
    private BehaviorBase<? extends Node> behavior;
    private final SupplierProperty<BehaviorBase<? extends Node>> behaviorFactory = new SupplierProperty<>() {
        @Override
        protected void invalidated() {
            if (behavior != null) behavior.dispose();
            behavior = get().get();
            SkinBase<?> skin = (SkinBase<?>) getSkin();
            if (skin != null && behavior != null) skin.registerBehavior();
        }
    };
    private final SupplierProperty<SkinBase<? extends Control>> skinFactory = new SupplierProperty<>() {
        @Override
        protected void invalidated() {
            // Do not run if createDefaultSkin() has not been called yet.
            // The downside of this is that if setSkin(...) is called before, then the factory is ignored
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
        setDefaultBehaviorFactory();
        setDefaultSkinFactory();
    }

    //================================================================================
    // Methods
    //================================================================================

    /// This is the core method responsible for creating the component's skin when the [#skinFactoryProperty()]
    /// changes. Does not allow `null` skins and automatically call [SkinBase#registerBehavior(BehaviorBase)]
    /// with the current behavior.
    ///
    /// Note that the very first skin instance is created by JavaFX with the usual [#createDefaultSkin()].
    protected SkinBase<?> buildSkin() {
        SkinBase<?> skin = getSkinFactory().get();
        if (skin == null)
            throw new IllegalArgumentException("The new skin cannot be null!");
        skin.registerBehavior();
        return skin;
    }

    //================================================================================
    // Overridden Methods
    //================================================================================

    /// {@inheritDoc}
    ///
    /// Overridden to also initialize the behavior on the preloaded skin!
    @Override
    public void preloadSkin() {
        MFXSkinnable.super.preloadSkin();
        if (getSkin() instanceof SkinBase<?> sb)
            sb.registerBehavior();
    }

    /// {@inheritDoc}
    ///
    ///
    /// Overridden to be final and to delegate to [#buildSkin()]. We still need this to initialize the component.
    @Override
    protected final SkinBase<?> createDefaultSkin() {
        return buildSkin();
    }

    //================================================================================
    // Getters/Setters
    //================================================================================
    @Override
    public BehaviorBase<? extends Node> getBehavior() {
        return behavior;
    }

    @Override
    public SupplierProperty<BehaviorBase<? extends Node>> behaviorFactoryProperty() {
        return behaviorFactory;
    }

    @Override
    public SupplierProperty<SkinBase<? extends Control>> skinFactoryProperty() {
        return skinFactory;
    }
}
