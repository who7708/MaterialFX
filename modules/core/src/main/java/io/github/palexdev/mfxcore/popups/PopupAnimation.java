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

import javafx.animation.Animation;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;

/// Convenience class for animating [MFXPopups][MFXPopup]. Helps keeping track of the animations' state, as well as
/// re-building them when needed.
///
/// Given a [PopupAnimationFunction], this produces two animations for 'show' and 'hide'.
public class PopupAnimation {
    //================================================================================
    // Properties
    //================================================================================
    private Animation inAnimation;
    private Animation outAnimation;
    private Node content;

    private final PopupAnimationFunction fn;

    //================================================================================
    // Constructors
    //================================================================================
    public PopupAnimation(PopupAnimationFunction fn) {
        this.fn = fn;
    }

    //================================================================================
    // Methods
    //================================================================================

    /// Re/builds the 'in' and 'out' animations for the given content.
    public void init(MFXPopup<?> popup, Node content) {
        this.content = content;
        inAnimation = fn.animation(popup, content, true);
        outAnimation = fn.animation(popup, content, false);
    }

    /// Stops the 'out' animation. Resets the content's state using [PopupAnimationFunction#reset(Node, boolean)] with
    /// `true` as the argument. Finally plays the 'in' animation.
    public void playIn() {
        if (outAnimation != null) outAnimation.stop();
        fn.reset(content, true);
        inAnimation.playFromStart();
    }

    /// Stops the 'in' animation. Resets the content's state using [PopupAnimationFunction#reset(Node, boolean)] with
    /// `false` as the argument. Finally plays the 'out' animation.
    ///
    /// Additionally, sets the given action to perform when the 'out' animation ends.
    /// (Animated popups delegate the hide to the peer only after the out animation finishes!)
    public void playOut(EventHandler<ActionEvent> onFinished) {
        if (inAnimation != null) inAnimation.stop();
        fn.reset(content, false);
        outAnimation.setOnFinished(onFinished);
        outAnimation.playFromStart();
    }

    /// Stops both animations without resetting.
    public void stop() {
        if (inAnimation != null) inAnimation.stop();
        if (outAnimation != null) outAnimation.stop();
    }

    /// Resets the content's state with [PopupAnimationFunction#reset(Node, boolean)] with `false` as the argument.
    public void reset(Node content) {
        fn.reset(content, false);
    }
}
