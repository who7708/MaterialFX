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

package io.github.palexdev.mfxcore.base.bindings.base;

import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import io.github.palexdev.mfxcore.base.bindings.Target;
import javafx.beans.property.Property;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

/// Can be considered as a mix of [Consumer] and [BiConsumer]. Has only one generic type, but the method expects
/// two values of the same type. This is specifically made for use with binding' sources and [ChangeListener]s.
///
/// @param <T> the type of the input to the operation
@FunctionalInterface
public interface Updater<T> {

    /// Performs this operation on the given oldValue and newValue.
    void update(T oldValue, T newValue);

    /// Returns a composed `Updater` that performs, in sequence, this operation followed by the `after` operation.
    /// If performing either operation throws an exception, it is relayed to the caller of the composed operation.
    /// If performing this operation throws an exception, the `after` operation will not be performed.
    ///
    /// @param after the operation to perform after this operation
    /// @return a composed `Updater` that performs in sequence this
    /// operation followed by the `after` operation
    /// @throws NullPointerException if `after` is null
    default Updater<T> andThen(Updater<? super T> after) {
        Objects.requireNonNull(after);
        return (oldValue, newValue) -> {
            update(oldValue, newValue);
            after.update(oldValue, newValue);
        };
    }

    /// Attempts to create an `Updater` for the given [ObservableValue].
    /// This works only for observables that are instance of [Property], as the most
    /// basic `Updater` for them is `(oldValue, newValue) -> property.setValue(newValue)`.
    ///
    /// @throws IllegalArgumentException if the given [ObservableValue] is not an instance of [Property]
    @SuppressWarnings({"rawtypes", "unchecked"})
    static <T> Updater<T> implicit(ObservableValue observable) {
        if (!(observable instanceof Property)) {
            throw new IllegalArgumentException("Cannot create implicit updater for " + observable);
        }
        return (oldValue, newValue) -> ((Property<T>) observable).setValue(newValue);
    }

    /// Calls [#implicit(ObservableValue)] with [Target#getObservable()].
    @SuppressWarnings("rawtypes")
    static <T> Updater<T> implicit(Target target) {
        return implicit(target.getObservable());
    }
}
