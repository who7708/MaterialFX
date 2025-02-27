package io.github.palexdev.materialfx.skins;


import io.github.palexdev.materialfx.beans.Alignment;
import io.github.palexdev.materialfx.controls.MFXListView;
import io.github.palexdev.materialfx.controls.MFXPagination;
import io.github.palexdev.materialfx.controls.MFXPopup;
import io.github.palexdev.materialfx.controls.cell.MFXListCell;
import io.github.palexdev.materialfx.controls.cell.MFXPage;
import io.github.palexdev.mfxcore.base.beans.range.IntegerRange;
import io.github.palexdev.mfxcore.events.WhenEvent;
import io.github.palexdev.virtualizedfx.cells.CellBaseBehavior;
import io.github.palexdev.virtualizedfx.events.VFXContainerEvent;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.input.MouseEvent;

public class MFXPageSkin extends MFXListCellSkin<Integer> {
    //================================================================================
    // Constructors
    //================================================================================
    public MFXPageSkin(MFXPage cell) {
        super(cell);
    }

    //================================================================================
    // Methods
    //================================================================================
    protected void showPopup() {
        MFXPage cell = getCell();
        MFXPagination pagination = cell.getPagination();
        IntegerRange between = cell.getBetween();
        if (!pagination.isShowPopupForTruncatedPages() || between == null) return;

        ObservableList<Integer> indexes = FXCollections.observableArrayList(IntegerRange.expandRangeToArray(between));
        MFXListView<Integer, MFXListCell<Integer>> listView = new MFXListView<>(indexes, null);

        MFXPopup popup = new MFXPopup(listView);
        popup.getStyleClass().add("pages-popup");
        popup.setPopupStyleableParent(pagination);

        listView.setCellFactory(i -> {
            MFXListCell<Integer> c = new MFXListCell<>(i);
            c.setOnMouseClicked(e -> {
                pagination.setCurrentPage(c.getItem());
                popup.hide();
            });
            return c;
        });

        popup.show(cell, Alignment.of(HPos.CENTER, VPos.BOTTOM), 0, 5);
    }

    //================================================================================
    // Overridden Methods
    //================================================================================
    @Override
    protected void initBehavior(CellBaseBehavior<Integer> behavior) {
        MFXPage cell = getCell();
        behavior.init();
        events(
            WhenEvent.intercept(cell, VFXContainerEvent.UPDATE)
                .process(e -> {
                    update();
                    e.consume();
                }),
            WhenEvent.intercept(cell, MouseEvent.MOUSE_CLICKED)
                .process(e -> behavior.mouseClicked(e, c -> showPopup()))
        );
    }

    @Override
    protected MFXPage getCell() {
        return (MFXPage) super.getCell();
    }
}
