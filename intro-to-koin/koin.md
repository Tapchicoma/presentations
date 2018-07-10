class: center, middle
name: agenda

# Intro to Koin

## Lightweight dependency injection framework

### https://insert-koin.io/

---

# What is Dependency injection

**Main principle** - object delegates creation of dependencies to some _external object_.

--

They can be provided via:

- _constructor parameters_

--

- _external object instance (service locator)_

--

- _setters_

--

- _interface injection_

???

setters - spring
interface injection - Avalon?

---

# Simple Koin setup

Base class structure:
``` kotlin
interface Brewery {
    fun brewBeer(): Beer
}

class AugustinerBrewery : Brewery {
    fun brewBeer(): Beer { ... }
}

class BeerLover (
    private val brewery: Brewery
) {
    fun drinkBeer() = {
        val beer = brewery.brewBeer()
        beer.drink()
    }
}
```

---

# Simple Koin setup

Add Koin dependency:

``` gradle
implementation "org.koin:koin-core:$koin_version"
```

--

Define module:
``` kotlin
val appModule: Module = applicationContext {
    `bean` { AugustinerBrewery() as Brewery }
    `factory` { BeerLover(get()) }
}
```

--

Start Koin on app app entry point:
``` kotlin
fun main(vararg args: String) {
    val koin = `startKoin(listOf(appModule))`
}
```

---

# Simple Koin setup

Get dependency and use it:

``` kotlin
fun main(vararg args: String) {
    val koin = startKoin(listOf(appModule))

*   val beerUser = koin.koinContext.get<BeerLover>()
    beerUser.drinkBeer()
}
```

---

# Simple Koin setup

Add `KoinComponent` interface to get a bunch of useful extensions:

``` kotlin
class Application : `KoinComponent` {
    fun run() {
*       val beerUser = get<BeerLover>()
        beerUser.drinkBeer()
    }
}

fun main(vararg args: String) {
    startKoin(listOf(appModule))
    val application = Application()
    application.run()
}
```

---

# Dependency resolution

- `get()` - basic method to resolve dependency from dependency tree:
  - by type: `get<SomeType>()`
  - by name: `get("some_name")`

--

- `by inject()` - kotlin delegate that resolves dependency on first class property access:

``` kotlin
class ApplicationWithInject : KoinComponent {
*   private val beerUser by inject<BeerLover>()

    fun run() {
        beerUser.drinkBeer()
    }
}
```

---

# Multiple definitions

- For the same type definitions, Koin is always selects the last one.

--

- To solve it - use named definitions:

``` kotlin
val appModule = applicationContext {
    `bean("best_brewery")` { AugustinerBrewery() as Brewery }
    `bean("okay_brewery")` { StanbergerBrewery() as Brewery }
}

class NamedApplication : KoinComponent {
    fun run() {
*       val brewery = get<Brewery>("best_brewery")
    }
}

```

---

# Properties

Properties is a static values loaded on koin start:
- from `koin.properties` file (in `src/main/resources`, `src/test/resources`, `assets/koin.properties`):

``` kotlin
startKoin(listOf(..), `useKoinPropertiesFile = true`)
```

--

- via parameter in `startKoin()` method:

``` kotlin
startKoin(listOf(..), `extraProperties = mapOf("one" to 12345)`)
```

--

- from environment properties:

``` kotlin
startKoin(listOf(..), `useEnvironmentProperties = true`)
```

--

Property can be accessed:
- `getProperty(<Key>)`
- `by property(<Key>)`

--

Property can be added by calling `setProperty("ZZZ", 123213)` in `KoinComponent` or on `context` object.

---

# Parameters

Allows to dynamically provide class paramter when requesting definition:

--

``` kotlin
class CraftBrewery(
    private val beerAmount: Int
) : Brewery {
    override fun brewBeer(): Beer = Beer(beerAmount)
}

val BEER_AMOUNT_PARAM = "beer_amount"

val moduleWithParams = applicationContext {
    factory { `params -> CraftBrewery(params[BEER_AMOUNT]) as Brewery` }
}
```

--

