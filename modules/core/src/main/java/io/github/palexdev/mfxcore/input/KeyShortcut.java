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

package io.github.palexdev.mfxcore.input;

import java.util.Collections;
import java.util.EnumSet;

import io.github.palexdev.mfxcore.utils.fx.KeyCodeUtils;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;

/// A simple record to represent a keyboard shortcut that is: zero or more [KeyModifiers][KeyModifier] and a generic
/// key pressed together at the same time.
///
/// Compared to the usual JavaFX spaghetti code, which should be [KeyCombination] and related subclasses, this is much
/// simpler, more "open".
///
/// There are two ways to build a shortcut:
/// 1) Via the constructors
/// 2) From a string, see [#of(String)]
///
/// Technically, there's also a third one, which is [#fromEvent(KeyEvent)], but that is more of a utility to check if
/// a [KeyEvent] corresponds to a certain [KeyShortcut].
///
/// The [Key][KeyCode] for the shortcut can't be a modifier ([KeyCode#isModifierKey()]) and it should be a valid key
/// according to [KeyCodeUtils#isValidShortcutKey(KeyCode)].
///
/// To display the shortcut in the UI don't use `toString()`, but rather [#toDisplayString()].
public record KeyShortcut(
    EnumSet<KeyModifier> modifiers,
    KeyCode key
) {

    //================================================================================
    // Constructors
    //================================================================================
    public KeyShortcut {
        if (key.isModifierKey())
            throw new IllegalArgumentException("Cannot use modifier key as shortcut key: " + key);
        if (!KeyCodeUtils.isValidShortcutKey(key))
            throw new IllegalArgumentException("Invalid shortcut key: " + key);
    }

    public KeyShortcut(KeyCode code, KeyModifier... modifiers) {
        this(EnumSet.noneOf(KeyModifier.class), code);
        Collections.addAll(this.modifiers, modifiers);
    }

    /// Creates a new [KeyShortcut] from the given string. Here's the ideal format for the string:
    /// `<modifier>+<modifier>+...<key>` where:
    /// - Each key/modifier is separated by the sign `+`
    /// - Modifiers can be zero or more
    /// - The key cannot be omitted and must be specified as its display representation, compliant with
    /// [KeyCodeUtils] and [KeyCodeUtils#fromDisplayString(String)]
    ///
    /// @throws IllegalArgumentException if the string is `null` or empty
    /// @throws IllegalArgumentException if the key cannot be parsed from the string
    public static KeyShortcut of(String s) {
        if (s == null || s.trim().isEmpty()) {
            throw new IllegalArgumentException("Key combo string cannot be null or empty");
        }
        String[] keys = s.trim().split("\\+");
        EnumSet<KeyModifier> modifiers = EnumSet.noneOf(KeyModifier.class);
        KeyCode code = null;
        for (String key : keys) {
            key = key.trim();
            KeyModifier mod;
            if ((mod = KeyModifier.fromString(key)) != null) {
                modifiers.add(mod);
                continue;
            }
            code = KeyCodeUtils.fromDisplayString(key);
        }
        if (code == null) {
            throw new IllegalArgumentException("Could not parse key from combo string: " + s);
        }
        return new KeyShortcut(modifiers, code);
    }

    /// Convenience method to create a new [KeyShortcut] object from a JavaFX [KeyEvent].
    /// This is useful if you want to check whether an event corresponds to your desired key combination.
    ///
    /// **Minor Performance Improvement**
    ///
    /// The only way to check which modifiers are active on a [KeyEvent] is to use the various query methods.
    /// ([KeyEvent#isAltDown()], [KeyEvent#isShiftDown()],...)
    /// This means that to create a [KeyShortcut] from an event, there are a series of ifs here to add the right [KeyModifiers][KeyModifier].
    ///
    /// The recommended way to use this is as follows:
    /// ```java
    /// EventHandler<KeyEvent> handler = e -> {
    ///     if (e.getText() != null && !e.getText().isEmpty()){
    ///         KeyShortcut ks = KeyShortcut.fromEvent(ke);
    ///         // ...your logic here
    ///}
    ///}
    ///```
    /// Explanation: [KeyEvents][KeyEvent] are fired for modifiers too. For example, if you press `SHIFT`, JavaFX will
    /// notify of such event. However, for a shortcut, such events are irrelevant.
    /// We want to create a [KeyShortcut] only when a "standard" key is pressed. So, to filter events, we check the
    /// `getText()` value, which apparently is `null` or empty for special keys (did not test in depth though!).
    public static KeyShortcut fromEvent(KeyEvent ke) {
        EnumSet<KeyModifier> modifiers = EnumSet.noneOf(KeyModifier.class);
        if (ke.isAltDown()) modifiers.add(KeyModifier.ALT);
        if (ke.isControlDown()) modifiers.add(KeyModifier.CONTROL);
        if (ke.isShiftDown()) modifiers.add(KeyModifier.SHIFT);
        if (ke.isMetaDown()) modifiers.add(KeyModifier.META);
        return new KeyShortcut(modifiers, ke.getCode());
    }

    //================================================================================
    // Methods
    //================================================================================

    /// Converts this [KeyShortcut] to a string which can be displayed in UI components.
    ///
    /// First it appends all modifiers, then the [KeyCode] converted with [KeyCodeUtils#toDisplayString(KeyCode)].
    /// Everything is separated by `+` signs.
    public String toDisplayString() {
        StringBuilder sb = new StringBuilder();
        modifiers.forEach(m -> sb.append(m).append("+"));
        sb.append(KeyCodeUtils.toDisplayString(key));
        return sb.toString();
    }
}
