/*
 *   Part of the Alchemy project - http://al.chemy.org
 * 
 *   Copyright (c) 2007 Karl D.D. Willis
 * 
 *   This library is free software; you can redistribute it and/or
 *   modify it under the terms of the GNU Lesser General Public
 *   License as published by the Free Software Foundation; either
 *   version 2.1 of the License, or (at your option) any later version.
 * 
 *   This library is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *   Lesser General Public License for more details.
 * 
 *   You should have received a copy of the GNU Lesser General
 *   Public License along with this library; if not, write to the
 *   Free Software Foundation, Inc., 59 Temple Place, Suite 330,
 *   Boston, MA  02111-1307  USA
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

/**
 * AlcUtil
 * 
 * 
 */
public class AlcUtil {

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
