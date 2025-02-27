package io.github.palexdev.materialfx.skins;


import io.github.palexdev.materialfx.controls.MFXCheckbox;
import io.github.palexdev.materialfx.controls.MFXNotificationCenter;
import io.github.palexdev.materialfx.controls.cell.MFXNotificationCell;
import io.github.palexdev.materialfx.effects.Interpolators;
import io.github.palexdev.materialfx.notifications.base.INotification;
import io.github.palexdev.materialfx.utils.AnimationUtils;
import io.github.palexdev.mfxcore.base.beans.Position;
import io.github.palexdev.mfxcore.controls.SkinBase;
import io.github.palexdev.mfxcore.events.WhenEvent;
import io.github.palexdev.mfxcore.observables.When;
import io.github.palexdev.mfxcore.utils.fx.LayoutUtils;
import io.github.palexdev.virtualizedfx.cells.CellBaseBehavior;
import io.github.palexdev.virtualizedfx.cells.VFXCellBase;
import io.github.palexdev.virtualizedfx.events.VFXContainerEvent;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.VPos;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Rectangle;

import static javafx.scene.layout.Region.USE_PREF_SIZE;

public class MFXNotificationCellSkin extends SkinBase<VFXCellBase<INotification>, CellBaseBehavior<INotification>> {
    //================================================================================
    // Properties
    //================================================================================
    private final MFXCheckbox checkbox;
    private final StackPane container;

    public MFXNotificationCellSkin(MFXNotificationCell cell) {
        super(cell);

        checkbox = new MFXCheckbox("");
        checkbox.setId("check");

        container = new StackPane(checkbox);
        container.setMinWidth(USE_PREF_SIZE);
        container.setPrefWidth(0);
        container.setMaxWidth(USE_PREF_SIZE);

        Rectangle clip = new Rectangle();
        clip.widthProperty().bind(container.widthProperty());
        clip.heightProperty().bind(container.heightProperty());
        container.setClip(clip);

        addListeners();
    }

    //================================================================================
    // Methods
    //================================================================================
    protected void addListeners() {
        MFXNotificationCell cell = getCell();
        listeners(
            When.onInvalidated(cell.itemProperty())
                .then(t -> update()),
            When.onInvalidated(cell.getNotificationCenter().selectionModeProperty())
                .then(this::expand)
                .executeNow()
        );
    }

    protected void update() {
        MFXNotificationCell cell = getCell();
        MFXNotificationCenter notificationCenter = cell.getNotificationCenter();
        INotification notification = cell.getItem();
        if (notificationCenter.isSelectionMode()) {
            checkbox.setOpacity(1.0);
            checkbox.setPrefWidth(45);
        }
        getChildren().setAll(container, notification.getContent());
    }

    protected void expand(boolean selectionMode) {
        MFXNotificationCell cell = getCell();
        MFXNotificationCenter notificationCenter = cell.getNotificationCenter();
        double width = selectionMode ? 45 : 0;
        double opacity = selectionMode ? 1 : 0;
        if (notificationCenter.isAnimated()) {
            AnimationUtils.ParallelBuilder.build()
                .add(
                    AnimationUtils.KeyFrames.of(150, checkbox.opacityProperty(), opacity, Interpolators.EASE_OUT),
                    AnimationUtils.KeyFrames.of(250, container.prefWidthProperty(), width, Interpolators.EASE_OUT_SINE)
                ).getAnimation().play();
        } else {
            container.setPrefWidth(width);
            checkbox.setOpacity(opacity);
        }
        if (!selectionMode) {
            notificationCenter.getSelectionModel().clearSelection();
        }
    }

    protected MFXNotificationCell getCell() {
        return (MFXNotificationCell) getSkinnable();
    }

    //================================================================================
    // Overridden Methods
    //================================================================================
    @Override
    protected void initBehavior(CellBaseBehavior<INotification> behavior) {
        MFXNotificationCell cell = getCell();
        behavior.init();
        events(
            WhenEvent.intercept(cell, VFXContainerEvent.UPDATE)
                .process(e -> {
                    update();
                    e.consume();
                })
        );
    }

    @Override
    protected void layoutChildren(double x, double y, double w, double h) {
        MFXNotificationCell cell = getCell();

        // Checkbox
        checkbox.autosize();
        Position cPos = LayoutUtils.computePosition(
            cell, checkbox,
            x, y, w, h, 0, Insets.EMPTY,
            HPos.LEFT, VPos.CENTER
        );
        checkbox.relocate(cPos.getX(), cPos.getY());

        // Content
        INotification notification = cell.getItem();
        if (notification == null) return;

        Region content = notification.getContent();
        double gap = cell.getHGap();
        layoutInArea(
            content,
            x + gap + checkbox.getLayoutX(), y, w, h, 0,
            HPos.LEFT, VPos.CENTER
        );

    }
}
