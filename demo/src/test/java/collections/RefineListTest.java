package collections;

import java.util.Comparator;

import io.github.palexdev.materialfx.collections.RefineList;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.framework.junit5.ApplicationExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(ApplicationExtension.class)
public class RefineListTest {
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
