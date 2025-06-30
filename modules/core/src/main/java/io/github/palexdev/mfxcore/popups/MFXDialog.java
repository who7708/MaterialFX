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

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import io.github.palexdev.mfxcore.base.beans.Position;
import io.github.palexdev.mfxcore.controls.MFXStyleable;
import io.github.palexdev.mfxcore.events.WhenEvent;
import io.github.palexdev.mfxcore.popups.MFXDialog.WindowPeer;
import io.github.palexdev.mfxcore.utils.fx.AnchorHandlers;
import io.github.palexdev.mfxcore.utils.fx.MFXBackdrop;
import javafx.geometry.*;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.*;

/// My custom implementation of dialogs based on the [MFXPopup] API. It also implements [MFXStyleable], the default CSS
/// style-class is set to '.mfx-dialog'.
///
/// ### Definition, Usage and Features
///
/// Dialogs are a type of popup used to present information and prompt users to input data or make decisions. They can
/// often interrupt the flow by being modal, but they can also be non-modal. Modal dialogs can show a backdrop effect
/// behind to lock the user in the context visually.
///
/// Dialogs usually have _explicit dismiss_ via a close button (e.g., x icon) or a 'cancel' button.
///
/// ### Implementation Details
///
/// `MFXDialog` uses a custom [Stage] as the peer to which delegate the true actions (show, hide, position, modality, etc...).
/// Which means that unfortunately, we inherit all the inconveniences of it like: not being able to create the dialog
/// outside the JavaFX Application Thread, not being able to change the modality or owner after it is shown, etc.
///
/// Unlike other kinds of popups, this is not necessarily tied to an owner. The [#computePosition(Window, Pos)] is
/// overridden to take this into account and use the [Screen] bounds instead. Which screen to use can be set via the
/// configuration [DialogConfig], otherwise it defaults to the primary.
///
/// Note that if no owner is specified, there won't be automatic dismissal. When an owner is set, this listens for
/// [WindowEvent#WINDOW_CLOSE_REQUEST] events on the owner and automatically closes itself!
///
/// @see AnchorHandlers.PositionMode
public class MFXDialog extends MFXPopupBase<WindowPeer, Window> implements MFXStyleable {
    //================================================================================
    // Properties
    //================================================================================
    private Screen fallbackScreen;
    private MFXBackdrop backdrop;

    //================================================================================
    // Constructors
    //================================================================================
    public MFXDialog() {
        defaultStyleClasses(peer.root);
    }

    public MFXDialog(String... styleClass) {
        setStyleClass(styleClass);
    }

    //================================================================================
    // Overridden Methods
    //================================================================================

    @Override
    protected WindowPeer buildPeer() {
        return new WindowPeer();
    }

    @SuppressWarnings("DataFlowIssue")
    @Override
    protected void doShow(Window owner, double x, double y) {
        if (animation != null) animation.stop();

        this.owner = owner;
        if (owner != null) {
            WhenEvent.intercept(owner, WindowEvent.WINDOW_CLOSE_REQUEST)
                .process(_ -> hide())
                .asFilter()
                .oneShot()
                .register();
        }

        Node content = getContent();
        content.setVisible(false);

        positionProperty().setPosition(x, y);
        peer.show();

        if (anchor != null) {
            // Re-compute position because the content's bounds are reliable now
            Position pos = computePosition(owner, anchor);
            setPosition(pos);
        }
        if (backdrop != null) backdrop.show(owner.getScene().getRoot());

        if (animation != null) animation.playIn();
        content.setVisible(true);
        setState(PopupState.SHOWN);
    }

    @Override
    public void show(Window owner, double x, double y) {
        if (isShowing()) {
            peer.toFront();
            return;
        }
        super.show(owner, x, y);
    }

    @Override
    public void show(Window owner, Pos anchor) {
        if (isShowing()) {
            peer.toFront();
            return;
        }
        super.show(owner, anchor);
    }

