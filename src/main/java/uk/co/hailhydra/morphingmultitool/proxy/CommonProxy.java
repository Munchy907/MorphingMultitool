package uk.co.hailhydra.morphingmultitool.proxy;

import uk.co.hailhydra.morphingmultitool.init.ModItems;
import uk.co.hailhydra.morphingmultitool.network.NetworkHandler;

public class CommonProxy {

    public void preInit(){
        ModItems.init();
        NetworkHandler.init();
    }

    public void init(){}
}
