package io.github.palexdev.materialfx.selection.base;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import io.github.palexdev.mfxcore.base.beans.range.IntegerRange;
import javafx.beans.property.MapProperty;

@SuppressWarnings("unchecked")
public interface ISelectionModel<T> {
    boolean contains(int index);

    boolean contains(T element);

    void clearSelection();

    void deselectIndex(int index);

    void deselectItem(T item);

    void deselectIndexes(int... indexes);

    void deselectIndexes(IntegerRange range);

    void deselectItems(T... items);

    void selectIndex(int index);

    void selectItem(T item);

    void selectIndexes(Integer... indexes);

    void selectIndexes(IntegerRange range);

    void selectItems(T... items);

    void expandSelection(int index, boolean fromLast);

    void replaceSelection(Integer... indexes);

    void replaceSelection(IntegerRange range);

    void replaceSelection(T... items);

    MapProperty<Integer, T> selection();

    List<T> getSelectedItems();

    default int size() {
        return selection().size();
    }

    default boolean isEmpty() {
        return selection().isEmpty();
    }

    default T getSelectedItem() {
        return (size() == 0) ? null : getSelectedItems().getFirst();
    }

    default Optional<T> getSelectedItemOpt() {
        return Optional.ofNullable(getSelectedItem());
    }

    default T getLastSelectedItem() {
        int size = size();
        return (size == 0) ? null : getSelectedItems().get(size - 1);
    }

    default Optional<T> getLastSelectedItemOpt() {
        return Optional.ofNullable(getLastSelectedItem());
    }

    default Map.Entry<Integer, T> getSelectedEntry() {
        return (size() == 0) ? null : selection().entrySet().iterator().next();
    }

    default Optional<Map.Entry<Integer, T>> getSelectedEntryOpt() {
        return Optional.ofNullable(getSelectedEntry());
    }

    boolean allowsMultipleSelection();

    void setAllowsMultipleSelection(boolean allowsMultipleSelection);

    void dispose();
}
