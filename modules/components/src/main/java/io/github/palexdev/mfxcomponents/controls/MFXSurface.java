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

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

import io.github.palexdev.mfxcomponents.theming.PseudoClasses;
import io.github.palexdev.mfxcore.base.properties.styleable.StyleableBooleanProperty;
import io.github.palexdev.mfxcore.base.properties.styleable.StyleableDoubleProperty;
import io.github.palexdev.mfxcore.base.properties.styleable.StyleableObjectProperty;
import io.github.palexdev.mfxcore.controls.Styleable;
import io.github.palexdev.mfxcore.utils.fx.StyleUtils;
import io.github.palexdev.mfxeffects.animations.Animations;
import io.github.palexdev.mfxeffects.animations.Animations.KeyFrames;
import io.github.palexdev.mfxeffects.animations.Animations.TimelineBuilder;
import io.github.palexdev.mfxeffects.animations.base.Curve;
import io.github.palexdev.mfxeffects.animations.motion.Cubic;
import io.github.palexdev.mfxeffects.animations.motion.M3Motion;
import io.github.palexdev.mfxeffects.enums.ElevationLevel;
import io.github.palexdev.mfxeffects.ripple.MFXRippleGenerator;
import javafx.animation.Animation;
import javafx.beans.InvalidationListener;
import javafx.css.CssMetaData;
import javafx.css.PseudoClass;
import javafx.css.StyleablePropertyFactory;
import javafx.scene.Parent;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.Effect;
import javafx.scene.layout.Region;

public class MFXSurface extends Region implements Styleable {
    //================================================================================
    // Static Properties
    //================================================================================
    public static boolean ANIMATED = true;

    //================================================================================
    // Properties
    //================================================================================
    private Parent owner;
    private MFXRippleGenerator rg;

    private InvalidationListener stateListener;
    private final Queue<State> states = new PriorityQueue<>(Comparator.comparing(State::priority));

    protected Animation animation;
    protected double lastOpacity = 1.0;

    //================================================================================
    // Constructors
    //================================================================================
    public MFXSurface(Parent owner) {
        this.owner = owner;
        states.addAll(State.DEFAULT_STATES);
        init();
    }

    //================================================================================
    // Methods
    //================================================================================
    private void init() {
        setManaged(false);
        setMaxSize(USE_PREF_SIZE, USE_PREF_SIZE);
        defaultStyleClasses(this);

        stateListener = _ -> updateOpacity();
        owner.getPseudoClassStates().addListener(stateListener);
    }

    public MFXSurface initRipple(Consumer<MFXRippleGenerator> config) {
        if (rg == null) {
            rg = new MFXRippleGenerator(this);
            getChildren().add(rg);
        }
        if (config != null) config.accept(rg);
        return this;
    }

    public void updateOpacity() {
        double target = getTargetOpacity();
        if (lastOpacity == target) return;
        if (ANIMATED && isAnimated()) {
            animate(target);
        } else {
            setOpacity(target);
        }
        lastOpacity = target;
    }

    protected void animate(double opacity) {
        if (Animations.isPlaying(animation)) animation.stop();
        Curve curve = new Cubic(0.34, 0.80, 0.34, 1.00);
        animation = TimelineBuilder.build()
            .add(KeyFrames.of(M3Motion.SHORT4, opacityProperty(), opacity))
            .getAnimation();
        animation.play();
    }

    public double getTargetOpacity() {
        return states.stream()
            .filter(s -> s.isActive(owner))
            .findFirst()
            .map(s -> s.opacity(this))
            .orElse(State.FALLBACK.opacity(this));
    }

    public void dispose() {
        getChildren().clear();
        if (rg != null) {
            rg.dispose();
            rg = null;
        }
        states.clear();
        owner.getPseudoClassStates().removeListener(stateListener);
        stateListener = null;
        owner = null;
    }

    //================================================================================
    // Overridden Methods
    //================================================================================
    @Override
    public List<String> defaultStyleClasses() {
        return Styleable.styleClasses("surface");
    }

    @Override
    protected void layoutChildren() {
        if (rg != null) {
            rg.resizeRelocate(0, 0, getWidth(), getHeight());
        }
    }

    //================================================================================
    // Styleable Properties
    //================================================================================
    private final StyleableBooleanProperty animated = new StyleableBooleanProperty(
        StyleableProperties.ANIMATED,
        this,
        "animated",
        true
    );

    private final StyleableDoubleProperty disabledOpacity = new StyleableDoubleProperty(
        StyleableProperties.DISABLED_OPACITY,
        this,
        "disabledOpacity",
        0.0
    ) {
        @Override
        public void set(double v) {
            double oldValue = get();
            super.set(v);
            if (!Objects.equals(oldValue, v)) updateOpacity();
        }
    };

    private final StyleableDoubleProperty pressedOpacity = new StyleableDoubleProperty(
        StyleableProperties.PRESSED_OPACITY,
        this,
        "pressedOpacity",
        0.0
    ) {
        @Override
        public void set(double v) {
            double oldValue = get();
            super.set(v);
            if (!Objects.equals(oldValue, v)) updateOpacity();
        }
    };

