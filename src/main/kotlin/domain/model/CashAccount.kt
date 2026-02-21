package com.github.sebastianp265.domain.model

data class AccountId(val accountId: String)

open class CashAccount(
    val id: AccountId,
) {
    protected var balance: Money = Money.ZERO

    fun deposit(amount: Money) {
        require(amount >= Money.ZERO)
        balance += amount
    }

    fun withdraw(amount: Money) {
        require(amount >= Money.ZERO)
        require(balance >= amount)
        balance -= amount
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is CashAccount) return false

        return this.id == other.id
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }

    override fun toString(): String {
        return "Account(id=$id, balance=$balance)"
    }

}



