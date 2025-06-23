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

package unit;

import java.util.List;

import io.github.palexdev.mfxcore.collections.ObservableCircularQueue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import static org.junit.jupiter.api.Assertions.assertEquals;

@TestInstance(TestInstance.Lifecycle.PER_METHOD)
public class ObservableCircularQueueTest {
    private ObservableCircularQueue<Integer> queue;

    @BeforeEach
    void setup() {
        queue = new ObservableCircularQueue<>(5);
    }

    @Test
    void testAdd() {
        for (int i = 0; i < 10; i++) {
            queue.add(i);
        }
        assertQueue();
    }

    @Test
    void testAddAll() {
        Integer[] vals = new Integer[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9};
        queue.addAll(vals);
        assertQueue();
    }

    @Test
    void testAddAll2() {
        List<Integer> vals = List.of(0, 1, 2, 3, 4, 5, 6, 7, 8, 9);
        queue.addAll(vals);
        assertQueue();
    }

    @Test
    void testAddAll3() {
        List<Integer> vals = List.of(0, 1, 2, 3, 4, 5, 6, 7, 8, 9);
        queue.addAll(0, vals);
        assertQueue();
    }

    @Test
    void testSetAll() {
        Integer[] vals = new Integer[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9};
        queue.setAll(vals);
        assertQueue();
    }

    @Test
    void testSetAll2() {
        List<Integer> vals = List.of(0, 1, 2, 3, 4, 5, 6, 7, 8, 9);
        queue.setAll(vals);
        assertQueue();
    }

    void assertQueue() {
        assertEquals(5, queue.size());
        for (int i = 5; i < 10; i++) {
            assertEquals(i, queue.get(i - 5));
        }
    }
}
