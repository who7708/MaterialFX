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

package io.github.palexdev.mfxcore.popups.menu;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Supplier;

import io.github.palexdev.mfxcore.behavior.MFXBehavior;
import io.github.palexdev.mfxcore.controls.MFXSkinBase;
import io.github.palexdev.mfxcore.input.WhenEvent;
import io.github.palexdev.mfxcore.utils.fx.LayoutUtils;
import javafx.beans.InvalidationListener;
import javafx.collections.ObservableList;
import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Region;

/// Default skin implementation for [MFXMenuContent]. Extends [MFXSkinBase] and expects behaviors of type
/// [MFXMenuContentBehavior].
///
/// The layout is manual but very simple:
/// 1) Entries are cached. This means that every time a change occurs in the [MFXMenu#getItems()] list, it
/// reuses the prebuilt entries if possible, while discarding only those for which the item is no longer in the list.
/// The method responsible for updating the entries is [#updateChildren()].
/// 2) Entries are positioned one below the other, spaced evenly by the [MFXMenuContent#spacingProperty()]
/// 3) The width is the maximum between the entries. The height is the sum of all the entries' heights including the gap.
/// 4) If the menu is empty, and the [MFXMenuContent#placeholderSupplierProperty()] produces a valid result, a placeholder
/// node is shown instead. This node can be selected from CSS via the style class: '.placeholder'
public class MFXMenuContentSkin extends MFXSkinBase<MFXMenuContent> {
    //================================================================================
    // Methods
    //================================================================================
    private Map<MFXMenuItem, MFXMenuEntry> itemsToNodes = new HashMap<>();
    private InvalidationListener itemsListener = _ -> updateChildren();
    private InvalidationListener placeholderListener = _ -> {
        MFXMenuContent content = getSkinnable();
        MFXMenu menu = content.getMenu();
        if (menu.getItems().isEmpty()) updateChildren();
    };

    // Keep a reference to it for layout
    private Node placeholder;

    //================================================================================
    // Constructors
    //================================================================================
    public MFXMenuContentSkin(MFXMenuContent mc) {
        super(mc);
        updateChildren();
        addListeners();
    }

    //================================================================================
    // Methods
    //================================================================================

    /// Adds the following listeners:
    /// - A listener on the [MFXMenu#getItems()] list to call [#updateChildren()]
    /// - A listener on the [MFXMenuContent#placeholderSupplierProperty()] to call [#updateChildren()] when the menu has
    /// no items
    protected void addListeners() {
        MFXMenuContent content = getSkinnable();
        MFXMenu menu = content.getMenu();
        menu.getItems().addListener(itemsListener);
        content.placeholderSupplierProperty().addListener(placeholderListener);
    }

    /// This core method is responsible for building, caching and reusing the nodes that compose the menu's content.
    /// It essentially produces two types of nodes:
    /// - A [Region] that acts as a separator for items that are [MFXMenuItem#SEPARATOR]
    /// - A [MFXMenuEntry] for all the others
    ///
    /// @see #buildEntry(MFXMenuItem)
    /// @see #buildSeparator()
    protected void updateChildren() {
        MFXMenuContent content = getSkinnable();
        ObservableList<MFXMenuItem> items = content.getMenu().getItems();
        if (items.isEmpty()) {
            Optional.ofNullable(content.getPlaceholderSupplier())
                .map(Supplier::get)
                .ifPresentOrElse(
                    n -> {
                        placeholder = n;
                        placeholder.getStyleClass().add("placeholder");
                        getChildren().setAll(n);
                    },
                    () -> {
                        placeholder = null;
                        getChildren().clear();
                    }
                );
            itemsToNodes.values().forEach(MFXMenuEntry::dispose);
            itemsToNodes.clear();
            return;
        }

        List<Node> children = new ArrayList<>();
        Map<MFXMenuItem, MFXMenuEntry> tmp = itemsToNodes;
        itemsToNodes = new HashMap<>();
        for (MFXMenuItem item : items) {
            // Separators are special
            if (MFXMenuItem.SEPARATOR == item) {
                children.add(buildSeparator());
                continue;
            }

            MFXMenuEntry entry;
            if ((entry = tmp.remove(item)) == null) {
                entry = buildEntry(item);
            }
            itemsToNodes.put(item, entry);
            children.add(entry);
        }

        // Clear and dispose remaining entries
        tmp.values().forEach(MFXMenuEntry::dispose);
        tmp.clear();

        placeholder = null;
        getChildren().setAll(children);
    }

    /// Creates a new [MFXMenuEntry] for the given [MFXMenuItem].
    protected MFXMenuEntry buildEntry(MFXMenuItem item) {
        return new MFXMenuEntry(getMenu(), item);
    }

    /// Create a [Region] with style class `.separator` for the special item [MFXMenuItem#SEPARATOR].
    protected Node buildSeparator() {
        Region separator = new Region();
        separator.getStyleClass().add("separator");
        return separator;
    }

    /// Shortcut for `getSkinnable().getMenu()`, retrieves the [MFXMenu] instance to which the content is associated to.
    protected MFXMenu getMenu() {
        return getSkinnable().getMenu();
    }

    //================================================================================
    // Overridden Methods
    //================================================================================

    /// Adds the following handlers:
    /// - An event handler on the control itself for key handling,
    /// see [MFXMenuContentBehavior#keyPressed(KeyEvent, Consumer)]
    @Override
    protected void registerBehavior() {
        super.registerBehavior();
        MFXMenuContent mc = getSkinnable();
        MFXBehavior<? extends Node> behavior = getBehavior();
        events(
            WhenEvent.intercept(mc, KeyEvent.KEY_PRESSED)
                .handle(behavior::keyPressed)
        );
    }

    @Override
    protected double computePrefWidth(double height, double topInset, double rightInset, double bottomInset, double leftInset) {
        if (placeholder != null) return LayoutUtils.snappedBoundWidth(placeholder);
        return getChildren().stream()
                   .mapToDouble(LayoutUtils::snappedBoundWidth)
                   .max()
                   .orElse(0.0) + rightInset + leftInset;
    }

    @Override
    protected double computePrefHeight(double width, double topInset, double rightInset, double bottomInset, double leftInset) {
        if (placeholder != null) return LayoutUtils.snappedBoundHeight(placeholder);
        double gap = getSkinnable().getSpacing();
        return getChildren().stream()
                   .mapToDouble(node -> LayoutUtils.boundHeight(node) + gap)
                   .sum() + topInset + bottomInset - gap;
    }

    @Override
    protected void layoutChildren(double x, double y, double w, double h) {
        double gap = getSkinnable().getSpacing();
        double advance = 0;
        for (Node child : getChildren()) {
            double ch = LayoutUtils.boundHeight(child);
            child.resizeRelocate(0, y + advance, w, ch);
            advance += ch + gap;
        }

        if (placeholder != null) {
            layoutInArea(placeholder, x, y, w, h, 0, HPos.CENTER, VPos.CENTER);
        }
    }

    @Override
    public void dispose() {
        MFXMenuContent content = getSkinnable();
        if (itemsListener != null) {
            MFXMenu menu = getMenu();
            menu.getItems().removeListener(itemsListener);
            itemsListener = null;
        }
        if (placeholderListener != null) {
            content.placeholderSupplierProperty().removeListener(placeholderListener);
            placeholderListener = null;
        }
        placeholder = null;
        itemsToNodes.values().forEach(MFXMenuEntry::dispose);
        itemsToNodes.clear();
        getChildren().clear();
        super.dispose();
    }
}
