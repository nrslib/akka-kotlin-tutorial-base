package com.example.sample

import akka.actor.typed.javadsl.AbstractBehavior
import akka.actor.typed.javadsl.ActorContext
import akka.actor.typed.javadsl.Behaviors
import akka.actor.typed.javadsl.Receive

class PrinterActor(context: ActorContext<SampleActor.Response>)
    : AbstractBehavior<SampleActor.Response>(context) {

    companion object {
        fun create() = Behaviors.setup<SampleActor.Response> {
            PrinterActor(it)
        }
    }

    override fun createReceive(): Receive<SampleActor.Response> =
        newReceiveBuilder()
            .onMessage(SampleActor.Response::class.java) {
                println("data: ${it.text}")

                this
            }
            .build()
}