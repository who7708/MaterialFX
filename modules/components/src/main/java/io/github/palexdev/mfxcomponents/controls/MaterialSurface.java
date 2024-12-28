/*
 * Copyright (C) 2024 Parisi Alessandro - alessandro.parisi406@gmail.com
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

import io.github.palexdev.mfxcomponents.controls.base.MFXStyleable;
import io.github.palexdev.mfxcomponents.theming.enums.PseudoClasses;
import io.github.palexdev.mfxcore.base.properties.styleable.StyleableBooleanProperty;
import io.github.palexdev.mfxcore.base.properties.styleable.StyleableDoubleProperty;
import io.github.palexdev.mfxcore.base.properties.styleable.StyleableObjectProperty;
import io.github.palexdev.mfxcore.utils.fx.StyleUtils;
import io.github.palexdev.mfxeffects.animations.Animations;
import io.github.palexdev.mfxeffects.animations.motion.M3Motion;
import io.github.palexdev.mfxeffects.enums.ElevationLevel;
import io.github.palexdev.mfxeffects.ripple.MFXRippleGenerator;
import javafx.animation.Animation;
import javafx.beans.InvalidationListener;
import javafx.css.CssMetaData;
import javafx.css.PseudoClass;
import javafx.css.Styleable;
import javafx.css.StyleablePropertyFactory;
import javafx.scene.Node;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.Effect;
import javafx.scene.layout.Background;
import javafx.scene.layout.Region;

/**
 * Material Design 3 components are stratified. Different layers have different purposes. Among the various layers, two
 * are quite important: the state layer and the focus ring layer.
 * <p>
 * <p> - The {@code state layer} is a region on which a color, which is in contrast with the main layer color, is applied
 * at specific levels of opacity according to the various interaction states. Hover -> 8%; Press and Focus -> 12%;
 * Dragged -> 16%. Additionally, on this layer ripple effect can be generated to further emphasize press/click interactions.
 * <p> - The {@code focus ring layer} is an effect applied only when the component is being focused by a keyboard
 * event, so {@link Node#focusVisibleProperty()} is true. A border is applied around the component.
 * <p></p>
 * There are also components that may also need a shadow effect to further separate themselves from other UI elements,
 * making them appear 3D. This is implemented with some caveats through the {@link #elevationProperty()}.
 * <p></p>
 * The goal of this region is to replicate such effects while still keeping the nodes count as low as possible.
 * Like the name suggests, this is intended to be used like an extra background on top of another region. For this reason,
 * it needs the instance of the region, called 'owner', on which this will act as an overlay.
 * <p></p>
 * The overlay is carried by a separate region that can be selected in CSS with the ".bg" style class. Despite having
 * another node just for the overlay, it is still more performant than animating the background color because
 * using {@link Region#setBackground(Background)} is much more expensive than just animating a node's opacity.
 * <p>
 * There are pros and cons deriving from this:
 * <p> Pros:
 * <p> - Implementing the {@code state layer} is much easier, as it's enough to specify the surface background color,
 * which will then just change in opacity as needed
 * <p> - The transition between the different states is a short animation, which makes component look prettier
 * <p> Cons:
 * <p> - I expect a slight impact on performance since we use two extra nodes now. And also because of the animations, however
 * they can be disabled globally via a public static flag, or per component via {@link #animatedProperty()}
 * (more convenient to set it through CSS since most of the time the MaterialSurface is part of a skin)
 * <p> - I expect another very slight impact on performance because to change the opacity according to the current interaction state,
 * a listener is added on the owner's {@link Node#getPseudoClassStates()}. Being a Set, the lookup will still be
 * pretty fast though. And also, before resorting to the lookup, some checks are first performed directly on the node's
 * properties.
 * <p></p>
 * This is intended to be used in skins of components that need to visually distinguish between the various interaction
 * states. When the skin is being disposed, {@link #dispose()} should be called too. Also, always make sure that this
 * is and remains the first child of the component to avoid this from covering the other children.
 * <p></p>
 * A recent update reworked the states' system. When a change is detected on the node and the background opacity should
 * change, the target opacity is determined by {@link #getTargetOpacity()}. To make the surface more flexible, and allow
 * for custom states (like "selected" which is technically out of specs), a "priority" list is used, {@link #getStates()}.
 * One can easily add/remove/replace states to adapt the surface to its own needs.
 */
// TODO dragged state is not implemented yet
public class MaterialSurface extends Region implements MFXStyleable {
    //================================================================================
    // Static Properties
    //================================================================================
    public static boolean GLOBAL_ANIMATED = true;

