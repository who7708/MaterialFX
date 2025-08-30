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

package io.github.palexdev.mfxcomponents.controls.base;

import java.util.Collection;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import io.github.palexdev.mfxcomponents.cells.MFXCell;
import io.github.palexdev.mfxcomponents.popups.ExtendedPopoverConfig;
import io.github.palexdev.mfxcore.base.beans.Position;
import io.github.palexdev.mfxcore.base.properties.functional.ConsumerProperty;
import io.github.palexdev.mfxcore.base.properties.functional.FunctionProperty;
import io.github.palexdev.mfxcore.behavior.BehaviorBase;
import io.github.palexdev.mfxcore.controls.MFXStyleable;
import io.github.palexdev.mfxcore.selection.model.ISelectionModel;
import io.github.palexdev.mfxcore.selection.model.ISelectionModel.MultipleSelectionHandler;
import io.github.palexdev.mfxcore.selection.model.ISelectionModel.SelectionEventHandler;
import io.github.palexdev.mfxcore.selection.model.ISelectionModel.SingleSelectionHandler;
import io.github.palexdev.mfxcore.selection.model.SelectionModel;
import io.github.palexdev.mfxcore.selection.model.WithSelectionModel;
import io.github.palexdev.virtualizedfx.cells.base.VFXCell;
import javafx.beans.InvalidationListener;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;

/// Base class for all `MaterialFX` components which allow the user to make a choice among a list of options, e.g.:
/// combo boxes, split buttons.
///
/// Implements the [WithSelectionModel] interface and uses a [SelectionModel] in single selection mode to keep track of
/// the selection.
///
/// Options are usually presented in a popup as a list. This component offers:
/// - the [#cellFactoryProperty()] to allow customizing how choices are rendered
/// - the [#popupConfigProperty()] to configure the popup
///
/// Like buttons, this component also has the [#trigger()] too. The method is invoked when the selection changes and the
/// action to perform can be specified through a [Consumer] which offers the new selection as the input, [#onActionProperty()].
/// See also [#updateOnActionHandler()].
public abstract class MFXChoice<T> extends MFXControl<BehaviorBase<MFXChoice<T>>> implements WithSelectionModel<T>, MFXStyleable {
    //================================================================================
    // Properties
    //================================================================================
    private final ConsumerProperty<T> onAction = new ConsumerProperty<>() {
        @Override
        protected void invalidated() {
            updateOnActionHandler();
        }
    };

    private final ObservableList<T> items = FXCollections.observableArrayList();
    private final ISelectionModel<T> sm = new SelectionModel<>(items);
    private final FunctionProperty<T, VFXCell<T>> cellFactory = new FunctionProperty<>(MFXCell::new);

    private final ObjectProperty<ExtendedPopoverConfig> popupConfig = new SimpleObjectProperty<>(
        ExtendedPopoverConfig.builder().offset(Position.of(0.0, 4.0)).build()
    );

    //================================================================================
    // Constructors
    //================================================================================
    protected MFXChoice() {}

    @SafeVarargs
    protected MFXChoice(T... items) {
        this.items.setAll(items);
    }

    protected MFXChoice(Collection<T> items) {
        this.items.setAll(items);
    }

    {
        sm.setAllowsMultipleSelection(false);
        sm.setEventHandler(this::selectionHandler);
        selection().addListener((InvalidationListener) _ -> trigger());
        defaultStyleClasses(this);
    }

    //================================================================================
    // Methods
    //================================================================================

    /// If the component is not disabled, fires a new [ActionEvent], triggering the action specified by the [#onActionProperty()].
    public void trigger() {
        if (!isDisabled()) fireEvent(new ActionEvent());
    }

    /// Sets the [EventHandler] for the [ActionEvent] fired when the selection changes.
    ///
    /// This is called every time the [#onActionProperty()] changes.
    protected void updateOnActionHandler() {
        setEventHandler(ActionEvent.ACTION, _ -> onAction.get().accept(getSelectedItem()));
    }

    /// This method is responsible for building the [SelectionEventHandler] for the selection model used by the component.
    ///
    /// Defaults may not be ideal depending on the component and the use case.
    protected SelectionEventHandler selectionHandler(ISelectionModel<T> selectionModel) {
        if (selectionModel.allowsMultipleSelection()) return new MultipleSelectionHandler(selectionModel);
        return new SingleSelectionHandler(selectionModel) {
            @Override
            public void handle(MouseEvent me, int index) {
                if (me.getButton() != MouseButton.PRIMARY) return;
                boolean selected = sm.contains(index);
                if (!selected) sm.selectIndex(index);
            }

            @Override
            public void handle(KeyEvent ke, int index) {
                if (ke.getCode() != KeyCode.ENTER && ke.getCode() != KeyCode.SPACE) return;
                boolean selected = sm.contains(index);
                if (!selected) sm.selectIndex(index);
            }
        };
    }

    //================================================================================
    // Overridden Methods
    //================================================================================
    @Override
    public Supplier<BehaviorBase<MFXChoice<T>>> defaultBehaviorProvider() {
        return () -> new BehaviorBase<>(this) {};
    }

    @Override
    public ISelectionModel<T> getSelectionModel() {
        return sm;
    }

    //================================================================================
    // Getters/Setters
    //================================================================================

    public Consumer<T> getOnAction() {
        return onAction.get();
    }

    /// Specifies the action to execute when an [ActionEvent] is fired on this component.
    ///
    /// @see #trigger()
    /// @see #updateOnActionHandler()
    public ConsumerProperty<T> onActionProperty() {
        return onAction;
    }

    public void setOnAction(Consumer<T> onAction) {
        this.onAction.set(onAction);
    }

    /// @return the list containing all the possible choices
    public ObservableList<T> getItems() {
        return items;
    }

    public Function<T, VFXCell<T>> getCellFactory() {
        return cellFactory.get();
    }

    /// Specifies the factory to use to build the cells used to display the choices.
    public FunctionProperty<T, VFXCell<T>> cellFactoryProperty() {
        return cellFactory;
    }

    public void setCellFactory(Function<T, VFXCell<T>> cellFactory) {
        this.cellFactory.set(cellFactory);
    }

    public ExtendedPopoverConfig getPopupConfig() {
        return popupConfig.get();
    }

    /// Specifies the settings for the popup used to display the choices.
    public ObjectProperty<ExtendedPopoverConfig> popupConfigProperty() {
        return popupConfig;
    }

    public void setPopupConfig(ExtendedPopoverConfig popupConfig) {
        this.popupConfig.set(popupConfig);
    }
}
