package io.github.palexdev.materialfx.selection;

import java.util.*;
import java.util.stream.Collectors;

import io.github.palexdev.materialfx.selection.base.ISelectionModel;
import io.github.palexdev.mfxcore.base.beans.range.IntegerRange;
import io.github.palexdev.mfxcore.utils.fx.ListChangeHelper;
import io.github.palexdev.virtualizedfx.utils.Utils;
import javafx.beans.property.ListProperty;
import javafx.beans.property.MapProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleMapProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;

import static java.util.function.Function.identity;

public class SelectionModel<T> implements ISelectionModel<T> {
    //================================================================================
    // Properties
    //================================================================================
    private final ListProperty<T> items = new SimpleListProperty<>();
    private final MapProperty<Integer, T> selection = new SimpleMapProperty<>(newMap());
    private SequencedMap<Integer, T> backingMap;
    private boolean allowsMultipleSelection = true;
    private ListChangeHelper<T> lch;

    //================================================================================
    // Constructors
    //================================================================================
    public SelectionModel(ObservableList<T> items) {
        if (items instanceof ListProperty<T> lp) {
            this.items.bind(lp);
        } else {
            this.items.set(items);
        }
        init();
    }

    //================================================================================
    // Methods
    //================================================================================
    protected void init() {
        lch = new ListChangeHelper<>(items)
            .setOnClear(selection::clear)
            .setOnPermutation(p -> replaceSelection(selection.keySet()
                .stream()
                .map(p::get)
                .toArray(Integer[]::new))
            )
            .setOnReplace(rep -> {
                if (!selection.containsKey(rep)) {
                    return;
                }
                selection.put(rep, items.get(rep));
            })
            .setOnRemoved(rem -> {
                List<Integer> updated = ListChangeHelper.shiftOnRemove(selection.keySet(), rem, rem.first());
                replaceSelection(updated.toArray(Integer[]::new));
            })
            .setOnAdded(add -> {
                List<Integer> updated = ListChangeHelper.shiftOnAdd(selection.keySet(), add);
                replaceSelection(updated.toArray(Integer[]::new));
            })
            .init();
    }

    protected ObservableMap<Integer, T> newMap() {
        this.backingMap = new LinkedHashMap<>();
        return FXCollections.observableMap(backingMap);
    }

    protected ObservableMap<Integer, T> newMap(Map<Integer, T> map) {
        this.backingMap = new LinkedHashMap<>(map);
        return FXCollections.observableMap(backingMap);
    }

    //================================================================================
    // Overridden Methods
    //================================================================================
    @Override
    public boolean contains(int index) {
        return selection.containsKey(index);
    }

    @Override
    public boolean contains(T element) {
        return selection.containsValue(element);
    }

    @Override
    public void clearSelection() {
        selection.set(newMap());
    }

    @Override
    public void deselectIndex(int index) {
        selection.remove(index);
    }

    @Override
    public void deselectItem(T item) {
        int index = items.indexOf(item);
        if (index != -1) {
            selection.remove(index);
        }
    }

    @Override
    public void deselectIndexes(int... indexes) {
        ObservableMap<Integer, T> tmp = newMap(selection);
        for (int index : indexes) {
            tmp.remove(index);
        }
        selection.set(tmp);
    }

    @Override
    public void deselectIndexes(IntegerRange range) {
        ObservableMap<Integer, T> tmp = newMap(selection);
        for (Integer index : range) {
            tmp.remove(index);
        }
        selection.set(tmp);
    }

    @Override
    public void deselectItems(T... items) {
        Map<Integer, T> tmp = Arrays.stream(items)
            .mapToInt(this.items::indexOf)
            .filter(i -> i >= 0)
            .boxed()
            .collect(Collectors.toMap(
                identity(),
                this.items::get
            ));
        selection.set(newMap(tmp));
    }

    @Override
    public void selectIndex(int index) {
        T item = items.get(index);
        if (allowsMultipleSelection) {
            selection.put(index, item);
        } else {
            ObservableMap<Integer, T> map = newMap();
            map.put(index, item);
            selection.set(map);
        }
    }

    @Override
    public void selectItem(T item) {
        int index = items.indexOf(item);
        if (index < 0)
            throw new IllegalArgumentException("Item %s is not part of the dataset".formatted(item));
        if (allowsMultipleSelection) {
            selection.put(index, item);
        } else {
            ObservableMap<Integer, T> map = newMap();
            map.put(index, item);
            selection.set(map);
        }
    }

    @Override
    public void selectIndexes(Integer... indexes) {
        if (indexes.length == 0) {
            return;
        }
        if (allowsMultipleSelection) {
            Set<Integer> indexesSet = new LinkedHashSet<>(List.of(indexes));
            Map<Integer, T> newSelection = indexesSet.stream()
                .collect(Collectors.toMap(
                    identity(),
                    items::get,
                    (t, t2) -> t2,
                    LinkedHashMap::new
                ));
            selection.putAll(newSelection);
        } else {
            int index = indexes[indexes.length - 1];
            T item = items.get(index);
            ObservableMap<Integer, T> map = newMap();
            map.put(index, item);
            selection.set(map);
        }
    }

