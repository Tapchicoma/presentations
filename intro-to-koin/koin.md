class: center, middle
name: agenda

# Intro to Koin

## Lightweight dependency injection framework

### https://insert-koin.io/

.footnote[By _Yahor Berdnikau_: Android developer **@Freeletics**]

---

# What is Dependency injection

Base class structure:
``` kotlin
interface Brewery {
    fun brewBeer(): Beer
}

class AugustinerBrewery : Brewery {
    fun brewBeer(): Beer { ... }
}

class BeerLover (private val brewery: Brewery) {
    fun drinkBeer() = brewery.brewBeer().drink()
}
```

---

# What is Dependency injection

**Main principle** - object delegates creation of dependencies to some _external object_.

---

# What is Dependency injection

They can be provided via:

- _constructor parameters_:

``` kotlin
val beerLover = BeerLover(AugustinerBrewery())
```

---

# What is Dependency injection

They can be provided via:

- _external object instance (service locator)_:

``` kotlin
class BeerLover(private val serivceLocator: ServiceLocator) {
    val brewery = serivceLocator.getBrewery()
}
```

---

# What is Dependency injection

They can be provided via:

- _setters_:

``` kotlin
class BeerLover {
    private var brewery: Brewery? = null

    fun setBrewery(brewery: Brewery) {
        this.brewery = brewery
    }
}
```

???

Spring framework

---

# What is Dependency injection

They can be provided via:
- _interface injection_:

``` kotlin
interface BrewerySetter {
    fun setBrewery(brewery: Brewery)
}

class BeerLover : BrewerySetter {
    private var brewery: Brewery? = null

    override fun setBrewery(brewery: Brewery) {
        this.brewery = brewery
    }
}
```

???

interface injection - Avalon?

---

# What is Dependency injection

**Main principle** - object delegates creation of dependencies to some _external object_.

They can be provided via:
- _constructor parameters_
- _external object instance (service locator)_
- _setters_
- _interface injection_

---

# Simple Koin setup

Add Koin dependency:

``` gradle
ext.koin_version = "1.0.0-beta-3"
implementation "org.koin:koin-core:$koin_version"
```

--

More details what is changed since `0.9.8`: https://bit.ly/2OsKBXr
or https://medium.com/koin-developers/opening-the-koin-1-0-0-beta-version-99cb8be1c308

---

# Simple Koin setup

Define module:
``` kotlin
val appModule: Module  = module {
    `single` { AugustinerBrewery() as Brewery }
    `factory` { BeerLover(get()) }
}
```

---

# Simple Koin setup

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

`get()` - basic method to resolve dependency from dependency tree:
  - by type: `get<SomeType>()`
  - by name: `get("depency_name")`

---

# Dependency resolution

`by inject()` - kotlin delegate that resolves dependency on first class property access:
- by type: `by inject<SomeType>()`
- by name: `by inject<SomeType>("dependency_name")`


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

For the same type definitions, Koin will throw `DependencyResolutionException`.

--

To solve it - use named definitions:

``` kotlin
val namedModule = module {
    `single("best_brewery")` { AugustinerBrewery() as Brewery }
    `single("okay_brewery")` { PaulanerBrewery() as Brewery }
}

class NamedApplication : KoinComponent {
    fun run() {
*       val brewery = get<Brewery>("best_brewery")
    }
}

```

---

# Properties

Properties is a "static" values loaded on koin start:
- from `koin.properties` file (in `src/main/resources`, `src/test/resources`, `assets/koin.properties`)

``` kotlin
startKoin(listOf(..), `useKoinPropertiesFile = true`)
```


---

# Properties

Properties is a "static" values loaded on koin start:
- from `koin.properties` file (in `src/main/resources`, `src/test/resources`, `assets/koin.properties`)
- from environment properties

``` kotlin
startKoin(listOf(..), `useEnvironmentProperties = true`)
```

---


# Properties

Properties is a "static" values loaded on koin start:
- from `koin.properties` file (in `src/main/resources`, `src/test/resources`, `assets/koin.properties`)
- from environment properties
- via parameter in `startKoin()` method

``` kotlin
startKoin(listOf(..), `extraProperties = mapOf("one" to 12345)`)
```

---

# Properties

Property can be accessed using:
- `getProperty("property_key")`
- `by property("property_key")`

--

Property can be added by calling on `KoinComponent` or on `context` object:

