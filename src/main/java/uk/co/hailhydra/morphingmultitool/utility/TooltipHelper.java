/**
 * This class is a copy of a class by <Vazkii>. It's distributed as
 * part of the Psi Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Psi
 *
 * Psi is Open Source and distributed under the
 * Psi License: http://psi.vazkii.us/license.php
 */

package uk.co.hailhydra.morphingmultitool.utility;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;

public class TooltipHelper {
    @SideOnly(Side.CLIENT)
    public static void tooltipOnShift(List<String> tooltip, Runnable runnable){
        if (GuiScreen.isShiftKeyDown()){
            runnable.run();
        }
        else{
            //TODO: Move to own class and not be hardcoded
            addToTooltip(tooltip, "util.morphingmultitool.shiftForInfo");
        }
    }

    @SideOnly(Side.CLIENT)
    public static void addToTooltip(List<String> tooltip, String s, Object... format) {
        s = local(s).replaceAll("&", "\u00a7");

        Object[] formatVals = new String[format.length];
        for(int i = 0; i < format.length; i++)
            formatVals[i] = local(format[i].toString()).replaceAll("&", "\u00a7");

        if(formatVals.length > 0)
            s = String.format(s, formatVals);

        tooltip.add(s);
    }

    @SideOnly(Side.CLIENT)
    public static String local(String s) {
        return I18n.format(s);
    }
}
