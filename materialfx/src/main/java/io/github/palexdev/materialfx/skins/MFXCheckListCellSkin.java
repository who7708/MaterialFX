package io.github.palexdev.materialfx.skins;

import io.github.palexdev.materialfx.controls.MFXCheckbox;
import io.github.palexdev.materialfx.controls.cell.MFXCheckListCell;
import io.github.palexdev.mfxcore.base.beans.Position;
import io.github.palexdev.mfxcore.events.WhenEvent;
import io.github.palexdev.mfxcore.utils.fx.LayoutUtils;
import io.github.palexdev.virtualizedfx.cells.CellBaseBehavior;
import io.github.palexdev.virtualizedfx.cells.VFXCellBase;
import io.github.palexdev.virtualizedfx.events.VFXContainerEvent;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.VPos;
import javafx.scene.control.ContentDisplay;
import javafx.scene.input.MouseEvent;

public class MFXCheckListCellSkin<T> extends MFXListCellSkin<T> {
    //================================================================================
    // Properties
    //================================================================================
    private final MFXCheckbox checkbox;

    //================================================================================
    // Constructors
    //================================================================================
    public MFXCheckListCellSkin(MFXCheckListCell<T> cell) {
        super(cell);

        checkbox = new MFXCheckbox() {
            @Override
            public void fire() {
            }
        };
        checkbox.setContentDisposition(ContentDisplay.GRAPHIC_ONLY);
        checkbox.selectedProperty().bind(cell.selectedProperty());
        getChildren().add(checkbox);
    }

    //================================================================================
    // Overridden Methods
    //================================================================================
    @Override
    protected MFXCheckListCell<T> getCell() {
        return (MFXCheckListCell<T>) super.getCell();
    }

    @Override
    protected void initBehavior(CellBaseBehavior<T> behavior) {
        VFXCellBase<T> cell = getSkinnable();
        behavior.init();
        events(
            WhenEvent.intercept(cell, VFXContainerEvent.UPDATE)
                .process(e -> {
                    update();
                    e.consume();
                }),
            WhenEvent.intercept(checkbox, MouseEvent.MOUSE_CLICKED)
                .process(e -> {
                    behavior.mouseClicked(null);
                    e.consume();
                })
        );
    }

    @Override
    protected void layoutChildren(double x, double y, double w, double h) {
        MFXCheckListCell<T> cell = getCell();

        // Checkbox
        checkbox.autosize();
        Position cPos = LayoutUtils.computePosition(
            cell, checkbox,
            x, y, w, h, 0, Insets.EMPTY,
            HPos.LEFT, VPos.CENTER
        );
        checkbox.relocate(cPos.getX(), cPos.getY());

        // Label
        double gap = cell.getHGap();
        layoutInArea(
            label,
            x + gap + checkbox.getWidth(), y, w, h, 0,
            HPos.LEFT, VPos.CENTER
        );

        // Ripple
        rg.resizeRelocate(0, 0, cell.getWidth(), cell.getHeight());
    }
}
