package io.github.palexdev.mfxcore.selection;

import java.util.*;

import io.github.palexdev.mfxcore.enums.SelectionMode;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableSet;
import javafx.collections.SetChangeListener;

/// Custom implementation and expansion of that pitiful thing that is [javafx.scene.control.ToggleGroup].
///
/// A `SelectionGroup` will work with anything that implements the necessary API described by the [Selectable]
/// interface. Not only that, it is also a lot more flexible and convenient.
///
/// You can set the selection to be single or multiple, by just setting the [#selectionModeProperty()], as well as
/// tell the group to always keep at least one [Selectable] active, by setting the [#atLeastOneSelectedProperty()]
/// to true. All of these can be changed anytime, although you better know the side effects of some particular cases, will
/// be listed below.
///
/// So you now have a grouping API for everything, not only controls, as long as they implement [Selectable], and you
/// also have capabilities such as 'at most/at least one selected', in single and multiple configurations, in just one class!
///
/// All of this sounds good right? Well, there are some caveats of course.
///
/// First of all, keep in mind that if you want to use this you will be forced to use the two custom properties:
/// [SelectionProperty] and [SelectionGroupProperty], the reason for this is to make the implementation/integration
/// for users less of a pain and less error-prone, more info can be found in the relative's docs.
///
/// Since this also supports multiple selection, for obvious reasons, the selection is a collection of `Selectables`.
/// To be precise, both the collections used to keep the `Selectables` that are managed by the group, and the ones
/// that are currently selected, are [ObservableSet] backed by a [LinkedHashSet].
/// The usage of such collections vastly helps to avoid duplicates while also having fast lookups (contains).
///
/// **Special cases when changing config**
///  1) When switching from MULTIPLE to SINGLE selection mode, the selection will be the same only and only if there was
/// only one `Selectable` in the selection Set, in all other cases the selection is **cleared!**
///  2) When activating the 'atLeastOneSelected' mode, if there are `Selectables` in the group the first will
/// be immediately selected! If none is available, the first added to the group will be.
///  3) If 'atLeastOneSelected' mode is active and multiple `Selectables` are added at the same time to the group,
/// and two or more of them are selected, the last will prevail, the others will be deselected (if in SINGLE selection mode)
///
/// Last but not least, to avoid some if statements, this makes use of polymorphism delegating the selection handling to
/// two internal classes, one for SINGLE selection mode, the other for the MULTIPLE mode.
public class SelectionGroup {
    //================================================================================
    // Properties
    //================================================================================
    private final ObjectProperty<SelectionMode> selectionMode = new SimpleObjectProperty<>() {
        @Override
        protected void invalidated() {
            SelectionMode mode = get();
            if (mode == SelectionMode.SINGLE) {
                handler = new SingleSelectionHandler();
                if (selection.size() > 1) selection.clear();
                return;
            }
            handler = new MultipleSelectionHandler();
        }
    };
    private final BooleanProperty atLeastOneSelected = new SimpleBooleanProperty() {
        @Override
        protected void invalidated() {
            boolean val = get();
            if (val && selection.isEmpty() && !selectables.isEmpty()) {
                getFirstSelectable().ifPresent(s -> s.setSelected(true));
            }
        }
    };

    private final SequencedSet<Selectable> _selectables = new LinkedHashSet<>();
    private final ObservableSet<Selectable> selectables = FXCollections.observableSet(_selectables);

    private final SequencedSet<Selectable> _selection = new LinkedHashSet<>();
    private final ObservableSet<Selectable> selection = FXCollections.observableSet(_selection);

    private SelectionHandler handler;
    private boolean isSwitching = false;
    private boolean isRemoval = false;

    //================================================================================
    // Constructors
    //================================================================================
    public SelectionGroup() {
        this(SelectionMode.SINGLE);
    }

    public SelectionGroup(SelectionMode selectionMode) {
        this(selectionMode, false);
    }

    public SelectionGroup(SelectionMode selectionMode, boolean atLeastOneSelected) {
        setSelectionMode(selectionMode);
        setAtLeastOneSelected(atLeastOneSelected);

        selectables.addListener(this::onSelectablesChanged);
        selection.addListener(this::onSelectionChanged);
    }

    //================================================================================
    // Methods
    //================================================================================

    /// Adds the given [Selectable] to this group (if not already present).
    ///
    /// If the given `Selectable`'s group is not the same as this, [Selectable#setSelectionGroup(SelectionGroup)]
    /// is also called. See [SelectionGroupProperty].
    ///
    /// When adding a `Selectable` to a group, there are a bunch of things to consider. The group has no guarantees
    /// that the given objects are in a state such that its rules won't be broken. For this reason, it's mandatory to perform
    /// a check on the `Selectable`'s state by invoking [#handleSelection(Selectable,boolean)]. If the returned
    /// correct state is different, then it's important to also fix it by invoking [Selectable#setSelected(boolean)].
    public SelectionGroup add(Selectable selectable) {
        if (selectable == null || !selectables.add(selectable)) return this;

        SelectionGroup group = selectable.getSelectionGroup();
        if (group != this) {
            selectable.setSelectionGroup(this);

            boolean state = handler.handle(selectable, selectable.isSelected());
            selectable.setSelected(state);
        }
        return this;
    }

