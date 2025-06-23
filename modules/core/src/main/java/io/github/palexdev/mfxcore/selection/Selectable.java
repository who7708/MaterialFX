package io.github.palexdev.mfxcore.selection;

/// Public API for components that are selectable (checks, radios, toggles and such).
///
/// Ideally, such controls should have two things:
///  1) A property for the selection state obviously
///  2) A way to group such controls, see [SelectionGroup]
///
/// Note how the API enforces the use of [SelectionProperty] and [SelectionGroupProperty].
///
/// The reason for this decision is that: compared to the pitiful JavaFX API, `SelectionGroups` allow multiple selection,
/// the possibility of always having at least one selection, as well as allowing the grouping of different components
/// as long as they implement this. Those custom properties handle some hassles (like what happens when switching groups,
/// or delegating the selection state to the group if the component is assigned to one) automatically so that neither developers
/// nor users need to worry about that. It's worth mentioning though that this is experimental, meaning that
/// the API may not take into account all the user cases, if such is the case, in the future the API may change and use
/// standard properties.
public interface Selectable {

    default boolean isSelected() {
        return selectedProperty().get();
    }

    /// Specifies the selection state.
    ///
    /// @see SelectionProperty
    SelectionProperty selectedProperty();

    default void setSelected(boolean selected) {
        selectedProperty().set(selected);
    }

    default SelectionGroup getSelectionGroup() {
        return selectionGroupProperty().get();
    }

    /// Specifies the [SelectionGroup] at which this `Selectable` is assigned.
    SelectionGroupProperty selectionGroupProperty();

    default void setSelectionGroup(SelectionGroup group) {
        selectionGroupProperty().set(group);
    }
}