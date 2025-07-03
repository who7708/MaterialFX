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

package io.github.palexdev.mfxcore.utils.fx;

import java.util.Map;
import java.util.Optional;

import io.github.palexdev.mfxcore.base.beans.Position;
import io.github.palexdev.mfxcore.popups.MFXPopup;
import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.stage.Window;


/// This class provides utility methods and handlers ([AnchorHandler]) for positioning a certain target relative to certain
/// `reference bounds`. They can be the bounds of a [Node] on the screen, or the positions and size of a [Window] on the screen.
///
/// Although this was designed to make [MFXPopup] positioning easier, it's generic enough to work with anything.
///
/// All constants from [Pos] are supported except for baseline ones. To get the corresponding handler for the computation,
/// use [#handler(Pos)]. Or you can use one of the offered methods directly.
///
/// @see PositionMode
public class AnchorHandlers {
    //================================================================================
    // Static Properties
    //================================================================================
    private static final Map<Pos, AnchorHandler> HANDLERS = Map.of(
        Pos.TOP_LEFT, AnchorHandlers::topLeft,
        Pos.TOP_CENTER, AnchorHandlers::topCenter,
        Pos.TOP_RIGHT, AnchorHandlers::topRight,
        Pos.CENTER_LEFT, AnchorHandlers::centerLeft,
        Pos.CENTER, AnchorHandlers::center,
        Pos.CENTER_RIGHT, AnchorHandlers::centerRight,
        Pos.BOTTOM_LEFT, AnchorHandlers::bottomLeft,
        Pos.BOTTOM_CENTER, AnchorHandlers::bottomCenter,
        Pos.BOTTOM_RIGHT, AnchorHandlers::bottomRight
    );

    //================================================================================
    // Static Methods
    //================================================================================

    /// @return an [AnchorHandler] appropriate for the given [Pos], or defaults to [Pos#CENTER]
    public static AnchorHandler handler(Pos pos) {
        return HANDLERS.getOrDefault(pos, AnchorHandlers::center);
    }

    public static Position topLeft(Bounds rb, Node content, PositionMode mode) {
        Bounds b = content.getLayoutBounds();
        return switch (mode) {
            case INSIDE -> Position.of(rb.getMinX(), rb.getMinY());
            case ADJACENT -> Position.of(rb.getMinX() - b.getWidth(), rb.getMinY() - b.getHeight());
            case ADJACENT_INWARDS -> Position.of(rb.getMinX(), rb.getMinY() - b.getHeight());
        };
    }

    public static Position topCenter(Bounds rb, Node content, PositionMode mode) {
        Bounds b = content.getLayoutBounds();
        double centerX = rb.getMinX() + (rb.getWidth() - b.getWidth()) / 2.0;
        return switch (mode) {
            case INSIDE -> Position.of(centerX, rb.getMinY());
            case ADJACENT -> Position.of(centerX, rb.getMinY() - b.getHeight());
            case ADJACENT_INWARDS -> Position.of(centerX, rb.getMinY() - b.getHeight());
        };
    }

    public static Position topRight(Bounds rb, Node content, PositionMode mode) {
        Bounds b = content.getLayoutBounds();
        return switch (mode) {
            case INSIDE -> Position.of(rb.getMaxX() - b.getWidth(), rb.getMinY());
            case ADJACENT -> Position.of(rb.getMaxX(), rb.getMinY() - b.getHeight());
            case ADJACENT_INWARDS -> Position.of(rb.getMaxX() - b.getWidth(), rb.getMinY() - b.getHeight());
        };
    }

    public static Position centerLeft(Bounds rb, Node content, PositionMode mode) {
        Bounds b = content.getLayoutBounds();
        double centerY = rb.getMinY() + (rb.getHeight() - b.getHeight()) / 2.0;
        return switch (mode) {
            case INSIDE -> Position.of(rb.getMinX(), centerY);
            case ADJACENT -> Position.of(rb.getMinX() - b.getWidth(), centerY);
            case ADJACENT_INWARDS -> Position.of(rb.getMinX() - b.getWidth(), centerY);
        };
    }

    public static Position center(Bounds rb, Node content, PositionMode mode) {
        Bounds b = content.getLayoutBounds();
        double centerX = rb.getMinX() + (rb.getWidth() - b.getWidth()) / 2.0;
        double centerY = rb.getMinY() + (rb.getHeight() - b.getHeight()) / 2.0;
        return Position.of(centerX, centerY);
    }

    public static Position centerRight(Bounds rb, Node content, PositionMode mode) {
        Bounds b = content.getLayoutBounds();
        double centerY = rb.getMinY() + (rb.getHeight() - b.getHeight()) / 2.0;
        return switch (mode) {
            case INSIDE -> Position.of(rb.getMaxX() - b.getWidth(), centerY);
            case ADJACENT -> Position.of(rb.getMaxX(), centerY);
            case ADJACENT_INWARDS -> Position.of(rb.getMaxX() - b.getWidth(), centerY);
        };
    }

