package com.github.shynixn.blockball.bukkit.logic.persistence.entity;

import com.github.shynixn.blockball.api.persistence.entity.BallMeta;
import com.github.shynixn.blockball.api.persistence.entity.SoundMeta;
import com.github.shynixn.blockball.api.entities.LightParticle;
import com.github.shynixn.blockball.lib.ParticleEffect;
import com.github.shynixn.blockball.lib.SParticle;
import org.bukkit.configuration.MemorySection;

import java.util.LinkedHashMap;
import java.util.Map;

class BallMetaEntity implements BallMeta {
    private String ballSkin = "http://textures.minecraft.net/texture/8e4a70b7bbcd7a8c322d522520491a27ea6b83d60ecf961d2b4efbbf9f605d";

    private LightParticle playerTeamBlueHitParticle = new SParticle(ParticleEffect.REDSTONE, 1, 1, 0, 0, 1).setColors(0, 0, 255);
    private LightParticle playerTeamRedHitParticle = new SParticle(ParticleEffect.REDSTONE, 1, 1, 0, 0, 0).setColors(255, 0, 0);

    private LightParticle ballSpawnParticle = new SParticle(ParticleEffect.SMOKE_LARGE, 4, 0.0002, 2, 2, 2);
    private SoundMeta ballSpawnSound = new SoundBuilder("NOTE_BASS", 1.0, 1.0);
    private LightParticle ballGoalParticle = new SParticle(ParticleEffect.NOTE, 4, 0.0002, 2, 2, 2).setNoteColor(2);
    private SoundMeta ballGoalSound = new SoundBuilder("NOTE_PLING", 1.0, 2.0);

    private SoundMeta genericHitSound = new SoundBuilder("ZOMBIE_WOOD", 1.0, 1.0);
    private LightParticle genericHitParticle = new SParticle(ParticleEffect.EXPLOSION_HUGE, 1, 0.0002, 0.01, 0.01, 0.01);

    private int ballSpawnTime = 3;
    private double horizontalStrength = 1.8;
    private double verticalStrength = 0.8;
    private boolean rotating;

    BallMetaEntity() {
        super();
    }

    BallMetaEntity(Map<String, Object> items) throws Exception {
        super();
        this.ballSkin = (String) items.get("skin");
        this.rotating = !(boolean) items.get("rotating");
        this.horizontalStrength = (double) items.get("horizontal-strength");
        this.verticalStrength = (double) items.get("vertical-strength");
        this.ballSpawnTime = (int) items.get("spawnduration");
        this.genericHitParticle = new SParticle(((MemorySection) items.get("particles.generic-hit")).getValues(true));
        this.playerTeamRedHitParticle = new SParticle(((MemorySection) items.get("particles.red-hit")).getValues(true));
        this.playerTeamBlueHitParticle = new SParticle(((MemorySection) items.get("particles.blue-hit")).getValues(true));
        this.ballSpawnParticle = new SParticle(((MemorySection) items.get("particles.spawn")).getValues(true));
        this.ballGoalParticle = new SParticle(((MemorySection) items.get("particles.goal")).getValues(true));
        this.genericHitSound = new SoundBuilder(((MemorySection) items.get("sounds.generic-hit")).getValues(true));
        this.ballSpawnSound = new SoundBuilder(((MemorySection) items.get("sounds.spawn")).getValues(true));
        this.ballGoalSound = new SoundBuilder(((MemorySection) items.get("sounds.goal")).getValues(true));
    }

    @Override
    public int getBallSpawnTime() {
        return this.ballSpawnTime;
    }

    @Override
    public double getHorizontalStrength() {
        return this.horizontalStrength;
    }

    @Override
    public void setHorizontalStrength(double horizontalStrength) {
        this.horizontalStrength = horizontalStrength;
    }

    @Override
    public double getVerticalStrength() {
        return this.verticalStrength;
    }

    @Override
    public void setVerticalStrength(double verticalStrength) {
        this.verticalStrength = verticalStrength;
    }

    @Override
    public void setBallSpawnTime(int ballSpawnTime) {
        this.ballSpawnTime = ballSpawnTime;
    }

    @Override
    public String getBallSkin() {
        return this.ballSkin;
    }

    @Override
    public void setBallSkin(String ballSkin) {
        this.ballSkin = ballSkin;
    }

    @Override
    public LightParticle getPlayerTeamBlueHitParticle() {
        return this.playerTeamBlueHitParticle;
    }

