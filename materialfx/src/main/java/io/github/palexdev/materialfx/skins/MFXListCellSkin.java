package io.github.palexdev.materialfx.skins;

import io.github.palexdev.materialfx.controls.cell.MFXListCell;
import io.github.palexdev.mfxcore.events.WhenEvent;
import io.github.palexdev.mfxeffects.ripple.MFXRippleGenerator;
import io.github.palexdev.virtualizedfx.cells.CellBaseBehavior;
import io.github.palexdev.virtualizedfx.cells.VFXLabeledCellSkin;
import javafx.scene.input.MouseEvent;
import javafx.util.StringConverter;

public class MFXListCellSkin<T> extends VFXLabeledCellSkin<T> {
    //================================================================================
    // Properties
    //================================================================================
    protected final MFXRippleGenerator rg;

    //================================================================================
    // Constructors
    //================================================================================
    public MFXListCellSkin(MFXListCell<T> cell) {
        super(cell);

        rg = new MFXRippleGenerator(cell);
        getChildren().addFirst(rg);
    }

    //================================================================================
    // Methods
    //================================================================================
    protected MFXListCell<T> getCell() {
        return ((MFXListCell<T>) getSkinnable());
    }

    //================================================================================
    // Overridden Methods
    //================================================================================

    @Override
    protected void initBehavior(CellBaseBehavior<T> behavior) {
        super.initBehavior(behavior);
        MFXListCell<T> cell = getCell();
        events(
            WhenEvent.intercept(cell, MouseEvent.MOUSE_PRESSED)
                .process(rg::generate),
            WhenEvent.intercept(cell, MouseEvent.MOUSE_CLICKED)
                .process(e -> behavior.mouseClicked(e, c -> rg.release()))
        );
    }

    @Override
    protected void update() {
        MFXListCell<T> cell = getCell();
        T item = cell.getItem();
        StringConverter<T> converter = cell.getConverter();
        label.setText(converter.toString(item));
    }

    @Override
    protected void layoutChildren(double x, double y, double w, double h) {
        super.layoutChildren(x, y, w, h);

        MFXListCell<T> cell = getCell();
        rg.resizeRelocate(0, 0, cell.getWidth(), cell.getHeight());
    }

    @Override
    public void dispose() {
        rg.dispose();
        super.dispose();
    }
}
