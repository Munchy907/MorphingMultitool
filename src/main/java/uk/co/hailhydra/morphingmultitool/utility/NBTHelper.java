package uk.co.hailhydra.morphingmultitool.utility;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.util.Constants;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;

/*
    This class is taken from Pahimar's Lets Mod Reboot Repo:
    https://github.com/pahimar/LetsModReboot/blob/master/src/main/java/com/pahimar/letsmodreboot/utility/NBTHelper.java
    Explained in his video:
    Let's Mod Reboot - Episode 12 - Named Binary Tag (NBT)
    Link: https://www.youtube.com/watch?v=l4rqJibM17k
    Come back to us king
 */
public class NBTHelper {

/*
    private static void initNBTTagCompound(ItemStack itemStack)
    {
        if (itemStack.getTagCompound() == null)
        {
            itemStack.setTagCompound(new NBTTagCompound());
        }
    }*/

    private static NBTTagCompound initNBTTagCompound(ItemStack itemStack)
    {
        if (itemStack.getTagCompound() == null)
        {
            itemStack.setTagCompound(new NBTTagCompound());
        }

        return itemStack.getTagCompound();
    }

    public static NBTTagCompound getOrCreateStackTagCompound(ItemStack itemStack){
        return initNBTTagCompound(itemStack);
    }

    public static boolean hasTag(ItemStack itemStack, String keyName)
    {
        return itemStack != null && itemStack.getTagCompound() != null && itemStack.getTagCompound().hasKey(keyName);
    }

    public static void removeTag(ItemStack itemStack, String keyName)
    {
        if (itemStack.getTagCompound() != null)
        {
            itemStack.getTagCompound().removeTag(keyName);
        }
    }

    public static void setTagCompound(ItemStack itemStack, String keyName, NBTTagCompound keyValue){
        NBTTagCompound tagCompound = initNBTTagCompound(itemStack);

        tagCompound.setTag(keyName, keyValue);
    }

    public static NBTTagCompound getTagCompound(ItemStack itemStack, String keyName){
        NBTTagCompound tagCompound = initNBTTagCompound(itemStack);

        if (!tagCompound.hasKey(keyName)){
            setTagCompound(itemStack, keyName, new NBTTagCompound());
        }

        return tagCompound.getCompoundTag(keyName);
    }

    public static void setTagList(ItemStack itemStack, String keyName, NBTTagList keyValue){
        NBTTagCompound tagCompound = initNBTTagCompound(itemStack);

        tagCompound.setTag(keyName, keyValue);
    }

    public static NBTTagList getTagList(ItemStack itemStack, String keyName, int type){
        NBTTagCompound tagCompound = initNBTTagCompound(itemStack);

        if (!tagCompound.hasKey(keyName)){
            setTagList(itemStack, keyName, new NBTTagList());
        }


        return tagCompound.getTagList(keyName, type);
    }

    public static int tagListContainsString(NBTTagList tagList, String searchKey){
        if (tagList.isEmpty()){return -1;}
        if (tagList.getTagType() != Constants.NBT.TAG_STRING){return -1;}

        for (int count = 0; count < tagList.tagCount(); count++) {
            if (tagList.getStringTagAt(count).equals(searchKey)){
                return count;
            }
        }

        return -1;
    }
}
