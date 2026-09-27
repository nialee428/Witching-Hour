package net.nia.witchinghour.entities.mobtarget;

import net.minecraft.entity.LivingEntity;
import net.minecraft.server.world.ServerWorld;

public final class MobTargetPrefs {

    private static final String STATE_ID =
            "witching_hour_mob_target_preferences";

    private MobTargetPrefs() {
    }

    private static MobTargetPreferenceState getState(ServerWorld world) {
        return world.getPersistentStateManager().getOrCreate(
                MobTargetPreferenceState::new,
                MobTargetPreferenceState::new,
                STATE_ID
        );
    }

    public static void setPreference(
            LivingEntity mob,
            LivingEntity target,
            MobTargetPreference preference
    ) {
        if (!(mob.getWorld() instanceof ServerWorld serverWorld)) {
            return;
        }

        getState(serverWorld).setPreference(
                mob.getUuid(),
                target.getUuid(),
                preference
        );
    }

    public static void ignore(
            LivingEntity mob,
            LivingEntity target
    ) {
        setPreference(
                mob,
                target,
                MobTargetPreference.IGNORE
        );
    }

    public static void prefer(
            LivingEntity mob,
            LivingEntity target
    ) {
        setPreference(
                mob,
                target,
                MobTargetPreference.PREFER
        );
    }

    public static void removePreference(
            LivingEntity mob,
            LivingEntity target
    ) {
        if (!(mob.getWorld() instanceof ServerWorld serverWorld)) {
            return;
        }

        getState(serverWorld).removePreference(
                mob.getUuid(),
                target.getUuid()
        );
    }

    public static MobTargetPreference getPreference(
            LivingEntity mob,
            LivingEntity target
    ) {
        if (!(mob.getWorld() instanceof ServerWorld serverWorld)) {
            return null;
        }

        return getState(serverWorld).getPreference(
                mob.getUuid(),
                target.getUuid()
        );
    }

    public static boolean isIgnored(
            LivingEntity mob,
            LivingEntity target
    ) {
        return getPreference(mob, target)
                == MobTargetPreference.IGNORE;
    }

    public static boolean isPreferred(
            LivingEntity mob,
            LivingEntity target
    ) {
        return getPreference(mob, target)
                == MobTargetPreference.PREFER;
    }
}