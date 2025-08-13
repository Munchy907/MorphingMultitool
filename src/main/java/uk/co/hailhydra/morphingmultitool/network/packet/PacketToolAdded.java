package uk.co.hailhydra.morphingmultitool.network.packet;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemShears;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import uk.co.hailhydra.morphingmultitool.handlers.MorphHandler;
import uk.co.hailhydra.morphingmultitool.items.ItemMorphTool;
import uk.co.hailhydra.morphingmultitool.network.NetworkHandler;
import uk.co.hailhydra.morphingmultitool.utility.ToolType;

import java.util.Set;

public class PacketToolAdded implements IMessage {

    // A default constructor is always required
    public PacketToolAdded(){}

    private int toolSlot;
    private ItemStack morphTool;
    public PacketToolAdded(int toolSlot, ItemStack morphTool){
        this.toolSlot = toolSlot;
        this.morphTool = morphTool;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        toolSlot = buf.readInt();
        morphTool = ByteBufUtils.readItemStack(buf);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(toolSlot);
        ByteBufUtils.writeItemStack(buf, morphTool);
    }

    public static class PacketToolAddedHandler implements IMessageHandler<PacketToolAdded, IMessage> {

        @Override
        public IMessage onMessage(PacketToolAdded message, MessageContext ctx) {
            if (ctx.side.isServer()){
                EntityPlayerMP serverPlayer = ctx.getServerHandler().player;
                serverPlayer.getServerWorld().addScheduledTask(() ->{

                    ItemStack morphTool = message.morphTool;
                    Container container = serverPlayer.openContainer;
                    Slot slot = container.getSlot(message.toolSlot);
                    ItemStack toolToAdd = slot.getStack();
                    if (toolToAdd.isEmpty()){return;}

                    if (morphTool.getItem() instanceof ItemMorphTool || MorphHandler.isMorphingTool(morphTool)){
                        Set<String> toolClasses = toolToAdd.getItem().getToolClasses(toolToAdd);
                        String toolClass = "";
                        if (toolClasses.isEmpty()) {
                            if (!(toolToAdd.getItem() instanceof ItemShears)) {
                                return;
                            } else {
                                toolClass = ToolType.SHEARS;
                            }
                        }else {toolClass = toolToAdd.getItem().getToolClasses(toolToAdd).iterator().next();}

                        if (!MorphHandler.addTool(morphTool, toolToAdd, toolClass)){return;}

                        serverPlayer.inventory.setItemStack(morphTool);
                        NetworkHandler.INSTANCE.sendTo(new PacketUpdateMouseStack(morphTool), serverPlayer) ;
                    }
                });
            }
            return null;
        }
    }
}
