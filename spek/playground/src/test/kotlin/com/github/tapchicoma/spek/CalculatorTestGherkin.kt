package com.github.tapchicoma.spek

import com.github.taphcicoma.spek.Calculator
import org.amshove.kluent.`should be equal to`
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.gherkin.Feature

class CalculatorTestGherkin : Spek({
    Feature("A calculator") {
        val calculator by memoized { Calculator() }
        Scenario("on set value") {
            val value = 2
            var result: Int = Int.MIN_VALUE
            When("set value") {
                result = calculator.setValue(value)
            }
            Then("it returns value") {
                result `should be equal to` 2
            }
        }

        Scenario("adding value") {
            var result: Int = Int.MIN_VALUE
            Given("with value 2") {
                calculator.setValue(2)
            }
            When("adding 4") {
                result = calculator.add(4)
            }
            Then("it returns 6") {
                result `should be equal to` 6
            }
        }
    }
})
