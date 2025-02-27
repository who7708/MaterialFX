package io.github.palexdev.materialfx.controls;

import java.util.List;
import java.util.function.Function;

import io.github.palexdev.materialfx.theming.MaterialFXStylesheets;
import io.github.palexdev.mfxcore.utils.fx.SceneBuilderIntegration;
import io.github.palexdev.virtualizedfx.cells.base.VFXCell;
import javafx.collections.ObservableList;
import javafx.geometry.Orientation;

public class MFXCheckListView<T, C extends VFXCell<T>> extends MFXListView<T, C> {

	//================================================================================
	// Constructors
	//================================================================================
	public MFXCheckListView() {
    }

    public MFXCheckListView(ObservableList<T> items, Function<T, C> cellFactory) {
        super(items, cellFactory);
	}

    public MFXCheckListView(ObservableList<T> items, Function<T, C> cellFactory, Orientation orientation) {
        super(items, cellFactory, orientation);
	}

	//================================================================================
    // Overridden Methods
	//================================================================================

	@Override
    public void sceneBuilderIntegration() {
        SceneBuilderIntegration.ifInSceneBuilder(() -> getStylesheets().add(MaterialFXStylesheets.CHECK_LIST_VIEW.toData()));
	}

	@Override
    public List<String> defaultStyleClasses() {
        return List.of("mfx-check-list");
	}
}