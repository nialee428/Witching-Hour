package net.nia.witchinghour.input;

import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

public class InputMain {

    public static KeyBinding GESTURE_KEY;
    public static KeyBinding CAST_KEY;

    public static KeyBinding SNAP_KEY;

    public static KeyBinding PUSH_KEY;
    public static KeyBinding PULL_KEY;
    public static KeyBinding RELEASE_KEY;

    public static void initialize() {
        GESTURE_KEY = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.witching-hour.gesture",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_V,
                "category.witching-hour"
        ));
        CAST_KEY = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.witching-hour.cast_spell",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_R,
                "category.witching-hour"
        ));
        SNAP_KEY = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.witching-hour.snap",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_G,
                "category.witching-hour"
        ));
    }

}
