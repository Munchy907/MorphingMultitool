package uk.co.hailhydra.morphingmultitool.handlers;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.gui.inventory.GuiContainerCreative;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.Slot;
import net.minecraft.inventory.SlotCrafting;
import net.minecraft.item.ItemShears;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemTool;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.common.IShearable;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.input.Mouse;
import uk.co.hailhydra.morphingmultitool.init.ModItems;
import uk.co.hailhydra.morphingmultitool.items.ItemMorphTool;
import uk.co.hailhydra.morphingmultitool.network.NetworkHandler;
import uk.co.hailhydra.morphingmultitool.network.packet.*;
import uk.co.hailhydra.morphingmultitool.utility.MorphToolResources;
import uk.co.hailhydra.morphingmultitool.utility.MouseInputType;
import uk.co.hailhydra.morphingmultitool.utility.ToolType;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@SideOnly(Side.CLIENT)
public class ClientHandler {
    public static final ClientHandler INSTANCE = new ClientHandler();
    public static final double INTERACTION_RANGE_SURVIVAL = 4.5;
    public static final double INTERACTION_RANGE_CREATIVE = 5.0;
    public static int tickCounter = 0;
    private boolean cancelButton = false;

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onClientTick(TickEvent.ClientTickEvent event) {

        EntityPlayerSP playerSP = Minecraft.getMinecraft().player;
        if (Minecraft.getMinecraft().isGamePaused() || Minecraft.getMinecraft().currentScreen != null || playerSP == null || playerSP.world == null){return;}
        World world = playerSP.world;

        if (!MorphHandler.isMorphingTool(playerSP.getHeldItemMainhand())){return;}

        ItemStack morphTool = playerSP.getHeldItemMainhand();

        double rayLength = (playerSP.isCreative()) ? INTERACTION_RANGE_CREATIVE : INTERACTION_RANGE_SURVIVAL;

        RayTraceResult rayResult = raycast(playerSP, rayLength);

        if (rayResult == null || rayResult.typeOfHit != RayTraceResult.Type.BLOCK){return;}

        BlockPos blockPos = rayResult.getBlockPos();
        IBlockState blockState = playerSP.world.getBlockState(blockPos);
        Block targetBlock = blockState.getBlock();

        String toolClass = getHarvestTool(world, blockState, targetBlock, blockPos);
        if (toolClass == null && targetBlock instanceof IShearable){toolClass = ToolType.SHEARS;}
        if (toolClass == null){return;}


        if (morphTool.getItem().getToolClasses(morphTool).contains(toolClass)){
                return;
        }

        NBTTagCompound tagStack = morphTool.getTagCompound();
        if (tagStack == null || !tagStack.hasKey(MorphToolResources.TAG_MMT_DATA)){
            return;}


        NBTTagCompound tagMorphData = tagStack.getCompoundTag(MorphToolResources.TAG_MMT_DATA);
        if (tagMorphData.isEmpty()){return;}


        if (!tagMorphData.hasKey(toolClass)){
                return;
        }

        MorphHandler.updateToolDamage(morphTool);
        NetworkHandler.INSTANCE.sendToServer(new PacketMorphToTool(tagStack, toolClass));
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onInventorySlotInteraction(GuiScreenEvent.MouseInputEvent.Pre mouseEvent){
        if (!mouseEvent.isCanceled() && mouseEvent.getGui() instanceof GuiContainer guiContainer && Mouse.getEventButton() == MouseInputType.RIGHT){

            if (cancelButton && !Mouse.getEventButtonState()){
                mouseEvent.setCanceled(true);
                cancelButton = false;
            }

            if (!Mouse.getEventButtonState()){return;}

            EntityPlayerSP playerSP = Minecraft.getMinecraft().player;
            if (playerSP == null){return;}

            ItemStack mouseStack = playerSP.inventory.getItemStack();
            if (mouseStack.isEmpty()){return;}

            Slot invSlot = guiContainer.getSlotUnderMouse();
            if (invSlot == null){return;}



            if (!(invSlot instanceof SlotCrafting) && invSlot.isEnabled()){
                if (mouseStack.getItem() instanceof ItemMorphTool){
                    ItemStack slotStack = invSlot.getStack();
                    if (slotStack.isEmpty()){
                        if (MorphHandler.isMorphingTool(mouseStack)){
                            if (mouseStack.getTagCompound() == null){return;}
                            NBTTagCompound morphData = mouseStack.getTagCompound().getCompoundTag(MorphToolResources.TAG_MMT_DATA);
                            if (morphData.isEmpty()){return;}

                            NetworkHandler.INSTANCE.sendToServer(new PacketRemoveTool(mouseStack, invSlot.slotNumber));

                            mouseEvent.setCanceled(true);
                            cancelButton = true;
                        }
                    }
                    else{
                        Set<String> toolClasses = slotStack.getItem().getToolClasses(slotStack);
                        if (toolClasses.isEmpty() && !(slotStack.getItem() instanceof ItemShears)){return;}

                        NetworkHandler.INSTANCE.sendToServer(new PacketToolAdded(invSlot.slotNumber, mouseStack));
                        mouseEvent.setCanceled(true);
                        cancelButton = true;
                    }
                }
            }

        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onItemMousePickup(GuiScreenEvent.MouseInputEvent.Pre mouseEvent){
        if (!mouseEvent.isCanceled() && mouseEvent.getGui() instanceof GuiContainer guiContainer && (Mouse.getEventButton() == MouseInputType.LEFT || Mouse.getEventButton() == MouseInputType.RIGHT)){

            EntityPlayerSP playerSP = Minecraft.getMinecraft().player;
            if (!playerSP.inventory.getItemStack().isEmpty()){return;}

            if (guiContainer.getSlotUnderMouse() != null){
                Slot slot = guiContainer.getSlotUnderMouse();
                ItemStack slotStack = slot.getStack().copy();
                if (MorphHandler.isMorphingTool(slotStack) && !(slotStack.getItem() instanceof ItemMorphTool)){
                    MorphHandler.updateToolDamage(slotStack);
                    ItemStack morphTool = new ItemStack(ModItems.MORPHING_MULTI_TOOL);
                    morphTool.setTagCompound(slotStack.getTagCompound());
                    slot.putStack(morphTool);
                    int slotID = (guiContainer instanceof GuiContainerCreative) ? slot.getSlotIndex() : slot.slotNumber;
                    NetworkHandler.INSTANCE.sendToServer(new PacketMorphOnMousePickup(slotID));
                }


            }
        }
    }

    public static RayTraceResult raycast(Entity entity, double rayLength){
        Vec3d startVec = new Vec3d(entity.posX, entity.posY, entity.posZ);
        if (entity instanceof EntityPlayer){startVec = startVec.add(0, entity.getEyeHeight(), 0);}

        Vec3d lookVec = entity.getLookVec();

        Vec3d endVec = startVec.add(lookVec.normalize().scale(rayLength));

        return entity.world.rayTraceBlocks(startVec, endVec);
    }

    private static final Map<String, ItemStack> testTools = new HashMap<>();
    static {
        testTools.put(ToolType.SHOVEL, new ItemStack(Items.WOODEN_SHOVEL));
        testTools.put(ToolType.PICKAXE, new ItemStack(Items.WOODEN_PICKAXE));
        testTools.put(ToolType.AXE, new ItemStack(Items.WOODEN_AXE));
    }

/*  modified version of McJty's showHarvestInfo method from theOneProbe:
    https://github.com/McJtyMods/TheOneProbe/blob/1.12/src/main/java/mcjty/theoneprobe/apiimpl/providers/HarvestInfoTools.java#L75
    McJty's comments were added on purpose because I thought they were funny (well last one, but require others for context)
*/
    //TODO swap from using wood tools, and test with the in the multi-tool
    public static String getHarvestTool(World world, IBlockState blockState, Block block, BlockPos blockPos){
        String harvestTool = block.getHarvestTool(blockState);
        if (harvestTool != null){
            //TODO: Config option if should swap if tool harvest level >= block hardness
            return harvestTool;
        }

        // The block doesn't have an explicitly-set harvest tool, so we're going to test our wooden tools against the block.
        float blockHardness = blockState.getBlockHardness(world, blockPos);
        if (blockHardness >= 0f){
            for (Map.Entry<String, ItemStack> testToolEntry : testTools.entrySet()){
                // loop through our test tools until we find a winner.
                ItemStack testTool = testToolEntry.getValue();

                if (testTool != null && testTool.getItem() instanceof ItemTool toolItem){
                    if (testTool.getDestroySpeed(blockState) >= toolItem.toolMaterial.getEfficiency()){
                        //BINGO
                        harvestTool = testToolEntry.getKey();
                        return harvestTool;
                    }
                }
            }
        }
        if (blockState instanceof IShearable){
            return ToolType.SHEARS;}

        return null;
    }

}
