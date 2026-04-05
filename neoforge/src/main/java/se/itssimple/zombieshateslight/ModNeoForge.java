package se.itssimple.zombieshateslight;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.neoforged.neoforge.common.NeoForge;
import se.itssimple.zombieshateslight.util.Reference;
import se.itssimple.zombieshateslight.events.ZombieGoalEvents;

/** The NeoForge version of the mod */
@Mod(Reference.MOD_ID)
public class ModNeoForge {
    /**
     * Default constructor
     * @param eventBus -
     */
    public ModNeoForge(IEventBus eventBus)
    {
        eventBus.addListener(this::loadComplete);
        NeoForge.EVENT_BUS.register(ZombieGoalEvents.class);
    }

    /**
     *
     * @param event -
     */
    private void loadComplete(final FMLLoadCompleteEvent event) { ModCommon.init(); }
}
