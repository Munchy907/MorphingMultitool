package uk.co.hailhydra.morphingmultitool.items;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import uk.co.hailhydra.morphingmultitool.handlers.MorphHandler;

public class ItemMorphTool extends ItemModBase {
    public ItemMorphTool() {
        super("tool");
    }

    @Override
    public void onCreated(ItemStack stack, World worldIn, EntityPlayer playerIn) {
        stack.setTagCompound(MorphHandler.createNBTData(stack));
        super.onCreated(stack, worldIn, playerIn);
    }
}
