package net.nia.witchinghour.magic.spells.whitemagic.charms;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.nia.witchinghour.WitchingHourEntities;
import net.nia.witchinghour.data.EntityMagicData;
import net.nia.witchinghour.data.ModComponents;
import net.nia.witchinghour.data.Names;
import net.nia.witchinghour.data.PlayerMagicData;
import net.nia.witchinghour.magic.grimoire.GrimoireEntity;
import net.nia.witchinghour.magic.spells.blackmagic.CursesUpdater;
import net.nia.witchinghour.magic.spells.bluemagic.SpellProtection;
import net.nia.witchinghour.magic.spells.other.*;

import java.util.*;
import java.util.stream.Collectors;

public class Revelation {

    public static final Spell SPELL = new Spell();
    public static final String ID = "Revelation";

    public static ItemStack makeWrittenBook(
            ItemStack writableBook,
            String title,
            String author,
            List<Text> pages
    ) {
        ItemStack writtenBook = new ItemStack(Items.WRITTEN_BOOK);

        NbtCompound nbt = new NbtCompound();
        nbt.putString("title", title);
        nbt.putString("author", author);

        NbtList pageList = new NbtList();
        for (Text page : pages) {
            pageList.add(NbtString.of(Text.Serializer.toJson(page)));
        }

        nbt.put("pages", pageList);
        writtenBook.setNbt(nbt);

        return writtenBook;
    }

    public static List<String> paginate(String text, int maxChars) {
        List<String> pages = new ArrayList<>();
        StringBuilder page = new StringBuilder();

        for (String word : text.split(" ")) {
            if (page.length() + word.length() + 1 > maxChars) {
                pages.add(page.toString());
                page = new StringBuilder();
            }

            if (!page.isEmpty()) {
                page.append(" ");
            }
            page.append(word);
        }

        if (!page.isEmpty()) {
            pages.add(page.toString());
        }

        return pages;
    }

    public static ItemStack runRevelationChecks(SpellBoltEntity bolt, String spell, Entity target, PlayerEntity player) {

        PlayerMagicData data = ModComponents.PLAYER_MAGIC.get(target);
        EntityMagicData eData = ModComponents.ENTITY_MAGIC.get(target);
        String name = data.getPlayerName();

        ItemStack heldItem = player.getMainHandStack();
        Item item = heldItem.getItem();

        List<Text> pages = new ArrayList<>();
        String text = bolt.spell.toLowerCase();

        if (text.contains("name")) {

            List<Spell> casted = new ArrayList<>();
            casted.add(SPELL);
            if (ManaHelper.consume(bolt, casted, false, 0)) {
                return null;
            }

            String result = Arrays.stream(name.split(" "))
                    .map(word -> word.substring(0, 1).toUpperCase() + word.substring(1).toLowerCase())
                    .collect(Collectors.joining(" "));

            pages.add(Text.literal("Name: ")
                    .formatted(Formatting.BLACK)
                    .append(Text.literal(result)
                            .formatted(Formatting.BLACK)));

        }

        if (text.contains("coven")) {
            String covenName = data.getCovenName();

            if (text.contains("name")) {

                List<Spell> casted = new ArrayList<>();
                casted.add(SPELL);
                if (ManaHelper.consume(bolt, casted, false, 0)) {
                    return null;
                }

                String[] words = covenName.split("\\s+");
                StringBuilder result = new StringBuilder();

                for (String word : words) {
                    if (!word.isEmpty()) {
                        result.append(Character.toUpperCase(word.charAt(0)))
                                .append(word.substring(1).toLowerCase())
                                .append(" ");
                    }
                }

                String result_ = result.toString().trim();

                pages.add(Text.literal("Coven Name: ")
                        .formatted(Formatting.BLACK)
                        .append(Text.literal(result_)
                                .formatted(Formatting.BLACK)));

            }

        }

        if (text.contains("location")) {

            List<Spell> casted = new ArrayList<>();
            casted.add(SPELL);
            if (ManaHelper.consume(bolt, casted, false, 0)) {
                return null;
            }

            String[] t = text.split("location");

            if (t[1].contains(" of ")) {
                t = t[1].split(" of ");

                if (t[1].contains("grimoire")) {


                    var entities = player.getServer().getWorld(player.getWorld().getRegistryKey()).getEntitiesByType(WitchingHourEntities.GRIMOIRE, Entity::isAlive);
                    for (GrimoireEntity g : entities) {
                        if (g.getOwnerUuid().equals(target.getUuid())) {
                            target = g;
                            break;
                        }
                    }
                }
            }

            String result = "("+ target.getBlockPos().getX() + ", " + target.getBlockPos().getY() + ", " + target.getBlockPos().getZ() +")";

            pages.add(Text.literal("Location: ")
                    .formatted(Formatting.BLACK)
                    .append(Text.literal(result)
                            .formatted(Formatting.BLACK)));

        }

        if (text.contains("curse")) {

            List<Spell> casted = new ArrayList<>();
            casted.add(SPELL);
            if (ManaHelper.consume(bolt, casted, false, 0)) {
                return null;
            }

            Set<String> curses = eData.getCurses();
            String all = String.join(",\n", curses);
            List<String> cursePages = paginate(all, 256);

            for (String pageText : cursePages) {
                pages.add(
                        Text.literal("Curses\n\n")
                                .formatted(Formatting.UNDERLINE, Formatting.ITALIC)
                                .append(
                                        Text.literal(pageText)
                                                .styled(style -> style
                                                        .withItalic(false)
                                                        .withUnderline(false)
                                                )
                                )
                );
            }

        }

        String result = Arrays.stream(name.trim().split("\\s+"))
                .filter(s -> !s.isBlank())
                .map(s -> Character.toUpperCase(s.charAt(0)) + s.substring(1).toLowerCase())
                .collect(Collectors.joining(" "));

        return makeWrittenBook(
                heldItem,
                "Revelation",
                result,
                pages
        );
    }

