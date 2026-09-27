package net.nia.witchinghour.world.dimensions.tomb;

import net.minecraft.block.Block;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class TrialRules {

    private final Set<Block> allowedInteractions = new HashSet<>();
    private final Set<Block> allowedBreaking = new HashSet<>();
    private final Set<Block> allowedPlacement = new HashSet<>();

    public boolean canInteract(Block block) {
        return allowedInteractions.contains(block);
    }

    public boolean canBreak(Block block) {
        return allowedBreaking.contains(block);
    }

    public boolean canPlace(Block block) {
        return allowedPlacement.contains(block);
    }

    // builder helpers
    public TrialRules allowInteract(Block... blocks) {
        allowedInteractions.addAll(Arrays.asList(blocks));
        return this;
    }

    public TrialRules allowBreak(Block... blocks) {
        allowedBreaking.addAll(Arrays.asList(blocks));
        return this;
    }

    public TrialRules allowPlace(Block... blocks) {
        allowedPlacement.addAll(Arrays.asList(blocks));
        return this;
    }
}