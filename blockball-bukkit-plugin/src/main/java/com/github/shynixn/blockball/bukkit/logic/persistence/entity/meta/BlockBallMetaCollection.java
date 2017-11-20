package com.github.shynixn.blockball.bukkit.logic.persistence.entity.meta;

import com.github.shynixn.blockball.api.business.enumeration.Team;
import com.github.shynixn.blockball.api.persistence.entity.BallMeta;
import com.github.shynixn.blockball.api.persistence.entity.BungeeCordLobbyMeta;
import com.github.shynixn.blockball.api.persistence.entity.HubLobbyMeta;
import com.github.shynixn.blockball.api.persistence.entity.Persistenceable;
import com.github.shynixn.blockball.api.persistence.entity.meta.MetaDataTransaction;
import com.github.shynixn.blockball.api.persistence.entity.meta.display.HologramMeta;
import com.github.shynixn.blockball.api.persistence.entity.meta.display.ScoreboardMeta;
import com.github.shynixn.blockball.api.persistence.entity.meta.misc.CustomizingMeta;
import com.github.shynixn.blockball.api.persistence.entity.meta.misc.TeamMeta;
import com.github.shynixn.blockball.bukkit.logic.persistence.entity.BallProperties;
import com.github.shynixn.blockball.bukkit.logic.persistence.entity.PersistenceObject;
import com.github.shynixn.blockball.bukkit.logic.persistence.entity.meta.display.HologramBuilder;
import com.github.shynixn.blockball.bukkit.logic.persistence.entity.meta.display.ScoreboardProperties;
import com.github.shynixn.blockball.bukkit.logic.persistence.entity.meta.lobby.BungeeCordLobbyProperties;
import com.github.shynixn.blockball.bukkit.logic.persistence.entity.meta.lobby.HubLobbyProperties;
import com.github.shynixn.blockball.bukkit.logic.persistence.entity.meta.misc.CustomizingProperties;
import com.github.shynixn.blockball.bukkit.logic.persistence.entity.meta.misc.RewardProperties;
import com.github.shynixn.blockball.bukkit.logic.persistence.entity.meta.misc.TeamProperties;
import com.github.shynixn.blockball.lib.YamlSerializer;

import java.util.*;

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
public class BlockBallMetaCollection implements MetaDataTransaction {

    private final Map<Class<?>, Object> metaCollection = new HashMap<>();

    @YamlSerializer.YamlSerialize(orderNumber = 1, value = "ball-meta")
    private final BallProperties ballProperties = new BallProperties();

    @YamlSerializer.YamlSerialize(orderNumber = 2, value = "red-team-meta")
    private final TeamProperties redTeamProperties = new TeamProperties();

    @YamlSerializer.YamlSerialize(orderNumber = 3, value = "blue-team-meta")
    private final TeamProperties blueTeamProperties = new TeamProperties();

    @YamlSerializer.YamlSerialize(orderNumber = 4, value = "holograms")
    private final List<HologramBuilder> holograms = new ArrayList<>();

    @YamlSerializer.YamlSerialize(orderNumber = 5, value = "hubmeta")
    private final HubLobbyMeta hubLobbyMeta = new HubLobbyProperties();

    @YamlSerializer.YamlSerialize(orderNumber = 5, value = "bungeecordlobby")
    private final BungeeCordLobbyMeta bungeeCordLobbyMeta = new BungeeCordLobbyProperties();

    @YamlSerializer.YamlSerialize(orderNumber = 6, value = "customizing")
    private final CustomizingMeta customizingMeta = new CustomizingProperties();

    @YamlSerializer.YamlSerialize(orderNumber = 7, value = "scoreboard-meta")
    private final ScoreboardProperties gameScoreboard = new ScoreboardProperties("title", new String[]{"easy"});

    public BlockBallMetaCollection() {
        this.metaCollection.put(BallMeta.class, this.ballProperties);
        this.metaCollection.put(TeamMeta[].class, new TeamMeta[]{this.redTeamProperties, this.blueTeamProperties});
        this.metaCollection.put(HologramMeta.class, this.holograms);
        this.metaCollection.put(CustomizingMeta.class, customizingMeta);
        this.metaCollection.put(ScoreboardMeta.class, this.gameScoreboard);
        this.metaCollection.put(HubLobbyMeta.class, this.hubLobbyMeta);
        this.metaCollection.put(BungeeCordLobbyMeta.class, this.bungeeCordLobbyMeta);
    }

    /**
     * Returns the meta data list
     *
     * @param metaClass metaclass
     * @param <T>       t
     * @return optMeta
     */
    @Override
    public <T> Optional<List<T>> findList(Class<T> metaClass) {
        if (this.metaCollection.containsKey(metaClass)) {
            return Optional.of((List<T>) this.metaCollection.get(metaClass));
        }
        return Optional.empty();
    }

    /**
     * Searches for the given MetaData class.
     *
     * @param metaClass metaDataClass
     * @return optMeta
     */
    @Override
    public <T> Optional<T> findByTeam(Class<T[]> metaClass, Team team) {
        if (this.metaCollection.containsKey(metaClass)) {
            final Object data = this.metaCollection.get(metaClass);
            if (data.getClass().isArray()) {
                final Object[] dataArray = (Object[]) data;
                if (team == Team.RED) {
                    return Optional.of((T) dataArray[0]);
                } else {
                    return Optional.of((T) dataArray[1]);
                }
            }
        }
        return Optional.empty();
    }

    /**
     * Searches for the given MetaData class.
     *
     * @param metaClass metaDataClass
     * @return optMeta
     */
    @Override
    public <T> Optional<T> find(Class<T> metaClass) {
        if (this.metaCollection.containsKey(metaClass)) {
            return Optional.of((T) this.metaCollection.get(metaClass));
        }
        return Optional.empty();
    }
}
