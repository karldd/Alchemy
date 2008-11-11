/*
 *  This file is part of the Alchemy project - http://al.chemy.org
 * 
 *  Copyright (c) 2007-2008 Karl D.D. Willis
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
 * Constant values used in Alchemy <br>
 * Some of this is taken from Processings' PConstants <br>
 * http://dev.processing.org/source/index.cgi/trunk/processing/core/src/processing/core/PConstants.java?view=markup
 */
public interface AlcConstants {

    //////////////////////////////////////////////////////////////
    // PLATFORM INFO
    //////////////////////////////////////////////////////////////
    /** Platform: Windows (1) */
    static final int WINDOWS = 1;
    /** Platform: MacOSX (3) */
    static final int MACOSX = 3;
    /** Platform: Linux (4) */
    static final int LINUX = 4;
    /** Platform: Other (0) */
    static final int OTHER = 0;
    /** Toolkit */
    static final Toolkit TOOLKIT = Toolkit.getDefaultToolkit();
    /** Modifier flags for the shortcut key used to trigger menus. (Cmd on Mac OS X, Ctrl on Linux and Windows) */
    static final int MODIFIER_KEY = TOOLKIT.getMenuShortcutKeyMask();
    /** Full name of the Java version (i.e. 1.5.0_11). */
    static final String JAVA_VERSION_NAME = System.getProperty("java.version");
    /** Version of Java that's in use, whether 1.1 or 1.3 or whatever stored as a float */
    static final float JAVA_VERSION = new Float(JAVA_VERSION_NAME.substring(0, 3)).floatValue();
    /** Current platform in use. Equivalent to System.getProperty("os.name"), just used internally */
    static final String PLATFORM_NAME = System.getProperty("os.name");
    /** The users name */
    static final String USER_NAME = System.getProperty("user.name");
    /** File Separator */
    static final String FILE_SEPARATOR = System.getProperty("file.separator");
    /** The users home directory */
    static final String HOME_DIR = System.getProperty("user.home");
    /** The users destop directory */
    static final String DESKTOP_DIR = System.getProperty("user.home") + FILE_SEPARATOR + "Desktop";
    /** The default temp directory */
    static final String TEMP_DIR = System.getProperty("java.io.tmpdir");
    /** Default Locale */
    static final Locale LOCALE = Locale.getDefault();
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
    // SHAPE STYLE
    //////////////////////////////////////////////////////////////
    /** Shape Style: Stroke (1)  */
    static final int STROKE = 1;
    /** Shape Style: Fill (2)  */
    static final int FILL = 2;
    /** Shape Style: Both (3) - Only used with the PDF Renderer */
    static final int BOTH = 3;
    /** Shape Style: Clip (4) - Only used with the PDF Renderer */
    static final int CLIP = 4;    //
    //////////////////////////////////////////////////////////////
    // MODULE TYPES
    //////////////////////////////////////////////////////////////
    /** Module Type: CREATE (1)  */
    static final int CREATE = 1;
    /** Module Type: AFFECT (2)  */
    static final int AFFECT = 2;
    //
    //////////////////////////////////////////////////////////////
    // CURSORS 
    //////////////////////////////////////////////////////////////
    /** Cursor - Default Cursor */
    static final Cursor ARROW = Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR);
    /** Cursor - Cross Cursor */
    static final Cursor CROSS = Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR);
    /** Cursor - Hand Cursor */
    static final Cursor HAND = Cursor.getPredefinedCursor(Cursor.HAND_CURSOR);
    /** Cursor - Move Cursor */
    static final Cursor MOVE = Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR);
    /** Cursor - Text Cursor */
    static final Cursor TEXT = Cursor.getPredefinedCursor(Cursor.TEXT_CURSOR);
    /** Cursor - Wait Cursor */
    static final Cursor WAIT = Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR);
    /** Cursor - Blank Cursor */
    static final Cursor BLANK = TOOLKIT.createCustomCursor(
            Toolkit.getDefaultToolkit().createImage(new MemoryImageSource(16, 16, new int[16 * 16], 0, 16)),
            new Point(0, 0),
            "Blank");
    //
    //////////////////////////////////////////////////////////////
    // PEN TYPES
    //////////////////////////////////////////////////////////////
    /** Pen Type: STYLUS (1)  */
    static final int STYLUS = 1;
    /** Pen Type: ERASER (2)  */
    static final int ERASER = 2;
    /** Pen Type: CURSOR (3)  */
    static final int CURSOR = 3;
    //
    //////////////////////////////////////////////////////////////
    // FONTS 
    //////////////////////////////////////////////////////////////
    static final Font FONT_SMALL = new Font("sansserif", Font.PLAIN, 10);
    static final Font FONT_MEDIUM = new Font("sansserif", Font.PLAIN, 11);
    static final Font FONT_LARGE = new Font("sansserif", Font.PLAIN, 12);
    static final Font FONT_BOLD = new Font("sansserif", Font.PLAIN, 11);

}
