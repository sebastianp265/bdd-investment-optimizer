package com.github.sebastianp265.domain.model.investment

import com.github.sebastianp265.domain.value.Money
import com.github.sebastianp265.domain.snapshot.AccountSnapshot

open class CashAccount : Investment {
    protected var balance: Money = Money.ZERO

    fun deposit(amount: Money) {
        require(amount > Money.ZERO)
        balance += amount
    }

    fun withdraw(amount: Money) {
        require(amount > Money.ZERO)
        require(balance >= amount)
        balance -= amount
    }

    override fun snapshot() = AccountSnapshot(
        balance = this.balance,
    )

}
