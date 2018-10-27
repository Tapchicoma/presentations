class: center, middle

# A test framework for Kotlin

https://spekframework.org/

---

# What is Spek?

**Spek** is a _behaviour driven framework_(BDD) written in Kotlin,
that supports Kotlin multiplatform.red[*].

.footnote[.red[*] WIP: common and jvm for now]

---

# Quick intro to BDD

Story started with unit test:

``` kotlin
class ReferenceNumberTest {
  @Test
  fun testValidate() {
    assertFalse(ReferenceNumber.validate("1234567890123"))
    assertFalse(ReferenceNumber.validate("1234567"))
    assertTrue(ReferenceNumber.validate("12345678"))
  }
}
```

???
 _Typical_ unit-test - just tests a method and that is all.

---

# Quick intro to BDD

Then, someone applied best-practices and rewrote test:
- split into smaller tests, that tests only one thing at a time
- give more meaningful test method names

---

# Quick intro to BDD

``` kotlin
class ReferenceNumberTest {
  @Test fun testTooLong() {
    val len13 = "1234567891111"
    assertEquals(len13.length, 13)
    assertEquals(ReferenceNumber.validate(len13), false)
  }

  ...
}
```

---

# Quick intro to BDD

``` kotlin
class ReferenceNumberTest {
  ...

  @Test fun testTooShort() {
    val len7 = "1234567"
    assertEquals(len7.length, 7)
    assertEquals(ReferenceNumber.validate(len7), false)
  }

  ...
}
```

---

# Quick intro to BDD

``` kotlin
class ReferenceNumberTest {
  ...

  @Test fun testOk() {
    val len8 = "12345678"
    assertEquals(len8.length, 8)
    assertEquals(ReferenceNumber.validate(len8), true)

    val len12 = "123456789111"
    assertEquals(len12.length, 12)
    assertEquals(ReferenceNumber.validate(len12), true)
  }
}
```

???

Now it reads easier, you can guess some conditions that method has.

---

# Quick intro to BDD

But still test doesn't provide all information about `ReferenceNumber.validate()` contract.

And developers may still look into implementation.

--

Again someone tried to improve tests...

---

# Quick intro to BDD

``` kotlin
class ReferenceNumberTest {
  @Test fun \`Null is not valid ReferenceNumber`() {
    assertFalse(ReferenceNumber.validate(null))
  }

  @Test fun \`ReferenceNumber should be shorter than 13`() {
    assertFalse(ReferenceNumber.validate("1234567890123"))
  }

  @Test fun \`ReferenceNumber should be longer than 7`() {
    assertFalse(ReferenceNumber.validate("1234567"))
  }
  ...
}
```

---

# Quick intro to BDD

``` kotlin
class ReferenceNumberTest {
  ...

  @Test fun \`ReferenceNumber should contain only numbers`() {
    assertFalse(ReferenceNumber.validate("1234567ab"))
    assertFalse(ReferenceNumber.validate("abcdefghi"))
    assertFalse(ReferenceNumber.validate("---------"))
    assertFalse(ReferenceNumber.validate("         "))
  }
  ...
}
```

---

# Quick intro to BDD

``` kotlin
class ReferenceNumberTest {
  ...

  @Test fun \`Valid ReferenceNumber examples`() {
    assertTrue(ReferenceNumber.validate("12345678"))
    assertTrue(ReferenceNumber.validate("123456789"))
    assertTrue(ReferenceNumber.validate("1234567890"))
    assertTrue(ReferenceNumber.validate("12345678901"))
    assertTrue(ReferenceNumber.validate("123456789012"))
  }
}
```

---

# Quick intro to BDD

**BDD** flow:
- create your object specifications in common language:

``` kotlin
spec "ReferenceNumber"

it "should not be null"
it "should be shorter than 13"
it "should be longer than 7"
it "should contain only numbers"
it "valid reference number examples"
```

- write test according them
- write actual object implementation

???

Other languages BDD frameworks:
Java (JDave, JBehave), Ruby (RSpec, RBehave, Cucumber), Groovy (Easyb), Scala (Scala-test), PHP (Behat), CPP (CppSpec), .Net (SpecFlow, Shouldly), Python (Lettuce, Cucumber).

---

# Spek setup

Minimal supported Gradle version - `4.7`

---

# Spek setup

Add dependencies to `build.gradle`:

```gradle
ext.spek = <spek_version>

dependencies {
  testImplementation(
    "org.spekframework.spek2:spek-dsl-jvm:$spek"
  )
  testRuntimeOnly(
    "org.spekframework.spek2:spek-runner-junit5:$spek"
  )
}
```

---

# Spek setup

For common sources, should be:

``` gradle
dependencies {
  testImplementation(
    "org.spekframework.spek2:spek-dsl-common:$spek"
  )
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

# Spek setup

Additional setup for android projects:

``` gradle
apply plugin: "de.mannodermaus.android-junit5"

android {
    ...
    testOptions {
        junitPlatform {
            filters {
                engines { include 'spek2' }
            }
        }
    }
}
```

---

# Core concepts

Tests are written using  nested lambdas:
``` kotlin
class CalculatorTest : Spek({
    `group`("A calculator") {
        `test`("returns set value") { .. }
        `group`("with value 2") {
            `test`("on adding 4 returns 6") { .. }
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

`by memoized {}` - special cache delegate, that replaces following code:

``` kotlin
lateinit var calculator: Calculator
beforeEachTest { calculator = Calculator() }
```

---

# Core concepts

### Fixtures

`by memoized {}` - by default, recreates instance for each test.

--

Caching modes(`mode` optional param):
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

// one assertion per test
// data driven tests

---

# Questions?
