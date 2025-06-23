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

package io.github.palexdev.mfxcore.utils.resize;

import javafx.scene.Cursor;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Region;
import javafx.stage.Stage;

/// Special extension of [RegionDragResizer] which can be used to resize a [Stage] by its content
/// (given that the root of the content is a [Region]).
public class StageResizer extends RegionDragResizer {
    //================================================================================
    // Properties
    //================================================================================
    private Stage stage;

    //================================================================================
    // Constructors
    //================================================================================
    public StageResizer(Region node, Stage stage) {
        super(node);
        this.stage = stage;
        setResizeHandler((n, x, y, w, h) -> resizeRelocateStage(stage, x, y, w, h));
    }

    //================================================================================
    // Methods
    //================================================================================
    protected void resizeRelocateStage(Stage stage, double x, double y, double w, double h) {
        if (!canResize()) return;
        stage.setX(x);
        stage.setY(y);
        stage.setWidth(w);
        stage.setHeight(h);
    }

    protected boolean canResize() {
        return stage.isResizable();
    }

    //================================================================================
    // Overridden Methods
    //================================================================================
    @Override
    protected void handlePressed(MouseEvent event) {
        node.requestFocus();
        clickedX = eventX(event);
        clickedY = eventY(event);
        nodeX = nodeX();
        nodeY = nodeY();
        nodeW = nodeW();
        nodeH = nodeH();
        draggedZone = getZoneByEvent(event);
    }

    @Override
    protected void handleDragged(MouseEvent event) {
        if (node.getCursor() == Cursor.MOVE) return;
        super.handleDragged(event);
    }

    @Override
    protected void handleMoved(MouseEvent event) {
        if (!canResize()) {
            node.setCursor(Cursor.DEFAULT);
            return;
        }
        super.handleMoved(event);
    }

    @Override
    protected double eventX(MouseEvent event) {
        return event.getScreenX();
    }

    @Override
    protected double eventY(MouseEvent event) {
        return event.getScreenY();
    }

    @Override
    protected double nodeX() {
        return stage.getX();
    }

    @Override
    protected double nodeY() {
        return stage.getY();
    }

    @Override
    public void dispose() {
        stage = null;
        super.dispose();
    }
}
