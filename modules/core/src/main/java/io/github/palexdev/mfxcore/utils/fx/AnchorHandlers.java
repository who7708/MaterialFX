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
import javafx.geometry.*;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.stage.Window;

/// This class provides a series of utilities for computing a position relative to a certain anchor, which is represented
/// by a [Pos].
///
/// These are the requirements for the computation:
/// - The bounds of an 'owner' node, also called 'reference bounds'
/// - The bounds of another node which has to be positioned relative to the 'owner', also called 'subject bounds'
/// - The anchor to get the point relative to which compute the position (see example below), represented as a [Pos]
/// - The vertical and horizontal alignment relative to the anchor, represented as an [Align]
///
/// _Example 1:_
/// ```java
/// Button btn = ...;
/// Popup p = ...;
/// // I want the popup to be adjacent to the button, at its right
/// // The anchor is the top right corner of the button
/// Pos anchor = Pos.TOP_RIGHT;
/// // Since I want it to be next to the button, the alignment is:
/// Align align = Align.of(
///     HAlign.AFTER,
///     VAlign.BELOW
///);
///```
///
/// _Example 2:_
/// ```java
/// // Now let's suppose I want to show the menu of a combo box
/// ComboBox combo = ...;
/// Popup p = ...;
/// // Typically, combo boxes' menus are shown below the component and facing inwards:
/// // ┌───────────┐
/// // │   Combo   │
/// // └───────────┘
/// //   ┌─ Popup ─┐
/// //   │         │
/// //   │ ◄────── │
/// //   │ Inwards │
/// //   │         │
/// //   └─────────┘
/// // The anchor is the bottom right corner of the component
/// Pos anchor = Pos.BOTTOM_RIGHT;
/// // The popup is below the anchor and to the left (inwards), so the alignment is:
/// Align align = Align.of(
///     HAlign.BEFORE,
///     VAlign.BELOW
///);
///```
/// It's a bit tricky to understand, but super flexible. Note that, although the examples show how to position a popup
/// relative to a certain owner, the utilities are generic enough to be used with anything.
///
/// The public API to compute a position given those requirements is specified by the [AnchorHandler] interface.
/// The class offers pre-made handlers for each anchor in [Pos]. You can retrieve the related handler with [#handler(Pos)]
/// or use the static methods directly. (Baseline positions are not covered!!)
///
/// The core methods responsible for the x and y computations are [#computeX(Bounds, Bounds, HPos, HAlign)] and
/// [#computeY(Bounds, Bounds, VPos, VAlign)].
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
    // Constructors
    //================================================================================
    private AnchorHandlers() {}
    
    //================================================================================
    // Static Methods
    //================================================================================

    /// @return an [AnchorHandler] appropriate for the given [Pos], or defaults to [Pos#CENTER]
    public static AnchorHandler handler(Pos anchor) {
        return HANDLERS.getOrDefault(anchor, AnchorHandlers::center);
    }

    public static Position topLeft(Bounds refBounds, Bounds subjectBounds, Align alignment) {
        return Position.of(
            computeX(refBounds, subjectBounds, HPos.LEFT, alignment.hAlign()),
            computeY(refBounds, subjectBounds, VPos.TOP, alignment.vAlign())
        );
    }

    public static Position topCenter(Bounds refBounds, Bounds subjectBounds, Align alignment) {
        return Position.of(
            computeX(refBounds, subjectBounds, HPos.CENTER, alignment.hAlign()),
            computeY(refBounds, subjectBounds, VPos.TOP, alignment.vAlign())
        );
    }

    public static Position topRight(Bounds refBounds, Bounds subjectBounds, Align alignment) {
        return Position.of(
            computeX(refBounds, subjectBounds, HPos.RIGHT, alignment.hAlign()),
            computeY(refBounds, subjectBounds, VPos.TOP, alignment.vAlign())
        );
    }

    public static Position centerLeft(Bounds refBounds, Bounds subjectBounds, Align alignment) {
        return Position.of(
            computeX(refBounds, subjectBounds, HPos.LEFT, alignment.hAlign()),
            computeY(refBounds, subjectBounds, VPos.CENTER, alignment.vAlign())
        );
    }

    public static Position center(Bounds refBounds, Bounds subjectBounds, Align alignment) {
        return Position.of(
            computeX(refBounds, subjectBounds, HPos.CENTER, alignment.hAlign()),
            computeY(refBounds, subjectBounds, VPos.CENTER, alignment.vAlign())
        );
    }

    public static Position centerRight(Bounds refBounds, Bounds subjectBounds, Align alignment) {
        return Position.of(
            computeX(refBounds, subjectBounds, HPos.RIGHT, alignment.hAlign()),
            computeY(refBounds, subjectBounds, VPos.CENTER, alignment.vAlign())
        );
    }

    public static Position bottomLeft(Bounds refBounds, Bounds subjectBounds, Align alignment) {
        return Position.of(
            computeX(refBounds, subjectBounds, HPos.LEFT, alignment.hAlign()),
            computeY(refBounds, subjectBounds, VPos.BOTTOM, alignment.vAlign())
        );
    }

    public static Position bottomCenter(Bounds refBounds, Bounds subjectBounds, Align alignment) {
        return Position.of(
            computeX(refBounds, subjectBounds, HPos.CENTER, alignment.hAlign()),
            computeY(refBounds, subjectBounds, VPos.BOTTOM, alignment.vAlign())
        );
    }

    public static Position bottomRight(Bounds refBounds, Bounds subjectBounds, Align alignment) {
        return Position.of(
            computeX(refBounds, subjectBounds, HPos.RIGHT, alignment.hAlign()),
            computeY(refBounds, subjectBounds, VPos.BOTTOM, alignment.vAlign())
        );
    }

    /// Responsible for computing the x position given the: reference bounds, the node bounds, the horizontal anchor and
    /// the horizontal alignment.
    ///
    /// First, it computes the anchor as follows:
    /// - `LEFT -> refBounds.minX`
    /// - `CENTER -> refBounds.centerX`
    /// - `RIGHT -> refBounds.maxX`
    ///
    /// Then computes the final position applying the given alignment to the retrieved anchor position:
    /// - `BEFORE -> anchorX - subjectBounds.width`
    /// - `CENTER -> anchorX - subjectBounds.width / 2`
    /// - `AFTER -> anchorX`
    public static double computeX(Bounds refBounds, Bounds subjectBounds, HPos hAnchor, HAlign hAlign) {
        double anchorX = switch (hAnchor) {
            case LEFT -> refBounds.getMinX();
            case CENTER -> refBounds.getCenterX();
            case RIGHT -> refBounds.getMaxX();
        };

        return switch (hAlign) {
            case BEFORE -> anchorX - subjectBounds.getWidth();
            case CENTER -> anchorX - subjectBounds.getWidth() / 2.0;
            case AFTER -> anchorX;
        };
    }

    /// Responsible for computing the y position given the: reference bounds, the node bounds, the vertical anchor and
    /// the vertical alignment.
    ///
    /// First, it computes the anchor as follows:
    /// - `TOP -> refBounds.minY`
    /// - `CENTER -> refBounds.centerY`
    /// - `BOTTOM -> refBounds.maxY`
    ///
    /// Then computes the final position applying the given alignment to the retrieved anchor position:
    /// - `ABOVE -> anchorY - subjectBounds.height`
    /// - `CENTER -> anchorY - subjectBounds.height / 2`
    /// - `BELOW -> anchorY`
    public static double computeY(Bounds refBounds, Bounds subjectBounds, VPos vAnchor, VAlign vAlign) {
        double anchorY = switch (vAnchor) {
            case TOP -> refBounds.getMinY();
            case CENTER -> refBounds.getCenterY();
            case BASELINE, BOTTOM -> refBounds.getMaxY();
        };

        return switch (vAlign) {
            case ABOVE -> anchorY - subjectBounds.getHeight();
            case CENTER -> anchorY - subjectBounds.getHeight() / 2.0;
            case BELOW -> anchorY;
        };
    }

    //================================================================================
    // Inner Classes
    //================================================================================

    /// Wrapper to represent both horizontal and vertical alignments, defined by [HAlign] and [VAlign] respectively.
    public record Align(
        HAlign hAlign, VAlign vAlign
    ) {
        public static Align of(HAlign hAlign, VAlign vAlign) {
            return new Align(hAlign, vAlign);
        }
    }

    /// Represents the horizontal alignment of some node/bounds relative to something else. In the case of [AnchorHandlers],
    /// relative to a certain anchor point.
    public enum HAlign {
        BEFORE, CENTER, AFTER;
    }

    /// Represents the vertical alignment of some node/bounds relative to something else. In the case of [AnchorHandlers],
    /// relative to a certain anchor point.
    public enum VAlign {
        ABOVE, CENTER, BELOW;

    }

    /// API to compute the position of something relative to the bounds of something else
    /// (the bounds could be of a node, a window or anything else).
    @FunctionalInterface
    public interface AnchorHandler {
        Position compute(Bounds refBounds, Bounds subjectBounds, Align alignment);

        /// Delegates to [#compute(Bounds, Node, Align)] after modifying the given bounds to take into account the
        /// given offset.
        default Position compute(Bounds refBounds, Node node, Align alignment, Position offset) {
            if (!Position.origin().equals(offset)) {
                refBounds = new BoundingBox(
                    refBounds.getMinX() + offset.x(),
                    refBounds.getMinY() + offset.y(),
                    refBounds.getWidth(),
                    refBounds.getHeight()
                );
            }
            return compute(refBounds, node.getLayoutBounds(), alignment);
        }

        /// Delegates to [#compute(Bounds, Node, Align, Position)] after building a [BoundingBox] with the position
        /// and size of the given owner [Window].
        ///
        /// This method prioritizes using the scene root's bounds converted to screen coordinates to get the actual
        /// content area of the window (excluding decorations like title bar and borders).
        /// If the scene or root is unavailable, it falls back to the raw window bounds.
        default Position compute(Window owner, Node content, Align alignment, Position offset) {
            Bounds owBounds = Optional.ofNullable(owner.getScene())
                .map(Scene::getRoot)
                .map(n -> n.localToScreen(n.getLayoutBounds()))
                .orElseGet(() ->
                    new BoundingBox(
                        owner.getX(), owner.getY(),
                        owner.getWidth(), owner.getHeight()
                    )
                );
            return compute(owBounds, content, alignment, offset);
        }

        /// Delegates to [#compute(Bounds, Node, Align, Position)] after converting the owner's bounds to screen coordinates.
        default Position compute(Node owner, Node content, Align alignment, Position offset) {
            Bounds onBounds = owner.localToScreen(owner.getLayoutBounds());
            return compute(onBounds, content, alignment, offset);
        }
    }
}
