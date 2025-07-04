package uk.co.hailhydra.morphingmultitool.handlers;

import net.minecraft.item.Item;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import uk.co.hailhydra.morphingmultitool.init.ModItems;

@Mod.EventBusSubscriber
public class RegisterHandler {

    @SubscribeEvent
    public static void registerItems(RegistryEvent.Register<Item> event){
        for (Item item : ModItems.items){
            event.getRegistry().register(item);
        }
    }
}