``` kotlin
    setProperty("property_key", 42)
```

---

# Properties usage example

``` kotlin
val parametersModule = module {
*   single { BrewDogBrewery(getProperty("beer_amount")) }
    factory { BeerLover(get()) }
}

class ParametersApplication : Application, KoinComponent {
    override fun run() {
        startKoin(
            listOf(parametersModule),
*           extraProperties = mapOf("beer_amount" to 750)
        )
        val beerLover = get<BeerLover>()
        beerLover.drink()
    ...
```

---

# Injection parameters

Allows to dynamically provide class paramter when requesting definition:

--

``` kotlin
val paramsModule = module {
    factory { `(beerAmount: Int)` ->
        BrewDogBrewery(beerAmount) as Brewery
    }
}
```

---

# Injection parameters

Allows to dynamically provide class paramter when requesting definition:

``` kotlin
class ParamsApplication : Application, KoinComponent {
    override fun run() {
        startKoin(listOf(paramsModule))
        val brewery = `get<Brewery> { parametersOf(750) }`
        println(brewery.brewBeer())
        closeKoin()
    }
}
```

---

# Injection parameters

Allows to dynamically provide class paramter when requesting definition:


``` kotlin
class ParamsApplication : Application, KoinComponent {
    private val brewery: Brewery `by inject { parametersOf(750) }`
    override fun run() {
        startKoin(listOf(paramsModule))
        println(brewery.brewBeer())
        closeKoin()
    }
}
```

---

# Modules

Main goal of modules:
- **to scope** definitions in namespace
- an ability **to drop** created definitions instances inside namespace

---

# Modules

Module namespace is "similar" to java packages:

--

``` kotlin
// Namespace = "."
val rootNamespace = module { }

```

--

```kotlin
// Namespace = ".sample"
val sampleNamespace = module("sample") {  }
```

--

``` kotlin
// Namespace = ".Brewery"
val classNamespace = module(Brewery::class.moduleName) {  }
```

---

# Inner modules

Module can also contain _inner_ modules, that will have _parent_ namespace:

``` kotlin
val innerModule = module {
    single { AugustinerBrewery() as Brewery }
*   module("month") {
*       module("october") {
            factory { OktoberfestBeerLover(get()) }
        }
    }
}
```

---

# Inner modules

Module can also contain _inner_ modules, that will have _parent_ namespace:

``` kotlin
val innerModule = module {
    single { AugustinerBrewery() as Brewery }
*   module("month.october") {
        factory { OktoberfestBeerLover(get()) }
    }
}
```

---

# Definitions visibility in modules

Module namespace isolation:

``` kotlin
val moduleVisibility = `module` {
    single("amount") { 750 }
*   module("craftbiermuc") {
        single { BrewDogBrewery(get("amount")) as Brewery }
        factory { BeerLover(get()) }
    }
*   module("oktoberfest") {
        single { AugustinerBrewery() as Brewery }
*       module("tourist") {
            factory { BeerLover(get()) }
        }
    }
}
```

???

* definitions from **tourist** can get definitions from **oktoberfest** and **root**
* definitions from **oktoberfest** can get definitions from **root**
* definitions from **craftbiermuc** can get definitions from **root**

All other visibilities are **blocked**.

---

# Definitions visibility in modules

**Child namespaces can see their parents, but not the inverse!**

---

# Using modules

``` kotlin
val sampleNamespace = module("sample") {
    single { AugustinerBrewery() as Brewery }
}
val classNamespace = module(Brewery::class.moduleName) {
    single { BrewDogBrewery() as Brewery }
}

class Application : KoinComponent {
    fun run() {
        startKoin(listOf(sampleNamespace, classNamespace))
        val brewery = get<Brewery>(`module = "sample"`)
    }
}
```

---

# Releasing definitions

Releasing defintions helps to manage instances lifecycle and save memory.

--

``` kotlin
val sampleNamespace = module("sample") {..}

class ReleaseInstancesApplication : Application, KoinComponent {
    override fun run() {
        val brewery = get<Brewery>()
        release("sample")
        val newBrewery = get<Brewery>()
        assert(brewery !== newBrewery)
    }
}
```

---

# Releasing definitions

_On releasing instances in parent namespace, all child namespaces instances are also released!_

---

# Testing

Include test dependency:

``` gradle
ext.koin_version = "1.0.0-beta-3"
testImplementation "org.koin:koin-test:$koin_version"
```

