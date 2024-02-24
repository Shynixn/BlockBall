package com.github.shynixn.blockball.entity

class Template(
        /**
         * Returns the name of the template.
         */
        val name: String,
        /**
         * Returns the author of the template.
         */
        val author: String,
        /**
         * Returns if the template belongs to an existing arena.
         */
        val existingArena: Boolean)
