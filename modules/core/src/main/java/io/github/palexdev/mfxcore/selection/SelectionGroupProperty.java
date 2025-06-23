package io.github.palexdev.mfxcore.selection;

import javafx.beans.property.SimpleObjectProperty;

/// Extension of [SimpleObjectProperty] meant to be used by classes implementing [Selectable].
///
/// This property overrides the [#set(SelectionGroup)] method to correctly handle the assignment/removal/switch of a
/// [Selectable] from a [SelectionGroup], see [#set(SelectionGroup)] for more info.
///
/// Note that for this purpose this property needs the reference of the [Selectable] in which it will operate
/// to be able to add/remove the `Selectable` to/from the [SelectionGroup].
public class SelectionGroupProperty extends SimpleObjectProperty<SelectionGroup> {
    //================================================================================
    // Properties
    //================================================================================
    private final Selectable selectable;

    //================================================================================
    // Constructors
    //================================================================================
    public SelectionGroupProperty(Selectable selectable) {
        this.selectable = selectable;
    }

    public SelectionGroupProperty(SelectionGroup initialValue, Selectable selectable) {
        super(initialValue);
        this.selectable = selectable;
    }

    //================================================================================
    // Overridden Methods
    //================================================================================

    /// {@inheritDoc}
    ///
    /// Overridden to correctly handle the addition/removal of a [Selectable] to/from a [SelectionGroup].
    ///
    /// If the `Selectable` was already in a group, it first needs to be removed from it by calling
    /// [SelectionGroup#remove(Selectable)].
    ///
    /// Then it's added to the new group with [SelectionGroup#add(Selectable)], and finally, the `super.set(...)`
    /// method is invoked.
    @Override
    public void set(SelectionGroup newValue) {
        SelectionGroup oldValue = get();
        if (oldValue != null) oldValue.remove(selectable);
        if (newValue != null) {
            newValue.add(selectable);
            super.set(newValue);
            return;
        }
        super.set(null);
    }
}