    public static void init() {

        // Create Recipe
        Map<SpellIngredient, Integer> RECIPE = new HashMap<>();

        RECIPE.put(new SpellIngredient(Items.GLOWSTONE, null), 4);
        RECIPE.put(new SpellIngredient(Items.TORCH, null), 4);
        RECIPE.put(new SpellIngredient(Items.WRITABLE_BOOK, null), 1);

        // Create Spell
        SpellLoader.create(
                SPELL,

                ID, new String[] {
                        "reveal", "revelation", "revealing", "revealed", "reveals"
                },

                new double[] {
                        60.0, 25.0, 70.0, 1.0
                },

                SpellType.WHITE_MAGIC,

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

                        Entity target = null;

                        Entity obtainedPlayer_ = Names.getFirstPlayerByName(Objects.requireNonNull(bolt.getServer()), spell);
                        if (obtainedPlayer_ != null) {
                            target = obtainedPlayer_;
                        }

                        if (!(target instanceof PlayerEntity)) {
                            return;
                        }


                        if (bolt.spellInst.contains(SpellProtection.SPELL)) {
                            SpellProtection.protect(bolt, spell, new EntityHitResult(target, target.getPos()), ID);
                            return;
                        }

                        if (CursesUpdater.isProtected(ID, player, SPELL, 1)) {
                            return;
                        }

                        PlayerMagicData data = ModComponents.PLAYER_MAGIC.get(target);
                        String name = data.getPlayerName();

                        if (name.isEmpty()) {
                            return;
                        }

                        ItemStack heldItem = player.getMainHandStack();
                        Item item = heldItem.getItem();

                        if (item == Items.WRITABLE_BOOK) {
                            ItemStack writtenBook = runRevelationChecks(bolt, spell, target, player);
                            if (writtenBook == null) {
                                return;
                            }

                            player.setStackInHand(player.getActiveHand(), writtenBook);
                        }

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

                        Entity target = hit.getEntity();

                        Entity obtainedPlayer_ = Names.getFirstPlayerByName(Objects.requireNonNull(bolt.getServer()), spell);
                        if (obtainedPlayer_ != null) {
                            target = obtainedPlayer_;
                        }

                        if (!(target instanceof PlayerEntity)) {
                            return;
                        }


                        if (target instanceof PlayerEntity p) {
                            if (bolt.spellInst.contains(SpellProtection.SPELL)) {
                                SpellProtection.protect(bolt, spell, new EntityHitResult(p, p.getPos()), ID);
                                return;
                            }

                            if (CursesUpdater.isProtected(ID, p, SPELL, 1)) {
                                return;
                            }
                        }

                        PlayerMagicData data = ModComponents.PLAYER_MAGIC.get(target);
                        String name = data.getPlayerName();

                        if (name.isEmpty()) {
                            return;
                        }

                        ItemStack heldItem = player.getMainHandStack();
                        Item item = heldItem.getItem();

                        if (item == Items.WRITABLE_BOOK) {
                            ItemStack writtenBook = runRevelationChecks(bolt, spell, target, player);
                            if (writtenBook == null) {
                                return;
                            }

                            player.setStackInHand(player.getActiveHand(), writtenBook);
                        }

                        bolt.discard();
                    }
                },

                RECIPE
        );

    }

}
