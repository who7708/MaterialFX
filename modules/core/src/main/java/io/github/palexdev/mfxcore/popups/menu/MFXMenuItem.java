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

import java.util.Arrays;

import io.github.palexdev.mfxcore.input.KeyShortcut;
import io.github.palexdev.mfxcore.utils.fx.FXCollectors;
import javafx.beans.binding.BooleanExpression;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;

/// This record represents an entry in a [MFXMenu] and has five key properties:
/// - The icon and the text for the entry, these **should** always be present.
/// - A [KeyShortcut] that triggers the action. To be precise, this is more of a hint to the user, there's no
/// key event handling involved. The event handling cannot be done by the menu or its entries, but rather they must be
/// intercepted and processed on the owner. Two examples come to my mind:
///     1) Preset app-wise action. For example, 'Save' or 'Load' in a text editor. In this case a key map on the app's
///     window or scene handles the event.
///     2) Context menus, for example, for a text field. Actions such as Copy(Ctrl+C), Paste(Ctrl+V) are intercepted and
///     handled by the control itself.
/// - An action to perform on trigger.
/// - A list of [MFXMenuItems][MFXMenuItem]. When this is not empty, it indicates that the entry should open a submenu.
///
/// @see MFXMenuItem#SEPARATOR
public record MFXMenuItem(
    Node icon,
    String text,
    KeyShortcut shortcut,
    Runnable action,
    BooleanExpression disableExpression,
    ObservableList<MFXMenuItem> children
) {

    /// Special instance of [MFXMenuItem] to indicate that at a certain position in [MFXMenu#getItems()] a separator region
    /// should be placed instead of a regular menu entry.
    // TODO add support for icon and text carrying separators, so that even virtualized menus can have them
    public static final MFXMenuItem SEPARATOR = new MFXMenuItem();

    //================================================================================
    // Constructors
    //================================================================================
    private MFXMenuItem() {
        this(null, null, null, null, null, null);
    }

    public MFXMenuItem {
        if (children == null)
            children = FXCollections.observableArrayList();
    }

    //================================================================================
    // Builder
    //================================================================================

    /// Convenience method to build a menu with a DSL.
    ///
    /// @return the array of items to be used in a menu
    public static MFXMenuItem[] items(Builder... builders) {
        return Arrays.stream(builders)
            .map(Builder::build)
            .toArray(MFXMenuItem[]::new);
    }

    public static Builder item(String text) {
        return new Builder().text(text);
    }

    public static Builder item(Node icon, String text) {
        return new Builder().icon(icon).text(text);
    }

    public static Builder submenu(String text, Builder... builders) {
        return new Builder().text(text).children(builders);
    }

    public static Builder submenu(Node icon, String text, Builder... children) {
        return new Builder().icon(icon).text(text).children(children);
    }

    /// Allows using [#SEPARATOR] in the DSL offered by [#items(Builder...)].
    public static Builder separator() {
        return new Builder() {
            @Override
            public MFXMenuItem build() {
                return SEPARATOR;
            }
        };
    }

    public static class Builder {
        private Node icon;
        private String text;
        private KeyShortcut shortcut;
        private Runnable action;
        private BooleanExpression disableExpression;
        private ObservableList<MFXMenuItem> children;

        public Builder icon(Node icon) {
            this.icon = icon;
            return this;
        }

        public Builder text(String text) {
            this.text = text;
            return this;
        }

        public Builder shortcut(KeyShortcut shortcut) {
            this.shortcut = shortcut;
            return this;
        }

        /// The string should follow the format indicated [here][KeyShortcut#of(String)].
        public Builder shortcut(String shortcut) {
            this.shortcut = KeyShortcut.of(shortcut);
            return this;
        }

        public Builder action(Runnable action) {
            this.action = action;
            return this;
        }

        public Builder disableWhen(BooleanExpression disableExpression) {
            this.disableExpression = disableExpression;
            return this;
        }

        public Builder children(Builder... builders) {
            this.children = Arrays.stream(builders)
                .map(Builder::build)
                .collect(FXCollectors.toList());
            return this;
        }

        public MFXMenuItem build() {
            return new MFXMenuItem(icon, text, shortcut, action, disableExpression, children);
        }
    }
}
