package net.nia.witchinghour.data;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.nia.witchinghour.ModGameRules;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class Names {

    public static String[] names = new String[] {
            "aaron", "adrian", "aiden", "alex", "alexander", "alexis", "alice", "allison",
            "amber", "amelia", "andy", "angel", "anna", "anthony", "aria", "audrey", "aurora", "ava",
            "avery", "axel", "beatrice", "benjamin", "bennett", "blake", "bradley",
            "brooke", "brooks", "caleb", "camila", "cameron", "caroline", "carter",
            "catherine", "charles", "charlotte", "chloe", "christian", "christopher",
            "claire", "clara", "cooper", "cora", "croft", "daisy", "dallas", "daniel", "david", "delilah",
            "dylan", "eden", "edward", "elena", "elias", "eliana", "elijah", "elizabeth",
            "ella", "ellie", "emilia", "emily", "emma", "eric", "erica", "ethan", "evelyn", "everly",
            "ezekiel", "finley", "finn", "frankie", "gabriel", "gabriella", "grace",
            "grayson", "hannah", "harper", "harriet", "harrison", "haru", "hazel", "henry",
            "hudson", "hunter", "ian", "iris", "isabella", "isaac", "isaiah", "ivy",
            "jack", "jackson", "jacob", "jade", "james", "jason", "jasper", "jax", "jayden",
            "joseph", "josephine", "josiah", "josie", "joshua", "julia", "kai", "kennedy", "kerrigan", "lara",
            "lee", "leo", "leon", "lewis", "liam", "lily", "lincoln", "louis", "luca", "lucas", "lucy",
            "luke", "luna", "lydia", "madison", "maeve", "mason", "marinette", "mary", "mateo",
            "matthew", "maverick", "maya", "michael", "mia", "miles", "millie",
            "naomi", "natalie", "nathan", "nia", "nolan", "noah", "nora", "nova",
            "oakley", "oliver", "olivia", "owen", "penelope", "peter", "poppy", "preston", "quinn",
            "rebecca", "reggie", "reuben", "riley", "roman", "ronnie", "rose", "rory",
            "rowan", "ruby", "sadie", "sam", "samantha", "samuel", "sarah", "savannah", "sebastian",
            "sophia", "sophie", "stella", "thomas", "theo", "theodore", "tommy",
            "valerie", "vera", "victoria", "vinnie", "violet", "vivian", "waylon",
            "wesley", "weston", "william", "willow", "wyatt", "zach", "zachary", "zoe"
    };

    public static boolean isName(String name) {
        return Arrays.asList(names).contains(name);
    }

    public static List<Entity> getPlayersByNames(MinecraftServer server, String text) {

        List<Entity> result = new ArrayList<>();

        text = text.toLowerCase()
                .replaceAll("[^a-z ]", " ");

        String[] words = text.split("\\s+");

        String first = "";
        String last = "";

        for (String word : words) {

            if (!isName(word))
                continue;

            if (first.isEmpty()) {
                first = word;
            } else {
                last = word;
                break;
            }
        }

        if (first.isEmpty())
            return result;

        boolean fullSearch = !last.isEmpty();

        for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {

            String playerName = ModComponents.PLAYER_MAGIC
                    .get(player)
                    .getPlayerName()
                    .toLowerCase();

            if (playerName.isEmpty())
                continue;

            if (fullSearch) {

                if (playerName.equals(first + " " + last))
                    result.add(player);

            } else {

                if (playerName.equals(first) || playerName.startsWith(first + " "))
                    result.add(player);
            }
        }

        return result;
    }

    public static Entity getFirstPlayerByName(MinecraftServer server, String text) {

        text = text.toLowerCase();

        String[] words = text
                .replaceAll("[^a-z ]", "")
                .split("\\s+");

        String first = "";
        String last = "";

        for (String word : words) {

            if (!isName(word))
                continue;

            if (first.isEmpty()) {
                first = word;
            } else {
                last = word;
                break;
            }
        }

        if (first.isEmpty())
            return null;

        boolean fullSearch = !last.isEmpty();

        for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {

            String playerName = ModComponents.PLAYER_MAGIC
                    .get(player)
                    .getPlayerName()
                    .toLowerCase();

            if (playerName.isEmpty())
                continue;

            if (fullSearch) {

                if (playerName.equals(first + " " + last))
                    return player;

            } else {

                if (playerName.equals(first) || playerName.startsWith(first + " "))
                    return player;
            }
        }

        return null;
    }

    public static String[] extractNames(MinecraftServer server, String input) {

        boolean firstOnly = firstNamesOnly(server);

        String[] words = input
                .toLowerCase()
                .replaceAll("[^a-zA-Z ]", "")
                .split("\\s+");

        List<String> found = new ArrayList<>();

        int i = 0;

        while (i < words.length) {

            if (!isName(words[i])) {
                i++;
                continue;
            }

            // First-name-only worlds:
            // Every valid name is treated independently.
            if (firstOnly) {
                found.add(words[i]);
                i++;
                continue;
            }

            // Full-name worlds:
            // Group consecutive valid names together.
            StringBuilder group = new StringBuilder(words[i]);
            i++;

            while (i < words.length && isName(words[i])) {
                group.append(" ").append(words[i]);
                i++;
            }

            found.add(group.toString());
        }

        return found.toArray(new String[0]);
    }

    public static boolean setName(PlayerEntity player, String first, String last) {

        first = first.toLowerCase();
        last = last.toLowerCase();

        if (!isName(first))
            return false;

        boolean firstOnly = firstNamesOnly(player.getServer());

        if (!firstOnly) {
            if (!isName(last))
                return false;

            if (first.equals(last))
                return false;
        }

        String fullName = firstOnly
                ? first
                : first + " " + last;

        PlayerMagicData data = ModComponents.PLAYER_MAGIC.get(player);

        if (!data.getPlayerName().isEmpty())
            return false;

        for (ServerPlayerEntity other :
                Objects.requireNonNull(player.getServer())
                        .getPlayerManager()
                        .getPlayerList()) {

            PlayerMagicData otherData = ModComponents.PLAYER_MAGIC.get(other);

            if (otherData.getPlayerName().equalsIgnoreCase(fullName))
                return false;
        }

        data.setPlayerName(fullName);
        return true;
    }

    public static boolean firstNamesOnly(MinecraftServer server) {
        return server.getGameRules().getBoolean(ModGameRules.FIRST_NAMES_ONLY);
    }

}
