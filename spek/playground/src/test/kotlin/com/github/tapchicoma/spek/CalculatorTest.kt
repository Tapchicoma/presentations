package com.github.tapchicoma.spek

import com.github.taphcicoma.spek.Calculator
import org.amshove.kluent.`should be equal to`
import org.spekframework.spek2.Spek

class CalculatorTest : Spek({
    group("A calculator") {
        val calculator by memoized { Calculator() }
        test("returns set value") {
            calculator.setValue(2) `should be equal to` 2
        }

        group("with value 2") {
            beforeGroup { calculator.setValue(2) }
            test("on adding 4 returns 6") {
                calculator.add(4) `should be equal to` 6
            }
        }
    }
})