---

# Testing

Test class shoud:
- extend `KoinTest`

--

- have following:

``` kotlin
@Before fun before() {
    startKoin(listOf(myModule))
}

@After fun after() {
    closeKoin()
}

```

---

# Testing

Koin test adds following:
* Use `by inject()` or `get()` in test fields to get required depedencies.
* Use `declareMock<Type>()` in test to replace actual instance with Mockito _mock_.
* Use `declare { factory {..}}` to provide stubs implementations.

---

# Testing: check

`check(listOf(..))` - walks through provided modules list definitions graph
and checks that each definition is bound.

---

# Testing: dry run

`dryRun()` - allows to verify app injections graph

``` kotlin
class MyTest : KoinTest {
    @Test
    fun dryRun() {
        startKoin(listOf(/* list of app modules */))`
*       dryRun()
    }
}
```

---

# Logging

Koin defines following interface fqййqor loggers:

``` kotlin
interface Logger {
    fun debug(msg : String)
    fun log(msg : String)
    fun err(msg : String)
}
```

---

# Logging

Use custom logger:

``` kotlin
val koinLogger = object : Logger {
    override fun debug(msg: String) { .. }
    override fun log(msg: String) { .. }
    override fun err(msg: String) { .. }
}
startKoin(listOf(..), `logger = koinLogger`)

```
---

# Logging

Already existing `Logger` implementations:
- `PrintLogger` - uses `println()` (default, in `koin-core`)
- `EmptyLogger` - log nothing (in `koin-core`)
- `SLF4JLogger` - uses `SLF4J` library (in `koin-logger-slf4j`)
- `AndroidLogger` - uses `android.util.Log` (in `koin-android`)

---

# Logging

Example:


``` irc-log
(KOIN)::[inf] [Logger] display debug = false
(KOIN)::[inf] [context] create
(KOIN)::[inf] [module] declare Single [class='koin.Brewery']
(KOIN)::[inf] [module] declare Factory [class='koin.BeerLover']
(KOIN)::[inf] [modules] loaded 2 definitions
(KOIN)::[inf] [properties] load koin.properties
(KOIN)::[inf] +-- 'koin.BeerLover'
(KOIN)::[inf] | +-- 'koin.Brewery'
(KOIN)::[inf] | \-- (*)
(KOIN)::[inf] \-- (*)
(KOIN)::[inf] [Close] Closing Koin context
```

---

# Koin packages

Core:

``` gradle
// Koin for Kotlin
compile "org.koin:koin-core:$koin_version"
// Koin for Unit tests
testCompile "org.koin:koin-test:$koin_version"
// Koin for Java developers
compile "org.koin:koin-java:$koin_version"
// Advanced features
compile "org.koin:koin-reflect:$koin_version"
```

???

`reflect` adds `build()` method that can instantiate object via reflection
`java` add java friendly functions to use Koin

---

# Koin packages

Android:

``` gradle
// Koin for Android
compile "org.koin:koin-android:$koin_version"
// Koin Android Scope feature
compile "org.koin:koin-android-scope:$koin_version"
// Koin Android ViewModel feature
compile "org.koin:koin-android-viewmodel:$koin_version"

// AndroidX (based on koin-android)
// Koin AndroidX Scope feature
compile "org.koin:koin-androidx-scope:$koin_version"
// Koin AndroidX ViewModel feature
compile "org.koin:koin-androidx-viewmodel:$koin_version"
```

???

`scope` - adds useful methods to scope to certain module namespace injections
and release it's definitions

---

# Koin packages

Spark:

``` gradle
// Koin for Spark Kotlin
compile "org.koin:koin-spark:$koin_version"
```

---

# Koin packages

Ktor:

``` gradle
// Koin for Ktor Kotlin
compile "org.koin:koin-ktor:$koin_version"
```

---

# Pros/cons versus Dagger 2

**Pros**:

--

- Easier to understand

--

- Easier to find where class dependency comes from

--

- Doesn't require annotation processing (my favourite)

--

- Logging sometimes is useful

--

- Easier to use in gradle modules/libraries

---

# Pros/cons versus Dagger 2

**Cons**:

--

- Creates graph of dependencies in runtime

--

- Graph verification only via dry run (not on compile time)

--

- Somewhat coupled with a code

---

class: center, middle

# Thank you!

## Questions?
