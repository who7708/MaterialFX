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

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import io.github.palexdev.mfxcomponents.behaviors.MFXButtonBehavior;
import io.github.palexdev.mfxcomponents.controls.base.MFXSelectable;
import io.github.palexdev.mfxcomponents.skins.MFXButtonSkin;
import io.github.palexdev.mfxcomponents.variants.Variant;
import io.github.palexdev.mfxcomponents.variants.VariantsHandler;
import io.github.palexdev.mfxcomponents.variants.WithVariants;
import io.github.palexdev.mfxcomponents.variants.button.ShapeVariant;
import io.github.palexdev.mfxcomponents.variants.button.SizeVariant;
import io.github.palexdev.mfxcomponents.variants.button.StyleVariant;
import io.github.palexdev.mfxcore.base.properties.styleable.StyleableBooleanProperty;
import io.github.palexdev.mfxcore.controls.MFXStyleable;
import io.github.palexdev.mfxcore.controls.SkinBase;
import io.github.palexdev.mfxcore.selection.SelectionGroup;
import io.github.palexdev.mfxcore.utils.fx.StyleUtils;
import javafx.css.CssMetaData;
import javafx.css.Styleable;
import javafx.css.StyleablePropertyFactory;
import javafx.scene.Node;

/// Custom implementation of a button which extends [MFXSelectable], uses [MFXButtonSkin] and [MFXButtonBehavior] as its
/// default skin and behavior. The default CSS style class is `.mfx-button`.
///
/// Material 3 specs show a vast number of configurations for standard buttons. Most of them determine the look of the
/// component and are implemented through the [WithVariants] API. These are the available configs:
/// - [StyleVariant] defines the colors, can be set through [#setStyle(StyleVariant)]
/// - [SizeVariant] defines preset sizes, can be set through [#setSize(SizeVariant)]
/// - [ShapeVariant] defines the shape/radius, can be set through [#setShape(ShapeVariant)]
/// The defaults are applied by [#defaultVariants()] upon initialization.
///
/// The most important, tricky and headache-inducing feature is the [#toggleableProperty()].
/// The newest Material 3 Expressive update unified toggles and regular buttons under the same component. To avoid code
/// duplication as much as possible, I also decided to unify them. The downside is that this makes the [#selectedProperty()]'s
/// handling complex, risky. For example, what happens if the button is not toggleable, but it's inside a [SelectionGroup]?
///
/// After much internal debate, I decided to keep things simple and stupid. The [#toggleableProperty()] is a mere indicator
/// to the behavior class, [MFXButtonBehavior], which can redirect the user input to the selection handling logic
/// or just trigger an action. (see [MFXButtonBehavior#handleSelection()])
///
/// If the button is selected and is not in toggle mode, it will be turned on by [#onSelectionChanged(boolean)].
// TODO add support for emphasized text through Variants
public class MFXButton extends MFXSelectable<MFXButtonBehavior> implements WithVariants {
    //================================================================================
    // Properties
    //================================================================================
    protected final VariantsHandler<MFXButton> variantsHandler = new VariantsHandler<>(this);

    //================================================================================
    // Constructors
    //================================================================================
    public MFXButton() {}

    public MFXButton(String text) {
        super(text);
    }

    public MFXButton(String text, Node graphic) {
        super(text, graphic);
    }

    {
        defaultVariants();
        setMinSize(USE_PREF_SIZE, USE_PREF_SIZE);
        setPickOnBounds(false);
    }

    //================================================================================
    // Config
    //================================================================================

    /// Shortcut for `setToggleable(true)`.
    ///
    /// @see #toggleableProperty()
    public MFXButton asToggle() {
        setToggleable(true);
        return this;
    }

    public MFXButton setStyle(StyleVariant style) {
        variantsHandler.setVariant(style);
        return this;
    }

    public MFXButton setSize(SizeVariant size) {
        variantsHandler.setVariant(size);
        return this;
    }

    public MFXButton setShape(ShapeVariant shape) {
        variantsHandler.setVariant(shape);
        return this;
    }

    /// Applies the default variants to the button:
    /// - [StyleVariant#ELEVATED]
    /// - [SizeVariant#S]
    /// - [ShapeVariant#ROUNDED]
    public MFXButton defaultVariants() {
        variantsHandler.setVariants(StyleVariant.ELEVATED, SizeVariant.S, ShapeVariant.ROUNDED);
        return this;
    }

    //================================================================================
    // Overridden Methods
    //================================================================================

    /// {@inheritDoc}
    ///
    /// Overridden to activate the the [#toggleableProperty()] if the given state is `true`.
    @Override
    protected void onSelectionChanged(boolean state) {
        if (state && !isToggleable()) setToggleable(true);
        super.onSelectionChanged(state);
    }

    @Override
    protected SkinBase<?, ?> buildSkin() {
        return new MFXButtonSkin<>(this);
    }

    @Override
    public Supplier<MFXButtonBehavior> defaultBehaviorProvider() {
        return () -> new MFXButtonBehavior(this);
    }

    @Override
    public List<String> defaultStyleClasses() {
        return MFXStyleable.styleClasses("mfx-button");
    }

    @Override
    public Map<Class<?>, Variant> getAppliedVariants() {
        return Collections.unmodifiableMap(variantsHandler.getAppliedVariants());
    }

    //================================================================================
    // Styleable Properties
    //================================================================================
    private final StyleableBooleanProperty toggleable = new StyleableBooleanProperty(
        StyleableProperties.TOGGLEABLE,
        this,
        "toggleable",
        false
    ) {
        @Override
        protected void invalidated() {
            boolean val = get();
            if (val) {
                getStyleClass().add("toggle");
            } else {
                getStyleClass().remove("toggle");
            }
        }
    };

    public boolean isToggleable() {
        return toggleable.get();
    }

    /// Specifies the button's behavior, either as a regular button or as a toggle.
    /// Applies/removes the `.toggle` CSS style class to the button as needed.
    ///
    /// Can be set in CSS via the property: '-mfx-toggleable'.
    ///
    /// #### Note!
    ///
    /// This property is more of an indicator to the behavior class, [MFXButtonBehavior].
    public StyleableBooleanProperty toggleableProperty() {
        return toggleable;
    }

    public void setToggleable(boolean toggleable) {
        this.toggleable.set(toggleable);
    }

    //================================================================================
    // CssMetaData
    //================================================================================
    private static class StyleableProperties {
        private static final StyleablePropertyFactory<MFXButton> FACTORY = new StyleablePropertyFactory<>(MFXSelectable.getClassCssMetaData());
        private static final List<CssMetaData<? extends Styleable, ?>> cssMetaDataList;

        private static final CssMetaData<MFXButton, Boolean> TOGGLEABLE =
            FACTORY.createBooleanCssMetaData(
                "-mfx-toggleable",
                MFXButton::toggleableProperty,
                false
            );

        static {
            cssMetaDataList = StyleUtils.cssMetaDataList(
                MFXSelectable.getClassCssMetaData(),
                TOGGLEABLE
            );
        }
    }

    public static List<CssMetaData<? extends Styleable, ?>> getClassCssMetaData() {
        return StyleableProperties.cssMetaDataList;
    }

    @Override
    public List<CssMetaData<? extends Styleable, ?>> getControlCssMetaData() {
        return getClassCssMetaData();
    }
}
