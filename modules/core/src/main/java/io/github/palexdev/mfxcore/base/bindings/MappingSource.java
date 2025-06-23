/*
 * Copyright (C) 2022 Parisi Alessandro - alessandro.parisi406@gmail.com
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

package io.github.palexdev.mfxcore.base.bindings;

import io.github.palexdev.mfxcore.base.bindings.base.Updater;
import io.github.palexdev.mfxcore.enums.BindingType;
import javafx.beans.value.ObservableValue;

/// Concrete implementation of [AbstractSource]. This type of `Source` is capable of mapping the source's type `S` to
/// the target's type and vice versa to perform both [#updateTarget(Object, Object)] and [#updateSource(Object, Object)].
///
/// @param <S> the source's observable type
/// @param <T> the target's observable type
public class MappingSource<S, T> extends AbstractSource<S, T> {
    //================================================================================
    // Properties
    //================================================================================
    protected MappedUpdater<S, T> targetUpdater; // From source to target
    protected MappedUpdater<T, S> sourceUpdater; // From target to source

    //================================================================================
    // Constructors
    //================================================================================
    protected MappingSource() {
    }

    public MappingSource(ObservableValue<? extends S> observable) {
        super(observable);
    }

    public static <S, T> MappingSource<S, T> of(ObservableValue<? extends S> observable) {
        return new MappingSource<>(observable);
    }

    //================================================================================
    // Methods
    //================================================================================

    /// {@inheritDoc}
    ///
    /// Operates differently depending on which binding activated this.
    ///
    /// For unidirectional bindings the target instance is `null` so the [#getTargetUpdater()] is called then exits.
    ///
    /// For bidirectional bindings the target instance is not null. Before calling the [#getTargetUpdater()]
    /// we first must check that the update is not has not been invoked because of a "bounce" effect, [Target#isFromSource()].
    @Override
    public void updateTarget(S oldValue, S newValue) {
        if (target.bindingType() == BindingType.UNIDIRECTIONAL) {
            try {
                target.ignoreBinding = true;
                targetUpdater.update(oldValue, newValue);
            } finally {
                target.ignoreBinding = false;
            }
            return;
        }

        if (target.isFromSource()) return;
        targetUpdater.update(oldValue, newValue);
    }

    /// {@inheritDoc}
    ///
    /// Operates differently depending on which binding activated this.
    ///
    /// For unidirectional bindings the target instance is `null` so the [#getTargetUpdater()] is called then exits.
    ///
    /// For bidirectional bindings the target instance is not null. The call to [#getSourceUpdater()]
    /// is surrounded by a try-finally block in which we also set [Target#isFromSource()] to true, if anything goes wrong,
    /// the finally block ensures to reset [Target#isFromSource()] back to false.
    @Override
    public void updateSource(T oldValue, T newValue) {
        if (target == null) {
            sourceUpdater.update(oldValue, newValue);
            return;
        }

        try {
            target.fromSource = true;
            sourceUpdater.update(oldValue, newValue);
        } finally {
            target.fromSource = false;
        }
    }

    /// {@inheritDoc}
    ///
    /// For unidirectional bindings. The listener added to this source's observable is responsible for
    /// triggering [#updateTarget(Object, Object)].
    @Override
    protected void listen() {
        if (obvListener == null) obvListener = (ov, o, n) -> updateTarget(o, n);
        observable.addListener(obvListener);
    }

    /// {@inheritDoc}
    ///
    /// For bidirectional bindings. The source's target is set to the given one.
    /// Then [#listen()] is called. Then a listener to the given target is added and is responsible for
    /// triggering [#updateSource(Object, Object)].
    @Override
    protected void listen(Target<T> target) {
        listen();

        this.target = target;
        if (tgtListener == null) tgtListener = (ov, o, n) -> updateSource(o, n);
        target.getObservable().addListener(tgtListener);
    }

    /// {@inheritDoc}
    ///
    /// Removes the listeners added to this source's observable and the target's observable (if not null).
    /// Then sets all its properties and listeners to null.
    @Override
    public void dispose() {
        observable.removeListener(obvListener);
        if (target != null && tgtListener != null) target.getObservable().removeListener(tgtListener);
        observable = null;
        target = null;
        targetUpdater = null;
        sourceUpdater = null;
        obvListener = null;
        tgtListener = null;
    }

    //================================================================================
    // Getters/Setters
    //================================================================================

    /// @return the [MappedUpdater] responsible for mapping the source's values to a compatible type and then
    /// update the target
    public MappedUpdater<S, T> getTargetUpdater() {
        return targetUpdater;
    }

    /// Sets the [MappedUpdater] for the target.
    public MappingSource<S, T> setTargetUpdater(MappedUpdater<S, T> targetUpdater) {
        this.targetUpdater = targetUpdater;
        return this;
    }

    /// Sets the [MappedUpdater] for the target.
    public MappingSource<S, T> setTargetUpdater(Mapper<S, T> mapper, Updater<T> updater) {
        this.targetUpdater = new MappedUpdater<>(mapper, updater);
        return this;
    }

    /// @return the [MappedUpdater] responsible for mapping the target's values to a compatible type and then
    /// update the source
    public MappedUpdater<T, S> getSourceUpdater() {
        return sourceUpdater;
    }

    /// Sets the [MappedUpdater] for the source.
    public MappingSource<S, T> setSourceUpdater(MappedUpdater<T, S> sourceUpdater) {
        this.sourceUpdater = sourceUpdater;
        return this;
    }

    /// Sets the [MappedUpdater] for the source.
    public MappingSource<S, T> setSourceUpdater(Mapper<T, S> mapper, Updater<S> updater) {
        this.sourceUpdater = new MappedUpdater<>(mapper, updater);
        return this;
    }

    //================================================================================
    // Builder
    //================================================================================
    public static class Builder<S, T> {
        private final MappingSource<S, T> source = new MappingSource<>();

        public Builder<S, T> observable(ObservableValue<? extends S> observable) {
            source.observable = observable;
            return this;
        }

        public Builder<S, T> targetUpdater(MappedUpdater<S, T> targetUpdater) {
            source.targetUpdater = targetUpdater;
            return this;
        }

        public Builder<S, T> sourceUpdater(MappedUpdater<T, S> sourceUpdater) {
            source.sourceUpdater = sourceUpdater;
            return this;
        }

        public MappingSource<S, T> get() {
            if (source.observable == null) throw new NullPointerException("Source is invalid as observable is null");
            return source;
        }
    }
}

