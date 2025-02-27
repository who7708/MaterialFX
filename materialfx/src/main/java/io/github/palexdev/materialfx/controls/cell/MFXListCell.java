package io.github.palexdev.materialfx.controls.cell;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;

import io.github.palexdev.materialfx.selection.base.ISelectionModel;
import io.github.palexdev.materialfx.selection.base.WithSelectionModel;
import io.github.palexdev.materialfx.skins.MFXListCellSkin;
import io.github.palexdev.materialfx.theming.MaterialFXStylesheets;
import io.github.palexdev.materialfx.theming.PseudoClasses;
import io.github.palexdev.mfxcore.base.properties.styleable.StyleableDoubleProperty;
import io.github.palexdev.mfxcore.builders.bindings.BooleanBindingBuilder;
import io.github.palexdev.mfxcore.controls.SkinBase;
import io.github.palexdev.mfxcore.utils.fx.SceneBuilderIntegration;
import io.github.palexdev.mfxcore.utils.fx.StyleUtils;
import io.github.palexdev.virtualizedfx.base.VFXContainer;
import io.github.palexdev.virtualizedfx.cells.CellBaseBehavior;
import io.github.palexdev.virtualizedfx.cells.VFXSimpleCell;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyBooleanWrapper;
import javafx.css.CssMetaData;
import javafx.css.Styleable;
import javafx.css.StyleablePropertyFactory;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.util.StringConverter;

public class MFXListCell<T> extends VFXSimpleCell<T> {
    //================================================================================
    // Properties
    //================================================================================
    protected final ReadOnlyBooleanWrapper selected = new ReadOnlyBooleanWrapper(false) {
        @Override
        protected void invalidated() {
            PseudoClasses.setOn(MFXListCell.this, PseudoClasses.SELECTED, get());
        }
    };

    //================================================================================
    // Constructors
    //================================================================================
    public MFXListCell(T item) {
        super(item);
    }

    public MFXListCell(T item, StringConverter<T> converter) {
        super(item, converter);
    }

    //================================================================================
    // Overridden Methods
    //================================================================================
    @Override
    public void onCreated(VFXContainer<T> container) {
        super.onCreated(container);

        getSelectionModel().ifPresent(sm ->
            selected.bind(BooleanBindingBuilder.build()
                .setMapper(() -> sm.contains(getIndex()))
                .addSources(sm.selection(), indexProperty())
                .get()
            )
        );

        sceneBuilderIntegration();
    }

    @Override
    protected SkinBase<?, ?> buildSkin() {
        return new MFXListCellSkin<>(this);
    }

    @Override
    public Supplier<CellBaseBehavior<T>> defaultBehaviorProvider() {
        return () -> new MFXListCellBehavior<>(this);
    }

    @Override
    public List<String> defaultStyleClasses() {
        return List.of("cell-base", "cell", "mfx-list-cell");
    }

    @Override
    protected void sceneBuilderIntegration() {
        SceneBuilderIntegration.ifInSceneBuilder(() -> getStylesheets().add(MaterialFXStylesheets.LIST_CELL.toData()));
    }

    //================================================================================
    // Styleable Properties
    //================================================================================
    private final StyleableDoubleProperty hGap = new StyleableDoubleProperty(
        StyleableProperties.HGAP,
        this,
        "hGap",
        10.0
    );

    public double getHGap() {
        return hGap.get();
    }

    public StyleableDoubleProperty hGapProperty() {
        return hGap;
    }

    public void setHGap(double hGap) {
        this.hGap.set(hGap);
    }

    //================================================================================
    // CssMetaData
    //================================================================================
    private static class StyleableProperties {
        private static final StyleablePropertyFactory<MFXListCell<?>> FACTORY = new StyleablePropertyFactory<>(VFXSimpleCell.getClassCssMetaData());
        private static final List<CssMetaData<? extends Styleable, ?>> cssMetaDataList;

        private static final CssMetaData<MFXListCell<?>, Number> HGAP =
            FACTORY.createSizeCssMetaData(
                "-fx-hgap",
                MFXListCell::hGapProperty,
                10.0
            );

        static {
            cssMetaDataList = StyleUtils.cssMetaDataList(
                VFXSimpleCell.getClassCssMetaData(),
                HGAP
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

    //================================================================================
    // Getters
    //================================================================================
    @SuppressWarnings("unchecked")
    public Optional<ISelectionModel<T>> getSelectionModel() {
        if (getContainer() instanceof WithSelectionModel<?>) {
            return Optional.of(((WithSelectionModel<T>) getContainer()).getSelectionModel());
        }
        return Optional.empty();
    }

    public boolean isSelected() {
        return selected.get();
    }

    public ReadOnlyBooleanProperty selectedProperty() {
        return selected.getReadOnlyProperty();
    }

    //================================================================================
    // Inner Classes
    //================================================================================
    public static class MFXListCellBehavior<T> extends CellBaseBehavior<T> {

        //================================================================================
        // Constructors
        //================================================================================
        public MFXListCellBehavior(MFXListCell<T> cell) {
            super(cell);
        }

        //================================================================================
        // Methods
        //================================================================================
        protected void updateSelection(SelectionMode mode) {
            MFXListCell<T> cell = getNode();
            cell.getSelectionModel().ifPresent(sm -> {
                int index = cell.getIndex();
                switch (mode) {
                    case STANDARD -> {
                        if (cell.isSelected()) {
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
        public void mouseClicked(MouseEvent e, Consumer<MouseEvent> callback) {
            // We use a null event to signal the selection update comes from another node
            if (e == null) {
                updateSelection(SelectionMode.STANDARD);
                return;
            } else if (e.getButton() == MouseButton.PRIMARY) {
                SelectionMode sm = SelectionMode.forEvent(e);
                updateSelection(sm);
            }
            if (callback != null) callback.accept(e);
        }

        @Override
        public MFXListCell<T> getNode() {
            return (MFXListCell<T>) super.getNode();
        }

        public enum SelectionMode {
            STANDARD,
            EXTEND,
            REPLACE,
            ;

            public static SelectionMode forEvent(MouseEvent me) {
                if (me.isControlDown())
                    return SelectionMode.STANDARD;
                if (me.isShiftDown())
                    return EXTEND;
                return REPLACE;
            }
        }
    }
}
