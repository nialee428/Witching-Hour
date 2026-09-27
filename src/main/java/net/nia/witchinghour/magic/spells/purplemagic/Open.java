package net.nia.witchinghour.magic.spells.purplemagic;

import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Items;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.nia.witchinghour.data.ModComponents;
import net.nia.witchinghour.data.PlayerMagicData;
import net.nia.witchinghour.magic.spells.bluemagic.Shield;
import net.nia.witchinghour.magic.spells.other.*;
import net.nia.witchinghour.screenhandlers.PlayerInventoryViewScreenHandler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Open {

    public static final Spell SPELL = new Spell();
    public static final String ID = "Open";
    public static boolean IGNORE_DISTANCE = true;

    public static boolean openBlockInventory(ServerPlayerEntity player, BlockPos pos, String dim) {
        MinecraftServer server = player.getServer();

        assert server != null;

        World world = server.getWorld(RegistryKey.of(
                RegistryKeys.WORLD,
                new Identifier(dim)
        ));
        BlockState state = world.getBlockState(pos);

        NamedScreenHandlerFactory factory = state.createScreenHandlerFactory(world, pos);
        if (factory != null) {
            IGNORE_DISTANCE = (true);
            var handler = factory.createMenu(0, player.getInventory(), player);

            if (handler == null || handler.slots.isEmpty()) {
                return false; // prevent crash
            }

            IGNORE_DISTANCE = (true);
            player.openHandledScreen(factory);
            return true;
        }

        BlockEntity be = world.getBlockEntity(pos);
        if (be instanceof NamedScreenHandlerFactory beFactory) {
            IGNORE_DISTANCE = (true);
            var handler = beFactory.createMenu(0, player.getInventory(), player);

            if (handler == null || handler.slots.isEmpty()) {
                return false;
            }

            IGNORE_DISTANCE = (true);
            player.openHandledScreen(beFactory);
            return true;
        }

        return false;
    }

    public static boolean openEntityInventory(ServerPlayerEntity player, Entity entity, String dim) {
        if (entity instanceof NamedScreenHandlerFactory factory) {
            IGNORE_DISTANCE = true;
            player.openHandledScreen(factory);
            return true;
        }
        return false;
    }

    public static void openPlayerInventory(ServerPlayerEntity opener, ServerPlayerEntity target, String dim) {

        opener.openHandledScreen(new ExtendedScreenHandlerFactory() {
            @Override
            public void writeScreenOpeningData(ServerPlayerEntity player, PacketByteBuf buf) {
                buf.writeUuid(target.getUuid());
            }

            @Override
            public Text getDisplayName() {
                return Text.literal("Player Inventory View");
            }

            @Override
            public ScreenHandler createMenu(int syncId, PlayerInventory inv, PlayerEntity player) {
                return new PlayerInventoryViewScreenHandler(syncId, inv, target.getInventory());
            }
        });
    }


    public static boolean openAnyInventory(ServerPlayerEntity opener, Object target, String dim) {
        if (target instanceof BlockPos pos) {
            return openBlockInventory(opener, pos, dim);
        }

        if (target instanceof Entity entity) {
            return openEntityInventory(opener, entity, dim);
        }

        return false;
    }


    public static void init() {

        // Create Recipe
        Map<SpellIngredient, Integer> RECIPE = new HashMap<>();

        RECIPE.put(new SpellIngredient(Items.CHEST, null), 1);
        RECIPE.put(new SpellIngredient(Items.HOPPER, null), 1);
        RECIPE.put(new SpellIngredient(Items.SPYGLASS, null), 1);

        // Create Spell
        SpellLoader.create(
                SPELL,

                ID, new String[] {
                        "open", "opens", "opening", "opened", "opener"
                },

                new double[] {
                        3.0, 5.0, 9.0, 1.0
                },

                SpellType.PURPLE_MAGIC,

                new SpellBehavior() {

                    @Override
                    public void onBlockHit(
                            SpellBoltEntity bolt,
                            String spell,
                            BlockHitResult hit
                    ) {

                        PlayerEntity player = bolt.getOwner() instanceof PlayerEntity p ? p : null;
                        if (player == null) {
                            return;
                        }

                        List<Spell> casted = new ArrayList<>();
                        casted.add(SPELL);
                        if (ManaHelper.consume(bolt, casted, false, 0)) {
                            return;
                        }

                        if (Shield.shieldedSpell(player, bolt, hit, player.getWorld())) {
                            bolt.discard();
                            return;
                        }

                        BlockPos pos = hit.getBlockPos();
                        World world = bolt.getWorld();

                        BlockState state = world.getBlockState(pos);
                        Block block = state.getBlock();

                        if (block instanceof DoorBlock || block instanceof TrapdoorBlock || block instanceof FenceGateBlock) {
                            block.onUse(state, world, pos, player, Hand.MAIN_HAND, hit);
                            return;
                        }

                        PlayerMagicData data = ModComponents.PLAYER_MAGIC.get(player);
                        String dim = player.getWorld().getRegistryKey().getValue().toString();

                        for (Map.Entry<String, PlayerMagicData.StoredLocation> entry : data.getAllPos()) {
                            String key = entry.getKey();
                            PlayerMagicData.StoredLocation loc = entry.getValue();

                            if (spell.contains(key)) {
                                pos = loc.pos; // the BlockPos
                                dim = loc.dimension; // the dimension string
                            }
                        }

                        openAnyInventory((ServerPlayerEntity) player, pos, dim);

                        bolt.discard();

                    }

                    @Override
                    public void onEntityHit(
                            SpellBoltEntity bolt,
                            String spell,
                            EntityHitResult hit) {

                        PlayerEntity player = bolt.getOwner() instanceof PlayerEntity p ? p : null;
                        if (player == null) {
                            return;
                        }

                        bolt.discard();

                    }
                },

                RECIPE
        );

    }
}