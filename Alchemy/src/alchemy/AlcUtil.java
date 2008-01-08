/*
 *  This file is part of the Alchemy project - http://al.chemy.org
 * 
 *  Copyright (c) 2007 Karl D.D. Willis
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
package alchemy;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.KeyboardFocusManager;
import java.awt.Point;
import java.awt.Window;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.ImageIcon;

/**
 * Static utility methods used in Alchemy
 * Used to manipulate strings, load images, and general stuff
 */
public class AlcUtil {

    //////////////////////////////////////////////////////////////
    // STRING FUNCTIONS
    //////////////////////////////////////////////////////////////
    /** Checks a path for a file extension and adds one if not present */
    public static File addFileExtension(File file, String ext) {
        String fileName = file.getName();
        String filePath = file.getPath();

        // Check if there is a file extension already
        int dotIndex = fileName.lastIndexOf(".");

        // If there is a dot in there, check the extension
        if (dotIndex > 0 && dotIndex < fileName.length() - 1) {
            String possibleExtension = fileName.substring(dotIndex + 1);
            // If the extensions match return as is
            if (possibleExtension.equals(ext)) {
                return file;
            }
        }
        // Otherwise append the extension
        return new File(filePath, fileName + ext);
    }

    /** Returns just the class name -- no package info. */
    public static String getClassName(Object o) {
        String classString = o.getClass().getName();
        int dotIndex = classString.lastIndexOf(".");
        return classString.substring(dotIndex + 1);
    }

    /** Function to append a string to the end of a given URL */
    public static URL appendStringToUrl(URL url, String append) {
        String urlString = url.toString();
        URL newUrl = null;
        // Look for a file extension
        int dot = url.toString().lastIndexOf(".");
        if (dot == -1) {
            try {
                // If no file extension return as is
                newUrl = new URL(urlString + append);
            } catch (MalformedURLException ex) {
                System.err.println(ex);
            }
        } else {
            try {
                // Append the string before the file extension
                newUrl = new URL(urlString.substring(0, dot) + append + urlString.substring(dot));
            } catch (MalformedURLException ex) {
                System.err.println(ex);
            }
        }
        return newUrl;
    }

    /** Returns a string date stamp according to the format given */
    public static String dateStamp(String format) {
        Date today;
        SimpleDateFormat formatter;

        formatter = new SimpleDateFormat(format);
        today = new Date();
        return formatter.format(today);
    //System.out.println("Result: " + result);
    }

    /** Zero Pad an int */
    public static String zeroPad(int i, int len) {
        // converts integer to left-zero padded string, len chars long.
        String s = Integer.toString(i);
        if (s.length() > len) {
            return s.substring(0, len);
        } else if (s.length() < len) // pad on left with zeros
        {
            return "000000000000000000000000000".substring(0, len - s.length()) + s;
        } else {
            return s;
        }
    }

    //////////////////////////////////////////////////////////////
    // IMAGE LOADING FUNCTIONS
    //////////////////////////////////////////////////////////////
    /** Returns a URL from a String, or null if the path was invalid.
     * 
     * @param path  The path to the resource
     * @return      URL to the resource or null if invalid
     */
    public static URL getUrlPath(String path) {
        return getUrlPath(path, null);
    }

    /** Returns a URL from a String, or null if the path was invalid.
     * 
     * @param path          The path to the resource
     * @param classLoader   The classloader
     * @return              URL to the resource or null if invalid
     */
    public static URL getUrlPath(String path, ClassLoader classLoader) {

        URL resourceUrl = null;
        if (classLoader == null) {
            // If unspecified look from the main class
            resourceUrl = AlcMain.class.getResource(path);
        } else {
            resourceUrl = classLoader.getResource(path);
        }

        if (resourceUrl != null) {
            return resourceUrl;
        } else {
            System.err.println("Couldn't find file: " + path);
            return null;
        }
    }

    /** Returns an ImageIcon from a String, or null if the path was invalid.
     * 
     * @param path  The path to the image
     * @return      ImageIcon or null if invalid
     */
    public static ImageIcon createImageIcon(String path) {

        URL imgUrl = AlcMain.class.getResource(path);
        if (imgUrl != null) {
            return createImageIcon(imgUrl);
        } else {
            System.err.println("Couldn't find file: " + path);
            return null;
        }
    }

    /** Returns an ImageIcon from a URL, or null if the path was invalid.
     * 
     * @param imgUrl    The URL to the image
     * @return          ImageIcon or null if invalid
     */
    public static ImageIcon createImageIcon(URL imgUrl) {
        if (imgUrl != null) {
            ImageIcon icon = new ImageIcon(imgUrl);
            // Check the icon actually exists - bit of a hack!
            if (icon.getIconWidth() > 0) {
                return icon;
            }
        }
        //System.err.println("Couldn't find file: " + resourceUrl.toString());
        return null;
    }

    //////////////////////////////////////////////////////////////
    // UI FUNCTIONS
    //////////////////////////////////////////////////////////////
    /** Calculate the centre of the screen with multiple monitors for the JFileChooser
     *  From: http://www.juixe.com/techknow/index.php/2007/06/20/multiscreen-dialogs-and-the-jfilechooser/
     */
    public static Point calculateCenter(Container popupFrame) {
        KeyboardFocusManager kfm = KeyboardFocusManager.getCurrentKeyboardFocusManager();
        Window windowFrame = kfm.getFocusedWindow();
        Point frameTopLeft = windowFrame.getLocation();
        Dimension frameSize = windowFrame.getSize();
        Dimension popSize = popupFrame.getSize();

        int x = (int) (frameTopLeft.getX() + (frameSize.width / 2) - (popSize.width / 2));
        int y = (int) (frameTopLeft.getY() + (frameSize.height / 2) - (popSize.height / 2));
        Point center = new Point(x, y);
        return center;
    }
}
