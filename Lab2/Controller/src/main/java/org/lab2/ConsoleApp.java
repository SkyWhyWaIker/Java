package org.lab2;

import org.lab2.Pets.Pet;
import org.lab2.Pets.Color;
import org.lab2.Owners.Owner;


import java.time.LocalDate;
import java.util.Scanner;

public class ConsoleApp {
    private final PetController petController;
    private final OwnerController ownerController;
    private final Scanner scanner;

    public ConsoleApp(PetDAO petDAO, OwnerDAO ownerDAO) {
        this.petController = new PetController(new PetServiceImpl(petDAO));
        this.ownerController = new OwnerController(new OwnerServiceImpl(ownerDAO));
        this.scanner = new Scanner(System.in);
    }

    public void run() {
        while (true) {
            System.out.println("1. Создать питомца");
            System.out.println("2. Создать хозяина");
            System.out.println("3. Показать всех питомцев");
            System.out.println("4. Удалить питомца по ID");
            System.out.println("5. Добавить друга питомцу");
            System.out.println("6. Выход");
            System.out.print("Выберите опцию: ");
            int choice = scanner.nextInt();
            scanner.nextLine();

            try {
                switch (choice) {
                    case 1 -> createPet();
                    case 2 -> createOwner();
                    case 3 -> showAllPets();
                    case 4 -> deletePet();
                    case 5 -> addFriend();
                    case 6 -> {
                        System.out.println("Выход...");
                        return;
                    }
                    default -> System.out.println("Неверный выбор");
                }
            } catch (IllegalArgumentException e) {
                System.out.println("Ошибка: " + e.getMessage());
            }
        }
    }

    private void createPet() {
        System.out.print("Имя питомца: ");
        String name = scanner.nextLine();
        System.out.print("Дата рождения (ГГГГ-ММ-ДД): ");
        LocalDate birthDate = LocalDate.parse(scanner.nextLine());
        System.out.print("Порода: ");
        String breed = scanner.nextLine();
        System.out.print("Цвет (BLACK, WHITE, GRAY, ORANGE, BROWN): ");
        Color color = Color.valueOf(scanner.nextLine().toUpperCase());
        System.out.print("ID хозяина (0, если нет): ");
        long ownerId = scanner.nextLong();
        scanner.nextLine();

        Pet pet = new Pet();
        pet.setName(name);
        pet.setBirthDate(birthDate);
        pet.setBreed(breed);
        pet.setColor(color);
        if (ownerId > 0) {
            Owner owner = ownerController.getOwnerById(ownerId);
            if (owner == null) {
                throw new IllegalArgumentException("Хозяин с ID " + ownerId + " не найден");
            }
            pet.setOwner(owner);
        }

        petController.createPet(pet);
        System.out.println("Питомец создан!");
    }

    private void createOwner() {
        System.out.print("Имя хозяина: ");
        String name = scanner.nextLine();
        System.out.print("Дата рождения (ГГГГ-ММ-ДД): ");
        LocalDate birthDate = LocalDate.parse(scanner.nextLine());

        Owner owner = new Owner();
        owner.setName(name);
        owner.setBirthDate(birthDate);

        ownerController.createOwner(owner);
        System.out.println("Хозяин создан!");
    }

    private void showAllPets() {
        petController.getAllPets().forEach(pet ->
                System.out.println("ID: " + pet.getId() + ", Имя: " + pet.getName() +
                        ", Порода: " + pet.getBreed() + ", Цвет: " + pet.getColor()));
    }

    private void deletePet() {
        System.out.print("Введите ID питомца для удаления: ");
        long id = scanner.nextLong();
        scanner.nextLine();
        petController.deletePetById(id);
        System.out.println("Питомец удален!");
    }

    private void addFriend() {
        System.out.print("Введите ID питомца: ");
        long petId = scanner.nextLong();
        System.out.print("Введите ID друга: ");
        long friendId = scanner.nextLong();
        scanner.nextLine();

        Pet pet = petController.getPetById(petId);
        Pet friend = petController.getPetById(friendId);
        if (pet == null || friend == null) {
            throw new IllegalArgumentException("Один из питомцев не найден");
        }
        pet.getFriends().add(friend);
        petController.updatePet(pet);
        System.out.println("Друг добавлен!");
    }

    public static void main(String[] args) {
        ConsoleApp app = new ConsoleApp(new PetDAOImpl(), new OwnerDAOImpl());
        app.run();
    }
}