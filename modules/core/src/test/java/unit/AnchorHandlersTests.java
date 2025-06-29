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

package unit;

import io.github.palexdev.mfxcore.base.beans.Position;
import io.github.palexdev.mfxcore.utils.fx.AnchorHandlers;
import io.github.palexdev.mfxcore.utils.fx.AnchorHandlers.PositionMode;
import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.shape.Rectangle;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AnchorHandlersOffsetTest {

    private Bounds referenceBounds;
    private Node content;
    private static final double DELTA = 0.001; // For floating point comparisons

    @BeforeEach
    void setUp() {
        // Reference bounds: 100x100 square at position (50, 50)
        referenceBounds = new BoundingBox(50, 50, 100, 100);

        // Content: 20x20 rectangle 
        content = new Rectangle(20, 20);
    }

    @Nested
    class BasicOffsetTests {

        @Test
        void leftOffsetMovesRight() {
            Insets offset = new Insets(0, 0, 0, 10); // left = 10

            Position withoutOffset = AnchorHandlers.topLeft(referenceBounds, content, PositionMode.INSIDE);
            Position withOffset = AnchorHandlers.handler(Pos.TOP_LEFT)
                .compute(referenceBounds, content, PositionMode.INSIDE, offset);

            assertEquals(withoutOffset.x() + 10, withOffset.x(), DELTA);
            assertEquals(withoutOffset.y(), withOffset.y(), DELTA);
        }

        @Test
        void rightOffsetMovesLeft() {
            Insets offset = new Insets(0, 10, 0, 0); // right = 10

            Position withoutOffset = AnchorHandlers.topLeft(referenceBounds, content, PositionMode.INSIDE);
            Position withOffset = AnchorHandlers.handler(Pos.TOP_LEFT)
                .compute(referenceBounds, content, PositionMode.INSIDE, offset);

            assertEquals(withoutOffset.x() - 10, withOffset.x(), DELTA);
            assertEquals(withoutOffset.y(), withOffset.y(), DELTA);
        }

        @Test
        void topOffsetMovesDown() {
            Insets offset = new Insets(10, 0, 0, 0); // top = 10

            Position withoutOffset = AnchorHandlers.topLeft(referenceBounds, content, PositionMode.INSIDE);
            Position withOffset = AnchorHandlers.handler(Pos.TOP_LEFT)
                .compute(referenceBounds, content, PositionMode.INSIDE, offset);

            assertEquals(withoutOffset.x(), withOffset.x(), DELTA);
            assertEquals(withoutOffset.y() + 10, withOffset.y(), DELTA);
        }

        @Test
        void bottomOffsetMovesUp() {
            Insets offset = new Insets(0, 0, 10, 0); // bottom = 10

            Position withoutOffset = AnchorHandlers.topLeft(referenceBounds, content, PositionMode.INSIDE);
            Position withOffset = AnchorHandlers.handler(Pos.TOP_LEFT)
                .compute(referenceBounds, content, PositionMode.INSIDE, offset);

            assertEquals(withoutOffset.x(), withOffset.x(), DELTA);
            assertEquals(withoutOffset.y() - 10, withOffset.y(), DELTA);
        }
    }

    @Nested
    class CombinedOffsetTests {

        @Test
        void leftAndRightOffsetsCombine() {
            Insets offset = new Insets(0, 5, 0, 15); // left=15, right=5, net dx = 15-5 = 10

            Position withoutOffset = AnchorHandlers.center(referenceBounds, content, PositionMode.INSIDE);
            Position withOffset = AnchorHandlers.handler(Pos.CENTER)
                .compute(referenceBounds, content, PositionMode.INSIDE, offset);

            assertEquals(withoutOffset.x() + 10, withOffset.x(), DELTA);
            assertEquals(withoutOffset.y(), withOffset.y(), DELTA);
        }

        @Test
        void topAndBottomOffsetsCombine() {
            Insets offset = new Insets(12, 0, 7, 0); // top=12, bottom=7, net dy = 12-7 = 5

            Position withoutOffset = AnchorHandlers.center(referenceBounds, content, PositionMode.INSIDE);
            Position withOffset = AnchorHandlers.handler(Pos.CENTER)
                .compute(referenceBounds, content, PositionMode.INSIDE, offset);

            assertEquals(withoutOffset.x(), withOffset.x(), DELTA);
            assertEquals(withoutOffset.y() + 5, withOffset.y(), DELTA);
        }

        @Test
        void allOffsetsCombinetCorrectly() {
            Insets offset = new Insets(20, 8, 5, 12); // top=20, right=8, bottom=5, left=12
            // dx = left - right = 12 - 8 = 4
            // dy = top - bottom = 20 - 5 = 15

            Position withoutOffset = AnchorHandlers.bottomRight(referenceBounds, content, PositionMode.INSIDE);
            Position withOffset = AnchorHandlers.handler(Pos.BOTTOM_RIGHT)
                .compute(referenceBounds, content, PositionMode.INSIDE, offset);

            assertEquals(withoutOffset.x() + 4, withOffset.x(), DELTA);
            assertEquals(withoutOffset.y() + 15, withOffset.y(), DELTA);
        }

        @Test
        void zeroOffsetProducesIdenticalPosition() {
            Insets offset = Insets.EMPTY;

            Position withoutOffset = AnchorHandlers.centerLeft(referenceBounds, content, PositionMode.ADJACENT);
            Position withOffset = AnchorHandlers.handler(Pos.CENTER_LEFT)
                .compute(referenceBounds, content, PositionMode.ADJACENT, offset);

            assertEquals(withoutOffset.x(), withOffset.x(), DELTA);
            assertEquals(withoutOffset.y(), withOffset.y(), DELTA);
        }
    }

    @Nested
    class PositionModeTests {

        @ParameterizedTest
        @EnumSource(PositionMode.class)
        void offsetWorksAcrossPositionModes(PositionMode mode) {
            Insets offset = new Insets(10, 5, 0, 8); // dx = 8-5 = 3, dy = 10-0 = 10

            Position withoutOffset = AnchorHandlers.topCenter(referenceBounds, content, mode);
            Position withOffset = AnchorHandlers.handler(Pos.TOP_CENTER)
                .compute(referenceBounds, content, mode, offset);

            assertEquals(withoutOffset.x() + 3, withOffset.x(), DELTA);
            assertEquals(withoutOffset.y() + 10, withOffset.y(), DELTA);
        }
    }

    @Nested
    class RealisticUseCaseTests {

        @Test
        void tooltipBelowWithMargin() {
            // Tooltip should appear below the reference with 5px margin
            Insets offset = new Insets(5, 0, 0, 0); // 5px down

            Position position = AnchorHandlers.handler(Pos.BOTTOM_CENTER)
                .compute(referenceBounds, content, PositionMode.ADJACENT, offset);

            Position expectedBase = AnchorHandlers.bottomCenter(referenceBounds, content, PositionMode.ADJACENT);
            assertEquals(expectedBase.x(), position.x(), DELTA);
            assertEquals(expectedBase.y() + 5, position.y(), DELTA);
        }

        @Test
        void popupToRightWithOffset() {
            // Popup should appear to the right with 10px spacing
            Insets offset = new Insets(0, 0, 0, 10); // 10px right

            Position position = AnchorHandlers.handler(Pos.CENTER_RIGHT)
                .compute(referenceBounds, content, PositionMode.ADJACENT, offset);

            Position expectedBase = AnchorHandlers.centerRight(referenceBounds, content, PositionMode.ADJACENT);
            assertEquals(expectedBase.x() + 10, position.x(), DELTA);
            assertEquals(expectedBase.y(), position.y(), DELTA);
        }

        @Test
        void dialogInsideWithPadding() {
            // Dialog should be positioned inside with 20px padding from edges
            Insets offset = new Insets(20, 20, 0, 0); // 20px padding from top-right

            Position position = AnchorHandlers.handler(Pos.TOP_RIGHT)
                .compute(referenceBounds, content, PositionMode.INSIDE, offset);

            Position expectedBase = AnchorHandlers.topRight(referenceBounds, content, PositionMode.INSIDE);
            assertEquals(expectedBase.x() - 20, position.x(), DELTA); // right offset moves left
            assertEquals(expectedBase.y() + 20, position.y(), DELTA); // top offset moves down
        }
    }

    @Nested
    class EdgeCaseTests {

        @Test
        void negativeOffsetsWork() {
            // Using negative insets (unusual but should work)
            Insets offset = new Insets(-5, -3, -2, -8);
            // dx = left - right = -8 - (-3) = -5
            // dy = top - bottom = -5 - (-2) = -3

            Position withoutOffset = AnchorHandlers.center(referenceBounds, content, PositionMode.INSIDE);
            Position withOffset = AnchorHandlers.handler(Pos.CENTER)
                .compute(referenceBounds, content, PositionMode.INSIDE, offset);

            assertEquals(withoutOffset.x() - 5, withOffset.x(), DELTA);
            assertEquals(withoutOffset.y() - 3, withOffset.y(), DELTA);
        }

        @Test
        void largeOffsetsWork() {
            Insets offset = new Insets(1000, 500, 200, 800);
            // dx = 800 - 500 = 300
            // dy = 1000 - 200 = 800

            Position withoutOffset = AnchorHandlers.bottomLeft(referenceBounds, content, PositionMode.ADJACENT);
            Position withOffset = AnchorHandlers.handler(Pos.BOTTOM_LEFT)
                .compute(referenceBounds, content, PositionMode.ADJACENT, offset);

            assertEquals(withoutOffset.x() + 300, withOffset.x(), DELTA);
            assertEquals(withoutOffset.y() + 800, withOffset.y(), DELTA);
        }

        @Test
        void fractionalOffsetsWork() {
            Insets offset = new Insets(2.5, 1.3, 0.7, 3.2);
            // dx = 3.2 - 1.3 = 1.9
            // dy = 2.5 - 0.7 = 1.8

            Position withoutOffset = AnchorHandlers.topRight(referenceBounds, content, PositionMode.INSIDE);
            Position withOffset = AnchorHandlers.handler(Pos.TOP_RIGHT)
                .compute(referenceBounds, content, PositionMode.INSIDE, offset);

            assertEquals(withoutOffset.x() + 1.9, withOffset.x(), DELTA);
            assertEquals(withoutOffset.y() + 1.8, withOffset.y(), DELTA);
        }
    }

    @Nested
    class AllPositionTypesTests {

        @ParameterizedTest
        @EnumSource(value = Pos.class, names = {"BASELINE_LEFT", "BASELINE_CENTER", "BASELINE_RIGHT"}, mode = EnumSource.Mode.EXCLUDE)
        void offsetWorksWithAllPositionTypes(Pos pos) {
            Insets offset = new Insets(5, 3, 2, 7); // dx = 7-3 = 4, dy = 5-2 = 3

            Position withoutOffset = AnchorHandlers.handler(pos)
                .compute(referenceBounds, content, PositionMode.INSIDE);
            Position withOffset = AnchorHandlers.handler(pos)
                .compute(referenceBounds, content, PositionMode.INSIDE, offset);

            // All positions should be shifted by the same delta
            assertEquals(withoutOffset.x() + 4, withOffset.x(), DELTA,
                "Position " + pos + " X coordinate not shifted correctly");
            assertEquals(withoutOffset.y() + 3, withOffset.y(), DELTA,
                "Position " + pos + " Y coordinate not shifted correctly");
        }
    }
}