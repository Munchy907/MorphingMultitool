package uk.co.hailhydra.morphingmultitool.utility;

import net.minecraft.init.Items;
import net.minecraft.item.Item;
import scala.collection.mutable.HashMap;

public class HelpfulMappings {
    public static HashMap<String, Item> toolsMap = new HashMap<>();

    public static void initMappings(){
        //TODO: Swap to ENUM e.g. 0 is shovel, 1 is pickaxe ect ect
        toolsMap.put("shovel", Items.WOODEN_SHOVEL);
        toolsMap.put("pickaxe", Items.WOODEN_PICKAXE);
        toolsMap.put("axe", Items.WOODEN_AXE);
        toolsMap.put("sword", Items.WOODEN_SWORD);
        toolsMap.put("hoe", Items.WOODEN_HOE);

    }
}
