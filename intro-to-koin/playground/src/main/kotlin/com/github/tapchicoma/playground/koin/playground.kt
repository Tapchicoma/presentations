package com.github.tapchicoma.playground.koin

import org.junit.After
import org.junit.Before
import org.junit.Test
import org.koin.core.Koin
import org.koin.core.parameter.parametersOf
import org.koin.dsl.module.module
import org.koin.dsl.path.moduleName
import org.koin.standalone.KoinComponent
import org.koin.standalone.StandAloneContext.closeKoin
import org.koin.standalone.StandAloneContext.startKoin
import org.koin.standalone.get
import org.koin.standalone.inject
import org.koin.standalone.property
import org.koin.standalone.release
import org.koin.test.KoinTest
import org.koin.test.check
import org.koin.test.declare
import org.koin.test.declareMock
import org.koin.test.dryRun

open class Beer {
    open fun drink() = println("gulp gulp gulp")
}

class CraftBeer(
    private val amount: Int = 350
) : Beer() {
    override fun drink() = println("Gulp $amount of beer")
}

interface Brewery {
    fun brewBeer(): Beer
}

class AugustinerBrewery : Brewery {
    override fun brewBeer(): Beer = Beer()
}

class BeerLover(
    private val brewery: Brewery
) {
    fun drink() = brewery.brewBeer().drink()
}

interface Application {
    fun run()
}

val simpleAppModule = module {
    single { AugustinerBrewery() as Brewery }
    factory { BeerLover(get()) }
}

class SimpleApplication : Application {
    override fun run() {
        println("Starting simple application")

        val koin: Koin = startKoin(listOf(simpleAppModule))
        val beerLover = koin.koinContext.get<BeerLover>()
        beerLover.drink()
        closeKoin()

        println("Simple application stopped")
    }
}

class SimpleApplicationComponent : Application, KoinComponent {
    override fun run() {
        println("Starting simple application")

        startKoin(listOf(simpleAppModule))
        val beerLover = get<BeerLover>()
        beerLover.drink()
        closeKoin()

        println("Simple application stopped")
    }
}

class SimpleApplicationInject : Application, KoinComponent {
    private val beerLover by inject<BeerLover>(name = "soms")

    override fun run() {
        startKoin(listOf(simpleAppModule))
        beerLover.drink()
        closeKoin()
    }
}

class PaulanerBrewery : Brewery {
    override fun brewBeer(): Beer = Beer()
}

val namedModule = module {
    single("best_brewery") { AugustinerBrewery() as Brewery }
    single("okay_brewery") { PaulanerBrewery() as Brewery }
}

class NamedApplication : KoinComponent {
    private val test: Boolean by property("property_name")
    fun run() {
        startKoin(listOf(namedModule))
        val brewery = get<Brewery>("best_brewery")
        println(brewery.toString())
        closeKoin()
    }
}

class BrewDogBrewery(
    private val defaultAmountOfBeer: Int = 350
) : Brewery {
    override fun brewBeer(): Beer = CraftBeer(defaultAmountOfBeer)
}

val propertiesModule = module {
    single { BrewDogBrewery(getProperty("beer_amount")) as Brewery }
    factory { BeerLover(get()) }
}

class PropertiesApplication : Application, KoinComponent {
    override fun run() {
        startKoin(listOf(propertiesModule), extraProperties = mapOf("beer_amount" to 750))
        val beerLover = get<BeerLover>()
        beerLover.drink()
        closeKoin()
    }
}

val paramsModule = module {
    factory { (beerAmount: Int) ->  BrewDogBrewery(beerAmount) as Brewery }
}

class ParamsApplication : Application, KoinComponent {
    override fun run() {
        startKoin(listOf(paramsModule))
        val brewery = get<Brewery> { parametersOf(750) }
        println(brewery.brewBeer())
        closeKoin()
    }
}

val rootNamespace = module {}
val sampleNamespace = module("sample") {
    single { AugustinerBrewery() as Brewery }
}
val classNamespace = module(Brewery::class.moduleName) {
    single { BrewDogBrewery() as Brewery }
}

class NamespaceApplication : Application, KoinComponent {
    override fun run() {
        startKoin(listOf(rootNamespace, sampleNamespace, classNamespace))
        val brewery: Brewery = get(module = "sample")
        println(brewery.brewBeer())
        closeKoin()
    }
}

val innerModule = module {
    single { AugustinerBrewery() as Brewery }
    module("sample") {
        factory { BeerLover(get()) }
    }
}

val moduleVisibility = module {
    single("amount") { 750 }
    module("craftbiermuc") {
        single { BrewDogBrewery(get("amount")) as Brewery }
        factory { BeerLover(get()) }
    }
    module("oktoberfest") {
        single { AugustinerBrewery() as Brewery }
        module("tourist") {
            factory { BeerLover(get()) }
        }
    }
}

class VisibilityApplication : Application, KoinComponent {
    override fun run() {
        startKoin(listOf(moduleVisibility))
        val beerLover = get<BeerLover>(module = "craftbiermuc")
        beerLover.drink()
        closeKoin()
    }
}

class ReleaseInstancesApplication : Application, KoinComponent {
    override fun run() {
        startKoin(listOf(sampleNamespace))
        val brewery = get<Brewery>()
        release("sample")
        val newBrewery = get<Brewery>()
        assert(brewery !== newBrewery)
        closeKoin()
    }
}

fun main(vararg args: String) {
    val application = ReleaseInstancesApplication()
    application.run()
}

/** Testing **/

class TestExample : KoinTest {
    @Before
    fun before() {
        startKoin(listOf(simpleAppModule))
        declareMock<Brewery>()
        declare { object : Brewery {
            override fun brewBeer(): Beer = Beer()
        }}
    }

    @Test
    fun someTest() {}

    @After
    fun after() {
        closeKoin()
    }
}

val checkFailModule = module { factory { BeerLover(get()) } }
class CheckTestExample : KoinTest {
    @Test fun checkGraph() {
        check(listOf(checkFailModule)) // Fail to find Brewery
    }
}

class DryRunExample : KoinTest {
    @Test fun testDryRun() {
        startKoin(listOf(simpleAppModule))
        dryRun()
    }
}
