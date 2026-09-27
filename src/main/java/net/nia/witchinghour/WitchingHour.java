package net.nia.witchinghour;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.nia.witchinghour.events.EventsHandler;
import net.nia.witchinghour.loot.LostJournalLootInjector;
import net.nia.witchinghour.magic.grimoire.GrimoireEntity;
import net.nia.witchinghour.magic.spells.blackmagic.CursesUpdater;
import net.nia.witchinghour.magic.spells.other.SpellLoader;
import net.nia.witchinghour.screenhandlers.WitchingHourScreenHandlers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WitchingHour implements ModInitializer {
	public static final String MOD_ID = "witching-hour";

	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
	
	@Override
	public void onInitialize() {

        ModGameRules.init();

        ModBlocks.registerAll();
        ModItems.registerAll();
        ModItemGroups.registerItemGroups();

        WitchingHourNetworking.register();

        WitchingHourScreenHandlers.register();

        LostJournalLootInjector.register();
        SpellLoader.register();

        WitchingHourOPCommands.register();
        WitchingHourEntities.init();

        FabricDefaultAttributeRegistry.register(
                WitchingHourEntities.GRIMOIRE,
                GrimoireEntity.createAttributes()
        );

        CursesUpdater.registerCurses();

        EventsHandler.register();

    }
}