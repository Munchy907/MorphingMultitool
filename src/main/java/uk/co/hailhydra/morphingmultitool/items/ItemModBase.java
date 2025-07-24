package uk.co.hailhydra.morphingmultitool.items;

import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import uk.co.hailhydra.morphingmultitool.MorphingMultiTool;
import uk.co.hailhydra.morphingmultitool.Tags;

public abstract class ItemModBase extends Item {

    public ItemModBase(String registryName){
        setRegistryName(Tags.MODID, registryName);
        setTranslationKey(Tags.MODID + "." + registryName);
        setMaxStackSize(1);
        setCreativeTab(MorphingMultiTool.MORPH_MULTI_TOOL_TAB);
    }

    @SideOnly(Side.CLIENT)
    public void initModel() {
        if (getRegistryName() == null){return;}
        ModelLoader.setCustomModelResourceLocation(this, 0, new ModelResourceLocation(getRegistryName(), "inventory"));
    }

}
