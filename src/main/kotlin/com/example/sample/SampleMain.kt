package com.example.sample

import akka.actor.typed.ActorSystem
import akka.actor.typed.javadsl.Behaviors

fun main() {
    ActorSystem.create(createRoot(), "sample-system")
}

fun createRoot() = Behaviors.setup<Void> {
    val sampleActor = it.spawn(SampleActor.create(), "sampleActor")
//    val printerActor = it.spawn(PrinterActor.create(), "printerActor")

    val receiverCreate = Behaviors.receive(SampleActor.Response::class.java)
        .onMessage(SampleActor.Response::class.java) {
            println("receiver get data: ${it.text}")

            Behaviors.same()
        }
    val receiver = it.spawn(receiverCreate.build(), "receiver")

    val message = SampleActor.GetData(receiver)
    sampleActor.tell(message)

    Behaviors.empty()
}