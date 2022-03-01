package com.example.rest

import akka.actor.typed.javadsl.Behaviors
import com.example.calc.Calculator

object RestGuardian {
    fun create() = Behaviors.setup<Void> {
        val calculator = it.spawn(Calculator.create("calculator-1"), "calculator-1")
        val route = CalculatorRoutes(it.system, calculator)
        val routes = route.routes()

        RestApp.startServer(it.system, routes)

        Behaviors.empty()
    }
}