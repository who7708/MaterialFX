package io.github.palexdev.materialfx.controls;

import java.util.List;
import java.util.Map;
import java.util.SequencedMap;
import java.util.function.Function;

import io.github.palexdev.materialfx.collections.RefineList;
import io.github.palexdev.materialfx.controls.base.Themable;
import io.github.palexdev.materialfx.filter.base.AbstractFilter;
import io.github.palexdev.materialfx.selection.base.ISelectionModel;
import io.github.palexdev.materialfx.selection.SelectionModel;
import io.github.palexdev.materialfx.selection.base.WithSelectionModel;
import io.github.palexdev.materialfx.controls.cell.MFXTableRow;
import io.github.palexdev.materialfx.skins.MFXTableViewSkin;
import io.github.palexdev.materialfx.theming.MaterialFXStylesheets;
import io.github.palexdev.materialfx.theming.base.Theme;
import io.github.palexdev.mfxcore.base.beans.Size;
import io.github.palexdev.mfxcore.base.beans.range.IntegerRange;
import io.github.palexdev.mfxcore.base.properties.functional.FunctionProperty;
import io.github.palexdev.mfxcore.base.properties.styleable.StyleableDoubleProperty;
import io.github.palexdev.mfxcore.base.properties.styleable.StyleableIntegerProperty;
import io.github.palexdev.mfxcore.base.properties.styleable.StyleableObjectProperty;
import io.github.palexdev.mfxcore.base.properties.styleable.StyleableSizeProperty;
import io.github.palexdev.virtualizedfx.base.VFXStyleable;
import io.github.palexdev.virtualizedfx.cells.base.VFXTableCell;
import io.github.palexdev.virtualizedfx.enums.BufferSize;
import io.github.palexdev.virtualizedfx.enums.ColumnsLayoutMode;
import io.github.palexdev.virtualizedfx.properties.CellFactory;
import io.github.palexdev.virtualizedfx.table.*;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Parent;
import javafx.scene.control.Control;
import javafx.scene.control.Skin;

// TODO prefer composition over inheritance for virtualized components
public class MFXTableView<T> extends Control implements WithSelectionModel<T>, Themable, VFXStyleable {
    //================================================================================
    // Properties
    //================================================================================
    protected final VFXTable<T> vfxTable;

    private final RefineList<T> refineList;
    private final ObservableList<AbstractFilter<T, ?>> filters = FXCollections.observableArrayList();

    private final ISelectionModel<T> selectionModel;

    //================================================================================
    // Constructors
    //================================================================================
    public MFXTableView() {
        this(FXCollections.observableArrayList());
    }

    public MFXTableView(ObservableList<T> items) {
        refineList = new RefineList<>(items);
        selectionModel = new SelectionModel<>(refineList);
        vfxTable = new VFXTable<>(refineList) {
            @Override
            protected Function<T, VFXTableRow<T>> defaultRowFactory() {
                return MFXTableRow::new;
            }
        };
        initialize();
    }

    //================================================================================
    // Methods
    //================================================================================
    private void initialize() {
        getStyleClass().setAll(defaultStyleClasses());
        sceneBuilderIntegration();
        autosizeColumns();
    }

    //================================================================================
    // Overridden Methods
    //================================================================================
    @Override
    public ISelectionModel<T> getSelectionModel() {
        return selectionModel;
    }

    @Override
    public Parent toParent() {
        return this;
    }

    @Override
    public Theme getTheme() {
        return MaterialFXStylesheets.TABLE_VIEW;
    }

    @Override
    public List<String> defaultStyleClasses() {
        return List.of("mfx-table-view");
    }

    @Override
    protected Skin<?> createDefaultSkin() {
        return new MFXTableViewSkin<>(this);
    }

    //================================================================================
    // Delegate Methods
    //================================================================================
    public VFXTable<T> getVirtualizedContainer() {
        return vfxTable;
    }

    public void autosizeColumn(int index) {
        vfxTable.autosizeColumn(index);
    }

    public void setVPos(double vPos) {
        vfxTable.setVPos(vPos);
    }

    public ReadOnlyDoubleProperty virtualMaxYProperty() {
        return vfxTable.virtualMaxYProperty();
    }

    public BufferSize getRowsBufferSize() {
        return vfxTable.getRowsBufferSize();
    }

    public void scrollToLastColumn() {
        vfxTable.scrollToLastColumn();
    }

    public void autosizeColumns() {
        vfxTable.autosizeColumns();
    }

