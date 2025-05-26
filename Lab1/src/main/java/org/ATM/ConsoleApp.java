package org.ATM;

import org.ATM.atms.ATM;
import org.ATM.exceptions.InvalidAmountException;

import java.util.InputMismatchException;
import java.util.Scanner;
import java.math.BigDecimal;

/**
 * Консольное приложение, позволяющее работать с банкоматом
 */
public class ConsoleApp {
    private final Scanner scanner;

    public ConsoleApp() {
        this.scanner = new Scanner(System.in);
    }

    /**
     * Запускает консольное приложение.
     */
    public void run() {
        while (true) {
            displayMenu();
            int choice = getUserChoice();
            if (choice == 5) {
                System.out.println("Спасибо за использование банкомата. До свидания!");
                break;
            }
            processChoice(choice);
        }
        scanner.close();
    }

    private void displayMenu() {
        System.out.println("\n=== Меню банкомата ===");
        System.out.println("1. Проверить баланс");
        System.out.println("2. Пополнить счет");
        System.out.println("3. Снять наличные");
        System.out.println("4. Посмотреть историю операций");
        System.out.println("5. Выйти");
        System.out.print("Введите номер действия: ");
    }

    private int getUserChoice() {
        try {
            int choice = scanner.nextInt();
            return switch (choice) {
                case 1, 2, 3, 4, 5 -> {
                    scanner.nextLine();
                    yield choice;
                }
                default -> {
                    System.out.println("Пожалуйста, выберите действие от 1 до 5.");
                    scanner.nextLine();
                    yield getUserChoice();
                }
            };
        } catch (InputMismatchException e) {
            System.out.println("Ошибка: введите число от 1 до 5.");
            scanner.nextLine();
            return getUserChoice();
        }
    }

    private void processChoice(int choice) {
        switch (choice) {
            case 1 -> System.out.println("Текущий баланс: " + ATM.checkBalance() + " руб.");
            case 2 -> handleDeposit();
            case 3 -> handleWithdrawal();
            case 4 -> displayTransactionHistory();
            default -> System.out.println("Неизвестное действие.");
        }
    }

    private void handleDeposit() {
        System.out.print("Введите сумму для пополнения (положительное число): ");
        try {
            BigDecimal amount = scanner.nextBigDecimal();
            ATM.deposit(amount);
            String formattedAmount = String.format("%.2f", amount);
            System.out.printf("Пополнение на %s руб. выполнено.%n", formattedAmount);
            scanner.nextLine();
        } catch (InvalidAmountException e) {
            System.out.println("Ошибка: введите корректное число.");
            scanner.nextLine();
        }
    }

    private void handleWithdrawal() {
        System.out.print("Введите сумму для снятия (положительное число): ");
        try {
            double amountDouble = scanner.nextDouble();
            BigDecimal amount = BigDecimal.valueOf(amountDouble);
            ATM.withdraw(amount);
            String formattedAmount = String.format("%.2f", amount);
            System.out.printf("Снято %s руб.%n", formattedAmount);
            scanner.nextLine();
        } catch (InvalidAmountException e) {
            System.out.println("Ошибка: введите корректное число.");
            scanner.nextLine();
        }
    }

    private void displayTransactionHistory() {
        System.out.println("История операций:");
        if (ATM.getHistory().isEmpty()) {
            System.out.println("Операций пока нет.");
        } else {
            ATM.getHistory().forEach(System.out::println);
        }
    }

    public static void main(String[] args) {
        new ConsoleApp().run();
    }
}
