/*
 * Copyright (C) 2026 Parisi Alessandro - alessandro.parisi406@gmail.com
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

import io.github.palexdev.mfxcore.behavior.MFXBehavior;
import io.github.palexdev.mfxcore.utils.fx.PseudoClasses;
import javafx.scene.TraversalDirection;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;

import static io.github.palexdev.mfxcore.input.WhenEvent.intercept;

/// Default behavior implementation for every [MFXMenuEntry].
/// <br >
/// This handles the following interactions:
/// - On mouse click runs the action specified by the entry's item and closes the menu from the root if the action was not
/// `null`!
///  On various other mouse interactions it enables/disables the `armed` pseudo class on the entry
/// - On key presses runs actions such as key navigation (including showing and closing submenus) and trigger
/// (run action or open submenu)
public class MFXMenuEntryBehavior extends MFXBehavior<MFXMenuEntry> {

    //================================================================================
    // Constructors
    //================================================================================

    public MFXMenuEntryBehavior(MFXMenuEntry entry) {
        super(entry);
    }

    //================================================================================
    // Overridden Methods
    //================================================================================

    @Override
    public void init() {
        MFXMenuEntry entry = getNode();
        register(
            intercept(entry, MouseEvent.MOUSE_PRESSED).handle(this::mousePressed),
            intercept(entry, MouseEvent.MOUSE_RELEASED).handle(this::mouseReleased),
            intercept(entry, MouseEvent.MOUSE_EXITED).handle(this::mouseExited),
            intercept(entry, MouseEvent.MOUSE_CLICKED).handle(this::mouseClicked),
            intercept(entry, KeyEvent.KEY_PRESSED).handle(this::keyPressed).asFilter()
        );
    }

    @Override
    public void mousePressed(MouseEvent e, Runnable callback) {
        PseudoClasses.setOn(getNode(), "armed", true);
    }

    @Override
    public void mouseReleased(MouseEvent e, Runnable callback) {
        PseudoClasses.setOn(getNode(), "armed", false);
    }

    @Override
    public void mouseExited(MouseEvent e, Runnable callback) {
        PseudoClasses.setOn(getNode(), "armed", false);
    }

    @Override
    public void mouseClicked(MouseEvent e, Runnable callback) {
        MFXMenuEntry entry = getNode();
        MFXMenuItem item = entry.getItem();
        if (e.getButton() == MouseButton.PRIMARY && item.action() != null) {
            entry.getMenu().getRootMenu().hide();
            item.action().run();
        }
    }

    @Override
    public void keyPressed(KeyEvent e, Runnable callback) {
        MFXMenuEntry entry = getNode();
        MFXMenu menu = entry.getMenu();
        SubMenuHandler subMenuHandler = entry.getSubMenuHandler();
        KeyCode code = e.getCode();
        switch (code) {
            case SPACE, ENTER -> {
                if (subMenuHandler != null) {
                    if (!subMenuHandler.isShowing()) {
                        subMenuHandler.show();
                        subMenuHandler.focus();
                    }
                } else {
                    entry.getItem().action().run();
                    menu.getRootMenu().hide();
                }
            }
            case UP -> entry.requestFocusTraversal(TraversalDirection.UP);
            case DOWN -> entry.requestFocusTraversal(TraversalDirection.DOWN);
            case RIGHT -> {
                if (subMenuHandler != null) {
                    subMenuHandler.show();
                    subMenuHandler.focus();
                }
            }
            case LEFT -> {
                if (!menu.isRootMenu()) {
                    menu.hide();
                }
            }
            case ESCAPE -> menu.hide();
        }
        e.consume();
    }
}
