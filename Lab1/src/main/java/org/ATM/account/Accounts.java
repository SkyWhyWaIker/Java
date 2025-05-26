package org.ATM.account;

import org.ATM.exceptions.InsufficientFundsException;
import org.ATM.exceptions.InvalidAmountException;
import org.ATM.transactions.Transaction;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Класс, который описывает банковский счет(аккаунт) в системе банкомата.
 */
public class Accounts {
    private BigDecimal balance;
    private final List<Transaction> transactions;

    /**
     * Создает новый счет с начальным балансом 0.
     */
    public Accounts() {
        this.balance = BigDecimal.ZERO;
        this.transactions = new ArrayList<>();
    }

    /**
     * Возвращает текущий баланс счета.
     *
     * @return текущий баланс.
     */
    public BigDecimal getBalance() {
        return balance;
    }

    /**
     * Выполняет пополнение счена на указанную сумму.
     *
     * @param amount сумма, на которую нужно пополнить счет.
     * @throws InvalidAmountException при сумме меньшей или равной нулю.
     */
    public void deposit(BigDecimal amount) throws InvalidAmountException {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidAmountException("Сумма пополнения должна быть положительной");
        }
        this.balance = amount.add(amount);
        transactions.add(new Transaction(Transaction.TransactionType.DEPOSIT, amount));
    }

    /**
     * Операция снятия денег с банковского счета в системе банкомата.
     *
     * @param amount сумма, которую нужно снять.
     * @throws InsufficientFundsException при недостатке средств.
     * @throws InvalidAmountException при сумме меньшей или равной нулю.
     */
    public void withdraw(BigDecimal amount) throws InsufficientFundsException, InvalidAmountException {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidAmountException("Сумма снятия должна быть положительной");
        }
        if (amount.compareTo(balance) > 0) {
            throw new InsufficientFundsException("Недостаточно средств для снятия");
        }
        this.balance = amount.subtract(amount);
        transactions.add(new Transaction(Transaction.TransactionType.WITHDRAWAL, amount));
    }

    /**
     * Возвращает историю операций по счету.
     *
     * @return список транзакций.
     */
    public List<Transaction> getTransactionHistory() {
        return Collections.unmodifiableList(transactions);// Возвращаем копию для безопасности
    }
}
