package org.icaema.module;

import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.protocol.game.ServerboundPlayerCommandPacket;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.lwjgl.glfw.GLFW;
import org.rusherhack.client.api.bind.key.GLFWKey;
import org.rusherhack.client.api.events.client.input.EventMouse;
import org.rusherhack.client.api.events.client.input.EventKeyboard;
import org.rusherhack.client.api.events.player.EventPlayerUpdate;
import org.rusherhack.client.api.feature.module.ModuleCategory;
import org.rusherhack.client.api.setting.BindSetting;
import org.rusherhack.client.api.feature.module.ToggleableModule;
import org.rusherhack.client.api.utils.InventoryUtils;
import org.rusherhack.core.bind.key.NullKey;
import org.rusherhack.core.event.subscribe.Subscribe;
import org.rusherhack.core.setting.BooleanSetting;
import org.rusherhack.core.setting.NumberSetting;


public class IcaemasTweaksElytraToggle extends ToggleableModule {
    private final BindSetting swapBind = new BindSetting("Swap Bind", NullKey.INSTANCE /* unbound */);
    private final NumberSetting<Integer> elytraSwapInventorySlot = new NumberSetting<>("Elytra Inventory Slot", 0, 0, 45);
    private final BooleanSetting autoStartFlying = new BooleanSetting("AutoDeploy", "Start flying if already in air when elytra is swapped to", true);
    private final NumberSetting<Integer> delayTicks = new NumberSetting<>("DelayTicks", 10, 4, 60);

    private int fallingTicks = 0;
    private boolean swappedMidair = false;

    public IcaemasTweaksElytraToggle() {
        super("ElytraToggle", "Keybind to switch between elytra and chestplate", ModuleCategory.CLIENT);

        this.autoStartFlying.addSubSettings(this.delayTicks);

        //register settings
        this.registerSettings(
                this.swapBind,
                this.elytraSwapInventorySlot,
                this.autoStartFlying
        );
    }

    private void doElytraToggle() {
        int CHEST_INVENTORY_SLOT = 6;
        InventoryUtils.clickSlot(this.elytraSwapInventorySlot.getValue(), false);
        InventoryUtils.clickSlot(CHEST_INVENTORY_SLOT, false);
        InventoryUtils.clickSlot(this.elytraSwapInventorySlot.getValue(), false);
        if (fallingTicks > 0) this.swappedMidair = true;
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
    private void EventKeyboard(EventKeyboard event) {
        if (!(this.swapBind.getValue() instanceof GLFWKey)) return;
        if (event.getKey() == ((GLFWKey) this.swapBind.getValue()).getKeyCode() && event.getAction() == GLFW.GLFW_PRESS) {
            doElytraToggle();
        }
    }

    @Subscribe
    private void EventMouse(EventMouse.Key event) {
        if  (!(this.swapBind.getValue() instanceof GLFWKey)) return;
        if ((((EventMouse.Key) event).getButton() == ((GLFWKey) this.swapBind.getValue()).getKeyCode()) && ((EventMouse.Key) event).getAction() == GLFW.GLFW_PRESS) {
            doElytraToggle();
        }
    }

    @Subscribe
    private void onPlayerUpdate(EventPlayerUpdate event) {
        final LocalPlayer player = event.getPlayer();
        final boolean isInWater = player.isInWater();
        final boolean isOnGround = player.onGround();
        final boolean isFallFlying = player.isFallFlying();
        final boolean hasLevitation = player.hasEffect(MobEffects.LEVITATION);
        if (isInWater || isOnGround || isFallFlying || hasLevitation) {
            this.fallingTicks = 0;
            this.swappedMidair = false; // either we deploy after the timer or it gets reset on landing
            return;
        }
        this.fallingTicks++;

        if (!this.swappedMidair) return;
        if (this.fallingTicks <= this.delayTicks.getValue()) return;
        if (!hasElytraEquipped(player)) return;

        startFlying(player);
        this.swappedMidair = false;

    }

}
