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

import app.others.ui.*;
import fr.brouillard.oss.cssfx.CSSFX;
import io.github.palexdev.mfxcomponents.controls.buttons.MFXButton;
import io.github.palexdev.mfxcomponents.controls.buttons.MFXIconButton;
import io.github.palexdev.mfxcomponents.controls.buttons.MFXSegmentedButton;
import io.github.palexdev.mfxcomponents.controls.checkbox.MFXCheckbox;
import io.github.palexdev.mfxcomponents.controls.checkbox.TriState;
import io.github.palexdev.mfxcomponents.controls.fab.MFXFab;
import io.github.palexdev.mfxcomponents.theming.MaterialThemes;
import io.github.palexdev.mfxcomponents.theming.enums.FABVariants;
import io.github.palexdev.mfxcomponents.theming.enums.IconButtonVariants;
import io.github.palexdev.mfxcomponents.theming.enums.PseudoClasses;
import io.github.palexdev.mfxcore.base.beans.Size;
import io.github.palexdev.mfxcore.builders.InsetsBuilder;
import io.github.palexdev.mfxcore.enums.SelectionMode;
import io.github.palexdev.mfxcore.selection.SelectionGroup;
import io.github.palexdev.mfxcore.utils.fx.CSSFragment;
import io.github.palexdev.mfxresources.fonts.MFXFontIcon;
import io.github.palexdev.mfxresources.fonts.fontawesome.FontAwesomeSolid;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Supplier;
import javafx.application.Application;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.css.PseudoClass;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ContentDisplay;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.scenicview.ScenicView;

public class Showcase extends Application implements MultipleViewApp<String> {
    //================================================================================
    // Properties
    //================================================================================
    private final ViewSwitcher<String> switcher = new ViewSwitcher<>();
    private final StringProperty themeVariant = new SimpleStringProperty("light");

