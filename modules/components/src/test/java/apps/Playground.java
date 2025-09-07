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

import io.github.palexdev.mfxcomponents.controls.MFXButton;
import io.github.palexdev.mfxcomponents.controls.MFXButton.MFXToggleButton;
import io.github.palexdev.mfxcomponents.controls.MFXButtonsGroup;
import io.github.palexdev.mfxcomponents.controls.MFXIconButton;
import io.github.palexdev.mfxcomponents.controls.MFXIconButton.MFXToggleIconButton;
import io.github.palexdev.mfxcomponents.variants.ButtonVariants;
import io.github.palexdev.mfxcomponents.variants.ButtonVariants.StyleVariant;
import io.github.palexdev.mfxcore.enums.SelectionMode;
import io.github.palexdev.mfxresources.MFXResources;
import io.github.palexdev.mfxresources.utils.IconUtils;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class Playground extends Application {

    static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        VBox root = new VBox(30.0);
        root.setAlignment(Pos.CENTER);

        MFXButton sb = new MFXButton("Standard Button").setStyle(StyleVariant.FILLED);
        MFXToggleButton tb = new MFXToggleButton("Toggle Button").setStyle(StyleVariant.FILLED);
        MFXIconButton isb = new MFXIconButton(IconUtils.randomIcon("fas-")).setStyle(StyleVariant.TONAL);
        MFXToggleIconButton tisb = new MFXToggleIconButton(IconUtils.randomIcon("fas-")).setStyle(StyleVariant.TONAL);

        isb.setOnAction(_ -> isb.setIcon(IconUtils.randomIcon("fas-")));

        MFXButtonsGroup group = new MFXButtonsGroup().addButtons(
            "First", IconUtils.randomIcon("fas-"),
            "Second", IconUtils.randomIcon("fas-"),
            "Third", IconUtils.randomIcon("fas-"),
            "Fourth", IconUtils.randomIcon("fas-"),
            "Fifth", IconUtils.randomIcon("fas-")
        );
        group.setGroupType(ButtonVariants.GroupVariant.CONNECTED);
        group.setSelectionMode(SelectionMode.SINGLE);

        root.getChildren().addAll(sb, tb, isb, tisb, group);

        Scene scene = new Scene(root, 400, 600);
        scene.getStylesheets().addAll(
            MFXResources.loadTheme("material/md-preset-blue.css"),
            MFXResources.loadTheme("material/md-theme.css"),
            MFXResources.loadTheme("material/motion/md-motion.css")
        );

        stage.setScene(scene);
        stage.show();
    }
}