    /// Calls [#add(Selectable)] on each given `Selectable`.
    public SelectionGroup addAll(Selectable... selectables) {
        for (Selectable selectable : selectables) {
            add(selectable);
        }
        return this;
    }

    /// Calls [#add(Selectable)] on each given `Selectable`.
    public SelectionGroup addAll(Collection<? extends Selectable> selectables) {
        for (Selectable selectable : selectables) {
            add(selectable);
        }
        return this;
    }

    /// Removes the given [Selectable] from the group (if present).
    ///
    /// The removal will also trigger [#onSelectablesChanged(SetChangeListener.Change)].
    public SelectionGroup remove(Selectable selectable) {
        if (!selectables.contains(selectable)) return this;
        isRemoval = true;
        selectables.remove(selectable);
        isRemoval = false;
        return this;
    }

    /// Calls [#remove(Selectable)] on each given `Selectable`.
    public SelectionGroup removeAll(Selectable... selectables) {
        for (Selectable selectable : selectables) {
            remove(selectable);
        }
        return this;
    }

    /// Calls [#remove(Selectable)] on each given `Selectable`.
    public SelectionGroup removeAll(Collection<? extends Selectable> selectables) {
        for (Selectable selectable : selectables) {
            remove(selectable);
        }
        return this;
    }

    /// Removes all the [Selectables][Selectable] from the group.
    public SelectionGroup clear() {
        selectables.clear();
        return this;
    }

    /// Given a [Selectable] and its current or 'requested' state returns a value that won't break the rules
    /// of the `SelectionGroup`.
    ///
    /// For example (but there are many other cases), if the 'atLeastOneSelected' mode is on, the given `Selectable`
    /// is the last selected one, and the requested selection state is 'false', the group won't allow it and return 'true'
    /// instead.
    ///
    /// This is the same mechanism used by [SelectionProperty] to avoid 'illegal' selection states.
    ///
    /// Delegates to the current selection handler.
    public boolean check(Selectable selectable, boolean state) {
        return handler.check(selectable, state);
    }

    //================================================================================
    // Protected Methods
    //================================================================================

    /// Given a [Selectable] and its current or 'requested' state returns a value that won't break the rules
    /// of the `SelectionGroup`.
    ///
    /// This is used by [SelectionProperty] to not feed the [SelectionProperty#set(boolean)] method values that
    /// would break the rules of the [SelectionGroup]. In other words, when the state is requested to switch to
    /// selected/deselected, the property first asks the group if it is allowed, in case it is not, the `newValue`
    /// parameter is "corrected".
    ///
    /// The difference between this and [#check(Selectable,boolean)] is that other than returning the correct state for
    /// the given [Selectable], this will also modify the state of the group. In fact, according to the returned state,
    /// the given [Selectable] will be also added/removed to/from the selection Set ([#getSelection()]).
    protected boolean handleSelection(Selectable selectable, boolean state) {
        if (!selectables.contains(selectable)) return state;
        return handler.handle(selectable, state);
    }

    /// Triggers when a [Selectable] is removed from the [ObservableSet] containing all the `Selectables`
    /// managed by the group.
    ///
    /// This will cause the [Selectable] to be also removed from the selection [ObservableSet] (meaning that
    /// [#onSelectionChanged(SetChangeListener.Change)] will also be triggered) as well as its `SelectionGroup`
    /// to be set to `null`.
    protected void onSelectablesChanged(SetChangeListener.Change<? extends Selectable> c) {
        Selectable removed = c.getElementRemoved();
        if (c.wasRemoved()) {
            selection.remove(removed);
            removed.setSelectionGroup(null);
        }
    }

    /// Triggers when a [Selectable] is removed from the [ObservableSet] containing all the `Selectables`
    /// that are currently selected.
    ///
    /// This executes two actions in two specific occasions:
    ///  1) If the removed `Selectable` is selected and the removal has not been triggered by any of the
    /// 'remove' methods then the `Selectable` is deselected (`selectable.setSelected(false)`)
    ///  2) If the selection is now empty, the 'atLeastOneSelected' mode is on and the removal was triggered by one of
    /// the 'remove' methods, then ensures that there's at least one `Selectable` that is selected by using
    /// [#getFirstSelectable()] and then if present `selectable.setSelected(true)`
    protected void onSelectionChanged(SetChangeListener.Change<? extends Selectable> c) {
        Selectable removed = c.getElementRemoved();
        if (c.wasRemoved() && removed.isSelected() && !isRemoval) {
            removed.setSelected(false);
        }
        if (c.getSet().isEmpty() && isAtLeastOneSelected() && isRemoval) {
            getFirstSelectable().ifPresent(s -> s.setSelected(true));
        }
    }

