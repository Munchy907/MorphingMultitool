package uk.co.hailhydra.morphingmultitool.network;

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

public class NetworkMessage implements IMessage {

    // A default constructor is always required
    public NetworkMessage(){}

    private NBTTagCompound stackTag;
    private String toolClass = ToolType.NONE;
    public NetworkMessage(NBTTagCompound stackTag, String toolClass){
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

    public static class NetworkMessageHandler implements IMessageHandler<NetworkMessage, IMessage> {

        @Override
        public IMessage onMessage(NetworkMessage message, MessageContext ctx) {
            if (ctx.side.isServer()){
                EntityPlayerMP serverPlayer = ctx.getServerHandler().player;
                NBTTagCompound updatedTag = message.stackTag;
                serverPlayer.getServerWorld().addScheduledTask(() ->{
                    if (updatedTag == null){return;}

                    NBTTagCompound morphData = updatedTag.getCompoundTag(MorphToolResources.TAG_MMT_DATA);
                    if (morphData.isEmpty()){return;}

                    ItemStack morphTool = serverPlayer.getHeldItemMainhand();
                    if (MorphHandler.isMorphingTool(morphTool)){
                        ItemStack tool = MorphHandler.getItemFromToolClass(morphData, message.toolClass);
                        if (tool.isEmpty()){return;}

                        tool.setTagCompound(updatedTag);
                        MorphingMultiTool.LOGGER.info("new tool: " + tool.getDisplayName());
                        serverPlayer.setHeldItem(EnumHand.MAIN_HAND, tool);
                        //serverPlayer.inventory.markDirty();
                    }
                });
            }
            return null;
        }
    }
}
