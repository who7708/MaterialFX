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

package io.github.palexdev.mfxcomponents.skins;

import java.util.function.Function;

import io.github.palexdev.mfxcomponents.behaviors.MFXButtonBehavior;
import io.github.palexdev.mfxcomponents.controls.MFXButton;
import io.github.palexdev.mfxcomponents.controls.MFXIconButton;
import io.github.palexdev.mfxcomponents.controls.MFXSurface;
import io.github.palexdev.mfxcore.controls.SkinBase;
import io.github.palexdev.mfxcore.utils.fx.StyleUtils;
import io.github.palexdev.mfxeffects.beans.Position;
import io.github.palexdev.mfxeffects.ripple.MFXRippleGenerator;
import io.github.palexdev.mfxresources.icon.MFXIconWrapper;
import javafx.beans.binding.Bindings;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;

import static io.github.palexdev.mfxcore.events.WhenEvent.intercept;

/// Default skin implementation for all [MFXIconButton][MFXIconButton]s and is just a simplification of the [MFXButtonSkin].
/// Icon buttons are designed to only show an icon, no text. Therefore, we can optimize the skin to not have a label node.
///
/// This skin uses behaviors of type [MFXButtonBehavior].
///
/// The layout is simple: the [MFXSurface] responsible for showing the various interaction states (applying an overlay background)
/// and the [MFXIconWrapper] responsible for showing the icon. We use the [MFXRippleGenerator] inside [MFXIconWrapper]
/// for convenience.
public class MFXIconButtonSkin extends SkinBase<MFXButton, MFXButtonBehavior> {
    //================================================================================
    // Properties
    //================================================================================
    protected final MFXIconWrapper icon;
    private final MFXSurface surface;

    //================================================================================
    // Constructors
    //================================================================================
    public MFXIconButtonSkin(MFXIconButton button) {
        super(button);

        // Init
        icon = new MFXIconWrapper() {
            @Override
            public MFXIconWrapper enableRipple(boolean enable, Function<MouseEvent, Position> posFn) {
                super.enableRipple(enable, posFn);
                // Since the ripple generator is inside the icon wrapper, we must redirect the clip builder to the
                // correct owner, which is the button
                MFXRippleGenerator rg = getRippleGenerator();
                rg.getStyleClass().add("surface-ripple");
                rg.setClipSupplier(() -> {
                    Region clip = new Region();
                    clip.backgroundProperty().bind(Bindings.createObjectBinding(
                        () -> {
                            CornerRadii radius = StyleUtils.parseCornerRadius(button);
                            BackgroundFill fill = new BackgroundFill(Color.WHITE, radius, Insets.EMPTY);
                            return new Background(fill);
                        },
                        button.backgroundProperty(), button.borderProperty()
                    ));
                    return clip;
                });
                return this;
            }
        };
        icon.enableRipple(true);
        icon.animatedProperty().bind(button.animatedProperty());
        icon.iconProperty().bindBidirectional(button.iconProperty());
        icon.setManaged(false);

        surface = new MFXSurface(button);

        // Finalize
        getChildren().setAll(surface, icon);
    }

    //================================================================================
    // Overridden Methods
    //================================================================================

    /// Adds the following handlers:
    /// - [MouseEvent#MOUSE_PRESSED] to call [MFXButtonBehavior#mousePressed]
    /// - [MouseEvent#MOUSE_CLICKED] to call [MFXButtonBehavior#mouseClicked]
    /// - [KeyEvent#KEY_PRESSED] to call [MFXButtonBehavior#keyPressed] and to generate the ripple effect at the center
    /// when the ENTER or SPACEBAR keys are pressed.
    @Override
    protected void initBehavior(MFXButtonBehavior behavior) {
        MFXButton button = getSkinnable();
        super.initBehavior(behavior);
        events(
            intercept(button, MouseEvent.MOUSE_PRESSED).process(behavior::mousePressed),
            intercept(button, MouseEvent.MOUSE_CLICKED).process(behavior::mouseClicked),
            intercept(button, KeyEvent.KEY_PRESSED).process(e -> behavior.keyPressed(e, _ -> {
                MFXRippleGenerator rg = icon.getRippleGenerator();
                if (rg != null && (e.getCode() == KeyCode.ENTER || e.getCode() == KeyCode.SPACE)) {
                    Bounds b = button.getLayoutBounds();
                    rg.generate(b.getCenterX(), b.getCenterY());
                    rg.release();
                }
            }))
        );
    }

    @Override
    protected double computePrefWidth(double height, double topInset, double rightInset, double bottomInset, double leftInset) {
        return leftInset + icon.prefWidth(-1) + rightInset;
    }

    @Override
    protected double computePrefHeight(double width, double topInset, double rightInset, double bottomInset, double leftInset) {
        return topInset + icon.prefHeight(-1) + bottomInset;
    }

    @Override
    protected double computeMaxWidth(double height, double topInset, double rightInset, double bottomInset, double leftInset) {
        return getSkinnable().prefWidth(height);
    }

    @Override
    protected double computeMaxHeight(double width, double topInset, double rightInset, double bottomInset, double leftInset) {
        return getSkinnable().prefHeight(width);
    }

    @Override
    protected void layoutChildren(double x, double y, double w, double h) {
        MFXButton button = getSkinnable();
        surface.resizeRelocate(0, 0, button.getWidth(), button.getHeight());
        // This is because of the ripple generator, otherwise it should be layoutInArea(...)
        // Should not be anyway since the wrapped icon is sized correctly, and the padding is still taken into account by
        // the above size computation methods
        icon.resizeRelocate(0, 0, button.getWidth(), button.getHeight());
    }

    @Override
    public void dispose() {
        surface.dispose();
        icon.enableRipple(false);
        super.dispose();
    }
}