    //================================================================================
    // Overridden Methods
    //================================================================================
    @Override
    public void start(Stage stage) {
        CSSFX.start();
        registerViews();

        MFXFab themeSwitcher = new MFXFab().lowered();
        MFXFontIcon icon = new MFXFontIcon("fas-moon");
        themeSwitcher.setExtended(true);
        themeSwitcher.textProperty().bind(themeVariant.map(s -> s.equals("light") ? "Dark" : "Light"));
        themeSwitcher.setIcon(icon);

        BorderPane root = new BorderPane();
        ComboBox<String> header = new ComboBox<>(FXCollections.observableArrayList(switcher.views().keySet()));
        header.valueProperty().addListener((observable, oldValue, newValue) -> root.setCenter(switcher.load(newValue)));
        root.setTop(header);
        BorderPane.setAlignment(header, Pos.CENTER);
        BorderPane.setMargin(header, new Insets(30, 0, 60, 0));
        root.getStyleClass().add("container");

        header.getSelectionModel().selectFirst();

        ScrollPane sp = new ScrollPane(root) {
            @Override
            protected void layoutChildren() {
                super.layoutChildren();
                layoutInArea(themeSwitcher,
                    getLayoutX(), getLayoutY(),
                    getWidth(), getHeight(), 0,
                    InsetsBuilder.of(0, 24, 16, 0), HPos.RIGHT, VPos.BOTTOM
                );
            }
        };
        sp.setFitToWidth(true);
        sp.setFitToHeight(true);
        CSSFragment.Builder.build()
            .select(".scroll-pane, .scroll-pane .viewport")
            .style("-fx-background-color: transparent")
            .applyOn(sp);

        Size ws = UIUtils.getWindowSize();
        Scene scene = new Scene(new StackPane(sp), ws.getWidth(), ws.getHeight());
        loadStyleSheet(scene);
        stage.setScene(scene);
        stage.setTitle("Buttons Playground");
        stage.show();

        themeSwitcher.setOnAction(e -> {
            String newVariant = themeVariant.get().equals("light") ? "dark" : "light";
            String iconDesc = themeVariant.get().equals("light") ? "fas-sun" : "fas-moon";
            themeVariant.set(newVariant);
            loadStyleSheet(scene);
            themeSwitcher.setIcon(new MFXFontIcon(iconDesc));
        });
        sp.getChildren().add(themeSwitcher);

        ScenicView.show(scene);

        stage.addEventFilter(MouseEvent.MOUSE_CLICKED, e -> {
            if (e.getButton() == MouseButton.SECONDARY) {
                String override = """
                    .root {
                      /*! Palette */
                      -md-source: #4CAF50;
                      -md-ref-palette-primary0: #000000;
                      -md-ref-palette-primary4: #001202;
                      -md-ref-palette-primary5: #001502;
                      -md-ref-palette-primary6: #001802;
                      -md-ref-palette-primary10: #002204;
                      -md-ref-palette-primary12: #002605;
                      -md-ref-palette-primary15: #002D07;
                      -md-ref-palette-primary17: #023209;
                      -md-ref-palette-primary20: #0A390F;
                      -md-ref-palette-primary24: #154217;
                      -md-ref-palette-primary25: #174419;
                      -md-ref-palette-primary30: #235024;
                      -md-ref-palette-primary35: #2F5C2F;
                      -md-ref-palette-primary40: #3B6939;
                      -md-ref-palette-primary50: #538250;
                      -md-ref-palette-primary60: #6C9C68;
                      -md-ref-palette-primary70: #86B880;
                      -md-ref-palette-primary80: #A1D39A;
                      -md-ref-palette-primary87: #B4E7AC;
                      -md-ref-palette-primary90: #BCF0B4;
                      -md-ref-palette-primary92: #C2F6B9;
                      -md-ref-palette-primary94: #C7FCBF;
                      -md-ref-palette-primary95: #CAFEC2;
                      -md-ref-palette-primary96: #D5FFCC;
                      -md-ref-palette-primary98: #ECFFE4;
                      -md-ref-palette-primary99: #F6FFF0;
                      -md-ref-palette-primary100: #FFFFFF;
                      -md-ref-palette-secondary0: #000000;
                      -md-ref-palette-secondary4: #041104;
                      -md-ref-palette-secondary5: #061406;
                      -md-ref-palette-secondary6: #081708;
                      -md-ref-palette-secondary10: #111F0F;
                      -md-ref-palette-secondary12: #152313;
                      -md-ref-palette-secondary15: #1B2919;
                      -md-ref-palette-secondary17: #1F2E1D;
                      -md-ref-palette-secondary20: #253423;
                      -md-ref-palette-secondary24: #2E3D2B;
                      -md-ref-palette-secondary25: #303F2E;
                      -md-ref-palette-secondary30: #3B4B38;
                      -md-ref-palette-secondary35: #475743;
                      -md-ref-palette-secondary40: #52634F;
                      -md-ref-palette-secondary50: #6B7C67;
                      -md-ref-palette-secondary60: #84967F;
                      -md-ref-palette-secondary70: #9EB099;
                      -md-ref-palette-secondary80: #BACCB3;
                      -md-ref-palette-secondary87: #CDDFC6;
                      -md-ref-palette-secondary90: #D5E8CF;
                      -md-ref-palette-secondary92: #DBEED4;
                      -md-ref-palette-secondary94: #E1F3DA;
                      -md-ref-palette-secondary95: #E4F6DC;
                      -md-ref-palette-secondary96: #E6F9DF;
                      -md-ref-palette-secondary98: #ECFFE5;
                      -md-ref-palette-secondary99: #F6FFF0;
                      -md-ref-palette-secondary100: #FFFFFF;
                      -md-ref-palette-tertiary0: #000000;
                      -md-ref-palette-tertiary4: #001113;
                      -md-ref-palette-tertiary5: #001416;
                      -md-ref-palette-tertiary6: #001719;
                      -md-ref-palette-tertiary10: #002023;
                      -md-ref-palette-tertiary12: #002427;
                      -md-ref-palette-tertiary15: #002B2F;
                      -md-ref-palette-tertiary17: #002F34;
                      -md-ref-palette-tertiary20: #00363B;
                      -md-ref-palette-tertiary24: #0C3F44;
                      -md-ref-palette-tertiary25: #104247;
                      -md-ref-palette-tertiary30: #1F4D52;
                      -md-ref-palette-tertiary35: #2C595E;
                      -md-ref-palette-tertiary40: #38656A;
                      -md-ref-palette-tertiary50: #527E83;
                      -md-ref-palette-tertiary60: #6B989E;
                      -md-ref-palette-tertiary70: #86B3B8;
                      -md-ref-palette-tertiary80: #A0CFD4;
                      -md-ref-palette-tertiary87: #B4E2E8;
                      -md-ref-palette-tertiary90: #BCEBF0;
                      -md-ref-palette-tertiary92: #C2F1F6;
                      -md-ref-palette-tertiary94: #C7F6FC;
                      -md-ref-palette-tertiary95: #CAF9FF;
                      -md-ref-palette-tertiary96: #D5FAFF;
                      -md-ref-palette-tertiary98: #EAFDFF;
                      -md-ref-palette-tertiary99: #F5FEFF;
                      -md-ref-palette-tertiary100: #FFFFFF;
                      -md-ref-palette-error0: #000000;
                      -md-ref-palette-error4: #280001;
                      -md-ref-palette-error5: #2D0001;
                      -md-ref-palette-error6: #310001;
                      -md-ref-palette-error10: #410002;
                      -md-ref-palette-error12: #490002;
                      -md-ref-palette-error15: #540003;
                      -md-ref-palette-error17: #5C0004;
                      -md-ref-palette-error20: #690005;
                      -md-ref-palette-error24: #790006;
                      -md-ref-palette-error25: #7E0007;
                      -md-ref-palette-error30: #93000A;
                      -md-ref-palette-error35: #A80710;
                      -md-ref-palette-error40: #BA1A1A;
                      -md-ref-palette-error50: #DE3730;
                      -md-ref-palette-error60: #FF5449;
                      -md-ref-palette-error70: #FF897D;
                      -md-ref-palette-error80: #FFB4AB;
                      -md-ref-palette-error87: #FFCFC9;
                      -md-ref-palette-error90: #FFDAD6;
                      -md-ref-palette-error92: #FFE2DE;
                      -md-ref-palette-error94: #FFE9E6;
                      -md-ref-palette-error95: #FFEDEA;
                      -md-ref-palette-error96: #FFF0EE;
                      -md-ref-palette-error98: #FFF8F7;
                      -md-ref-palette-error99: #FFFBFF;
                      -md-ref-palette-error100: #FFFFFF;
                      -md-ref-palette-neutral0: #000000;
                      -md-ref-palette-neutral4: #0B0F0A;
                      -md-ref-palette-neutral5: #0E120D;
                      -md-ref-palette-neutral6: #10140F;
                      -md-ref-palette-neutral10: #191D17;
                      -md-ref-palette-neutral12: #1D211B;
                      -md-ref-palette-neutral15: #232721;
                      -md-ref-palette-neutral17: #272B25;
                      -md-ref-palette-neutral20: #2D322C;
                      -md-ref-palette-neutral24: #363A34;
                      -md-ref-palette-neutral25: #383D36;
                      -md-ref-palette-neutral30: #444841;
                      -md-ref-palette-neutral35: #4F544D;
                      -md-ref-palette-neutral40: #5B6059;
                      -md-ref-palette-neutral50: #747871;
                      -md-ref-palette-neutral60: #8E928A;
                      -md-ref-palette-neutral70: #A9ACA4;
                      -md-ref-palette-neutral80: #C4C8BF;
                      -md-ref-palette-neutral87: #D8DBD2;
                      -md-ref-palette-neutral90: #E0E4DB;
                      -md-ref-palette-neutral92: #E6E9E0;
                      -md-ref-palette-neutral94: #ECEFE6;
                      -md-ref-palette-neutral95: #EFF2E9;
                      -md-ref-palette-neutral96: #F1F5EC;
                      -md-ref-palette-neutral98: #F7FBF1;
                      -md-ref-palette-neutral99: #FAFDF4;
                      -md-ref-palette-neutral100: #FFFFFF;
                      -md-ref-palette-neutral-variant0: #000000;
                      -md-ref-palette-neutral-variant4: #0A1009;
                      -md-ref-palette-neutral-variant5: #0D120C;
                      -md-ref-palette-neutral-variant6: #0F150E;
                      -md-ref-palette-neutral-variant10: #171D16;
                      -md-ref-palette-neutral-variant12: #1B211A;
                      -md-ref-palette-neutral-variant15: #212720;
                      -md-ref-palette-neutral-variant17: #252C24;
                      -md-ref-palette-neutral-variant20: #2C322A;
                      -md-ref-palette-neutral-variant24: #353B32;
                      -md-ref-palette-neutral-variant25: #373D35;
                      -md-ref-palette-neutral-variant30: #424940;
                      -md-ref-palette-neutral-variant35: #4E544B;
                      -md-ref-palette-neutral-variant40: #5A6057;
                      -md-ref-palette-neutral-variant50: #72796F;
                      -md-ref-palette-neutral-variant60: #8C9388;
                      -md-ref-palette-neutral-variant70: #A7ADA2;
                      -md-ref-palette-neutral-variant80: #C2C9BD;
                      -md-ref-palette-neutral-variant87: #D6DCD0;
                      -md-ref-palette-neutral-variant90: #DEE5D8;
                      -md-ref-palette-neutral-variant92: #E4EADE;
                      -md-ref-palette-neutral-variant94: #EAF0E4;
                      -md-ref-palette-neutral-variant95: #ECF3E6;
                      -md-ref-palette-neutral-variant96: #EFF6E9;
                      -md-ref-palette-neutral-variant98: #F5FBEF;
                      -md-ref-palette-neutral-variant99: #F8FEF2;
                      -md-ref-palette-neutral-variant100: #FFFFFF;
                      /*! Scheme */
                      -md-sys-color-primary: #3B6939;
                      -md-sys-color-primary-container: #BCF0B4;
                      -md-sys-color-on-primary: #FFFFFF;
                      -md-sys-color-on-primary-container: #002204;
                      -md-sys-color-inverse-primary: #A1D39A;
                      -md-sys-color-secondary: #52634F;
                      -md-sys-color-secondary-container: #D5E8CF;
                      -md-sys-color-on-secondary: #FFFFFF;
                      -md-sys-color-on-secondary-container: #111F0F;
                      -md-sys-color-tertiary: #38656A;
                      -md-sys-color-tertiary-container: #BCEBF0;
                      -md-sys-color-on-tertiary: #FFFFFF;
                      -md-sys-color-on-tertiary-container: #002023;
                      -md-sys-color-error: #BA1A1A;
                      -md-sys-color-error-container: #FFDAD6;
                      -md-sys-color-on-error: #FFFFFF;
                      -md-sys-color-on-error-container: #410002;
                      -md-sys-color-surface: #F7FBF1;
                      -md-sys-color-surface-dim: #D8DBD2;
                      -md-sys-color-surface-bright: #F7FBF1;
                      -md-sys-color-surface-container-lowest: #FFFFFF;
                      -md-sys-color-surface-container-low: #F1F5EC;
                      -md-sys-color-surface-container: #ECEFE6;
                      -md-sys-color-surface-container-high: #E6E9E0;
                      -md-sys-color-surface-container-highest: #E0E4DB;
                      -md-sys-color-surface-variant: #DEE5D8;
                      -md-sys-color-on-surface: #191D17;
                      -md-sys-color-on-surface-variant: #424940;
                      -md-sys-color-inverse-surface: #2D322C;
                      -md-sys-color-inverse-on-surface: #EFF2E9;
                      -md-sys-color-surface-tint: #3B6939;
                      -md-sys-color-background: #F7FBF1;
                      -md-sys-color-on-background: #191D17;
                      -md-sys-color-outline: #72796F;
                      -md-sys-color-outline-variant: #C2C9BD;
                      -md-sys-color-shadow: #000000;
                      -md-sys-color-scrim: #000000
                    }
                    """;
                CSSFragment frag = new CSSFragment(override);
                scene.getStylesheets().add(frag.toDataUri());
            }
        });
    }

