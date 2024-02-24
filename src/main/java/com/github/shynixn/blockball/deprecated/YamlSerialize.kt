package com.github.shynixn.blockball.deprecated

import kotlin.reflect.KClass

@Deprecated("Use FastXml serializer.")
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FIELD)
annotation class YamlSerialize(
        /**
         * Name of the target property.
         */
        val value: String,
        /**
         * Order number in the target file.
         */
        val orderNumber: Int,

        /**
         * Custom serialization class.
         */
        val customserializer: KClass<*> = Any::class,
        /**
         * Optional implementation of the class if the type is specified as interface.
         */
        val implementation: KClass<*> = Any::class
)
