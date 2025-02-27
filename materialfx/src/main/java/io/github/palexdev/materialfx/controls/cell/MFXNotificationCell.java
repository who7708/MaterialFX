package io.github.palexdev.materialfx.controls.cell;


import java.util.List;
import java.util.Optional;

import io.github.palexdev.materialfx.controls.MFXNotificationCenter;
import io.github.palexdev.materialfx.selection.base.ISelectionModel;
import io.github.palexdev.materialfx.notifications.base.INotification;

public class MFXNotificationCell extends MFXCheckListCell<INotification> {
    //================================================================================
    // Properties
    //================================================================================
    private MFXNotificationCenter notificationCenter;

    //================================================================================
    // Constructors
    //================================================================================
    public MFXNotificationCell(MFXNotificationCenter notificationCenter, INotification item) {
        super(item);
        this.notificationCenter = notificationCenter;
    }

    //================================================================================
    // Overridden Methods
    //================================================================================
    @Override
    public Optional<ISelectionModel<INotification>> getSelectionModel() {
        return Optional.ofNullable(notificationCenter.getSelectionModel());
    }

    @Override
    protected void sceneBuilderIntegration() {
    }

    @Override
    public List<String> defaultStyleClasses() {
        return List.of("mfx-notification-cell");
    }

    @Override
    public void dispose() {
        this.notificationCenter = null;
        super.dispose();
    }

    //================================================================================
    // Getters
    //================================================================================
    public MFXNotificationCenter getNotificationCenter() {
        return notificationCenter;
    }
}