    @Override
    public void registerViews() {
        switcher.register(defaultView(), s -> ebView());
        switcher.register("filled-buttons", s -> fbView());
        switcher.register("tonal-filled-buttons", s -> tfbView());
        switcher.register("outlined-buttons", s -> obView());
        switcher.register("text-buttons", s -> tbView());
        switcher.register("fabs", s -> fabView());
        switcher.register("extended-fabs", s -> extendedFabView());
        switcher.register("icon-buttons", s -> iconButtonsView());
        switcher.register("segmented-buttons", s -> segmentedButtonsView());
        switcher.register("checkboxes", s -> checkBoxesView());
    }

    @Override
    public String defaultView() {
        return "elevated-buttons";
    }

    @Override
    public List<String> getStylesheet() {
        String base = ComponentsLauncher.load("AppBase.css");
        String theme = themeVariant.get().equals("light") ?
            MaterialThemes.PURPLE_LIGHT.getUrl().toExternalForm() :
            MaterialThemes.PURPLE_DARK.getUrl().toExternalForm();
        return List.of(base, theme);
    }

    //================================================================================
    // Methods
    //================================================================================

    private Node ebView() {
        return createButtonsView("Elevated Buttons", MFXButton::new);
    }

    private Node fbView() {
        return createButtonsView("Filled Buttons", (s, node) -> new MFXButton(s, node).filled());
    }

