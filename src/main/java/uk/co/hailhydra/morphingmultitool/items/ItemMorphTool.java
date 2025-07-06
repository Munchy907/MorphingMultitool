package uk.co.hailhydra.morphingmultitool.items;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;
import org.jetbrains.annotations.Nullable;
import uk.co.hailhydra.morphingmultitool.handlers.MorphHandler;
import uk.co.hailhydra.morphingmultitool.utility.MorphToolResources;
import uk.co.hailhydra.morphingmultitool.utility.NBTHelper;

import java.util.List;

public class ItemMorphTool extends ItemModBase {
    public ItemMorphTool() {
        super("tool");
    }

    @Override
    public void onCreated(ItemStack stack, World worldIn, EntityPlayer playerIn) {
        stack.setTagCompound(MorphHandler.createNBTData(stack));
        super.onCreated(stack, worldIn, playerIn);
    }

    public static void addTool(ItemStack morphTool, ItemStack toAddStack, String toolClass){
        //NBTTagCompound morphNBTData = NBTHelper.getOrCreateStackTagCompound(morphTool);
        //Statement should never be true
        if (NBTHelper.getOrCreateStackTagCompound(morphTool) == null){return;}

        if (NBTHelper.getOrCreateStackTagCompound(morphTool).hasKey(MorphToolResources.TAG_MMT_DATA)){return;}

        if (!NBTHelper.hasTag(morphTool, MorphToolResources.TAG_MMT_LIST_NBT_TOOL_CLASSES)){
            NBTHelper.setTagList(morphTool, MorphToolResources.TAG_MMT_LIST_NBT_TOOL_CLASSES, new NBTTagList());
        }

        NBTTagList toolClassNBT = NBTHelper.getTagList(morphTool, MorphToolResources.TAG_MMT_LIST_NBT_TOOL_CLASSES, Constants.NBT.TAG_STRING);

        if (NBTHelper.tagListContainsString(toolClassNBT, toolClass)){return;}

        toolClassNBT.appendTag(new NBTTagString(toolClass));

        NBTTagCompound toAddStackNBT = NBTHelper.getOrCreateStackTagCompound(toAddStack);
        //ItemStack test = new ItemStack()
        //toAddStackNBT.setString();

        NBTHelper.setTagCompound(morphTool, "MMT" + toolClass, NBTHelper.getOrCreateStackTagCompound(toAddStack));

    }

    public static ItemStack removeTool(ItemStack morphTool, int itemPos){
        return new ItemStack(Items.AIR);
    }

    private boolean canFit(int toolClass){
        return false;
    }
}
