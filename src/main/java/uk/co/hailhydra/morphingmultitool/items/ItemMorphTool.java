package uk.co.hailhydra.morphingmultitool.items;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.*;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;
import uk.co.hailhydra.morphingmultitool.handlers.MorphHandler;
import uk.co.hailhydra.morphingmultitool.utility.MorphToolResources;

public class ItemMorphTool extends ItemModBase {
    public ItemMorphTool() {
        super("tool");
    }

    @Override
    public void onCreated(ItemStack stack, World worldIn, EntityPlayer playerIn) {
        stack.setTagCompound(MorphHandler.createNBTData(stack));
        super.onCreated(stack, worldIn, playerIn);
    }

    private boolean canFit(int toolClass){
        return false;
    }
}