    private Node tfbView() {
        return createButtonsView("Tonal Filled Buttons", (s, node) -> new MFXButton(s, node).tonal());
    }

    private Node obView() {
        return createButtonsView("Outlined Buttons", (s, node) -> new MFXButton(s, node).outlined());
    }

    private Node tbView() {
        return createButtonsView("Text Buttons", 600, (s, node) -> new MFXButton(s, node).text());
    }

    private Node fabView() {
        VBox box = new VBox(50);
        box.setAlignment(Pos.TOP_CENTER);
        box.setPadding(InsetsBuilder.uniform(10).get());
        Node def = createFabsView("Floating Action Buttons", (s, i) -> new MFXFab(i));
        Node surf = createFabsView("Floating Action Buttons (Surface)", (s, i) -> new MFXFab(i).setVariants(FABVariants.SURFACE));
        Node sdy = createFabsView("Floating Action Buttons (Secondary)", (s, i) -> new MFXFab(i).setVariants(FABVariants.SECONDARY));
        Node tty = createFabsView("Floating Action Buttons (Tertiary)", (s, i) -> new MFXFab(i).setVariants(FABVariants.TERTIARY));
        box.getChildren().addAll(def, surf, sdy, tty);
        return box;
    }

    private Node extendedFabView() {
        BiFunction<String, MFXFontIcon, MFXFab> generator = (s, i) -> {
            MFXFab fab = new MFXFab().extended();
            fab.setText(s);
            fab.setIcon(i);
            return fab;
        };
        VBox box = new VBox(50);
        box.setAlignment(Pos.TOP_CENTER);
        box.setPadding(InsetsBuilder.uniform(10).get());
        Node def = createExtendedFabView("Extended FABs", generator);
        Node surf = createExtendedFabView("Extended FABs (Surface)", generator.andThen(f -> f.setVariants(FABVariants.SURFACE)));
        Node sdy = createExtendedFabView("Extended FABs (Secondary)", generator.andThen(f -> f.setVariants(FABVariants.SECONDARY)));
        Node tty = createExtendedFabView("Extended FABs (Tertiary)", generator.andThen(f -> f.setVariants(FABVariants.TERTIARY)));
        box.getChildren().addAll(def, surf, sdy, tty);
        return box;
    }

