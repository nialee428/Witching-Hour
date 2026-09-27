package net.nia.witchinghour.magic.grimoire.info;

import net.nia.witchinghour.magic.grimoire.GrimoireEntity;

import java.util.WeakHashMap;

public class GrimoireClientData {

    private static final WeakHashMap<GrimoireEntity, GrimoireDisplayState> STATES = new WeakHashMap<>();

    public static GrimoireDisplayState get(GrimoireEntity entity) {
        return STATES.computeIfAbsent(entity, e -> new GrimoireDisplayState());
    }
}
