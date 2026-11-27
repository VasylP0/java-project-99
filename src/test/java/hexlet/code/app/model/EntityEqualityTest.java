package hexlet.code.app.model;

import static org.junit.jupiter.api.Assertions.*;

import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class EntityEqualityTest {

    @Test
    void unsavedEntitiesAreNotEqual() {
        assertNotEquals(new Label(), new Label());
        assertNotEquals(new Task(), new Task());
        assertNotEquals(new TaskStatus(), new TaskStatus());
        assertNotEquals(new User(), new User());
    }

    @Test
    void labelsWithSameIdAreEqual() {
        Label first = new Label();
        first.setId(1L);
        first.setName("feature");

        Label second = new Label();
        second.setId(1L);
        second.setName("updated");

        assertEquals(first, second);
        assertEquals(second, first);
        assertEquals(first.hashCode(), second.hashCode());
    }

    @Test
    void labelsWithDifferentIdsAreNotEqual() {
        Label first = new Label();
        first.setId(1L);

        Label second = new Label();
        second.setId(2L);

        assertNotEquals(first, second);
    }

    @Test
    void hashCodeRemainsStableAfterIdAssignment() {
        Label label = new Label();

        Set<Label> labels = new HashSet<>();
        labels.add(label);

        int originalHashCode = label.hashCode();

        label.setId(1L);

        assertEquals(originalHashCode, label.hashCode());
        assertTrue(labels.contains(label));
    }

    @Test
    void hashSetPreventsDuplicateLabels() {
        Label first = new Label();
        first.setId(1L);

        Label second = new Label();
        second.setId(1L);

        Set<Label> labels = new HashSet<>();
        labels.add(first);
        labels.add(second);

        assertEquals(1, labels.size());
    }

    @Test
    void tasksWithSameIdAreEqual() {
        Task first = new Task();
        first.setId(1L);

        Task second = new Task();
        second.setId(1L);

        assertEquals(first, second);
        assertEquals(first.hashCode(), second.hashCode());
    }

    @Test
    void taskStatusesWithSameIdAreEqual() {
        TaskStatus first = new TaskStatus();
        first.setId(1L);

        TaskStatus second = new TaskStatus();
        second.setId(1L);

        assertEquals(first, second);
        assertEquals(first.hashCode(), second.hashCode());
    }

    @Test
    void usersWithSameIdAreEqual() {
        User first = new User();
        first.setId(1L);

        User second = new User();
        second.setId(1L);

        assertEquals(first, second);
        assertEquals(first.hashCode(), second.hashCode());
    }

    @Test
    void differentEntityTypesAreNotEqual() {
        Label label = new Label();
        label.setId(1L);

        Task task = new Task();
        task.setId(1L);

        assertNotEquals(label, task);
    }
}
