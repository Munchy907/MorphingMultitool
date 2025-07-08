package uk.co.hailhydra.morphingmultitool;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import uk.co.hailhydra.morphingmultitool.Tags;
import uk.co.hailhydra.morphingmultitool.init.ModItems;

public class MorphingMultiToolTab extends CreativeTabs {

    public MorphingMultiToolTab(){super(Tags.MODID);}


    @Override
    public @NotNull ItemStack createIcon() {
        return new ItemStack(ModItems.MORPHING_MULTI_TOOL);
    }
}
