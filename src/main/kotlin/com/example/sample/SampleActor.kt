package com.example.sample

import akka.actor.typed.ActorRef
import akka.actor.typed.Behavior
import akka.actor.typed.javadsl.AbstractBehavior
import akka.actor.typed.javadsl.ActorContext
import akka.actor.typed.javadsl.Behaviors
import akka.actor.typed.javadsl.Receive
import java.util.*

class SampleActor(context: ActorContext<Message>)
    : AbstractBehavior<SampleActor.Message>(context) {

    companion object {
        fun create() = Behaviors.setup<Message> {
            SampleActor(it)
        }
    }

    sealed interface Message
    data class Greet(val name: String) : Message
    data class GetData(val replyTo: ActorRef<Response>) : Message
    data class Response(val text: String)

    override fun createReceive(): Receive<Message> =
        newReceiveBuilder()
            .onMessage(Greet::class.java) {
                println("hello ${it.name}")

                this
            }
            .onMessage(GetData::class.java) {
                val data = UUID.randomUUID().toString()
                val response = Response(data)

                it.replyTo.tell(response)

                this
            }
            .build()
}