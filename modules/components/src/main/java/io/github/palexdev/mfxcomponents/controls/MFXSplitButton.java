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

package io.github.palexdev.mfxcomponents.controls;

import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

import io.github.palexdev.mfxcomponents.controls.base.MFXChoice;
import io.github.palexdev.mfxcomponents.skins.MFXSplitButtonSkin;
import io.github.palexdev.mfxcomponents.variants.ButtonVariants.SizeVariant;
import io.github.palexdev.mfxcomponents.variants.ButtonVariants.StyleVariant;
import io.github.palexdev.mfxcomponents.variants.api.Variant;
import io.github.palexdev.mfxcomponents.variants.api.VariantsHandler;
import io.github.palexdev.mfxcomponents.variants.api.WithVariants;
import io.github.palexdev.mfxcore.base.properties.functional.ConsumerProperty;
import io.github.palexdev.mfxcore.controls.MFXSkinBase;
import javafx.collections.ObservableMap;
import javafx.scene.Node;

import static io.github.palexdev.mfxcore.controls.MFXStyleable.styleClasses;

public class MFXSplitButton<T> extends MFXChoice<T> implements WithVariants {
    //================================================================================
    // Properties
    //================================================================================
    private final VariantsHandler<MFXSplitButton<T>> variantsHandler = new VariantsHandler<>(this);
    private final ConsumerProperty<T> onAction = new ConsumerProperty<>(_ -> {}) {
        @Override
        public void set(Consumer<T> newValue) {
            if (newValue == null) newValue = _ -> {};
            super.set(newValue);
        }
    };

    //================================================================================
    // Constructors
    //================================================================================

    public MFXSplitButton() {}

    @SafeVarargs
    public MFXSplitButton(T... items) {
        super(items);
    }

    public MFXSplitButton(Collection<T> items) {
        super(items);
    }

    {
        defaultVariants();
    }

    //================================================================================
    // Config
    //================================================================================

    /// [StyleVariant#TEXT] is not supported and will default to [StyleVariant#ELEVATED].
    public MFXSplitButton<T> setStyle(StyleVariant style) {
        if (style == StyleVariant.TEXT) style = StyleVariant.ELEVATED;
        variantsHandler.setVariant(style);
        return this;
    }

    public MFXSplitButton<T> setSize(SizeVariant size) {
        variantsHandler.setVariant(size);
        return this;
    }

    /// Applies the default variants to the button:
    /// - [StyleVariant#ELEVATED]
    /// - [SizeVariant#S]
    public MFXSplitButton<T> defaultVariants() {
        variantsHandler.setVariants(StyleVariant.ELEVATED, SizeVariant.S);
        return this;
    }

    //================================================================================
    // Overridden Methods
    //================================================================================

    @Override
    public Supplier<MFXSkinBase<? extends Node>> defaultSkinFactory() {
        return () -> new MFXSplitButtonSkin<>(this);
    }

    @Override
    public List<String> defaultStyleClasses() {
        return styleClasses("mfx-split-button");
    }

    @Override
    public ObservableMap<Class<?>, Variant> getAppliedVariants() {
        return variantsHandler.getAppliedVariantsUnmodifiable();
    }

    //================================================================================
    // Getters/Setters
    //================================================================================

    public Consumer<T> getOnAction() {
        return onAction.get();
    }

    /// Specifies the action to perform when the selected choice is executed.
    public ConsumerProperty<T> onActionProperty() {
        return onAction;
    }

    public void setOnAction(Consumer<T> onAction) {
        this.onAction.set(onAction);
    }
}
