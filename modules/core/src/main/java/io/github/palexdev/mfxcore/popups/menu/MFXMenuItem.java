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

import java.util.function.Supplier;

import io.github.palexdev.mfxcore.input.KeyShortcut;
import javafx.beans.binding.BooleanExpression;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;

/// This record represents an entry in a [MFXMenu] and has five key properties:
/// - The icon and the text for the entry, these **should** always be present.
/// - A [KeyShortcut] that triggers the action. To be precise, this is more of a hint to the user, there's no
/// key event handling involved. The event handling cannot be done by the menu or its entries, but rather they must
/// intercepted and processed on the owner. Two examples come to my mind:
///     1) Preset app-wise actions. For example, 'Save' or 'Load' in a text editor. In this case a key map on the app's
///     window or scene handles the event.
///     2) Context menus, for example, for a text field. Actions such as Copy(Ctrl+C), Paste(Ctrl+V) are intercepted and
///     handled by the control itself.
/// - An action to perform on click.
/// - A list of [MFXMenuItems][MFXMenuItem]. When this is not empty, it indicates that the entry should open a submenu
/// containing all the items of such list.
///
/// @see MFXMenuItem#SEPARATOR
public record MFXMenuItem(
    Node icon,
    String text,
    KeyShortcut shortcut,
    Runnable action,
    ObservableList<MFXMenuItem> subMenuItems,
    BooleanExpression disableExpression
) {
    //================================================================================
    // Static Properties
    //================================================================================

    /// Special instance of [MFXMenuItem] to indicate that at a certain position in [MFXMenu#getItems()] a separator region
    /// should be placed instead of a regular menu entry.
    public static final MFXMenuItem SEPARATOR = new MFXMenuItem(null, null, null, null);

    //================================================================================
    // Constructors
    //================================================================================
    public MFXMenuItem(Node icon, String text, KeyShortcut shortcut, Runnable action) {
        this(icon, text, shortcut, action, FXCollections.observableArrayList());
    }

    public MFXMenuItem(Node icon, String text, KeyShortcut shortcut, Runnable action, ObservableList<MFXMenuItem> subMenuItems) {
        this(icon, text, shortcut, action, subMenuItems, null);
    }

    /// Allows building a [MFXMenuItem] by specifying the shortcut as a string. It is converted to a [KeyShortcut] with
    /// [KeyShortcut#of(String)].
    public static MFXMenuItem of(Node icon, String text, String shortcut, Runnable action) {
        return new MFXMenuItem(icon, text, KeyShortcut.of(shortcut), action);
    }

    //================================================================================
    // Withers
    //================================================================================
    public MFXMenuItem withIcon(Node icon) {
        return new MFXMenuItem(icon, text, shortcut, action, subMenuItems, disableExpression);
    }

    public MFXMenuItem withText(String text) {
        return new MFXMenuItem(icon, text, shortcut, action, subMenuItems, disableExpression);
    }

    public MFXMenuItem withShortcut(KeyShortcut shortcut) {
        return new MFXMenuItem(icon, text, shortcut, action, subMenuItems, disableExpression);
    }

    public MFXMenuItem withShortcut(String shortcut) {
        return new MFXMenuItem(icon, text, KeyShortcut.of(shortcut), action, subMenuItems, disableExpression);
    }

    public MFXMenuItem withAction(Runnable action) {
        return new MFXMenuItem(icon, text, shortcut, action, subMenuItems, disableExpression);
    }

    public MFXMenuItem withDisableExpression(BooleanExpression disableExpression) {
        return new MFXMenuItem(icon, text, shortcut, action, subMenuItems, disableExpression);
    }

    public MFXMenuItem withDisableExpression(Supplier<BooleanExpression> disableExpressionSupplier) {
        return new MFXMenuItem(icon, text, shortcut, action, subMenuItems, disableExpressionSupplier.get());
    }
}
