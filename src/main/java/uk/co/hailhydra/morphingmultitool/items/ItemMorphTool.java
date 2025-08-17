package uk.co.hailhydra.morphingmultitool.items;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import uk.co.hailhydra.morphingmultitool.MorphingMultiTool;
import uk.co.hailhydra.morphingmultitool.handlers.MorphHandler;
import uk.co.hailhydra.morphingmultitool.utility.MorphToolResources;
import uk.co.hailhydra.morphingmultitool.utility.TooltipHelper;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class ItemMorphTool extends ItemModBase {
    public ItemMorphTool() {
        super("tool");
    }

    @Override
    public void onCreated(ItemStack stack, World worldIn, EntityPlayer playerIn) {
        stack.setTagCompound(MorphHandler.createNBTData(stack));
        super.onCreated(stack, worldIn, playerIn);
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        if (stack.getTagCompound() == null || !MorphHandler.isValidMorphStackNBT(stack.getTagCompound()))
            {return;}

        NBTTagCompound tagMorphData = stack.getTagCompound().getCompoundTag(MorphToolResources.TAG_MMT_DATA);
        if (tagMorphData.isEmpty())
            {return;}

        if (tooltip.isEmpty()){return;}

        TooltipHelper.tooltipOnShift(tooltip, () -> {
            List<String> morphDataKeys = tagMorphData.getKeySet().stream()
                    .sorted((key1, key2) -> {
                        //TODO: Either move tool data to own class or make it public in morphing handler
                        int pos1 = tagMorphData.getCompoundTag(key1).getByte("pos");
                        int pos2 = tagMorphData.getCompoundTag(key2).getByte("pos");
                        return Integer.compare(pos1, pos2);
                    })
                    .collect(Collectors.toList());

            for (String toolClass: morphDataKeys) {
                //TODO: Replace hacky substring method of capitalizing first letter of tool class, with a utility method that capitalizes the first letter & every letter starting after a space
                tooltip.add(toolClass.substring(0,1).toUpperCase() + toolClass.substring(1) + ": " + new ItemStack(tagMorphData.getCompoundTag(toolClass)).getDisplayName());
            }
        });

        //super.addInformation(stack, worldIn, tooltip, flagIn);
    }
}
