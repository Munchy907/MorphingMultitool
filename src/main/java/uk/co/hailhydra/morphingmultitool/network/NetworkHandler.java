package uk.co.hailhydra.morphingmultitool.network;

import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;
import uk.co.hailhydra.morphingmultitool.Tags;
import uk.co.hailhydra.morphingmultitool.network.packet.PacketToolAdded;

public class NetworkHandler {

    public static final SimpleNetworkWrapper INSTANCE = NetworkRegistry.INSTANCE.newSimpleChannel(Tags.MODID);

    public static int ID = 0;

    public static void init(){
        INSTANCE.registerMessage(NetworkMessage.NetworkMessageHandler.class, NetworkMessage.class, ID++, Side.SERVER);
        INSTANCE.registerMessage(PacketToolAdded.PacketToolAddedHandler.class, PacketToolAdded.class, ID++, Side.SERVER);
    }

}