    public Function<ColumnsLayoutMode, VFXTableHelper<T>> getHelperFactory() {
        return vfxTable.getHelperFactory();
    }

    public StyleableObjectProperty<BufferSize> columnsBufferSizeProperty() {
        return vfxTable.columnsBufferSizeProperty();
    }

    public ReadOnlyObjectWrapper<VFXTableHelper<T>> helperProperty() {
        return vfxTable.helperProperty();
    }

    public ViewportLayoutRequest<T> getViewportLayoutRequest() {
        return vfxTable.getViewportLayoutRequest();
    }

    public void scrollToPixelHorizontal(double pixel) {
        vfxTable.scrollToPixelHorizontal(pixel);
    }

    public int indexOf(VFXTableColumn<T, ?> column) {
        return vfxTable.indexOf(column);
    }

    public IntegerRange getColumnsRange() {
        return vfxTable.getColumnsRange();
    }

    public void setExtraAutosizeWidth(double extraAutosizeWidth) {
        vfxTable.setExtraAutosizeWidth(extraAutosizeWidth);
    }

    public BufferSize getBufferSize() {
        return vfxTable.getBufferSize();
    }

    public double getClipBorderRadius() {
        return vfxTable.getClipBorderRadius();
    }

    public double getRowsHeight() {
        return vfxTable.getRowsHeight();
    }

    public VFXTable<T> populateCacheAll() {
        return vfxTable.populateCacheAll();
    }

    public void scrollToFirstColumn() {
        vfxTable.scrollToFirstColumn();
    }

    public VFXTableState<T> getState() {
        return vfxTable.getState();
    }

    public void scrollToRow(int index) {
        vfxTable.scrollToRow(index);
    }

    public void setRowsCacheCapacity(int rowsCacheCapacity) {
        vfxTable.setRowsCacheCapacity(rowsCacheCapacity);
    }

    public BufferSize getColumnsBufferSize() {
        return vfxTable.getColumnsBufferSize();
    }

    public double getHPos() {
        return vfxTable.getHPos();
    }

    public ReadOnlyDoubleProperty virtualMaxXProperty() {
        return vfxTable.virtualMaxXProperty();
    }

    public void setColumnsWidth(double w) {
        vfxTable.setColumnsWidth(w);
    }

    public VFXTableHelper<T> getHelper() {
        return vfxTable.getHelper();
    }

    public IntegerRange getRowsRange() {
        return vfxTable.getRowsRange();
    }

    public StyleableDoubleProperty extraAutosizeWidthProperty() {
        return vfxTable.extraAutosizeWidthProperty();
    }

    public void setColumnsSize(Size columnsSize) {
        vfxTable.setColumnsSize(columnsSize);
    }

    public void setRowFactory(Function<T, VFXTableRow<T>> rowFactory) {
        vfxTable.setRowFactory(rowFactory);
    }

    public VFXTable<T> populateCache() {
        return vfxTable.populateCache();
    }

    public ReadOnlyObjectProperty<VFXTableState<T>> stateProperty() {
        return vfxTable.stateProperty();
    }

    public void scrollVerticalBy(double pixels) {
        vfxTable.scrollVerticalBy(pixels);
    }

    public boolean isNeedsViewportLayout() {
        return vfxTable.isNeedsViewportLayout();
    }

    public void scrollToLastRow() {
        vfxTable.scrollToLastRow();
    }

    public void setColumnsHeight(double h) {
        vfxTable.setColumnsHeight(h);
    }

    public Function<T, VFXTableRow<T>> getRowFactory() {
        return vfxTable.getRowFactory();
    }

    public double getVPos() {
        return vfxTable.getVPos();
    }

    public void setRowsBufferSize(BufferSize rowsBufferSize) {
        vfxTable.setRowsBufferSize(rowsBufferSize);
    }

    public DoubleProperty hPosProperty() {
        return vfxTable.hPosProperty();
    }

    public ReadOnlyDoubleProperty maxHScrollProperty() {
        return vfxTable.maxHScrollProperty();
    }

    public void setColumnsSize(double w, double h) {
        vfxTable.setColumnsSize(w, h);
    }

    public List<Map.Entry<T, VFXTableRow<T>>> getRowsByItemUnmodifiable() {
        return vfxTable.getRowsByItemUnmodifiable();
    }

    public void scrollToColumn(int index) {
        vfxTable.scrollToColumn(index);
    }

    public void setHelperFactory(Function<ColumnsLayoutMode, VFXTableHelper<T>> helperFactory) {
        vfxTable.setHelperFactory(helperFactory);
    }