    @Override
    public void setPlayerTeamBlueHitParticle(LightParticle playerTeamBlueHitParticle) {
        this.playerTeamBlueHitParticle = playerTeamBlueHitParticle;
    }

    @Override
    public LightParticle getPlayerTeamRedHitParticle() {
        return this.playerTeamRedHitParticle;
    }

    @Override
    public void setPlayerTeamRedHitParticle(LightParticle playerTeamRedHitParticle) {
        this.playerTeamRedHitParticle = playerTeamRedHitParticle;
    }

    @Override
    public LightParticle getBallSpawnParticle() {
        return this.ballSpawnParticle;
    }

    @Override
    public void setBallSpawnParticle(LightParticle ballSpawnParticle) {
        this.ballSpawnParticle = ballSpawnParticle;
    }

    @Override
    public SoundMeta getBallSpawnSound() {
        return this.ballSpawnSound;
    }

    @Override
    public void setBallSpawnSound(SoundMeta ballSpawnSound) {
        this.ballSpawnSound = ballSpawnSound;
    }

    @Override
    public LightParticle getBallGoalParticle() {
        return this.ballGoalParticle;
    }

    @Override
    public void setBallGoalParticle(LightParticle ballGoalParticle) {
        this.ballGoalParticle = ballGoalParticle;
    }

    @Override
    public SoundMeta getBallGoalSound() {
        return this.ballGoalSound;
    }

    @Override
    public void setBallGoalSound(SoundMeta ballGoalSound) {
        this.ballGoalSound = ballGoalSound;
    }

    @Override
    public SoundMeta getGenericHitSound() {
        return this.genericHitSound;
    }

    @Override
    public void setGenericHitSound(SoundMeta genericHitSound) {
        this.genericHitSound = genericHitSound;
    }

    @Override
    public LightParticle getGenericHitParticle() {
        return this.genericHitParticle;
    }

    @Override
    public void setGenericHitParticle(LightParticle genericHitParticle) {
        this.genericHitParticle = genericHitParticle;
    }

    @Override
    public boolean isRotating() {
        return !this.rotating;
    }

    @Override
    public void setRotating(boolean rotating) {
        this.rotating = !rotating;
    }

    @Override
    public Map<String, Object> serialize() {
        final Map<String, Object> map = new LinkedHashMap<>();
        final Map<String, Object> tmp1 = new LinkedHashMap<>();
        final Map<String, Object> tmp2 = new LinkedHashMap<>();

        map.put("skin", this.ballSkin);
        map.put("rotating", !this.rotating);
        map.put("horizontal-strength", this.horizontalStrength);
        map.put("vertical-strength", this.verticalStrength);
        map.put("spawnduration", this.ballSpawnTime);
        tmp1.put("generic-hit", this.genericHitParticle.serialize());
        tmp1.put("red-hit", this.playerTeamRedHitParticle.serialize());
        tmp1.put("blue-hit", this.playerTeamBlueHitParticle.serialize());
        tmp1.put("spawn", this.ballSpawnParticle.serialize());
        tmp1.put("goal", this.ballGoalParticle.serialize());
        map.put("particles", tmp1);

        tmp2.put("generic-hit", this.genericHitSound.serialize());
        tmp2.put("spawn", this.ballSpawnSound.serialize());
        tmp2.put("goal", this.ballGoalSound.serialize());
        map.put("sounds", tmp2);
        return map;
    }

    /**
     * Sets if the ball rotation is enabled
     *
     * @param enabled enabled
     */
    @Override
    public void setRotationEnabled(boolean enabled) {
        this.rotating = !enabled;
    }

    /**
     * Returns if the ball rotation is enabled
     *
     * @return enabled
     */
    @Override
    public boolean isRotationEnabled() {
        return !this.rotating;
    }

    /**
     * Returns the sound played when the ball spawns
     *
     * @return meta
     */
    @Override
    public SoundMeta getSpawnSound() {
        return this.ballSpawnSound;
    }

    /**
     * Sets the skin of the ball. Can be a skinUrl or the name of the skin owner
     *
     * @param skin skin
     */
    @Override
    public void setSkin(String skin) {
        this.ballSkin = skin;
    }

    /**
     * Returns the skin of the ball
     *
     * @return skin
     */
    @Override
    public String getSkin() {
        return this.ballSkin;
    }

    /**
     * Returns the id of the object
     *
     * @return id
     */
    @Override
    public long getId() {
        return this.hashCode();
    }

    /**
     * Clones the current object
     *
     * @return meta
     */
    @Override
    public BallMeta clone() {
        try {
            return new BallMetaEntity(this.serialize());
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }
}
