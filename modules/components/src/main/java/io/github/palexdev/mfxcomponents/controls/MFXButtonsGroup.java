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

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

import io.github.palexdev.mfxcomponents.behaviors.MFXButtonsGroupBehavior;
import io.github.palexdev.mfxcomponents.controls.base.MFXControl;
import io.github.palexdev.mfxcomponents.skins.MFXButtonSkin;
import io.github.palexdev.mfxcomponents.skins.MFXButtonsGroupSkin;
import io.github.palexdev.mfxcomponents.skins.MFXIconButtonSkin;
import io.github.palexdev.mfxcomponents.variants.Variant;
import io.github.palexdev.mfxcomponents.variants.VariantsHandler;
import io.github.palexdev.mfxcomponents.variants.WithVariants;
import io.github.palexdev.mfxcomponents.variants.button.*;
import io.github.palexdev.mfxcore.base.properties.styleable.StyleableDoubleProperty;
import io.github.palexdev.mfxcore.controls.BoundLabel;
import io.github.palexdev.mfxcore.controls.SkinBase;
import io.github.palexdev.mfxcore.enums.SelectionMode;
import io.github.palexdev.mfxcore.observables.When;
import io.github.palexdev.mfxcore.selection.Selectable;
import io.github.palexdev.mfxcore.selection.SelectionGroup;
import io.github.palexdev.mfxcore.utils.fx.StyleUtils;
import io.github.palexdev.mfxresources.fonts.MFXFontIcon;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.css.CssMetaData;
import javafx.css.Styleable;
import javafx.css.StyleablePropertyFactory;

/// Implementation of the buttons group shown in the Material 3 Expressive specs.
/// This is essentially a container for a bunch of buttons (either [MFXButton] or [MFXIconButton]) that can be selected.
///
/// Extends [MFXControl], expects behaviors of type [MFXButtonsGroupBehavior], the default skin is [MFXButtonsGroupSkin],
/// and the default CSS style class is `.mfx-buttons-group`.
/// The selection is handled by a [SelectionGroup] that can be configured through a bunch of delegate methods, by default
/// the selection is [SelectionMode#SINGLE] and [SelectionGroup#atLeastOneSelectedProperty()] is `false`.
///
/// It also implements [WithVariants]. The specs define a bunch of different configurations; we can distinguish two types:
/// - Configurations that apply on the group itself, like the [GroupVariant] and the [SizeVariant] (inherited from [ButtonsConfig])
/// - Configurations that apply on the buttons, which are the same defined in [MFXButton] and [MFXIconButton]. To make this
/// more convenient, we grouped them into [ButtonsConfig] and can be set through [#setButtonsConfig(ButtonsConfig)].
/// When buttons are added to the group, the configuration is automatically applied by [#updateGroup(ListChangeListener.Change)]
///
/// Buttons can be added or removed through the delegate methods [#addButton(MFXButton)], [#addButton(String, MFXFontIcon)],
/// [#addButtons(Object...)] and [#removeButtons(MFXButton...)], the list returned by [#getButtons()] is unmodifiable!
public class MFXButtonsGroup extends MFXControl<MFXButtonsGroupBehavior> implements WithVariants {
    //================================================================================
    // Properties
    //================================================================================
    private final VariantsHandler<MFXButtonsGroup> variantsHandler = new VariantsHandler<>(this);
    private final ObservableList<MFXButton> buttons = FXCollections.observableArrayList();
    private ButtonsConfig buttonsConfig;
    private final SelectionGroup selectionGroup = new SelectionGroup(SelectionMode.SINGLE);

    //================================================================================
    // Constructors
    //================================================================================
    public MFXButtonsGroup() {
        defaultConfig();
        buttons.addListener(this::updateGroup);
    }

    //================================================================================
    // Config
    //================================================================================

    /// Applies the default variants to the group and its buttons:
    /// - [GroupVariant#STANDARD]
    /// - [ButtonsConfig#DEFAULT]
    public MFXButtonsGroup defaultConfig() {
        variantsHandler.setVariant(GroupVariant.STANDARD);
        setButtonsConfig(ButtonsConfig.DEFAULT);
        return this;
    }

    public MFXButtonsGroup setGroupType(GroupVariant type) {
        variantsHandler.setVariant(type);
        return this;
    }

    public MFXButtonsGroup setButtonsConfig(ButtonsConfig config) {
        buttons.forEach(config::apply);
        variantsHandler.setVariant(config.size()); // We need this for the spacing
        this.buttonsConfig = config;
        return this;
    }

