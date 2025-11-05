package org.icaema.module;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BeaconBlockEntity;
import org.rusherhack.client.api.events.render.EventRender3D;
import org.rusherhack.client.api.feature.module.ModuleCategory;
import org.rusherhack.client.api.feature.module.ToggleableModule;
import org.rusherhack.client.api.render.IRenderer3D;
import org.rusherhack.client.api.setting.ColorSetting;
import org.rusherhack.client.api.utils.WorldUtils;
import org.rusherhack.core.event.subscribe.Subscribe;
import org.rusherhack.core.setting.NumberSetting;

import java.awt.*;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class IcaemasTweaksBeaconGrid extends ToggleableModule {

    public IcaemasTweaksBeaconGrid() {
        super("BeaconGrid", "Shows where you need to place beacons for proper coverage", ModuleCategory.CLIENT);
        this.registerSettings(
                beaconLevel,
                perfectRenderColor,
                existingRenderColor
        );
    }

    private static final int[] PERFECT_DISTANCES = {41, 61, 81, 101};

    private final NumberSetting<Integer> beaconLevel = new NumberSetting<>("BeaconLevel", 4, 1, 4);
    private final ColorSetting perfectRenderColor = new ColorSetting("GridColor", new Color(255, 166, 0, 100));
    private final ColorSetting existingRenderColor = new ColorSetting("ExistingColor", new Color(0, 255, 0, 100));


    @Subscribe
    public void onRender3D(EventRender3D event) {
        IRenderer3D renderer = event.getRenderer();
        renderer.begin(event.getMatrixStack());

        Set<BlockPos> perfectLocations = new HashSet<>();
        List<BlockPos> beaconLocations = WorldUtils.getBlockEntities(false)
                .stream()
                .filter(blockEntity -> blockEntity instanceof BeaconBlockEntity)
                .map(blockEntity -> blockEntity.getBlockPos().atY(0))
                .toList();

        for (BlockPos beaconPos : beaconLocations) {
            renderer.drawBox((double) beaconPos.getX(), -64, (double) beaconPos.getZ(), 1.0, 255.0, 1.0, true, true, existingRenderColor.getValueRGB());

            perfectLocations.add(beaconPos.north(PERFECT_DISTANCES[(int)beaconLevel.getValue()-1]));
            perfectLocations.add(beaconPos.south(PERFECT_DISTANCES[(int)beaconLevel.getValue()-1]));
            perfectLocations.add(beaconPos.east(PERFECT_DISTANCES[(int)beaconLevel.getValue()-1]));
            perfectLocations.add(beaconPos.west(PERFECT_DISTANCES[(int)beaconLevel.getValue()-1]));

        }

        perfectLocations.removeIf(beaconLocations::contains);

        for (BlockPos perfectPos : perfectLocations) {
            renderer.drawBox((double) perfectPos.getX(), -64, (double) perfectPos.getZ(), 1.0, 255.0, 1.0, true, true, perfectRenderColor.getValueRGB());
        }
        renderer.end();
    }

}
