package com.github.shynixn.blockball.entity

import com.github.shynixn.blockball.api.persistence.entity.Template

class TemplateEntity(
        /**
         * Returns the name of the template.
         */
        override val name: String,
        /**
         * Returns the author of the template.
         */
        override val author: String,
        /**
         * Returns if the template belongs to an existing arena.
         */
        override val existingArena: Boolean) : Template
