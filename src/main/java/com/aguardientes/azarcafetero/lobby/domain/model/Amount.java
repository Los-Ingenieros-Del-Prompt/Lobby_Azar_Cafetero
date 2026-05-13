package com.aguardientes.azarcafetero.lobby.domain.model;

import java.math.BigDecimal;

public class Amount {

    private final BigDecimal value;

    public Amount(BigDecimal value) {
        if (value == null)
            throw new IllegalArgumentException("El monto no puede ser nulo");
        if (value.compareTo(BigDecimal.ZERO) < 0)
            throw new IllegalArgumentException("El monto no puede ser negativo");
        this.value = value;
    }

    public static Amount of(long value) {
        return new Amount(BigDecimal.valueOf(value));
    }

    public Amount add(Amount other) {
        return new Amount(this.value.add(other.value));
    }

    public Amount subtract(Amount other) {
        BigDecimal result = this.value.subtract(other.value);
        if (result.compareTo(BigDecimal.ZERO) < 0)
            throw new IllegalStateException("Saldo insuficiente");
        return new Amount(result);
    }

    public boolean isGreaterThanOrEqual(Amount other) {
        return this.value.compareTo(other.value) >= 0;
    }

    public BigDecimal getValue() { return value; }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Amount)) return false;
        return value.compareTo(((Amount) o).value) == 0;
    }

    @Override
    public int hashCode() { return value.stripTrailingZeros().hashCode(); }

    @Override
    public String toString() { return value.toPlainString(); }
}