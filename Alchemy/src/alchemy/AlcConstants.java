/*
 * AlcConstants.java
 *
 * Created on November 22, 2007, 9:31 PM
 *
 */
package alchemy;

import java.awt.DisplayMode;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Toolkit;
import java.util.prefs.Preferences;

/**
 *
 * @author karldd
 */
public interface AlcConstants {

    //////////////////////////////////////////////////////////////
    // PLATFORM INFO
    //////////////////////////////////////////////////////////////
    static final int WINDOWS = 1;
    static final int MACOSX = 3;
    static final int LINUX = 4;
    static final int OTHER = 0;
    /** Modifier flags for the shortcut key used to trigger menus. (Cmd on Mac OS X, Ctrl on Linux and Windows) */
    static final int MENU_SHORTCUT = Toolkit.getDefaultToolkit().getMenuShortcutKeyMask();
    /** Graphics Device */
    static final GraphicsDevice DEVICE = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
    /** Display Mode */
    static final DisplayMode DISPLAY_MODE = DEVICE.getDisplayMode();
    /** Fullscreen mode supported or not */
    static final boolean FULLSCREEN_SUPPORTED = DEVICE.isFullScreenSupported();
    /** Full name of the Java version (i.e. 1.5.0_11). */
    public static final String JAVA_VERSION_NAME = System.getProperty("java.version");
    /** Version of Java that's in use, whether 1.1 or 1.3 or whatever stored as a float */
    public static final float JAVA_VERSION = new Float(JAVA_VERSION_NAME.substring(0, 3)).floatValue();
    /** Current platform in use. Equivalent to System.getProperty("os.name"), just used internally */
    static public String PLATFORM_NAME = System.getProperty("os.name");
    /** The users name */
    static public String USER_NAME = System.getProperty("user.name");
    /** The users home directory */
    static public String HOME_DIR = System.getProperty("user.home");
    /** The default temp directory */
    static public String TEMP_DIR = System.getProperty("java.io.tmpdir");
    /** File Separator */
    static public String FILE_SEPARATOR = System.getProperty("file.separator");
    //
    //////////////////////////////////////////////////////////////
    // MATH CONSTANTS
    //////////////////////////////////////////////////////////////
    static final float PI = (float) Math.PI;
    static final float HALF_PI = PI / 2.0f;
    static final float THIRD_PI = PI / 3.0f;
    static final float QUARTER_PI = PI / 4.0f;
    static final float TWO_PI = PI * 2.0f;
    static final float DEG_TO_RAD = PI / 180.0f;
    static final float RAD_TO_DEG = 180.0f / PI;
    //
    //////////////////////////////////////////////////////////////
    // SHAPE TYPES
    //////////////////////////////////////////////////////////////
    static final int LINE = 0;
    static final int SOLID = 1;
    //
    //////////////////////////////////////////////////////////////
    // MODULE TYPES
    //////////////////////////////////////////////////////////////
    static final int CREATE = 0;
    static final int AFFECT = 1;
}
