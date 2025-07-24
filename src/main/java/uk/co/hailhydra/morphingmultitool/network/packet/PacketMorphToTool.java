package uk.co.hailhydra.morphingmultitool.network.packet;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumHand;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import uk.co.hailhydra.morphingmultitool.MorphingMultiTool;
import uk.co.hailhydra.morphingmultitool.handlers.MorphHandler;
import uk.co.hailhydra.morphingmultitool.utility.MorphToolResources;
import uk.co.hailhydra.morphingmultitool.utility.ToolType;

public class PacketMorphToTool implements IMessage {

    // A default constructor is always required
    public PacketMorphToTool(){}

    private NBTTagCompound stackTag;
    private String toolClass = ToolType.NONE;
    public PacketMorphToTool(NBTTagCompound stackTag, String toolClass){
        this.stackTag = stackTag;
        this.toolClass = toolClass;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        //TODO: Extend if more than NBTTagCompound needs to be read
        stackTag = readNBT(buf);
        toolClass = ByteBufUtils.readUTF8String(buf);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        //TODO: Extend if more than NBTTagCompound needs to be write
        writeNBT(this.stackTag, buf);
        ByteBufUtils.writeUTF8String(buf, this.toolClass);
    }

    private static NBTTagCompound readNBT(ByteBuf buf){
        return ByteBufUtils.readTag(buf);
    }

    private static void writeNBT(NBTTagCompound tagCompound, ByteBuf buf){
        ByteBufUtils.writeTag(buf, tagCompound);
    }

    public static class PacketMorphToToolHandler implements IMessageHandler<PacketMorphToTool, IMessage> {

        @Override
        public IMessage onMessage(PacketMorphToTool message, MessageContext ctx) {
            if (ctx.side.isServer()){
                EntityPlayerMP serverPlayer = ctx.getServerHandler().player;
                NBTTagCompound updatedTag = message.stackTag;
                serverPlayer.getServerWorld().addScheduledTask(() ->{
                    if (updatedTag == null){
                        MorphingMultiTool.LOGGER.info("Updated Tag is null");
                        return;}



                    NBTTagCompound morphData = updatedTag.getCompoundTag(MorphToolResources.TAG_MMT_DATA);
                    if (morphData.isEmpty()){
                        MorphingMultiTool.LOGGER.info("MorphData is empty");
                        return;}



                    ItemStack morphTool = serverPlayer.getHeldItemMainhand();
                    if (MorphHandler.isMorphingTool(morphTool)){
                        ItemStack tool = MorphHandler.getItemFromToolClass(morphData, message.toolClass);
                        MorphingMultiTool.LOGGER.info("Inside morph tool");
                        if (tool.isEmpty()){return;}

                        MorphingMultiTool.LOGGER.info("Tool is not empty");

                        tool.setTagCompound(updatedTag);
                        serverPlayer.setHeldItem(EnumHand.MAIN_HAND, tool);
                        //serverPlayer.inventory.markDirty();
                    }else {
                        MorphingMultiTool.LOGGER.info("Isn't morphing tool");
                    }
                });
            }
            return null;
        }
    }
}
