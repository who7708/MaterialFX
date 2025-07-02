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

package io.github.palexdev.mfxcomponents.controls;

import java.util.List;
import java.util.function.Supplier;

import io.github.palexdev.mfxcomponents.behaviors.MFXFabMenuBehavior;
import io.github.palexdev.mfxcomponents.controls.base.MFXControl;
import io.github.palexdev.mfxcomponents.skins.MFXFabMenuSkin;
import io.github.palexdev.mfxcomponents.theming.PseudoClasses;
import io.github.palexdev.mfxcomponents.variants.FABVariants.StyleVariant;
import io.github.palexdev.mfxcomponents.variants.api.Variant;
import io.github.palexdev.mfxcomponents.variants.api.VariantsHandler;
import io.github.palexdev.mfxcomponents.variants.api.WithVariants;
import io.github.palexdev.mfxcore.base.properties.styleable.StyleableDoubleProperty;
import io.github.palexdev.mfxcore.base.properties.styleable.StyleableObjectProperty;
import io.github.palexdev.mfxcore.controls.MFXStyleable;
import io.github.palexdev.mfxcore.controls.SkinBase;
import io.github.palexdev.mfxcore.enums.Corner;
import io.github.palexdev.mfxcore.utils.fx.StyleUtils;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.css.CssMetaData;
import javafx.css.Styleable;
import javafx.css.StyleablePropertyFactory;
import javafx.geometry.Pos;
import javafx.scene.transform.Scale;

/// Implementation of the FAB menu shown in the Material 3 Expressive specs.
/// This is essentially a container for a bunch of [MFXFabs][MFXFab]. A so-called 'entry' FAB is responsible for switching
/// the [#openProperty()], thus revealing or hiding the other FABs specified in the [#getButtons()] list.
///
/// Extends [MFXControl], expects behaviors of type [MFXFabMenuBehavior], the default skin is [MFXFabMenuSkin],
/// and the default CSS style class is `.mfx-fab-menu`.
///
/// It also implements [WithVariants] because by design the menu can have three color styles:
/// - [StyleVariant#PRIMARY]
/// - [StyleVariant#SECONDARY]
/// - [StyleVariant#TERTIARY]
///
/// Those are applied on the 'entry' FAB. The tonal variants are restricted by [#setStyle(StyleVariant)] and are reserved
/// for the other FABs in the menu. (the mechanism is handled by the default skin!)
///
/// The 'menu' can be shown at the four corners, specified by the [#menuCornerProperty()].
public class MFXFabMenu extends MFXControl<MFXFabMenuBehavior> implements WithVariants {
    //================================================================================
    // Properties
    //================================================================================
    private final VariantsHandler<MFXFabMenu> variantsHandler = new VariantsHandler<>(this);
    private final ObservableList<MFXFab> buttons = FXCollections.observableArrayList();
    private final BooleanProperty open = new SimpleBooleanProperty(false) {
        @Override
        protected void invalidated() {
            PseudoClasses.setOn(MFXFabMenu.this, "open", get());
        }
    };

    //================================================================================
    // Constructors
    //================================================================================
    public MFXFabMenu() {
        defaultConfig();
    }

    //================================================================================
    // Configs
    //================================================================================

    /// Applies the default variants to the menu and its buttons:
    /// - [StyleVariant#PRIMARY] (will be TONAL_PRIMARY for the buttons)
    public MFXFabMenu defaultConfig() {
        variantsHandler.setVariant(StyleVariant.PRIMARY);
        return this;
    }

    /// Note: Tonal variants from [StyleVariant] are forbidden and are automatically converted to
    /// their standard counterpart.
    public MFXFabMenu setStyle(StyleVariant style) {
        // Restrict to non-tonal variants
        style = switch (style) {
            case TONAL_PRIMARY -> StyleVariant.PRIMARY;
            case TONAL_SECONDARY -> StyleVariant.SECONDARY;
            case TONAL_TERTIARY -> StyleVariant.TERTIARY;
            default -> style;
        };
        variantsHandler.setVariant(style);
        return this;
    }

    //================================================================================
    // Overridden Methods
    //================================================================================
    @Override
    public Supplier<MFXFabMenuBehavior> defaultBehaviorProvider() {
        return () -> new MFXFabMenuBehavior(this);
    }

    @Override
    public Supplier<SkinBase<?, ?>> defaultSkinProvider() {
        return () -> new MFXFabMenuSkin(this);
    }

    @Override
    public List<String> defaultStyleClasses() {
        return MFXStyleable.styleClasses("mfx-fab-menu");
    }