    private Node iconButtonsView() {
        BiFunction<Boolean, MFXFontIcon, MFXIconButton> generator = (s, i) -> {
            MFXIconButton btn = new MFXIconButton(i);
            btn.setSelectable(s);
            return btn;
        };

        VBox box = new VBox(50);
        box.setAlignment(Pos.TOP_CENTER);
        box.setPadding(InsetsBuilder.uniform(10).get());
        Node standard = createIconButtonsView("Standard IconButtons", generator);
        Node filled = createIconButtonsView("Filled IconButtons", generator.andThen(b -> b.addVariants(IconButtonVariants.FILLED)));
        Node filledTonal = createIconButtonsView("Filled Tonal IconButtons", generator.andThen(b -> b.addVariants(IconButtonVariants.FILLED_TONAL)));
        Node outlined = createIconButtonsView("Outlined IconButtons", generator.andThen(b -> b.addVariants(IconButtonVariants.OUTLINED)));
        box.getChildren().addAll(standard, filled, filledTonal, outlined);
        return box;
    }

    private Node segmentedButtonsView() {
        return createSegmentedButtonsView("Segmented Buttons");
    }

    private Node checkBoxesView() {
        return createCheckboxesView("Checkboxes");
    }

    // Creators
    private Node createButtonsView(String title, BiFunction<String, Node, MFXButton> generator) {
        return createButtonsView(title, 700, generator);
    }

