package com.example.rest

import akka.actor.setup.ActorSystemSetup
import akka.actor.typed.ActorSystem
import akka.serialization.jackson.JacksonObjectMapperProviderSetup
import com.example.serialize.KotlinModuleJacksonObjectMapperFactory

fun main() {
    val setup = ActorSystemSetup.empty().withSetup(
        JacksonObjectMapperProviderSetup(
            KotlinModuleJacksonObjectMapperFactory()
        )
    )

    ActorSystem.create(RestGuardian.create(), "tutorial", setup)
}