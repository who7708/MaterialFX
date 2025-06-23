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

package interactive;

import java.util.concurrent.TimeoutException;
import java.util.function.Supplier;

import io.github.palexdev.mfxcore.behavior.BehaviorBase;
import io.github.palexdev.mfxcore.controls.Control;
import io.github.palexdev.mfxcore.controls.Label;
import io.github.palexdev.mfxcore.controls.SkinBase;
import javafx.scene.Scene;
import javafx.scene.control.Skin;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.api.FxRobot;
import org.testfx.api.FxToolkit;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(ApplicationExtension.class)
public class TestCustomControls {

    @Start
    void start(Stage stage) {
        stage.show();
    }

    @Test
    void testCustomControl(FxRobot robot) {
        StackPane pane = setupStage();
        CustomControl control = new CustomControl();

        // The behavior is created with the control
        assertEquals(1, CustomBehavior.instances);
        assertEquals(0, CustomSkin.instances);

        robot.interact(() -> pane.getChildren().add(control));

        assertEquals(1, CustomBehavior.instances);
        assertEquals(1, CustomSkin.instances);
        assertEquals(1, control.getBehavior().initCount);
        Skin<?> skin = control.getSkin();

        robot.interact(() -> {
            pane.getChildren().clear();
            assertNull(control.getScene());
            control.setSkinProvider(() -> new CustomSkin(control) {
                final Label label = new Label("Hello world!");

                {
                    getChildren().setAll(label);
                }
            });
            pane.getChildren().add(control);
        });

        assertNotSame(skin, control.getSkin());
        assertEquals(1, CustomBehavior.instances);
        assertEquals(2, CustomSkin.instances);
        assertEquals(2, control.getBehavior().initCount);
        assertEquals(1, control.getChildrenUnmodifiable().size());

        robot.interact(() ->
            control.setSkinProvider(() -> new CustomSkin(control) {
                final Label label = new Label("Hello custom world!");

                {
                    getChildren().setAll(label);
                }
            })
        );

        assertEquals(1, CustomBehavior.instances);
        assertEquals(3, CustomSkin.instances);
        assertEquals(3, control.getBehavior().initCount);
        assertEquals(1, control.getChildrenUnmodifiable().size());
        Label label = (Label) control.lookup(".label");
        assertEquals("Hello custom world!", label.getText());
    }

    StackPane setupStage() {
        StackPane pane = new StackPane();
        try {
            Scene scene = new Scene(pane, 240, 240);
            FxToolkit.setupStage(s -> s.setScene(scene));
        } catch (TimeoutException e) {
            throw new RuntimeException(e);
        }
        return pane;
    }

    //================================================================================
    // Inner Classes
    //================================================================================
    static class CustomControl extends Control<CustomBehavior> {

        @Override
        public Supplier<CustomBehavior> defaultBehaviorProvider() {
            return () -> new CustomBehavior(this);
        }

        @Override
        public Supplier<SkinBase<?, ?>> defaultSkinProvider() {
            return () -> new CustomSkin(this);
        }
    }

    static class CustomBehavior extends BehaviorBase<CustomControl> {
        public static int instances = 0;
        public int initCount = 0;

        public CustomBehavior(CustomControl node) {
            super(node);
            instances++;
        }

        @Override
        public void init() {
            initCount++;
        }
    }

    static class CustomSkin extends SkinBase<CustomControl, CustomBehavior> {
        public static int instances = 0;

        public CustomSkin(CustomControl control) {
            super(control);
            instances++;
        }
    }
}
