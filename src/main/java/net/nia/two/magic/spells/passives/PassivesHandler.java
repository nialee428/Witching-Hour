package net.nia.witchinghour.magic.spells.passives;

import net.minecraft.entity.player.PlayerEntity;
import net.nia.witchinghour.data.ModComponents;
import net.nia.witchinghour.data.PlayerMagicData;
import net.nia.witchinghour.magic.spells.other.ManaHelper;

public class PassivesHandler {

    public static int whiteMagicLvl(PlayerEntity player) {
        PlayerMagicData data = ModComponents.PLAYER_MAGIC.get(player);
        return data.getSpellLevel("White Magic");
    }

    public static int redMagicLvl(PlayerEntity player) {
        PlayerMagicData data = ModComponents.PLAYER_MAGIC.get(player);
        return data.getSpellLevel("Red Magic");
    }

    public static int purpleMagicLvl(PlayerEntity player) {
        PlayerMagicData data = ModComponents.PLAYER_MAGIC.get(player);
        return data.getSpellLevel("Purple Magic");
    }

    public static int greenMagicLvl(PlayerEntity player) {
        PlayerMagicData data = ModComponents.PLAYER_MAGIC.get(player);
        return data.getSpellLevel("Green Magic");
    }

    public static int blueMagicLvl(PlayerEntity player) {
        PlayerMagicData data = ModComponents.PLAYER_MAGIC.get(player);
        return data.getSpellLevel("Blue Magic");
    }

    public static int blackMagicLvl(PlayerEntity player) {
        PlayerMagicData data = ModComponents.PLAYER_MAGIC.get(player);
        return data.getSpellLevel("Black Magic");
    }


    public static void whiteMagicTrain(PlayerEntity player) {
        PlayerMagicData data = ModComponents.PLAYER_MAGIC.get(player);
        ManaHelper.applyCategoryExp("White Magic", data);
    }

    public static void redMagicTrain(PlayerEntity player) {
        PlayerMagicData data = ModComponents.PLAYER_MAGIC.get(player);
        ManaHelper.applyCategoryExp("Red Magic", data);
    }

    public static void purpleMagicTrain(PlayerEntity player) {
        PlayerMagicData data = ModComponents.PLAYER_MAGIC.get(player);
        ManaHelper.applyCategoryExp("Purple Magic", data);
    }

    public static void greenMagicTrain(PlayerEntity player) {
        PlayerMagicData data = ModComponents.PLAYER_MAGIC.get(player);
        ManaHelper.applyCategoryExp("Green Magic", data);
    }

    public static void blueMagicTrain(PlayerEntity player) {
        PlayerMagicData data = ModComponents.PLAYER_MAGIC.get(player);
        ManaHelper.applyCategoryExp("Blue Magic", data);
    }

    public static void blackMagicTrain(PlayerEntity player) {
        PlayerMagicData data = ModComponents.PLAYER_MAGIC.get(player);
        ManaHelper.applyCategoryExp("Black Magic", data);
    }

}