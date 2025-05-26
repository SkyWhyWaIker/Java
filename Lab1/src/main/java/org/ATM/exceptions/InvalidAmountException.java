package org.ATM.exceptions;

/**
 * Данное исключение выбрасывается в ситуациях, когда указывается некорректная сумма для выполнения операции.
 */
public class InvalidAmountException extends Exception{
    public InvalidAmountException(String message) {
        super(message);
    }
}