    public static Position bottomLeft(Bounds rb, Node content, PositionMode mode) {
        Bounds b = content.getLayoutBounds();
        return switch (mode) {
            case INSIDE -> Position.of(rb.getMinX(), rb.getMaxY() - b.getHeight());
            case ADJACENT -> Position.of(rb.getMinX(), rb.getMaxY());
            case ADJACENT_INWARDS -> Position.of(rb.getMinX(), rb.getMaxY());
        };
    }

    public static Position bottomCenter(Bounds rb, Node content, PositionMode mode) {
        Bounds b = content.getLayoutBounds();
        double centerX = rb.getMinX() + (rb.getWidth() - b.getWidth()) / 2.0;
        return switch (mode) {
            case INSIDE -> Position.of(centerX, rb.getMaxY() - b.getHeight());
            case ADJACENT -> Position.of(centerX, rb.getMaxY());
            case ADJACENT_INWARDS -> Position.of(centerX, rb.getMaxY());
        };
    }

    public static Position bottomRight(Bounds rb, Node content, PositionMode mode) {
        Bounds b = content.getLayoutBounds();
        return switch (mode) {
            case INSIDE -> Position.of(rb.getMaxX() - b.getWidth(), rb.getMaxY() - b.getHeight());
            case ADJACENT -> Position.of(rb.getMaxX(), rb.getMaxY());
            case ADJACENT_INWARDS -> Position.of(rb.getMaxX() - b.getWidth(), rb.getMaxY());
        };
    }

    //================================================================================
    // Inner Classes
    //================================================================================

    /// This enumeration defines two ways to compute the position:
    /// 1) Inside the `reference bounds`
    /// 2) Outside the `reference bounds`
    ///
    /// The reason for this is that the positioning for dialogs and popovers is slightly different.
    /// - Dialogs can be shown relative to an owner [Window], so if you take the [Pos#BOTTOM_CENTER] for example,
    /// it does not make sense to its window below the owner's bounds. It's more appropriate to show it inside the bounds
    /// while still at the bottom.
    /// - Popovers, on the other hand, are usually shown next to some owner [Node]. For example, the combo box popover is
    /// topically shown below the control, outside its bounds
    public enum PositionMode {
        /// Position target inside the owner bounds (dialogs)
        INSIDE,

        /// Position target adjacent to the owner bounds (e.g., popovers/tooltips)
        ADJACENT,

        /// Position target adjacent to the owner, but facing inward (this affects only the x position)
        ADJACENT_INWARDS
    }


    /// An interface to handle the computation of positions for nodes or windows relative to a reference.
    /// The `AnchorHandler` is responsible for providing the appropriate [Position] based on the given bounds,
    /// content, and position mode.
    public interface AnchorHandler {
        Position compute(Bounds referenceBounds, Node content, PositionMode mode);

        /// Delegates to [#compute(Bounds, Node, PositionMode)] after modifying the given bounds to take into account the
        /// given offset.
        default Position compute(Bounds referenceBounds, Node content, PositionMode mode, Position offset) {
            if (Position.origin().equals(offset)) return compute(referenceBounds, content, mode);
            Bounds shiftedBounds = new BoundingBox(
                referenceBounds.getMinX() + offset.x(),
                referenceBounds.getMinY() + offset.y(),
                referenceBounds.getWidth(),
                referenceBounds.getHeight()
            );
            return compute(shiftedBounds, content, mode);
        }

        /// Delegates to [#compute(Bounds, Node, PositionMode, Position)] after building a [BoundingBox] with the position
        /// and size of the given owner [Window].
        ///
        /// This method prioritizes using the scene root's bounds converted to screen coordinates to get the actual
        /// content area of the window (excluding decorations like title bar and borders).
        /// If the scene or root is unavailable, it falls back to the raw window bounds.
        ///
        /// Uses [PositionMode#INSIDE].
        default Position compute(Window owner, Node content, Position offset) {
            Bounds owBounds = Optional.ofNullable(owner.getScene())
                .map(Scene::getRoot)
                .map(n -> n.localToScreen(n.getLayoutBounds()))
                .orElseGet(() ->
                    new BoundingBox(
                        owner.getX(), owner.getY(),
                        owner.getWidth(), owner.getHeight()
                    )
                );
            return compute(owBounds, content, PositionMode.INSIDE, offset);
        }

        /// Delegates to [#compute(Bounds, Node, PositionMode, Position)] after converting the owner's bounds to screen coordinates.
        ///
        /// Uses [PositionMode#ADJACENT_INWARDS].
        default Position compute(Node owner, Node content, Position offset) {
            Bounds onBounds = owner.localToScreen(owner.getLayoutBounds());
            return compute(onBounds, content, PositionMode.ADJACENT_INWARDS, offset);
        }
    }
}
