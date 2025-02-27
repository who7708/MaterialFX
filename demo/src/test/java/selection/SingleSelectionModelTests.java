package selection;

import io.github.palexdev.materialfx.bindings.BiBindingManager;
import io.github.palexdev.materialfx.bindings.BindingManager;
import io.github.palexdev.materialfx.demo.model.Person;
import io.github.palexdev.materialfx.selection.SelectionModel;
import io.github.palexdev.materialfx.selection.base.ISelectionModel;
import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.framework.junit5.ApplicationExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(ApplicationExtension.class)
public class SingleSelectionModelTests {
	private final ListProperty<Person> people1 = new SimpleListProperty<>(
			FXCollections.observableArrayList(
					new Person("Jack"),
					new Person("Mark"),
					new Person("Linda"),
					new Person("Marty"),
					new Person("Lily"),
					new Person("Sam"))
	);

	private final ListProperty<Person> people2 = new SimpleListProperty<>(
			FXCollections.observableArrayList(
					new Person("Mark"),
					new Person("Roberto"),
					new Person("Alex"),
					new Person("Samantha"),
					new Person("Elyse"),
					new Person("Mark"),
					new Person("Sam"),
					new Person("Jennifer"),
					new Person("Alex"),
					new Person("Rocky"),
					new Person("Phil"))
	);

	private final ISelectionModel<Person> model1 = new SelectionModel<>(people1);
	private final ISelectionModel<Person> model2 = new SelectionModel<>(people2);

	@AfterEach
	public void checkManagers() {
		BindingManager.instance().dispose();
		BiBindingManager.instance().dispose();
	}

	@AfterAll
	static void dispose() {
		assertEquals(0, BindingManager.instance().size());
		assertEquals(0, BiBindingManager.instance().size());
	}

	@Test
	public void testIndexSelection1() {
		model1.selectIndex(0);
		assertEquals(0, model1.getSelectedEntry().getKey());
		assertEquals("Jack", model1.getSelectedItem().getName());
	}

	@Test
	public void testIndexSelection2() {
		assertThrows(IndexOutOfBoundsException.class, () -> model1.selectIndex(10));
		assertNull(model1.getSelectedEntry());
	}

	@Test
	public void testIndexSelection3() {
		assertThrows(IndexOutOfBoundsException.class, () -> model1.selectIndex(-1));
		assertNull(model1.getSelectedEntry());
	}

	@Test
	public void testItemSelection1() {
		model1.selectItem(new Person("Mark"));
		assertEquals(1, model1.getSelectedEntry().getKey());
		assertEquals("Mark", model1.getSelectedItem().getName());
	}

	@Test
	public void testItemSelection2() {
		assertThrows(IllegalArgumentException.class, () -> model1.selectItem(new Person("Unexisting")));
	}

	@Test
	public void testClearSelection() {
		model1.selectIndex(0);
		model1.clearSelection();
		assertNull(model1.getSelectedEntry());
		assertNull(model1.getSelectedItem());
	}
}
