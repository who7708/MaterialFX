package io.github.palexdev.materialfx.controls;

import java.util.List;
import java.util.function.Function;

import io.github.palexdev.materialfx.beans.properties.styleable.StyleableObjectProperty;
import io.github.palexdev.materialfx.selection.base.ISelectionModel;
import io.github.palexdev.materialfx.selection.SelectionModel;
import io.github.palexdev.materialfx.selection.base.WithSelectionModel;
import io.github.palexdev.materialfx.theming.MaterialFXStylesheets;
import io.github.palexdev.mfxcore.utils.fx.SceneBuilderIntegration;
import io.github.palexdev.mfxcore.utils.fx.StyleUtils;
import io.github.palexdev.mfxeffects.enums.ElevationLevel;
import io.github.palexdev.virtualizedfx.cells.base.VFXCell;
import io.github.palexdev.virtualizedfx.controls.VFXScrollPane;
import io.github.palexdev.virtualizedfx.list.VFXList;
import javafx.collections.ObservableList;
import javafx.css.CssMetaData;
import javafx.css.Styleable;
import javafx.css.StyleablePropertyFactory;
import javafx.geometry.Orientation;
import javafx.scene.Parent;
import javafx.scene.effect.DropShadow;

public class MFXListView<T, C extends VFXCell<T>> extends VFXList<T, C> implements WithSelectionModel<T> {
    //================================================================================
    // Properties
    //================================================================================
    private final ISelectionModel<T> selectionModel = new SelectionModel<>(itemsProperty());

    private VFXScrollPane vsp;

    //================================================================================
    // Constructors
    //================================================================================
    public MFXListView() {
        initialize();
    }

    public MFXListView(ObservableList<T> items, Function<T, C> cellFactory) {
        super(items, cellFactory);
        initialize();
    }

    public MFXListView(ObservableList<T> items, Function<T, C> cellFactory, Orientation orientation) {
        super(items, cellFactory, orientation);
        initialize();
    }

    //================================================================================
    // Methods
    //================================================================================
    private void initialize() {
        sceneBuilderIntegration();
    }

    protected void findVsp() {
        Parent parent = getParent();
        while (parent != null) {
            if (parent instanceof VFXScrollPane p) {
                vsp = p;
                onDepthChanged();
                break;
            }
            parent = parent.getParent();
        }
    }

    protected void onDepthChanged() {
        ElevationLevel level = getDepth();
        DropShadow effect = (level == null || level == ElevationLevel.LEVEL0) ? null : level.toShadow();
        if (vsp == null) findVsp();
        if (vsp != null) vsp.setEffect(effect);
    }

    //================================================================================
    // Overridden Methods
    //================================================================================
    @Override
    public ISelectionModel<T> getSelectionModel() {
        return selectionModel;
    }

    @Override
    public void sceneBuilderIntegration() {
        SceneBuilderIntegration.ifInSceneBuilder(() -> getStylesheets().add(MaterialFXStylesheets.LIST_VIEW.toData()));
    }

    @Override
    public List<String> defaultStyleClasses() {
        return List.of("mfx-list-view");
    }

    //================================================================================
    // Styleable Properties
    //================================================================================
    private final StyleableObjectProperty<ElevationLevel> depth = new StyleableObjectProperty<>(
        StyleableProperties.DEPTH,
        this,
        "depth",
        ElevationLevel.LEVEL0
    ) {
        @Override
        protected void invalidated() {
            onDepthChanged();
        }
    };

    public ElevationLevel getDepth() {
        return depth.get();
    }

    public StyleableObjectProperty<ElevationLevel> depthProperty() {
        return depth;
    }

    public void setDepth(ElevationLevel depth) {
        this.depth.set(depth);
    }

    //================================================================================
    // CssMetaData
    //================================================================================
    private static class StyleableProperties {
        private static final StyleablePropertyFactory<MFXListView<?, ?>> FACTORY = new StyleablePropertyFactory<>(VFXList.getClassCssMetaData());
        private static final List<CssMetaData<? extends Styleable, ?>> cssMetaDataList;

        private static final CssMetaData<MFXListView<?, ?>, ElevationLevel> DEPTH =
            FACTORY.createEnumCssMetaData(
                ElevationLevel.class,
                "-mfx-depth",
                MFXListView::depthProperty,
                ElevationLevel.LEVEL0
            );

        static {
            cssMetaDataList = StyleUtils.cssMetaDataList(
                VFXList.getClassCssMetaData(),
                DEPTH
            );
        }
    }

    public static List<CssMetaData<? extends Styleable, ?>> getClassCssMetaData() {
        return StyleableProperties.cssMetaDataList;
    }

    @Override
    protected List<CssMetaData<? extends Styleable, ?>> getControlCssMetaData() {
        return getClassCssMetaData();
    }
}
