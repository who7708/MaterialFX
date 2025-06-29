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
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.stage.Window;

/// Abstract implementation of [MFXPopup] to contain properties and behaviors that are common to all kinds of popups.
///
/// @param <O> the popup's owner type
/// @param <P> the popup's peer type, which is some kind of [Window]
public abstract class MFXPopupBase<P extends Window, O> implements MFXPopup<O> {
    //================================================================================
    // Properties
    //================================================================================
    protected P peer;
    protected O owner;
    protected Pos anchor;
    protected Insets offset = Insets.EMPTY;

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

    //================================================================================
    // Constructors
    //================================================================================
    protected MFXPopupBase() {
        this.peer = buildPeer();
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
    /// given anchor position.
    ///
    /// Implementations should take into account the offset returned by [#getOffset()].
    protected abstract Position computePosition(O owner, Pos anchor);

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
    public void show(O owner, Pos anchor) {
        if (isShowing()) return;
        preShowCheck(owner);
        setState(PopupState.SHOWING);
        this.anchor = anchor;
        Position p = computePosition(owner, anchor);
        doShow(owner, p.x(), p.y());
    }

    @Override
    public void hide() {
        if (!isShowing()) return;
        setState(PopupState.HIDING);
        anchor = null;
        owner = null;
        peer.hide();
        setState(PopupState.HIDDEN);
    }

    @Override
    public O getOwner() {
        return owner;
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
    public Insets getOffset() {
        return Optional.ofNullable(offset).orElse(Insets.EMPTY);
    }

    @Override
    public void setOffset(Insets offset) {
        this.offset = offset;
    }

    @Override
    public ReadOnlyObjectWrapper<PopupState> stateProperty() {
        return state;
    }

    protected void setState(PopupState state) {
        this.state.set(state);
    }
}
