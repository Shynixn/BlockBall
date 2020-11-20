package com.github.shynixn.blockball.api.business.enumeration

/**
 * Created by Shynixn 2019.
 * <p>
 * Version 1.2
 * <p>
 * MIT License
 * <p>
 * Copyright (c) 2019 by Shynixn
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
enum class MenuCommand(val key: MenuPageKey, private val internalCommand: String) {
    BACK(MenuPageKey.OPEN, "back"),
    CLOSE(MenuPageKey.OPEN, "close"),
    OPEN(MenuPageKey.OPEN, ""),
    ARENA_RELOAD(MenuPageKey.MAINCONFIGURATION, "reload"),
    OPEN_EDIT_ARENA(MenuPageKey.OPEN, "edit-arenas"),
    OPEN_DELETE_ARENA(MenuPageKey.OPEN, "delete-arenas"),

    ARENA_CREATE(MenuPageKey.MAINCONFIGURATION, "create-arena"),
    ARENA_EDIT(MenuPageKey.MAINCONFIGURATION, "edit-arena"),
    ARENA_DELETE(MenuPageKey.MAINCONFIGURATION, "delete-arena"),

    ARENA_MAINCONFIGURATION(MenuPageKey.MAINCONFIGURATION, "main-configuration"),
    ARENA_SETDISPLAYNAME(MenuPageKey.MAINCONFIGURATION, "set-displayname"),
    ARENA_ENABLE(MenuPageKey.MAINCONFIGURATION, "set-enable"),
    ARENA_SETBALLSPAWNPOINT(MenuPageKey.MAINCONFIGURATION, "set-ballspawnpoint"),

    ARENA_SETAREA(MenuPageKey.MAINCONFIGURATION, "set-area"),
    ARENA_SETGOALRED(MenuPageKey.MAINCONFIGURATION, "set-goalred"),
    ARENA_SETGOALBLUE(MenuPageKey.MAINCONFIGURATION, "set-goalblue"),
    ARENA_SAVE(MenuPageKey.MAINCONFIGURATION, "save-arena"),

    SETTINGS_OPEN(MenuPageKey.MAINSETTING, "open_settings"),

    MATCHTIMES_OPEN(MenuPageKey.MATCHTIMES, "open_matcht"),
    MATCHTIMES_CALLBACK(MenuPageKey.MATCHTIMES, "callback"),
    MATCHTIMES_CALLBACKCLOSETYPE(MenuPageKey.MATCHTIMES, "callbclot"),
    MATCHTIMES_CREATE(MenuPageKey.MATCHTIMES, "create"),
    MATCHTIMES_DELETE(MenuPageKey.MATCHTIMES, "del"),
    MATCHTIMES_DURATION(MenuPageKey.MATCHTIMES, "dur"),
    MATCHTIMES_SWITCHGOAL(MenuPageKey.MATCHTIMES, "swgo"),
    MATCHTIMES_CLOSECONDITION(MenuPageKey.MATCHTIMES, "clc"),
    MATCHTIMES_BALLAVAILABLE(MenuPageKey.MATCHTIMES, "bav"),
    MATCHTIMES_RESPAWN(MenuPageKey.MATCHTIMES, "res"),
    MATCHTIMES_STARTSUBTITLEMESSAGE(MenuPageKey.MATCHTIMES, "sstlm"),
    MATCHTIMES_STARTTITLEMESSAGE(MenuPageKey.MATCHTIMES, "stlm"),

    TEAM_RED_CONFIGURE(MenuPageKey.TEAMMETA, "team_red"),
    TEAM_BLUE_CONFIGURE(MenuPageKey.TEAMMETA, "team_blue"),

    TEAM_NAME(MenuPageKey.TEAMMETA, "name"),
    TEAM_PREFIX(MenuPageKey.TEAMMETA, "prefix"),
    TEAM_SPAWNPOINT(MenuPageKey.TEAMMETA, "spawnpoint"),
    TEAM_MINAMOUNT(MenuPageKey.TEAMMETA, "mina"),
    TEAM_MAXAMOUNT(MenuPageKey.TEAMMETA, "maxa"),
    TEAM_ARMOR(MenuPageKey.TEAMMETA, "armor"),
    TEAM_INVENTORY(MenuPageKey.TEAMMETA, "invent"),
    TEAM_WALKSPEED(MenuPageKey.TEAMMETA, "wspeed"),
    TEAM_POINTSGOAL(MenuPageKey.TEAMMETA, "ppgoal"),
    TEAM_POINTSDEATH(MenuPageKey.TEAMMETA, "ppdeath"),

    TEAM_SIGN(MenuPageKey.TEAMMETA, "sign"),

    TEAM_MSGLEAVE(MenuPageKey.TEAMMETA, "mleave"),
    TEAM_MSGJOIN(MenuPageKey.TEAMMETA, "mjoin"),

    TEAM_SCORETITLE(MenuPageKey.TEAMMETA, "sct"),
    TEAM_SCORESUBTITLE(MenuPageKey.TEAMMETA, "scst"),
    TEAM_WINTITLE(MenuPageKey.TEAMMETA, "wct"),
    TEAM_WINSUBTITLE(MenuPageKey.TEAMMETA, "wcst"),

    EFFECTS_OPEN(MenuPageKey.EFFECTS, "general"),
    EFFECTS_BOSSBAR(MenuPageKey.EFFECTS, "bossbar"),
    EFFECTS_HOLOGRAMS(MenuPageKey.EFFECTS, "holograms"),
    EFFECTS_SCOREBOARD(MenuPageKey.EFFECTS, "scoreboard"),

    MULTILINES_ANY(MenuPageKey.MULTIPLELINES, "any"),
    MULTILINES_ADD(MenuPageKey.MULTIPLELINES, "add"),
    MULTILINES_SET(MenuPageKey.MULTIPLELINES, "set"),
    MULTILINES_REMOVE(MenuPageKey.MULTIPLELINES, "rmv"),
    MULTILINES_SCOREBOARD(MenuPageKey.MULTIPLELINES, "show_scoreboard"),
    MULTILINES_HOLOGRAM(MenuPageKey.MULTIPLELINES, "show_hologram"),
    MULTILINES_TEAMSIGNTEMPLATE(MenuPageKey.MULTIPLELINES, "show_teamsign"),
    MULTILINES_HUBGAMEJOINMESSAGE(MenuPageKey.MULTIPLELINES, "hubgame_join"),
    MULTILINES_SPECTATEJOINMESSAGE(MenuPageKey.MULTIPLELINES, "minigame_spectate"),

    BALL_OPEN(MenuPageKey.BALL, "open"),
    BALL_SIZE_CALLBACK(MenuPageKey.BALL, "size-callback"),
    BALL_PARTICLEACTION_CALLBACK(MenuPageKey.BALL, "part-callback"),
    BALL_SOUNDACTION_CALLBACK(MenuPageKey.BALL, "sound-callback"),

    BALL_SKIN(MenuPageKey.BALL, "skin"),
    BALL_INTERACTION_HITBOX(MenuPageKey.BALL, "inhitbox"),
    BALL_KICKPASS_HITBOX(MenuPageKey.BALL, "kphitbox"),
    BALL_TOGGLE_CARRYABLE(MenuPageKey.BALL, "tgl-car"),
    BALL_TOGGLE_ALWAYSBOUNCE(MenuPageKey.BALL, "tgl-bounce"),
    BALL_TOGGLE_ROTATING(MenuPageKey.BALL, "tgl-rotate"),
    BALL_INTERACT_COOLDOWN(MenuPageKey.BALL, "intrkp"),
    BALL_KICKPASS_DELAY(MenuPageKey.BALL, "kpdelay"),

    BALLMOD_OPEN(MenuPageKey.BALLMODIFIER, "open"),
    BALLMOD_HORIZONTALTOUCH(MenuPageKey.BALLMODIFIER, "touch-horizontal"),
    BALLMOD_VERTICALTOUCH(MenuPageKey.BALLMODIFIER, "touch-vertical"),
    BALLMOD_SHOTVELOCITY(MenuPageKey.BALLMODIFIER, "shot-velocity"),
    BALLMOD_PASSVELOCITY(MenuPageKey.BALLMODIFIER, "pass-velocity"),
    BALLMOD_MAXSPIN(MenuPageKey.BALLMODIFIER, "max-spin"),
    BALLMOD_MAXPITCH(MenuPageKey.BALLMODIFIER, "max-pitch"),
    BALLMOD_MINPITCH(MenuPageKey.BALLMODIFIER, "min-pitch"),
    BALLMOD_DEFAULTPITCH(MenuPageKey.BALLMODIFIER, "default-pitch"),
    BALLMOD_GRAVITY(MenuPageKey.BALLMODIFIER, "gravity"),
    BALLMOD_AIRRESISTANCE(MenuPageKey.BALLMODIFIER, "air-resistance"),
    BALLMOD_ROLLINGRESISTANCE(MenuPageKey.BALLMODIFIER, "rolling-resistance"),

    MULTIPLEITEMS_BOSSBAR(MenuPageKey.MULTIPLEITEMS, "show_bossbar"),
    MULTIPLEITEMS_OPEN(MenuPageKey.MULTIPLEITEMS, "open"),
    MULTIPLEITEMS_REMOVE(MenuPageKey.MULTIPLEITEMS, "rmv"),

    SCOREBOARD_OPEN(MenuPageKey.SCOREBOARD, "open"),
    SCOREBOARD_TOGGLE(MenuPageKey.SCOREBOARD, "tgl"),
    SCOREBOARD_TITLE(MenuPageKey.SCOREBOARD, "title"),

    ABILITIES_OPEN(MenuPageKey.ABILITIES, "open"),

    DOUBLEJUMP_OPEN(MenuPageKey.DOUBLEJUMP, "open"),
    DOUBLEJUMP_TOGGLE(MenuPageKey.DOUBLEJUMP, "tgl"),
    DOUBLEJUMP_COOLDOWN(MenuPageKey.DOUBLEJUMP, "cool"),
    DOUBLEJUMP_VERTICAL_STRENGTH(MenuPageKey.DOUBLEJUMP, "vstr"),
    DOUBLEJUMP_HORIZONTAL_STRENGTH(MenuPageKey.DOUBLEJUMP, "hstr"),

    HOLOGRAM_OPEN(MenuPageKey.HOLOGRAM, "open"),
    HOLOGRAM_CALLBACK(MenuPageKey.HOLOGRAM, "callback"),
    HOLOGRAM_CREATE(MenuPageKey.HOLOGRAM, "create"),
    HOLOGRAM_DELETE(MenuPageKey.HOLOGRAM, "del"),
    HOLOGRAM_LOCATION(MenuPageKey.HOLOGRAM, "loc"),
    HOLOGRAM_TOGGLE(MenuPageKey.HOLOGRAM, "tgl"),

    BOSSBAR_OPEN(MenuPageKey.BOSSBAR, "open"),
    BOSSBAR_MESSAGE(MenuPageKey.BOSSBAR, "msg"),
    BOSSBAR_PERCENT(MenuPageKey.BOSSBAR, "prc"),
    BOSSBAR_TOGGLE(MenuPageKey.BOSSBAR, "tgl"),
    BOSSBAR_CALLBACKFLAGS(MenuPageKey.BOSSBAR, "fla"),
    BOSSBAR_CALLBACKCOLORS(MenuPageKey.BOSSBAR, "colo"),

    GAMESETTINGS_CALLBACK_BUKKITGAMEMODES(MenuPageKey.GAMESETTINGS, "bukkitgame"),
    GAMESETTINGS_OPEN(MenuPageKey.GAMESETTINGS, "open"),
    GAMESETTINGS_MAXSCORE(MenuPageKey.GAMESETTINGS, "maxscore"),
    GAMESETTINGS_MAXDURATION(MenuPageKey.GAMESETTINGS, "matchduration"),
    GAMESETTINGS_LOBBYDURATION(MenuPageKey.GAMESETTINGS, "lobbyduration"),
    GAMESETTINGS_BUNGEEKICKMESSAGE(MenuPageKey.GAMESETTINGS, "kickmes"),
    GAMESETTINGS_BUNGEELEAVEKICKMESSAGE(MenuPageKey.GAMESETTINGS, "levkikmes"),
    GAMESETTINGS_BUNGEEFALLBACKSERVER(MenuPageKey.GAMESETTINGS, "falbac"),
    GAMESETTINGS_REMAININGPLAYERSMESSAGE(MenuPageKey.GAMESETTINGS, "remainmes"),
    GAMESETTINGS_LEAVESPAWNPOINT(MenuPageKey.GAMESETTINGS, "lspawn"),
    GAMESETTINGS_LOBBYSPAWNPOINT(MenuPageKey.GAMESETTINGS, "lobspawn"),
    GAMESETTINGS_JOINMESSAGE(MenuPageKey.GAMESETTINGS, "joinm"),
    GAMESETTINGS_TOGGLE_EVENTEAMS(MenuPageKey.GAMESETTINGS, "tlgeven"),
    GAMESETTINGS_TOGGLE_RESETEMPTY(MenuPageKey.GAMESETTINGS, "resemp"),
    GAMESETTINGS_TOGGLE_INSTATFORCEFIELD(MenuPageKey.GAMESETTINGS, "iforce"),
    GAMESETTINGS_TOGGLE_TELEPORTONJOIN(MenuPageKey.GAMESETTINGS, "tglteleport"),

    SIGNS_OPEN(MenuPageKey.SIGNS, "open"),
    SIGNS_ADDTEAMRED(MenuPageKey.SIGNS, "adteamr"),
    SIGNS_ADDJOINANY(MenuPageKey.SIGNS, "addjoin"),
    SIGNS_LEAVE(MenuPageKey.SIGNS, "leave"),
    SIGNS_ADDTEAMBLUE(MenuPageKey.SIGNS, "adteamb"),

    PARTICLE_DOUBLEJUMP(MenuPageKey.PARTICLEFFECTS, "doub"),
    SOUND_DOUBLEJUMP(MenuPageKey.SOUNDEFFECTS, "doub"),

    PARTICLE_BALL(MenuPageKey.PARTICLEFFECTS, "ball-par"),
    PARTICLE_CALLBACK_EFFECTING(MenuPageKey.PARTICLEFFECTS, "caleffecting"),
    PARTICLE_CALLBACK_TYPE(MenuPageKey.PARTICLEFFECTS, "caltype"),
    PARTICLE_OPEN(MenuPageKey.PARTICLEFFECTS, "open"),
    PARTICLE_EFFECTING(MenuPageKey.PARTICLEFFECTS, "ef"),
    PARTICLE_TYPE(MenuPageKey.PARTICLEFFECTS, "type"),
    PARTICLE_AMOUNT(MenuPageKey.PARTICLEFFECTS, "amount"),
    PARTICLE_SPEED(MenuPageKey.PARTICLEFFECTS, "speed"),
    PARTICLE_OFFSET_X(MenuPageKey.PARTICLEFFECTS, "offx"),
    PARTICLE_OFFSET_Y(MenuPageKey.PARTICLEFFECTS, "offy"),
    PARTICLE_OFFSET_Z(MenuPageKey.PARTICLEFFECTS, "offz"),

    SOUND_BALL(MenuPageKey.SOUNDEFFECTS, "ball-sound"),
    SOUND_OPEN(MenuPageKey.SOUNDEFFECTS, "open"),
    SOUND_EFFECTING(MenuPageKey.SOUNDEFFECTS, "ef"),
    SOUND_TYPE(MenuPageKey.SOUNDEFFECTS, "type"),
    SOUND_VOLUME(MenuPageKey.SOUNDEFFECTS, "volume"),
    SOUND_PITCH(MenuPageKey.SOUNDEFFECTS, "pitch"),
    SOUND_CALLBACK_EFFECTING(MenuPageKey.SOUNDEFFECTS, "caleffecting"),
    SOUND_CALLBACK_TYPE(MenuPageKey.SOUNDEFFECTS, "caltype"),

    REWARD_OPEN(MenuPageKey.REWARDSPAGE, "open"),
    REWARD_EDIT_MONEY(MenuPageKey.REWARDSPAGE, "money"),
    REWARD_EDIT_COMMAND(MenuPageKey.REWARDSPAGE, "command"),
    REWARD_CALLBACK_MONEY(MenuPageKey.REWARDSPAGE, "calmoney"),
    REWARD_CALLBACK_COMMAND(MenuPageKey.REWARDSPAGE, "calcommand"),
    REWARD_CALLBACK_COMMANDMODE(MenuPageKey.REWARDSPAGE, "calcommode"),

    SPECTATOR_OPEN(MenuPageKey.SPECTATING, "open"),

    SPECTATE_OPEN(MenuPageKey.SPECTATE, "open"),
    SPECTATE_TOGGLE(MenuPageKey.SPECTATE, "tgl-sp"),
    SPECTATE_SPAWNPOINT(MenuPageKey.SPECTATE, "spawn"),

    MISC_OPEN(MenuPageKey.MISC, "open"),

    GAMEPROPERTIES_OPEN(MenuPageKey.GAMEEXTENSIONS, "open"),
    GAMEPROPERTIES_TOGGLE_BALLFORCEFIELD(MenuPageKey.GAMEEXTENSIONS, "tglbaf"),
    GAMEPROPERTIES_TOGGLE_DAMAGE(MenuPageKey.GAMEEXTENSIONS, "tglda"),
    GAMEPROPERTIES_TOGGLE_EVENTEAMS(MenuPageKey.GAMEEXTENSIONS, "tgle"),
    GAMEPROPERTIES_TOGGLE_TELEPORTBACK(MenuPageKey.GAMEEXTENSIONS, "tgltelback"),
    GAMEPROPERTIES_TOGGLE_KEEPINVENTORY(MenuPageKey.GAMEEXTENSIONS, "tglkeepinv"),
    GAMEPROPERTIES_TELEPORTBACKDELAY(MenuPageKey.GAMEEXTENSIONS, "telbackdelay"),

    NOTIFICATIONS_OPEN(MenuPageKey.NOTIFICATIONS, "open"),
    NOTIFICATIONS_TOGGLE(MenuPageKey.NOTIFICATIONS, "togl"),
    NOTIFICATIONS_RADIUS(MenuPageKey.NOTIFICATIONS, "radius"),

    AREAPROTECTION_OPEN(MenuPageKey.AREAPROTECTION, "open"),
    AREAPROTECTION_TOGGLE_ENTITYFORCEFIELD(MenuPageKey.AREAPROTECTION, "enforce"),
    AREAPROTECTION_SET_ENTITYFORCEFIELD(MenuPageKey.AREAPROTECTION, "enprot"),
    AREAPROTECTION_TOGGLE_PLAYERJOINFORCEFIELD(MenuPageKey.AREAPROTECTION, "plforce"),
    AREAPROTECTION_SET_PLAYERJOINFORCEFIELD(MenuPageKey.AREAPROTECTION, "plprot"),

    TEXTBOOK_OPEN(MenuPageKey.TEAMTEXTBOOK, "open"),
    TEXTBOOK_JOINMESSAGE(MenuPageKey.TEAMTEXTBOOK, "join"),
    TEXTBOOK_LEAVEMESSAGE(MenuPageKey.TEAMTEXTBOOK, "leave"),
    TEXTBOOK_SCORETIELE(MenuPageKey.TEAMTEXTBOOK, "scoretitle"),
    TEXTBOOK_SCORESUBTITLE(MenuPageKey.TEAMTEXTBOOK, "scorestitle"),
    TEXTBOOK_WINTIELE(MenuPageKey.TEAMTEXTBOOK, "wintitle"),
    TEXTBOOK_WINSUBTITLE(MenuPageKey.TEAMTEXTBOOK, "winstitle"),
    TEXTBOOK_DRAWTIELE(MenuPageKey.TEAMTEXTBOOK, "drawtitle"),
    TEXTBOOK_DRAWSUBTITLE(MenuPageKey.TEAMTEXTBOOK, "drawstitle"),

    TEMPLATE_OPEN(MenuPageKey.TEMPLATEPAGE, "open"),
    TEMPLATE_SELECT_CALLBACK(MenuPageKey.TEMPLATEPAGE, "calls"),

    LIST_TEMPLATES(MenuPageKey.LISTABLE, "show_templates"),
    LIST_BUKKITGAMESMODES(MenuPageKey.LISTABLE, "show_bukkitgamemodes"),
    LIST_BALL_SOUNDEFFECTS(MenuPageKey.LISTABLE, "show_ballsounds"),
    LIST_BALL_PARTICLEFFECTS(MenuPageKey.LISTABLE, "show_ballparticles"),
    LIST_LINES(MenuPageKey.LISTABLE, "show_lines"),
    LIST_COMMANDMODES(MenuPageKey.LISTABLE, "show_commandmodes"),
    LIST_BALLSIZES(MenuPageKey.LISTABLE, "show_ballsizes"),
    LIST_PARTICLE_EFFECTINGTYPES(MenuPageKey.LISTABLE, "particlef"),
    LIST_PARTICLE_TYPES(MenuPageKey.LISTABLE, "particles"),
    LIST_SOUND_TYPES(MenuPageKey.LISTABLE, "sounds"),
    LIST_REWARDED_MONEY(MenuPageKey.LISTABLE, "rewmoney"),
    LIST_REWARDED_COMMAND(MenuPageKey.LISTABLE, "recommand"),
    LIST_SOUND_EFFECTINGTYPES(MenuPageKey.LISTABLE, "soundf"),
    LIST_BOSSBARFLAGS(MenuPageKey.LISTABLE, "show_bossbarflags"),
    LIST_BOSSBARSTYLES(MenuPageKey.LISTABLE, "show_bossbarstyles"),
    LIST_BOSSBARCOLORS(MenuPageKey.LISTABLE, "show_bossbarcolors"),
    LIST_HOLOGRAMS(MenuPageKey.LISTABLE, "show_holograms"),
    LIST_GAMETYPES(MenuPageKey.LISTABLE, "show_gametypes"),
    LIST_MATCHTIMES(MenuPageKey.LISTABLE, "show_matchtimes"),
    LIST_MATCHCLOSETYPES(MenuPageKey.LISTABLE, "show_closetypes")
    ;

    /**
     * Gets the command.
     */
    val command: String
        get() {
            return "/blockball " + this.key.key + ' '.toString() + this.internalCommand + ' '.toString()
        }

    companion object {
        /**
         * Generates a menu command from args.
         */
        fun from(args: Array<out String>): MenuCommand? {
            if (args.isEmpty()) {
                return OPEN
            }

            if (args.isNotEmpty()) {
                for (command in MenuCommand.values()) {
                    if (command.key.key.equals(args[0], ignoreCase = true) && command.internalCommand.equals(args[1], ignoreCase = true)) {
                        return command
                    }
                }
            }

            return null
        }
    }
}
