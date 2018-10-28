package com.github.tapchicoma.spek

import com.github.taphcicoma.spek.Calculator
import org.amshove.kluent.`should be equal to`
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

class CalculatorDataTest : Spek({
    describe("adding a number") {
        val calculator by memoized { Calculator() }

        context("to calculator with value 4") {
            beforeEach { calculator.setValue(4) }
            val testData = mapOf(
                4 to 8,
                122 to 126,
                730 to 734
            )
            testData.forEach { addValue, expectedResult ->
                it("adding $addValue returns $expectedResult") {
                    calculator.add(addValue) `should be equal to` expectedResult
                }
            }
        }
    }
})
