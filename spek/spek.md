class: center, middle

# A test framework for Kotlin

https://spekframework.org/

---

# What is spek?

---

# History of spek creation

Possibly not needed...

---

# Spek setup

Minimal supported Gradle version - `4.7`

---

# Spek setup

Add dependencies to `build.gradle`:

```gradle
ext.spek = <spek_version>

dependencies {
  testImplementation("org.spekframework.spek2:spek-dsl-jvm:$spek")
  testRuntimeOnly("org.spekframework.spek2:spek-runner-junit5:$spek")
}
```

---

# Spek setup

Use `spek2` engine for tests:

```gradle
test {
    useJUnitPlatform {
        includeEngines 'spek2'
    }
}
```

---

# Spek setup

Optionally install plugin:

https://plugins.jetbrains.com/plugin/10915-spek-framework

---

# Core concepts

Tests are written using  nested lambdas:
``` kotlin
class CalculatorTest : Spek({
    `group`("A calculator") {
        `test`("returns set value") { .. }
        `group`("with value 2") {
            `test`("on adding 4 returns { .. }
        }
    }
})
```

---

# Core concepts

Lambdas has two scopes:

--

- **Test** scope is where you place your assertions/checks.

``` kotlin
    `test`("some test decription") { assert((2 + 2) == 4) }
```

--

- **Group** scope is used to organize your tests. It can contain test scopes and other group scopes as well.

``` kotlin
 `group`("group description") {
    `group`("nested group") { .. }
    test("test in 'group description'") { .. }
 }
```

---

# Core concepts

### Phases:

--

- Discovery -  build the test tree execution using **group scope**.

--

**Don't** initialize test value(s) or do any setup in **group scope**, as it runs only _once_!

--

- Execution - executes tests from **test scope** using execution tree from discovery phase.

---

# Core concepts

### Fixtures:

- `beforeGroup()` - executes code _before all tests in the group_
- `afterGroup()` - executes code _after all tests in the group_

``` kotlin
*beforeGroup() { initializeTempFile() }
group("on temp file) { ... }
*afterGroup() { removeTestFile() }
```

---

# Core concepts

### Fixtures:

- `beforeEachTest()` - executes code before **each** test in the group
- `afterEachTest()` - executes code after **each** test in the group

``` kotlin
group("on temp file") {
*   beforeEachTest { fillTestData() }
    test("some test 1") { .. }
    test("some test 2") { .. }
*   afterEachTest { cleanTempFile() }
}
```

---

# Core concepts

### Fixtures

`by memoized {}` - special cache delegate. By default, recreates instance for each test:

``` kotlin
lateinit var calculator: Calculator
beforeEachTest { calculator = Calculator() }
```

--

Caching modes(`mode` param):
- `TEST` - creates new instance per test
- `GROUP` - creates new instance per group (nested one as well)
- `SCOPE` - creates new instance withing the group

---

# Test Styles - Specification

It was inspired by Ruby `RSpec` and javascript `Jasmine` test frameworks:

``` kotlin
class CalculatorTestSpec : Spek({
    `describe`("A calculator") {
        val calculator by memoized { Calculator() }
        `it`("returns set value") { <test_assertion> }

        `describe`("with value 2") {
            `before` { calculator.setValue(2) }
            `it`("on adding 4 returns 6") { <test_assertion> }
        }
}})
```

---

# Test Styles - Specification

Core to style bindings:

.pure-table.pure-table-bordered.pure-table-striped.smaller-font[

| Core      | Specification |
| :----------------: | :-------------------: |
| group | describe |
| group | context |
| test | it |
| beforeGroup | before |
| afterGroup | after |
| beforeEach | beforeEachTest |
| afterEach | afterEachTest |

]

---

# Test Styles - Gherkin

It was inspired by Cucumber test framework Gherkin style:

``` kotlin
class CalculatorTestGherkin : Spek({
    `Feature`("A calculator") {
        val calculator by memoized { Calculator() }
        `Scenario`("on set value") {
            `Then`("it returns value") { <test_assertion> }
        }

        `Scenario`("with value 2") {
            `Given`("with value") { calculator.setValue(2) }
            `Then`("on adding 4 returns 6") { <test_assertion> }
    }
})
```

---

# Test Styles - Gherkin

Core to style bindings:

.pure-table.pure-table-bordered.pure-table-striped.smaller-font[

| Core      | Gherkin |
| :----------------: | :-------------------: |
| group | Feature |
| group | Scenario |
| test | Given |
| test | When |
| test | Then |
| test | And |

]

---

# Tips and tricks

---

# Why it is better then Junit 5?

---

# Questions?
