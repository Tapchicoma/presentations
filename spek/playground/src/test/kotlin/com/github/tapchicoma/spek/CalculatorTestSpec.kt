package com.github.tapchicoma.spek

import com.github.taphcicoma.spek.Calculator
import org.amshove.kluent.`should be equal to`
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

class CalculatorTestSpec : Spek({
    describe("A calculator") {
        val calculator by memoized { Calculator() }
        it("returns set value") {
            calculator.setValue(2) `should be equal to` 2
        }

        context("with value 2") {
            before { calculator.setValue(2) }
            it("on adding 4 returns 6") {
                calculator.add(4) `should be equal to` 6
            }
        }
    }
})
