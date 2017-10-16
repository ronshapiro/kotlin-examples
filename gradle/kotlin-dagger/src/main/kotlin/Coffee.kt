package com.example.dagger.kotlin

import dagger.*
import javax.inject.Inject
import javax.inject.Singleton

interface Heater {
    fun on()
    fun off()
    val isHot: Boolean
}

interface Pump {
    fun pump()
}

class ElectricHeater : Heater {
    var heating: Boolean = false

    override fun on() {
        println("~ ~ ~ heating ~ ~ ~")
        this.heating = true
    }

    override fun off() {
        this.heating = false
    }

    override val isHot get() = heating
}

class Thermosiphon
@Inject constructor(
        private val heater: Heater
) : Pump {
    override fun pump() {
        if (heater.isHot) {
            println("=> => pumping => =>")
        }
    }
}

@Module
interface PumpModule {
    @Binds
    fun providePump(pump: Thermosiphon): Pump

    @Module
    companion object {
        @Provides @Singleton @JvmStatic
        fun provideHeater(): Heater = ElectricHeater()
    }
}

class CoffeeMaker
@Inject constructor(
        private val heater: Lazy<Heater>,
        private val pump: Pump
) {
    fun brew() {
        heater.get().on()
        pump.pump()
        println(" [_]P coffee! [_]P ")
        heater.get().off()
    }
}

@Singleton
@Component(modules = arrayOf(PumpModule::class))
interface CoffeeShop {
    fun maker(): CoffeeMaker
}

fun main(args: Array<String>) {
    val coffee = DaggerCoffeeShop.builder().build()
    coffee.maker().brew()
}
