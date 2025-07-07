package uk.co.hailhydra.morphingmultitool.items;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.*;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;
import uk.co.hailhydra.morphingmultitool.MorphingMultiTool;
import uk.co.hailhydra.morphingmultitool.handlers.MorphHandler;
import uk.co.hailhydra.morphingmultitool.utility.MorphToolResources;

import java.util.Set;

public class ItemMorphTool extends ItemModBase {
    public ItemMorphTool() {
        super("tool");
    }

    @Override
    public void onCreated(ItemStack stack, World worldIn, EntityPlayer playerIn) {
        stack.setTagCompound(MorphHandler.createNBTData(stack));
        super.onCreated(stack, worldIn, playerIn);
    }

    public void addTool(ItemStack morphTool, ItemStack toAddStack, Set<String> toolClasses){
        //NBTTagCompound morphNBTData = NBTHelper.getOrCreateStackTagCompound(morphTool);
        //Statement should never be true

        if (morphTool.isEmpty() || toAddStack.isEmpty() || toolClasses.isEmpty()){return;}

        if (morphTool.getTagCompound() == null){
            NBTTagCompound tagStack = MorphHandler.createNBTData(morphTool);
            NBTTagCompound tagMorphData = tagStack.getCompoundTag(MorphToolResources.TAG_MMT_DATA);
            NBTTagList tagTools = tagMorphData.getTagList(MorphToolResources.TAG_MMT_TOOLS, Constants.NBT.TAG_COMPOUND);
            NBTTagList tagToolClass = tagMorphData.getTagList(MorphToolResources.TAG_MMT_LIST_NBT_TOOL_CLASSES, Constants.NBT.TAG_STRING);
            tagToolClass.appendTag(new NBTTagString(toolClasses.iterator().next()));
            NBTTagCompound tagToolData = new NBTTagCompound();
            ResourceLocation toolResource = Item.REGISTRY.getNameForObject(toAddStack.getItem());
            if (toolResource == null){return;}
            tagToolData.setTag("Slot", new NBTTagByte((byte) 0));
            tagToolData.setTag("id", new NBTTagString(toolResource.toString()));
            tagToolData.setTag("tool_class", new NBTTagString(toolClasses.iterator().next()));
            tagToolData.setTag("Count", new NBTTagByte((byte) 1));
            tagToolData.setTag("Damage", new NBTTagShort((short) toAddStack.getItemDamage()));
            tagTools.appendTag(tagToolData);
            MorphingMultiTool.LOGGER.info(tagTools.tagCount());
            //tagMorphData.setTag(toAddStack.getDisplayName(), NBTHelper.getOrCreateStackTagCompound(toAddStack));
            toAddStack.shrink(1);
            return;
        }

        return;

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
