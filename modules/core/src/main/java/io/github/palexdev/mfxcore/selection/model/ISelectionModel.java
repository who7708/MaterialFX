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

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import io.github.palexdev.mfxcore.base.beans.range.IntegerRange;
import javafx.beans.property.MapProperty;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;

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

    boolean allowsMultipleSelection();

    void setAllowsMultipleSelection(boolean allowsMultipleSelection);

    SelectionEventHandler eventHandler();

    void setEventHandler(Function<ISelectionModel<T>, SelectionEventHandler> fn);

    void dispose();

    //================================================================================
    // Inner Classes
    //================================================================================
    interface SelectionEventHandler {
        void handle(MouseEvent me, int index, boolean selected);

        void handle(KeyEvent ke, int index, boolean selected);
        void handle(MouseEvent me, int index);
        void handle(KeyEvent ke, int index);
    }

    class SingleSelectionHandler implements SelectionEventHandler {
        private final ISelectionModel<?> sm;

        public SingleSelectionHandler(ISelectionModel<?> sm) {this.sm = sm;}

        @Override
        public void handle(MouseEvent me, int index) {
            if (me.getButton() != MouseButton.PRIMARY) return;
            boolean selected = sm.contains(index);
            if (selected) {
                sm.selectIndex(index);
            } else {
                sm.deselectIndex(index);
            }
        }

        @Override
        public void handle(KeyEvent ke, int index) {
            if (ke.getCode() == KeyCode.ENTER || ke.getCode() == KeyCode.SPACE) {
                boolean selected = sm.contains(index);
                if (selected) {
                    sm.selectIndex(index);
                } else {
                    sm.deselectIndex(index);
                }
            }
        }
    }

    class MultipleSelectionHandler implements SelectionEventHandler {
        private final ISelectionModel<?> sm;

        public MultipleSelectionHandler(ISelectionModel<?> sm) {this.sm = sm;}

        @Override
        public void handle(MouseEvent me, int index) {
            boolean shiftDown = me.isShiftDown();
            boolean ctrlDown = me.isControlDown();
            if (!shiftDown && ctrlDown) {
                boolean selected = sm.contains(index);
                if (selected) {
                    sm.deselectIndex(index);
                } else {
                    sm.selectIndex(index);
                }
                return;
            }

            if (shiftDown) {
                sm.expandSelection(index, ctrlDown);
                return;
            }
            sm.replaceSelection(index);
        }

        @Override
        public void handle(KeyEvent ke, int index) {
            if (ke.getCode() != KeyCode.ENTER && ke.getCode() != KeyCode.SPACE) return;
            boolean selected = sm.contains(index);
            if (selected) {
                sm.deselectIndex(index);
            } else {
                sm.selectIndex(index);
            }
        }
    }
}
