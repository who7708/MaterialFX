package io.github.palexdev.materialfx.controls.cell;


import java.util.function.Consumer;

import io.github.palexdev.materialfx.controls.MFXPagination;
import io.github.palexdev.materialfx.skins.MFXPageSkin;
import io.github.palexdev.mfxcore.base.beans.range.IntegerRange;
import io.github.palexdev.mfxcore.builders.bindings.BooleanBindingBuilder;
import io.github.palexdev.mfxcore.controls.SkinBase;
import io.github.palexdev.virtualizedfx.base.VFXContainer;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;

public class MFXPage extends MFXListCell<Integer> {
    //================================================================================
    // Properties
    //================================================================================
    private MFXPagination pagination;
    private IntegerRange between;

    //================================================================================
    // Constructors
    //================================================================================
    public MFXPage(MFXPagination pagination, int item) {
        super(item);
        this.pagination = pagination;
        setDefaultConverter();
    }

    //================================================================================
    // Methods
    //================================================================================
    public void setDefaultConverter() {
        setConverter(i -> i == -1 ? pagination.getEllipseString() : String.valueOf(i));
    }

    //================================================================================
    // Overridden Methods
    //================================================================================
    @Override
    public void onCreated(VFXContainer<Integer> container) {
        super.onCreated(container);

        selected.bind(BooleanBindingBuilder.build()
            .setMapper(() -> pagination.getCurrentPage() == getItem())
            .addSources(itemProperty(), pagination.currentPageProperty())
            .get()
        );
    }

    @Override
    protected SkinBase<?, ?> buildSkin() {
        return new MFXPageSkin(this);
    }

    @Override
    public void dispose() {
        this.pagination = null;
        super.dispose();
    }

    //================================================================================
    // Getters/Setters
    //================================================================================
    public MFXPagination getPagination() {
        return pagination;
    }

    public IntegerRange getBetween() {
        return between;
    }

    public void setBetween(IntegerRange between) {
        this.between = between;
    }

    //================================================================================
    // Inner Classes
    //================================================================================
    public static class MFXPageBehavior extends MFXListCellBehavior<Integer> {

        public MFXPageBehavior(MFXPage cell) {
            super(cell);
        }

        @Override
        public void mouseClicked(MouseEvent e, Consumer<MouseEvent> callback) {
            MFXPage cell = getNode();
            if (e.getButton() == MouseButton.PRIMARY) {
                MFXPagination pagination = cell.getPagination();
                Integer item = cell.getItem();
                if (item != -1) {
                    pagination.setCurrentPage(item);
                } else {
                    super.mouseClicked(e, callback);
                }
            }
        }

        @Override
        public MFXPage getNode() {
            return (MFXPage) super.getNode();
        }
    }
}
