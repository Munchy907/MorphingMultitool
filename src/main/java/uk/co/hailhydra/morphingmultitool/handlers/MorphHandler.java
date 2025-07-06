package uk.co.hailhydra.morphingmultitool.handlers;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import uk.co.hailhydra.morphingmultitool.init.ModItems;
import uk.co.hailhydra.morphingmultitool.utility.MorphToolResources;
import uk.co.hailhydra.morphingmultitool.utility.NBTHelper;

public class MorphHandler {

    public static Boolean isMorphingTool(ItemStack toCompareStack){
        if (toCompareStack.isEmpty()){return false;}

        if (toCompareStack.isItemEqual(new ItemStack(ModItems.MORPHING_MULTI_TOOL))){
            return true;
        }

        //Temp
        return false;
    }

    public static NBTTagCompound createNBTData(ItemStack morphTool){
        if (morphTool.getTagCompound() != null && morphTool.getTagCompound().hasKey(MorphToolResources.TAG_MMT_DATA)){
            return morphTool.getTagCompound();
        }

        NBTTagCompound tagMorphTool = NBTHelper.getOrCreateStackTagCompound(morphTool);

        tagMorphTool.setTag(MorphToolResources.TAG_MMT_DATA, new NBTTagCompound());

        return tagMorphTool;

        //NBTTagCompound tagMorphData = tagMorphTool.getCompoundTag(MorphToolResources.TAG_MMT_DATA);
    }

}
