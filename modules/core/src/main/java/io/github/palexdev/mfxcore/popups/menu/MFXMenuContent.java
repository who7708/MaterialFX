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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.github.palexdev.mfxcore.events.WhenEvent;
import javafx.beans.InvalidationListener;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.TraversalDirection;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

/// This is the predefined content of every [MFXMenu]. Simply builds the UI entries of the menu from the [MFXMenuItems][MFXMenuItem]
/// specified in [MFXMenu#getItems()].
///
/// To avoid rebuilding the entire menu when the items change, this stores them in a [Map] as they are built. On every
/// [#build()], it reuses as many already built components as possible. Remaining items that may have been removed from
/// the menu are disposed and removed from here too.
///
/// The menu content produces two types of components depending on the [MFXMenuItem], either [#separator()] or [#cell(MFXMenuItem)].
public class MFXMenuContent extends VBox {
    //================================================================================
    // Properties
    //================================================================================
    private MFXMenu menu;
    private Map<MFXMenuItem, MFXMenuCell> itemNodes = new HashMap<>();
    private InvalidationListener itemsListener = _ -> build();

    private WhenEvent<?> focusWhen;

    //================================================================================
    // Constructors
    //================================================================================
    public MFXMenuContent(MFXMenu menu) {
        this.menu = menu;
        build();
        menu.getItems().addListener(itemsListener);
        getStyleClass().add("content");

        /*
         * For some reason the JavaFX focus API transfers focus to the second item in the menu.
         *
         * The solution is a bit intricate, but it works. When MFXPopupContent is shown, it requests the focus.
         * The advantage of this is that any item previously focused is reset, and thanks to this handler, the focus
         * is transferred to the first item.
         */
        focusWhen = WhenEvent.intercept(this, KeyEvent.KEY_PRESSED)
            .condition(e -> e.getCode() == KeyCode.DOWN && isFocused())
            .process(_ -> requestFocusTraversal(TraversalDirection.NEXT))
            .register();
    }

    //================================================================================
    // Methods
    //================================================================================
    protected void build() {
        ObservableList<MFXMenuItem> items = menu.getItems();
        if (items.isEmpty()) {
            getChildren().clear();
            itemNodes.values().forEach(MFXMenuCell::dispose);
            itemNodes.clear();
            return;
        }

        List<Node> children = new ArrayList<>();
        Map<MFXMenuItem, MFXMenuCell> alreadyBuilt = itemNodes;
        itemNodes = new HashMap<>();
        for (MFXMenuItem item : items) {
            // Separators are special
            if (MFXMenuItem.SEPARATOR == item) {
                children.add(separator());
                continue;
            }

            MFXMenuCell node;
            if ((node = alreadyBuilt.remove(item)) == null) {
                node = cell(item);
            }
            itemNodes.put(item, node);
            children.add(node);
        }

        // Clear and dispose remaining cells
        alreadyBuilt.values().forEach(MFXMenuCell::dispose);
        alreadyBuilt.clear();

        getChildren().setAll(children);
    }

    /// Creates the [MFXMenuCell] responsible for displaying the given item.
    protected MFXMenuCell cell(MFXMenuItem item) {
        return new MFXMenuCell(menu, item);
    }

    /// Creates a separating [Region] with the style class: `.separator`.
    protected Node separator() {
        Region separator = new Region();
        separator.getStyleClass().add("separator");
        return separator;
    }

    public void dispose() {
        menu.getItems().removeListener(itemsListener);
        itemsListener = null;
        itemNodes.clear();
        getChildren().clear();
        focusWhen.dispose();
        focusWhen = null;
        menu = null;
    }
}