    @Override
    protected Position computePosition(Window owner, Pos anchor) {
        if (anchor == null) return Position.origin();
        AnchorHandlers.AnchorHandler handler = AnchorHandlers.handler(anchor);
        if (owner != null) return handler.compute(owner, getContent(), getOffset());

        Screen screen = Optional.ofNullable(fallbackScreen).orElse(Screen.getPrimary());
        Rectangle2D vb = screen.getVisualBounds();
        Bounds screenBounds = new BoundingBox(
            vb.getMinX(), vb.getMinY(),
            vb.getWidth(), vb.getHeight()
        );
        return handler.compute(screenBounds, getContent(), AnchorHandlers.PositionMode.INSIDE, getOffset());
    }

    @Override
    protected void onContentChanged() {
        Node content = getContent();
        peer.setContent(content);
    }

    @Override
    protected void preShowCheck(Window owner) {
        if (owner != null && !owner.isShowing()) throw new IllegalStateException("Owner window must be showing");
        if (getContent() == null) throw new IllegalStateException("Content cannot be null");
    }

    @Override
    public Position getPeerPosition() {
        return Position.of(peer.getX(), peer.getY());
    }

    @Override
    public List<String> defaultStyleClasses() {
        return MFXStyleable.styleClasses("mfx-dialog");
    }

    @Override
    public void setStyleClass(String... styleClass) {
        peer.setStyleClass(styleClass);
    }

    //================================================================================
    // Inner Classes
    //================================================================================

    /// Custom extension of [Stage] which serves as the peer for [MFXDialog].
    ///
    /// Sets the root node to a [StackPane], the style to [StageStyle#TRANSPARENT] and the scene's fill to transparent.
    ///
    /// Offers a bunch of convenience methods.
    protected static class WindowPeer extends Stage {
        private final StackPane root = new StackPane();

        {
            initStyle(StageStyle.TRANSPARENT);
            Scene scene = new Scene(root);
            scene.setFill(Color.TRANSPARENT);
            setScene(scene);
        }

        protected void setContent(Node content) {
            root.getChildren().setAll(content);
        }

        protected void setStyleClass(String... styleClass) {
            root.getStyleClass().setAll(styleClass);
        }
    }

    // Config

    public record DialogConfig(
        Insets offset,
        Screen fallbackScreen,
        Modality modality,
        boolean useBackdrop,
        String... backdropStyleClass
    ) implements Config<MFXDialog> {

        @Override
        public void apply(MFXDialog popup) {
            popup.offset = offset;
            popup.fallbackScreen = fallbackScreen;
            popup.peer.initModality(modality);
            if (useBackdrop) {
                popup.backdrop = new MFXBackdrop();
                popup.backdrop.getStyleClass().addAll(backdropStyleClass);
            } else {
                popup.backdrop = null;
            }
        }

        public static Builder builder() {
            return new Builder();
        }

        public static final class Builder {
            private Insets offset = Insets.EMPTY;
            private Screen fallbackScreen;
            private Modality modality = Modality.NONE;
            private boolean useBackdrop = false;
            private String[] backdropStyleClass = new String[]{};

            public Builder offset(Insets offset) {
                this.offset = offset;
                return this;
            }

            public Builder fallbackScreen(Screen fallbackScreen) {
                this.fallbackScreen = fallbackScreen;
                return this;
            }

            public Builder modality(Modality modality) {
                this.modality = Objects.requireNonNull(modality, "Null modality");
                return this;
            }

            public Builder useBackdrop(boolean useBackdrop) {
                this.useBackdrop = useBackdrop;
                return this;
            }

            public Builder backdropStyleClass(String... backdropStyleClass) {
                this.backdropStyleClass = backdropStyleClass;
                return this;
            }

            public DialogConfig build() {
                return new DialogConfig(
                    offset,
                    fallbackScreen,
                    modality,
                    useBackdrop,
                    backdropStyleClass
                );
            }
        }
    }
}
