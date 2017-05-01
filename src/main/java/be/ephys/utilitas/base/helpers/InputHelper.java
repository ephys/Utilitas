package be.ephys.utilitas.base.helpers;

import org.lwjgl.input.Keyboard;

public class InputHelper {

    public static boolean isShiftPressed() {
        return Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_RSHIFT);
    }
}
