package com.github.shynixn.blockball.bukkit.logic.business.commandexecutor.menu

import com.github.shynixn.blockball.api.business.service.TemplateService
import com.github.shynixn.blockball.api.persistence.entity.Template
import com.github.shynixn.blockball.bukkit.logic.business.helper.ChatBuilder
import com.google.inject.Inject
import org.bukkit.ChatColor
import org.bukkit.entity.Player

/**
 * Created by Shynixn 2018.
 * <p>
 * Version 1.2
 * <p>
 * MIT License
 * <p>
 * Copyright (c) 2018 by Shynixn
 * <p>
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * <p>
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * <p>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
class TemplateSettingsPage : Page(TemplateSettingsPage.ID, OpenPage.ID) {

    @Inject
    private lateinit var templateService: TemplateService

    companion object {
        /** Id of the page. */
        const val ID = 4812
    }

    /**
     * Returns the key of the command when this page should be executed.
     *
     * @return key
     */
    override fun getCommandKey(): PageKey {
        return PageKey.TEMPLATEPAGE
    }

    /**
     * Executes actions for this page.
     *
     * @param cache cache
     */
    override fun execute(player: Player, command: BlockBallCommand, cache: Array<Any?>, args: Array<String>): CommandResult {
        if (command == BlockBallCommand.TEMPLATE_OPEN) {
            templateService.copyTemplateFilesFromResources()
            val templates = templateService.getAvailableTemplates()

            if (templates.isEmpty()) {
                throw IllegalArgumentException("Amount of templates cannot be 0.")
            }

            var targetTemplate: Template = templates[0]
            templates.forEach { t ->
                if (t.name.endsWith("en")) {
                    targetTemplate = t
                }
            }

            cache[0] = templateService.generateArena(targetTemplate)
            cache[3] = targetTemplate
            return CommandResult.SUCCESS
        } else if (command == BlockBallCommand.TEMPLATE_SELECT_CALLBACK && args.size >= 3) {
            val templateID = args[2].toInt()
            val templates = templateService.getAvailableTemplates()

            templateService.copyTemplateFilesFromResources()
            cache[0] = templateService.generateArena(templates[templateID])
            cache[3] = templates[templateID]

            return CommandResult.SUCCESS
        }

        return super.execute(player, command, cache, args)
    }

    /**
     * Builds the page content.
     *
     * @param cache cache
     * @return content
     */
    override fun buildPage(cache: Array<Any?>): ChatBuilder {
        val template = cache[3] as Template

        return ChatBuilder()
                .component("- Create arena: ").builder()
                .component(" [create..]").setColor(ChatColor.AQUA)
                .setClickAction(ChatBuilder.ClickAction.RUN_COMMAND, BlockBallCommand.ARENA_CREATE.command)
                .setHoverText("Creates a new blockball arena from the selected template.")
                .builder().nextLine()
                .component("- Selected Template: ").builder().nextLine()
                .text("- ")
                .component(template.name + " by " + template.author).setColor(ChatColor.YELLOW).builder()
                .component(ClickableComponent.SELECT.text).setColor(ClickableComponent.SELECT.color)
                .setClickAction(ChatBuilder.ClickAction.RUN_COMMAND, BlockBallCommand.LIST_TEMPLATES.command)
                .setHoverText("Opens a selection box to change the current template.").builder()
    }
}