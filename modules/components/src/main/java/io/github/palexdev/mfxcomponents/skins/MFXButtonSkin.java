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

package io.github.palexdev.mfxcomponents.skins;

import io.github.palexdev.mfxcomponents.behaviors.MFXButtonBehaviorBase;
import io.github.palexdev.mfxcomponents.controls.MFXSurface;
import io.github.palexdev.mfxcomponents.controls.base.MFXButtonBase;
import io.github.palexdev.mfxcomponents.skins.base.MFXLabeledSkin;
import io.github.palexdev.mfxcore.utils.fx.LayoutUtils;
import io.github.palexdev.mfxeffects.ripple.MFXRippleGenerator;
import javafx.geometry.Bounds;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.ContentDisplay;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;

import static io.github.palexdev.mfxcore.events.WhenEvent.intercept;

/// Base skin implementation for all components of type {@link MFXButtonBase}.
///
/// This skin uses behaviors of type {@link MFXButtonBehaviorBase}.
///
/// The layout is simple, there are just the label to show the text, the [MFXSurface] responsible for
/// showing the various interaction states (applying an overlay background) and the [MFXRippleGenerator] for the ripple effects.
public class MFXButtonSkin<C extends MFXButtonBase<B>, B extends MFXButtonBehaviorBase<C>> extends MFXLabeledSkin<C, B> {
    //================================================================================
    // Properties
    //================================================================================
    protected final MFXSurface surface;
    protected final MFXRippleGenerator rg;

    //================================================================================
    // Constructors
    //================================================================================
    public MFXButtonSkin(C button) {
        super(button);

        // Init
        initTextMeasurementCache();
        surface = new MFXSurface(button);
        rg = new MFXRippleGenerator(button);
        rg.getStyleClass().add("surface-ripple");
        rg.enable();

        // Finalize
        getChildren().setAll(surface, rg, label);
    }

    //================================================================================
    // Overridden Methods
    //================================================================================
    @Override
    protected void initBehavior(B behavior) {
        C button = getSkinnable();
        super.initBehavior(behavior);
        events(
            intercept(button, MouseEvent.MOUSE_PRESSED).process(behavior::mousePressed),
            intercept(button, MouseEvent.MOUSE_CLICKED).process(behavior::mouseClicked),
            intercept(button, KeyEvent.KEY_PRESSED)
                .process(e -> behavior.keyPressed(e, _ -> {
                    if (e.getCode() == KeyCode.ENTER || e.getCode() == KeyCode.SPACE) {
                        Bounds b = button.getLayoutBounds();
                        rg.generate(b.getCenterX(), b.getCenterY());
                        rg.release();
                    }
                }))
        );
    }

    @Override
    protected double computePrefWidth(double height, double topInset, double rightInset, double bottomInset, double leftInset) {
        C button = getSkinnable();
        Node graphic = button.getGraphic();
        return leftInset +
               ((graphic != null) ? LayoutUtils.snappedBoundWidth(graphic) + button.getGraphicTextGap() : 0.0) +
               ((button.getContentDisplay() == ContentDisplay.GRAPHIC_ONLY) ? 0.0 : getCachedTextWidth()) +
               rightInset;
    }

    @Override
    protected double computePrefHeight(double width, double topInset, double rightInset, double bottomInset, double leftInset) {
        C button = getSkinnable();
        Node graphic = button.getGraphic();
        return topInset +
               Math.max(
                   ((graphic != null) ? LayoutUtils.snappedBoundHeight(graphic) : 0.0),
                   getCachedTextHeight()
               ) + bottomInset;
    }

    @Override
    protected double computeMaxWidth(double height, double topInset, double rightInset, double bottomInset, double leftInset) {
        return getSkinnable().prefWidth(height);
    }

    @Override
    protected double computeMaxHeight(double width, double topInset, double rightInset, double bottomInset, double leftInset) {
        return getSkinnable().prefHeight(width);
    }

    @Override
    protected void layoutChildren(double x, double y, double w, double h) {
        C button = getSkinnable();
        Pos align = button.getAlignment();
        layoutInArea(label, x, y, w, h, 0.0, align.getHpos(), align.getVpos());
        surface.resizeRelocate(0, 0, button.getWidth(), button.getHeight());
        rg.resizeRelocate(0, 0, button.getWidth(), button.getHeight());
    }

    @Override
    public void dispose() {
        surface.dispose();
        rg.dispose();
        super.dispose();
    }
}
