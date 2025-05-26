package org.lab2;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.lab2.Owners.Owner;
import org.lab2.Pets.Pet;
import org.testcontainers.containers.PostgreSQLContainer;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class OwnerDAOTest {

    private static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:17")
            .withDatabaseName("pets")
            .withUsername("postgres")
            .withPassword("Pofigict2005")
            .withReuse(true);

    private static SessionFactory sessionFactory;
    private static OwnerDAO ownerDao;

    @BeforeAll
    public static void beforeAll() {
        postgres.start();
        System.out.println("PostgreSQL container running: " + postgres.isRunning());
        System.out.println("JDBC URL: " + postgres.getJdbcUrl());

        Configuration config = new Configuration()
                .setProperty("hibernate.connection.url", postgres.getJdbcUrl())
                .setProperty("hibernate.connection.username", postgres.getUsername())
                .setProperty("hibernate.connection.password", postgres.getPassword())
                .setProperty("hibernate.dialect", "org.hibernate.dialect.PostgreSQLDialect")
                .setProperty("hibernate.hbm2ddl.auto", "create-drop")
                .addAnnotatedClass(Pet.class)
                .addAnnotatedClass(Owner.class);
        sessionFactory = config.buildSessionFactory();

        ownerDao = new OwnerDAOImpl();
    }

    @AfterAll
    public static void afterAll() {
        // Закрываем SessionFactory
        if (sessionFactory != null) {
            sessionFactory.close();
        }
        postgres.stop();
    }

    @Test
    void testAddOwner() {
        Owner owner = new Owner("Пётр", LocalDate.of(2000, 1, 1));

        ownerDao.save(owner);

        Owner savedOwner = ownerDao.getById(owner.getId());
        assertNotNull(savedOwner);
        assertEquals("Пётр", savedOwner.getName());
        assertEquals(LocalDate.of(2000, 1, 1), savedOwner.getBirthDate());
    }

    @Test
    void testAddOwner_NullOwner_ThrowsException() {
        assertThrows(RuntimeException.class, () -> ownerDao.save(null));
    }

    @Test
    void testAddOwner_EmptyName_ThrowsException() {
        Owner owner = new Owner("", LocalDate.of(2000, 1, 1));
        assertThrows(IllegalArgumentException.class, () -> ownerDao.save(owner));
    }

    @Test
    void testFindAllOwners() {
        Owner owner1 = new Owner("Пётр", LocalDate.of(2000, 1, 1));
        Owner owner2 = new Owner("Анна", LocalDate.of(1995, 5, 15));
        ownerDao.save(owner1);
        ownerDao.save(owner2);

        List<Owner> result = ownerDao.getAll();

        assertNotEquals(8, result.size());
        assertTrue(result.stream().anyMatch(o -> o.getName().equals("Пётр")));
        assertTrue(result.stream().anyMatch(o -> o.getName().equals("Анна")));
    }

    @Test
    void testGetOwnerById() {
        Owner owner = new Owner("Пётр", LocalDate.of(2000, 1, 1));
        ownerDao.save(owner);

        Owner result = ownerDao.getById(owner.getId());

        assertNotNull(result);
        assertEquals("Пётр", result.getName());
    }

    @Test
    void testGetOwnerById_InvalidId_ThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> ownerDao.getById(0L));
    }

    @Test
    void testUpdateOwner() {
        Owner owner = new Owner("Пётр", LocalDate.of(2000, 1, 1));
        ownerDao.save(owner);
        owner.setName("Иван");

        ownerDao.update(owner);

        Owner updatedOwner = ownerDao.getById(owner.getId());
        assertEquals("Иван", updatedOwner.getName());
    }

    @Test
    void testUpdateOwner_NullOwner_ThrowsException() {
        assertThrows(RuntimeException.class, () -> ownerDao.update(null));
    }

    @Test
    void testUpdateOwner_NoId_ThrowsException() {
        Owner owner = new Owner("Пётр", LocalDate.of(2000, 1, 1));
        assertThrows(IllegalArgumentException.class, () -> ownerDao.update(owner));
    }

    @Test
    void testDeleteOwner() {
        Owner owner = new Owner("Пётр", LocalDate.of(2000, 1, 1));
        ownerDao.save(owner);
        Long id = owner.getId();

        ownerDao.deleteById(id);

        assertNull(ownerDao.getById(id));
    }

    @Test
    void testDeleteOwner_InvalidId_ThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> ownerDao.deleteById(0L));
    }
}