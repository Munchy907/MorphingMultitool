package uk.co.hailhydra.morphingmultitool.proxy;

import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import uk.co.hailhydra.morphingmultitool.handlers.ClientHandler;
import uk.co.hailhydra.morphingmultitool.init.ModItems;

@EventBusSubscriber(value = Side.CLIENT)
public class ClientProxy extends CommonProxy {

    public ClientProxy(){}

    @Override
    public void preInit() {
        super.preInit();
        MinecraftForge.EVENT_BUS.register(ClientHandler.INSTANCE);
    }

    @Override
    public void init() {
        super.init();
    }

    @SubscribeEvent
    public static void registerModels(ModelRegistryEvent event){
        ModItems.registerRender();
    }
}
