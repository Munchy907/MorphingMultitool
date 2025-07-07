package uk.co.hailhydra.morphingmultitool.handlers;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.Slot;
import net.minecraft.inventory.SlotCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemTool;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagShort;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.common.IShearable;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.input.Mouse;
import uk.co.hailhydra.morphingmultitool.MorphingMultiTool;
import uk.co.hailhydra.morphingmultitool.init.ModItems;
import uk.co.hailhydra.morphingmultitool.items.ItemMorphTool;
import uk.co.hailhydra.morphingmultitool.utility.MorphToolResources;
import uk.co.hailhydra.morphingmultitool.utility.MouseInputType;
import uk.co.hailhydra.morphingmultitool.utility.NBTHelper;

import java.util.Objects;
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
        tickCounter += 1;

        //TODO Remove tick counter on release
        if (event.phase == TickEvent.Phase.END && tickCounter >= 40){
            tickCounter = 0;
            EntityPlayerSP playerSP = Minecraft.getMinecraft().player;
            if (Minecraft.getMinecraft().isGamePaused() || playerSP == null || playerSP.world == null){return;}

/*            if (!playerSP.getHeldItemMainhand().isEmpty()){
                MorphingMultiTool.LOGGER.info(playerSP.getHeldItemMainhand().getItem().getToolClasses(playerSP.getHeldItemMainhand()));
            }*/

            if (!MorphHandler.isMorphingTool(playerSP.getHeldItemMainhand())){return;}

            ItemStack morphTool = playerSP.getHeldItemMainhand();

/*          Note: Minecraft method is bugged, doesn't change based on if player is in creative or not.
            It will always return 5.0 (creative reach distance), even when it should be 4.5 (survival reach distance)
            playerSP.getEntityAttribute(EntityPlayer.REACH_DISTANCE).getAttributeValue();*/
            double rayLength = (playerSP.isCreative()) ? INTERACTION_RANGE_CREATIVE : INTERACTION_RANGE_SURVIVAL;

            RayTraceResult rayResult = raycast(playerSP, rayLength);

            if (rayResult == null || rayResult.typeOfHit != RayTraceResult.Type.BLOCK){return;}

            IBlockState blockState = playerSP.world.getBlockState(rayResult.getBlockPos());
            Block targetBlock = blockState.getBlock();
            String toolName = targetBlock.getHarvestTool(blockState);

            if (targetBlock instanceof IShearable){
                toolName = new ItemStack(Items.SHEARS).getDisplayName().toLowerCase();
            }

            if (toolName == null){
                MorphingMultiTool.LOGGER.warn("Tool Name was null");

                NBTTagCompound tagStack = morphTool.getTagCompound();
                if (tagStack == null || !tagStack.hasKey(MorphToolResources.TAG_MMT_DATA)){
                    return;
                }

                NBTTagCompound tagMorphData = tagStack.getCompoundTag(MorphToolResources.TAG_MMT_DATA);
                if (!tagMorphData.hasKey(MorphToolResources.TAG_MMT_TOOLS)){
                    MorphingMultiTool.LOGGER.warn("Tool has Morph Data but not Tools Data! How?!");
                }

                NBTTagList tagToolsData = tagMorphData.getTagList(MorphToolResources.TAG_MMT_TOOLS, Constants.NBT.TAG_COMPOUND);
                if (tagToolsData.isEmpty()){return;}

                tagToolsData.getCompoundTagAt(0).setTag("Damage", new NBTTagShort((short) morphTool.getItemDamage()));

                //TODO: Better name as it's morph tool but that's already defined in scope
                ItemStack swapTool = new ItemStack(ModItems.MORPHING_MULTI_TOOL);

                swapTool.setTagCompound(tagStack);
                playerSP.setHeldItem(EnumHand.MAIN_HAND, swapTool);
                return;
            }

            if (morphTool.getItem().getToolClasses(morphTool).contains(toolName)){return;}

            NBTTagCompound tagStack = morphTool.getTagCompound();
            if (tagStack == null || !tagStack.hasKey(MorphToolResources.TAG_MMT_DATA)){
                MorphingMultiTool.LOGGER.warn("No MMT_DATA?!");
                return;}

            MorphingMultiTool.LOGGER.info("Has MMT_DATA");

            NBTTagCompound tagMorphData = tagStack.getCompoundTag(MorphToolResources.TAG_MMT_DATA);
            if (!tagMorphData.hasKey(MorphToolResources.TAG_MMT_TOOLS)){
                MorphingMultiTool.LOGGER.warn("Tool has Morph Data but not Tools Data! How?!");
            }

            MorphingMultiTool.LOGGER.info("Has MMT_TOOLS");

            NBTTagList tagToolsData = tagMorphData.getTagList(MorphToolResources.TAG_MMT_TOOLS, Constants.NBT.TAG_COMPOUND);
            if (tagToolsData.isEmpty()){return;}
            MorphingMultiTool.LOGGER.info("MMT_Tools Not Empty");

            //TODO: Remove NBTTagList toolClasses as it's redundant and requires making another NBTTagCompound
            NBTTagList tagToolClasses = tagMorphData.getTagList(MorphToolResources.TAG_MMT_LIST_NBT_TOOL_CLASSES, Constants.NBT.TAG_STRING);
            int toolPos = NBTHelper.tagListContainsString(tagToolClasses, toolName);
            if (toolPos == -1){return;}

            MorphingMultiTool.LOGGER.info(tagToolsData.getCompoundTagAt(0));
            MorphingMultiTool.LOGGER.info(tagToolsData.getCompoundTagAt(0).hasKey("id", 8));
            NBTTagCompound hold = tagToolsData.getCompoundTagAt(0);
            Item test = Item.getByNameOrId(tagToolsData.getCompoundTagAt(0).getString("id"));
            MorphingMultiTool.LOGGER.info(test);
            ItemStack pickedTool = new ItemStack(hold);
            MorphingMultiTool.LOGGER.info(pickedTool);
            pickedTool.setTagCompound(tagStack);
            MorphingMultiTool.LOGGER.info(pickedTool);
            playerSP.setHeldItem(EnumHand.MAIN_HAND, pickedTool);
            MorphingMultiTool.LOGGER.info(toolName);
        }
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

            ItemStack carriedStack = playerSP.inventory.getItemStack();
            if (carriedStack.isEmpty()){return;}

            Slot invSlot = guiContainer.getSlotUnderMouse();
            if (invSlot == null){return;}



            if (!(invSlot instanceof SlotCrafting) && invSlot.isEnabled()){

                ItemStack slotStack = invSlot.getStack();
                MorphingMultiTool.LOGGER.info(carriedStack.getDisplayName());
                MorphingMultiTool.LOGGER.info(slotStack.getDisplayName());
                //TODO: Network Shiz
                if (carriedStack.getItem() instanceof ItemMorphTool){
                    Set<String> toolClass = slotStack.getItem().getToolClasses(slotStack);
                    if (toolClass.isEmpty()){return;}

                    ((ItemMorphTool) carriedStack.getItem()).addTool(carriedStack, slotStack, toolClass);
                    mouseEvent.setCanceled(true);
                    cancelButton = true;
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

}
