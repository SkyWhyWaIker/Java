package org.lab2;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.lab2.Owners.Owner;
import org.lab2.Pets.Color;
import org.lab2.Pets.Pet;
import org.testcontainers.containers.PostgreSQLContainer;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class PetDAOTest {

    private static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:17")
            .withDatabaseName("pets")
            .withUsername("postgres")
            .withPassword("Pofigict2005")
            .withReuse(true);

    private static SessionFactory sessionFactory;
    private static PetDAO petDao;
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

        petDao = new PetDAOImpl();
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
    void testAddPet() {
        Owner owner = new Owner();
        owner.setName("Пётр");
        owner.setBirthDate(LocalDate.of(2000, 1, 1));
        ownerDao.save(owner);

        Pet pet = new Pet("Барсик", LocalDate.of(2020, 3, 10), "Перс", Color.ORANGE);
        pet.setOwner(owner);

        petDao.save(pet);

        Pet savedPet = petDao.getById(pet.getId());
        assertNotNull(savedPet);
        assertEquals("Барсик", savedPet.getName());
        assertEquals("Перс", savedPet.getBreed());
        assertEquals(Color.ORANGE, savedPet.getColor());
        assertEquals("Пётр", savedPet.getOwner().getName());
    }

    @Test
    void testAddPet_NullPet_ThrowsException() {
        assertThrows(RuntimeException.class, () -> petDao.save(null));
    }

    @Test
    void testAddPet_EmptyName_ThrowsException() {
        Pet pet = new Pet("", LocalDate.of(2020, 3, 10), "Перс", Color.ORANGE);
        assertThrows(IllegalArgumentException.class, () -> petDao.save(pet));
    }

    @Test
    void testFindAllPets() {
        Pet pet1 = new Pet("Барсик", LocalDate.of(2020, 3, 10), "Перс", Color.ORANGE);
        Pet pet2 = new Pet("Мурка", LocalDate.of(2019, 6, 20), "Сиамская", Color.BLACK);
        petDao.save(pet1);
        petDao.save(pet2);

        List<Pet> result = petDao.getAll();

        assertNotEquals(2, result.size());
        assertTrue(result.stream().anyMatch(p -> p.getName().equals("Барсик")));
        assertTrue(result.stream().anyMatch(p -> p.getName().equals("Мурка")));
    }

    @Test
    void testGetPetById() {
        Pet pet = new Pet("Барсик", LocalDate.of(2020, 3, 10), "Перс", Color.ORANGE);
        petDao.save(pet);

        Pet result = petDao.getById(pet.getId());

        assertNotNull(result);
        assertEquals("Барсик", result.getName());
    }

    @Test
    void testGetPetById_InvalidId_ThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> petDao.getById(0L));
    }

    @Test
    void testUpdatePet() {
        Pet pet = new Pet("Барсик", LocalDate.of(2020, 3, 10), "Перс", Color.ORANGE);
        petDao.save(pet);
        pet.setName("Мурзик");

        petDao.update(pet);

        Pet updatedPet = petDao.getById(pet.getId());
        assertEquals("Мурзик", updatedPet.getName());
    }

    @Test
    void testUpdatePet_NullPet_ThrowsException() {
        assertThrows(RuntimeException.class, () -> petDao.update(null));
    }

    @Test
    void testUpdatePet_NoId_ThrowsException() {
        Pet pet = new Pet("Барсик", LocalDate.of(2020, 3, 10), "Перс", Color.ORANGE);
        assertThrows(IllegalArgumentException.class, () -> petDao.update(pet));
    }

    @Test
    void testDeletePet() {
        Pet pet = new Pet("Барсик", LocalDate.of(2020, 3, 10), "Перс", Color.ORANGE);
        petDao.save(pet);
        Long id = pet.getId();

        petDao.deleteById(id);

        assertNull(petDao.getById(id));
    }

    @Test
    void testDeletePet_InvalidId_ThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> petDao.deleteById(0L));
    }
}