    @Override
    public void selectIndexes(IntegerRange range) {
        if (Utils.INVALID_RANGE.equals(range)) {
            return;
        }
        if (allowsMultipleSelection) {
            Map<Integer, T> newSelection = range.stream().collect(Collectors.toMap(
                identity(),
                items::get,
                (t1, t2) -> t2,
                LinkedHashMap::new
            ));
            selection.putAll(newSelection);
        } else {
            int index = range.getMax();
            T item = items.get(index);
            ObservableMap<Integer, T> map = newMap();
            map.put(index, item);
            selection.set(map);
        }
    }

    @Override
    public void selectItems(T... items) {
        if (items.length == 0) {
            return;
        }
        if (allowsMultipleSelection) {
            Set<Integer> indexesSet = Arrays.stream(items)
                .mapToInt(this.items::indexOf)
                .filter(i -> i >= 0)
                .boxed()
                .collect(Collectors.toSet());
            Map<Integer, T> newSelection = indexesSet.stream()
                .collect(Collectors.toMap(
                    identity(),
                    i -> items[i]
                ));
            selection.putAll(newSelection);
        } else {
            T item = items[items.length - 1];
            int index = this.items.indexOf(item);
            if (index < 0)
                throw new IllegalArgumentException("Item %s is not part of the dataset".formatted(item));

            ObservableMap<Integer, T> map = newMap();
            map.put(index, item);
            selection.set(map);
        }
    }

    @Override
    public void expandSelection(int index, boolean fromLast) {
        if (selection.isEmpty()) {
            replaceSelection(IntegerRange.of(0, index));
            return;
        }

        if (fromLast) {
            Map.Entry<Integer, T> last = backingMap.lastEntry();
            Integer lastIndex = last.getKey();
            int min = Math.min(lastIndex, index);
            int max = Math.max(lastIndex, index);
            selectIndexes(IntegerRange.of(min, max));
            return;
        }

        int min = selection.keySet().stream()
            .min(Integer::compareTo)
            .orElse(-1);
        if (index == min) {
            replaceSelection(index);
            return;
        }

        IntegerRange range = (index < min) ?
            IntegerRange.of(index, min) :
            IntegerRange.of(min, index);
        replaceSelection(range);
    }

    @Override
    public void replaceSelection(Integer... indexes) {
        if (indexes.length == 0) {
            selection.set(newMap());
            return;
        }
        ObservableMap<Integer, T> newSelection = newMap();
        if (allowsMultipleSelection) {
            newSelection.putAll(
                Arrays.stream(indexes)
                    .collect(Collectors.toMap(
                        identity(),
                        items::get)
                    )
            );
        } else {
            Integer index = indexes[indexes.length - 1];
            T item = items.get(index);
            newSelection.put(index, item);
        }
        selection.set(newSelection);
    }

    @Override
    public void replaceSelection(IntegerRange range) {
        if (Utils.INVALID_RANGE.equals(range)) {
            return;
        }

        ObservableMap<Integer, T> newSelection;
        if (allowsMultipleSelection) {
            newSelection = range.stream().collect(Collectors.toMap(
                identity(),
                items::get,
                (t1, t2) -> t2,
                this::newMap
            ));
        } else {
            newSelection = newMap();
            int index = range.getMax();
            T item = items.get(index);
            newSelection.put(index, item);
        }
        selection.set(newSelection);
    }

    @Override
    public void replaceSelection(T... items) {
        ObservableMap<Integer, T> newSelection = newMap();
        if (allowsMultipleSelection) {
            newSelection.putAll(
                Arrays.stream(items)
                    .mapToInt(this.items::indexOf)
                    .filter(i -> i >= 0)
                    .boxed()
                    .collect(Collectors.toMap(
                            identity(),
                            this.items::get
                        )
                    )
            );
        } else {
            T item = items[items.length - 1];
            int index = this.items.indexOf(item);
            if (index < 0)
                throw new IllegalArgumentException("Item %s is not part of the dataset".formatted(item));

            newSelection.put(index, item);
        }
        selection.set(newSelection);
    }

    @Override
    public MapProperty<Integer, T> selection() {
        return selection;
    }

    @Override
    public List<T> getSelectedItems() {
        return List.copyOf(selection.values());
    }

    @Override
    public boolean allowsMultipleSelection() {
        return allowsMultipleSelection;
    }

    @Override
    public void setAllowsMultipleSelection(boolean allowsMultipleSelection) {
        // Clear selection when switching modes
        if (this.allowsMultipleSelection != allowsMultipleSelection) {
            if (!allowsMultipleSelection && size() > 1)
                selection.clear();
        }
        this.allowsMultipleSelection = allowsMultipleSelection;
    }

    @Override
    public Map.Entry<Integer, T> getSelectedEntry() {
        return (size() == 0 || backingMap == null) ? null : backingMap.lastEntry();
    }

    @Override
    public void dispose() {
        lch.dispose();
        lch = null;
        items.unbind();
        items.clear();
        selection.clear();
    }

    //================================================================================
    // Getters
    //================================================================================
    public ObservableList<T> getItems() {
        return FXCollections.unmodifiableObservableList(items);
    }
}
