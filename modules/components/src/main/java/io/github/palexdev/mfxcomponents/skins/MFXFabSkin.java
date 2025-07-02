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

import io.github.palexdev.mfxcomponents.behaviors.MFXButtonBehaviorBase;
import io.github.palexdev.mfxcomponents.controls.MFXFab;
import io.github.palexdev.mfxcomponents.controls.MFXSurface;
import io.github.palexdev.mfxcomponents.controls.base.MFXLabeled;
import io.github.palexdev.mfxcomponents.skins.base.MFXLabeledSkin;
import io.github.palexdev.mfxcore.controls.BoundLabel;
import io.github.palexdev.mfxcore.controls.Label;
import io.github.palexdev.mfxcore.utils.fx.LabelMeasurementCache;
import io.github.palexdev.mfxcore.utils.fx.LayoutUtils;
import io.github.palexdev.mfxcore.utils.fx.TextMeasurementCache;
import io.github.palexdev.mfxeffects.animations.Animations;
import io.github.palexdev.mfxeffects.animations.Animations.KeyFrames;
import io.github.palexdev.mfxeffects.animations.Animations.TimelineBuilder;
import io.github.palexdev.mfxeffects.animations.motion.M3Motion;
import io.github.palexdev.mfxeffects.beans.Position;
import io.github.palexdev.mfxeffects.ripple.MFXRippleGenerator;
import io.github.palexdev.mfxresources.fonts.MFXFontIcon;
import io.github.palexdev.mfxresources.fonts.MFXIconWrapper;
import javafx.animation.Animation;
import javafx.geometry.Bounds;
import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.shape.Rectangle;

import static io.github.palexdev.mfxcore.events.WhenEvent.intercept;
import static io.github.palexdev.mfxcore.observables.When.onInvalidated;

/// Default skin implementation for all [MFXFabs][MFXFab]. Extends [MFXLabeledSkin] and expects behaviors of type
/// [MFXButtonBehaviorBase].
///
/// Because of the numerous animations (and the type) shown by the Material Design specs, this skin is quite complex and
/// it's precisely tweaked for them to play correctly.
///
/// It is composed of four nodes:
/// - the label which displays the icon and the text. This label's truncation mechanism is completely disabled (see [Label]),
///   but it's also clipped by [#clip()] to prevent the text from overflowing when extending/collapsing the FAB.
/// - the icon from [MFXFab#iconProperty()] is wrapped in a [MFXIconWrapper] because it already has what we need
///   to animate the icon change when the FAB is not extended
/// - the [MFXSurface] node to show interaction states (by applying an overlay background)
/// - the [MFXRippleGenerator] responsible for generating ripple effects
///
/// The FAB expand/collapse is handled by [#extend(boolean, boolean)].
public class MFXFabSkin extends MFXLabeledSkin<MFXFab, MFXButtonBehaviorBase<MFXFab>> {
    //================================================================================
    // Properties
    //================================================================================
    private final MFXIconWrapper iconWrapper;
    private final MFXSurface surface;
    private final MFXRippleGenerator rg;

    protected boolean init = false;
    protected boolean switching = false;
    protected LabelMeasurementCache lmc;
    protected Animation animation;

    //================================================================================
    // Constructors
    //================================================================================
    public MFXFabSkin(MFXFab fab) {
        super(fab);

        // Init
        iconWrapper = new MFXIconWrapper(fab.getIcon());
        iconWrapper.iconProperty().bindBidirectional(fab.iconProperty());
        iconWrapper.animatedProperty().bind(fab.extendedProperty().not());
        label.setGraphic(iconWrapper);

        surface = new MFXSurface(fab);
        rg = new MFXRippleGenerator(fab);
        rg.getStyleClass().add("surface-ripple");
        rg.setMeToPosConverter(me ->
            (me.getButton() == MouseButton.PRIMARY) ? Position.of(me.getX(), me.getY()) : null
        );
        rg.enable();

        initTextMeasurementCache();
        clip();

        // Finalize
        addListeners();
        getChildren().setAll(surface, rg, label);
    }

