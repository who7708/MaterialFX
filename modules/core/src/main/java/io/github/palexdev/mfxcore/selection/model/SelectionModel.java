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

package io.github.palexdev.mfxcore.selection.model;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import io.github.palexdev.mfxcore.base.beans.range.IntegerRange;
import io.github.palexdev.mfxcore.utils.fx.ListChangeHelper;
import javafx.beans.property.ListProperty;
import javafx.beans.property.MapProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleMapProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;

import static java.util.function.Function.identity;

@SuppressWarnings("unchecked")
public class SelectionModel<T> implements ISelectionModel<T> {
    //================================================================================
    // Static Properties
    //================================================================================
    protected static final IntegerRange INVALID_RANGE = IntegerRange.of(-1);

    //================================================================================
    // Properties
    //================================================================================
    private final ListProperty<T> items = new SimpleListProperty<>();
    private final MapProperty<Integer, T> selection = new SimpleMapProperty<>();
    protected SequencedMap<Integer, T> backingMap;
    protected ListChangeHelper<T> lch;
    private boolean allowsMultipleSelection = true;

    private Function<ISelectionModel<T>, SelectionEventHandler> ehSupplier = sm ->
        sm.allowsMultipleSelection() ? new MultipleSelectionHandler(sm) : new SingleSelectionHandler(sm);
    private SelectionEventHandler eh = ehSupplier.apply(this);

    //================================================================================
    // Constructors
    //================================================================================
    public SelectionModel(ObservableList<T> items) {
        this.items.set(items);
    }

    public SelectionModel(ListProperty<T> list) {
        this.items.bind(list);
    }

    {
        init();
    }

    //================================================================================
    // Methods
    //================================================================================
    protected void init() {
        lch = new ListChangeHelper<>(items)
            .setOnClear(selection::clear)
            .setOnPermutation(p -> replaceSelection(
                selection.keySet().stream()
                    .map(p::get)
                    .toArray(Integer[]::new)
            ))
            .setOnReplace(rep -> {
                if (selection.containsKey(rep))
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

    public ObservableList<T> getItems() {
        return FXCollections.unmodifiableObservableList(items);
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
            .filter(this.items::contains)
            .collect(Collectors.toMap(
                this.items::indexOf,
                identity()
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
        if (indexes.length != 0) {
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
    }

    @Override
    public void selectIndexes(IntegerRange range) {
        if (!INVALID_RANGE.equals(range)) {
            if (allowsMultipleSelection) {
                Map<Integer, T> newSelection = range.stream().collect(Collectors.toMap(
                    identity(),
                    items::get,
                    (_, t2) -> t2,
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
    }

    @Override
    public void selectItems(T... items) {
        if (items.length != 0) {
            if (allowsMultipleSelection) {
                Set<Integer> indexesSet = Arrays.stream(items)
                    .mapToInt(this.items::indexOf)
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
                ObservableMap<Integer, T> map = newMap();
                map.put(index, item);
                selection.set(map);
            }
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

        if (index < min) {
            replaceSelection(IntegerRange.of(index, min));
        } else {
            replaceSelection(IntegerRange.of(min, index));
        }
    }

    @Override
    public void replaceSelection(Integer... indexes) {
        if (indexes.length != 0) {
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
    }

    @Override
    public void replaceSelection(IntegerRange range) {
        if (!INVALID_RANGE.equals(range)) {
            ObservableMap<Integer, T> newSelection;
            if (allowsMultipleSelection) {
                newSelection = range.stream().collect(Collectors.toMap(
                    identity(),
                    items::get,
                    (_, t2) -> t2,
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
    }

    @Override
    public void replaceSelection(T... items) {
        ObservableMap<Integer, T> newSelection = newMap();
        if (allowsMultipleSelection) {
            newSelection.putAll(
                Arrays.stream(items)
                    .collect(Collectors.toMap(
                        this.items::indexOf,
                        identity())
                    )
            );
        } else {
            T item = items[items.length - 1];
            int index = this.items.indexOf(item);
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
            selection.clear();
        }
        this.allowsMultipleSelection = allowsMultipleSelection;
    }

    @Override
    public SelectionEventHandler eventHandler() {
        return eh;
    }

    @Override
    public void setEventHandler(Function<ISelectionModel<T>, SelectionEventHandler> fn) {
        ehSupplier = fn;
        eh = ehSupplier.apply(this);
    }

    @Override
    public void dispose() {
        ehSupplier = null;
        eh = null;
        lch.dispose();
        lch = null;
        items.unbind();
        items.clear();
        selection.clear();
    }
}
