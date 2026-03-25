package se.itssimple.zombieshateslight;
import net.minecraftforge.eventbus.api.bus.BusGroup;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import se.itssimple.zombieshateslight.util.Reference;
import net.minecraftforge.fml.common.Mod;

@Mod(Reference.MOD_ID)
public class ModForge {

	public ModForge(FMLJavaModLoadingContext modLoadingContext) {
        BusGroup modEventBus = modLoadingContext.getModBusGroup();

        FMLLoadCompleteEvent.getBus(modEventBus).addListener(this::loadComplete);
	}

    private void loadComplete(final FMLLoadCompleteEvent event) {
        ModCommon.init();
    }

}