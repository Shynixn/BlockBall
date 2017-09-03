package com.github.shynixn.blockball.business.logic.arena;

import com.github.shynixn.blockball.api.entities.EventMeta;

import java.io.Serializable;
import java.util.*;

class EventMetaEntity implements Serializable, EventMeta {
    private static final String[] A = new String[0];
    private static final long serialVersionUID = 1L;

    private final List<String> registeredRedPlayers = new ArrayList<>();
    private final List<String> registeredBluePlayers = new ArrayList<>();
    private String referee;

    EventMetaEntity() {
        super();
    }

    EventMetaEntity(Map<String, Object> items) throws Exception {
        super();
        if (items.get("redplayers") != null) {
            this.registeredRedPlayers.addAll((Collection<? extends String>) items.get("redplayers"));
        }
        if (items.get("blueplayers") != null) {
            this.registeredBluePlayers.addAll((Collection<? extends String>) items.get("blueplayers"));
        }
        this.referee = (String) items.get("referee");
    }

    @Override
    public void addRegisteredRedPlayer(String name) {
        this.registeredRedPlayers.add(name);
    }

    @Override
    public void removeRegisteredRedPlayer(String pos) {
        if (this.registeredRedPlayers.contains(pos))
            this.registeredRedPlayers.remove(pos);
    }

    @Override
    public String[] getRegisteredRedPlayers() {
        return this.registeredRedPlayers.toArray(A);
    }

    @Override
    public void addRegisteredBluePlayer(String name) {
        this.registeredBluePlayers.add(name);
    }

    @Override
    public void removeRegisteredBluePlayer(String pos) {
        if (!this.registeredBluePlayers.contains(pos))
            this.registeredBluePlayers.remove(pos);
    }

    @Override
    public String[] getRegisteredBluePlayers() {
        return this.registeredBluePlayers.toArray(A);
    }

    @Override
    public String getReferee() {
        return this.referee;
    }

    @Override
    public void setReferee(String name) {
        this.referee = name;
    }

    @Override
    public Map<String, Object> serialize() {
        final Map<String, Object> map = new LinkedHashMap<>();
        map.put("referee", this.referee);
        map.put("redplayers", this.registeredRedPlayers);
        map.put("blueplayers", this.registeredBluePlayers);
        return map;
    }
}
