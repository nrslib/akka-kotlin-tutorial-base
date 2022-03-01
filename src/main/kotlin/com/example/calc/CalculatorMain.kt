package com.example.calc

import akka.actor.setup.ActorSystemSetup
import akka.actor.typed.ActorSystem
import akka.actor.typed.javadsl.Behaviors
import akka.serialization.jackson.JacksonObjectMapperProviderSetup
import com.example.serialize.KotlinModuleJacksonObjectMapperFactory

fun main() {
    val setup = ActorSystemSetup.empty().withSetup(
        JacksonObjectMapperProviderSetup(
            KotlinModuleJacksonObjectMapperFactory()
        )
    )
    ActorSystem.create(createRoot(), "tutorial-system", setup)
}

fun createRoot() = Behaviors.setup<Void> {
    val calculator = it.spawn(Calculator.create("calculator-1"), "calculator-1")
    calculator.tell(Calculator.Add(1.0))
    calculator.tell(Calculator.Add(2.0)) // 3
    calculator.tell(Calculator.Subtract(1.0)) // 2
    calculator.tell(Calculator.Multiply(10.0)) // 20
    calculator.tell(Calculator.Divide(5.0)) // 4

    val createReceiver = Behaviors.receive(Calculator.GetDataResponse::class.java)
        .onMessage(Calculator.GetDataResponse::class.java) {
            println("value=${it.value}")

            Behaviors.stopped()
        }.build()
    val receiver = it.spawn(createReceiver, "receiver")
    calculator.tell(Calculator.GetData(receiver))
    calculator.tell(Calculator.Clear)

    val afterClearReceiver = it.spawn(createReceiver, "receiver2")
    calculator.tell(Calculator.GetData(afterClearReceiver))

    Behaviors.empty()
}