    private final StyleableDoubleProperty focusedOpacity = new StyleableDoubleProperty(
        StyleableProperties.FOCUSED_OPACITY,
        this,
        "focusedOpacity",
        0.0
    ) {
        @Override
        public void set(double v) {
            double oldValue = get();
            super.set(v);
            if (!Objects.equals(oldValue, v)) updateOpacity();
        }
    };

    private final StyleableDoubleProperty hoverOpacity = new StyleableDoubleProperty(
        StyleableProperties.HOVER_OPACITY,
        this,
        "hoverOpacity",
        0.0
    ) {
        @Override
        public void set(double v) {
            double oldValue = get();
            super.set(v);
            if (!Objects.equals(oldValue, v)) updateOpacity();
        }
    };

    private final StyleableObjectProperty<ElevationLevel> elevation = new StyleableObjectProperty<>(
        StyleableProperties.ELEVATION,
        this,
        "elevation",
        ElevationLevel.LEVEL0
    ) {
        @Override
        public void set(ElevationLevel newValue) {
            if (newValue == ElevationLevel.LEVEL0) {
                owner.setEffect(null);
                super.set(newValue);
                return;
            }

            Effect effect = owner.getEffect();
            if (effect == null) {
                owner.setEffect(newValue.toShadow());
                super.set(newValue);
                return;
            }
            if (!(effect instanceof DropShadow)) {
                return;
            }


            ElevationLevel oldValue = get();
            if (oldValue != null && newValue != null && oldValue != newValue)
                oldValue.animateTo((DropShadow) effect, newValue);
            super.set(newValue);
        }
    };

    public boolean isAnimated() {
        return animated.get();
    }

    /// Specifies whether to animate the background's opacity when the interaction state changes,
    /// [#updateOpacity()] and [#animate(double)].
    ///
    /// Can be set in CSS via the property: '-mfx-animated'.
    public StyleableBooleanProperty animatedProperty() {
        return animated;
    }

    public void setAnimated(boolean animated) {
        this.animated.set(animated);
    }

    public double getDisabledOpacity() {
        return disabledOpacity.get();
    }

    /// Specifies the surface's background opacity when the owner is disabled.
    ///
    /// Can be set in CSS via the property: '-mfx-disabled-opacity'.
    public StyleableDoubleProperty disabledOpacityProperty() {
        return disabledOpacity;
    }

    public void setDisabledOpacity(double disabledOpacity) {
        this.disabledOpacity.set(disabledOpacity);
    }

    public double getPressedOpacity() {
        return pressedOpacity.get();
    }

    /// Specifies the surface's background opacity when the owner is pressed.
    ///
    /// Can be set in CSS via the property: '-mfx-pressed-opacity'.
    public StyleableDoubleProperty pressedOpacityProperty() {
        return pressedOpacity;
    }

    public void setPressedOpacity(double pressedOpacity) {
        this.pressedOpacity.set(pressedOpacity);
    }

    public double getFocusedOpacity() {
        return focusedOpacity.get();
    }

    /// Specifies the surface's background opacity when the owner is focused.
    ///
    /// Can be set in CSS via the property: '-mfx-focused-opacity'.
    public StyleableDoubleProperty focusedOpacityProperty() {
        return focusedOpacity;
    }

    public void setFocusedOpacity(double focusedOpacity) {
        this.focusedOpacity.set(focusedOpacity);
    }

    public double getHoverOpacity() {
        return hoverOpacity.get();
    }

    /// Specifies the surface's background opacity when the owner is hovered.
    ///
    /// Can be set in CSS via the property: '-mfx-hover-opacity'.
    public StyleableDoubleProperty hoverOpacityProperty() {
        return hoverOpacity;
    }

    public void setHoverOpacity(double hoverOpacity) {
        this.hoverOpacity.set(hoverOpacity);
    }

    public ElevationLevel getElevation() {
        return elevation.get();
    }

    /// Specifies the elevation level of the owner, not the surface! Each level corresponds to a different [DropShadow]
    /// effect.[ElevationLevel#LEVEL0] corresponds to `null`.
    ///
    /// Unfortunately, since the crap that is JavaFX, handles the effects in strange ways, the shadow cannot be applied to the
    /// surface for various reasons. So, the effect will be applied on the owner instead.
    ///
    /// Can be set in CSS via the property: '-mfx-elevation'.
    public StyleableObjectProperty<ElevationLevel> elevationProperty() {
        return elevation;
    }

    public void setElevation(ElevationLevel elevation) {
        this.elevation.set(elevation);
    }

    //================================================================================
    // CssMetaData
    //================================================================================
    private static class StyleableProperties {
        private static final StyleablePropertyFactory<MFXSurface> FACTORY = new StyleablePropertyFactory<>(Region.getClassCssMetaData());
        private static final List<CssMetaData<? extends javafx.css.Styleable, ?>> cssMetaDataList;

        private static final CssMetaData<MFXSurface, Boolean> ANIMATED =
            FACTORY.createBooleanCssMetaData(
                "-mfx-animated",
                MFXSurface::animatedProperty,
                true
            );

