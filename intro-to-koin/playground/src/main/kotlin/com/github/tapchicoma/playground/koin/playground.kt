package com.github.tapchicoma.playground.koin

import org.koin.dsl.module.module
import org.koin.standalone.StandAloneContext.closeKoin
import org.koin.standalone.StandAloneContext.startKoin
import kotlin.system.measureTimeMillis

val mainModule = module {}

fun main(vararg args: String) {
    println("Starting app")
    val kotinStartupTime = measureTimeMillis {
        startKoin(listOf(mainModule))
    }
    println("Koin stared in $kotinStartupTime millis")
    closeKoin()
}
