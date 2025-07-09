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

import java.util.Optional;

import io.github.palexdev.mfxcore.base.beans.Position;
import io.github.palexdev.mfxcore.base.properties.NodeProperty;
import io.github.palexdev.mfxcore.base.properties.PositionProperty;
import io.github.palexdev.mfxcore.popups.MFXPopup.Peer;
import io.github.palexdev.mfxcore.utils.fx.AnchorHandlers.Align;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.layout.Pane;
import javafx.stage.Window;

/// Abstract implementation of [MFXPopup] to contain properties and behaviors that are common to all kinds of popups.
///
/// @param <O> the popup's owner type
/// @param <P> the popup's peer type, which is some kind of [Window]
public abstract class MFXPopupBase<P extends Window & Peer, O> implements MFXPopup<O> {
    //================================================================================
    // Properties
    //================================================================================
    protected P peer;
    protected O owner;
    protected Pos anchor;
    protected Align alignment;
    protected Position offset = Position.origin();
    protected PopupAnimation animation = new PopupAnimation(PopupAnimationFunction.FADE);

    private final NodeProperty content = new NodeProperty() {
        @Override
        protected void invalidated() {
            onContentChanged();
        }
    };
    private final PositionProperty position = new PositionProperty(Position.origin()) {
        @Override
        protected void invalidated() {
            peer.setX(getX());
            peer.setY(getY());
        }
    };
    private final ReadOnlyObjectWrapper<PopupState> state = new ReadOnlyObjectWrapper<>(PopupState.HIDDEN);

    protected Config<? extends MFXPopup<O>> config;

    //================================================================================
    // Constructors
    //================================================================================
    protected MFXPopupBase() {
        this.peer = buildPeer();
        if (animation != null) animation.init(this, peer.getScene().getRoot());
    }

    //================================================================================
    // Abstract Methods
    //================================================================================

    /// This is responsible for building the popup's peer, to which delegate the effective show/hide logic
    /// (as well as some other features specific to each kind).
    protected abstract P buildPeer();

    /// This is the method responsible for effectively delegating to the peer and showing the popup's window.
    /// The public show methods invoke this after all checks are passed and essential setup is done.
    ///
    /// @see #preShowCheck(Object)
    protected abstract void doShow(O owner, double x, double y);

    /// This is responsible for computing the popup's window position relative to the given owner and according to the
    /// given anchor and alignment.
    ///
    /// Implementations should take into account the offset returned by [#getOffset()].
    protected abstract Position computePosition(O owner, Pos anchor, Align alignment);

    //================================================================================
    // Methods
    //================================================================================

    /// This method should be used by public show methods to check that everything is ok before actually showing the popup.
    ///
    /// Each implementation may perform different, specific checks.
    protected void preShowCheck(O owner) {}

    /// Hook to the [#contentProperty()] triggered when the content is changed. This can be used by implementations to
    /// update the peer's content, as well as other actions if needed.
    protected void onContentChanged() {}

    //================================================================================
    // Overridden Methods
    //================================================================================

    @Override
    public void show(O owner, double x, double y) {
        if (isShowing()) return;
        preShowCheck(owner);
        setState(PopupState.SHOWING);
        doShow(owner, x, y);
    }

    @Override
    public void show(O owner, Pos anchor, Align alignment) {
        if (isShowing()) return;
        preShowCheck(owner);
        setState(PopupState.SHOWING);
        this.anchor = anchor;
        this.alignment = alignment;
        Position p = computePosition(owner, anchor, alignment);
        doShow(owner, p.x(), p.y());
    }

    @Override
    public void hide() {
        if (!isShowing()) return;
        if (animation != null) {
            animation.stop();
        }
        setState(PopupState.HIDING);
        anchor = null;
        owner = null;

        if (animation != null) {
            animation.playOut(_ -> {
                    peer.hide();
                    setState(PopupState.HIDDEN);
                }
            );
            return;
        }

        peer.hide();
        setState(PopupState.HIDDEN);
    }

    /// Simply calls [#computePosition(Object, Pos, Align)] to re-compute the position and then sets the [#positionProperty()].
    ///
    /// Note that this will work only if the popup was shown with an anchor using [#show(Object, Pos, Align)].
    /// Technically, it depends on the implementation of [#computePosition(Object, Pos, Align)], so make sure to read both
    /// [MFXDialog#computePosition(Window, Pos ,Align)] and [MFXPopover#computePosition(Node, Pos, Align)].
    @Override
    public void reposition() {
        Position pos = computePosition(owner, anchor, alignment);
        setPosition(pos);
    }

    @Override
    public O getOwner() {
        return owner;
    }

    /// Convenience delegation for [Peer#getRoot()].
    protected Pane getRoot() {
        return peer.getRoot();
    }

    @Override
    public NodeProperty contentProperty() {
        return content;
    }

    @Override
    public PositionProperty positionProperty() {
        return position;
    }

    @Override
    public Position getOffset() {
        return Optional.ofNullable(offset).orElse(Position.origin());
    }

    @Override
    public void setOffset(Position offset) {
        this.offset = offset;
    }

    @Override
    public ReadOnlyObjectWrapper<PopupState> stateProperty() {
        return state;
    }

    protected void setState(PopupState state) {
        this.state.set(state);
    }

    @Override
    public PopupAnimation getAnimation() {
        return animation;
    }

    @Override
    public void setAnimation(PopupAnimation animation) {
        if (this.animation != null) {
            this.animation.stop();
            this.animation.reset(peer.getScene().getRoot());
        }
        this.animation = animation;
        if (this.animation != null) {
            this.animation.init(this, peer.getScene().getRoot());
        }
    }
}