    //================================================================================
    // Methods
    //================================================================================

    /// Adds the given button to the group and sets [MFXButton#toggleableProperty()] to `true`.
    ///
    /// #### Workaround!
    /// Material 3 Expressive specs show that in standard groups the buttons slightly grow when pressed. The easiest way
    /// to implement this was to scale them along the x-axis. Unfortunately, when applying the scale, all its children
    /// (label and graphic/icon) are stretched too. To avoid this, we set a custom skin on the buttons added to the group.
    /// For [MFXButton] the label's scale-x, and for [MFXIconButton] the inner icon's scale-x are bound to counteract the
    /// button's scale, given by: `1.0 / btnScaleX`. The binding ensures the animation plays correctly.
    public MFXButtonsGroup addButton(MFXButton btn) {
        if (btn instanceof MFXIconButton iBtn) {
            btn.setSkinProvider(() -> new MFXIconButtonSkin(iBtn) {
                {
                    // We want the inner font icon because we use the ripple generator inside the wrapper
                    // and that needs to be scaled
                    listeners(
                        When.onChanged(icon.iconProperty())
                            .then((o, n) -> {
                                if (o != null) o.scaleXProperty().unbind();
                                if (n != null)
                                    n.scaleXProperty().bind(btn.scaleXProperty().map(s -> 1.0 / s.doubleValue()));
                            })
                            .executeNow(() -> icon.getIcon() != null)
                    );
                }
            });
        } else {
            btn.setSkinProvider(() -> new MFXButtonSkin<>(btn) {
                @Override
                protected BoundLabel buildLabelNode() {
                    BoundLabel label = super.buildLabelNode();
                    label.scaleXProperty().bind(btn.scaleXProperty().map(s -> 1.0 / s.doubleValue()));
                    return label;
                }

                @Override
                public void dispose() {
                    label.scaleXProperty().unbind();
                    super.dispose();
                }
            });
        }
        btn.setToggleable(true);
        buttons.add(btn);
        return this;
    }

    /// Delegates to [#addButton(MFXButton)] by building a [MFXIconButton] if the given text is `null` or empty,
    /// otherwise a [MFXButton].
    public MFXButtonsGroup addButton(String text, MFXFontIcon icon) {
        return addButton((text == null || text.isEmpty()) ?
            new MFXIconButton(icon) :
            new MFXButton(text, icon)
        );
    }

    /// Convenience method similar to `Map.of(...)` but with an unlimited number of arguments.
    ///
    /// Expects an even number of arguments, each pair `<String, MFXFontIcon>` is sent to [#addButton(String, MFXFontIcon)].
    public MFXButtonsGroup addButtons(Object... args) {
        if (args.length % 2 != 0)
            throw new IllegalArgumentException("The number of arguments must be even and follow the format: <text>, <icon>");
        for (int i = 0; i < args.length; i += 2) {
            String text = (String) args[i];
            MFXFontIcon icon = (MFXFontIcon) args[i + 1];
            addButton(text, icon);
        }
        return this;
    }

    /// Removes the given buttons from the group (also automatically removed from the [SelectionGroup]).
    public MFXButtonsGroup removeButtons(MFXButton... buttons) {
        this.buttons.removeAll(buttons);
        return this;
    }

    /// This is responsible for updating the [SelectionGroup] by adding/removing buttons from it when the buttons' list changes.
    ///
    /// Note that for added buttons the [ButtonsConfig] is automatically applied.
    protected void updateGroup(ListChangeListener.Change<? extends MFXButton> change) {
        while (change.next()) {
            if (change.wasRemoved()) {
                change.getRemoved().forEach(selectionGroup::remove);
            } else if (change.wasAdded()) {
                change.getAddedSubList().forEach(b -> {
                    selectionGroup.add(b);
                    buttonsConfig.apply(b);
                });
            }
        }
    }

    //================================================================================
    // Overridden Methods
    //================================================================================
    @Override
    public Supplier<MFXButtonsGroupBehavior> defaultBehaviorProvider() {
        return () -> new MFXButtonsGroupBehavior(this);
    }

    @Override
    public Supplier<SkinBase<?, ?>> defaultSkinProvider() {
        return () -> new MFXButtonsGroupSkin(this);
    }

    @Override
    public List<String> defaultStyleClasses() {
        return List.of("mfx-buttons-group");
    }

    @Override
    public Map<Class<?>, Variant> getAppliedVariants() {
        return Collections.unmodifiableMap(variantsHandler.getAppliedVariants());
    }

