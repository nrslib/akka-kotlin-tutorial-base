package com.example.calc

import akka.actor.typed.ActorRef
import akka.actor.typed.javadsl.Behaviors
import akka.persistence.typed.PersistenceId
import akka.persistence.typed.javadsl.CommandHandler
import akka.persistence.typed.javadsl.EventHandler
import akka.persistence.typed.javadsl.EventSourcedBehavior
import com.example.serialize.JacksonSerializable

class Calculator(entityId: String)
    : EventSourcedBehavior<Calculator.Command, Calculator.Event, Calculator.State>(PersistenceId.ofUniqueId(entityId)) {

    companion object {
        fun create(id: String) = Behaviors.setup<Command> {
            Calculator(id)
        }
    }

    sealed interface Command
    data class Add(val value:Double) : Command
    data class Subtract(val value:Double) : Command
    data class Multiply(val value:Double) : Command
    data class Divide(val value:Double) : Command
    object Clear : Command

    data class GetData(val replyTo: ActorRef<GetDataResponse>) : Command
    data class GetDataResponse(val value: Double)

    sealed interface Event : JacksonSerializable
    data class Added(val value: Double) : Event
    data class Subtracted(val value: Double) : Event
    data class Multiplied(val value: Double) : Event
    data class Divided(val value: Double) : Event
    object Cleared : Event

    data class State(val value: Double) : JacksonSerializable

    override fun emptyState(): State {
        return State(0.0)
    }

    override fun shouldSnapshot(state: State, event: Event, sequenceNr: Long): Boolean {
        return event is Cleared
    }

    override fun commandHandler(): CommandHandler<Command, Event, State> =
        newCommandHandlerBuilder()
            .forAnyState()
            .onCommand(Add::class.java) { _, command ->
                Effect().persist(Added(command.value))
            }
            .onCommand(Subtract::class.java) { _, (value) ->
                Effect().persist(Subtracted(value))
            }
            .onCommand(Multiply::class.java) { _, (value) ->
                Effect().persist(Multiplied(value))
            }
            .onCommand(Divide::class.java) { state, (value) ->
                if (state.value == 0.0) {
                    throw IllegalCallerException()
                }
                Effect().persist(Divided(value))
            }
            .onCommand(Clear::class.java) { _, _ ->
                Effect().persist(Cleared)
            }
            .onCommand(GetData::class.java) { (value), (replyTo) ->
                val response = GetDataResponse(value)
                replyTo.tell(response)

                Effect().none()
            }
            .build()

    override fun eventHandler(): EventHandler<State, Event> =
        newEventHandlerBuilder()
            .forAnyState()
            .onEvent(Added::class.java) { state, event ->
                val result = state.value + event.value
                state.copy(value = result)
            }
            .onEvent(Subtracted::class.java) { state, (value) ->
                val result = state.value - value
                state.copy(value = result)
            }
            .onEvent(Multiplied::class.java) { state, (value) ->
                val result = state.value * value
                state.copy(value = result)
            }
            .onEvent(Divided::class.java) { state, (value) ->
                val result = state.value / value
                state.copy(value = result)
            }
            .onEvent(Cleared::class.java) { state, _ ->
                State(0.0)
            }
            .build()
}