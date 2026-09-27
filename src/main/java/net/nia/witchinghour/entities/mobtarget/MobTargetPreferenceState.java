package net.nia.witchinghour.entities.mobtarget;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.world.PersistentState;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class MobTargetPreferenceState extends PersistentState {

    private static final String PREFERENCES_KEY = "Preferences";

    // Mob UUID -> (Target UUID -> Preference)
    private final Map<UUID, Map<UUID, MobTargetPreference>> preferences =
            new HashMap<>();

    public MobTargetPreferenceState() {
    }

    public MobTargetPreferenceState(NbtCompound nbt) {
        NbtCompound mobsNbt = nbt.getCompound(PREFERENCES_KEY);

        for (String mobUuidString : mobsNbt.getKeys()) {
            try {
                UUID mobUuid = UUID.fromString(mobUuidString);
                NbtCompound targetsNbt = mobsNbt.getCompound(mobUuidString);

                Map<UUID, MobTargetPreference> targets = new HashMap<>();

                for (String targetUuidString : targetsNbt.getKeys()) {
                    try {
                        UUID targetUuid = UUID.fromString(targetUuidString);

                        String preferenceName =
                                targetsNbt.getString(targetUuidString);

                        try {
                            MobTargetPreference preference =
                                    MobTargetPreference.valueOf(preferenceName);

                            targets.put(targetUuid, preference);
                        } catch (IllegalArgumentException ignored) {
                        }

                    } catch (IllegalArgumentException ignored) {
                    }
                }

                if (!targets.isEmpty()) {
                    preferences.put(mobUuid, targets);
                }

            } catch (IllegalArgumentException ignored) {
            }
        }
    }

    public void setPreference(
            UUID mobUuid,
            UUID targetUuid,
            MobTargetPreference preference
    ) {
        preferences
                .computeIfAbsent(mobUuid, uuid -> new HashMap<>())
                .put(targetUuid, preference);

        markDirty();
    }

    public void removePreference(
            UUID mobUuid,
            UUID targetUuid
    ) {
        Map<UUID, MobTargetPreference> targets =
                preferences.get(mobUuid);

        if (targets == null) {
            return;
        }

        targets.remove(targetUuid);

        if (targets.isEmpty()) {
            preferences.remove(mobUuid);
        }

        markDirty();
    }

    public MobTargetPreference getPreference(
            UUID mobUuid,
            UUID targetUuid
    ) {
        Map<UUID, MobTargetPreference> targets =
                preferences.get(mobUuid);

        if (targets == null) {
            return null;
        }

        return targets.get(targetUuid);
    }

    @Override
    public NbtCompound writeNbt(NbtCompound nbt) {
        NbtCompound mobsNbt = new NbtCompound();

        for (Map.Entry<UUID, Map<UUID, MobTargetPreference>> mobEntry
                : preferences.entrySet()) {

            NbtCompound targetsNbt = new NbtCompound();

            for (Map.Entry<UUID, MobTargetPreference> targetEntry
                    : mobEntry.getValue().entrySet()) {

                targetsNbt.putString(
                        targetEntry.getKey().toString(),
                        targetEntry.getValue().name()
                );
            }

            mobsNbt.put(
                    mobEntry.getKey().toString(),
                    targetsNbt
            );
        }

        nbt.put(PREFERENCES_KEY, mobsNbt);

        return nbt;
    }
}