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

import java.util.function.Consumer;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.stage.Window;

/// A nice, convenient facade over the [MFXPopups][MFXPopup] API.
///
/// You can easily build, configure and show any kind of [MFXPopup] in a type-safe and declarative way.
public class MFXPopups {

    //================================================================================
    // Constructors
    //================================================================================
    private MFXPopups() {}

    //================================================================================
    // Static Methods
    //================================================================================

    public static Builder<Window, MFXDialog> dialog() {
        return new Builder<>(new MFXDialog(), null);
    }

    public static Builder<Window, MFXDialog> dialog(Consumer<MFXDialog.DialogConfig.Builder> config) {
        MFXDialog.DialogConfig.Builder builder = MFXDialog.DialogConfig.builder();
        config.accept(builder);
        return new Builder<>(new MFXDialog(), builder.build());
    }

    public static Builder<Node, MFXPopover> popover() {
        return new Builder<>(new MFXPopover(), null);
    }

    public static Builder<Node, MFXPopover> popover(Consumer<MFXPopover.PopoverConfig.Builder> config) {
        MFXPopover.PopoverConfig.Builder builder = MFXPopover.PopoverConfig.builder();
        config.accept(builder);
        return new Builder<>(new MFXPopover(), builder.build());
    }

    public static Builder<Node, MFXTooltip> tooltip() {
        return new Builder<>(new MFXTooltip(), null);
    }

    public static Builder<Node, MFXTooltip> tooltip(Consumer<MFXTooltip.TooltipConfig.Builder> config) {
        MFXTooltip.TooltipConfig.Builder builder = MFXTooltip.TooltipConfig.builder();
        config.accept(builder);
        return new Builder<>(new MFXTooltip(), builder.build());
    }

    //================================================================================
    // Builder
    //================================================================================
    public static class Builder<O, P extends MFXPopup<O>> {
        private final P popup;

        private Builder(P popup, MFXPopup.Config<P> config) {
            this.popup = popup;
            if (config != null) config.apply(popup);
        }

        public Builder<O, P> setContent(Node content) {
            popup.setContent(content);
            return this;
        }

        public Builder<O, P> setOffset(Insets offset) {
            popup.setOffset(offset);
            return this;
        }

        public Builder<O, P> setStyleClass(String... styleClass) {
            popup.setStyleClass(styleClass);
            return this;
        }

        public Builder<O, P> setAnimation(PopupAnimation animation) {
            popup.setAnimation(animation);
            return this;
        }

        /// Note: for tooltips this will call [MFXTooltip#install(Node)]!
        public P show(O owner, double x, double y) {
            if (popup instanceof MFXTooltip t) {
                t.install(((Node) owner));
                return popup;
            }
            popup.show(owner, x, y);
            return popup;
        }

        /// Note: for tooltips this will call [MFXTooltip#install(Node)] and override the set anchor with the given one!
        public P show(O owner, Pos anchor) {
            if (popup instanceof MFXTooltip t) {
                t.install(((Node) owner));
                MFXTooltip.TooltipConfig.builder(t.getConfig())
                    .anchor(anchor)
                    .build()
                    .apply(t);
                return popup;
            }
            popup.show(owner, anchor);
            return popup;
        }
    }
}
