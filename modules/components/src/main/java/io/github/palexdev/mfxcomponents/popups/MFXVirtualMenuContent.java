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

package io.github.palexdev.mfxcomponents.popups;

import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

import io.github.palexdev.mfxcomponents.cells.MFXMenuCell;
import io.github.palexdev.mfxcomponents.skins.MFXVirtualMenuContentSkin;
import io.github.palexdev.mfxcore.base.properties.functional.FunctionProperty;
import io.github.palexdev.mfxcore.base.properties.styleable.StyleableIntegerProperty;
import io.github.palexdev.mfxcore.controls.MFXSkinnable;
import io.github.palexdev.mfxcore.controls.SkinBase;
import io.github.palexdev.mfxcore.popups.menu.MFXMenu;
import io.github.palexdev.mfxcore.popups.menu.MFXMenuContent;
import io.github.palexdev.mfxcore.popups.menu.MFXMenuItem;
import io.github.palexdev.mfxcore.utils.fx.StyleUtils;
import io.github.palexdev.virtualizedfx.cells.base.VFXCell;
import io.github.palexdev.virtualizedfx.list.VFXList;
import javafx.css.CssMetaData;
import javafx.css.Styleable;
import javafx.css.StyleablePropertyFactory;

/// Extension of [MFXMenuContent] which virtualizes its entries' list thanks to [VFXList].
///
/// #### Intentions & Guidelines <br >
/// Let's take combo boxes as an example. They can show a menu of items the user can choose from to set the combo box's
/// value. One problem that may arise is if the list of items is fairly big. In such cases, the menu is scrollable and
/// ideally virtualized for better performance. Instead, if you think about menus from app bars and such, those are typically
/// not scrollable, nor virtualized, and all the items are shown at once. This is because it's very rare to see menus of
/// options so long that do not fit the screen, and even in that case it's a design smell.
///
/// This type of content is intended to be used for cases such as the first example. In fact, separators are not well-supported
/// here because virtualization imposes that all entries have the same size. So, if you add a separator, you'll end up
/// having a huge blank space between your items.
///
/// Last but not least, because ultimately we use the same infrastructure for both use cases, you can add submenus for this
/// content type too. However, I would suggest not doing so, as it is a bad design practice.
///
/// Long story short: if you need to show some options to the user, maybe relative to the app, or current context, then
/// you're better off using a standard [MFXMenuContent]; if you need to present the user some choices, and they are too
/// many to show at once, you can use [MFXVirtualMenuContent] instead.
///
/// #### Performance <br >
/// This content is much heavier to initialize compared to [MFXMenuContent]. In my testing, this led to the menu to be
/// shown after a noticeable delay. To mitigate this, [MFXVirtualMenuContent] preloads its skin upon creation, see [MFXSkinnable#preloadSkin()].
/// However, keep in mind that this depends on the machine it runs on!
///
/// @see MFXVirtualMenuContentSkin
/// @see MFXMenuCell
public class MFXVirtualMenuContent extends MFXMenuContent {
    //================================================================================
    // Properties
    //================================================================================
    private final FunctionProperty<MFXMenuItem, VFXCell<MFXMenuItem>> cellFactory = new FunctionProperty<>();

    //================================================================================
    // Constructors
    //================================================================================
    public MFXVirtualMenuContent(MFXMenu menu) {
        super(menu);
        setCellFactory(t -> new MFXMenuCell(getMenu(), t));
    }

    public MFXVirtualMenuContent(MFXMenu menu, Function<MFXMenuItem, VFXCell<MFXMenuItem>> cellFactory) {
        super(menu);
        setCellFactory(cellFactory);
    }

    {
        preloadSkin(); // Make the popup show as fast as possible the first time by pre-initializing the skin
    }

    //================================================================================
    // Overridden Methods
    //================================================================================
    @Override
    public Supplier<SkinBase<?, ?>> defaultSkinProvider() {
        return () -> new MFXVirtualMenuContentSkin(this);
    }

    //================================================================================
    // Styleable Properties
    //================================================================================
    private final StyleableIntegerProperty visibleItems = new StyleableIntegerProperty(
        StyleableProperties.VISIBLE_ITEMS,
        this,
        "visibleItems",
        5
    ) {
        @Override
        protected void invalidated() {
            requestLayout();
        }
    };

    public int getVisibleItems() {
        return visibleItems.get();
    }

    /// Specifies the minimum number of elements to show at once.
    ///
    /// Can be set from CSS via the property: '-mfx-visible-items'.
    public StyleableIntegerProperty visibleItemsProperty() {
        return visibleItems;
    }

    public void setVisibleItems(int visibleItems) {
        this.visibleItems.set(visibleItems);
    }

    //================================================================================
    // CssMetaData
    //================================================================================
    private static class StyleableProperties {
        private static final StyleablePropertyFactory<MFXVirtualMenuContent> FACTORY = new StyleablePropertyFactory<>(MFXMenuContent.getClassCssMetaData());
        private static final List<CssMetaData<? extends Styleable, ?>> cssMetaDataList;

        private static final CssMetaData<MFXVirtualMenuContent, Number> VISIBLE_ITEMS =
            FACTORY.createSizeCssMetaData(
                "-mfx-visible-items",
                MFXVirtualMenuContent::visibleItemsProperty,
                5
            );

        static {
            cssMetaDataList = StyleUtils.cssMetaDataList(
                MFXMenuContent.getClassCssMetaData(),
                VISIBLE_ITEMS
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
    public Function<MFXMenuItem, VFXCell<MFXMenuItem>> getCellFactory() {
        return cellFactory.get();
    }

    /// Specifies the function responsible for building the cells used to display the [MFXMenuItems][MFXMenuItem].
    public FunctionProperty<MFXMenuItem, VFXCell<MFXMenuItem>> cellFactoryProperty() {
        return cellFactory;
    }

    public void setCellFactory(Function<MFXMenuItem, VFXCell<MFXMenuItem>> cellFactory) {
        this.cellFactory.set(cellFactory);
    }
}
