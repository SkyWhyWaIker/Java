package Utils;

import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import org.testcontainers.containers.PostgreSQLContainer;

import java.util.HashMap;
import java.util.Map;

public class Util {
    private static final String PERSISTENCE_UNIT_NAME = "pet_persistence_unit";
    private static EntityManagerFactory entityManagerFactory;
    private static PostgreSQLContainer<?> postgres;

    private Util() { }

    public static EntityManagerFactory getEntityManagerFactory() {
        if (entityManagerFactory == null) {
            // Инициализация Testcontainers
            postgres = new PostgreSQLContainer<>("postgres:15")
                    .withDatabaseName("petdb")
                    .withUsername("postgres")
                    .withPassword("password")
                    .withReuse(true);
            postgres.start();
            System.out.println("JDBC URL: " + postgres.getJdbcUrl());
            System.out.println("Container running: " + postgres.isRunning());

            // Настройка свойств Hibernate для Testcontainers
            Map<String, String> properties = new HashMap<>();
            properties.put("hibernate.connection.url", postgres.getJdbcUrl());
            properties.put("hibernate.connection.username", postgres.getUsername());
            properties.put("hibernate.connection.password", postgres.getPassword());
            properties.put("hibernate.dialect", "org.hibernate.dialect.PostgreSQLDialect");
            properties.put("hibernate.hbm2ddl.auto", "create-drop");
            properties.put("hibernate.show_sql", "true");
            properties.put("hibernate.format_sql", "true");

            // Создание EntityManagerFactory с динамическими свойствами
            entityManagerFactory = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME, properties);
        }
        return entityManagerFactory;
    }

    public static void close() {
        if (entityManagerFactory != null && entityManagerFactory.isOpen()) {
            entityManagerFactory.close();
            entityManagerFactory = null;
        }
        if (postgres != null && postgres.isRunning()) {
            postgres.stop();
        }
    }
}