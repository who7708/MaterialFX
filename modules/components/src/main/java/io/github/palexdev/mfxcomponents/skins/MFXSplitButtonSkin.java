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

import java.util.Objects;

import io.github.palexdev.mfxcomponents.controls.MFXButton;
import io.github.palexdev.mfxcomponents.controls.MFXButton.MFXToggleButton;
import io.github.palexdev.mfxcomponents.controls.MFXSplitButton;
import io.github.palexdev.mfxcomponents.skins.base.MFXChoiceSkin;
import io.github.palexdev.mfxcomponents.variants.ButtonVariants.SizeVariant;
import io.github.palexdev.mfxcomponents.variants.ButtonVariants.StyleVariant;
import io.github.palexdev.mfxcore.popups.PopupState;
import io.github.palexdev.mfxcore.utils.fx.LayoutUtils;
import io.github.palexdev.mfxresources.icon.MFXFontIcon;
import javafx.beans.InvalidationListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.CacheHint;
import javafx.scene.Node;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Control;
import javafx.scene.control.Label;

import static io.github.palexdev.mfxcore.observables.When.onInvalidated;

/// Default skin implementation for all [MFXSplitButtons][MFXSplitButton]. Extends [MFXChoiceSkin].
///
/// It is composed of two nodes:
/// - A leading [MFXButton] which is used as a container for the view cell described in [MFXChoiceSkin]. In fact, its
/// [MFXButton#contentDisplayProperty()] is set to [ContentDisplay#GRAPHIC_ONLY]. The button is set up to execute the
/// action specified by the [MFXSplitButton#onActionProperty()] when triggered.
/// - A trailing [MFXButton] which is used to show the popup with the various choices. This button is also set to display
/// the [MFXFontIcon] only.
///
/// Between the two buttons there is a gap, 2px by default, can be changed by overriding the class and setting the [#GAP] variable.
///
/// The variants in [MFXSplitButton] are applied to the aforementioned buttons too, so that we can leverage the CSS styles
/// already defined for [MFXButton] and its variants.
public class MFXSplitButtonSkin<T> extends MFXChoiceSkin<T> {
    //================================================================================
    // Properties
    //================================================================================
    private final MFXButton lead;
    private final MFXToggleButton trail;
    private InvalidationListener variantsUpdater = _ -> updateVariants();

    protected double GAP = 2.0;

    //================================================================================
    // Constructors
    //================================================================================

    public MFXSplitButtonSkin(MFXSplitButton<T> button) {
        lead = new MFXButton();
        trail = new MFXToggleButton();
        super(button);

        // Init
        lead.onActionProperty().bind(button.onActionProperty()
            .map(c -> _ -> c.accept(button.getSelectedItem()))
        );
        lead.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        lead.getStyleClass().add("leading");
        lead.setManaged(false);

        MFXFontIcon trailIcon = new MFXFontIcon();
        trailIcon.setCache(true);
        trailIcon.setCacheHint(CacheHint.ROTATE);
        trail.setGraphic(trailIcon);
        trail.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        trail.getStyleClass().add("trailing");
        trail.setManaged(false);
        trail.onSelectionChanged(s -> {
            if (s && !popup.isShowing()) {
                popup.show(trail, popupConfig.anchor(), popupConfig.alignment());
            } else if (popup.isShowing()) {
                popup.hide();
            }
        });

        // Finalize
        updateVariants();
        button.getAppliedVariants().addListener(variantsUpdater);
        getChildren().addAll(lead, trail);
    }

    //================================================================================
    // Methods
    //================================================================================

    /// This is responsible for setting the variants set on the [MFXSplitButton], on the two leading and trailing buttons too.
    protected void updateVariants() {
        MFXSplitButton<T> button = getControl();
        StyleVariant style = button.getAppliedVariant(StyleVariant.class);
        SizeVariant size = button.getAppliedVariant(SizeVariant.class);
        lead.setStyle(style);
        lead.setSize(size);
        trail.setStyle(style);
        trail.setSize(size);
    }

    //================================================================================
    // Overridden Methods
    //================================================================================

    /// {@inheritDoc}
    ///
    /// To avoid cluttering CSS, we override this to implement a trick on the view cell. Since the button's font and text
    /// color are already defined, and especially considering that font size depends on the set [SizeVariant], we bind those
    /// properties from the leading button to the view cell.
    ///
    /// To do that, we perform a [Node#lookup(String)] on the cell's node and search for a '.label' node.
    /// So, we expect either the cell to be a label or to contain one.
    ///
    /// Finally, this is also responsible for setting the view cell as the graphic of the leading button.
    // FIXME this trick must be removed in some way
    @Override
    protected void buildViewCell() {
        super.buildViewCell();
        if (viewCell == null) {
            lead.setGraphic(null);
            return;
        }

        Node node = viewCell.toNode();
        ObservableValue<?> ov = node instanceof Control c ? c.skinProperty() : node.sceneProperty();
        onInvalidated(ov)
            .condition(Objects::nonNull)
            .then(_ -> {
                if (node.lookup(".label") instanceof Label label) {
                    label.fontProperty().bind(lead.fontProperty());
                    label.textFillProperty().bind(lead.textFillProperty());
                }
            })
            .oneShot(true)
            .executeNow(() -> node.getScene() != null)
            .listen();
        lead.setGraphic(node);
    }

    @Override
    protected void onPopupState(PopupState state) {
        super.onPopupState(state);
        if (state == PopupState.HIDING) {
            trail.setSelected(false);
        }
    }

    @Override
    protected double computePrefWidth(double height, double topInset, double rightInset, double bottomInset, double leftInset) {
        return leftInset +
               LayoutUtils.snappedBoundWidth(lead) +
               GAP +
               LayoutUtils.snappedBoundWidth(trail) +
               rightInset;
    }

    @Override
    protected double computePrefHeight(double width, double topInset, double rightInset, double bottomInset, double leftInset) {
        return topInset +
               Math.max(
                   LayoutUtils.snappedBoundHeight(lead),
                   LayoutUtils.snappedBoundHeight(trail)
               ) +
               bottomInset;
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
        layoutInArea(lead, x, y, w, h, 0, HPos.LEFT, VPos.CENTER);
        layoutInArea(trail, x + GAP, y, w, h, 0, HPos.RIGHT, VPos.CENTER);
    }

    @Override
    public void dispose() {
        MFXSplitButton<T> button = getControl();
        button.getAppliedVariants().removeListener(variantsUpdater);
        variantsUpdater = null;
        lead.onActionProperty().unbind();
        super.dispose();
    }

    @Override
    protected MFXSplitButton<T> getControl() {
        return (MFXSplitButton<T>) super.getControl();
    }
}
