package net.nia.witchinghour.magic.grimoire.info.categories;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import net.nia.witchinghour.magic.grimoire.GrimoireEntity;
import net.nia.witchinghour.magic.grimoire.info.GrimoireClientData;

public class CommandsDisplay {

    public static void display(GrimoireEntity book, PlayerEntity player) {

        var state = GrimoireClientData.get(book);

        state.setTitle(Text.literal("§d✦ Categories / Entries ✦"));
        state.clearPages();

        state.addPage(Text.literal("This category contains a list of commands that you may provide the book." +
                "You can find a list of commands on the next pages.\nPG: 1 of 2"));

        state.addPage(Text.literal("""
                "Follow me / Follow,"
                "Stay / Stay here,"
                "Show me [category/entry],"
                "Reveal [category/entry],"
                "Display [category/entry],"
                "Tell me about [category/entry],"
                "Craft [spell],"
                PG: 2 of 2"""));
    }

}
