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

package io.github.palexdev.mfxcomponents.skins;

import io.github.palexdev.mfxcomponents.popups.MFXVirtualMenuContent;
import io.github.palexdev.mfxcore.controls.MFXSkinnable;
import io.github.palexdev.mfxcore.controls.SkinBase;
import io.github.palexdev.mfxcore.events.WhenEvent;
import io.github.palexdev.mfxcore.popups.menu.MFXMenuContentBehavior;
import io.github.palexdev.mfxcore.popups.menu.MFXMenuItem;
import io.github.palexdev.mfxcore.utils.fx.LayoutUtils;
import io.github.palexdev.virtualizedfx.cells.base.VFXCell;
import io.github.palexdev.virtualizedfx.controls.VFXScrollPane;
import io.github.palexdev.virtualizedfx.enums.ScrollPaneEnums;
import io.github.palexdev.virtualizedfx.list.VFXList;
import io.github.palexdev.virtualizedfx.utils.ScrollParams;
import javafx.event.Event;
import javafx.geometry.Orientation;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Region;

/// Default skin implementation for [MFXVirtualMenuContent]. There's not much going on here: there's the [VFXList] to show
/// and virtualize the menu items; the [VFXScrollPane] which contains the list and adds scroll capabilities.
/// Both the components also preload their skin for faster init and show ([MFXSkinnable#preloadSkin()]).
public class MFXVirtualMenuContentSkin extends SkinBase<MFXVirtualMenuContent, MFXMenuContentBehavior> {
    //================================================================================
    // Properties
    //================================================================================
    private final VFXScrollPane vsp;
    private final VFXList<MFXMenuItem, VFXCell<MFXMenuItem>> list;

    //================================================================================
    // Constructors
    //================================================================================
    public MFXVirtualMenuContentSkin(MFXVirtualMenuContent vmc) {
        super(vmc);

        // Init
        list = new VFXList<>(vmc.getMenu().getItems(), null);
        list.getCellFactory().bind(vmc.cellFactoryProperty());
        list.spacingProperty().bind(vmc.spacingProperty());
        list.setFocusTraversable(false);

        vsp = new VFXScrollPane(list) {
            @Override
            protected double computePrefHeight(double width) {
                int prefSize = vmc.getVisibleItems();
                int listSize = list.size();
                return (prefSize < 0 ? listSize : Math.min(prefSize, listSize)) * list.getCellSize();
            }
        };
        vsp.setMinSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
        vsp.setFocusTraversable(false);
        vsp.setHBarPolicy(ScrollPaneEnums.ScrollBarPolicy.NEVER);
        ScrollParams.cells(0.75).bind(vsp, Orientation.VERTICAL);

        // Make the popup show as fast as possible the first time by pre-initializing these skins
        list.preloadSkin();
        vsp.preloadSkin();

        // Finalize
        getChildren().setAll(vsp);
    }

    //================================================================================
    // Overridden Methods
    //================================================================================
    @Override
    protected void initBehavior(MFXMenuContentBehavior behavior) {
        super.initBehavior(behavior);
        MFXVirtualMenuContent vmc = getSkinnable();
        events(
            WhenEvent.intercept(vmc, KeyEvent.KEY_PRESSED)
                // Consuming the event here is crucial to avoid JavaFX shenanigans
                .process(e -> behavior.keyPressed(e, Event::consume))
        );
    }

    @Override
    protected double computePrefHeight(double width, double topInset, double rightInset, double bottomInset, double leftInset) {
        return topInset +
               LayoutUtils.snappedBoundHeight(vsp) +
               bottomInset;
    }

    @Override
    protected void layoutChildren(double x, double y, double w, double h) {
        vsp.resizeRelocate(x, y, w, h);
    }

    @Override
    public void dispose() {
        list.getCellFactory().unbind();
        super.dispose();
    }
}
