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
import java.util.function.Supplier;

import io.github.palexdev.mfxcomponents.controls.base.MFXChoice;
import io.github.palexdev.mfxcomponents.skins.MFXSplitButtonSkin;
import io.github.palexdev.mfxcomponents.variants.ButtonVariants.SizeVariant;
import io.github.palexdev.mfxcomponents.variants.ButtonVariants.StyleVariant;
import io.github.palexdev.mfxcomponents.variants.api.Variant;
import io.github.palexdev.mfxcomponents.variants.api.VariantsHandler;
import io.github.palexdev.mfxcomponents.variants.api.WithVariants;
import io.github.palexdev.mfxcore.controls.MFXStyleable;
import io.github.palexdev.mfxcore.controls.SkinBase;
import javafx.collections.ObservableMap;

/// Implementation of Material Design's 'Split Buttons'. Extends [MFXChoice], uses [MFXSplitButtonSkin] as its default skin
/// and a generic behavior. The default CSS style class is `.mfx-split-button`.
///
/// As stated by the Material Design specs, split buttons offer a way to give the user more options related to an action
/// through a menu. It helps reduce visual complexity by hiding extra options.
///
/// Essentially, it is the combination of two standard buttons with a little gap in between.
/// The left button displays the current choice, while the right button manages the popup.<br >
/// So, it inherits most of the styles/variants from [MFXButton]:
/// - there are five size presets expressed through [SizeVariant]
/// - there are four style presets expressed through [StyleVariant]. The text variant is not allowed and will default to
/// [StyleVariant#ELEVATED].
///
/// The defaults are applied by [#defaultVariants()] upon initialization.
public class MFXSplitButton<T> extends MFXChoice<T> implements WithVariants {
    //================================================================================
    // Properties
    //================================================================================
    private final VariantsHandler<MFXSplitButton<T>> variantsHandler = new VariantsHandler<>(this);

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
    protected void updateOnActionHandler() {
        // The action is managed by the leading button here
    }

    @Override
    public Supplier<SkinBase<?, ?>> defaultSkinProvider() {
        return () -> new MFXSplitButtonSkin<>(this);
    }

    @Override
    public List<String> defaultStyleClasses() {
        return MFXStyleable.styleClasses("mfx-split-button");
    }

    @Override
    public ObservableMap<Class<?>, Variant> getAppliedVariants() {
        return variantsHandler.getAppliedVariantsUnmodifiable();
    }
}
