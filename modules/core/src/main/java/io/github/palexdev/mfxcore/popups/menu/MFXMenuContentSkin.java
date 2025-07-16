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
import java.util.function.Consumer;

import io.github.palexdev.mfxcore.controls.SkinBase;
import io.github.palexdev.mfxcore.events.WhenEvent;
import javafx.beans.InvalidationListener;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

/// Default skin implementation for [MFXMenuContent]. Extends [SkinBase] and expects behaviors of type
/// [MFXMenuContentBehavior].
///
/// The layout is very simple as we rely on a [VBox] to contain the menu's entries.
/// There are only two peculiarities in this skin:
/// 1) Entries are cached. This means that every time a change occurs in the [MFXMenu#getItems()] list, it
/// reuses the prebuilt entries if possible, while discarding only those for which the item is no longer in the list.
/// The method responsible for updating the entries is [#updateChildren()].
/// 2) The style classes specified by [MFXMenu#defaultStyleClasses()] are applied on the [VBox] container here. This is
/// one of the things that I DO NOT like about the JavaFX MVC architecture is that the control is effectively a Node that
/// needs to be rendered. In my opinion, only the skin should have been, because the current architecture increases the
/// nodes count for nothing. Not only that, if you use containers/panes for easier layout handling, you are basically
/// adding one more level in the hierarchy. So, from a CSS point of view, you would select the content as
/// `.control > .container > .content`. I wanted to avoid this, and so, by applying the classes directly to the container
/// it becomes `.mfx-menu .menu-content > .menu-entry`.
public class MFXMenuContentSkin extends SkinBase<MFXMenuContent, MFXMenuContentBehavior> {
    //================================================================================
    // Methods
    //================================================================================
    protected final VBox box;

    private Map<MFXMenuItem, MFXMenuEntry> itemsToNodes = new HashMap<>();
    private InvalidationListener itemsListener = _ -> updateChildren();

    //================================================================================
    // Constructors
    //================================================================================
    public MFXMenuContentSkin(MFXMenuContent mc) {
        super(mc);

        // Init
        box = new VBox();
        mc.defaultStyleClasses(box); // Apply the style classes on the box rather than the control itself for easier styling
        updateChildren();

        // Finalize
        addListeners();
        getChildren().setAll(box);
    }

    //================================================================================
    // Methods
    //================================================================================

    /// Adds the following listeners:
    /// - A listener on the [MFXMenu#getItems()] list to call [#updateChildren()]
    protected void addListeners() {
        MFXMenu menu = getMenu();
        menu.getItems().addListener(itemsListener);
    }

    /// This core method is responsible for building, caching and reusing the nodes that compose the menu's content.
    /// It essentially produces two types of nodes:
    /// - A [Region] that acts as a separator for items that are [MFXMenuItem#SEPARATOR]
    /// - A [MFXMenuEntry] for all the others
    ///
    /// @see #buildEntry(MFXMenuItem)
    /// @see #buildSeparator()
    protected void updateChildren() {
        ObservableList<MFXMenuItem> items = getMenu().getItems();
        if (items.isEmpty()) {
            box.getChildren().clear();
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

        box.getChildren().setAll(children);
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
    protected void initBehavior(MFXMenuContentBehavior behavior) {
        super.initBehavior(behavior);
        MFXMenuContent mc = getSkinnable();
        events(
            WhenEvent.intercept(mc, KeyEvent.KEY_PRESSED)
                .process(behavior::keyPressed)
        );
    }

    @Override
    public void dispose() {
        if (itemsListener != null) {
            MFXMenu menu = getMenu();
            menu.getItems().removeListener(itemsListener);
            itemsListener = null;
        }
        itemsToNodes.values().forEach(MFXMenuEntry::dispose);
        itemsToNodes.clear();
        box.getChildren().clear();
        super.dispose();
    }
}