``` kotlin
class ApplicationWithParams : KoinComponent {
    fun run() {
*       val brewery: Brewery = get { mapOf(BOTTLE_AMOUNT to 350) }
    }
}
```

---

# Context

A `context` is a logical subset of bean definitions inside a module:


``` kotlin
val moduleWithContext = applicationContext {
    bean { AugustinerBrewery() as Brewery }

*   context("Oktoberfest") {
        factory { BeerLover(get()) }
    }
}
```

---

# Context

Main goal of context is an ability to drop it's definitions instances:


``` kotlin
class ApplicationWithContext : KoinComponent {
    fun run() {
        val beerLover = get<BeerLover>()
*       releaseContext("Oktoberfest")
        val newBeerLover = get<BeerLover>()

        beerLover `should not be` newBeerLover
    }
}
```

---

# Context

Context isolation:

``` kotlin
val moduleWithNestedContexts = applicationContext {
    context("A") {
        context("B") {}
    }

    context("C") {}
}
```

* definitions from **B** can see definitions from **A** and **Application context**
* definitions from **A** can see definitions from **Application context**
* definitions from **C** can see definitions from **Application context**

All other visibilities are **blocked**.

---

# Testing

* Include test dependency:

``` gradle
testImplementation "org.koin:koin-test:$koin_version"
```

--

* Test class shoud extend `KoinTest`

--

* Add:

``` kotlin
@Before
fun before(){
    startKoin(listOf(myModule))
}

@After
fun after(){
    closeKoin()
}

```

Use `by inject()` in test fields to get required depedencies.

---

# Koin dry run

Allows to verify injections graph using Unit test:

``` kotlin
class MyTest : KoinTest {
    @Test
    fun dryRun() {
        startKoin(/* list of app modules */)
*       dryRun()
    }
}
```

---

# Logging

Koin defines following interface for loggers:

``` kotlin
interface Logger {
    fun log(msg : String)
    fun debug(msg : String)
    fun err(msg : String)
}
```

--

Set your custom logger:

``` kotlin
Koin.logger = object : Logger {
    override fun debug(msg: String) { .. }

    override fun err(msg: String) { .. }

    override fun log(msg: String) { .. }
}
```

--

Default is `PrintLogger` implementation - it uses `println()`.

---

# Koin packages

``` gradle
koin_version = '0.9.3' // Latest stable version

// Koin for Kotlin
implementation "org.koin:koin-core:$koin_version"

// Koin for Android
implementation "org.koin:koin-android:$koin_version"

// Koin for Android Architecture Components
implementation "org.koin:koin-android-architecture:$koin_version"

// Koin for Spark Kotlin
implementation "org.koin:koin-spark:$koin_version"

// Koin for Ktor Kotlin
implementation "org.koin:koin-ktor:$koin_version"

// Koin for JUnit tests
testImplementation "org.koin:koin-test:$koin_version"
```

???
`koin-android` - adds android logger and extentions to `Application` class
and `ComponentCallbacks` interface

`koin-android-architecture` - add extentions helper functions to provide
`ViewModel` using Koin and special version of `ViewModelProvider.Factory`

`koin-ktor` - adds helper methods to create koin graph on ktor `Application`

`koin-spark` - add helper methods to spark

`koin-test` - exposes internals, add junit assertions, dry run

---

# Pro/cons versus Dagger 2

**Pros**:
- Easier to understand
- Easier to find where class dependency comes from
- Doesn't require annotation processing
- Logging sometimes is useful

**Cons**:
- Creates graph of dependencies in runtime
- Graph verification only via dry run
- Somewhat coupled with a code

---

# Next Koin 1.0.0 release

- instead of `applicationContext` and `context` just `module`
- `bean` renamed to `single`
- added `createOnStart` flag
- added `override` flag to be possible override definition from another module
- improved parameters injection
- adds java interop
- optional reflection based injection
- android lifecycle support for contexts
- Spark and Ktor are using `koin-logger-slf4j` as default

More details: https://medium.com/koin-developers/opening-the-koin-1-0-0-beta-version-99cb8be1c308

---

class: center, middle

# Thank you
