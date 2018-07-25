package com.github.tapchicoma.playground.koin

import org.koin.core.Koin
import org.koin.dsl.module.module
import org.koin.standalone.KoinComponent
import org.koin.standalone.StandAloneContext.closeKoin
import org.koin.standalone.StandAloneContext.startKoin
import org.koin.standalone.get

class Beer {
    fun drink() = println("gulp gulp gulp")
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


fun main(vararg args: String) {
    val application = SimpleApplication()
    application.run()
}
