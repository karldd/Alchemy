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
import java.awt.Toolkit;
import java.awt.event.KeyEvent;

/**
 *
 * @author karldd
 */
public interface AlcConstants {
    
    // platform IDs
    static final int WINDOWS = 1;
    static final int MACOSX  = 3;
    static final int LINUX   = 4;
    static final int OTHER   = 0;
    
    /**
     * Modifier flags for the shortcut key used to trigger menus.
     * (Cmd on Mac OS X, Ctrl on Linux and Windows)
     */
    static public final int MENU_SHORTCUT = Toolkit.getDefaultToolkit().getMenuShortcutKeyMask();
    
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
    static final float HALF_PI    = PI / 2.0f;
    static final float THIRD_PI   = PI / 3.0f;
    static final float QUARTER_PI = PI / 4.0f;
    static final float TWO_PI     = PI * 2.0f;
    
    static final float DEG_TO_RAD = PI/180.0f;
    static final float RAD_TO_DEG = 180.0f/PI;
    
    
    // key constants
    
    // only including the most-used of these guys
    // if people need more esoteric keys, they can learn about
    // the esoteric java KeyEvent api and of virtual keys
    
    // both key and keyCode will equal these values
    // for 0125, these were changed to 'char' values, because they
    // can be upgraded to ints automatically by Java, but having them
    // as ints prevented split(blah, TAB) from working
    static final char BACKSPACE = 8;
    static final char TAB       = 9;
    static final char ENTER     = 10;
    static final char RETURN    = 13;
    static final char ESC       = 27;
    static final char DELETE    = 127;
    static final char SPACE    = 32;
    
    // i.e. if ((key == CODED) && (keyCode == UP))
    static final int CODED     = 0xffff;
    
    // key will be CODED and keyCode will be this value
    static final int UP        = KeyEvent.VK_UP;
    static final int DOWN      = KeyEvent.VK_DOWN;
    static final int LEFT      = KeyEvent.VK_LEFT;
    static final int RIGHT     = KeyEvent.VK_RIGHT;
    
    // key will be CODED and keyCode will be this value
    static final int ALT       = KeyEvent.VK_ALT;
    static final int CONTROL   = KeyEvent.VK_CONTROL;
    static final int SHIFT     = KeyEvent.VK_SHIFT;
    
    
    // cursor types
    
    static final int ARROW = Cursor.DEFAULT_CURSOR;
    static final int CROSS = Cursor.CROSSHAIR_CURSOR;
    static final int HAND  = Cursor.HAND_CURSOR;
    static final int MOVE  = Cursor.MOVE_CURSOR;
    static final int TEXT  = Cursor.TEXT_CURSOR;
    static final int WAIT  = Cursor.WAIT_CURSOR;
    
    // shape types
    static final int LINE = 0;
    static final int SOLID = 1;
    
    // module types
    static final int CREATE = 0;
    static final int AFFECT = 1;
    
}