    //================================================================================
    // Methods
    //================================================================================

    /// Adds the following listeners:
    /// - On [MFXFab#extendedProperty()] to call [#extend(boolean, boolean)]. Note that here we use a special flag `switching`
    ///   to avoid the [LabelMeasurementCache] from triggering the same method. The flat is reset immediately after.
    /// - On the [LabelMeasurementCache] to properly size and layout the FAB when any of its dependencies change
    /// - On [MFXFab#iconProperty()] to properly size and layout the FAB. To be precise, we call [#extend(boolean, boolean)]
    ///   two times: the first one with both parameters to `false` (collapsing the FAB if it is extended), the second one
    ///   with [MFXFab#isExtended()] (re-extending the FAB if it is now collapsed) and `true` as the parameters.
    ///   This animated behavior is showcased in the Material Design 3 specs.
    protected void addListeners() {
        MFXFab fab = getSkinnable();
        listeners(
            onInvalidated(fab.extendedProperty())
                .then(e -> {
                    switching = true;
                    extend(e, true);
                    switching = false;
                }),
            onInvalidated(lmc)
                .condition(_ -> !switching)
                .then(_ -> extend(fab.isExtended(), false)),
            onInvalidated(fab.iconProperty())
                .then(_ -> {
                    extend(false, false);
                    extend(fab.isExtended(), true);
                })
        );
    }

    /// This is responsible for transitioning the FAB from collapsed to extended and vice versa.
    ///
    /// Three values are adjusted here:
    /// - The FAB's width through its [MFXFab#prefWidthProperty()], the value is given by [#computeTargetWidth(boolean)]
    /// - The label's [Node#translateXProperty()] so that the label or the icon are always centered in the FAB, the value
    ///   is given by [#computeTargetX(boolean, double)]
    /// - The text's opacity, `1.0` is extended, `0.0` otherwise. Note! I said the text opacity, not the label!
    ///   See [MFXLabeled]
    ///
    /// @param extend   specified whether to extend or collapse
    /// @param animated specifies whether to extend/collapse with an animation or not
    protected void extend(boolean extend, boolean animated) {
        MFXFab fab = getSkinnable();
        double targetW = snapSizeX(computeTargetWidth(extend));
        double targetX = snapSizeX(computeTargetX(extend, targetW));
        double targetO = extend ? 1.0 : 0.0;

        if (Animations.isPlaying(animation))
            animation.stop();

        if (!animated) {
            fab.setPrefWidth(targetW);
            label.setTranslateX(targetX);
            fab.setTextOpacity(targetO);
            return;
        }

        M3Motion.MotionPreset eMotion = M3Motion.EXPRESSIVE_DEFAULT_EFFECTS;
        M3Motion.MotionPreset sMotion = M3Motion.EXPRESSIVE_FAST_SPATIAL;
        animation = TimelineBuilder.build()
            .add(KeyFrames.of(sMotion.millis(), fab.prefWidthProperty(), targetW, sMotion.curve()))
            .add(KeyFrames.of(sMotion.millis(), label.translateXProperty(), targetX, sMotion.curve()))
            .add(KeyFrames.of(eMotion.millis(), fab.textOpacityProperty(), targetO, eMotion.curve()))
            .getAnimation();
        animation.play();
    }

    /// Computes the FAB's width according to its extended state. Before the computation we refresh the CSS by calling
    /// [Node#applyCss()]!
    ///
    /// Depending on the state, the target is given by:
    /// - Extended: the label's width, which we get from the cache [LabelMeasurementCache]
    /// - Collapsed: the icon's width or `0.0` if there's no icon
    ///
    /// The final value, however, is the maximum between: the width given by the [MFXFab#minSizeProperty()] and
    /// the computed value including horizontal padding.
    protected double computeTargetWidth(boolean extended) {
        MFXFab fab = getSkinnable();
        fab.applyCss(); // Ensure minSize is correct
        double minW = fab.getMinSize().width();

        MFXFontIcon icon = fab.getIcon();
        double target;
        if (extended) {
            target = lmc.get().width();
        } else {
            target = icon != null ? LayoutUtils.snappedBoundWidth(icon) : 0.0;
        }
        return Math.max(minW, snappedLeftInset() + target + snappedRightInset());
    }

