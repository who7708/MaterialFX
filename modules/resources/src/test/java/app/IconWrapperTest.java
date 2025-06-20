/*
 * Copyright (C) 2023 Parisi Alessandro - alessandro.parisi406@gmail.com
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

package app;

import io.github.palexdev.mfxeffects.ripple.MFXRippleGenerator;
import io.github.palexdev.mfxresources.fonts.*;
import io.github.palexdev.mfxresources.fonts.fontawesome.FontAwesomeBrands;
import io.github.palexdev.mfxresources.fonts.fontawesome.FontAwesomeRegular;
import io.github.palexdev.mfxresources.fonts.fontawesome.FontAwesomeSolid;
import io.github.palexdev.mfxresources.utils.EnumUtils;
import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class IconWrapperTest extends Application {

    @Override
    public void start(Stage primaryStage) {
        StackPane pane = new StackPane();

        // Set up like this, the ripple is in front of icon
        // But the set view order should fix it
        // Expect the effect behind the icon
        MFXIconWrapper icon = new MFXIconWrapper(FontAwesomeSolid.random(64.0), 128)
            .setAnimated(true)
            .setAnimationProvider(AnimationPresets.FADE)
            .enableRipple(true)
            .setIconClip(IconClip.of(IconClip.ClipShape.ROUNDED, -1.0));
        icon.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> {
            IconsProviders provider = EnumUtils.randomEnum(IconsProviders.class);
            Object desc = switch (provider) {
                case FONTAWESOME_BRANDS -> FontAwesomeBrands.class;
                case FONTAWESOME_SOLID -> FontAwesomeSolid.class;
                case FONTAWESOME_REGULAR -> FontAwesomeRegular.class;
            };
            Enum val = EnumUtils.randomEnum(((Class<Enum>) desc));
            IconDescriptor toDesc = (IconDescriptor) val;
            // This is to also test icon switch with CSS
            icon.setStyle("-mfx-icon: " + toDesc.getDescription());
        });
        icon.iconProperty().addListener(i -> icon.getIcon().setSize(64.0));

        // Check
        ObservableList<Node> children = icon.getChildrenUnmodifiable();
        assert children.get(0) instanceof MFXFontIcon;
        assert children.get(1) instanceof MFXRippleGenerator;

        pane.getChildren().add(icon);
        Scene scene = new Scene(pane, 400, 400);
        primaryStage.setScene(scene);
        primaryStage.show();
        //ScenicView.show(scene);
    }
}