    private Node createButtonsView(String title, double length, BiFunction<String, Node, MFXButton> generator) {
        TitledFlowPane tfp = new TitledFlowPane(title);
        tfp.setMaxWidth(length);

        MFXButton btn0 = generator.apply("Enabled", null);
        MFXButton btn1 = generator.apply("Disabled", null);
        MFXButton btn2 = generator.apply("Hovered", null);
        MFXButton btn3 = generator.apply("Focused", null);
        MFXButton btn4 = generator.apply("Pressed", null);
        MFXButton btn5 = generator.apply("Icon Left", FontAwesomeSolid.random());
        MFXButton btn6 = generator.apply("Icon Right", FontAwesomeSolid.random());
        btn6.setContentDisplay(ContentDisplay.RIGHT);

        btn1.setDisable(true);
        btn2.setMouseTransparent(true);
        btn2.pseudoClassStateChanged(PseudoClass.getPseudoClass("hover"), true);
        btn3.setMouseTransparent(true);
        btn3.pseudoClassStateChanged(PseudoClass.getPseudoClass("focused"), true);
        btn4.setMouseTransparent(true);
        btn4.pseudoClassStateChanged(PseudoClass.getPseudoClass("pressed"), true);

        tfp.add(btn0, btn1, btn2, btn3, btn4, btn5, btn6);
        return tfp;
    }

    private Node createFabsView(String title, BiFunction<String, MFXFontIcon, MFXFab> generator) {
        return createFabsView(title, 700, generator);
    }

    private Node createFabsView(String title, double length, BiFunction<String, MFXFontIcon, MFXFab> generator) {
        TitledFlowPane defTfp = new TitledFlowPane(title);
        defTfp.setMaxWidth(length);

        MFXFab btn0 = generator.apply("Enabled", FontAwesomeSolid.random());
        MFXFab btn1 = generator.apply("Disabled", FontAwesomeSolid.random());
        MFXFab btn2 = generator.apply("Hovered", FontAwesomeSolid.random());
        MFXFab btn3 = generator.apply("Focused", FontAwesomeSolid.random());
        MFXFab btn4 = generator.apply("Pressed", FontAwesomeSolid.random());
        MFXFab btn5 = generator.apply("Small", FontAwesomeSolid.random());
        MFXFab btn6 = generator.apply("Large", FontAwesomeSolid.random());
        MFXFab btn7 = generator.apply("Large Lowered", FontAwesomeSolid.random());
        MFXFab btn8 = generator.apply("Lowered Large", FontAwesomeSolid.random());

        btn1.setDisable(true);
        btn2.setMouseTransparent(true);
        btn2.pseudoClassStateChanged(PseudoClass.getPseudoClass("hover"), true);
        btn3.setMouseTransparent(true);
        btn3.pseudoClassStateChanged(PseudoClass.getPseudoClass("focused"), true);
        btn4.setMouseTransparent(true);
        btn4.pseudoClassStateChanged(PseudoClass.getPseudoClass("pressed"), true);
        btn5.addVariants(FABVariants.SMALL);
        btn6.addVariants(FABVariants.LARGE);
        btn7.addVariants(FABVariants.LARGE, FABVariants.LOWERED);
        btn8.addVariants(FABVariants.LOWERED, FABVariants.LARGE);

        btn8.addEventFilter(MouseEvent.MOUSE_PRESSED, e -> {
            btn8.setIcon(FontAwesomeSolid.random());
            e.consume();
        });

        defTfp.add(btn0, btn1, btn2, btn3, btn4, btn5, btn6, btn7, btn8);
        return defTfp;
    }

