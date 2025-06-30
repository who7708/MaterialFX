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

package io.github.palexdev.mfxcore.popups;

import javafx.animation.*;
import javafx.scene.CacheHint;
import javafx.scene.Node;

import static javafx.util.Duration.millis;

/// This interface represents a function that generates a [Animation] given three parameters:
/// - The [MFXPopup] for which the animation will play
/// - The popup's content, which may not necessarily be the [MFXPopup#contentProperty()] but rather the popup's root
/// - The state to animate, which is either 'show' or 'hide'
@FunctionalInterface
public interface PopupAnimationFunction {

    Animation animation(MFXPopup<?> popup, Node content, boolean show);

    /// This method can be used by the animations to reset the content state before playing.
    ///
    /// For example, a fade in (show = `true`) transition may want to set the content's opacity to `0.0` before starting.
    default void reset(Node content, boolean show) {}

    //================================================================================
    // Preset Functions
    //================================================================================

    /// Preset animation that fades the given content in/out according to the `show` argument.
    PopupAnimationFunction FADE = new PopupAnimationFunction() {
        @Override
        public Animation animation(MFXPopup<?> popup, Node content, boolean show) {
            double targetOpacity = show ? 1.0 : 0.0;
            Interpolator curve = new CubicCurve(0.31, 0.94, 0.34, 1.0);
            KeyFrame kf = new KeyFrame(millis(150.0), new KeyValue(content.opacityProperty(), targetOpacity, curve));
            return new Timeline(kf);
        }

        @Override
        public void reset(Node content, boolean show) {
            content.setOpacity(show ? 0.0 : 1.0);
        }
    };

    /// Preset animation that fades and scales the given content in/out according to the `show` argument.
    /// (the fading makes the animation look better)
    PopupAnimationFunction SCALE = new PopupAnimationFunction() {
        @Override
        public Animation animation(MFXPopup<?> popup, Node content, boolean show) {
            double target = show ? 1.0 : 0.0;
            Interpolator oCurve = new CubicCurve(0.34, 0.80, 0.34, 1.0);
            KeyFrame okf = new KeyFrame(millis(200.0), new KeyValue(content.opacityProperty(), 1.0, oCurve));

            Interpolator sCurve = new CubicCurve(0.27, 1.06, 0.18, 1.0);
            KeyFrame skf = new KeyFrame(millis(200.0),
                new KeyValue(content.scaleXProperty(), target, sCurve),
                new KeyValue(content.scaleYProperty(), target, sCurve)
            );
            return new Timeline(okf, skf);
        }

        @Override
        public void reset(Node content, boolean show) {
            double target = show ? 0.0 : 1.0;
            content.setOpacity(target);
            content.setScaleX(target);
            content.setScaleY(target);

            if (show) {
                content.setCacheHint(CacheHint.SCALE);
                content.setCache(true);
            } else {
                content.setCacheHint(CacheHint.DEFAULT);
                content.setCache(false);
            }
        }
    };
}

/// Backport from effects module.
class CubicCurve extends Interpolator {
    private final double x1;
    private final double y1;
    private final double x2;
    private final double y2;

    private static final double CUBIC_ERROR_BOUND = 0.001;

    public CubicCurve(double x1, double y1, double x2, double y2) {
        this.x1 = x1;
        this.y1 = y1;
        this.x2 = x2;
        this.y2 = y2;
    }

    double elevateCubic(double a, double b, double m) {
        return 3 * a * (1 - m) * (1 - m) * m +
               3 * b * (1 - m) * m * m +
               m * m * m;
    }

    @Override
    public double curve(double t) {
        double start = 0.0;
        double end = 1.0;
        while (true) {
            final double midpoint = (start + end) / 2;
            final double estimate = elevateCubic(x1, x2, midpoint);
            if (Math.abs(t - estimate) < CUBIC_ERROR_BOUND) {
                return elevateCubic(y1, y2, midpoint);
            }
            if (estimate < t) {
                start = midpoint;
            } else {
                end = midpoint;
            }
        }
    }
}
