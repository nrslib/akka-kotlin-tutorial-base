package com.example.rest

import akka.actor.typed.ActorRef
import akka.actor.typed.ActorSystem
import akka.actor.typed.javadsl.AskPattern
import akka.http.javadsl.marshallers.jackson.Jackson
import akka.http.javadsl.model.StatusCodes
import akka.http.javadsl.server.Directives.*
import akka.http.javadsl.server.PathMatchers.segment
import com.example.calc.Calculator

class CalculatorRoutes(private val system: ActorSystem<*>, private val calculator: ActorRef<Calculator.Command>) {
    val timeout = system.settings().config().getDuration("calculator.ask-timeout")

    fun routes() = calculatorRoute()

    private fun calculatorRoute() =
        pathPrefix("calculator") {
            concat(
                get(),
                postAdd(),
                postSubtract(),
                postMultiply(),
                postDivide()
            )
        }

    private fun postAdd() =
        post {
            path(segment("add").slash(segment())) { valueText ->
                val value = valueText.toDouble()

                val command = Calculator.Add(value)
                calculator.tell(command)

                complete(StatusCodes.OK)
            }
        }

    private fun postSubtract() =
        post {
            path(segment("subtract").slash(segment())) { valueText ->
                val value = valueText.toDouble()

                val command = Calculator.Subtract(value)
                calculator.tell(command)

                complete(StatusCodes.OK)
            }
        }

    private fun postMultiply() =
        post {
            path(segment("multiply").slash(segment())) { valueText ->
                val value = valueText.toDouble()

                val command = Calculator.Multiply(value)
                calculator.tell(command)

                complete(StatusCodes.OK)
            }
        }

    private fun postDivide() =
        post {
            path(segment("divide").slash(segment())) { valueText ->
                val value = valueText.toDouble()

                val command = Calculator.Divide(value)
                calculator.tell(command)

                complete(StatusCodes.OK)
            }
        }

    private fun get() =
        get {
            onSuccess({
                AskPattern.ask(
                    calculator,
                    { replyTo: ActorRef<Calculator.GetDataResponse> -> Calculator.GetData(replyTo) },
                    timeout,
                    system.scheduler()
                )
            }) {
                completeOK(it.value, Jackson.marshaller())
            }
        }
}