package net.nia.witchinghour.data;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DestinationOptions {

    public static final Map<String, List<String>> DESTINATIONS = new HashMap<>();

    static {

        // ============================
        // STRUCTURES (GENERALIZED)
        // ============================

        DESTINATIONS.put("village", List.of("village"));

        DESTINATIONS.put("ruined_portal", List.of("ruined portal"));

        DESTINATIONS.put("shipwreck", List.of("shipwreck"));

        DESTINATIONS.put("ancient_city", List.of("ancient city"));
        DESTINATIONS.put("bastion_remnant", List.of("bastion remnant"));
        DESTINATIONS.put("desert_pyramid", List.of("desert pyramid"));
        DESTINATIONS.put("end_city", List.of("end city"));
        DESTINATIONS.put("fortress", List.of("nether fortress"));
        DESTINATIONS.put("igloo", List.of("igloo"));
        DESTINATIONS.put("jungle_pyramid", List.of("jungle pyramid", "jungle temple"));
        DESTINATIONS.put("mansion", List.of("woodland mansion", "wood land mansion", "wooden mansion"));
        DESTINATIONS.put("mineshaft", List.of("mineshaft"));
        DESTINATIONS.put("monument", List.of("ocean monument"));
        DESTINATIONS.put("pillager_outpost", List.of("pillager outpost", "outpost"));
        DESTINATIONS.put("stronghold", List.of("stronghold"));
        DESTINATIONS.put("swamp_hut", List.of("swamp hut"));

        // Removed: buried treasure, nether fossil, all ruined_portal_* variants,
        // all ocean_ruin_* variants, all village_* variants, shipwreck_beached.


        // ============================
        // BIOMES (CURATED)
        // ============================

        // --- Forests (kept separate)
        DESTINATIONS.put("forest", List.of("oak forest"));
        DESTINATIONS.put("birch_forest", List.of("birch forest"));
        DESTINATIONS.put("dark_forest", List.of("dark forest"));
        DESTINATIONS.put("taiga", List.of("taiga", "spruce forest"));
        DESTINATIONS.put("savanna", List.of("savanna", "acacia biome"));
        DESTINATIONS.put("jungle", List.of("jungle"));
        DESTINATIONS.put("crimson_forest", List.of("crimson forest"));
        DESTINATIONS.put("warped_forest", List.of("warped forest"));

        // --- Mountains (generalized)
        DESTINATIONS.put("mountains", List.of("mountains", "peaks"));

        // --- Swamps
        DESTINATIONS.put("swamp", List.of("swamp"));
        DESTINATIONS.put("mangrove_swamp", List.of("mangrove swamp"));

        // --- Caves
        DESTINATIONS.put("lush_caves", List.of("lush caves"));
        DESTINATIONS.put("dripstone_caves", List.of("dripstone caves"));
        DESTINATIONS.put("deep_dark", List.of("deep dark"));

        // --- Nether biomes
        DESTINATIONS.put("nether_wastes", List.of("nether wastes"));
        DESTINATIONS.put("basalt_deltas", List.of("basalt deltas"));
        DESTINATIONS.put("soul_sand_valley", List.of("soul sand valley"));

        // --- Overworld misc
        DESTINATIONS.put("desert", List.of("desert"));
        DESTINATIONS.put("plains", List.of("plains"));
        DESTINATIONS.put("badlands", List.of("badlands"));
        DESTINATIONS.put("meadow", List.of("meadow"));

        // --- Beaches (generalized)
        DESTINATIONS.put("beach", List.of("beach"));

        // --- Oceans (generalized)
        DESTINATIONS.put("ocean", List.of("ocean"));

        // Removed: mushroom_fields, all End biomes, all deep_* ocean variants,
        // all frozen/lukewarm/cold/warm ocean variants, snowy_beach, stony_shore,
        // windswept_* variants, jagged_peaks, frozen_peaks, stony_peaks, snowy_slopes.
    }

}
