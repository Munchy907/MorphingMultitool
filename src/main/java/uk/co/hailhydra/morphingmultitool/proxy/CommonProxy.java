package uk.co.hailhydra.morphingmultitool.proxy;

import net.minecraftforge.common.MinecraftForge;
import uk.co.hailhydra.morphingmultitool.handlers.MorphHandler;
import uk.co.hailhydra.morphingmultitool.init.ModItems;
import uk.co.hailhydra.morphingmultitool.network.NetworkHandler;

public class CommonProxy {

    public void preInit(){
        ModItems.init();
        NetworkHandler.init();
        MinecraftForge.EVENT_BUS.register(MorphHandler.INSTANCE);
    }

    public void init(){
    }
}
