package uk.co.hailhydra.morphingmultitool.handlers;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.common.IShearable;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import uk.co.hailhydra.morphingmultitool.MorphingMultiTool;

import java.util.Objects;

@SideOnly(Side.CLIENT)
public class ClientHandler {
    public static final ClientHandler INSTANCE = new ClientHandler();
    public static final double INTERACTION_RANGE_SURVIVAL = 4.5;
    public static final double INTERACTION_RANGE_CREATIVE = 5.0;
    public static int tickCounter = 0;

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onClientTick(TickEvent.ClientTickEvent event) {
        tickCounter += 1;
        if (event.phase == TickEvent.Phase.END && tickCounter >= 40){
            tickCounter = 0;
            EntityPlayerSP playerSP = Minecraft.getMinecraft().player;
            if (playerSP == null || playerSP.world == null){return;}

            double rayLength = (playerSP.isCreative()) ? INTERACTION_RANGE_CREATIVE : INTERACTION_RANGE_SURVIVAL;

            RayTraceResult rayResult = raycast(playerSP, rayLength);
            if (rayResult == null || rayResult.typeOfHit != RayTraceResult.Type.BLOCK){return;}

            IBlockState blockState = playerSP.world.getBlockState(rayResult.getBlockPos());
            Block targetBlock = blockState.getBlock();
            String toolName = targetBlock.getHarvestTool(blockState);
            if (Objects.equals(toolName, "null")){return;}

            ItemStack harvestTool;
            if (targetBlock instanceof IShearable){
                toolName = new ItemStack(Items.SHEARS).getDisplayName().toLowerCase();
            }

            MorphingMultiTool.LOGGER.info(toolName);
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
