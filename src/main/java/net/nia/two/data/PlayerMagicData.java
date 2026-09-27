package net.nia.witchinghour.data;

import dev.onyxstudios.cca.api.v3.component.Component;
import dev.onyxstudios.cca.api.v3.component.sync.AutoSyncedComponent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;
import net.minecraft.util.math.BlockPos;
import org.joml.Vector3f;

import java.util.*;

public class PlayerMagicData implements Component, AutoSyncedComponent {
    private int mana = 0;
    private int totalExp = 0;
    private int magicLevel = 0;
    private String playerName = "";
    private String covenName = "";

    // Dynamic spell stats
    private final Map<String, Vector3f> boltColor = new HashMap<>();

    private final Map<String, Integer> spellExp = new HashMap<>();
    private final Map<String, Integer> spellLevel = new HashMap<>();

    private final List<String> knownSpells = new ArrayList<>();


    // Optional: categories like "elemental", "occult", etc.
    private final Map<String, Integer> categoryExp = new HashMap<>();
    private final Map<String, Integer> categoryLevel = new HashMap<>();

    private final PlayerEntity player;

    private double manaRegenMomentum = 0;
    private int regenDelayTicks = 0;
    private double manaFraction = 0;
    private int regenTimer = 0;       // counts down to next regen tick
    private int regenInterval = 40;   // starting interval (40 ticks = 2 seconds)

    private final Map<String, StoredLocation> storedPosition = new HashMap<>();

    private String shapeshiftForm = ""; // entity ID, e.g. "minecraft:zombie"

    public static class StoredLocation {
        public final String dimension;
        public final BlockPos pos;

        public StoredLocation(String dimension, BlockPos pos) {
            this.dimension = dimension;
            this.pos = pos;
        }
    }

    // --- StoredPos data ---
    public StoredLocation getPos(String key) {
        return storedPosition.get(key);
    }

    public Set<Map.Entry<String, StoredLocation>> getAllPos() {
        return storedPosition.entrySet();
    }

    public void setPos(String key, String dimension, BlockPos value) {
        storedPosition.put(key, new StoredLocation(dimension, value));
        ModComponents.PLAYER_MAGIC.sync(player);
    }

    public void removePos(String key) {
        storedPosition.remove(key);
        ModComponents.PLAYER_MAGIC.sync(player);
    }

    public int getRegenTimer() { return regenTimer; }
    public void setRegenTimer(int t) { regenTimer = t; ModComponents.PLAYER_MAGIC.sync(player); }

    public int getRegenInterval() { return regenInterval; }
    public void setRegenInterval(int i) { regenInterval = i; ModComponents.PLAYER_MAGIC.sync(player); }

    public double getManaRegenMomentum() { return manaRegenMomentum; }
    public void setManaRegenMomentum(double m) {
        manaRegenMomentum = m;
        ModComponents.PLAYER_MAGIC.sync(player);
    }

    public int getRegenDelay() { return regenDelayTicks; }
    public void setRegenDelay(int ticks) {
        regenDelayTicks = Math.max(0, ticks);
        ModComponents.PLAYER_MAGIC.sync(player);
    }

    public double getManaFraction() { return manaFraction; }
    public void setManaFraction(double f) {
        manaFraction = f;
        ModComponents.PLAYER_MAGIC.sync(player);
    }

    public PlayerMagicData(PlayerEntity player) {
        this.player = player;
    }

    // --- Getters/Setters ---
    public int getMana() { return mana; }
    public void setMana(int mana) {
        if (mana < 0) {
            mana = 0;
        }

        this.mana = mana;
        ModComponents.PLAYER_MAGIC.sync(player);
    }

    public int getSpellExp(String spell) {
        return spellExp.getOrDefault(spell, 0);
    }

    public void addSpellExp(String spell, int amount) {
        spellExp.put(spell, getSpellExp(spell) + amount);
        ModComponents.PLAYER_MAGIC.sync(player);
    }

    public int getSpellLevel(String spell) {
        return spellLevel.getOrDefault(spell, 1);
    }

    public boolean knowsSpell(String key) {
        return knownSpells.contains(key);
    }

    public void unlockSpell(String spell) {
        if (!knownSpells.contains(spell)) {
            knownSpells.add(spell);
            ModComponents.PLAYER_MAGIC.sync(player);
        }
    }

    public void lockSpell(String spell) {
        knownSpells.remove(spell);
        ModComponents.PLAYER_MAGIC.sync(player);
    }

    public boolean hasStat(String key) {
        return spellLevel.containsKey(key);
    }

    public void setSpellLevel(String spell, int level) {
        spellLevel.put(spell, level);
        if (spell.equals("General")) {
            setMana(5*getSpellLevel(spell));
        }
        addSpellExp(spell, getSpellExp(spell)*-1);
        ModComponents.PLAYER_MAGIC.sync(player);
    }

    // --- Bolt Color (Vector3f) ---
    public Vector3f getBoltColor(String spell) {
        return boltColor.getOrDefault(spell, new Vector3f(2f, 2f, 2f));
    }

    public void setBoltColor(String spell, Vector3f color) {
        boltColor.put(spell, color);
        ModComponents.PLAYER_MAGIC.sync(player);
    }

    public String getPlayerName() {
        return playerName;
    }

    public void setPlayerName(String name) {
        this.playerName = name;
        ModComponents.PLAYER_MAGIC.sync(player);
    }

    public String getCovenName() {
        return covenName;
    }

    public void setCovenName(String name) {
        this.covenName = name;
        ModComponents.PLAYER_MAGIC.sync(player);
    }

