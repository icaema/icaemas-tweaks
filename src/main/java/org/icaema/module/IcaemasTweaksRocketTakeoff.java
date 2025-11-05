package org.icaema.module;

import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.protocol.game.ServerboundPlayerCommandPacket;
import net.minecraft.network.protocol.game.ServerboundUseItemPacket;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.Fireworks;
import org.rusherhack.client.api.events.network.EventPacket;
import org.rusherhack.client.api.feature.module.ModuleCategory;
import org.rusherhack.client.api.feature.module.ToggleableModule;
import org.rusherhack.core.event.subscribe.Subscribe;

public class IcaemasTweaksRocketTakeoff extends ToggleableModule {

    public IcaemasTweaksRocketTakeoff() {
        super("RocketTakeoff", "Auto takeoff when rightclicking with a rocket", ModuleCategory.MOVEMENT);
    }
    public static boolean hasElytraEquipped(LocalPlayer player) {
        Iterable<ItemStack> armorSlots = player.getArmorSlots();
        for (ItemStack stack : armorSlots) {
            if (stack.getItem() == Items.ELYTRA) return true;
        }
        return false;
    }
    public static void startFlying(LocalPlayer player) {
        final var startFallFlying = new ServerboundPlayerCommandPacket(player, ServerboundPlayerCommandPacket.Action.START_FALL_FLYING);
        player.connection.send(startFallFlying);
        player.startFallFlying();
    }

    @Subscribe
    private void onPacketSend(EventPacket.Send event) {
        // some stolen from this: https://github.com/tillay-rh-plugins/elytra-trajectories/blob/main/src/main/java/tilley/elypath/ElytraPathTracerModule.java
        // Don't do anything if this isn't the exact type of packet we want
        if (!(event.getPacket() instanceof ServerboundUseItemPacket packet)) return;
        if ( mc.player == null) return;
        if (packet.getHand() != InteractionHand.MAIN_HAND) return;

        // If a firework is used in this packet
        ItemStack stack = mc.player.getInventory().getItem(mc.player.getInventory().selected);
        if (!stack.is(Items.FIREWORK_ROCKET)) return;
        Fireworks fireworks = stack.get(DataComponents.FIREWORKS);
        if (fireworks == null) return;
        if (!hasElytraEquipped(mc.player)) return;

        // check if player is not flying already
        if (mc.player.isFallFlying()) return;

        // if player is on ground, then need to jump for a tick first
        if (mc.player.onGround()) {
            mc.player.jumpFromGround();
            mc.tick();
        }

        startFlying(mc.player);
    }

}
