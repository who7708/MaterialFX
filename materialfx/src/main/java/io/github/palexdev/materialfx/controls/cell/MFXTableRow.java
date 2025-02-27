package io.github.palexdev.materialfx.controls.cell;


import java.util.Optional;

import io.github.palexdev.materialfx.collections.RefineList;
import io.github.palexdev.materialfx.controls.cell.MFXListCell.MFXListCellBehavior.SelectionMode;
import io.github.palexdev.materialfx.selection.base.ISelectionModel;
import io.github.palexdev.materialfx.selection.base.WithSelectionModel;
import io.github.palexdev.materialfx.theming.PseudoClasses;
import io.github.palexdev.mfxcore.builders.bindings.BooleanBindingBuilder;
import io.github.palexdev.mfxcore.events.WhenEvent;
import io.github.palexdev.mfxeffects.ripple.MFXRippleGenerator;
import io.github.palexdev.virtualizedfx.base.VFXContainer;
import io.github.palexdev.virtualizedfx.table.VFXTable;
import io.github.palexdev.virtualizedfx.table.defaults.VFXDefaultTableRow;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyBooleanWrapper;
import javafx.collections.ObservableList;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;

public class MFXTableRow<T> extends VFXDefaultTableRow<T> {
    //================================================================================
    // Properties
    //================================================================================
    protected final ReadOnlyBooleanWrapper selected = new ReadOnlyBooleanWrapper(false) {
        @Override
        protected void invalidated() {
            PseudoClasses.setOn(MFXTableRow.this, PseudoClasses.SELECTED, get());
        }
    };

    private final MFXRippleGenerator rg = new MFXRippleGenerator(this);
    private WhenEvent<?> wMouseClicked;

    //================================================================================
    // Constructors
    //================================================================================
    public MFXTableRow(T item) {
        super(item);

        rg.enable();
        wMouseClicked = WhenEvent.intercept(this, MouseEvent.MOUSE_CLICKED)
            .condition(e -> e.getButton() == MouseButton.PRIMARY)
            .process(e -> updateSelection(SelectionMode.forEvent(e)))
            .register();
    }

    //================================================================================
    // Methods
    //================================================================================
    protected void updateSelection(SelectionMode mode) {
        getSelectionModel().ifPresent(sm -> {
            int index = getIndex();
            switch (mode) {
                case STANDARD -> {
                    if (isSelected()) {
                        sm.deselectIndex(index);
                    } else {
                        sm.selectIndex(index);
                    }
                }
                case EXTEND -> sm.expandSelection(index, true);
                case REPLACE -> sm.replaceSelection(index);
            }
        });
    }

    //================================================================================
    // Overridden Methods
    //================================================================================
    @Override
    protected void onCellsChanged() {
        super.onCellsChanged();
        getChildren().addFirst(rg);
    }

    @Override
    public void onCreated(VFXContainer<T> container) {
        super.onCreated(container);

        getSelectionModel().ifPresent(sm ->
            selected.bind(BooleanBindingBuilder.build()
                .setMapper(() -> sm.contains(getIndex()))
                .addSources(sm.selection(), indexProperty())
                .get()
            ));
    }

    @Override
    public void updateIndex(int index) {
        ObservableList<T> items = getTable().getItems();
        if (items instanceof RefineList<T> rl) {
            setIndex(rl.viewToSource(index));
            return;
        }
        super.updateIndex(index);
    }

    @Override
    protected void layoutChildren() {
        super.layoutChildren();

        rg.resizeRelocate(0, 0, getWidth(), getHeight());
    }

    @Override
    public void dispose() {
        if (wMouseClicked != null) {
            wMouseClicked.dispose();
            wMouseClicked = null;
        }
        rg.dispose();
        super.dispose();
    }

    //================================================================================
    // Getters
    //================================================================================
    @SuppressWarnings("unchecked")
    public Optional<ISelectionModel<T>> getSelectionModel() {
        VFXTable<T> table = getTable();
        if (table instanceof WithSelectionModel<?>) {
            return Optional.of(((WithSelectionModel<T>) table).getSelectionModel());
        }
        return Optional.empty();
    }

    public boolean isSelected() {
        return selected.get();
    }

    public ReadOnlyBooleanProperty selectedProperty() {
        return selected.getReadOnlyProperty();
    }
}
