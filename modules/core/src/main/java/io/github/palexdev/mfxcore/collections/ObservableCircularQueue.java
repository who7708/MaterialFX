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

package io.github.palexdev.mfxcore.collections;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;

/// A crude implementation of an observable queue with limited capacity, the backing data structure is [CircularQueue].
///
/// @param <E> Any type
public class ObservableCircularQueue<E> extends SimpleListProperty<E> {
    //================================================================================
    // Properties
    //================================================================================
    private final CircularQueue<E> queue;

    //================================================================================
    // Constructors
    //================================================================================
    public ObservableCircularQueue(int capacity) {
        this.queue = new CircularQueue<>(capacity);
        super.set(FXCollections.observableList(queue));
    }

    //================================================================================
    // Methods
    //================================================================================
    @Override
    public boolean add(E element) {
        if (size() == queue.getCapacity()) {
            queue.remove();
        }
        return super.add(element);
    }

    @Override
    public void add(int i, E element) {
        if (size() == queue.getCapacity()) {
            queue.remove();
        }
        int clamped = Math.min(i, getCapacity() - 1);
        super.add(clamped, element);
    }

    @Override
    public boolean addAll(E... elements) {
        boolean res = false;
        for (E element : elements) {
            res = add(element);
        }
        return res;
    }

    @Override
    public boolean addAll(Collection<? extends E> elements) {
        boolean res = false;
        for (E element : elements) {
            res = add(element);
        }
        return res;
    }

    @Override
    public boolean addAll(int i, Collection<? extends E> elements) {
        List<E> toList = new ArrayList<>(elements);
        for (int j = 0; j < toList.size(); j++) {
            E e = toList.get(j);
            add(i + j, e);
        }
        return true;
    }

    @Override
    public boolean setAll(E... elements) {
        boolean res = false;
        clear();
        for (E element : elements) {
            res = add(element);
        }
        return res;
    }

    @Override
    public boolean setAll(Collection<? extends E> elements) {
        boolean res = false;
        clear();
        for (E element : elements) {
            res = add(element);
        }
        return res;
    }

    //================================================================================
    // Getters/Setters
    //================================================================================

    /// Delegate of [#getCapacity()].
    public int getCapacity() {
        return queue.getCapacity();
    }

    /// Delegate of [#setCapacity(int)].
    public void setCapacity(int capacity) {
        queue.setCapacity(capacity);
    }
}