    //================================================================================
    // Styleable Properties
    //================================================================================
    private final StyleableDoubleProperty spacing = new StyleableDoubleProperty(
        StyleableProperties.SPACING,
        this,
        "spacing",
        0.0
    );

    public double getSpacing() {return spacing.get();}

    /// Specifies the gap between each button in the group.
    ///
    /// Can be set from CSS via the property: `-mfx-spacing`.
    public StyleableDoubleProperty spacingProperty() {return spacing;}

    public void setSpacing(double spacing) {this.spacing.set(spacing);}

    //================================================================================
    // CssMetaData
    //================================================================================
    private static class StyleableProperties {
        private static final StyleablePropertyFactory<MFXButtonsGroup> FACTORY = new StyleablePropertyFactory<>(MFXControl.getClassCssMetaData());
        private static final List<CssMetaData<? extends Styleable, ?>> cssMetaDataList;

        private static final CssMetaData<MFXButtonsGroup, Number> SPACING =
            FACTORY.createSizeCssMetaData(
                "-mfx-spacing",
                MFXButtonsGroup::spacingProperty,
                0.0
            );

        static {
            cssMetaDataList = StyleUtils.cssMetaDataList(
                MFXControl.getClassCssMetaData(),
                SPACING
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
    public ObservableList<MFXButton> getButtons() {
        return FXCollections.unmodifiableObservableList(buttons);
    }

    /// Returns the currently selected button.
    ///
    /// Note that this is more of a convenience for when the selection mode is [SelectionMode#SINGLE].
    /// If the selection mode is [SelectionMode#MULTIPLE], then this will return the first selected button.
    public Optional<Selectable> getSelected() {return selectionGroup.getFirstSelected();}

    /// @return all the currently selected buttons.
    public List<Selectable> getSelection() {return selectionGroup.getSelectionList();}

    public SelectionMode getSelectionMode() {return selectionGroup.getSelectionMode();}

    /// @see SelectionGroup#selectionModeProperty()
    public ObjectProperty<SelectionMode> selectionModeProperty() {return selectionGroup.selectionModeProperty();}

    public void setSelectionMode(SelectionMode mode) {selectionGroup.setSelectionMode(mode);}

    public boolean isAtLeastOneSelected() {return selectionGroup.isAtLeastOneSelected();}

    /// @see SelectionGroup#atLeastOneSelectedProperty()
    public BooleanProperty atLeastOneSelectedProperty() {return selectionGroup.atLeastOneSelectedProperty();}

    public void setAtLeastOneSelected(boolean atLeastOneSelected) {selectionGroup.setAtLeastOneSelected(atLeastOneSelected);}

    //================================================================================
    // Inner Classes
    //================================================================================

    /// Convenience record for applying a combination of variants to all the buttons in the group.
    public record ButtonsConfig(
        ShapeVariant shape,
        SizeVariant size,
        StyleVariant style,
        WidthVariant width
    ) {
        /// The default buttons configuration:
        /// - [ShapeVariant#ROUNDED]
        /// - [SizeVariant#S]
        /// - [StyleVariant#FILLED]
        /// - [WidthVariant#DEFAULT] (for [MFXIconButton] only)
        public static final ButtonsConfig DEFAULT = ButtonsConfig.of(
            ShapeVariant.ROUNDED,
            SizeVariant.S,
            StyleVariant.FILLED,
            WidthVariant.DEFAULT
        );

        public static ButtonsConfig of(ShapeVariant shape, SizeVariant size, StyleVariant style, WidthVariant width) {
            return new ButtonsConfig(shape, size, style, width);
        }

        /// Applies the configuration to the given button.
        public void apply(MFXButton btn) {
            btn.setShape(shape);
            btn.setSize(size);
            btn.setStyle(style);
            if (btn instanceof MFXIconButton iBtn)
                iBtn.setWidth(width);
        }

        /// @return a new configuration with the same values as this one but the given shape.
        public ButtonsConfig withShape(ShapeVariant shape) {
            return new ButtonsConfig(shape, size, style, width);
        }

        /// @return a new configuration with the same values as this one but the given size.
        public ButtonsConfig withSize(SizeVariant size) {
            return new ButtonsConfig(shape, size, style, width);
        }

        /// @return a new configuration with the same values as this one but the given style.
        public ButtonsConfig withStyle(StyleVariant style) {
            return new ButtonsConfig(shape, size, style, width);
        }

        /// @return a new configuration with the same values as this one but the given width.
        public ButtonsConfig withWidth(WidthVariant width) {
            return new ButtonsConfig(shape, size, style, width);
        }
    }
}
