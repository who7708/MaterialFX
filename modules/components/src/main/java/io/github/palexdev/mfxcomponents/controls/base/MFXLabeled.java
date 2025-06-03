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

package io.github.palexdev.mfxcomponents.controls.base;

import java.util.List;

import io.github.palexdev.mfxcomponents.skins.base.MFXLabeledSkin;
import io.github.palexdev.mfxcore.base.properties.styleable.StyleableDoubleProperty;
import io.github.palexdev.mfxcore.behavior.BehaviorBase;
import io.github.palexdev.mfxcore.controls.Labeled;
import io.github.palexdev.mfxcore.controls.Styleable;
import io.github.palexdev.mfxcore.utils.fx.StyleUtils;
import javafx.css.CssMetaData;
import javafx.css.StyleablePropertyFactory;
import javafx.scene.Node;

public abstract class MFXLabeled<B extends BehaviorBase<? extends Node>> extends Labeled<B> implements Styleable {

    //================================================================================
    // Constructors
    //================================================================================
    public MFXLabeled() {}

    public MFXLabeled(String text) {
        super(text);
    }

    public MFXLabeled(String text, Node graphic) {
        super(text, graphic);
    }

    //================================================================================
    // Overridden Methods
    //================================================================================
    @Override
    protected void init() {
        defaultStyleClasses(this);
        super.init();
    }

    @Override
    public double computeMinWidth(double height) {
        return super.computeMinWidth(height);
    }

    @Override
    public double computeMinHeight(double width) {
        return super.computeMinHeight(width);
    }

    @Override
    public double computePrefWidth(double height) {
        return super.computePrefWidth(height);
    }

    @Override
    public double computePrefHeight(double width) {
        return super.computePrefHeight(width);
    }

    @Override
    public double computeMaxWidth(double height) {
        return super.computeMaxWidth(height);
    }

    @Override
    public double computeMaxHeight(double width) {
        return super.computeMaxHeight(width);
    }

    //================================================================================
    // Styleable Properties
    //================================================================================
    private final StyleableDoubleProperty textOpacity = new StyleableDoubleProperty(
        StyleableProperties.TEXT_OPACITY,
        this,
        "textOpacity",
        1.0
    );

    public double getTextOpacity() {
        return textOpacity.get();
    }

    /// MaterialFX labeled components use by default skins that inherit from [MFXLabeledSkin], which retrieves and gives
    /// the user control over the node displaying the text.
    /// Through this property, you can control the text node's opacity (not the control, not the label).
    ///
    /// Can be set in CSS via the property: '-mfx-text-opacity'.
    public StyleableDoubleProperty textOpacityProperty() {
        return textOpacity;
    }

    public void setTextOpacity(double textOpacity) {
        this.textOpacity.set(textOpacity);
    }

    //================================================================================
    // CssMetaData
    //================================================================================
    private static class StyleableProperties {
        private static final StyleablePropertyFactory<MFXLabeled<?>> FACTORY = new StyleablePropertyFactory<>(Labeled.getClassCssMetaData());
        private static final List<CssMetaData<? extends javafx.css.Styleable, ?>> cssMetaDataList;

        private static final CssMetaData<MFXLabeled<?>, Number> TEXT_OPACITY =
            FACTORY.createSizeCssMetaData(
                "-mfx-text-opacity",
                MFXLabeled::textOpacityProperty,
                1.0
            );

        static {
            cssMetaDataList = StyleUtils.cssMetaDataList(
                Labeled.getClassCssMetaData(),
                TEXT_OPACITY
            );
        }
    }

    public static List<CssMetaData<? extends javafx.css.Styleable, ?>> getClassCssMetaData() {
        return StyleableProperties.cssMetaDataList;
    }

    @Override
    public List<CssMetaData<? extends javafx.css.Styleable, ?>> getControlCssMetaData() {
        return getClassCssMetaData();
    }
}
