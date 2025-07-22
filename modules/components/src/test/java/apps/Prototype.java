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

package apps;

import java.util.Optional;
import java.util.function.Supplier;

import io.github.palexdev.mfxcomponents.controls.MFXButton;
import io.github.palexdev.mfxcomponents.theming.PseudoClasses;
import io.github.palexdev.mfxcomponents.variants.ButtonVariants;
import io.github.palexdev.mfxcore.base.beans.Position;
import io.github.palexdev.mfxcore.builders.InsetsBuilder;
import io.github.palexdev.mfxcore.controls.SkinBase;
import io.github.palexdev.mfxcore.events.WhenEvent;
import io.github.palexdev.mfxcore.popups.MFXPopups;
import io.github.palexdev.mfxcore.popups.PopupState;
import io.github.palexdev.mfxcore.popups.menu.MFXMenu;
import io.github.palexdev.mfxcore.popups.menu.MFXMenuItem;
import io.github.palexdev.mfxcore.utils.fx.AnchorHandlers.Align;
import io.github.palexdev.mfxcore.utils.fx.AnchorHandlers.HAlign;
import io.github.palexdev.mfxcore.utils.fx.AnchorHandlers.VAlign;
import io.github.palexdev.mfxcore.utils.fx.CSSFragment;
import io.github.palexdev.mfxcore.utils.fx.LayoutUtils;
import io.github.palexdev.mfxeffects.enums.ElevationLevel;
import io.github.palexdev.mfxresources.MFXResources;
import io.github.palexdev.virtualizedfx.cells.CellBaseBehavior;
import io.github.palexdev.virtualizedfx.cells.VFXCellBase;
import io.github.palexdev.virtualizedfx.cells.VFXLabeledCellSkin;
import io.github.palexdev.virtualizedfx.cells.VFXSimpleCell;
import io.github.palexdev.virtualizedfx.list.VFXList;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.CacheHint;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import org.scenicview.ScenicView;

import static io.github.palexdev.mfxresources.utils.IconUtils.randomIcon;

public class Prototype extends Application {

    @Override
    public void start(Stage stage) {
        SplitButtonPrototype sbp = new SplitButtonPrototype();

        StackPane root = new StackPane(sbp);

        Scene scene = new Scene(root, 600, 400);
        scene.getStylesheets().addAll(
            MFXResources.load("fonts/Fonts.css"),
            MFXResources.load("sass/themes/material/md-preset-purple.css"),
            MFXResources.load("sass/themes/material/md-theme.css"),
            MFXResources.load("sass/themes/material/motion/md-motion.css")
        );

        CSSFragment.Builder.build()
            .select(".mfx-button.leading")
            .and(".mfx-button.leading > .surface")
            .backgroundRadius("20px 4px 4px 20px")
            .select(".mfx-button.leading:selected")
            .backgroundRadius("20px")
            .select(".mfx-button.trailing")
            .and(".mfx-button.trailing > .surface")
            .backgroundRadius("4px 20px 20px 4px")
            .select(".mfx-button.trailing:selected")
            .backgroundRadius("20px")
            .select(".mfx-button.trailing > .label > .mfx-font-icon")
            .transitionProperty("-fx-rotate")
            .transitionDuration(350)
            .transitionCurve("cubic-bezier(0.27, 1.0, 0.18, 1.0)")
            .select(".mfx-button.trailing:selected > .label > .mfx-font-icon")
            .rotate(180.0)
            .applyOn(scene);

        stage.setScene(scene);
        stage.show();
    }

    static class SplitButtonPrototype extends Region {
        private final MFXButton lead;
        private final MFXButton trail;
        private final ObservableList<MFXMenuItem> items = FXCollections.observableArrayList();

