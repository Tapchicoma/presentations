package com.github.tapchicoma.playground.koin

import org.koin.core.Koin
import org.koin.core.parameter.parametersOf
import org.koin.dsl.module.module
import org.koin.standalone.KoinComponent
import org.koin.standalone.StandAloneContext.closeKoin
import org.koin.standalone.StandAloneContext.startKoin
import org.koin.standalone.get
import org.koin.standalone.inject
import org.koin.standalone.property

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

fun main(vararg args: String) {
    val application = ParamsApplication()
    application.run()
}