    /// @return the state of a special flag that indicates whether changes currently occurring in the group are caused
    /// by a "switch" operation. This occurs when the group is in SINGLE selection mode, and a [Selectable] is going
    /// to take the place of another one (the current selected).
    ///
    /// More details: this flag is set to true when the selection Set is going to be cleared so that the new `Selectable`
    /// can take its place. The flag is reset immediately after. However, before the reset, listeners attached to the selection
    /// Set will trigger, causing the [#handleSelection(Selectable, boolean)] to trigger again. This can be problematic
    /// when the "At least one selected" feature is on. Since the "switch" process has not been completed yet, the
    /// group will try to select the first `Selectable` in the [#getSelectables()] Set, so that the rule is
    /// respected. This behavior is undesired; the flag will stop the group from doing this, afterward the "switch" process
    /// is completed.
    public boolean isSwitching() {
        return isSwitching;
    }

    //================================================================================
    // Internal Classes
    //================================================================================
    interface SelectionHandler {

        boolean check(Selectable selectable, boolean state);

        boolean handle(Selectable selectable, boolean state);
    }

    class SingleSelectionHandler implements SelectionHandler {
        @Override
        public boolean check(Selectable selectable, boolean state) {
            if (!selectables.contains(selectable)) return state;
            if (!state) {
                if (isAtLeastOneSelected()) {
                    return !isSwitching() && (selection.size() == 1 && selection.contains(selectable) || selection.isEmpty());
                }
                return false;
            }
            return true;
        }

        @Override
        public boolean handle(Selectable selectable, boolean state) {
            if (!state) {
                if (isAtLeastOneSelected()) {
                    if (isSwitching()) {
                        selection.remove(selectable);
                        return false;
                    }
                    if (selection.size() == 1 && selection.contains(selectable)) {
                        return true;
                    }
                    if (selection.isEmpty()) {
                        selection.add(selectable);
                        return true;
                    }
                }
                selection.remove(selectable);
                return false;
            }

            if (selection.contains(selectable)) return true;
            isSwitching = true;
            selection.clear();
            selection.add(selectable);
            isSwitching = false;
            return true;
        }
    }

    class MultipleSelectionHandler implements SelectionHandler {
        @Override
        public boolean check(Selectable selectable, boolean state) {
            if (!selectables.contains(selectable)) return state;
            if (!state) {
                if (isAtLeastOneSelected()) {
                    return (selection.size() == 1 && selection.contains(selectable)) || selection.isEmpty();
                }
                return false;
            }
            return true;
        }

        @Override
        public boolean handle(Selectable selectable, boolean state) {
            if (!state) {
                if (isAtLeastOneSelected()) {
                    if (selection.size() == 1 && selection.contains(selectable)) {
                        return true;
                    }
                    if (selection.isEmpty()) {
                        selection.add(selectable);
                        return true;
                    }
                }
                selection.remove(selectable);
                return false;
            }
            selection.add(selectable);
            return true;
        }
    }

    //================================================================================
    // Getters
    //================================================================================

    /// @return an unmodifiable [ObservableSet] which contains all the `Selectables` managed by the group
    public ObservableSet<Selectable> getSelectables() {
        return FXCollections.unmodifiableObservableSet(selectables);
    }

    /// @return an unmodifiable [ObservableSet] which contains all the `Selectables` that are currently selected
    public ObservableSet<Selectable> getSelection() {
        return FXCollections.unmodifiableObservableSet(selection);
    }

    /// @return [#getSelectables()] but as a modifiable List, changes to this collection won't have any effect on the
    /// group
    public List<Selectable> getSelectablesList() {
        return new ArrayList<>(selectables);
    }

    /// @return [#getSelection()] but as a modifiable List, changes to this collection won't have any effect on the
    /// group
    public List<Selectable> getSelectionList() {
        return new ArrayList<>(selection);
    }

    /// Convenience method to get the first added [Selectable] of this group. As the group may contain no
    /// `Selectables`, this returns an [Optional] instead of raising an Exception.
    protected Optional<Selectable> getFirstSelectable() {
        try {
            return Optional.of(_selectables.getFirst());
        } catch (Exception ex) {
            return Optional.empty();
        }
    }

    /// Convenience method to get the first selected [Selectable] of this group. As the group selection may be empty,
    /// this returns an [Optional] instead of raising an Exception.
    public Optional<Selectable> getFirstSelected() {
        try {
            return Optional.of(_selection.getFirst());
        } catch (Exception ex) {
            return Optional.empty();
        }
    }

    public SelectionMode getSelectionMode() {
        return selectionMode.get();
    }

    /// Specifies the selection mode of the group, can be set to single or multiple selection.
    public ObjectProperty<SelectionMode> selectionModeProperty() {
        return selectionMode;
    }

    public void setSelectionMode(SelectionMode mode) {
        this.selectionMode.set(mode);
    }

    public boolean isAtLeastOneSelected() {
        return atLeastOneSelected.get();
    }

    /// Specifies whether the group should always keep at least one of its `Selectables` selected.
    ///
    /// This may be useful for use cases in which a user is forced to pick a choice, no matter what, as long as it is one
    /// of the offered.
    ///
    /// @see SelectionGroup
    public BooleanProperty atLeastOneSelectedProperty() {
        return atLeastOneSelected;
    }

    public void setAtLeastOneSelected(boolean atLeastOneSelected) {
        this.atLeastOneSelected.set(atLeastOneSelected);
    }
}
