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

import io.github.palexdev.mfxcore.behavior.DisposableAction;
import io.github.palexdev.mfxcore.controls.Label;
import io.github.palexdev.mfxcore.controls.MFXStyleable;
import io.github.palexdev.mfxcore.events.WhenEvent;
import io.github.palexdev.mfxcore.observables.When;
import io.github.palexdev.mfxcore.utils.fx.AnchorHandlers.Align;
import io.github.palexdev.mfxcore.utils.fx.AnchorHandlers.HAlign;
import io.github.palexdev.mfxcore.utils.fx.AnchorHandlers.VAlign;
import io.github.palexdev.mfxcore.utils.fx.LayoutUtils;
import javafx.beans.InvalidationListener;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Region;

/// Despite the name, [MFXMenu] does not use virtualization for its entries' list, but the concept is similar.
///
/// Each cell is associated and responsible for displaying a certain [MFXMenuItem]. The layout is simple, there are just
/// two labels:
/// - The 'leading' label displays the icon and the text specified by the item. Can be selected in CSS with `.leading`.
/// - The 'trailing' label displays the shortcut text or an arrow if the item is the entry for a submenu. Can be selected
/// in CSS with `.leading`. If it opens a submenu, the style class `.sub` is also added.
///
/// Any cell has also a dependency on the menu that owns it. This is necessary to properly handle the cascade of submenus.
///
/// [MFXMenuCell] also implements [MFXStyleable], the default CSS style class is set to `.menu-cell`.
public class MFXMenuCell extends Region implements MFXStyleable {
    //================================================================================
    // Properties
    //================================================================================
    private final MFXMenu menu;
    private final MFXMenuItem item;

    private final Label leading;
    private final Label trailing;

    private InvalidationListener subListener;
    private SubMenuHandler subMenuHandler;
    private final List<DisposableAction> disposables = new ArrayList<>();

    //================================================================================
    // Constructors
    //================================================================================
    public MFXMenuCell(MFXMenu menu, MFXMenuItem item) {
        this.menu = menu;
        this.item = item;
        defaultStyleClasses(this);

        // Build UI
        leading = new Label();
        leading.setGraphic(item.icon());
        leading.setText(item.text());
        leading.getStyleClass().add("leading");

        Region tIcon = new Region();
        tIcon.getStyleClass().add("icon");
        trailing = new Label("", tIcon);
        trailing.getStyleClass().add("trailing");
        handleSubMenu();

        // Finalize
        addListeners();
        getChildren().setAll(leading, trailing);
    }

    //================================================================================
    // Methods
    //================================================================================

    /// Adds the following listeners/handlers:
    /// - A listener on the cell's [#hoverProperty()] to show/hide the submenu if present. This also sets the parent menu's
    /// [MFXMenu#hoveredItemProperty()] to this cell.
    /// - A mouse click handler to run the action specified by the cell's item and close the menu from the root.
    /// - A listener on the [MFXMenuItem#subMenuItems()] list to build/dispose the submenu as needed.
    protected void addListeners() {
        Collections.addAll(disposables,
            When.onInvalidated(hoverProperty())
                .then(h -> {
                    menu.setHoveredItem(this);
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
                .process(_ -> {
                    menu.getRootMenu().hide();
                    item.action().run();
                })
                .register()
        );

        subListener = _ -> handleSubMenu();
        item.subMenuItems().addListener(subListener);
    }

    /// This method is mainly responsible for creating or disposing the submenu depending on the [MFXMenuItem#subMenuItems()]
    /// list. It also updates the trailing label because if a submenu is needed, its text is set to `null` (no shortcut)
    /// and the `.sub` style clas is added.
    protected void handleSubMenu() {
        if (item.subMenuItems().isEmpty()) {
            trailing.getStyleClass().remove("sub");
            trailing.setText(item.shortcut() != null ? item.shortcut().toDisplayString() : null);
        } else {
            trailing.setText(null);
            trailing.getStyleClass().add("sub");
            subMenuHandler = new SubMenuHandler();
        }
    }

    public void dispose() {
        if (subListener != null) {
            item.subMenuItems().removeListener(subListener);
            subListener = null;
        }
        if (subMenuHandler != null) {
            subMenuHandler.dispose();
            subMenuHandler = null;
        }
        disposables.forEach(DisposableAction::dispose);
        disposables.clear();
    }

    //================================================================================
    // Overridden Methods
    //================================================================================
    @Override
    public List<String> defaultStyleClasses() {
        return MFXStyleable.styleClasses("menu-cell");
    }

    @Override
    protected double computeMinWidth(double height) {
        return snappedLeftInset() +
               LayoutUtils.snappedBoundWidth(leading) +
               LayoutUtils.snappedBoundWidth(trailing) +
               snappedRightInset();
    }

    @Override
    protected double computeMinHeight(double width) {
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
        layoutInArea(leading, x, y, w, h, 0, getPadding(), HPos.LEFT, VPos.CENTER);
        layoutInArea(trailing, x, y, w, h, 0, getPadding(), HPos.RIGHT, VPos.CENTER);
    }

    //================================================================================
    // Inner Classes
    //================================================================================

    /// Utility class to make the submenu's handling easier and clearer.
    protected class SubMenuHandler {
        private MFXMenu subMenu;
        private When<?> hideListener;

        public SubMenuHandler() {
            subMenu = new MFXMenu(menu, item.subMenuItems());
            hideListener = When.onInvalidated(menu.hoveredItemProperty())
                .then(_ -> hide())
                .listen();
        }

        /// Shows the submenu by calling [MFXMenu#showSub(Node, Pos, Align)] with [Pos#TOP_RIGHT], [HAlign#AFTER] and
        /// [VAlign#BELOW] as the parameters.
        ///
        /// Also resets the submenu's hovered item to `null`.
        public void show() {
            subMenu.setHoveredItem(null);
            subMenu.showSub(MFXMenuCell.this, Pos.TOP_RIGHT, Align.of(HAlign.AFTER, VAlign.BELOW));
        }

        /// Hides the submenu only if its parent's [MFXMenu#hoveredItemProperty()] is not this cell.
        public void hide() {
            Node hc = menu.getHoveredItem();
            if (hc != MFXMenuCell.this) {
                subMenu.hide();
            }
        }

        public void dispose() {
            hideListener.dispose();
            hideListener = null;
            ((MFXMenuContent) subMenu.getContent()).dispose();
            subMenu = null;
        }
    }
}
