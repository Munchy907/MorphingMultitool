package uk.co.hailhydra.morphingmultitool.init;

import net.minecraft.item.Item;
import java.util.ArrayList;
import java.util.List;
import uk.co.hailhydra.morphingmultitool.items.ItemTool;

public class ModItems {
    public static List<Item> items = new ArrayList<>();

    public static ItemTool MORPHING_MULTI_TOOL = new ItemTool();

    public static void init(){
        items.add(MORPHING_MULTI_TOOL);
    }

    public static void registerRender() {
        MORPHING_MULTI_TOOL.initModel();
    }
}
