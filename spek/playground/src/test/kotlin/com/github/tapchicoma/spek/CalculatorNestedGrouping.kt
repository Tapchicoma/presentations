package com.github.tapchicoma.spek

import com.github.taphcicoma.spek.Calculator
import org.amshove.kluent.`should be equal to`
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

class CalculatorNestedGrouping : Spek({
    describe("A calculator") {
        val calculator by memoized { Calculator() }
        context("With initial value 2") {
            beforeEach { calculator.setValue(2) }
            it("has 2 as current value ") {
                calculator.currentValue() `should be equal to` 2
            }
            describe("adding") {
                val addTestData = mapOf(
                    2 to 4,
                    10 to 12
                )
                addTestData.forEach { add, result ->
                    it("$add should return $result") {
                        calculator.add(add) `should be equal to` result
                    }
                }
            }
            describe("subtracting") {
                val subtractTestData = mapOf(
                    2 to 0,
                    -10 to 12,
                    0 to 2
                )
                subtractTestData.forEach { sub, result ->
                    it("$sub should return $result") {
                        calculator.subtract(sub) `should be equal to` result
                    }
                }
            }
        }
    }
})