    //================================================================================
    // Properties
    //================================================================================
    private Region owner;
    private MFXRippleGenerator rg;
    private InvalidationListener stateListener;
    private final Queue<State> states = new PriorityQueue<>(Comparator.comparingInt(State::getPriority));

    protected final Region bg;
    protected Animation animation;
    protected double lastOpacity;

    //================================================================================
    // Constructors
    //================================================================================
    public MaterialSurface(Region owner) {
        this.owner = owner;
        states.addAll(State.DEFAULTS);

        bg = new Region();
        bg.getStyleClass().add("bg");
        bg.setOpacity(0.0);

        rg = new MFXRippleGenerator(bg);
        initialize();
    }

    //================================================================================
    // Methods
    //================================================================================
    private void initialize() {
        setManaged(false);
        setMaxSize(USE_PREF_SIZE, USE_PREF_SIZE);
        getStyleClass().setAll(defaultStyleClasses());
        getChildren().addAll(bg, rg);

        stateListener = i -> updateBackground();
        owner.getPseudoClassStates().addListener(stateListener);
    }

    /**
     * Fluent way to set up the surface's {@link MFXRippleGenerator}.
     */
    public MaterialSurface initRipple(Consumer<MFXRippleGenerator> config) {
        config.accept(rg);
        return this;
    }

    /**
     * This is the core method responsible for setting the surface's background opacity.
     * <p>
     * The opacity is determined by the {@link #getTargetOpacity()} method.
     * <p>
     * The opacity is set immediately or through an animation started by {@link #animate(double)}.
     */
    public void updateBackground() {
        double target = getTargetOpacity();
        if (lastOpacity == target) return;
        if (GLOBAL_ANIMATED && isAnimated()) {
            animate(target);
        } else {
            bg.setOpacity(target);
        }
        lastOpacity = target;
    }

    /**
     * Stops any previous animation, then creates a new one and transitions the background opacity to the target value.
     *
     * @param opacity the opacity specified by the new state
     */
    protected void animate(double opacity) {
        if (Animations.isPlaying(animation)) animation.stop();
        animation = Animations.TimelineBuilder.build()
            .add(Animations.KeyFrames.of(M3Motion.SHORT4, bg.opacityProperty(), opacity))
            .getAnimation();
        animation.play();
    }

    /**
     * Iterates over the interaction states in {@link #getStates()}. The first state whose {@link Predicate} returns
     * {@code true} determines the target opacity, given by its {@link State#getOpacityFunction()}.
     * <p></p>
     * In case no state results "active", then uses {@link State#FALLBACK}.
     */
    protected double getTargetOpacity() {
        for (State state : states) {
            if (state.isActive(owner))
                return state.opacity(this);
        }
        return State.FALLBACK.opacity(this);
    }

    /**
     * Removes any added listener, disposes the {@link MFXRippleGenerator}.
     */
    public void dispose() {
        getChildren().clear();
        rg.dispose();
        rg = null;
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
        return List.of("surface");
    }

