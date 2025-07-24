package uk.co.hailhydra.morphingmultitool.network.packet;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import uk.co.hailhydra.morphingmultitool.handlers.MorphHandler;
import uk.co.hailhydra.morphingmultitool.init.ModItems;
import uk.co.hailhydra.morphingmultitool.items.ItemMorphTool;

public class PacketMorphOnMousePickup implements IMessage {

    // A default constructor is always required
    public PacketMorphOnMousePickup(){}

    int slotID;
    public PacketMorphOnMousePickup(int slotID){
        this.slotID = slotID;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        slotID = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(slotID);
    }

    public static class PacketMorphOnMousePickupHandler implements IMessageHandler<PacketMorphOnMousePickup, IMessage> {

        @Override
        public IMessage onMessage(PacketMorphOnMousePickup message, MessageContext ctx) {
            if (ctx.side.isServer()) {
                EntityPlayerMP serverPlayer = ctx.getServerHandler().player;
                serverPlayer.getServerWorld().addScheduledTask(() -> {
                    Container container = serverPlayer.openContainer;
                    Slot slot = container.getSlot(message.slotID);
                    ItemStack tool = slot.getStack().copy();
                    if (MorphHandler.isMorphingTool(tool) && !(tool.getItem() instanceof ItemMorphTool)){
                        MorphHandler.updateToolDamage(tool);
                        ItemStack morphTool = new ItemStack(ModItems.MORPHING_MULTI_TOOL);
                        morphTool.setTagCompound(tool.getTagCompound());
                        slot.putStack(morphTool);
                    }
                });
            }
            return null;
        }
    }
}
