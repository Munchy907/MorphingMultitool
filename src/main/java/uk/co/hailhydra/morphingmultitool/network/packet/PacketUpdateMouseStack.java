package uk.co.hailhydra.morphingmultitool.network.packet;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketUpdateMouseStack implements IMessage {
    // A default constructor & being public is always required
    public PacketUpdateMouseStack(){}

    ItemStack mouseStack;
    public PacketUpdateMouseStack(ItemStack mouseStack){
            this.mouseStack = mouseStack;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        mouseStack = ByteBufUtils.readItemStack(buf);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        ByteBufUtils.writeItemStack(buf, mouseStack);
    }

    public static class PacketUpdateMouseStackHandler implements IMessageHandler<PacketUpdateMouseStack, IMessage> {

        @Override
        public IMessage onMessage(PacketUpdateMouseStack message, MessageContext ctx) {
            if (ctx.side.isClient()){
                Minecraft.getMinecraft().addScheduledTask(() -> {
                    EntityPlayerSP playerSP = Minecraft.getMinecraft().player;
                    if (playerSP != null){
                        playerSP.inventory.setItemStack(message.mouseStack);
                    }
                });
            }
            else if (ctx.side.isServer()) {
                EntityPlayerMP serverPlayer = ctx.getServerHandler().player;
                serverPlayer.getServerWorld().addScheduledTask(() ->{
                    serverPlayer.inventory.setItemStack(message.mouseStack);
                });
            }
            return null;
        }
    }
}

