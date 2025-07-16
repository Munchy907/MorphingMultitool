package uk.co.hailhydra.morphingmultitool.network.packet;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import uk.co.hailhydra.morphingmultitool.MorphingMultiTool;
import uk.co.hailhydra.morphingmultitool.handlers.MorphHandler;

public class PacketToolAdded implements IMessage {

    // A default constructor is always required
    public PacketToolAdded(){}

    private int toolSlot;
    private NBTTagCompound tagCompound;
    public PacketToolAdded(int toolSlot, NBTTagCompound tagCompound){
        this.toolSlot = toolSlot;
        this.tagCompound = tagCompound;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        toolSlot = buf.readInt();
        tagCompound = ByteBufUtils.readTag(buf);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(toolSlot);
        ByteBufUtils.writeTag(buf, tagCompound);
    }

    public static class PacketToolAddedHandler implements IMessageHandler<PacketToolAdded, IMessage> {

        @Override
        public IMessage onMessage(PacketToolAdded message, MessageContext ctx) {
            if (ctx.side.isServer()){
                MorphingMultiTool.LOGGER.info("PacketToolAddedHandler working?");
                EntityPlayerMP serverPlayer = ctx.getServerHandler().player;
                NBTTagCompound updatedTag = message.tagCompound;
                serverPlayer.getServerWorld().addScheduledTask(() ->{
                    ItemStack morphTool = serverPlayer.inventory.getItemStack();
                    ItemStack toolToAdd = serverPlayer.inventory.getStackInSlot(message.toolSlot);
                    if (MorphHandler.isMorphingTool(morphTool)){
                        MorphingMultiTool.LOGGER.info("PacketToolAddedHandler working!");
                        morphTool.setTagCompound(updatedTag);
                        toolToAdd.shrink(1);
                        serverPlayer.inventory.markDirty();
                    }
                });
            }
            return null;
        }
    }
}
