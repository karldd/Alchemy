/*
 *  This file is part of the Alchemy project - http://al.chemy.org
 * 
 *  Copyright (c) 2007-2010 Karl D.D. Willis
 * 
 *  Alchemy is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 * 
 *  Alchemy is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 * 
 *  You should have received a copy of the GNU General Public License
 *  along with Alchemy.  If not, see <http://www.gnu.org/licenses/>.
 * 
 */
package org.alchemy.core;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.image.MemoryImageSource;
import java.util.Locale;

/**
 * Constant values used in Alchemy
 */
public interface AlcConstants {

    //////////////////////////////////////////////////////////////
    // PLATFORM INFO
    //////////////////////////////////////////////////////////////
    /** OS: Windows (1) */
    static final int OS_WINDOWS = 1;
    /** OS: Mac (2) */
    static final int OS_MAC = 2;
    /** OS: Linux (3) */
    static final int OS_LINUX = 3;
    /** OS: Other (0) */
    static final int OS_OTHER = 0;
    /** Convenience access to the {@link Toolkit} */
    static final Toolkit TOOLKIT = Toolkit.getDefaultToolkit();
    /** Modifier flags for the shortcut key used to trigger menus. (Cmd on Mac OS X, Ctrl on Linux and Windows) */
    static final int KEY_MODIFIER = TOOLKIT.getMenuShortcutKeyMask();
    /** Full name of the Java version eg 1.5.0_11 */
    static final String JAVA_VERSION_NAME = System.getProperty("java.version");
    /** Version of Java that's in use, whether 1.1 or 1.3 or whatever stored as a float */
    static final float JAVA_VERSION = new Float(JAVA_VERSION_NAME.substring(0, 3)).floatValue();
    /** Name of the OS */
    static final String OS_NAME = System.getProperty("os.name");
    /** The users name */
    static final String USER_NAME = System.getProperty("user.name");
    /** The file separator used on this OS */
    static final String DIR_SEPARATOR = System.getProperty("file.separator");
    /** The users home directory */
    static final String DIR_HOME = System.getProperty("user.home");
    /** The users destop directory */
    static final String DIR_DESKTOP = System.getProperty("user.home") + DIR_SEPARATOR + "Desktop";
    /** The default temp directory */
    static final String DIR_TEMP = System.getProperty("java.io.tmpdir");
    /** Default Locale */
    static final Locale LOCALE = Locale.getDefault();
    //
    //////////////////////////////////////////////////////////////
    // MATH CONSTANTS
    //////////////////////////////////////////////////////////////
    static final float MATH_PI = (float) Math.PI;
    static final float MATH_HALF_PI = MATH_PI / 2.0f;
    static final float MATH_THIRD_PI = MATH_PI / 3.0f;
    static final float MATH_QUARTER_PI = MATH_PI / 4.0f;
    static final float MATH_TWO_PI = MATH_PI * 2.0f;
    static final float MATH_DEG_TO_RAD = MATH_PI / 180.0f;
    static final float MATH_RAD_TO_DEG = 180.0f / MATH_PI;
    //
    //////////////////////////////////////////////////////////////
    // SHAPE STYLE
    //////////////////////////////////////////////////////////////
    /** Shape Style: Stroke (1)  */
    static final int STYLE_STROKE = 1;
    /** Shape Style: Fill (2)  */
    static final int STYLE_FILL = 2;
    /** Shape Style: Both (3) - Only used with the PDF Renderer */
    static final int STYLE_BOTH = 3;
    /** Shape Style: Clip (4) - Only used with the PDF Renderer */
    static final int STYLE_CLIP = 4;    //
    //////////////////////////////////////////////////////////////
    // MODULE TYPES
    //////////////////////////////////////////////////////////////
    /** Module Type: CREATE (1)  */
    static final int MODULE_CREATE = 1;
    /** Module Type: AFFECT (2)  */
    static final int MODULE_AFFECT = 2;
    //
    //////////////////////////////////////////////////////////////
    // CURSORS 
    //////////////////////////////////////////////////////////////
    /** Cursor - Default Cursor */
    static final Cursor CURSOR_ARROW = Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR);
    /** Cursor - Cross Cursor */
    static final Cursor CURSOR_CROSS = Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR);
    /** Cursor - Hand Cursor */
    static final Cursor CURSOR_HAND = Cursor.getPredefinedCursor(Cursor.HAND_CURSOR);
    /** Cursor - Move Cursor */
    static final Cursor CURSOR_MOVE = Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR);
    /** Cursor - Text Cursor */
    static final Cursor CURSOR_TEXT = Cursor.getPredefinedCursor(Cursor.TEXT_CURSOR);
    /** Cursor - Wait Cursor */
    static final Cursor CURSOR_WAIT = Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR);
    /** Cursor - Blank Cursor */
    static final Cursor CURSOR_BLANK = TOOLKIT.createCustomCursor(
            Toolkit.getDefaultToolkit().createImage(new MemoryImageSource(16, 16, new int[16 * 16], 0, 16)),
            new Point(0, 0),
            "Blank");
    /** Cursor - Circle Cursor used with the color picker */
    static final Cursor CURSOR_CIRCLE = AlcUtil.getCursor("cursor-circle.png");
    /** Cursor - Zoom Cursor */
    static final Cursor CURSOR_ZOOM = AlcUtil.getCursor("cursor-zoom.png");
    /** Cursor - Eyedropper Cursor */
    static final Cursor CURSOR_EYEDROPPER = AlcUtil.getCursor("cursor-eyedropper.png");
    //////////////////////////////////////////////////////////////
    // PEN TYPES
    //////////////////////////////////////////////////////////////
    /** Pen Type: STYLUS (1)  */
    static final int PEN_STYLUS = 1;
    /** Pen Type: ERASER (2)  */
    static final int PEN_ERASER = 2;
    /** Pen Type: CURSOR (3)  */
    static final int PEN_CURSOR = 3;
    //
    //////////////////////////////////////////////////////////////
    // FONTS 
    //////////////////////////////////////////////////////////////
    /** Font - Sanserif plain 9pt */
    static final Font FONT_SMALLER = new Font("sansserif", Font.PLAIN, 9);
    /** Font - Sanserif plain 10pt */
    static final Font FONT_SMALL = new Font("sansserif", Font.PLAIN, 10);
    /** Font - Sanserif plain 11pt */
    static final Font FONT_MEDIUM = new Font("sansserif", Font.PLAIN, 11);
    /** Font - Sanserif plain 12pt */
    static final Font FONT_LARGE = new Font("sansserif", Font.PLAIN, 12);
    /** Font - Sanserif bold 10pt */
    static final Font FONT_SMALL_BOLD = new Font("sansserif", Font.BOLD, 10);
    /** Font - Sanserif bold 11pt */
    static final Font FONT_MEDIUM_BOLD = new Font("sansserif", Font.BOLD, 11);
    /** Font - Sanserif bold 12pt */
    static final Font FONT_LARGE_BOLD = new Font("sansserif", Font.BOLD, 12);
    //

    //////////////////////////////////////////////////////////////
    // INTERFACE COLORS
    //////////////////////////////////////////////////////////////
    /** UI background color */
    static final Color COLOR_UI_BG = new Color(225, 225, 225);
    /** UI gradient background start color */
    static final Color COLOR_UI_START = new Color(235, 235, 235, 240);
    /** UI gradient background end color */
    static final Color COLOR_UI_END = new Color(215, 215, 215, 240);
    /** UI line color */
    static final Color COLOR_UI_LINE = new Color(140, 140, 140);
    /** UI line alpha color - 50% Alpha for soft corners */
    static final Color COLOR_UI_LINE_ALPHA = new Color(140, 140, 140, 128);
    /** UI highlight color */
    static final Color COLOR_UI_HIGHLIGHT = new Color(231, 231, 231);
    /** UI box color - used for the CheckBox Boxes */
    static final Color COLOR_UI_BOX = new Color(190, 190, 190);
}
