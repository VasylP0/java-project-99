package hexlet.code.app.model;

import jakarta.persistence.EntityManager;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class EntityPersistenceTest {

    @Autowired
    private EntityManager entityManager;

    private Label createLabel(String name) {
        Label label = new Label();
        label.setName(name);
        return label;
    }

    @Test
    void hashCodeRemainsStableAfterPersistence() {
        Label label = createLabel("persistence-hash");

        int originalHashCode = label.hashCode();

        Set<Label> labels = new HashSet<>();
        labels.add(label);

        assertNull(label.getId());

        entityManager.persist(label);
        entityManager.flush();

        assertNotNull(label.getId());
        assertEquals(originalHashCode, label.hashCode());
        assertTrue(labels.contains(label));
    }

    @Test
    void detachedAndReloadedEntitiesAreEqual() {
        Label original = createLabel("persistence-reload");

        entityManager.persist(original);
        entityManager.flush();

        Long id = original.getId();

        entityManager.clear();

        Label reloaded = entityManager.find(Label.class, id);

        assertNotNull(reloaded);
        assertNotSame(original, reloaded);
        assertEquals(original, reloaded);
        assertEquals(reloaded, original);
        assertEquals(original.hashCode(), reloaded.hashCode());
    }

    @Test
    void entityAndHibernateProxyAreEqual() {
        Label original = createLabel("persistence-proxy");

        entityManager.persist(original);
        entityManager.flush();

        Long id = original.getId();

        entityManager.clear();

        Label reference = entityManager.getReference(Label.class, id);

        assertNotSame(original, reference);
        assertEquals(original, reference);
        assertEquals(reference, original);
        assertEquals(original.hashCode(), reference.hashCode());
    }

    @Test
    void hashSetDoesNotDuplicatePersistedEntity() {
        Label original = createLabel("persistence-set");

        entityManager.persist(original);
        entityManager.flush();

        Long id = original.getId();

        Set<Label> labels = new HashSet<>();
        labels.add(original);

        entityManager.clear();

        Label reloaded = entityManager.find(Label.class, id);

        labels.add(reloaded);

        assertEquals(1, labels.size());
        assertTrue(labels.contains(original));
        assertTrue(labels.contains(reloaded));
    }

    @Test
    void differentPersistedEntitiesAreNotEqual() {
        Label first = createLabel("persistence-first");
        Label second = createLabel("persistence-second");

        entityManager.persist(first);
        entityManager.persist(second);
        entityManager.flush();

        assertNotNull(first.getId());
        assertNotNull(second.getId());

        assertNotEquals(first.getId(), second.getId());
        assertNotEquals(first, second);

        Set<Label> labels = new HashSet<>();
        labels.add(first);
        labels.add(second);

        assertEquals(2, labels.size());
    }
}