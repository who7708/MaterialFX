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

import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.github.palexdev.mfxcore.builders.InsetsBuilder;
import io.github.palexdev.mfxcore.events.WhenEvent;
import io.github.palexdev.mfxcore.utils.EnumUtils;
import io.github.palexdev.mfxcore.utils.fx.CSSFragment;
import io.github.palexdev.mfxcore.utils.fx.LayoutUtils;
import io.github.palexdev.mfxeffects.animations.AnimationFactory;
import io.github.palexdev.mfxeffects.animations.Animations;
import io.github.palexdev.mfxeffects.animations.Animations.KeyFrames;
import io.github.palexdev.mfxeffects.animations.Animations.SequentialBuilder;
import io.github.palexdev.mfxeffects.animations.Animations.TimelineBuilder;
import io.github.palexdev.mfxeffects.animations.motion.M3Motion;
import io.github.palexdev.mfxeffects.beans.Position;
import io.github.palexdev.mfxeffects.ripple.MFXRippleGenerator;
import io.github.palexdev.mfxeffects.utils.StyleUtils;
import io.github.palexdev.mfxresources.base.properties.IconProperty;
import io.github.palexdev.mfxresources.fonts.IconDescriptor;
import io.github.palexdev.mfxresources.fonts.MFXFontIcon;
import io.github.palexdev.mfxresources.fonts.fontawesome.FontAwesomeSolid;
import javafx.animation.Animation;
import javafx.application.Application;
import javafx.css.*;
import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.CacheHint;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class Prototype extends Application {

    @Override
    public void start(Stage stage) {
        StackPane root = new StackPane();

        MFXIconWrapper wrapper = new MFXIconWrapper()
            .enableRipple(true)
            .setIcon(EnumUtils.randomEnum(FontAwesomeSolid.class))
            .setSize(56.0)
            .setAnimated(true)
            .setAnimationPreset(MFXIconWrapper.AnimationPresets.SLIDE_TOP_BOTTOM);

        Optional.ofNullable(wrapper.getRippleGenerator()).ifPresent(rg -> rg.setRippleColor(
            Color.rgb(255, 117, 20, 0.16)
        ));

        WhenEvent.intercept(wrapper, MouseEvent.MOUSE_PRESSED)
            .process(e -> action(e, wrapper))
            .register();

        CSSFragment.Builder.build()
            .select(wrapper)
            .background("rgba(255, 117, 20, 0.1)")
            .padding(InsetsBuilder.uniform(12.0))
            .style("-mfx-clip: \"squared 16px\"")
            .select(".mfx-icon-wrapper:hover")
            .background("rgba(255, 117, 20, 0.2)")
            .select(".mfx-icon-wrapper > .mfx-font-icon")
            .style("-mfx-size: 24px")
            .applyOn(root);

        root.getChildren().add(wrapper);
        Scene scene = new Scene(root, 400, 400);
        stage.setScene(scene);
        stage.show();
    }

    void action(MouseEvent e, MFXIconWrapper wrapper) {
        if (e.getButton() == MouseButton.PRIMARY)
            wrapper.setIcon(EnumUtils.randomEnum(FontAwesomeSolid.class));

        // Debug Info
        System.out.printf(
            "Info:{Size:%f, Ripple:%s, Children:%d}%n",
            wrapper.computeSize(),
            ((wrapper.getRippleGenerator() != null) ? "active" : "inactive"),
            wrapper.getChildrenUnmodifiable().size()
        );
    }

    class MFXIconWrapper extends Region {
        //================================================================================
        // Properties
        //================================================================================
        // Icon
        private boolean preserveClip = false;
        private MFXFontIcon _icon;
        private final IconProperty icon = new IconProperty() {
            @Override
            protected void invalidated() {
                MFXFontIcon oldIcon = _icon;
                if (isAnimated()) {
                    animate(oldIcon, get());
                } else {
                    updateChildren();
                }
            }
        };

        // Animation
        private IconAnimation animation;
        private BiFunction<MFXFontIcon, MFXFontIcon, IconAnimation> animationProvider;

        // Ripple
        private MFXRippleGenerator rg;

        //================================================================================
        // Constructors
        //================================================================================
        public MFXIconWrapper() {
            this(new MFXFontIcon(), -1.0);
        }

        public MFXIconWrapper(MFXFontIcon icon) {
            this(icon, -1.0);
        }

        public MFXIconWrapper(MFXFontIcon icon, double size) {
            setSize(size);
            setIcon(icon);
        }

        {
            getStyleClass().add("mfx-icon-wrapper");
            setMinSize(USE_PREF_SIZE, USE_PREF_SIZE);
            setMaxSize(USE_PREF_SIZE, USE_PREF_SIZE);
            addEventHandler(MouseEvent.MOUSE_PRESSED, _ -> requestFocus());
        }

        //================================================================================
        // Methods
        //================================================================================

        public MFXIconWrapper enableRipple(boolean enable) {
            return enableRipple(enable, me ->
                (me.getButton() == MouseButton.PRIMARY) ? Position.of(me.getX(), me.getY()) : null
            );
        }

        public MFXIconWrapper enableRipple(boolean enable, Function<MouseEvent, Position> posFn) {
            if (!enable && rg != null) {
                rg.dispose();
                getChildren().remove(rg);
                rg = null;
            } else if (enable && rg == null) {
                rg = new MFXRippleGenerator(this);
                rg.setMeToPosConverter(posFn);
                getChildren().add(rg);
                rg.enable();
            }
            return this;
        }

        protected void animate(MFXFontIcon oldIcon, MFXFontIcon newIcon) {
            if (oldIcon == null ||
                newIcon == null ||
                isDisabled() ||
                animationProvider == null
            ) {
                updateChildren();
                return;
            }

            if (animation != null)
                animation.stop();

            newIcon.setVisible(false);
            newIcon.setMouseTransparent(true);
            getChildren().add(newIcon);
            animation = animationProvider.apply(oldIcon, newIcon);
            animation.play(this);
            _icon = newIcon;
        }

        protected void updateChildren() {
            MFXFontIcon icon = getIcon();
            if (_icon != null) getChildren().remove(_icon);
            if (icon != null) {
                icon.setViewOrder(1);
                icon.setMouseTransparent(true);
                getChildren().add(icon);
            }
            _icon = icon;
        }

        protected void updateClip() {
            IconClip ic = getIconClip();
            if (ic == null) {
                setClip(null);
                return;
            }
            Node clip = ic.build(this);
            setClip(clip);
        }

        protected double computeSize() {
            MFXFontIcon icon = getIcon();
            if (icon == null) return 0.0;

            double size = getSize();
            double iw = snappedLeftInset() + LayoutUtils.snappedBoundWidth(icon) + snappedRightInset();
            double ih = snappedTopInset() + LayoutUtils.snappedBoundHeight(icon) + snappedBottomInset();
            return Math.max(size, Math.max(iw, ih));
        }

        //================================================================================
        // Overridden Methods
        //================================================================================

        @Override
        protected double computePrefWidth(double height) {
            return computeSize();
        }

        @Override
        protected double computePrefHeight(double width) {
            return computeSize();
        }

        @Override
        protected void layoutChildren() {
            for (Node child : getChildren()) {
                layoutInArea(child, 0, 0, getWidth(), getHeight(), 0, HPos.CENTER, VPos.CENTER);
            }
        }

        //================================================================================
        // Styleable Properties
        //================================================================================
        private final StyleableBooleanProperty animated = new SimpleStyleableBooleanProperty(
            StyleableProperties.ANIMATED,
            this,
            "animated",
            false
        ) {
            @Override
            public StyleOrigin getStyleOrigin() {
                return StyleOrigin.USER_AGENT;
            }
        };

        private final StyleableObjectProperty<AnimationPresets> animationPreset = new SimpleStyleableObjectProperty<>(
            StyleableProperties.ANIMATION_PRESET,
            this,
            "animationPreset",
            null
        ) {
            @Override
            protected void invalidated() {
                AnimationPresets ap = get();
                if (ap != null) setAnimationProvider(ap);
            }

            @Override
            public StyleOrigin getStyleOrigin() {
                return StyleOrigin.USER_AGENT;
            }
        };

        private final StyleableDoubleProperty size = new SimpleStyleableDoubleProperty(
            StyleableProperties.SIZE,
            this,
            "size",
            -1.0
        ) {
            @Override
            protected void invalidated() {
                requestLayout();
            }

            @Override
            public StyleOrigin getStyleOrigin() {
                return StyleOrigin.USER_AGENT;
            }
        };

        private final StyleableBooleanProperty enableRipple = new SimpleStyleableBooleanProperty(
            StyleableProperties.ENABLE_RIPPLE,
            this,
            "enableRipple",
            false
        ) {
            @Override
            protected void invalidated() {
                enableRipple(get());
            }

            @Override
            public StyleOrigin getStyleOrigin() {
                return StyleOrigin.USER_AGENT;
            }
        };

        private final StyleableObjectProperty<IconClip> iconClip = new SimpleStyleableObjectProperty<>(
            StyleableProperties.ICON_CLIP,
            this,
            "iconClip",
            null
        ) {
            @Override
            protected void invalidated() {
                updateClip();
            }

            @Override
            public StyleOrigin getStyleOrigin() {
                return StyleOrigin.USER_AGENT;
            }
        };

        public boolean isAnimated() {
            return animated.get();
        }

        public StyleableBooleanProperty animatedProperty() {
            return animated;
        }

        public MFXIconWrapper setAnimated(boolean animated) {
            this.animated.set(animated);
            return this;
        }

        public AnimationPresets getAnimationPreset() {
            return animationPreset.get();
        }

        public StyleableObjectProperty<AnimationPresets> animationPresetProperty() {
            return animationPreset;
        }

        public MFXIconWrapper setAnimationPreset(AnimationPresets animationPreset) {
            this.animationPreset.set(animationPreset);
            return this;
        }

        public double getSize() {
            return size.get();
        }

        public StyleableDoubleProperty sizeProperty() {
            return size;
        }

        public MFXIconWrapper setSize(double size) {
            this.size.set(size);
            return this;
        }

        public boolean isEnableRipple() {
            return enableRipple.get();
        }

        public StyleableBooleanProperty enableRippleProperty() {
            return enableRipple;
        }

        public MFXIconWrapper setEnableRipple(boolean enableRipple) {
            this.enableRipple.set(enableRipple);
            return this;
        }

        public IconClip getIconClip() {
            return iconClip.get();
        }

        public StyleableObjectProperty<IconClip> iconClipProperty() {
            return iconClip;
        }

        public void setIconClip(IconClip iconClip) {
            this.iconClip.set(iconClip);
        }

        //================================================================================
        // CssMetaData
        //================================================================================
        private static class StyleableProperties {
            private static final StyleablePropertyFactory<MFXIconWrapper> FACTORY = new StyleablePropertyFactory<>(Region.getClassCssMetaData());
            private static final List<CssMetaData<? extends Styleable, ?>> cssMetaDataList;

            private static final CssMetaData<MFXIconWrapper, Boolean> ANIMATED =
                FACTORY.createBooleanCssMetaData(
                    "-mfx-animated",
                    MFXIconWrapper::animatedProperty,
                    false
                );

            private static final CssMetaData<MFXIconWrapper, AnimationPresets> ANIMATION_PRESET =
                FACTORY.createEnumCssMetaData(
                    AnimationPresets.class,
                    "-mfx-animation-preset",
                    MFXIconWrapper::animationPresetProperty,
                    null
                );

            private static final CssMetaData<MFXIconWrapper, Number> SIZE =
                FACTORY.createSizeCssMetaData(
                    "-mfx-size",
                    MFXIconWrapper::sizeProperty,
                    -1.0
                );

            private static final CssMetaData<MFXIconWrapper, Boolean> ENABLE_RIPPLE =
                FACTORY.createBooleanCssMetaData(
                    "-mfx-enable-ripple",
                    MFXIconWrapper::enableRippleProperty,
                    false
                );

            private static final CssMetaData<MFXIconWrapper, IconClip> ICON_CLIP = new CssMetaData<>(
                "-mfx-clip", IconClipConverter.instance(), null
            ) {
                @Override
                public StyleableProperty<IconClip> getStyleableProperty(MFXIconWrapper styleable) {
                    return styleable.iconClipProperty();
                }

                @Override
                public boolean isSettable(MFXIconWrapper styleable) {
                    return !styleable.iconClipProperty().isBound();
                }
            };

            static {
                cssMetaDataList = StyleUtils.cssMetaDataList(
                    Region.getClassCssMetaData(),
                    ANIMATED, ANIMATION_PRESET, SIZE, ENABLE_RIPPLE, ICON_CLIP
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
        // Getters/Setters
        //================================================================================

        public MFXFontIcon getIcon() {
            return icon.get();
        }

        public IconProperty iconProperty() {
            return icon;
        }

        public MFXIconWrapper setIcon(MFXFontIcon icon) {
            this.icon.set(icon);
            return this;
        }

        public MFXIconWrapper setIcon(String description) {
            icon.setDescription(description);
            return this;
        }

        public MFXIconWrapper setIcon(IconDescriptor description) {
            icon.setDescription(description);
            return this;
        }

        public BiFunction<MFXFontIcon, MFXFontIcon, IconAnimation> getAnimationProvider() {
            return animationProvider;
        }

        public MFXIconWrapper setAnimationProvider(BiFunction<MFXFontIcon, MFXFontIcon, IconAnimation> animationProvider) {
            this.animationProvider = animationProvider;
            return this;
        }

        public MFXIconWrapper setAnimationProvider(AnimationPresets preset) {
            this.animationProvider = (o, n) -> preset.animate(this, o, n);
            return this;
        }

        public MFXRippleGenerator getRippleGenerator() {
            return rg;
        }

        //================================================================================
        // Inner Classes
        //================================================================================
        public static class IconAnimation {
            private final Animation animation;
            private final MFXFontIcon oldIcon;
            private final MFXFontIcon newIcon;

            public IconAnimation(Animation animation, MFXFontIcon oldIcon, MFXFontIcon newIcon) {
                this.animation = animation;
                this.oldIcon = oldIcon;
                this.newIcon = newIcon;
            }

            public void play(MFXIconWrapper wrapper) {
                if (animation != null) {
                    Animations.onStopped(animation, () -> onStopped(wrapper), true);
                    animation.play();
                }
            }

            public void stop() {
                if (!Animations.isStopped(animation))
                    animation.stop();
            }

            protected void onStopped(MFXIconWrapper wrapper) {
                wrapper.getChildren().remove(oldIcon);
            }

            public Animation animation() {return animation;}

            public MFXFontIcon oldIcon() {return oldIcon;}

            public MFXFontIcon newIcon() {return newIcon;}
        }

        public enum AnimationPresets {
            FADE {
                @Override
                public IconAnimation animate(MFXIconWrapper wrapper, MFXFontIcon oldIcon, MFXFontIcon newIcon) {
                    newIcon.setOpacity(0.0);
                    newIcon.setVisible(true);
                    M3Motion.MotionPreset motion = M3Motion.EXPRESSIVE_DEFAULT_EFFECTS;
                    return new IconAnimation(
                        new SequentialBuilder()
                            .addIf(oldIcon != null, AnimationFactory.FADE_OUT.build(oldIcon, motion.duration(), motion.curve()))
                            .add(AnimationFactory.FADE_IN.build(newIcon, motion.duration(), motion.curve()))
                            .getAnimation(),
                        oldIcon,
                        newIcon
                    );
                }
            },

            SCALE {
                @Override
                public IconAnimation animate(MFXIconWrapper wrapper, MFXFontIcon oldIcon, MFXFontIcon newIcon) {
                    oldIcon.setCacheHint(CacheHint.SCALE);
                    oldIcon.setCache(true);
                    newIcon.setCacheHint(CacheHint.SCALE);
                    newIcon.setCache(true);
                    newIcon.setScaleX(0);
                    newIcon.setScaleY(0);
                    newIcon.setOpacity(0.0);
                    newIcon.setVisible(true);
                    M3Motion.MotionPreset motion = M3Motion.EXPRESSIVE_DEFAULT_SPATIAL;
                    return new IconAnimation(
                        TimelineBuilder.build()
                            .add(
                                KeyFrames.of(motion.duration(), oldIcon.opacityProperty(), 0.0, motion.curve()),
                                KeyFrames.of(motion.duration(), oldIcon.scaleXProperty(), 0.0, motion.curve()),
                                KeyFrames.of(motion.duration(), oldIcon.scaleYProperty(), 0.0, motion.curve())
                            )
                            .add(
                                KeyFrames.of(motion.duration(), newIcon.opacityProperty(), 1.0, motion.curve()),
                                KeyFrames.of(motion.duration(), newIcon.scaleXProperty(), 1.0, motion.curve()),
                                KeyFrames.of(motion.duration(), newIcon.scaleYProperty(), 1.0, motion.curve())
                            ).getAnimation(),
                        oldIcon,
                        newIcon
                    ) {
                        @Override
                        protected void onStopped(MFXIconWrapper wrapper) {
                            super.onStopped(wrapper);
                            newIcon.setCacheHint(CacheHint.QUALITY);
                            newIcon.setOpacity(1.0);
                            newIcon.setScaleX(1.0);
                            newIcon.setScaleY(1.0);
                        }
                    };
                }
            },

            SLIDE_TOP {
                @Override
                public IconAnimation animate(MFXIconWrapper wrapper, MFXFontIcon oldIcon, MFXFontIcon newIcon) {
                    return slide(wrapper, oldIcon, AnimationFactory.SLIDE_OUT_TOP, newIcon, AnimationFactory.SLIDE_IN_TOP);
                }
            },

            SLIDE_BOTTOM {
                @Override
                public IconAnimation animate(MFXIconWrapper wrapper, MFXFontIcon oldIcon, MFXFontIcon newIcon) {
                    return slide(wrapper, oldIcon, AnimationFactory.SLIDE_OUT_BOTTOM, newIcon, AnimationFactory.SLIDE_IN_BOTTOM);
                }
            },

            SLIDE_BOTTOM_TOP {
                @Override
                public IconAnimation animate(MFXIconWrapper wrapper, MFXFontIcon oldIcon, MFXFontIcon newIcon) {
                    return slide(wrapper, oldIcon, AnimationFactory.SLIDE_OUT_BOTTOM, newIcon, AnimationFactory.SLIDE_IN_TOP);
                }
            },

            SLIDE_TOP_BOTTOM {
                @Override
                public IconAnimation animate(MFXIconWrapper wrapper, MFXFontIcon oldIcon, MFXFontIcon newIcon) {
                    return slide(wrapper, oldIcon, AnimationFactory.SLIDE_OUT_TOP, newIcon, AnimationFactory.SLIDE_IN_BOTTOM);
                }
            },

            SLIDE_RIGHT {
                @Override
                public IconAnimation animate(MFXIconWrapper wrapper, MFXFontIcon oldIcon, MFXFontIcon newIcon) {
                    return slide(wrapper, oldIcon, AnimationFactory.SLIDE_OUT_RIGHT, newIcon, AnimationFactory.SLIDE_IN_RIGHT);
                }
            },

            SLIDE_LEFT {
                @Override
                public IconAnimation animate(MFXIconWrapper wrapper, MFXFontIcon oldIcon, MFXFontIcon newIcon) {
                    return slide(wrapper, oldIcon, AnimationFactory.SLIDE_OUT_LEFT, newIcon, AnimationFactory.SLIDE_IN_LEFT);
                }
            },

            SLIDE_RIGHT_LEFT {
                @Override
                public IconAnimation animate(MFXIconWrapper wrapper, MFXFontIcon oldIcon, MFXFontIcon newIcon) {
                    return slide(wrapper, oldIcon, AnimationFactory.SLIDE_OUT_RIGHT, newIcon, AnimationFactory.SLIDE_IN_RIGHT);
                }
            },

            SLIDE_LEFT_RIGHT {
                @Override
                public IconAnimation animate(MFXIconWrapper wrapper, MFXFontIcon oldIcon, MFXFontIcon newIcon) {
                    return slide(wrapper, oldIcon, AnimationFactory.SLIDE_OUT_LEFT, newIcon, AnimationFactory.SLIDE_IN_LEFT);
                }
            },
            ;

            public abstract IconAnimation animate(MFXIconWrapper wrapper, MFXFontIcon oldIcon, MFXFontIcon newIcon);

            protected IconAnimation slide(MFXIconWrapper wrapper,
                                          MFXFontIcon oldIcon, AnimationFactory oldSlide,
                                          MFXFontIcon newIcon, AnimationFactory newSlide
            ) {
                clip(wrapper);
                newIcon.setOpacity(0.0);
                newIcon.setVisible(true);
                M3Motion.MotionPreset motion = M3Motion.EXPRESSIVE_FAST_SPATIAL;
                return new IconAnimation(
                    TimelineBuilder.build()
                        .add(KeyFrames.of(motion.duration(), oldIcon.opacityProperty(), 0.0, motion.curve()))
                        .add(oldSlide.keyFrames(oldIcon, motion.millis(), motion.curve()))
                        .add(KeyFrames.of(motion.duration(), newIcon.opacityProperty(), 1.0, motion.curve()))
                        .add(newSlide.keyFrames(newIcon, motion.millis(), motion.curve()))
                        .getAnimation(),
                    oldIcon,
                    newIcon
                ) {
                    @Override
                    protected void onStopped(MFXIconWrapper wrapper) {
                        super.onStopped(wrapper);
                        if (!wrapper.preserveClip) wrapper.setIconClip(null);
                        newIcon.setTranslateX(0);
                        newIcon.setTranslateY(0);
                        newIcon.setOpacity(1.0);
                    }
                };
            }

            /**
             * Used by the slide animations to clip the {@link MFXIconWrapper} so that icons that go outside its bounds are
             * hidden. The animations automatically remove it once they stop/end.
             */
            protected void clip(MFXIconWrapper wrapper) {
                if (wrapper.getClip() != null) {
                    wrapper.preserveClip = true;
                    return;
                }
                wrapper.setIconClip(IconClip.of(ClipShape.SQUARED, 0.0));
                wrapper.preserveClip = false;
            }
        }

        public record IconClip(
            ClipShape type,
            double radius
        ) {
            public static IconClip of(ClipShape type, double radius) {
                return new IconClip(type, radius);
            }

            public Node build(Region region) {
                return type.buildClip(region, radius);
            }
        }

        public enum ClipShape {
            ROUNDED {
                @Override
                public Node buildClip(Region region, double radius) {
                    Circle circle = new Circle();
                    if (radius < 0) {
                        circle.radiusProperty().bind(region.layoutBoundsProperty()
                            .map(b -> Math.max(b.getWidth(), b.getHeight()) / 2.0));
                    } else {
                        circle.setRadius(radius);
                    }
                    circle.centerXProperty().bind(region.widthProperty().divide(2.0));
                    circle.centerYProperty().bind(region.heightProperty().divide(2.0));
                    return circle;
                }
            },
            SQUARED {
                @Override
                public Node buildClip(Region region, double radius) {
                    Rectangle rect = new Rectangle();
                    rect.widthProperty().bind(region.widthProperty());
                    rect.heightProperty().bind(region.heightProperty());
                    rect.setArcWidth(radius);
                    rect.setArcHeight(radius);
                    return rect;
                }
            };

            public abstract Node buildClip(Region region, double radius);
        }
    }

    static class IconClipConverter extends StyleConverter<String, MFXIconWrapper.IconClip> {
        private static final IconClipConverter INSTANCE = new IconClipConverter();

        public static IconClipConverter instance() {
            return INSTANCE;
        }

        private IconClipConverter() {}

        @Override
        public MFXIconWrapper.IconClip convert(ParsedValue<String, MFXIconWrapper.IconClip> value, Font font) {
            String sVal = value.getValue();
            if (sVal == null || sVal.isBlank()) return null;

            String[] sVals = sVal.split(" ");
            MFXIconWrapper.ClipShape shape = MFXIconWrapper.ClipShape.valueOf(sVals[0].toUpperCase());
            double px = convertSize(sVals);
            return new MFXIconWrapper.IconClip(shape, px);
        }

        private double convertSize(String[] sVals) {
            if (sVals.length == 1) return -1.0;

            Pattern pattern = Pattern.compile("([+-]?\\d*\\.?\\d+)([a-zA-Z%]+)");
            Matcher matcher = pattern.matcher(sVals[1].trim());
            if (!matcher.matches())
                throw new IllegalArgumentException("Invalid size format: " + sVals[1]);

            double val = Double.parseDouble(matcher.group(1));
            SizeUnits units = SizeUnits.valueOf(matcher.group(2).toUpperCase());
            return new Size(val, units).pixels();
        }
    }
}
