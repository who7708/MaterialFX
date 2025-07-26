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

package io.github.palexdev.mfxcomponents.cells;

import java.util.List;
import java.util.function.Supplier;

import io.github.palexdev.mfxcomponents.controls.MFXSurface;
import io.github.palexdev.mfxcomponents.popups.MFXVirtualMenuContent;
import io.github.palexdev.mfxcore.controls.MFXStyleable;
import io.github.palexdev.mfxcore.controls.SkinBase;
import io.github.palexdev.mfxcore.events.WhenEvent;
import io.github.palexdev.mfxcore.observables.When;
import io.github.palexdev.mfxcore.popups.menu.MFXMenu;
import io.github.palexdev.mfxcore.popups.menu.MFXMenuEntry;
import io.github.palexdev.mfxcore.popups.menu.MFXMenuItem;
import io.github.palexdev.mfxcore.popups.menu.SubMenuHandler;
import io.github.palexdev.mfxeffects.animations.Animations;
import io.github.palexdev.mfxeffects.animations.MomentumTransition;
import io.github.palexdev.mfxeffects.animations.motion.M3Motion;
import io.github.palexdev.mfxeffects.beans.Position;
import io.github.palexdev.mfxeffects.ripple.MFXRippleGenerator;
import io.github.palexdev.mfxresources.icon.MFXFontIcon;
import io.github.palexdev.virtualizedfx.cells.CellBaseBehavior;
import io.github.palexdev.virtualizedfx.cells.VFXCellBase;
import io.github.palexdev.virtualizedfx.cells.base.VFXCell;
import io.github.palexdev.virtualizedfx.list.VFXList;
import io.github.palexdev.virtualizedfx.list.VFXListHelper;
import io.github.palexdev.virtualizedfx.list.VFXListState;
import javafx.animation.Animation;
import javafx.beans.InvalidationListener;
import javafx.beans.property.ObjectProperty;
import javafx.geometry.Bounds;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.TraversalDirection;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Region;

/// Essentially the same as [MFXMenuEntry] but to be used in a virtualized list, [VFXList].
/// Even the default style class is the same: '.menu-entry'.
///
/// There are just a few differences in the skin, see [MFXMenuCellSkin].
public class MFXMenuCell extends VFXCellBase<MFXMenuItem> {
    //================================================================================
    // Properties
    //================================================================================
    private MFXMenu menu;

    //================================================================================
    // Constructors
    //================================================================================
    public MFXMenuCell(MFXMenu menu, MFXMenuItem item) {
        super(item);
        this.menu = menu;
        setFocusTraversable(true);
    }

    //================================================================================
    // Overridden Methods
    //================================================================================
    @Override
    public Supplier<SkinBase<?, ?>> defaultSkinProvider() {
        return () -> new MFXMenuCellSkin(this);
    }

    @Override
    public List<String> defaultStyleClasses() {
        return MFXStyleable.styleClasses("menu-entry");
    }

    @Override
    public void dispose() {
        super.dispose();
        this.menu = null;
    }

    //================================================================================
    // Getters
    //================================================================================
    public MFXMenu getMenu() {
        return menu;
    }

    //================================================================================
    // Inner Classes
    //================================================================================

    /// Main differences from [MFXMenuEntry]:
    /// - The icon for entries that show submenus is not a [Region] anymore but a font icon, [MFXFontIcon]
    /// - See [#update()]
    /// - See [#createSubMenuHandler()]
    public static class MFXMenuCellSkin extends SkinBase<MFXMenuCell, CellBaseBehavior<MFXMenuItem>> {
        //================================================================================
        // Properties
        //================================================================================
        private final MFXSurface surface;
        private final MFXRippleGenerator rg;

        private final Label leading;
        private final Label trailing;

        private InvalidationListener subListener = _ -> createSubMenuHandler();
        private SubMenuHandler subMenuHandler;

        private Animation focusScrollAnimation;

        //================================================================================
        // Constructors
        //================================================================================
        public MFXMenuCellSkin(MFXMenuCell cell) {
            super(cell);

            // Init
            surface = new MFXSurface(cell);
            rg = new MFXRippleGenerator(cell);
            rg.getStyleClass().add("surface-ripple");
            rg.setMeToPosConverter(me ->
                (me.getButton() == MouseButton.PRIMARY) ? Position.of(me.getX(), me.getY()) : null
            );
            rg.enable();

            leading = new Label();
            leading.getStyleClass().add("leading");

            trailing = new Label("");
            trailing.getStyleClass().add("trailing");

            // Finalize
            addListeners();
            getChildren().setAll(surface, rg, leading, trailing);
        }

        //================================================================================
        // Methods
        //================================================================================
        protected void addListeners() {
            MFXMenuCell cell = getSkinnable();
            listeners(
                When.onChanged(cell.itemProperty())
                    .then((o, _) -> {
                        if (o != null) o.subMenuItems().removeListener(subListener);
                        update();
                    })
                    .executeNow(),
                When.onInvalidated(cell.hoverProperty())
                    .then(h -> {
                        ((ObjectProperty<Node>) cell.getMenu().hoveredItemProperty()).set(cell);
                        cell.requestFocus(); // Reset focus acquired by key navigation
                        if (subMenuHandler == null) return;
                        if (h) {
                            subMenuHandler.show();
                        } else {
                            subMenuHandler.hide();
                        }
                    })
                    .executeNow(cell::isHover)
                    .listen()
            );
        }

