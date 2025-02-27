package io.github.palexdev.materialfx.controls.cell;

import java.util.List;
import java.util.Optional;

import io.github.palexdev.materialfx.controls.base.MFXCombo;
import io.github.palexdev.materialfx.selection.base.ISelectionModel;
import io.github.palexdev.materialfx.theming.MaterialFXStylesheets;
import io.github.palexdev.mfxcore.utils.fx.SceneBuilderIntegration;
import io.github.palexdev.virtualizedfx.cells.VFXSimpleCell;
import javafx.util.StringConverter;

public class MFXComboBoxCell<T> extends MFXListCell<T> {
    //================================================================================
    // Properties
    //================================================================================
    private MFXCombo<T> combo;

    //================================================================================
    // Constructors
    //================================================================================
    public MFXComboBoxCell(MFXCombo<T> combo, T item) {
        super(item);
        this.combo = combo;
    }

    //================================================================================
    // Overridden Methods
    //================================================================================
    @Override
    public Optional<ISelectionModel<T>> getSelectionModel() {
        return Optional.ofNullable(combo.getSelectionModel());
    }

    @Override
    protected void sceneBuilderIntegration() {
        SceneBuilderIntegration.ifInSceneBuilder(() -> getStylesheets().add(MaterialFXStylesheets.COMBO_BOX_CELL.toData()));
    }

    @Override
    public List<String> defaultStyleClasses() {
        return List.of("mfx-combo-box-cell");
    }

    @Override
    public VFXSimpleCell<T> setConverter(StringConverter<T> converter) {
        combo.setConverter(converter);
        return this;
    }

    @Override
    public StringConverter<T> getConverter() {
        return combo.getConverter();
    }

    @Override
    public void dispose() {
        combo = null;
        super.dispose();
    }
}
