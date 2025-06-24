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

import java.util.List;
import java.util.function.Supplier;

import io.github.palexdev.mfxcomponents.skins.MFXButtonSkin;
import io.github.palexdev.mfxcomponents.skins.MFXIconButtonSkin;
import io.github.palexdev.mfxcomponents.variants.ButtonVariants.ShapeVariant;
import io.github.palexdev.mfxcomponents.variants.ButtonVariants.SizeVariant;
import io.github.palexdev.mfxcomponents.variants.ButtonVariants.StyleVariant;
import io.github.palexdev.mfxcomponents.variants.ButtonVariants.WidthVariant;
import io.github.palexdev.mfxcore.base.properties.styleable.StyleableBooleanProperty;
import io.github.palexdev.mfxcore.controls.MFXStyleable;
import io.github.palexdev.mfxcore.controls.SkinBase;
import io.github.palexdev.mfxcore.utils.fx.StyleUtils;
import io.github.palexdev.mfxresources.base.properties.IconProperty;
import io.github.palexdev.mfxresources.fonts.IconDescriptor;
import io.github.palexdev.mfxresources.fonts.MFXFontIcon;
import javafx.css.CssMetaData;
import javafx.css.Styleable;
import javafx.css.StyleablePropertyFactory;

public class MFXIconButton extends MFXButton {
    //================================================================================
    // Properties
    //================================================================================
    private final IconProperty icon = new IconProperty();

    //================================================================================
    // Constructors
    //================================================================================

    public MFXIconButton() {}

    public MFXIconButton(MFXFontIcon icon) {
        setIcon(icon);
    }

    {
        graphicProperty().bind(icon);
    }

    //================================================================================
    // Config
    //================================================================================
    @Override
    public MFXIconButton asToggle() {
        super.asToggle();
        return this;
    }

    @Override
    public MFXIconButton setStyle(StyleVariant style) {
        if (style == StyleVariant.ELEVATED || style == StyleVariant.TEXT) {
            variantsHandler.unsetVariant(StyleVariant.class);
        } else {
            super.setStyle(style);
        }
        return this;
    }

    @Override
    public MFXIconButton setSize(SizeVariant size) {
        super.setSize(size);
        return this;
    }

    @Override
    public MFXIconButton setShape(ShapeVariant shape) {
        super.setShape(shape);
        return this;
    }

    public MFXIconButton setWidth(WidthVariant width) {
        if (width == WidthVariant.DEFAULT) {
            variantsHandler.unsetVariant(WidthVariant.class);
        } else {
            variantsHandler.setVariant(width);
        }
        return this;
    }

    @Override
    public MFXIconButton defaultVariants() {
        variantsHandler.setVariants(SizeVariant.S, ShapeVariant.ROUNDED);
        return this;
    }

    //================================================================================
    // Overridden Methods
    //================================================================================
    @Override
    public Supplier<SkinBase<?, ?>> defaultSkinProvider() {
        return () -> new MFXIconButtonSkin(this);
    }

    @Override
    public List<String> defaultStyleClasses() {
        return MFXStyleable.styleClasses("mfx-icon-button");
    }

    //================================================================================
    // Styleable Properties
    //================================================================================
    private final StyleableBooleanProperty animated = new StyleableBooleanProperty(
        StyleableProperties.ANIMATED,
        this,
        "animated",
        true
    );

    public boolean isAnimated() {
        return animated.get();
    }

    public StyleableBooleanProperty animatedProperty() {
        return animated;
    }

    public void setAnimated(boolean animated) {
        this.animated.set(animated);
    }

    //================================================================================
    // CssMetaData
    //================================================================================
    private static class StyleableProperties {
        private static final StyleablePropertyFactory<MFXIconButton> FACTORY = new StyleablePropertyFactory<>(MFXButtonSkin.getClassCssMetaData());
        private static final List<CssMetaData<? extends Styleable, ?>> cssMetaDataList;

        private static final CssMetaData<MFXIconButton, Boolean> ANIMATED =
            FACTORY.createBooleanCssMetaData(
                "-mfx-animated",
                MFXIconButton::animatedProperty,
                true
            );

        static {
            cssMetaDataList = StyleUtils.cssMetaDataList(
                MFXButton.getClassCssMetaData(),
                ANIMATED
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

    //================================================================================
    // Getters/Setters
    //================================================================================
    public MFXFontIcon getIcon() {
        return icon.get();
    }

    public IconProperty iconProperty() {
        return icon;
    }

    public void setIcon(MFXFontIcon icon) {
        this.icon.set(icon);
    }

    public void setIcon(String code) {
        icon.setDescription(code);
    }

    public void setIcon(IconDescriptor description) {
        icon.setDescription(description);
    }
}
