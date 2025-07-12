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

import java.util.*;
import java.util.function.Consumer;

import io.github.palexdev.mfxcore.base.beans.Position;
import io.github.palexdev.mfxcore.controls.MFXStyleable;
import io.github.palexdev.mfxcore.observables.When;
import io.github.palexdev.mfxcore.popups.MFXPopover.PopoverConfig.Builder;
import io.github.palexdev.mfxcore.popups.MFXPopover.PopupPeer;
import io.github.palexdev.mfxcore.utils.fx.AnchorHandlers;
import io.github.palexdev.mfxcore.utils.fx.AnchorHandlers.Align;
import io.github.palexdev.mfxcore.utils.fx.CSSFragment;
import javafx.css.Styleable;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.PopupControl;
import javafx.scene.layout.StackPane;
import javafx.stage.PopupWindow;
import javafx.stage.Window;

/// Custom implementation of popovers based on the [MFXPopup] API. It also implements [MFXStyleable], the default CSS
/// style-class is set to: '.root' and '.mfx-popup.'. This mimics JavaFX popups which also have the '.root' style class
/// applied. It's recommended to keep it so that if your theme defines some lookup color on the '.root' selector, it gets
/// propagated to the popovers too.
///
/// ### Definition, Usage and Features
///
/// Popovers are a type of popup appearing close to a specific UI element, usually triggered by user interaction with it,
/// such as hovering or clicking. They provide additional contextual information, options or actions related to the UI
/// element they are associated with.
///
/// Popovers are typically non-modal, and therefore they do not need to use backdrops either. They usually have
/// _light dismiss_ by design, meaning that they disappear if the user hovers out of the element that triggered it or clicks
/// out of the popover.
///
/// ### Implementation Details
///
/// `MFXPopover` uses a custom [PopupWindow] as the peer to which delegate the true actions (show, hide, position,
/// auto-fix, auto-hide, etc...). I didn't want to use that crap that is [PopupControl], so I suggest you reading [PopupPeer]'s
/// documentation to see what I came up with to compensate.
///
/// Popovers cannot be shown without an owner.
///
/// Features such as _auto-fix_ and _auto-hide_ are problematic:
/// 1) Auto fix changes the x and y coordinates of the window. Because it operates internally to the peer, the [#positionProperty()]
/// and the actual window position may end up being desynchronized. If you want to get the true window position,
/// use [#getPeerPosition()]
/// 2) Auto hide changes the visibility state internally to the peer. Because we have a custom way to represent the
/// popup's state ([#stateProperty()], and [PopupState]), it could cause a disastrous desynchronization. The good thing
/// is that such feature still hides the window by calling [Window#hide()]; therefore, we can override the logic in our
/// peer to redirect auto-hide requests to our method instead, as simple as adding a boolean flag.
public class MFXPopover extends MFXPopupBase<PopupPeer, Node> implements MFXStyleable {
    //================================================================================
    // Properties
    //================================================================================
    private Node styleableParent;

    //================================================================================
    // Constructors
    //================================================================================
    public MFXPopover() {
        defaultStyleClasses(getRoot());
        PopoverConfig.DEFAULT.apply(this);
    }

    public MFXPopover(String... styleClass) {
        setStyleClass(styleClass);
        PopoverConfig.DEFAULT.apply(this);
    }

    //================================================================================
    // Methods
    //================================================================================

    /// Convenience method to change the configuration of this popover. The provided builder starts with the values from
    /// the current config.
    public MFXPopover configure(Consumer<Builder> cfg) {
        Builder builder = PopoverConfig.builder(getConfig());
        cfg.accept(builder);
        builder.build().apply(this);
        return this;
    }

    /// Invokes [PopupPeer#updateStylesheets()]. Check [PopupPeer]'s documentation to understand when to use this!
    public void updateStylesheets() {
        peer.updateStylesheets();
    }

    //================================================================================
    // Overridden Methods
    //================================================================================

    @Override
    protected PopupPeer buildPeer() {
        return new PopupPeer();
    }

