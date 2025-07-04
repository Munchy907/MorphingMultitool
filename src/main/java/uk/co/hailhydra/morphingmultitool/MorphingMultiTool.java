package uk.co.hailhydra.morphingmultitool;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.co.hailhydra.morphingmultitool.proxy.ICommonProxy;
import uk.co.hailhydra.morphing_multi_tool.Tags;

@Mod(modid = Tags.MODID, version = Tags.VERSION, name = Tags.MODNAME, acceptedMinecraftVersions = "[1.12.2]")
public class MorphingMultiTool {

    @SidedProxy(
            clientSide = "uk.co.hailhydra.morphingmultitool.proxy.ClientProxy",
            serverSide = "uk.co.hailhydra.morphingmultitool.proxy.ServerProxy"
    )
    public static ICommonProxy proxy;

    @Instance(Tags.MODID)
    public static MorphingMultiTool instance;

    public static final Logger LOGGER = LogManager.getLogger(Tags.MODID);

    public MorphingMultiTool(){instance = this;}

    @EventHandler
    // preInit Run before anything else. Read your config, create blocks, items, etc. (Remove if not needed)
    public void preInit(FMLPreInitializationEvent event) {
        // register to the event bus so that we can listen to events
        MinecraftForge.EVENT_BUS.register(this);
        LOGGER.info("I am " + Tags.MODNAME + " at version " + Tags.VERSION);
    }

    @EventHandler
    // load "Do your mod setup. Build whatever data structures you care about." (Remove if not needed)
    public void init(FMLInitializationEvent event) {
    }

    @EventHandler
    // postInit "Handle interaction with other mods, complete your setup based on this." (Remove if not needed)
    public void postInit(FMLPostInitializationEvent event) {
    }
}
