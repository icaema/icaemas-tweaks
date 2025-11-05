package org.icaema;

import org.icaema.module.IcaemasTweaksBeaconGrid;
import org.icaema.module.IcaemasTweaksElytraToggle;
import org.icaema.module.IcaemasTweaksHotbarSwap;
import org.icaema.module.IcaemasTweaksRocketTakeoff;
import org.rusherhack.client.api.RusherHackAPI;
import org.rusherhack.client.api.plugin.Plugin;


public class IcaemasTweaksPlugin extends Plugin {
    @Override
    public void onLoad() {

        //logger
        this.getLogger().info("Icaema's Tweaks Loaded!");

        //creating and registering a new module
        final IcaemasTweaksElytraToggle elytraToggle = new IcaemasTweaksElytraToggle();
        final IcaemasTweaksHotbarSwap hotbarSwap = new IcaemasTweaksHotbarSwap();
        final IcaemasTweaksRocketTakeoff rocketTakeoff = new IcaemasTweaksRocketTakeoff();
        final IcaemasTweaksBeaconGrid  beaconGrid = new IcaemasTweaksBeaconGrid();

        RusherHackAPI.getModuleManager().registerFeature(elytraToggle);
        RusherHackAPI.getModuleManager().registerFeature(hotbarSwap);
        RusherHackAPI.getModuleManager().registerFeature(rocketTakeoff);
        RusherHackAPI.getModuleManager().registerFeature(beaconGrid);

        /*
        //creating and registering a new hud element
        final ExampleHudElement exampleHudElement = new ExampleHudElement();
        RusherHackAPI.getHudManager().registerFeature(exampleHudElement);

        //creating and registering a new command
        final ExampleCommand exampleCommand = new ExampleCommand();
        RusherHackAPI.getCommandManager().registerFeature(exampleCommand);*/
    }

    @Override
    public void onUnload() {
        this.getLogger().info("Icaema's Tweaks unloaded!");
    }
}