package io.github.palexdev.materialfx.controls.cell;

import java.util.List;

import io.github.palexdev.materialfx.skins.MFXCheckListCellSkin;
import io.github.palexdev.materialfx.theming.MaterialFXStylesheets;
import io.github.palexdev.mfxcore.controls.SkinBase;
import io.github.palexdev.mfxcore.utils.fx.SceneBuilderIntegration;
import javafx.util.StringConverter;

public class MFXCheckListCell<T> extends MFXListCell<T> {

    //================================================================================
    // Constructors
    //================================================================================
    public MFXCheckListCell(T item) {
        super(item);
    }

    public MFXCheckListCell(T item, StringConverter<T> converter) {
        super(item, converter);
    }

    //================================================================================
    // Overridden Methods
    //================================================================================

    @Override
    protected SkinBase<?, ?> buildSkin() {
        return new MFXCheckListCellSkin<>(this);
    }

    @Override
    protected void sceneBuilderIntegration() {
        SceneBuilderIntegration.ifInSceneBuilder(() -> getStylesheets().add(MaterialFXStylesheets.CHECK_LIST_CELL.toData()));
    }

    @Override
    public List<String> defaultStyleClasses() {
        return List.of("cell-base", "cell", "mfx-list-cell", "mfx-check-list-cell");
    }
}
