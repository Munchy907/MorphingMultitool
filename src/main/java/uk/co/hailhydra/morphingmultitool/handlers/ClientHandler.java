package uk.co.hailhydra.morphingmultitool.handlers;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.Slot;
import net.minecraft.inventory.SlotCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemTool;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.event.entity.item.ItemTossEvent;
import net.minecraftforge.event.entity.player.PlayerDropsEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.input.Mouse;
import uk.co.hailhydra.morphingmultitool.MorphingMultiTool;
import uk.co.hailhydra.morphingmultitool.init.ModItems;
import uk.co.hailhydra.morphingmultitool.items.ItemMorphTool;
import uk.co.hailhydra.morphingmultitool.network.NetworkHandler;
import uk.co.hailhydra.morphingmultitool.network.packet.PacketMorphToTool;
import uk.co.hailhydra.morphingmultitool.network.packet.PacketRemoveTool;
import uk.co.hailhydra.morphingmultitool.network.packet.PacketToolAdded;
import uk.co.hailhydra.morphingmultitool.network.packet.PacketUpdateMouseStack;
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
        tickCounter += 1;

        //TODO Remove tick counter on release
        if (event.phase == TickEvent.Phase.END && tickCounter >= 40){
            tickCounter = 0;
            EntityPlayerSP playerSP = Minecraft.getMinecraft().player;
            if (Minecraft.getMinecraft().isGamePaused() || Minecraft.getMinecraft().currentScreen != null || playerSP == null || playerSP.world == null){return;}
            World world = playerSP.world;

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

            BlockPos blockPos = rayResult.getBlockPos();
            IBlockState blockState = playerSP.world.getBlockState(blockPos);
            Block targetBlock = blockState.getBlock();
            //if (targetBlock.canHarvestBlock(world, blockPos, playerSP)){return;}

            String toolName = getHarvestTool(world, blockState, targetBlock, blockPos);
            if (toolName == null){return;}

            if (morphTool.getItem().getToolClasses(morphTool).contains(toolName)){
                MorphingMultiTool.LOGGER.info("Tool class & tool name the same");
                return;
            }

/*          if (targetBlock instanceof IShearable){
                toolName = new ItemStack(Items.SHEARS).getDisplayName().toLowerCase();
            }*/

/*            if (toolName == null){
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
            }*/

            NBTTagCompound tagStack = morphTool.getTagCompound();
            if (tagStack == null || !tagStack.hasKey(MorphToolResources.TAG_MMT_DATA)){
                MorphingMultiTool.LOGGER.warn("No MMT_DATA?!");
                return;}

            MorphingMultiTool.LOGGER.info("Has MMT_DATA");

            NBTTagCompound tagMorphData = tagStack.getCompoundTag(MorphToolResources.TAG_MMT_DATA);
            if (tagMorphData.isEmpty()){return;}


            ItemStack tool = MorphHandler.getItemFromToolClass(tagMorphData, toolName);
            if (tool.isEmpty()){return;}

            MorphingMultiTool.LOGGER.info("Sends packet to server the server");

            //tool.setTagCompound(tagStack);
            //playerSP.setHeldItem(EnumHand.MAIN_HAND, tool);
            NetworkHandler.INSTANCE.sendToServer(new PacketMorphToTool(tagStack, toolName));
            //ItemStack tool = new ItemStack()


            /*if (!tagMorphData.hasKey(MorphToolResources.TAG_MMT_TOOLS)){
                MorphingMultiTool.LOGGER.warn("Tool has Morph Data but not Tools Data! How?!");
                return;
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
            MorphingMultiTool.LOGGER.info("Picked tool: " + pickedTool);
            playerSP.setHeldItem(EnumHand.MAIN_HAND, pickedTool);
            MorphingMultiTool.LOGGER.info("Tool name: " + toolName);*/

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

            ItemStack mouseStack = playerSP.inventory.getItemStack();
            if (mouseStack.isEmpty()){return;}

            Slot invSlot = guiContainer.getSlotUnderMouse();
            if (invSlot == null){return;}



            if (!(invSlot instanceof SlotCrafting) && invSlot.isEnabled()){
                //TODO: Network Shiz & removing tools

                if (mouseStack.getItem() instanceof ItemMorphTool){
                    ItemStack slotStack = invSlot.getStack();
                    if (slotStack.isEmpty()){
                        if (MorphHandler.isMorphingTool(mouseStack)){
                            if (mouseStack.getTagCompound() == null){return;}
                            NBTTagCompound morphData = mouseStack.getTagCompound().getCompoundTag(MorphToolResources.TAG_MMT_DATA);
                            if (morphData.isEmpty()){return;}

                            //String toolClass = morphData.getKeySet().iterator().next();
                            //NetworkHandler.INSTANCE.sendToServer(new PacketRemoveTool(mouseStack, toolClass, invSlot.slotNumber));
                            NetworkHandler.INSTANCE.sendToServer(new PacketRemoveTool(mouseStack, invSlot.slotNumber));

                            mouseEvent.setCanceled(true);
                            cancelButton = true;
                        }
                    }
                    else{
                        Set<String> toolClass = slotStack.getItem().getToolClasses(slotStack);
                        if (toolClass.isEmpty()){return;}

                        //TODO: Have it check every tool class not just the first
                        //if (!MorphHandler.addTool(mouseStack, slotStack, toolClass.iterator().next())){return;}

                        NetworkHandler.INSTANCE.sendToServer(new PacketToolAdded(invSlot.slotNumber, mouseStack));
                        mouseEvent.setCanceled(true);
                        cancelButton = true;
                    }
                }
            }

        }
    }
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onItemMousePickup(GuiScreenEvent.MouseInputEvent.Post mouseEvent){
        if (!mouseEvent.isCanceled() && mouseEvent.getGui() instanceof GuiContainer guiContainer && Mouse.getEventButton() == MouseInputType.LEFT){
            EntityPlayerSP playerSP = Minecraft.getMinecraft().player;
            ItemStack mouseStack = playerSP.inventory.getItemStack();
            if (!mouseStack.isEmpty() && MorphHandler.isMorphingTool(mouseStack)){
               ItemStack morphTool = new ItemStack(ModItems.MORPHING_MULTI_TOOL);
               morphTool.setTagCompound(mouseStack.getTagCompound());
               playerSP.inventory.setItemStack(morphTool);
               NetworkHandler.INSTANCE.sendToServer(new PacketUpdateMouseStack(morphTool));
            }
        }
    }

