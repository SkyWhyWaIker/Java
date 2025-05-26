package org.ATM.atms;

import org.ATM.account.Accounts;
import org.ATM.exceptions.InsufficientFundsException;
import org.ATM.exceptions.InvalidAmountException;
import org.ATM.transactions.Transaction;

import java.math.BigDecimal;
import java.util.List;

/**
 * Класс который описывает сиситему банкомата.
 */
public class ATM {
    private static final Accounts account = new Accounts();

    /**
     * Проверяет текущий баланс.
     */
    public static BigDecimal checkBalance() {
        return account.getBalance();
    }

    /**
     * Пополнение счета на оперделённую сумму.
     *
     * @param amount сумма, на которую нужно пополнить.
     * @throws InvalidAmountException когда указывается некорректная сумма для выполнения операции.
     */
    public static void deposit(BigDecimal amount) throws InvalidAmountException {
        account.deposit(amount);
    }

    /**
     * Снятие средств со счета.
     *
     * @param amount сумма, которую нужно снять.
     * @throws InsufficientFundsException на банковском счете недостаточно средств.
     * @throws InvalidAmountException указывается некорректная сумма для выполнения операции.
     */
    public static void withdraw(BigDecimal amount) throws InsufficientFundsException, InvalidAmountException {
        account.withdraw(amount);
    }

    /**
     * Посмотр всей истории операций
     */
    public static List<Transaction> getHistory() {
        return account.getTransactionHistory();
    }
}
