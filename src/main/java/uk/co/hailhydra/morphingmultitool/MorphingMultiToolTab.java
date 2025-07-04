package uk.co.hailhydra.morphingmultitool;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import uk.co.hailhydra.morphingmultitool.Tags;

public class MorphingMultiToolTab extends CreativeTabs {

    public MorphingMultiToolTab(){super(Tags.MODID);}


    @Override
    public @NotNull ItemStack createIcon() {
        return new ItemStack(Items.FLOWER_POT);
    }
}
