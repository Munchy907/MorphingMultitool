package uk.co.hailhydra.morphingmultitool.handlers;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.event.entity.item.ItemTossEvent;
import net.minecraftforge.event.entity.player.PlayerDestroyItemEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import uk.co.hailhydra.morphingmultitool.init.ModItems;
import uk.co.hailhydra.morphingmultitool.items.ItemMorphTool;
import uk.co.hailhydra.morphingmultitool.utility.MorphToolResources;
import uk.co.hailhydra.morphingmultitool.utility.NBTHelper;

public class MorphHandler {

    public static final MorphHandler INSTANCE = new MorphHandler();

    private static final String TOOL_DATA_ID = "id";
    private static final String TOOL_DATA_COUNT = "Count";
    private static final String TOOL_DATA_DAMAGE = "Damage";
    private static final String TOOL_DATA_POSITION = "pos";

    //TODO: Fix minor bug where if adds to the first free slot and not the slot your currently using
    @SubscribeEvent
    public void onItemBreaks(PlayerDestroyItemEvent event){
        if (event.getHand() == null || !MorphHandler.isMorphingTool(event.getOriginal())
                || event.getOriginal().getItem().getToolClasses(event.getOriginal()).isEmpty()) {return;}

        EntityPlayer entityPlayer = event.getEntityPlayer();
        ItemStack brokeTool = event.getOriginal();
        removeTool(brokeTool, brokeTool.getItem().getToolClasses(event.getOriginal()).iterator().next());

        if (!entityPlayer.getEntityWorld().isRemote){
            ItemStack morphTool = new ItemStack(ModItems.MORPHING_MULTI_TOOL);
            morphTool.setTagCompound(brokeTool.getTagCompound());
            entityPlayer.getEntityWorld().spawnEntity(new EntityItem(entityPlayer.world, entityPlayer.posX,
                    entityPlayer.posY, entityPlayer.posZ, morphTool));
        }
    }

    //TODO: If implemented should be done in morphHandler
    @SubscribeEvent
    public void onToolDrop(ItemTossEvent tossEvent){
        ItemStack tool = tossEvent.getEntityItem().getItem();
        if (MorphHandler.isMorphingTool(tool) && !(tool.getItem() instanceof ItemMorphTool)){
            EntityItem droppedItem = tossEvent.getEntityItem();
            ItemStack morphTool = new ItemStack(ModItems.MORPHING_MULTI_TOOL);
            morphTool.setTagCompound(tool.getTagCompound());
            droppedItem.setItem(morphTool);
        }
    }

    public static Boolean isMorphingTool(ItemStack stack){
        if (stack.isEmpty() || stack.getTagCompound() == null){return false;}

        if (stack.isItemEqual(new ItemStack(ModItems.MORPHING_MULTI_TOOL))){
            return true;
        }

        return isValidMorphStackNBT(stack.getTagCompound());
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
        else if (!tagToolData.hasKey(TOOL_DATA_POSITION, Constants.NBT.TAG_BYTE)){return false;}
        else if (!tagToolData.hasKey(TOOL_DATA_COUNT, Constants.NBT.TAG_BYTE)){return false;}
        else return tagToolData.hasKey(TOOL_DATA_DAMAGE, Constants.NBT.TAG_SHORT);
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

        NBTTagCompound tagToolData = createNBTToolData(toolResource.toString(), getNextPos(tagMorphData), (short) toAddStack.getItemDamage());

        tagMorphData.setTag(toolClass, tagToolData);
        toAddStack.shrink(1);
        return true;
    }

    private static NBTTagCompound createNBTToolData(String ID, byte pos, short damage){
        NBTTagCompound tagToolData = new NBTTagCompound();

        tagToolData.setString(TOOL_DATA_ID, ID);
        tagToolData.setByte(TOOL_DATA_POSITION, pos);
        tagToolData.setByte(TOOL_DATA_COUNT, (byte) 1);
        tagToolData.setShort(TOOL_DATA_DAMAGE, damage);
        return tagToolData;
    }

    private static byte getNextPos(NBTTagCompound tagMorphData){
        byte lastPos = -1;

        for (String toolDataKey: tagMorphData.getKeySet()) {
            if (tagMorphData.hasKey(toolDataKey, Constants.NBT.TAG_COMPOUND)){
                byte pos = tagMorphData.getCompoundTag(toolDataKey).getByte(TOOL_DATA_POSITION);
                if (pos > lastPos){lastPos = pos;}
            }
        }
        return ++lastPos;
    }

    public static ItemStack removeTool(ItemStack morphTool, String toolClass){
        if (!isMorphingTool(morphTool) || toolClass.isEmpty()){return new ItemStack(Items.AIR);}

        ItemStack tool = getItemFromToolClass(morphTool.getTagCompound().getCompoundTag(MorphToolResources.TAG_MMT_DATA), toolClass);
        if (tool.isEmpty()){return tool;}

        morphTool.getTagCompound().getCompoundTag(MorphToolResources.TAG_MMT_DATA).removeTag(toolClass);
        return tool;
    }

    public static ItemStack removeTool(ItemStack morphTool){
        if (!isMorphingTool(morphTool)){return ItemStack.EMPTY;}

        NBTTagCompound tagMorphData = morphTool.getTagCompound().getCompoundTag(MorphToolResources.TAG_MMT_DATA);

        byte lastPos = -1;
        String toolClass = "";
        for (String toolDataKey: tagMorphData.getKeySet()) {
            if (tagMorphData.hasKey(toolDataKey, Constants.NBT.TAG_COMPOUND)){
                byte pos = tagMorphData.getCompoundTag(toolDataKey).getByte(TOOL_DATA_POSITION);
                if (pos > lastPos){
                    lastPos = pos;
                    toolClass = toolDataKey;
                }
            }
        }

        if (toolClass.isEmpty()){return ItemStack.EMPTY;}
        else return removeTool(morphTool, toolClass);
    }

    public static void updateToolDamage(ItemStack tool){
        if (isMorphingTool(tool)){
            NBTTagCompound tagMorphData = tool.getTagCompound().getCompoundTag(MorphToolResources.TAG_MMT_DATA);
            if (!tool.getItem().getToolClasses(tool).isEmpty()){
                String toolClass = tool.getItem().getToolClasses(tool).iterator().next();
                if (tagMorphData.hasKey(toolClass, Constants.NBT.TAG_COMPOUND)){
                    NBTTagCompound tagToolData = tagMorphData.getCompoundTag(toolClass);
                    tagToolData.setShort(MorphHandler.TOOL_DATA_DAMAGE, (short) tool.getItemDamage());
                }
            }
        }
    }



}