    public double getExtraAutosizeWidth() {
        return vfxTable.getExtraAutosizeWidth();
    }

    public void setBufferSize(BufferSize bufferSize) {
        vfxTable.setBufferSize(bufferSize);
    }

    public int cellsCacheSize() {
        return vfxTable.cellsCacheSize();
    }

    public void setClipBorderRadius(double clipBorderRadius) {
        vfxTable.setClipBorderRadius(clipBorderRadius);
    }

    public StyleableSizeProperty columnsSizeProperty() {
        return vfxTable.columnsSizeProperty();
    }

    public StyleableIntegerProperty rowsCacheCapacityProperty() {
        return vfxTable.rowsCacheCapacityProperty();
    }

    public StyleableObjectProperty<BufferSize> bufferSizeProperty() {
        return vfxTable.bufferSizeProperty();
    }

    public void setColumnsLayoutMode(ColumnsLayoutMode columnsLayoutMode) {
        vfxTable.setColumnsLayoutMode(columnsLayoutMode);
    }

    public CellFactory<T, VFXTableRow<T>> rowFactoryProperty() {
        return vfxTable.rowFactoryProperty();
    }

    public ObservableList<VFXTableColumn<T, ? extends VFXTableCell<T>>> getColumns() {
        return vfxTable.getColumns();
    }

    public void scrollHorizontalBy(double pixels) {
        vfxTable.scrollHorizontalBy(pixels);
    }

    public void setRowsHeight(double rowsHeight) {
        vfxTable.setRowsHeight(rowsHeight);
    }

    public void scrollToFirstRow() {
        vfxTable.scrollToFirstRow();
    }

    public DoubleProperty vPosProperty() {
        return vfxTable.vPosProperty();
    }

    public void update(int... indexes) {
        vfxTable.update(indexes);
    }

    public ReadOnlyDoubleProperty maxVScrollProperty() {
        return vfxTable.maxVScrollProperty();
    }

    public StyleableObjectProperty<BufferSize> rowsBufferSizeProperty() {
        return vfxTable.rowsBufferSizeProperty();
    }

    public ColumnsLayoutMode getColumnsLayoutMode() {
        return vfxTable.getColumnsLayoutMode();
    }

    public SequencedMap<Integer, VFXTableRow<T>> getRowsByIndexUnmodifiable() {
        return vfxTable.getRowsByIndexUnmodifiable();
    }

    public void setHPos(double hPos) {
        vfxTable.setHPos(hPos);
    }

    public void autosizeColumn(VFXTableColumn<T, ?> column) {
        vfxTable.autosizeColumn(column);
    }

    public void setColumnsBufferSize(BufferSize columnsBufferSize) {
        vfxTable.setColumnsBufferSize(columnsBufferSize);
    }

    public FunctionProperty<ColumnsLayoutMode, VFXTableHelper<T>> helperFactoryProperty() {
        return vfxTable.helperFactoryProperty();
    }

    public int getRowsCacheCapacity() {
        return vfxTable.getRowsCacheCapacity();
    }

    public void scrollToPixelVertical(double pixel) {
        vfxTable.scrollToPixelVertical(pixel);
    }

    public Size getColumnsSize() {
        return vfxTable.getColumnsSize();
    }

    public void setHelper(VFXTableHelper<T> helper) {
        vfxTable.setHelper(helper);
    }

    public ReadOnlyObjectProperty<ViewportLayoutRequest<T>> needsViewportLayoutProperty() {
        return vfxTable.needsViewportLayoutProperty();
    }

    public StyleableObjectProperty<ColumnsLayoutMode> columnsLayoutModeProperty() {
        return vfxTable.columnsLayoutModeProperty();
    }

    public void requestViewportLayout() {
        vfxTable.requestViewportLayout();
    }

    public StyleableDoubleProperty rowsHeightProperty() {
        return vfxTable.rowsHeightProperty();
    }

    public void switchColumnsLayoutMode() {
        vfxTable.switchColumnsLayoutMode();
    }

    public int rowsCacheSize() {
        return vfxTable.rowsCacheSize();
    }

    public StyleableDoubleProperty clipBorderRadiusProperty() {
        return vfxTable.clipBorderRadiusProperty();
    }

    //================================================================================
    // Getters
    //================================================================================
    public ObservableList<T> getItems() {
        return refineList;
    }

    public ObservableList<AbstractFilter<T, ?>> getFilters() {
        return filters;
    }
}
