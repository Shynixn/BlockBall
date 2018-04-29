package com.github.shynixn.blockball.bukkit.logic.business.commandexecutor.menu;

/**
 * Created by Shynixn 2017.
 * <p>
 * Version 1.1
 * <p>
 * MIT License
 * <p>
 * Copyright (c) 2017 by Shynixn
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
public enum BlockBallCommand {
    BACK(PageKey.OPEN, "back"),
    CLOSE(PageKey.OPEN, "close"),
    OPEN(PageKey.OPEN, null),
    ARENA_RELOAD(PageKey.MAINCONFIGURATION, "reload"),
    OPEN_EDIT_ARENA(PageKey.OPEN, "edit-arenas"),
    OPEN_DELETE_ARENA(PageKey.OPEN, "delete-arenas"),

    ARENA_CREATE(PageKey.MAINCONFIGURATION, "create-arena"),
    ARENA_EDIT(PageKey.MAINCONFIGURATION, "edit-arena"),
    ARENA_DELETE(PageKey.MAINCONFIGURATION, "delete-arena"),

    ARENA_MAINCONFIGURATION(PageKey.MAINCONFIGURATION, "main-configuration"),
    ARENA_SETDISPLAYNAME(PageKey.MAINCONFIGURATION, "set-displayname"),
    ARENA_ENABLE(PageKey.MAINCONFIGURATION, "set-enable"),
    ARENA_SETBALLSPAWNPOINT(PageKey.MAINCONFIGURATION, "set-ballspawnpoint"),

    ARENA_SETAREA(PageKey.MAINCONFIGURATION, "set-area"),
    ARENA_SETGOALRED(PageKey.MAINCONFIGURATION, "set-goalred"),
    ARENA_SETGOALBLUE(PageKey.MAINCONFIGURATION, "set-goalblue"),
    ARENA_SAVE(PageKey.MAINCONFIGURATION, "save-arena"),

    SETTINGS_OPEN(PageKey.MAINSETTING, "open_settings"),

    TEAM_RED_CONFIGURE(PageKey.TEAMMETA, "team_red"),
    TEAM_BLUE_CONFIGURE(PageKey.TEAMMETA, "team_blue"),

    TEAM_NAME(PageKey.TEAMMETA, "name"),
    TEAM_PREFIX(PageKey.TEAMMETA, "prefix"),
    TEAM_SPAWNPOINT(PageKey.TEAMMETA, "spawnpoint"),
    TEAM_MINAMOUNT(PageKey.TEAMMETA, "mina"),
    TEAM_MAXAMOUNT(PageKey.TEAMMETA, "maxa"),
    TEAM_ARMOR(PageKey.TEAMMETA, "armor"),
    TEAM_WALKSPEED(PageKey.TEAMMETA, "wspeed"),

    TEAM_SIGN(PageKey.TEAMMETA, "sign"),

    TEAM_MSGLEAVE(PageKey.TEAMMETA, "mleave"),
    TEAM_MSGJOIN(PageKey.TEAMMETA, "mjoin"),

    TEAM_SCORETITLE(PageKey.TEAMMETA, "sct"),
    TEAM_SCORESUBTITLE(PageKey.TEAMMETA, "scst"),
    TEAM_WINTITLE(PageKey.TEAMMETA, "wct"),
    TEAM_WINSUBTITLE(PageKey.TEAMMETA, "wcst"),

    EFFECTS_OPEN(PageKey.EFFECTS, "general"),
    EFFECTS_BOSSBAR(PageKey.EFFECTS, "bossbar"),
    EFFECTS_HOLOGRAMS(PageKey.EFFECTS, "holograms"),
    EFFECTS_SCOREBOARD(PageKey.EFFECTS, "scoreboard"),

    MULTILINES_ANY(PageKey.MULTIPLELINES, "any"),
    MULTILINES_ADD(PageKey.MULTIPLELINES, "add"),
    MULTILINES_SET(PageKey.MULTIPLELINES, "set"),
    MULTILINES_REMOVE(PageKey.MULTIPLELINES, "rmv"),
    MULTILINES_SCOREBOARD(PageKey.MULTIPLELINES, "show_scoreboard"),
    MULTILINES_HOLOGRAM(PageKey.MULTIPLELINES, "show_hologram"),
    MULTILINES_TEAMSIGNTEMPLATE(PageKey.MULTIPLELINES, "show_teamsign"),
    MULTILINES_HUBGAMEJOINMESSAGE(PageKey.MULTIPLELINES, "hubgame_join"),

    BALL_OPEN(PageKey.BALL, "open"),
    BALL_SIZE_CALLBACK(PageKey.BALL, "size-callback"),
    BALL_PARTICLEACTION_CALLBACK(PageKey.BALL, "part-callback"),
    BALL_SOUNDACTION_CALLBACK(PageKey.BALL, "sound-callback"),

    BALL_SKIN(PageKey.BALL, "skin"),
    BALL_HITBOX(PageKey.BALL, "hitbox"),
    BALL_TOGGLE_CARRYABLE(PageKey.BALL, "tgl-car"),
    BALL_TOGGLE_ALWAYSBOUNCE(PageKey.BALL, "tgl-bounce"),
    BALL_TOGGLE_ROTATING(PageKey.BALL, "tgl-rotate"),

    BALLMOD_OPEN(PageKey.BALLMODIFIER, "open"),
    BALLMOD_HORIZONTALTOUCH(PageKey.BALLMODIFIER, "touch-horizontal"),
    BALLMOD_VERTICALTOUCH(PageKey.BALLMODIFIER, "touch-vertical"),
    BALLMOD_HORIZONTALKICK(PageKey.BALLMODIFIER, "kick-horizontal"),
    BALLMOD_VERTICALKICK(PageKey.BALLMODIFIER, "kick-vertical"),
    BALLMOD_HORIZONTALTHROW(PageKey.BALLMODIFIER, "throw-horizontal"),
    BALLMOD_VERTICALTHROW(PageKey.BALLMODIFIER, "throw-vertical"),
    BALLMOD_ROLLINGDISTANCE(PageKey.BALLMODIFIER, "rolling"),
    BALLMOD_GRAVITY(PageKey.BALLMODIFIER, "gravity"),

    MULTIPLEITEMS_BOSSBAR(PageKey.MULTIPLEITEMS, "show_bossbar"),
    MULTIPLEITEMS_OPEN(PageKey.MULTIPLEITEMS, "open"),
    MULTIPLEITEMS_REMOVE(PageKey.MULTIPLEITEMS, "rmv"),

    SCOREBOARD_OPEN(PageKey.SCOREBOARD, "open"),
    SCOREBOARD_TOGGLE(PageKey.SCOREBOARD, "tgl"),
    SCOREBOARD_TITLE(PageKey.SCOREBOARD, "title"),

    ABILITIES_OPEN(PageKey.ABILITIES, "open"),

    DOUBLEJUMP_OPEN(PageKey.DOUBLEJUMP, "open"),
    DOUBLEJUMP_TOGGLE(PageKey.DOUBLEJUMP, "tgl"),
    DOUBLEJUMP_COOLDOWN(PageKey.DOUBLEJUMP, "cool"),
    DOUBLEJUMP_VERTICAL_STRENGTH(PageKey.DOUBLEJUMP, "vstr"),
    DOUBLEJUMP_HORIZONTAL_STRENGTH(PageKey.DOUBLEJUMP, "hstr"),

    HOLOGRAM_OPEN(PageKey.HOLOGRAM, "open"),
    HOLOGRAM_CALLBACK(PageKey.HOLOGRAM, "callback"),
    HOLOGRAM_CREATE(PageKey.HOLOGRAM, "create"),
    HOLOGRAM_DELETE(PageKey.HOLOGRAM, "del"),
    HOLOGRAM_LOCATION(PageKey.HOLOGRAM, "loc"),
    HOLOGRAM_TOGGLE(PageKey.HOLOGRAM, "tgl"),

    BOSSBAR_OPEN(PageKey.BOSSBAR, "open"),
    BOSSBAR_MESSAGE(PageKey.BOSSBAR, "msg"),
    BOSSBAR_PERCENT(PageKey.BOSSBAR, "prc"),
    BOSSBAR_TOGGLE(PageKey.BOSSBAR, "tgl"),
    BOSSBAR_CALLBACKFLAGS(PageKey.BOSSBAR, "fla"),
    BOSSBAR_CALLBACKCOLORS(PageKey.BOSSBAR, "colo"),

    GAMESETTINGS_CALLBACK_BUKKITGAMEMODES(PageKey.GAMESETTINGS, "bukkitgame"),
    GAMESETTINGS_OPEN(PageKey.GAMESETTINGS, "open"),
    GAMESETTINGS_MAXSCORE(PageKey.GAMESETTINGS, "maxscore"),
    GAMESETTINGS_MAXDURATION(PageKey.GAMESETTINGS, "matchduration"),
    GAMESETTINGS_LOBBYDURATION(PageKey.GAMESETTINGS, "lobbyduration"),
    GAMESETTINGS_BUNGEEKICKMESSAGE(PageKey.GAMESETTINGS, "kickmes"),
    GAMESETTINGS_REMAININGPLAYERSMESSAGE(PageKey.GAMESETTINGS, "remainmes"),
    GAMESETTINGS_LEAVESPAWNPOINT(PageKey.GAMESETTINGS, "lspawn"),
    GAMESETTINGS_LOBBYSPAWNPOINT(PageKey.GAMESETTINGS, "lobspawn"),
    GAMESETTINGS_JOINMESSAGE(PageKey.GAMESETTINGS, "joinm"),
    GAMESETTINGS_TOGGLE_EVENTEAMS(PageKey.GAMESETTINGS, "tlgeven"),
    GAMESETTINGS_TOGGLE_RESETEMPTY(PageKey.GAMESETTINGS, "resemp"),
    GAMESETTINGS_TOGGLE_INSTATFORCEFIELD(PageKey.GAMESETTINGS, "iforce"),

    SIGNS_OPEN(PageKey.SIGNS, "open"),
    SIGNS_ADDTEAMRED(PageKey.SIGNS, "adteamr"),
    SIGNS_ADDJOINANY(PageKey.SIGNS, "addjoin"),
    SIGNS_LEAVE(PageKey.SIGNS, "leave"),
    SIGNS_ADDTEAMBLUE(PageKey.SIGNS, "adteamb"),

    PARTICLE_DOUBLEJUMP(PageKey.PARTICLEFFECTS, "doub"),
    SOUND_DOUBLEJUMP(PageKey.SOUNDEFFECTS, "doub"),

    PARTICLE_BALL(PageKey.PARTICLEFFECTS, "ball-par"),
    PARTICLE_CALLBACK_EFFECTING(PageKey.PARTICLEFFECTS,"caleffecting"),
    PARTICLE_CALLBACK_TYPE(PageKey.PARTICLEFFECTS,"caltype"),
    PARTICLE_OPEN(PageKey.PARTICLEFFECTS, "open"),
    PARTICLE_EFFECTING(PageKey.PARTICLEFFECTS, "ef"),
    PARTICLE_TYPE(PageKey.PARTICLEFFECTS, "type"),
    PARTICLE_AMOUNT(PageKey.PARTICLEFFECTS, "amount"),
    PARTICLE_SPEED(PageKey.PARTICLEFFECTS, "speed"),
    PARTICLE_OFFSET_X(PageKey.PARTICLEFFECTS, "offx"),
    PARTICLE_OFFSET_Y(PageKey.PARTICLEFFECTS, "offy"),
    PARTICLE_OFFSET_Z(PageKey.PARTICLEFFECTS, "offz"),

    SOUND_BALL(PageKey.SOUNDEFFECTS, "ball-sound"),
    SOUND_OPEN(PageKey.SOUNDEFFECTS, "open"),
    SOUND_EFFECTING(PageKey.SOUNDEFFECTS, "ef"),
    SOUND_TYPE(PageKey.SOUNDEFFECTS, "type"),
    SOUND_VOLUME(PageKey.SOUNDEFFECTS, "volume"),
    SOUND_PITCH(PageKey.SOUNDEFFECTS, "pitch"),
    SOUND_CALLBACK_EFFECTING(PageKey.SOUNDEFFECTS,"caleffecting"),
    SOUND_CALLBACK_TYPE(PageKey.SOUNDEFFECTS,"caltype"),

    REWARD_OPEN(PageKey.REWARDSPAGE, "open"),
    REWARD_EDIT_MONEY(PageKey.REWARDSPAGE, "money"),
    REWARD_EDIT_COMMAND(PageKey.REWARDSPAGE, "command"),
    REWARD_CALLBACK_MONEY(PageKey.REWARDSPAGE, "calmoney"),
    REWARD_CALLBACK_COMMAND(PageKey.REWARDSPAGE, "calcommand"),
    REWARD_CALLBACK_COMMANDMODE(PageKey.REWARDSPAGE, "calcommode"),

    MISC_OPEN(PageKey.MISC, "open"),

    GAMEPROPERTIES_OPEN(PageKey.GAMEEXTENSIONS, "open"),
    GAMEPROPERTIES_TOGGLE_DAMAGE(PageKey.GAMEEXTENSIONS, "tglda"),
    GAMEPROPERTIES_TOGGLE_EVENTEAMS(PageKey.GAMEEXTENSIONS, "tgle"),
    GAMEPROPERTIES_TOGGLE_TELEPORTBACK(PageKey.GAMEEXTENSIONS, "tgltelback"),
    GAMEPROPERTIES_TELEPORTBACKDELAY(PageKey.GAMEEXTENSIONS, "telbackdelay"),

    AREAPROTECTION_OPEN(PageKey.AREAPROTECTION, "open"),
    AREAPROTECTION_TOGGLE_ENTITYFORCEFIELD(PageKey.AREAPROTECTION, "enforce"),
    AREAPROTECTION_SET_ENTITYFORCEFIELD(PageKey.AREAPROTECTION, "enprot"),
    AREAPROTECTION_TOGGLE_PLAYERJOINFORCEFIELD(PageKey.AREAPROTECTION, "plforce"),
    AREAPROTECTION_SET_PLAYERJOINFORCEFIELD(PageKey.AREAPROTECTION, "plprot"),

    TEXTBOOK_OPEN(PageKey.TEAMTEXTBOOK, "open"),
    TEXTBOOK_JOINMESSAGE(PageKey.TEAMTEXTBOOK, "join"),
    TEXTBOOK_LEAVEMESSAGE(PageKey.TEAMTEXTBOOK, "leave"),
    TEXTBOOK_SCORETIELE(PageKey.TEAMTEXTBOOK, "scoretitle"),
    TEXTBOOK_SCORESUBTITLE(PageKey.TEAMTEXTBOOK, "scorestitle"),
    TEXTBOOK_WINTIELE(PageKey.TEAMTEXTBOOK, "wintitle"),
    TEXTBOOK_WINSUBTITLE(PageKey.TEAMTEXTBOOK, "winstitle"),
    TEXTBOOK_DRAWTIELE(PageKey.TEAMTEXTBOOK, "drawtitle"),
    TEXTBOOK_DRAWSUBTITLE(PageKey.TEAMTEXTBOOK, "drawstitle"),

    LIST_BUKKITGAMESMODES(PageKey.LISTABLE, "show_bukkitgamemodes"),
    LIST_BALL_SOUNDEFFECTS(PageKey.LISTABLE, "show_ballsounds"),
    LIST_BALL_PARTICLEFFECTS(PageKey.LISTABLE, "show_ballparticles"),
    LIST_LINES(PageKey.LISTABLE, "show_lines"),
    LIST_COMMANDMODES(PageKey.LISTABLE, "show_commandmodes"),
    LIST_BALLSIZES(PageKey.LISTABLE, "show_ballsizes"),
    LIST_PARTICLE_EFFECTINGTYPES(PageKey.LISTABLE, "particlef"),
    LIST_PARTICLE_TYPES(PageKey.LISTABLE, "particles"),
    LIST_SOUND_TYPES(PageKey.LISTABLE, "sounds"),
    LIST_REWARDED_MONEY(PageKey.LISTABLE, "rewmoney"),
    LIST_REWARDED_COMMAND(PageKey.LISTABLE, "recommand"),
    LIST_SOUND_EFFECTINGTYPES(PageKey.LISTABLE, "soundf"),
    LIST_BOSSBARFLAGS(PageKey.LISTABLE, "show_bossbarflags"),
    LIST_BOSSBARSTYLES(PageKey.LISTABLE, "show_bossbarstyles"),
    LIST_BOSSBARCOLORS(PageKey.LISTABLE, "show_bossbarcolors"),
    LIST_HOLOGRAMS(PageKey.LISTABLE, "show_holograms"),
    LIST_GAMETYPES(PageKey.LISTABLE, "show_gametypes"),;

    private final PageKey key;
    private final String command;

    BlockBallCommand(PageKey key, String command) {
        this.key = key;
        this.command = command;
    }

    public PageKey getKey() {
        return this.key;
    }

    public String getCommand() {
        return "/blockball " + this.key.getKey() + ' ' + this.command + ' ';
    }

    public static BlockBallCommand from(String[] args) {
        if (args.length == 0)
            return OPEN;
        if (args.length > 0) {
            for (final BlockBallCommand command : BlockBallCommand.values()) {
                if (command.key.getKey() != null) {
                    if (command.key.getKey().equalsIgnoreCase(args[0])) {
                        if (command.command != null) {
                            if (command.command.equalsIgnoreCase(args[1])) {
                                return command;
                            }
                        }
                    }
                }
            }
        }
        return null;
    }
}
