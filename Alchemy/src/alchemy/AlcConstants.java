/*
 * AlcConstants.java
 *
 * Created on November 22, 2007, 9:31 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package alchemy;

import java.awt.Cursor;
import java.awt.DisplayMode;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;

/**
 *
 * @author karldd
 */
public interface AlcConstants {

    // platform IDs
    static final int WINDOWS = 1;
    static final int MACOSX = 3;
    static final int LINUX = 4;
    static final int OTHER = 0;
    /**
     * Modifier flags for the shortcut key used to trigger menus.
     * (Cmd on Mac OS X, Ctrl on Linux and Windows)
     */
    static final int MENU_SHORTCUT = Toolkit.getDefaultToolkit().getMenuShortcutKeyMask();
    /** 
     * Graphics Device 
     */
    static final GraphicsDevice device = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
    /**
     * Display Mode
     */
    static final DisplayMode displayMode = device.getDisplayMode();
    /** 
     *  Fullscreen mode supported or not
     */
    static final boolean fullScreenSupported = device.isFullScreenSupported();
    /**
     * Full name of the Java version (i.e. 1.5.0_11).
     */
    public static final String javaVersionName = System.getProperty("java.version");
    /**
     * Version of Java that's in use, whether 1.1 or 1.3 or whatever,
     * stored as a float.
     */
    public static final float javaVersion = new Float(javaVersionName.substring(0, 3)).floatValue();
    /**
     * Current platform in use.
     * <P>
     * Equivalent to System.getProperty("os.name"), just used internally.
     */
    static public String platformName =
            System.getProperty("os.name");
    // useful goodness
    static final float PI = (float) Math.PI;
    static final float HALF_PI = PI / 2.0f;
    static final float THIRD_PI = PI / 3.0f;
    static final float QUARTER_PI = PI / 4.0f;
    static final float TWO_PI = PI * 2.0f;
    static final float DEG_TO_RAD = PI / 180.0f;
    static final float RAD_TO_DEG = 180.0f / PI;

    // shape types
    static final int LINE = 0;
    static final int SOLID = 1;
    // module types
    static final int CREATE = 0;
    static final int AFFECT = 1;
}
