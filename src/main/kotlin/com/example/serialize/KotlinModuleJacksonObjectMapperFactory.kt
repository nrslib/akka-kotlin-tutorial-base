package com.example.serialize

import akka.serialization.jackson.JacksonObjectMapperFactory
import com.fasterxml.jackson.core.JsonFactory
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule

// For deserializing Kotlin data class
class KotlinModuleJacksonObjectMapperFactory : JacksonObjectMapperFactory() {
    override fun newObjectMapper(bindingName: String?, jsonFactory: JsonFactory?)
    : ObjectMapper {
        return jacksonObjectMapper().registerKotlinModule()
    }
}