    @Override
    protected void layoutChildren() {
        double w = getWidth();
        double h = getHeight();
        if (rg != null)
            rg.resizeRelocate(0, 0, w, h);
        bg.resizeRelocate(0, 0, w, h);
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
            if (!Objects.equals(oldValue, v)) updateBackground();
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
            if (!Objects.equals(oldValue, v)) updateBackground();
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
            if (!Objects.equals(oldValue, v)) updateBackground();
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
            if (!Objects.equals(oldValue, v)) updateBackground();
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

    /**
     * Specifies whether to animate the background's opacity when the interaction state changes,
     * see {@link #updateBackground()} and {@link #animate(double)}.
     * <p>
     * Can be set in CSS via the property: '-mfx-animated'.
     */
    public StyleableBooleanProperty animatedProperty() {
        return animated;
    }

    public void setAnimated(boolean animated) {
        this.animated.set(animated);
    }

    public double getDisabledOpacity() {
        return disabledOpacity.get();
    }

    /**
     * Specifies the surface's background opacity when the owner is disabled.
     * <p>
     * Can be set in CSS via the property: '-mfx-disabled-opacity'.
     */
    public StyleableDoubleProperty disabledOpacityProperty() {
        return disabledOpacity;
    }

    public void setDisabledOpacity(double disabledOpacity) {
        this.disabledOpacity.set(disabledOpacity);
    }

    public double getPressedOpacity() {
        return pressedOpacity.get();
    }

    /**
     * Specifies the surface's background opacity when the owner is pressed.
     * <p>
     * Can be set in CSS via the property: '-mfx-pressed-opacity'.
     */
    public StyleableDoubleProperty pressedOpacityProperty() {
        return pressedOpacity;
    }

    public void setPressedOpacity(double pressedOpacity) {
        this.pressedOpacity.set(pressedOpacity);
    }

    public double getFocusedOpacity() {
        return focusedOpacity.get();
    }

    /**
     * Specifies the surface's background opacity when the owner is focused.
     * <p>
     * Can be set in CSS via the property: '-mfx-focused-opacity'.
     */
    public StyleableDoubleProperty focusedOpacityProperty() {
        return focusedOpacity;
    }

    public void setFocusedOpacity(double focusedOpacity) {
        this.focusedOpacity.set(focusedOpacity);
    }

    public double getHoverOpacity() {
        return hoverOpacity.get();
    }

    /**
     * Specifies the surface's background opacity when the owner is hovered.
     * <p>
     * Can be set in CSS via the property: '-mfx-hover-opacity'.
     */
    public StyleableDoubleProperty hoverOpacityProperty() {
        return hoverOpacity;
    }

    public void setHoverOpacity(double hoverOpacity) {
        this.hoverOpacity.set(hoverOpacity);
    }

    public ElevationLevel getElevation() {
        return elevation.get();
    }

    /**
     * Specifies the elevation level of the owner, not the surface! Each level corresponds to a different {@link DropShadow}
     * effect. {@link ElevationLevel#LEVEL0} corresponds to {@code null}.
     * <p>
     * Unfortunately since the crap that is JavaFX, handles the effects in strange ways, the shadow cannot be applied to the
     * surface for various reasons. So, the effect will be applied on the owner instead.
     * <p>
     * Can be set in CSS via the property: '-mfx-elevation'.
     */
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
        private static final StyleablePropertyFactory<MaterialSurface> FACTORY = new StyleablePropertyFactory<>(Region.getClassCssMetaData());
        private static final List<CssMetaData<? extends Styleable, ?>> cssMetaDataList;

        private static final CssMetaData<MaterialSurface, Boolean> ANIMATED =
            FACTORY.createBooleanCssMetaData(
                "-mfx-animated",
                MaterialSurface::animatedProperty,
                true
            );

        private static final CssMetaData<MaterialSurface, Number> DISABLED_OPACITY =
            FACTORY.createSizeCssMetaData(
                "-mfx-disabled-opacity",
                MaterialSurface::disabledOpacityProperty,
                0.0
            );

        private static final CssMetaData<MaterialSurface, Number> PRESSED_OPACITY =
            FACTORY.createSizeCssMetaData(
                "-mfx-press-opacity",
                MaterialSurface::pressedOpacityProperty,
                0.0
            );

        private static final CssMetaData<MaterialSurface, Number> FOCUSED_OPACITY =
            FACTORY.createSizeCssMetaData(
                "-mfx-focus-opacity",
                MaterialSurface::focusedOpacityProperty,
                0.0
            );

        private static final CssMetaData<MaterialSurface, Number> HOVER_OPACITY =
            FACTORY.createSizeCssMetaData(
                "-mfx-hover-opacity",
                MaterialSurface::hoverOpacityProperty,
                0.0
            );

        private static final CssMetaData<MaterialSurface, ElevationLevel> ELEVATION =
            FACTORY.createEnumCssMetaData(
                ElevationLevel.class,
                "-mfx-elevation",
                MaterialSurface::elevationProperty,
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

    public static List<CssMetaData<? extends Styleable, ?>> getClassCssMetaData() {
        return StyleableProperties.cssMetaDataList;
    }

    @Override
    public List<CssMetaData<? extends Styleable, ?>> getCssMetaData() {
        return getClassCssMetaData();
    }

    //================================================================================
    // Getters
    //================================================================================
    public Region getOwner() {
        return owner;
    }

    public MFXRippleGenerator getRippleGenerator() {
        return rg;
    }

    public Queue<State> getStates() {
        return states;
    }

    //================================================================================
    // Inner Classes
    //================================================================================

    /**
     * This class represents interaction states with a node called 'owner'. It's a simple wrapper for two values:
     * <p> 1) A {@link Predicate} used to check whether the state is active on the owner
     * <p> 2) A {@link Function} that determines the state's opacity
     * <p></p>
     * There are 5 default states:
     * <p> 1) {@link #FALLBACK}
     * <p> 2) {@link #DISABLED}
     * <p> 3) {@link #PRESSED}
     * <p> 4) {@link #FOCUSED}
     * <p> 5) {@link #HOVER}
     * <p>
     * Their opacity, depend on the properties defined in {@link MaterialSurface} (hence why a function and not a supplier).
     */
    public static class State {
        //================================================================================
        // Defaults
        //================================================================================

        /**
         * Special state whose predicate is always {@code true}. Used when none of the other states is active. Opacity is 0.0.
         */
        public static final State FALLBACK = State.of(Integer.MIN_VALUE, n -> true, s -> 0.0);

        /**
         * This state is activated when the node is disabled or the {@link PseudoClass} ':disabled' is active.
         * The opacity is retrieved from {@link MaterialSurface#disabledOpacityProperty()}.
         */
        public static final State DISABLED = State.of(
            0,
            n -> n.isDisabled() || isPseudoActive(n, PseudoClasses.DISABLED),
            MaterialSurface::getDisabledOpacity
        );

        /**
         * This state is activated when the node is pressed or the {@link PseudoClass} ':pressed' is active.
         * The opacity is retrieved from {@link MaterialSurface#pressedOpacityProperty()}.
         */
        public static final State PRESSED = State.of(
            1,
            n -> n.isPressed() || isPseudoActive(n, PseudoClasses.PRESSED),
            MaterialSurface::getPressedOpacity
        );

        /**
         * This state is activated when the node or any of its children are focused. Or the {@link PseudoClass}es
         * ':focused' or ':focus-within' are active.
         * The opacity is retrieved from {@link MaterialSurface#focusedOpacityProperty()}.
         */
        public static final State FOCUSED = State.of(
            2,
            n -> n.isFocused() || n.isFocusWithin() || isPseudoActive(n, PseudoClasses.FOCUSED, PseudoClasses.FOCUS_WITHIN),
            MaterialSurface::getFocusedOpacity
        );

        /**
         * This state is activated when the node is hovered or the {@link PseudoClass} ':hover: is active.
         * The opacity is retrieved from {@link MaterialSurface#hoverOpacityProperty()}.
         */
        public static final State HOVER = State.of(
            3,
            n -> n.isHover() || isPseudoActive(n, PseudoClasses.HOVER),
            MaterialSurface::getHoverOpacity
        );

        /**
         * This list contains all the default states, common to pretty much any surface/component. The order by priority
         * is: disabled, pressed, focused, hover.
         */
        public static final List<State> DEFAULTS = List.of(DISABLED, PRESSED, FOCUSED, HOVER);

        //================================================================================
        // Properties
        //================================================================================
        private final int priority;
        private final Predicate<Node> condition;
        private final Function<MaterialSurface, Double> opacityFunction;

        public State(int priority, Predicate<Node> condition, Function<MaterialSurface, Double> opacityFunction) {
            this.priority = priority;
            this.condition = condition;
            this.opacityFunction = opacityFunction;
        }

        public static State of(int priority, Predicate<Node> condition, Function<MaterialSurface, Double> opacityFunction) {
            return new State(priority, condition, opacityFunction);
        }

        //================================================================================
        // Methods
        //================================================================================

        /**
         * Shortcut method for `getCondition().test(node)`.
         */
        public boolean isActive(Node node) {
            return condition.test(node);
        }

        /**
         * Shortcut method for `getOpacityFunction().apply(surface)`.
         */
        public double opacity(MaterialSurface surface) {
            return opacityFunction.apply(surface);
        }

        //================================================================================
        // Static Methods
        //================================================================================

        /**
         * Convenience method to check whether at least one of the given pseudo classes are active on the owner node.
         */
        public static boolean isPseudoActive(Node owner, PseudoClass... classes) {
            for (PseudoClass pClass : classes) {
                if (PseudoClasses.isActiveOn(owner, pClass))
                    return true;
            }
            return false;
        }

        /**
         * Convenience method to check whether at least one of the given pseudo classes are active on the owner node.
         */
        public static boolean isPseudoActive(Node owner, PseudoClasses... classes) {
            for (PseudoClasses pClass : classes) {
                if (pClass.isActiveOn(owner))
                    return true;
            }
            return false;
        }

        //================================================================================
        // Getters
        //================================================================================

        /**
         * This parameter determines which state wins over another.
         * <p>
         * In {@link MaterialSurface} states are stored in a {@link PriorityQueue} for two reasons:
         * <p> 1) To clearly state that there's a hierarchy for states
         * <p> 2) To more easily add custom states
         */
        public int getPriority() {
            return priority;
        }

        /**
         * @return the {@link Predicate} used to check whether the state is active on a given node
         */
        public Predicate<Node> getCondition() {
            return condition;
        }

        /**
         * @return the {@link Function} which determines the state's opacity
         */
        public Function<MaterialSurface, Double> getOpacityFunction() {
            return opacityFunction;
        }
    }
}