    @Override
    public ObservableMap<Class<?>, Variant> getAppliedVariants() {
        return variantsHandler.getAppliedVariantsUnmodifiable();
    }

    //================================================================================
    // Styleable Properties
    //================================================================================
    private final StyleableObjectProperty<Corner> menuCorner = new StyleableObjectProperty<>(
        StyleableProperties.MENU_CORNER,
        this,
        "menuCorner",
        Corner.TOP_RIGHT
    ) {
        @Override
        protected void invalidated() {
            requestLayout();
        }
    };

    private final StyleableDoubleProperty gap = new StyleableDoubleProperty(
        StyleableProperties.GAP,
        this,
        "gap",
        8.0
    ) {
        @Override
        protected void invalidated() {
            requestLayout();
        }
    };

    private final StyleableObjectProperty<Pos> scalePivot = new StyleableObjectProperty<>(
        StyleableProperties.SCALE_PIVOT,
        this,
        "scalePivot",
        Pos.CENTER
    );

    public Corner getMenuCorner() {
        return menuCorner.get();
    }

    /// Specifies the corner at which the menu is shown relative to the 'entry' FAB.
    ///
    /// Can be set from CSS via the property: '-mfx-menu-corner'.
    public StyleableObjectProperty<Corner> menuCornerProperty() {
        return menuCorner;
    }

    public void setMenuCorner(Corner menuCorner) {
        this.menuCorner.set(menuCorner);
    }

    public double getGap() {
        return gap.get();
    }

    /// Specifies the gap between the FABs in the menu.
    ///
    /// **Note:** by design the gap between the 'entry' and the other FABs is the value specified by this property.
    /// The gap between each FAB in the menu is half of this value! This is done in the layout logic of the default skin.
    ///
    /// Can be set from CSS via the property: '-mfx-gap'.
    public StyleableDoubleProperty gapProperty() {
        return gap;
    }

    public void setGap(double gap) {
        this.gap.set(gap);
    }

    public Pos getScalePivot() {
        return scalePivot.get();
    }

    /// By design, when the menu is open, the 'entry' FAB becomes a little smaller. By default, this is done by scaling
    /// it down. This property specifies the pivot for the applied [Scale] transform.
    ///
    /// Can be set from CSS via the property: '-mfx-scale-pivot'.
    ///
    /// @see MFXFabMenuSkin
    public StyleableObjectProperty<Pos> scalePivotProperty() {
        return scalePivot;
    }

    public void setScalePivot(Pos scalePivot) {
        this.scalePivot.set(scalePivot);
    }

    //================================================================================
    // CssMetaData
    //================================================================================
    private static class StyleableProperties {
        private static final StyleablePropertyFactory<MFXFabMenu> FACTORY = new StyleablePropertyFactory<>(MFXControl.getClassCssMetaData());
        private static final List<CssMetaData<? extends Styleable, ?>> cssMetaDataList;

        private static final CssMetaData<MFXFabMenu, Corner> MENU_CORNER =
            FACTORY.createEnumCssMetaData(
                Corner.class,
                "-mfx-menu-corner",
                MFXFabMenu::menuCornerProperty,
                Corner.TOP_RIGHT
            );

        private static final CssMetaData<MFXFabMenu, Number> GAP =
            FACTORY.createSizeCssMetaData(
                "-mfx-gap",
                MFXFabMenu::gapProperty,
                8.0
            );

        private static final CssMetaData<MFXFabMenu, Pos> SCALE_PIVOT =
            FACTORY.createEnumCssMetaData(
                Pos.class,
                "-mfx-scale-pivot",
                MFXFabMenu::scalePivotProperty,
                Pos.CENTER
            );

        static {
            cssMetaDataList = StyleUtils.cssMetaDataList(
                MFXControl.getClassCssMetaData(),
                MENU_CORNER, GAP, SCALE_PIVOT
            );
        }
    }

    public static List<CssMetaData<? extends Styleable, ?>> getClassCssMetaData() {
        return StyleableProperties.cssMetaDataList;
    }

    @Override
    public List<CssMetaData<? extends Styleable, ?>> getControlCssMetaData() {
        return getClassCssMetaData();
    }

    //================================================================================
    // Getters/Setters
    //================================================================================
    public ObservableList<MFXFab> getButtons() {
        return buttons;
    }

    public boolean isOpen() {
        return open.get();
    }

    public BooleanProperty openProperty() {
        return open;
    }

    public void setOpen(boolean open) {
        this.open.set(open);
    }
}
