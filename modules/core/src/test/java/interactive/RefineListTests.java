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

package interactive;

import java.util.Comparator;

import io.github.palexdev.mfxcore.collections.RefineList;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.framework.junit5.ApplicationExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(ApplicationExtension.class)
public class RefineListTests {
    private final ObservableList<String> source = FXCollections.observableArrayList("A", "B", "C", "D", "E");

    @Test
    public void sortTest1() {
        RefineList<String> transformed = new RefineList<>(source);
        transformed.setComparator(Comparator.reverseOrder());

        assertEquals("A", transformed.getView().get(4));
        assertEquals(0, transformed.getView().indexOf("E"));
        assertEquals(4, transformed.viewToSource(0));
        assertEquals(4, transformed.sourceToView(0));
    }

    @Test
    public void sortAndFilterTest1() {
        RefineList<String> transformed = new RefineList<>(source);
        transformed.setComparator(Comparator.reverseOrder());
        transformed.setPredicate(s -> s.equals("A") || s.equals("C") || s.equals("E"));

        assertThrows(IndexOutOfBoundsException.class, () -> transformed.getView().get(4));
        assertEquals("C", transformed.getView().get(1));
        assertEquals(0, transformed.getView().indexOf("E"));
        assertEquals(2, transformed.viewToSource(1));
        assertTrue(transformed.sourceToView(1) < 0);
    }

    @Test
    public void testJavaFX1() {
        SortedList<String> sorted = new SortedList<>(source);
        sorted.setComparator(Comparator.reverseOrder());

        assertEquals("A", sorted.get(4));
        assertEquals(0, sorted.indexOf("E"));
        assertEquals(4, sorted.getSourceIndex(0));
        assertEquals(4, sorted.getViewIndex(0));
    }

    @Test
    public void testJavaFX2() {
        SortedList<String> sorted = new SortedList<>(source);
        sorted.setComparator(Comparator.reverseOrder());

        FilteredList<String> filtered = new FilteredList<>(sorted);
        filtered.setPredicate(s -> s.equals("A") || s.equals("C") || s.equals("E"));

        assertThrows(IndexOutOfBoundsException.class, () -> filtered.get(4));
        assertEquals("C", filtered.get(1));
        assertEquals(0, filtered.indexOf("E"));
        assertEquals(2, filtered.getSourceIndex(1));
        assertTrue(filtered.getViewIndex(1) < 0);
    }
}
