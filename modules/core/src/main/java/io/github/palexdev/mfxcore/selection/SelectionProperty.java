/*
 * Copyright (C) 2025 Parisi Alessandro - alessandro.parisi406@gmail.com
 * This file is part of MaterialFX (https://github.com/palexdev/MaterialFX)
 *
 * MaterialFX is free software: you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 3 of the License,
 * or (at your option) any later version.
 *
 * MaterialFX is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with MaterialFX. If not, see <http://www.gnu.org/licenses/>.
 */

package io.github.palexdev.mfxcore.selection;

import javafx.beans.property.SimpleBooleanProperty;

/// Extension of [SimpleBooleanProperty] meant to be used by classes implementing [Selectable].
///
/// This property overrides the [#set(boolean)] method to correctly handle the selection when a group is/is not present,
/// see [#set(boolean)] for more info.
///
/// Note that for this purpose this property needs the reference to the [Selectable] on which it will operate
/// to get the `Selectable`'s [SelectionGroup].
///
///
/// Last note but not least, if for whatever reason you need to override the `set(...)` method, beware that the
/// `newValue` parameter may not be right as it may be modified by the `Selectables`'s [SelectionGroup]
/// (if there's one), since in Java all parameters are passed by value you won't be able to see the right value.
/// There are two ways to avoid this issue:
///  1) If you need to execute some kind of side effect, and you don't need the old selection state,
/// you can move your code to the [#invalidated()] method instead
///  2) If the above solution cannot be implemented, [SelectionGroup] offers a method to check what the true state
/// of a `Selectable` should be, therefore use [SelectionGroup#check(Selectable, boolean)] to get the true value
public class SelectionProperty extends SimpleBooleanProperty {
    //================================================================================
    // Properties
    //================================================================================
    private final Selectable selectable;

    //================================================================================
    // Constructors
    //================================================================================
    public SelectionProperty(Selectable selectable) {
        this.selectable = selectable;
    }

    public SelectionProperty(Selectable selectable, boolean initialValue) {
        super(initialValue);
        this.selectable = selectable;
    }

    //================================================================================
    // Overridden Methods
    //================================================================================

    /// {@inheritDoc}
    ///
    /// Overridden to correctly handle the selection state (true|false) when the [Selectable] is/is not in a
    /// [SelectionGroup].
    ///
    /// When it is in a group, the `newValue` is given by [SelectionGroup#check(Selectable, boolean)].
    /// This is because there are cases in which the selection cannot be set to true/false at the user will,
    /// the group's rules will prevail.
    ///
    /// For example, the group will not allow a [Selectable] to be deselected if [SelectionGroup#isAtLeastOneSelected()]
    /// is true, and it is the only one present in the selection list. Cases like this, but not limited to, must be handled
    /// by the group.
    ///
    /// Now there can be two cases:
    ///  1) The returned state is "selected" and the group's selection Set doesn't contain the `Selectable`
    ///  2) The returned state is "deselected" and the group's selection Set contains the `Selectable`
    ///
    /// In either case the [#invalidated()] is invoked!
    @Override
    public void set(boolean newValue) {
        SelectionGroup group = selectable.getSelectionGroup();
        if (group != null) {
            newValue = group.check(selectable, newValue);
            if ((newValue && !group.getSelection().contains(selectable)) ||
                !newValue && group.getSelection().contains(selectable)) {
                invalidated();
            }
        }
        super.set(newValue);
    }

    /// {@inheritDoc}
    ///
    /// Overridden to update the [SelectionGroup] assigned to the [Selectable] handled by this property.
    ///
    /// If the `Selectable` is in a group, and the selection state has changed (or additional checks decide that
    /// invalidation is needed, see [#set(boolean)]), then [SelectionGroup#handleSelection(Selectable, boolean)].
    ///
    /// Last but not least, note that the invalidation doesn't occur if the group is changing its state due to a "switch"
    /// operation, see [SelectionGroup#isSwitching()].
    @Override
    protected void invalidated() {
        SelectionGroup group = selectable.getSelectionGroup();
        if (group != null && !group.isSwitching())
            group.handleSelection(selectable, get());
    }
}
