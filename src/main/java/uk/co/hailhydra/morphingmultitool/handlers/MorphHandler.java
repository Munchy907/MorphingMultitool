package uk.co.hailhydra.morphingmultitool.handlers;

import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.Constants;
import uk.co.hailhydra.morphingmultitool.MorphingMultiTool;
import uk.co.hailhydra.morphingmultitool.init.ModItems;
import uk.co.hailhydra.morphingmultitool.utility.MorphToolResources;
import uk.co.hailhydra.morphingmultitool.utility.NBTHelper;

public class MorphHandler {

    private static final String[] tagToolDataKeys = {"id", "Count", "Damage"};

    private static final String TOOL_DATA_ID = "id";
    private static final String TOOL_DATA_COUNT = "Count";
    private static final String TOOL_DATA_DAMAGE = "Damage";
    //private static final String TOOL_DATA_CLASS = "Class";

    public static Boolean isMorphingTool(ItemStack stack){
        if (stack.isEmpty() || stack.getTagCompound() == null){return false;}

        if (stack.isItemEqual(new ItemStack(ModItems.MORPHING_MULTI_TOOL))){
            return true;
        }

        //Temp
        return isValidMorphStackNBT(stack.getTagCompound());
        //return stack.getTagCompound() != null && stack.getTagCompound().hasKey(MorphToolResources.TAG_MMT_DATA);

    }

    public static NBTTagCompound createNBTData(ItemStack morphTool){
        if (morphTool.getTagCompound() != null && morphTool.getTagCompound().hasKey(MorphToolResources.TAG_MMT_DATA)){
            return morphTool.getTagCompound();
        }

        NBTTagCompound tagMorphTool = NBTHelper.getOrCreateStackTagCompound(morphTool);

        tagMorphTool.setTag(MorphToolResources.TAG_MMT_DATA, new NBTTagCompound());

        return tagMorphTool;
    }

    public static ItemStack getItemFromToolClass(NBTTagCompound morphData, String toolClass){

        ItemStack emptyStack = new ItemStack(Items.AIR);

        if (morphData.isEmpty()){return emptyStack;}

        if (!isValidMorphDataNBT(morphData)){return emptyStack;}

        for (String toolDataKey: morphData.getKeySet()) {
            if (toolClass.equals(toolDataKey)){
                return new ItemStack(morphData.getCompoundTag(toolDataKey));
            }
        }
        return emptyStack;
    }

    public static boolean isValidMorphStackNBT(NBTTagCompound tagMorphStack){
        if (tagMorphStack.isEmpty() || !tagMorphStack.hasKey(MorphToolResources.TAG_MMT_DATA)){return false;}

        MorphingMultiTool.LOGGER.info("Passes IsValidMorphStack");
        return isValidMorphDataNBT(tagMorphStack.getCompoundTag(MorphToolResources.TAG_MMT_DATA));
    }

    public static boolean isValidMorphDataNBT(NBTTagCompound tagMorphData){
        if (tagMorphData.isEmpty()){return true;}

        if (tagMorphData.hasKey(MorphToolResources.TAG_MMT_DATA)) {return false;} // Is the stack nbt

        for (String toolKey: tagMorphData.getKeySet()) {
            if (toolKey.isEmpty() || tagMorphData.getCompoundTag(toolKey).isEmpty()){return false;}

            if (!isValidToolDataNBT(tagMorphData.getCompoundTag(toolKey))) {return false;}
        }
        return true;
    }

    private static boolean isValidToolDataNBT(NBTTagCompound tagToolData){
        if (!tagToolData.hasKey(TOOL_DATA_ID, Constants.NBT.TAG_STRING)){return false;}
        else return tagToolData.hasKey(TOOL_DATA_COUNT, Constants.NBT.TAG_BYTE);
        //else return tagToolData.hasKey(tagToolDataKeys[2], Constants.NBT.TAG_SHORT);
    }

    public static boolean addTool(ItemStack morphTool, ItemStack toAddStack, String toolClass){
        if (morphTool.isEmpty() || toAddStack.isEmpty() || toolClass.isEmpty()){return false;}

        NBTTagCompound tagStack = morphTool.getTagCompound();

        if (tagStack == null || !tagStack.hasKey(MorphToolResources.TAG_MMT_DATA)){
            tagStack = MorphHandler.createNBTData(morphTool);
        }

        NBTTagCompound tagMorphData = tagStack.getCompoundTag(MorphToolResources.TAG_MMT_DATA);
        if (tagMorphData.hasKey(toolClass, Constants.NBT.TAG_COMPOUND)){return false;}


        ResourceLocation toolResource = Item.REGISTRY.getNameForObject(toAddStack.getItem());
        if (toolResource == null){return false;}

        //Don't know what this is/was for
        //tagToolData.setTag("Slot", new NBTTagByte((byte) 0));

        NBTTagCompound tagToolData = createNBTToolData(toolResource.toString(), (short) toAddStack.getItemDamage());

        tagMorphData.setTag(toolClass, tagToolData);
        toAddStack.shrink(1);
        return true;
    }

    private static NBTTagCompound createNBTToolData(String ID, short damage){
        NBTTagCompound tagToolData = new NBTTagCompound();

        tagToolData.setString(TOOL_DATA_ID, ID);
        //tagToolData.setString(TOOL_DATA_CLASS, toolClass);
        tagToolData.setByte(TOOL_DATA_COUNT, (byte) 1);
        if (damage <= 0){return tagToolData;}

        tagToolData.setShort(TOOL_DATA_DAMAGE, (short) damage);
        return tagToolData;
    }

    public static ItemStack removeTool(ItemStack morphTool, String toolClass){
        if (!isMorphingTool(morphTool) || toolClass.isEmpty()){return new ItemStack(Items.AIR);}

        assert morphTool.getTagCompound() != null;
        ItemStack tool = getItemFromToolClass(morphTool.getTagCompound().getCompoundTag(MorphToolResources.TAG_MMT_DATA), toolClass);
        if (tool.isEmpty()){return tool;}

        morphTool.getTagCompound().getCompoundTag(MorphToolResources.TAG_MMT_DATA).removeTag(toolClass);
        return tool;
    }



}