    private Node createExtendedFabView(String title, BiFunction<String, MFXFontIcon, MFXFab> generator) {
        return createExtendedFabView(title, 900, generator);
    }

    private Node createExtendedFabView(String title, double length, BiFunction<String, MFXFontIcon, MFXFab> generator) {
        TitledFlowPane defTfp = new TitledFlowPane(title);
        defTfp.setMaxWidth(length);

        MFXFab btn0 = generator.apply("Enabled", FontAwesomeSolid.random());
        MFXFab btn1 = generator.apply("Disabled", FontAwesomeSolid.random());
        MFXFab btn2 = generator.apply("Hovered", FontAwesomeSolid.random());
        MFXFab btn3 = generator.apply("Focused", FontAwesomeSolid.random());
        MFXFab btn4 = generator.apply("Pressed", FontAwesomeSolid.random());
        MFXFab btn5 = generator.apply("Text Only", FontAwesomeSolid.random());
        MFXFab btn6 = generator.apply("Expandable", FontAwesomeSolid.random());
        MFXFab btn7 = generator.apply("Change Icon", FontAwesomeSolid.random());
        MFXFab btn8 = generator.apply("Lowered Text Only", FontAwesomeSolid.random());

        btn1.setDisable(true);
        btn2.setMouseTransparent(true);
        btn2.pseudoClassStateChanged(PseudoClass.getPseudoClass("hover"), true);
        btn3.setMouseTransparent(true);
        btn3.pseudoClassStateChanged(PseudoClass.getPseudoClass("focused"), true);
        btn4.setMouseTransparent(true);
        btn4.pseudoClassStateChanged(PseudoClass.getPseudoClass("pressed"), true);

        btn5.setContentDisplay(ContentDisplay.TEXT_ONLY);
        btn5.setAlignment(Pos.CENTER_RIGHT);
        btn8.addVariants(FABVariants.LOWERED);
        btn8.setContentDisplay(ContentDisplay.TEXT_ONLY);
        btn8.setAlignment(Pos.CENTER);

        btn6.setExtended(false);
        btn6.setOnAction(e -> btn6.setExtended(!btn6.isExtended()));
        btn7.setOnAction(e -> btn7.setIcon(FontAwesomeSolid.random()));

        defTfp.add(btn0, btn1, btn2, btn3, btn4, btn5, btn6, btn7, btn8);
        return defTfp;
    }

    private Node createIconButtonsView(String title, BiFunction<Boolean, MFXFontIcon, MFXIconButton> generator) {
        return createIconButtonsView(title, 400, generator);
    }

    private Node createIconButtonsView(String title, double length, BiFunction<Boolean, MFXFontIcon, MFXIconButton> generator) {
        TitledFlowPane defTfp = new TitledFlowPane(title);
        defTfp.setMaxWidth(length);

        // As toggles
        MFXIconButton btn0 = generator.apply(false, FontAwesomeSolid.random());
        MFXIconButton btn1 = generator.apply(false, FontAwesomeSolid.random());
        MFXIconButton btn2 = generator.apply(false, FontAwesomeSolid.random());
        MFXIconButton btn3 = generator.apply(false, FontAwesomeSolid.random());
        MFXIconButton btn4 = generator.apply(false, FontAwesomeSolid.random());
        btn1.setMouseTransparent(true);
        btn1.pseudoClassStateChanged(PseudoClass.getPseudoClass("hover"), true);
        btn2.setMouseTransparent(true);
        btn2.pseudoClassStateChanged(PseudoClass.getPseudoClass("focused"), true);
        btn3.setMouseTransparent(true);
        btn3.pseudoClassStateChanged(PseudoClass.getPseudoClass("pressed"), true);
        btn4.setDisable(true);

        // Standard
        MFXIconButton btn5 = generator.apply(true, FontAwesomeSolid.random());
        MFXIconButton btn6 = generator.apply(true, FontAwesomeSolid.random());
        MFXIconButton btn7 = generator.apply(true, FontAwesomeSolid.random());
        MFXIconButton btn8 = generator.apply(true, FontAwesomeSolid.random());
        MFXIconButton btn9 = generator.apply(true, FontAwesomeSolid.random());
        btn6.setMouseTransparent(true);
        btn6.pseudoClassStateChanged(PseudoClass.getPseudoClass("hover"), true);
        btn7.setMouseTransparent(true);
        btn7.pseudoClassStateChanged(PseudoClass.getPseudoClass("focused"), true);
        btn8.setMouseTransparent(true);
        btn8.pseudoClassStateChanged(PseudoClass.getPseudoClass("pressed"), true);
        btn9.setDisable(true);

        if (btn5.getStyleClass().contains(IconButtonVariants.OUTLINED.variantStyleClass()) ||
            btn5.getStyleClass().size() == 1) {
            btn5.setSelected(true);
            btn6.setSelected(true);
            btn7.setSelected(true);
            btn8.setSelected(true);
            btn9.setSelected(true);
        }

        defTfp.add(btn0, btn1, btn2, btn3, btn4, btn5, btn6, btn7, btn8, btn9);
        return defTfp;
    }