    /// Computes the label's x displacement so that its icon or the label as a whole always appear at the center of the FAB.
    /// Since this is intended to be used in conjunction with [#computeTargetWidth(boolean)], to avoid recomputing such value,
    /// it's accepted as an argument.
    ///
    /// Depending on the state, the target is given by:
    /// - Extended: `(targetW - labelW) / 2.0 - startX`. The label's width is not recomputed but retrieved from the cache
    ///   [LabelMeasurementCache].
    /// - Collapsed: `(targetW - iW) / 2.0 - startX`. Where `iW` is the icon's width or `0.0` if there's no icon.
    ///
    /// The `startX` value is the natural x position of the label.
    protected double computeTargetX(boolean extended, double targetW) {
        double target;
        double startX = label.getLayoutX();
        if (extended) {
            target = (targetW - lmc.get().width()) / 2.0 - startX;
        } else {
            double iW = LayoutUtils.snappedBoundWidth(iconWrapper);
            return (targetW - iW) / 2.0 - startX;
        }
        return target;
    }

    /// Clips the FAB's label so that the text does not overflow when animating between extended/collapsed states.
    protected void clip() {
        MFXFab fab = getSkinnable();
        Rectangle r = new Rectangle();
        r.widthProperty().bind(fab.widthProperty());
        r.heightProperty().bind(fab.heightProperty());
        label.setClip(r);
    }

    //================================================================================
    // Overridden Methods
    //================================================================================
    @Override
    protected void initBehavior(MFXButtonBehaviorBase<MFXFab> behavior) {
        MFXFab fab = getSkinnable();
        super.initBehavior(behavior);
        events(
            intercept(fab, MouseEvent.MOUSE_PRESSED).process(behavior::mousePressed),
            intercept(fab, MouseEvent.MOUSE_CLICKED).process(behavior::mouseClicked),
            intercept(fab, KeyEvent.KEY_PRESSED).process(e -> behavior.keyPressed(e, _ -> {
                if (e.getCode() == KeyCode.ENTER || e.getCode() == KeyCode.SPACE) {
                    Bounds b = fab.getLayoutBounds();
                    rg.generate(b.getCenterX(), b.getCenterY());
                    rg.release();
                }
            }))
        );
    }

    /// Overridden to not use [TextMeasurementCache] but rather [LabelMeasurementCache]. In this skin, because of the animations,
    /// it's not enough to have the text's sizes, we need the full size of the label, which includes many more parameters.
    @Override
    protected void initTextMeasurementCache() {
        lmc = new LabelMeasurementCache(label);
    }

    /// {@inheritDoc}
    ///
    /// Overridden to unbind the graphic property as the icon is carried in a [MFXIconWrapper], and to set the
    /// [Label#disableTruncationProperty()] to `true`.
    @Override
    protected BoundLabel buildLabelNode() {
        BoundLabel label = super.buildLabelNode();
        label.graphicProperty().unbind();
        label.setDisableTruncation(true);
        return label;
    }

    @Override
    protected double computeMinHeight(double width, double topInset, double rightInset, double bottomInset, double leftInset) {
        return topInset + getSkinnable().getMinSize().height() + bottomInset;
    }

    @Override
    protected double computeMinWidth(double height, double topInset, double rightInset, double bottomInset, double leftInset) {
        return leftInset + rightInset;
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
        MFXFab fab = getSkinnable();
        surface.resizeRelocate(0, 0, fab.getWidth(), fab.getHeight());
        rg.resizeRelocate(0, 0, fab.getWidth(), fab.getHeight());
        layoutInArea(label, x, y, w, h, 0, HPos.LEFT, VPos.CENTER);

        if (!init) {
            extend(fab.isExtended(), false);
            init = true;
        }
    }

    @Override
    public void dispose() {
        surface.dispose();
        rg.dispose();
        lmc.dispose();
        super.dispose();
    }
}