        private static final CssMetaData<MFXSurface, Number> DISABLED_OPACITY =
            FACTORY.createSizeCssMetaData(
                "-mfx-disabled-opacity",
                MFXSurface::disabledOpacityProperty,
                0.0
            );

        private static final CssMetaData<MFXSurface, Number> PRESSED_OPACITY =
            FACTORY.createSizeCssMetaData(
                "-mfx-press-opacity",
                MFXSurface::pressedOpacityProperty,
                0.0
            );

        private static final CssMetaData<MFXSurface, Number> FOCUSED_OPACITY =
            FACTORY.createSizeCssMetaData(
                "-mfx-focus-opacity",
                MFXSurface::focusedOpacityProperty,
                0.0
            );

        private static final CssMetaData<MFXSurface, Number> HOVER_OPACITY =
            FACTORY.createSizeCssMetaData(
                "-mfx-hover-opacity",
                MFXSurface::hoverOpacityProperty,
                0.0
            );

        private static final CssMetaData<MFXSurface, ElevationLevel> ELEVATION =
            FACTORY.createEnumCssMetaData(
                ElevationLevel.class,
                "-mfx-elevation",
                MFXSurface::elevationProperty,
                ElevationLevel.LEVEL0
            );

        static {
            cssMetaDataList = StyleUtils.cssMetaDataList(
                Region.getClassCssMetaData(),
                ANIMATED,
                DISABLED_OPACITY, PRESSED_OPACITY, FOCUSED_OPACITY, HOVER_OPACITY,
                ELEVATION
            );
        }
    }

    public static List<CssMetaData<? extends javafx.css.Styleable, ?>> getClassCssMetaData() {
        return StyleableProperties.cssMetaDataList;
    }

    @Override
    public List<CssMetaData<? extends javafx.css.Styleable, ?>> getCssMetaData() {
        return getClassCssMetaData();
    }

    //================================================================================
    // Getters
    //================================================================================
    public Parent getOwner() {
        return owner;
    }

    public Optional<MFXRippleGenerator> getRippleGenerator() {
        return Optional.ofNullable(rg);
    }

    public Queue<State> getStates() {
        return states;
    }

    //================================================================================
    // Inner Classes
    //================================================================================
    public record State(
        int priority,
        Predicate<Parent> condition,
        Function<MFXSurface, Double> opacity
    ) {
        //================================================================================
        // Defaults
        //================================================================================

        /// Special state whose predicate is always `true`. Used when none of the other states is active. Opacity is `0.0`.
        public static final State FALLBACK = State.of(Integer.MIN_VALUE, _ -> true, _ -> 0.0);

        /// This state is activated when the node is disabled or the [PseudoClass] `:disabled` is active.
        /// The opacity is retrieved from [MFXSurface#disabledOpacityProperty()].
        public static final State DISABLED = State.of(
            0,
            n -> n.isDisabled() || PseudoClasses.DISABLED.isActiveOn(n),
            MFXSurface::getDisabledOpacity
        );

        /// This state is activated when the node is pressed or the [PseudoClass] `:pressed` is active.
        /// The opacity is retrieved from [MFXSurface#pressedOpacityProperty()].
        public static final State PRESSED = State.of(
            1,
            n -> n.isPressed() || PseudoClasses.PRESSED.isActiveOn(n),
            MFXSurface::getPressedOpacity
        );

        /// This state is activated when the node or any of its children are focused. Or the [PseudoClasses][PseudoClass]
        /// `:focused` or `:focus-within` are active.
        /// The opacity is retrieved from [MFXSurface#focusedOpacityProperty()].
        public static final State FOCUSED = State.of(
            2,
            n -> n.isFocused() || n.isFocusWithin() || PseudoClasses.isActiveOn(n, "focused", "focus-within"),
            MFXSurface::getFocusedOpacity
        );

        /// This state is activated when the node is hovered or the [PseudoClass] `:hover` is active.
        /// The opacity is retrieved from [MFXSurface#hoverOpacityProperty()].
        public static final State HOVER = State.of(
            3,
            n -> n.isHover() || PseudoClasses.HOVER.isActiveOn(n),
            MFXSurface::getHoverOpacity
        );

        /// This list contains all the default states, common to pretty much any surface/component. The order by priority
        /// is: disabled, pressed, focused, hover.
        public static final List<State> DEFAULT_STATES = List.of(DISABLED, PRESSED, FOCUSED, HOVER);

        //================================================================================
        // Constructors
        //================================================================================
        public static State of(int priority, Predicate<Parent> condition, Function<MFXSurface, Double> opacity) {
            return new State(priority, condition, opacity);
        }

        //================================================================================
        // Methods
        //================================================================================

        /// Shortcut for `condition.test(node)`.
        public boolean isActive(Parent node) {
            return condition.test(node);
        }

        /// Shortcut for `opacity.apply(surface)`.
        public double opacity(MFXSurface surface) {
            return opacity.apply(surface);
        }
    }
}