        /// Because [MFXVirtualMenuContent] is virtualized, cells can be updated depending on the scroll position of the
        /// container (instead, [MFXMenuEntry] are 'static'). This core method is called whenever the [MFXMenuCell#itemProperty()]
        /// changes, and it's responsible for updating both the leading and trailing labels according to the new item.
        /// <br >
        /// It's also responsible for creating the [SubMenuHandler] is the new item should show a submenu
        /// ([MFXMenuItem#subMenuItems()] is not empty).
        ///
        /// **WARN!** <br >
        /// Because virtualized lists only support fixed size elements, it's not recommended to use separators for this
        /// type of content.
        ///
        /// @see #createSubMenuHandler()
        protected void update() {
            MFXMenuCell cell = getSkinnable();
            MFXMenuItem item = cell.getItem();
            if (item == null) {
                cell.disableProperty().unbind();
                leading.setText("");
                leading.setGraphic(null);
                trailing.setText("");
                return;
            }
            cell.setVisible(MFXMenuItem.SEPARATOR != item);

            if (item.disableExpression() != null) cell.disableProperty().bind(item.disableExpression());
            leading.setGraphic(item.icon());
            leading.setText(item.text());
            trailing.setText(item.shortcut() != null ? item.shortcut().toDisplayString() : null);
            if (item.subMenuItems().isEmpty()) {
                trailing.getStyleClass().remove("sub");
                trailing.setGraphic(null);
                trailing.setContentDisplay(ContentDisplay.TEXT_ONLY);
                if (subMenuHandler != null) {
                    subMenuHandler.dispose();
                    subMenuHandler = null;
                }
                item.subMenuItems().addListener(subListener);
            } else {
                trailing.getStyleClass().add("sub");
                MFXFontIcon tIcon = new MFXFontIcon();
                trailing.setGraphic(tIcon);
                trailing.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
                createSubMenuHandler();
            }
        }

        protected void createSubMenuHandler() {
            MFXMenuCell cell = getSkinnable();
            MFXMenuItem item = cell.getItem();
            if (item == null) return;
            subMenuHandler = new SubMenuHandler(cell.getMenu(), item, cell);
            subMenuHandler.setContent(MFXVirtualMenuContent::new);
        }

        protected void scrollToFocus() {
            MFXMenuCell cell = getSkinnable();
            if (!(cell.getContainer() instanceof VFXList<?, ?> list)) return;
            VFXListHelper<?, ?> helper = list.getHelper();
            VFXListState<?, ?> state = list.getState();

            double vMinY = Math.abs(helper.getViewportPosition().y());
            double vMaxY = vMinY + list.getHeight();

            Node focused = state.getCellsByIndexUnmodifiable().values().stream()
                .map(VFXCell::toNode)
                .filter(Node::isFocusVisible)
                .findFirst()
                .orElse(null);
            if (focused == null) return;

            Bounds bp = focused.getBoundsInParent();
            final double scrollDelta;
            if (bp.getMinY() < vMinY) {
                scrollDelta = bp.getMinY() - vMinY;
            } else if (bp.getMaxY() > vMaxY) {
                scrollDelta = bp.getMaxY() - vMaxY;
            } else {
                return;
            }

            if (Animations.isPlaying(focusScrollAnimation))
                focusScrollAnimation.stop();
            focusScrollAnimation = MomentumTransition.fromTime(
                scrollDelta,
                M3Motion.SHORT4.toMillis()
            ).setOnUpdate(list::scrollBy);
            focusScrollAnimation.play();
        }

        //================================================================================
        // Overridden Methods
        //================================================================================
        @Override
        protected void initBehavior(CellBaseBehavior<MFXMenuItem> behavior) {
            super.initBehavior(behavior);
            MFXMenuCell cell = getSkinnable();
            events(
                WhenEvent.intercept(cell, MouseEvent.MOUSE_CLICKED)
                    .condition(e -> e.getButton() == MouseButton.PRIMARY && cell.getItem().action() != null)
                    .process(_ -> {
                        cell.getMenu().getRootMenu().hide();
                        cell.getItem().action().run();
                    }),
                WhenEvent.intercept(cell, KeyEvent.KEY_PRESSED)
                    .process(e -> {
                        MFXMenu menu = cell.getMenu();
                        KeyCode code = e.getCode();
                        switch (code) {
                            case SPACE, ENTER -> {
                                if (subMenuHandler != null) {
                                    if (!subMenuHandler.isShowing()) {
                                        subMenuHandler.show();
                                        subMenuHandler.focus();
                                    }
                                } else {
                                    cell.getItem().action().run();
                                    menu.getRootMenu().hide();
                                }
                                Bounds b = cell.getLayoutBounds();
                                rg.generate(b.getCenterX(), b.getCenterY());
                                rg.release();
                            }
                            case UP -> {
                                cell.requestFocusTraversal(TraversalDirection.UP);
                                scrollToFocus();
                            }
                            case DOWN -> {
                                cell.requestFocusTraversal(TraversalDirection.DOWN);
                                scrollToFocus();
                            }
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
            );
        }

        @Override
        protected void layoutChildren(double x, double y, double w, double h) {
            MFXMenuCell cell = getSkinnable();
            surface.resizeRelocate(0, 0, cell.getWidth(), cell.getHeight());
            rg.resizeRelocate(0, 0, cell.getWidth(), cell.getHeight());
            layoutInArea(leading, x, y, w, h, 0, Insets.EMPTY, HPos.LEFT, VPos.CENTER);
            layoutInArea(trailing, x, y, w, h, 0, Insets.EMPTY, HPos.RIGHT, VPos.CENTER);
        }

        @Override
        public void dispose() {
            MFXMenuCell cell = getSkinnable();
            if (cell.getItem() != null) {
                cell.getItem().subMenuItems().removeListener(subListener);
            }
            subListener = null;
            if (subMenuHandler != null) {
                subMenuHandler.dispose();
                subMenuHandler = null;
            }
            surface.dispose();
            rg.dispose();
            cell.disableProperty().unbind();
            super.dispose();
        }
    }
}
