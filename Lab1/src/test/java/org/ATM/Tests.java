package org.ATM;

import org.ATM.atms.ATM;
import org.ATM.exceptions.InvalidAmountException;

import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;

import static org.junit.Assert.*;

public class Tests {

    @Before
    public void setUp() {
        ATM atm;
        atm = new ATM();
    }

    @Test
    public void testCheckBalanceInitial() {
        assertEquals(BigDecimal.ZERO, ATM.checkBalance());
    }

    @Test
    public void testDeposit() throws InvalidAmountException {
        BigDecimal amount = BigDecimal.valueOf(100);
        ATM.deposit(amount);
        assertEquals(BigDecimal.valueOf(200), ATM.checkBalance());
    }

    @Test
    public void testDepositNegativeAmount() {
        BigDecimal negativeAmount = new BigDecimal("-100.00");
        InvalidAmountException exception = assertThrows(InvalidAmountException.class, () -> {
            ATM.deposit(negativeAmount);
        });
        assertEquals("Сумма пополнения должна быть положительной", exception.getMessage());
        assertEquals(BigDecimal.ZERO, ATM.checkBalance());
    }

    @Test
    public void testWithdrawNegativeAmount() {
        BigDecimal negativeAmount = new BigDecimal("-100.00");
        InvalidAmountException exception = assertThrows(InvalidAmountException.class, () -> {
            ATM.withdraw(negativeAmount);
        });
        assertEquals("Сумма снятия должна быть положительной", exception.getMessage());
        assertEquals(BigDecimal.ZERO, ATM.checkBalance());
    }
}