    @Override
    protected void doShow(Node owner, double x, double y) {
        if (animation != null) animation.stop();

        // We need this as a workaround because for some reason when showing/hiding the popover at a very fast rate,
        // it causes the state property to no update, leaving the popover unable to show anymore
        When.onInvalidated(peer.showingProperty())
            .condition(s -> !s)
            .then(_ -> setState(PopupState.HIDDEN))
            .oneShot()
            .listen();

        this.owner = owner;
        Node content = getContent();
        content.setVisible(false);

        peer.setStyleableParent(
            Optional.ofNullable(styleableParent).orElse(owner)
        );
        peer.show(owner, 0, 0);
        positionProperty().setPosition(x, y);

        if (anchor != null) {
            // Re-compute position
            Position pos = computePosition(owner, anchor, alignment);
            setPosition(pos);
        }

        if (animation != null) animation.playIn();
        content.setVisible(true);
        setState(PopupState.SHOWN);
    }

    @Override
    public void hide() {
        peer.indirectHide = true;
        super.hide();
    }

    /// If the given anchor or alignment are `null` returns a position of `<0, 0>`.
    ///
    /// Otherwise, the computation is delegated to the handlers in [AnchorHandlers].
    @Override
    protected Position computePosition(Node owner, Pos anchor, Align alignment) {
        if (anchor == null || alignment == null) return Position.origin();
        return AnchorHandlers.handler(anchor).compute(owner, getRoot(), alignment, getOffset());
    }

    @Override
    protected void onContentChanged() {
        Node content = getContent();
        peer.setContent(content);
    }

    @Override
    protected void preShowCheck(Node owner) {
        if (owner == null) throw new IllegalStateException("Owner cannot be null");
        if (getContent() == null) throw new IllegalStateException("Content cannot be null");
    }

    @Override
    public Position getPeerPosition() {
        return Position.of(peer.getX(), peer.getY());
    }

    @Override
    public void setStyleClass(String... styleClass) {
        peer.setStyleClass(styleClass);
    }

    @Override
    public PopoverConfig getConfig() {
        return ((PopoverConfig) config);
    }

    @Override
    public List<String> defaultStyleClasses() {
        return MFXStyleable.styleClasses("root", "mfx-popup");
    }

    //================================================================================
    // Inner Classes
    //================================================================================

    /// Custom extension of [PopupWindow] which serves as the peer for [MFXPopover].
    ///
    /// Sets the root to a [StackPane].
    ///
    /// ### CSS
    ///
    /// The only good thing about [PopupControl] is that it allows styling from CSS _fairly_ easily. This until you look
    /// at the internal spaghetti code. Because there is absolutely no need to overcomplicate things by enforcing the usage
    /// of a skin, I opted for [PopupWindow] instead.
    ///
    /// It turns out the mechanism it uses to allow CSS styling is quite easy and stupid.
    ///
    /// First of all, you need to understand what is a `styleable parent`, because the information given by [Styleable] and
    /// [Styleable#getStyleableParent()] are just **ridiculous**.
    ///
    /// Let's suppose you show a [PopupControl] for a button. And then you style the popup with CSS like this:
    /// ```css
    /// .button .my-popup {...}
    ///```
    ///
    /// [PopupControl] will internally use the button (which is the owner) as the `styleable parent`, and that's what
    /// allows you to write `.button .my-popup`; it makes it appear to the JavaFX CSS module as the popup is inside the
    /// button.
    ///
    /// The `styleable parent` is not enough alone. The other internal mechanism of [PopupControl] is that starting from
    /// that node, it walks up the scenegraph getting all the applied stylesheets along the way, adding them on the popup.
    ///
    /// We do the same thing here. When the `styleable parent` changes, we call [#updateStylesheets()] which will use
    /// [#fetchStylesheets()] to get all the stylesheets upwards to the root.
    ///
    /// Three important notes:
    /// 1) Who is the `styleable parent`? By default, we use the owner node for which the popup is shown. However, for
    /// more flexibility, we allow overriding it via the [PopoverConfig].
    /// 2) The [#updateStylesheets()] here is automatically called by [#setStyleableParent(Node)] only if the old and new
    /// `styleable parent` are not the same. The only JavaFX mechanism I could not replicate because it would be either
    /// dirty or too heavy on performance is the automatic refresh of stylesheets.
    /// If you add some stylesheets on the `styleable parent` path, you'll have to call [MFXPopover#updateStylesheets()].
    /// 3) Note that for convenience we accept a generic [Node] as the `styleable parent`, but for this mechanism to work
    /// we need a [Parent]. Casting is automatically handled in a safe way, so if it's not a [Parent] it will fail silently
    /// (by not fetching any stylesheet).
    protected class PopupPeer extends PopupWindow implements Peer {
        private static final String RESET_CSS = CSSFragment.Builder.build()
            .select(".mfx-popup")
            .background("transparent")
            .padding("0px")
            .toFragment()
            .toDataUri();

