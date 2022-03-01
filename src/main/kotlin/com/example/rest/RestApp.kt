package com.example.rest

import akka.actor.typed.ActorSystem
import akka.http.javadsl.Http
import akka.http.javadsl.server.Route

object RestApp {
    fun startServer(system: ActorSystem<*>, route: Route) {
        val config = system.settings().config()
        val host = config.getString("http.host")
        val port = config.getInt("http.port")

        val futureBinding = Http.get(system)
            .newServerAt(host, port)
            .bind(route)

        futureBinding.whenComplete { binding, exception ->
            if (binding != null) {
                val address = binding.localAddress()
                system.log().info("Server online at http://${address.hostString}:${address.port}/")
            } else {
                system.log().error("Failed to bind HTTP endpoint, terminating system", exception)
                system.terminate()
            }

        }
    }
}