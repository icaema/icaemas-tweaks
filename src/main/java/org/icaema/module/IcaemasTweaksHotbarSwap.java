package org.icaema.module;

import org.lwjgl.glfw.GLFW;
import org.rusherhack.client.api.bind.key.GLFWKey;
import org.rusherhack.client.api.events.client.input.EventKeyboard;
import org.rusherhack.client.api.events.client.input.EventMouse;
import org.rusherhack.client.api.feature.module.ModuleCategory;
import org.rusherhack.client.api.feature.module.ToggleableModule;
import org.rusherhack.client.api.setting.BindSetting;
import org.rusherhack.client.api.utils.InventoryUtils;
import org.rusherhack.core.bind.key.NullKey;
import org.rusherhack.core.event.subscribe.Subscribe;
import org.rusherhack.core.setting.StringSetting;

import java.util.ArrayList;
import java.util.List;

public class IcaemasTweaksHotbarSwap extends ToggleableModule {
    private final BindSetting swapBind = new BindSetting("Swap Bind", NullKey.INSTANCE );
    private final StringSetting swapSlots = new StringSetting("Slots", "3,5,6,7,8");

    public IcaemasTweaksHotbarSwap() {
        super("HotbarSwap", "Keybind to switch slots between the hotbar and first row of inventory slots", ModuleCategory.CLIENT);

        //register settings
        this.registerSettings(
                this.swapBind,
                this.swapSlots
        );
    }

    public static List<Integer> parseRange(String input) {
        List<Integer> result = new ArrayList<>();
        if (input == null || input.isEmpty()) {
            return result; // return empty list
        }

        String[] parts = input.split(",");
        for (String part : parts) {
            try {
                result.add(Integer.parseInt(part.trim()));
            } catch (NumberFormatException e) {
                System.err.println("Invalid number format: " + part);
                // You can choose to skip invalid values or throw an exception
            }
        }
        return result;
    }

    public void doSwap() {
        for (int slot : parseRange(this.swapSlots.getValue())) {
            InventoryUtils.swapSlots( 27+slot, slot);
        }
    }

    @Subscribe
    private void EventKeyboard(EventKeyboard event) {
        if (!(this.swapBind.getValue() instanceof GLFWKey)) return;
        if (event.getKey() == ((GLFWKey) this.swapBind.getValue()).getKeyCode() && event.getAction() == GLFW.GLFW_PRESS) {
            doSwap();
        }
    }

    @Subscribe
    private void EventMouse(EventMouse.Key event) {
        if  (!(this.swapBind.getValue() instanceof GLFWKey)) return;
        if ((((EventMouse.Key) event).getButton() == ((GLFWKey) this.swapBind.getValue()).getKeyCode()) && ((EventMouse.Key) event).getAction() == GLFW.GLFW_PRESS) {
            doSwap();
        }
    }

}
