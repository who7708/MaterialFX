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
import java.util.Collections;
import java.util.List;

import io.github.palexdev.mfxcore.base.Disposable;
import io.github.palexdev.mfxcore.controls.Label;
import io.github.palexdev.mfxcore.controls.MFXStyleable;
import io.github.palexdev.mfxcore.input.WhenEvent;
import io.github.palexdev.mfxcore.observables.When;
import io.github.palexdev.mfxcore.utils.fx.LayoutUtils;
import javafx.beans.InvalidationListener;
import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.TraversalDirection;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Region;

/// Each entry is associated and responsible for displaying a certain [MFXMenuItem]. The layout is simple, there are just
/// two labels:
/// - The 'leading' label displays the icon and the text specified by the item. Can be selected in CSS with `.leading`.
/// - The 'trailing' label displays the shortcut text or an arrow if the item is the entry for a submenu. Can be selected
/// in CSS with `.trailing`. If it opens a submenu, the style class `.sub` is also added.<br >
/// When the entry shows a submenu, the trailing label contains a [Region] with style class '.svg-icon'. This is used to
/// show the arrow icon, and it's expected to be properly set up in CSS with a SVG shape (-fx-shape. This is because
/// MFXCore does not depend on MFXResources, so we don't have font icon capabilities here).
///
/// Any entry has also a dependency on the menu that owns it. This is necessary to properly handle the cascade of submenus.
///
/// [MFXMenuEntry] also implements [MFXStyleable], the default CSS style class is set to `.menu-entry`.
public class MFXMenuEntry extends Region implements MFXStyleable {
    //================================================================================
    // Properties
    //================================================================================
    private final MFXMenu menu;
    private final MFXMenuItem item;

    private final Region surface;
    private final Label leading;
    private final Label trailing;

    private InvalidationListener subListener;
    private SubMenuHandler subMenuHandler;
    private final List<Disposable> disposables = new ArrayList<>();

    //================================================================================
    // Constructors
    //================================================================================
    public MFXMenuEntry(MFXMenu menu, MFXMenuItem item) {
        this.menu = menu;
        this.item = item;
        defaultStyleClasses(this);
        setFocusTraversable(true);

        // Build UI
        surface = new Region();
        surface.getStyleClass().add("pseudo-surface");
        surface.setManaged(false);

        leading = new Label();
        leading.setGraphic(item.icon());
        leading.setText(item.text());
        leading.getStyleClass().add("leading");

        Region tIcon = new Region();
        tIcon.getStyleClass().add("svg-icon");
        trailing = new Label("", tIcon);
        trailing.getStyleClass().add("trailing");
        handleSubMenu();

        if (item.disableExpression() != null)
            disableProperty().bind(item.disableExpression());

        // Finalize
        addListeners();
        getChildren().setAll(surface, leading, trailing);
    }

    //================================================================================
    // Methods
    //================================================================================

    /// Adds the following listeners/handlers:
    /// - A listener on the entry's [#hoverProperty()] to show/hide the submenu if present. This also sets the parent menu's
    /// [MFXMenu#hoveredItemProperty()] to this entry.
    /// - A mouse click handler to run the action specified by the entry's item and close the menu from the root.
    /// - A key handler on the entry to manage actions such as key navigation (including showing and closing submenus)
    /// and trigger
    /// - A listener on the [MFXMenuItem#children()] list to build/dispose the submenu as needed.
    protected void addListeners() {
        Collections.addAll(disposables,
            When.onInvalidated(hoverProperty())
                .then(h -> {
                    menu.setHoveredItem(this);
                    requestFocus(); // Reset focus acquired by key navigation
                    if (subMenuHandler == null) return;
                    if (h) {
                        subMenuHandler.show();
                    } else {
                        subMenuHandler.hide();
                    }
                })
                .executeNow(this::isHover)
                .listen(),
            WhenEvent.intercept(this, MouseEvent.MOUSE_CLICKED)
                .condition(e -> e.getButton() == MouseButton.PRIMARY && item.action() != null)
                .handle(_ -> {
                    menu.getRootMenu().hide();
                    item.action().run();
                })
                .register(),
            WhenEvent.intercept(this, KeyEvent.KEY_PRESSED)
                .handle(e -> {
                    KeyCode code = e.getCode();
                    switch (code) {
                        case SPACE, ENTER -> {
                            if (subMenuHandler != null) {
                                if (!subMenuHandler.isShowing()) {
                                    subMenuHandler.show();
                                    subMenuHandler.focus();
                                }
                            } else {
                                item.action().run();
                                menu.getRootMenu().hide();
                            }
                        }
                        case UP -> requestFocusTraversal(TraversalDirection.UP);
                        case DOWN -> requestFocusTraversal(TraversalDirection.DOWN);
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
                })
                .asFilter()
                .register()
        );

        subListener = _ -> handleSubMenu();
        item.children().addListener(subListener);
    }

    /// This method is mainly responsible for creating or disposing the submenu depending on the [MFXMenuItem#children()]
    /// list. It also updates the trailing label because if a submenu is needed, its text is set to `null` (no shortcut)
    /// and the `.sub` style clas is added.
    protected void handleSubMenu() {
        if (item.children().isEmpty()) {
            trailing.getStyleClass().remove("sub");
            trailing.setText(item.shortcut() != null ? item.shortcut().toDisplayString() : null);
        } else {
            trailing.setText(null);
            trailing.getStyleClass().add("sub");
            subMenuHandler = new SubMenuHandler(menu, item, this);
        }
    }

    public void dispose() {
        if (subListener != null) {
            item.children().removeListener(subListener);
            subListener = null;
        }
        if (subMenuHandler != null) {
            subMenuHandler.dispose();
            subMenuHandler = null;
        }
        disposables.forEach(Disposable::dispose);
        disposables.clear();
        disableProperty().unbind();
    }

    //================================================================================
    // Overridden Methods
    //================================================================================
    @Override
    public List<String> defaultStyleClasses() {
        return MFXStyleable.styleClasses("menu-entry");
    }

    @Override
    protected double computePrefWidth(double height) {
        return snappedLeftInset() +
               LayoutUtils.snappedBoundWidth(leading) +
               LayoutUtils.snappedBoundWidth(trailing) +
               12.0 + // Arbitrary ideal gap between the labels
               snappedRightInset();
    }

    @Override
    protected double computePrefHeight(double width) {
        double lH = LayoutUtils.snappedBoundHeight(leading);
        double tH = LayoutUtils.snappedBoundHeight(trailing);
        return snappedTopInset() +
               Math.max(lH, tH) +
               snappedBottomInset();
    }

    @Override
    protected void layoutChildren() {
        double x = 0;
        double y = 0;
        double w = getWidth();
        double h = getHeight();
        surface.resizeRelocate(x, y, w, h);
        layoutInArea(leading, x, y, w, h, 0, getPadding(), HPos.LEFT, VPos.CENTER);
        layoutInArea(trailing, x, y, w, h, 0, getPadding(), HPos.RIGHT, VPos.CENTER);
    }
}
