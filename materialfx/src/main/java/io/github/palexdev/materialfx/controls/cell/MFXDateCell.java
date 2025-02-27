package io.github.palexdev.materialfx.controls.cell;

import java.time.LocalDate;
import java.util.List;
import java.util.function.Supplier;

import io.github.palexdev.materialfx.controls.MFXDatePicker;
import io.github.palexdev.materialfx.skins.MFXListCellSkin;
import io.github.palexdev.materialfx.theming.MaterialFXStylesheets;
import io.github.palexdev.materialfx.theming.PseudoClasses;
import io.github.palexdev.mfxcore.controls.SkinBase;
import io.github.palexdev.mfxcore.utils.fx.SceneBuilderIntegration;
import io.github.palexdev.virtualizedfx.cells.CellBaseBehavior;
import javafx.beans.property.ReadOnlyBooleanWrapper;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.util.StringConverter;

public class MFXDateCell extends MFXListCell<LocalDate> {
    //================================================================================
    // Properties
    //================================================================================
    private MFXDatePicker datePicker;

    private final ReadOnlyBooleanWrapper current = new ReadOnlyBooleanWrapper(false) {
        @Override
        protected void invalidated() {
            PseudoClasses.setOn(MFXDateCell.this, PseudoClasses.CURRENT, get());
        }
    };

    private boolean extra = false;

    //================================================================================
    // Constructors
    //================================================================================
    public MFXDateCell(MFXDatePicker datePicker, LocalDate item) {
        super(item);
        this.datePicker = datePicker;
        initialize();
    }

    public MFXDateCell(MFXDatePicker datePicker, LocalDate item, StringConverter<LocalDate> converter) {
        super(item, converter);
        this.datePicker = datePicker;
        initialize();
    }

    //================================================================================
    // Methods
    //================================================================================
    private void initialize() {
        selected.bind(datePicker.valueProperty().isEqualTo(itemProperty()));
        current.bind(datePicker.currentDateProperty().isEqualTo(itemProperty()));
    }

    /**
     * Marks/unmarks this cell as an extra cell.
     */
    public void setExtra(boolean isExtra) {
        extra = isExtra;
        PseudoClasses.setOn(this, PseudoClasses.EXTRA, isExtra);
    }

    //================================================================================
    // Overridden Methods
    //================================================================================
    @Override
    protected void sceneBuilderIntegration() {
        SceneBuilderIntegration.ifInSceneBuilder(() -> getStylesheets().add(MaterialFXStylesheets.DATE_CELL.toData()));
    }

    @Override
    protected SkinBase<?, ?> buildSkin() {
        return new MFXListCellSkin<>(this) {
            {
                visibleProperty().bind(label.textProperty().isNotEmpty());
            }
        };
    }

    @Override
    public Supplier<CellBaseBehavior<LocalDate>> defaultBehaviorProvider() {
        return () -> new MFXListCellBehavior<>(this) {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getButton() == MouseButton.PRIMARY) {
                    datePicker.setValue(getItem());
                }
                mouseClicked(e, null);
            }
        };
    }

    @Override
    public List<String> defaultStyleClasses() {
        return List.of("mfx-date-cell");
    }

    @Override
    public void dispose() {
        datePicker = null;
        super.dispose();
    }

    //================================================================================
    // Getters
    //================================================================================
    public boolean isExtra() {
        return extra;
    }
}
