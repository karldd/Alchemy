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

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.Transferable;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.ImageIcon;

/**
 * Static utility methods used in Alchemy
 * Used to manipulate strings, load images, and general stuff
 */
public class AlcUtil implements AlcConstants {

    private final static Clipboard CLIPBOARD = Toolkit.getDefaultToolkit().getSystemClipboard();


    //////////////////////////////////////////////////////////////
    // STRING FUNCTIONS
    //////////////////////////////////////////////////////////////
    /** Checks a path for a file extension and adds one if not present */
    public static File addFileExtension(File file, String ext) {
        //String fileName = file.getName();
        String filePath = file.getPath();

        if (filePath.endsWith("." + ext)) {
            return file;
        } else {
            // If it does not end in a dot then add one
            if (!filePath.endsWith(".")) {
                filePath += ".";
            }
            System.out.println(filePath + ext);
            return new File(filePath + ext);
        }
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
        SimpleDateFormat formatter = new SimpleDateFormat(format);
        Date today = new Date();
        return formatter.format(today);
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
    // FILE FUNCTIONS
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

    public static Image getImage(String name, Component who) {
        Image image = null;
        Toolkit tk = Toolkit.getDefaultToolkit();

        image = tk.getImage(getUrlPath(name));
        MediaTracker tracker = new MediaTracker(who);
        tracker.addImage(image, 0);
        try {
            tracker.waitForAll();
        } catch (InterruptedException e) {
        }
        return image;
    }

    /** Copies the source file to destination file.
     *  If the destination file does not exist, it is created.
     * 
     * @param in    The source file as an InputStream
     * @param dst   The destination file
     * @throws java.io.IOException
     */
    public static void copyFile(InputStream in, File dst) throws IOException {
        //InputStream in = new FileInputStream(src);
        OutputStream out = new FileOutputStream(dst);

        // Transfer bytes from in to out
        byte[] buf = new byte[1024];
        int len;
        while ((len = in.read(buf)) > 0) {
            out.write(buf, 0, len);
        }
        in.close();
        out.close();
    }

    //////////////////////////////////////////////////////////////
    // UI FUNCTIONS
    //////////////////////////////////////////////////////////////
    /** 
     *  Calculate the centre of the screen with multiple monitors
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

    /**
     * Launch a url in the default browser
     * Adapted from: http://www.centerkey.com/java/browser/
     * @param url   The url to be launched
     */
    public static void openURL(String url) {
        final String errMsg = "Error attempting to launch web browser";
        //String osName = System.getProperty("os.name");
        try {
            if (AlcMain.PLATFORM == MACOSX) {
                Class fileMgr = Class.forName("com.apple.eio.FileManager");
                Method openURL = fileMgr.getDeclaredMethod("openURL",
                        new Class[]{String.class});
                openURL.invoke(null, new Object[]{url});

            } else if (AlcMain.PLATFORM == WINDOWS) {
                Runtime.getRuntime().exec("rundll32 url.dll,FileProtocolHandler " + url);

            } else if (AlcMain.PLATFORM == LINUX) {
                String[] browsers = {
                    "firefox", "opera", "konqueror", "epiphany", "mozilla", "netscape"
                };
                String browser = null;
                for (int count = 0; count < browsers.length && browser == null; count++) {
                    if (Runtime.getRuntime().exec(
                            new String[]{"which", browsers[count]}).waitFor() == 0) {
                        browser = browsers[count];
                    }
                }
                if (browser == null) {
                    throw new Exception("Could not find web browser");
                } else {
                    Runtime.getRuntime().exec(new String[]{browser, url});
                }
            }
        } catch (Exception e) {
            System.err.println(errMsg + ":\n" + e.getLocalizedMessage());
        }
    }

    /**
     * Sets the current contents of the clipboard to the specified transferable object and registers the specified clipboard owner as the owner of the new contents. 
     * Shows warning message if an exception occured
     * @param contents
     * @return boolean false if an exception occured
     */
    public static boolean setClipboard(Transferable contents, ClipboardOwner owner) {
        boolean result = true;
        try {
            CLIPBOARD.setContents(contents, owner);
        } catch (IllegalStateException e) {
            e.printStackTrace();
            result = false;
        }
        return result;
    }


    //////////////////////////////////////////////////////////////
    // DEBUGGING
    //////////////////////////////////////////////////////////////
    /** Print an array */
    public static void printArray(float[] array) {
        for (int i = 0; i < array.length; i++) {
            System.out.println("Array [" + String.valueOf(i) + "] = " + String.valueOf(array[i]));
        }
    }
}
