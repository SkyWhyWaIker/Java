package org.ATM.transactions;

import java.math.BigDecimal;

/**
 * Класс, описывающий транзакции в системе банкомата.
 */
public record Transaction(org.ATM.transactions.Transaction.TransactionType type, BigDecimal amount) {
    public enum TransactionType {
        DEPOSIT,
        WITHDRAWAL
    }

    /**
     * Создает новую транзакцию.
     *
     * @param type   тип транзакции
     * @param amount сумма транзакции
     */
    public Transaction(TransactionType type, BigDecimal amount) {
        this.type = type;
        this.amount = amount.add(amount);
    }

    @Override
    public String toString() {
        return type + " | " + amount;
    }
}
