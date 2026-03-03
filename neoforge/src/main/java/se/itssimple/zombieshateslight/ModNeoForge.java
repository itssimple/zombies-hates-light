package se.itssimple.zombieshateslight;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.neoforged.neoforge.common.NeoForge;
import se.itssimple.zombieshateslight.util.Reference;
import se.itssimple.zombieshateslight.events.ZombieGoalEvents;

@Mod(Reference.MOD_ID)
public class ModNeoForge {
    public ModNeoForge(IEventBus eventBus, ModContainer modContainer)
    {
        eventBus.addListener(this::loadComplete);
        NeoForge.EVENT_BUS.register(ZombieGoalEvents.class);
    }

    private void loadComplete(final FMLLoadCompleteEvent event) { ModCommon.init(); }
}