        {
            lead = new MFXButton("Leading", randomIcon("fas-"));
            lead.getStyleClass().add("leading");
            lead.setStyle(ButtonVariants.StyleVariant.FILLED);

            trail = new MFXButton("", randomIcon("fas-")) {
                @Override
                protected void onSelectionChanged(boolean state) {
                    PseudoClasses.SELECTED.setOn(this, state);
                }
            };
            trail.getStyleClass().add("trailing");
            trail.setStyle(ButtonVariants.StyleVariant.FILLED);
            trail.setOnAction(_ -> trail.setSelected(!trail.isSelected()));

            Node graphic = trail.getGraphic();
            graphic.setCacheHint(CacheHint.QUALITY);
            graphic.setCache(true);

            getChildren().setAll(lead, trail);

            Region clip = new Region();
            clip.setBackground(new Background(new BackgroundFill(
                Color.WHITE,
                InsetsBuilder.uniform(999.0).toRadius(false),
                Insets.EMPTY))
            );
            setClip(clip);

            // Popup
            items.addAll(

            );

            // FIXME context menus should be scrollable!
/*            StackPane sp = new StackPane(list.makeScrollable());
            sp.prefWidthProperty().bind(widthProperty());
            sp.setMaxHeight(200.0);

            */

            MFXMenu menu = MFXPopups.menu(cfg -> cfg.triggerButton(MouseButton.PRIMARY))
                .setOffset(Position.of(0.0, 8.0))
                .onState(PopupState.HIDING, (_, _) -> trail.setSelected(false))
                .addMenuItems(
                    new MFXMenuItem(randomIcon("fas-"), "Item 1", null, () -> System.out.println("Item 1 Action")),
                    new MFXMenuItem(randomIcon("fas-"), "Item 2", null, () -> System.out.println("Item 2 Action")),
                    new MFXMenuItem(randomIcon("fas-"), "Item 3", null, () -> System.out.println("Item 3 Action")),
                    new MFXMenuItem(randomIcon("fas-"), "Item 4", null, () -> System.out.println("Item 4 Action")),
                    new MFXMenuItem(randomIcon("fas-"), "Item 5", null, () -> System.out.println("Item 5 Action")),
                    new MFXMenuItem(randomIcon("fas-"), "Item 6", null, () -> System.out.println("Item 6 Action")),
                    new MFXMenuItem(randomIcon("fas-"), "Item 7", null, () -> System.out.println("Item 7 Action")),
                    new MFXMenuItem(randomIcon("fas-"), "Item 8", null, () -> System.out.println("Item 8 Action")),
                    new MFXMenuItem(randomIcon("fas-"), "Item 9", null, () -> System.out.println("Item 9 Action")),
                    new MFXMenuItem(randomIcon("fas-"), "Item 10", null, () -> System.out.println("Item 10 Action"))
                )
                .show(trail, Pos.BOTTOM_RIGHT, Align.of(HAlign.BEFORE, VAlign.BELOW));
            Node content = menu.getRoot();
            content.setPickOnBounds(false);
            content.setEffect(ElevationLevel.LEVEL2.toShadow());

            ScenicView.show(menu.getContent().getParent());

            CSSFragment.Builder.build()
                .select(trail, ".mfx-menu")
                .background("-md-sys-color-surface-container")
                .backgroundRadius("8px")
                .minWidth(120.0)
                .select(trail, ".mfx-menu", ".content")
                .padding("8px")
                .spacing(12.0)
                .select(trail, ".mfx-menu", ".content", ".menu-cell")
                .backgroundRadius("8px")
                .padding("6px")
                .select(trail, ".mfx-menu", ".content", ".menu-cell").states("hover")
                .background("rgba(0, 0, 0, 0.1)")
                .select(trail, ".mfx-menu", ".content", ".menu-cell").states("focus-visible")
                .background("rgba(0, 0, 0, 0.25)")
                .applyOn(trail);

            // Selection
/*            list.getProperties().addListener((InvalidationListener) _ -> {
                MenuItem item = (MenuItem) list.getProperties().get("selected");
                if (item != null) {
                    lead.setText(item.text);
                    Node g = new SnapshotWrapper(item.icon).getGraphic();
                    lead.setGraphic(g);
                    lead.setOnAction(_ -> item.action.run());
                }
            });*/
        }

        @Override
        protected double computePrefWidth(double height) {
            return snappedLeftInset() +
                   LayoutUtils.snappedBoundWidth(lead) +
                   2.0 +
                   LayoutUtils.snappedBoundWidth(trail) +
                   snappedRightInset();
        }

        @Override
        protected double computePrefHeight(double width) {
            return snappedTopInset() +
                   Math.max(
                       LayoutUtils.snappedBoundHeight(lead),
                       LayoutUtils.snappedBoundHeight(trail)
                   ) +
                   snappedBottomInset();
        }

        @Override
        protected double computeMaxWidth(double height) {
            return computePrefWidth(-1);
        }

        @Override
        protected double computeMaxHeight(double width) {
            return computePrefHeight(-1);
        }

        @Override
        protected void layoutChildren() {
            layoutInArea(lead, 0, 0, getWidth(), getHeight(), 0, HPos.LEFT, VPos.CENTER);
            layoutInArea(trail, 0, 0, getWidth(), getHeight(), 0, HPos.RIGHT, VPos.CENTER);

            Node clip = getClip();
            if (clip != null) clip.resizeRelocate(0, 0, getWidth(), getHeight());
        }
    }

    record MenuItem(
        String text,
        Node icon,
        Runnable action
    ) {}

    static class MenuCell extends VFXSimpleCell<MenuItem> {

        public MenuCell(MenuItem item) {
            super(item);
        }

        @Override
        public Supplier<CellBaseBehavior<MenuItem>> defaultBehaviorProvider() {
            return () -> new CellBaseBehavior<>(this) {
                @Override
                public void mouseClicked(MouseEvent e) {
                    if (e.getButton() != MouseButton.PRIMARY) return;
                    Optional.ofNullable(getContainer())
                        .map(c -> ((VFXList<?, ?>) c))
                        .ifPresent(l -> l.getProperties().put("selected", getItem()));
                }
            };
        }

        @Override
        public Supplier<SkinBase<?, ?>> defaultSkinProvider() {
            return () -> new VFXLabeledCellSkin<>(this) {
                @Override
                protected void initBehavior(CellBaseBehavior<MenuItem> behavior) {
                    VFXCellBase<MenuItem> cell = getSkinnable();
                    super.initBehavior(behavior);
                    events(
                        WhenEvent.intercept(cell, MouseEvent.MOUSE_CLICKED)
                            .process(behavior::mouseClicked)
                    );
                }

                @Override
                protected void update() {
                    MenuItem item = getItem();
                    label.setText(item.text);
                    setGraphic(item.icon);
                }
            };
        }
    }
}
