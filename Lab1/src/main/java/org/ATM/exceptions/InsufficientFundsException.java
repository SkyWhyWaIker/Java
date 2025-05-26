package org.ATM.exceptions;

/**
 * Данное исключение выбрасывается в ситуациях, когда на банковском счете недостаточно средств.
 */
public class InsufficientFundsException extends RuntimeException {
    public InsufficientFundsException(String message) {
        super(message);
    }
}
