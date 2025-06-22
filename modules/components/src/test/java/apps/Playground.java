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

import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

import io.github.palexdev.mfxcomponents.controls.MFXButton;
import io.github.palexdev.mfxcomponents.controls.MFXIconButton;
import io.github.palexdev.mfxcomponents.controls.base.MFXButtonBase;
import io.github.palexdev.mfxcomponents.theming.PseudoClasses;
import io.github.palexdev.mfxcomponents.variants.button.ShapeVariant;
import io.github.palexdev.mfxcomponents.variants.button.SizeVariant;
import io.github.palexdev.mfxcomponents.variants.button.StyleVariant;
import io.github.palexdev.mfxcomponents.variants.button.WidthVariant;
import io.github.palexdev.mfxcore.base.TriFunction;
import io.github.palexdev.mfxcore.builders.InsetsBuilder;
import io.github.palexdev.mfxcore.utils.fx.CSSFragment;
import io.github.palexdev.mfxresources.MFXResources;
import io.github.palexdev.mfxresources.fonts.MFXFontIcon;
import io.github.palexdev.mfxresources.fonts.fontawesome.FontAwesomeSolid;
import javafx.application.Application;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class Playground extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        StackPane root = new StackPane(createTestGrid());
        root.setPadding(InsetsBuilder.uniform(48).get());

        Scene scene = new Scene(root, 800, 600);
        scene.getStylesheets().addAll(
            MFXResources.load("fonts/Fonts.css"),
            MFXResources.load("sass/themes/material/md-preset-purple.css"),
            MFXResources.load("sass/themes/material/md-theme.css"),
            MFXResources.load("sass/themes/material/motion/md-motion.css")
        );
        stage.setScene(scene);
        stage.show();
    }

    Parent createTest() {
        boolean toggles = false;
        StyleVariant style = StyleVariant.ELEVATED;
        ShapeVariant shape = ShapeVariant.ROUNDED;

        String[] text = new String[]{"Enabled", "Disabled", "Hovered", "Focused", "Pressed"};
        String[] states = new String[]{"", "disabled", "hover", "focus-visible", "pressed"};

        Function<Consumer<HBox>, HBox> generator = cfg -> {
            HBox box = new HBox(20);
            box.setAlignment(Pos.CENTER);
            box.setPadding(InsetsBuilder.uniform(48.0).get());
            cfg.accept(box);
            for (int i = 0; i < 5; i++) {
                MFXButton btn = new MFXButton(text[i], FontAwesomeSolid.random());
                btn.setToggleable(toggles);
                btn.setStyle(style);
                btn.setShape(shape);
                if (!states[i].isEmpty()) {
                    PseudoClasses.setOn(btn, states[i].toLowerCase(), true);
                    btn.setFocusTraversable(false);
                    btn.setMouseTransparent(true);
                }
                box.getChildren().add(btn);
            }
            return box;
        };

        HBox top = generator.apply(box ->
            box.setStyle("""
                -fx-background-color: -md-sys-color-background;
                -fx-background-radius: 24px 24px 0px 0px;
                -fx-border-color: -md-sys-color-outline -md-sys-color-outline transparent -md-sys-color-outline;
                -fx-border-insets: -1px;
                -fx-border-radius: 24px 24px 0px 0px;
                """)
        );
        HBox bottom = generator.apply(box -> {
            box.setStyle("""
                -fx-background-color: -md-sys-color-background;
                -fx-background-radius: 0px 0px 24px 24px;
                -fx-border-color: transparent -md-sys-color-outline -md-sys-color-outline -md-sys-color-outline;
                -fx-border-insets: -1px;
                -fx-border-radius: 0px 0px 24px 24px;
                """);
            box.getStyleClass().add("root");
            PseudoClasses.setOn(box, "dark", true);
        });

        VBox box = new VBox(top, bottom);
        box.setAlignment(Pos.CENTER);
        box.setPadding(InsetsBuilder.uniform(24.0).get());
        return box;
    }

    Parent createTestGrid() {
        boolean toggles = true;
        boolean dark = false;
        ShapeVariant shape = ShapeVariant.ROUNDED;
        String[] text = new String[]{"Enabled", "Disabled", "Hovered", "Focused", "Pressed"};
        String[] states = new String[]{"", "disabled", "hover", "focus-visible", "pressed"};
        TriFunction<String, String, StyleVariant, MFXButtonBase<?>> builder = (t, s, v) -> {
            MFXButton btn = new MFXIconButton(FontAwesomeSolid.random());
            btn.setToggleable(toggles);
            btn.setStyle(v);
            btn.setShape(shape);
            btn.setSize(SizeVariant.S);

            if (!s.isEmpty()) {
                PseudoClasses.setOn(btn, s, true);
                btn.setFocusTraversable(false);
                btn.setMouseTransparent(true);
            }
            return btn;
        };

        HBox hbox = new HBox(40);
        hbox.setAlignment(Pos.CENTER);
        hbox.setPadding(InsetsBuilder.uniform(24.0).get());
        hbox.setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
        if (dark) {
            hbox.getStyleClass().add("root");
            PseudoClasses.setOn(hbox, "dark", true);
        }
        hbox.setStyle("""
            -fx-background-color: -md-sys-color-background;
            -fx-background-radius: 24px;
            -fx-border-color: -md-sys-color-outline;
            -fx-border-insets: -1px;
            -fx-border-radius: 24px;
            """);

        for (int i = 0; i < 5; i++) {
            VBox column = new VBox(20);
            column.setAlignment(Pos.CENTER);
            for (int j = 0; j < 5; j++) {
                MFXButtonBase<?> btn = builder.apply(text[j], states[j], StyleVariant.values()[i]);
                column.getChildren().add(btn);
            }
            hbox.getChildren().add(column);
        }
        return hbox;
    }

    Parent createMeasurementsGrid() {
        boolean toggles = false;
        ShapeVariant shape = ShapeVariant.SQUARED;
        BiFunction<SizeVariant, WidthVariant, MFXIconButton> builder = (s, w) -> {
            MFXIconButton btn = new MFXIconButton(new MFXFontIcon(FontAwesomeSolid.PENCIL));
            btn.setToggleable(toggles);
            btn.setShape(shape);
            btn.setSize(s);
            btn.setWidth(w);
            return btn;
        };

        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(40);
        grid.setVgap(40);

        CSSFragment.Builder.build()
            .select(grid)
            .border("-md-sys-color-outline")
            .borderRadius(InsetsBuilder.uniform(24))
            .background("-md-sys-color-background")
            .backgroundRadius(InsetsBuilder.uniform(24))
            .padding(InsetsBuilder.top(24).withBottom(24))
            .applyOn(grid);

        for (int i = 0; i < 5; i++) { // Rows
            for (int j = 0; j < 3; j++) { // Columns
                MFXIconButton btn = builder.apply(SizeVariant.values()[i], WidthVariant.values()[j]);
                GridPane.setHalignment(btn, HPos.CENTER);
                GridPane.setValignment(btn, VPos.CENTER);
                grid.add(btn, j, i);
            }
        }

        return grid;
    }
}
