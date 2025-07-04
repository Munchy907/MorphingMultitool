package uk.co.hailhydra.morphingmultitool.proxy;

import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import uk.co.hailhydra.morphingmultitool.init.ModItems;

@EventBusSubscriber(value = Side.CLIENT)
public class ClientProxy implements ICommonProxy{

    public ClientProxy(){}

    @Override
    public void preInit() {

    }

    @Override
    public void init() {

    }

    @SubscribeEvent
    public static void registerModels(ModelRegistryEvent event){
        ModItems.registerRender();
    }
}
