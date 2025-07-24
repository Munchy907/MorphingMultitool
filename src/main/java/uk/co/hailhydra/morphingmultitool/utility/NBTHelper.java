package uk.co.hailhydra.morphingmultitool.utility;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

/*
    This class is taken from Pahimar's Lets Mod Reboot Repo:
    https://github.com/pahimar/LetsModReboot/blob/master/src/main/java/com/pahimar/letsmodreboot/utility/NBTHelper.java
    Explained in his video:
    Let's Mod Reboot - Episode 12 - Named Binary Tag (NBT)
    Link: https://www.youtube.com/watch?v=l4rqJibM17k
    Come back to us king
 */
public class NBTHelper {


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
}
