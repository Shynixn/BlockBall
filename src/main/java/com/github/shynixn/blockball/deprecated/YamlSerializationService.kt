package com.github.shynixn.blockball.deprecated

@Deprecated("Use FastXml serializer.")
interface YamlSerializationService {

    /**
     * Serializes the given [instance] to a key value pair map.
     */
    fun serialize(instance: Any): Map<String, Any?>

    /**
     * Deserializes the given [dataSource] into a new instance of the given [targetObjectClass].
     */
    fun <R> deserialize(targetObjectClass: Class<R>, dataSource: Map<String, Any?>) : R
}