/*
    @SubscribeEvent
    public void onToolDrop(ItemTossEvent tossEvent){
        if (tossEvent.getPlayer().isSneaking()){

            ItemStack tool = tossEvent.getEntityItem().getItem();
            if (MorphHandler.isMorphingTool(tool) && !(tool.getItem() instanceof ItemMorphTool)){
                EntityItem droppedItem = tossEvent.getEntityItem();
                if (droppedItem.getEntityWorld().isRemote){
                    MorphingMultiTool.LOGGER.info("is Remote, what we doing chef?");
                    droppedItem.setItem(MorphHandler.removeTool(tool, tool.getItem().getToolClasses(tool).iterator().next()));
                }else {
                    MorphingMultiTool.LOGGER.info("Not Remote, what we doing chef?");
                    EntityItem newItem = new EntityItem(tossEvent.getEntityItem().getEntityWorld(), droppedItem.posX, droppedItem.posY, droppedItem.posZ, MorphHandler.removeTool(tool, tool.getItem().getToolClasses(tool).iterator().next()));
                    droppedItem.getEntityWorld().spawnEntity(newItem);
                }
            }
        }

    }*/

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

    public static String getHarvestTool(World world, IBlockState blockState, Block block, BlockPos blockPos){
        String harvestTool = block.getHarvestTool(blockState);
        MorphingMultiTool.LOGGER.info("H tool Name: " + harvestTool);
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

        return null;
    }

}