    private NbtList writeKnownSpells() {
        NbtList list = new NbtList();
        for (String spell : knownSpells) {
            list.add(NbtString.of(spell));
        }
        return list;
    }

    // --- NBT Serialization ---
    @Override
    public void readFromNbt(NbtCompound tag) {
        mana = tag.getInt("mana");
        totalExp = tag.getInt("totalExp");
        magicLevel = tag.getInt("magicLevel");
        playerName = tag.getString("playerName");
        covenName = tag.getString("covenName");

        readKnownSpells(tag.getList("knownSpells", NbtElement.STRING_TYPE));

        readIntMap(tag.getCompound("spellExp"), spellExp);
        readIntMap(tag.getCompound("spellLevel"), spellLevel);
        readIntMap(tag.getCompound("categoryExp"), categoryExp);
        readIntMap(tag.getCompound("categoryLevel"), categoryLevel);

        readVectorMap(tag.getCompound("boltColor"), boltColor);
        readStoredLocationMap(tag.getCompound("storedPosition"), storedPosition);

        manaRegenMomentum = tag.getDouble("manaRegenMomentum");
        regenDelayTicks = tag.getInt("regenDelayTicks");
        manaFraction = tag.getDouble("manaFraction");
        regenTimer = tag.getInt("regenTimer");
        regenInterval = tag.getInt("regenInterval");

        shapeshiftForm = tag.getString("shapeshiftForm");
    }

    private void readKnownSpells(NbtList list) {
        knownSpells.clear();
        for (int i = 0; i < list.size(); i++) {
            knownSpells.add(list.getString(i));
        }
    }

    @Override
    public void writeToNbt(NbtCompound tag) {
        tag.putInt("mana", mana);
        tag.putInt("totalExp", totalExp);
        tag.putInt("magicLevel", magicLevel);
        tag.putString("playerName", playerName);
        tag.putString("covenName", covenName);

        tag.put("knownSpells", writeKnownSpells());

        tag.put("spellExp", writeIntMap(spellExp));
        tag.put("spellLevel", writeIntMap(spellLevel));
        tag.put("categoryExp", writeIntMap(categoryExp));
        tag.put("categoryLevel", writeIntMap(categoryLevel));

        tag.put("boltColor", writeVectorMap(boltColor));
        tag.put("storedPosition", writeStoredLocationMap(storedPosition));

        tag.putString("shapeshiftForm", shapeshiftForm);

        tag.putDouble("manaRegenMomentum", manaRegenMomentum);
        tag.putInt("regenDelayTicks", regenDelayTicks);
        tag.putDouble("manaFraction", manaFraction);
        tag.putInt("regenTimer", regenTimer);
        tag.putInt("regenInterval", regenInterval);
    }

    // --- Helpers for int maps ---
    private void readIntMap(NbtCompound tag, Map<String, Integer> map) {
        map.clear();
        for (String key : tag.getKeys()) {
            map.put(key, tag.getInt(key));
        }
    }

    private NbtCompound writeIntMap(Map<String, Integer> map) {
        NbtCompound tag = new NbtCompound();
        for (var entry : map.entrySet()) {
            tag.putInt(entry.getKey(), entry.getValue());
        }
        return tag;
    }

    // --- Helpers for Vector3f maps ---
    private void readVectorMap(NbtCompound tag, Map<String, Vector3f> map) {
        map.clear();
        for (String key : tag.getKeys()) {
            NbtCompound vecTag = tag.getCompound(key);
            map.put(key, new Vector3f(
                    vecTag.getFloat("r"),
                    vecTag.getFloat("g"),
                    vecTag.getFloat("b")
            ));
        }
    }

    private NbtCompound writeVectorMap(Map<String, Vector3f> map) {
        NbtCompound tag = new NbtCompound();
        for (var entry : map.entrySet()) {
            Vector3f vec = entry.getValue();
            NbtCompound vecTag = new NbtCompound();
            vecTag.putFloat("r", vec.x());
            vecTag.putFloat("g", vec.y());
            vecTag.putFloat("b", vec.z());
            tag.put(entry.getKey(), vecTag);
        }
        return tag;
    }

    private void readBlockPosMap(NbtCompound tag, Map<String, BlockPos> map) {
        map.clear();
        for (String key : tag.getKeys()) {
            NbtCompound posTag = tag.getCompound(key);
            BlockPos pos = new BlockPos(
                    posTag.getInt("x"),
                    posTag.getInt("y"),
                    posTag.getInt("z")
            );
            map.put(key, pos);
        }
    }

    private void readStoredLocationMap(NbtCompound tag, Map<String, StoredLocation> map) {
        map.clear();

        for (String key : tag.getKeys()) {
            NbtCompound locTag = tag.getCompound(key);

            String dimension = locTag.getString("dimension");
            BlockPos pos = new BlockPos(
                    locTag.getInt("x"),
                    locTag.getInt("y"),
                    locTag.getInt("z")
            );

            map.put(key, new StoredLocation(dimension, pos));
        }
    }

    private NbtCompound writeStoredLocationMap(Map<String, StoredLocation> map) {
        NbtCompound tag = new NbtCompound();

        for (var entry : map.entrySet()) {
            StoredLocation loc = entry.getValue();
            NbtCompound locTag = new NbtCompound();

            locTag.putString("dimension", loc.dimension);

            locTag.putInt("x", loc.pos.getX());
            locTag.putInt("y", loc.pos.getY());
            locTag.putInt("z", loc.pos.getZ());

            tag.put(entry.getKey(), locTag);
        }

        return tag;
    }

}