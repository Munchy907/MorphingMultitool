package uk.co.hailhydra.morphingmultitool.handlers;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.IShearable;
import uk.co.hailhydra.morphingmultitool.init.ModItems;
import uk.co.hailhydra.morphingmultitool.utility.MorphToolResources;
import uk.co.hailhydra.morphingmultitool.utility.NBTHelper;

import java.util.Objects;

public class MorphHandler {

    public static Boolean isMorphingTool(ItemStack toCheckStack){
        if (toCheckStack.isEmpty()){return false;}

        if (toCheckStack.isItemEqual(new ItemStack(ModItems.MORPHING_MULTI_TOOL))){
            return true;
        }

        //Temp
        return toCheckStack.getTagCompound() != null && toCheckStack.getTagCompound().hasKey(MorphToolResources.TAG_MMT_DATA);

    }

    public static NBTTagCompound createNBTData(ItemStack morphTool){
        if (morphTool.getTagCompound() != null && morphTool.getTagCompound().hasKey(MorphToolResources.TAG_MMT_DATA)){
            return morphTool.getTagCompound();
        }

        NBTTagCompound tagMorphTool = NBTHelper.getOrCreateStackTagCompound(morphTool);

        tagMorphTool.setTag(MorphToolResources.TAG_MMT_DATA, new NBTTagCompound());
        NBTTagCompound tagMorphData = tagMorphTool.getCompoundTag(MorphToolResources.TAG_MMT_DATA);
        tagMorphData.setTag(MorphToolResources.TAG_MMT_TOOLS, new NBTTagList());
        tagMorphData.setTag(MorphToolResources.TAG_MMT_LIST_NBT_TOOL_CLASSES, new NBTTagList());

        return tagMorphTool;

        //NBTTagCompound tagMorphData = tagMorphTool.getCompoundTag(MorphToolResources.TAG_MMT_DATA);
    }

}
