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

    public boolean addTool(ItemStack morphTool, ItemStack toAddStack, String toolClass){
        if (morphTool.isEmpty() || toAddStack.isEmpty() || toolClass.isEmpty()){return false;}

        NBTTagCompound tagStack = morphTool.getTagCompound();

        if (tagStack == null || !tagStack.hasKey(MorphToolResources.TAG_MMT_DATA)){
            tagStack = MorphHandler.createNBTData(morphTool);
        }

        NBTTagCompound tagMorphData = tagStack.getCompoundTag(MorphToolResources.TAG_MMT_DATA);
        if (tagMorphData.hasKey(toolClass, Constants.NBT.TAG_COMPOUND)){return false;}
        NBTTagCompound tagToolData = new NBTTagCompound();

        ResourceLocation toolResource = Item.REGISTRY.getNameForObject(toAddStack.getItem());
        if (toolResource == null){return false;}

        //Don't know what this is/was for
        //tagToolData.setTag("Slot", new NBTTagByte((byte) 0));

        tagToolData.setString("id", toolResource.toString());
        tagToolData.setByte("Count", (byte) 1) ;
        tagToolData.setShort("Damage", (short) toAddStack.getItemDamage());

        tagMorphData.setTag(toolClass, tagToolData);
        toAddStack.shrink(1);
        return true;

/*        if (NBTHelper.getOrCreateStackTagCompound(morphTool).hasKey(MorphToolResources.TAG_MMT_DATA)){return;}

        if (!NBTHelper.hasTag(morphTool, MorphToolResources.TAG_MMT_LIST_NBT_TOOL_CLASSES)){
            NBTHelper.setTagList(morphTool, MorphToolResources.TAG_MMT_LIST_NBT_TOOL_CLASSES, new NBTTagList());
        }

        NBTTagList toolClassNBT = NBTHelper.getTagList(morphTool, MorphToolResources.TAG_MMT_LIST_NBT_TOOL_CLASSES, Constants.NBT.TAG_STRING);

        if (NBTHelper.tagListContainsString(toolClassNBT, toolClass)){return;}

        toolClassNBT.appendTag(new NBTTagString(toolClass));

        NBTTagCompound toAddStackNBT = NBTHelper.getOrCreateStackTagCompound(toAddStack);
        //ItemStack test = new ItemStack()
        //toAddStackNBT.setString();

        NBTHelper.setTagCompound(morphTool, "MMT" + toolClass, NBTHelper.getOrCreateStackTagCompound(toAddStack));*/

    }

    public ItemStack removeTool(ItemStack morphTool, int itemPos){
        return new ItemStack(Items.AIR);
    }

    private boolean canFit(int toolClass){
        return false;
    }
}
