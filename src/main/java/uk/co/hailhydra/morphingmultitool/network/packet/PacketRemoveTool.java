package uk.co.hailhydra.morphingmultitool.network.packet;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import uk.co.hailhydra.morphingmultitool.handlers.MorphHandler;
import uk.co.hailhydra.morphingmultitool.network.NetworkHandler;

public class PacketRemoveTool implements IMessage {

    // A default constructor is always required
    public PacketRemoveTool(){}

    private ItemStack morphTool;
    private int emptySlotID;


    public PacketRemoveTool(ItemStack morphTool, int emptySlotID){
        this.morphTool = morphTool;
        this.emptySlotID = emptySlotID;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        morphTool = ByteBufUtils.readItemStack(buf);
        emptySlotID = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        ByteBufUtils.writeItemStack(buf, morphTool);
        buf.writeInt(emptySlotID);
    }

    public static class PacketRemoveToolHandler implements IMessageHandler<PacketRemoveTool, IMessage> {

        @Override
        public IMessage onMessage(PacketRemoveTool message, MessageContext ctx) {
            if (ctx.side.isServer()){
                EntityPlayerMP serverPlayer = ctx.getServerHandler().player;
                serverPlayer.getServerWorld().addScheduledTask(() ->{

                    Container container = serverPlayer.openContainer;
                    Slot emptySlot = container.getSlot(message.emptySlotID);

                    if (MorphHandler.isMorphingTool(message.morphTool)){

                        ItemStack removedTool = MorphHandler.removeTool(message.morphTool);

                        if (removedTool.isEmpty()){
                            serverPlayer.inventory.setItemStack(ItemStack.EMPTY);
                            emptySlot.putStack(message.morphTool);
                            NetworkHandler.INSTANCE.sendTo(new PacketUpdateMouseStack(ItemStack.EMPTY), serverPlayer);

                        }else {
                            emptySlot.putStack(removedTool);
                            serverPlayer.inventory.setItemStack(message.morphTool);
                            NetworkHandler.INSTANCE.sendTo(new PacketUpdateMouseStack(message.morphTool), serverPlayer);

                        }
                    }
                });
            }
            return null;
        }
    }
}