        private final StackPane root = new StackPane() {
            @Override
            public Styleable getStyleableParent() {
                return styleableParent;
            }
        };
        private Node styleableParent;
        private boolean indirectHide = false;

        {
            getScene().setRoot(root);
        }

        protected void setStyleableParent(Node styleableParent) {
            boolean reFetch = this.styleableParent != styleableParent;
            this.styleableParent = styleableParent;
            if (reFetch) updateStylesheets();
        }

        protected void updateStylesheets() {
            Collection<String> fetched = fetchStylesheets();
            root.getStylesheets().setAll(fetched);
        }

        protected Collection<String> fetchStylesheets() {
            SequencedSet<String> set = new LinkedHashSet<>();
            set.addFirst(RESET_CSS);

            if (!(styleableParent instanceof Parent parent))
                return set;

            while (parent != null) {
                set.addAll(parent.getStylesheets());
                parent = parent.getParent();
            }
            return set;
        }

        @Override
        public void hide() {
            // Redirect auto-hide handling to popover hide logic!
            if (!indirectHide) {
                MFXPopover.this.hide();
                return;
            }
            indirectHide = false;
            super.hide();
        }

        @Override
        public StackPane getRoot() {
            return root;
        }
    }

    // Config

    public record PopoverConfig(
        Position offset,
        Node styleableParent,
        boolean autoFix,
        boolean autoHide,
        boolean hideOnEscape,
        boolean consumeAutoHideEvents
    ) implements Config<MFXPopover> {
        public static final PopoverConfig DEFAULT = builder().build();

        @Override
        public void apply(MFXPopover popup) {
            popup.offset = offset;
            popup.styleableParent = styleableParent;
            popup.peer.setAutoFix(autoFix);
            popup.peer.setAutoHide(autoHide);
            popup.peer.setHideOnEscape(hideOnEscape);
            popup.peer.setConsumeAutoHidingEvents(consumeAutoHideEvents);
            popup.config = this;
        }

        public static Builder builder() {
            return new Builder();
        }

        public static Builder builder(PopoverConfig config) {
            return new Builder()
                .offset(config.offset)
                .styleableParent(config.styleableParent)
                .autoFix(config.autoFix)
                .autoHide(config.autoHide)
                .hideOnEscape(config.hideOnEscape)
                .consumeAutoHideEvents(config.consumeAutoHideEvents);
        }

        public static class Builder {
            private Position offset = Position.origin();
            private Node styleableParent;
            private boolean autoFix = true;
            private boolean autoHide = true;
            private boolean hideOnEscape = true;
            private boolean consumeAutoHideEvents = false;

            public Builder offset(Position offset) {
                this.offset = offset;
                return this;
            }

            public Builder styleableParent(Node styleableParent) {
                this.styleableParent = styleableParent;
                return this;
            }

            public Builder autoFix(boolean autoFix) {
                this.autoFix = autoFix;
                return this;
            }

            public Builder autoHide(boolean autoHide) {
                this.autoHide = autoHide;
                return this;
            }

            public Builder hideOnEscape(boolean hideOnEscape) {
                this.hideOnEscape = hideOnEscape;
                return this;
            }

            public Builder consumeAutoHideEvents(boolean consumeAutoHideEvents) {
                this.consumeAutoHideEvents = consumeAutoHideEvents;
                return this;
            }

            public PopoverConfig build() {
                return new PopoverConfig(
                    offset,
                    styleableParent,
                    autoFix,
                    autoHide,
                    hideOnEscape,
                    consumeAutoHideEvents
                );
            }
        }
    }
}
