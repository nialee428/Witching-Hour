package net.nia.witchinghour.magic.grimoire.info.categories;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import net.nia.witchinghour.magic.grimoire.GrimoireEntity;
import net.nia.witchinghour.magic.grimoire.info.GrimoireClientData;

public class CategoriesDisplay {

    public static void display(GrimoireEntity book, PlayerEntity player) {

        var state = GrimoireClientData.get(book);

        state.setTitle(Text.literal("§d✦ Categories / Entries ✦"));
        state.clearPages();

        state.addPage(Text.literal("This book contains various categories. To view all possible categories/" +
                "entries, please utilize Minecraft's Advancements menu. The Advancement Menu will have a section " +
                "containing the names of every single category/entry currently available to you. To access the " +
                "category/entry, say \"show me [category/entry].\" Replace [category/entry] with the name of the " +
                "advancement.\nPG: 1 of 2"));

        state.addPage(Text.literal("Remember to read through any new categories and entries that you unlock, as " +
                "they may hint towards how to further progress!\nPG: 2 of 2"));
    }

}