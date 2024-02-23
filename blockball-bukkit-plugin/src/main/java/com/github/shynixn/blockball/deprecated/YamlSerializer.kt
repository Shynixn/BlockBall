package com.github.shynixn.blockball.deprecated

interface YamlSerializer<I, O> {
    /**
     * Gets called on serialization.
     */
    fun onSerialization(item: I): O

    /**
     * Gets called on Deserialization.
     */
    fun onDeserialization(item: O): I
}