    private Node createSegmentedButtonsView(String title) {
        TitledFlowPane defTP = new TitledFlowPane(title);

        MFXSegmentedButton woIcons = new MFXSegmentedButton();
        for (int i = 0; i < 5; i++) {
            woIcons.addSegment(null, "Segment " + i);
        }

        MFXSegmentedButton wIcons = new MFXSegmentedButton();
        for (int i = 0; i < 5; i++) {
            wIcons.addSegment(FontAwesomeSolid.random(), "Segment " + i);
        }

        MFXSegmentedButton wDisabled = new MFXSegmentedButton();
        for (int i = 0; i < 5; i++) {
            wDisabled.addSegment(FontAwesomeSolid.random(), "Segment " + i);
        }
        wDisabled.getSegments().get(1).setDisable(true);
        wDisabled.getSegments().get(2).setDisable(true);

        defTP.add(woIcons, wIcons, wDisabled);
        return defTP;
    }

    private Node createCheckboxesView(String title) {
        TitledFlowPane defTP = new TitledFlowPane(title);
        defTP.setMaxWidth(350);
        List<Supplier<MFXCheckbox>> generators = new ArrayList<>(List.of(
            MFXCheckbox::new,
            () -> {
                MFXCheckbox c = new MFXCheckbox();
                c.setDisable(true);
                return c;
            },
            () -> {
                MFXCheckbox c = new MFXCheckbox();
                c.setMouseTransparent(true);
                PseudoClasses.HOVER.setOn(c, true);
                return c;
            },
            () -> {
                MFXCheckbox c = new MFXCheckbox();
                c.setMouseTransparent(true);
                PseudoClasses.FOCUSED.setOn(c, true);
                return c;
            },
            () -> {
                MFXCheckbox c = new MFXCheckbox();
                c.setMouseTransparent(true);
                PseudoClasses.PRESSED.setOn(c, true);
                return c;
            }
        ));

        // Unchecked
        for (Supplier<MFXCheckbox> g : generators) {
            defTP.add(g.get());
        }

        // Indeterminate
        for (Supplier<MFXCheckbox> g : generators) {
            MFXCheckbox c = g.get();
            c.setAllowIndeterminate(true);
            c.setState(TriState.INDETERMINATE);
            defTP.add(c);
        }

        // Selected
        for (Supplier<MFXCheckbox> g : generators) {
            MFXCheckbox c = g.get();
            c.setSelected(true);
            defTP.add(c);
        }

        // Error
        for (Supplier<MFXCheckbox> g : generators) {
            MFXCheckbox c = g.get();
            c.setAllowIndeterminate(true);
            PseudoClasses.ERROR.setOn(c, true);
            defTP.add(c);
        }

        // Group
        SelectionGroup sg = new SelectionGroup(SelectionMode.SINGLE, true);
        for (int i = 0; i < 4; i++) {
            MFXCheckbox c = new MFXCheckbox("C" + (i + 1));
            sg.add(c);
            defTP.add(c);
        }

        return defTP;
    }
}
