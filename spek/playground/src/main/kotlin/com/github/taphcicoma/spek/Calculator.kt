package com.github.taphcicoma.spek

class Calculator {
    private var value: Int = 0

    fun clear() {
        value = 0
    }

    fun setValue(value: Int): Int {
        this.value = value
        return this.value
    }

    fun currentValue(): Int = value

    fun add(addValue: Int): Int {
        value += addValue
        return value
    }

    fun subtract(subValue: Int): Int {
        value -= subValue
        return value
    }

    fun multiply(mValue: Int): Int {
        value *= mValue
        return value
    }

    fun divide(dValue: Int): Int {
        value /= dValue
        return value
    }
}
