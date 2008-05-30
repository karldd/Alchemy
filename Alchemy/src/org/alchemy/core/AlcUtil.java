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

import java.awt.*;
import java.awt.datatransfer.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.ResourceBundle;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.UIManager;

/**
 * Static utility methods used in Alchemy
 * Used to manipulate strings, load images, and general stuff
 */
public class AlcUtil implements AlcConstants {

    private final static Clipboard CLIPBOARD = TOOLKIT.getSystemClipboard();


    //////////////////////////////////////////////////////////////
    // STRING FUNCTIONS
    //////////////////////////////////////////////////////////////
    /** Checks a name for a file extension and adds one if not present */
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
    /** Returns a URL from a String, or null if the name was invalid.
     * 
     * @param name  The name to the resource
     * @return      URL to the resource or null if invalid
     */
    public static URL getUrlPath(String path) {
        return getUrlPath(path, null);
    }

    /** Returns a URL from a String, or null if the name was invalid.
     * 
     * @param name          The name to the resource
     * @param classLoader   The classloader
     * @return              URL to the resource or null if invalid
     */
    public static URL getUrlPath(String path, ClassLoader classLoader) {

        URL resourceUrl = null;
        if (classLoader == null) {
            // If unspecified look from the main class
            resourceUrl = Alchemy.class.getResource("/org/alchemy/data/" + path);
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

    /** Returns an ImageIcon from a String, or null if the name was invalid.
     * 
     * @param name  The name to the image
     * @return      ImageIcon or null if invalid
     */
    public static ImageIcon getImageIcon(String name) {
        URL imgUrl = Alchemy.class.getResource("/org/alchemy/data/" + name);
        if (imgUrl != null) {
            return getImageIcon(imgUrl);
        } else {
            System.err.println("Couldn't find file: " + name);
            return null;
        }
    }

    /** Returns an ImageIcon from a URL, or null if the name was invalid.
     * 
     * @param imgUrl    The URL to the image
     * @return          ImageIcon or null if invalid
     */
    public static ImageIcon getImageIcon(URL imgUrl) {
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

    public static Image getImage(String name) {
        URL imgUrl = Alchemy.class.getResource("/org/alchemy/data/" + name);
        if (imgUrl != null) {
            return getImageIcon(imgUrl).getImage();
        } else {
            System.err.println("Couldn't find file: " + name);
            return null;
        }
    }

    public static Image getImage(URL imgUrl) {
        if (imgUrl != null) {
            ImageIcon icon = new ImageIcon(imgUrl);
            // Check the icon actually exists - bit of a hack!
            if (icon.getIconWidth() > 0) {
                return icon.getImage();
            }
        }
        //System.err.println("Couldn't find file: " + resourceUrl.toString());
        return null;
    }

    /** Create a custom cursor from a given image file
     * 
     * @param name  The image file to use as the cursor
     * @return      A Custom cursor
     */
    public static Cursor getCursor(String name) {

        // Cursor size differs depending on the platform
        // Add padding based on the best cursor size
        final Cursor customCursor;
        Image smallCursor = AlcUtil.getImage(name);
        Dimension smallCursorSize = new Dimension(smallCursor.getWidth(null), smallCursor.getHeight(null));
        Dimension cursorSize = TOOLKIT.getBestCursorSize(smallCursorSize.width, smallCursorSize.height);

        if (cursorSize.equals(smallCursorSize)) {
            customCursor = TOOLKIT.createCustomCursor(
                    smallCursor,
                    new Point(smallCursorSize.width / 2, smallCursorSize.height / 2),
                    "CustomCursor");
        } else {
            int leftGap = (cursorSize.width - smallCursorSize.width) / 2;
            int topGap = (cursorSize.height - smallCursorSize.height) / 2;

            BufferedImage bigCursor = new BufferedImage(cursorSize.width, cursorSize.height, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2 = bigCursor.createGraphics();
            g2.drawImage(smallCursor, leftGap, topGap, null);
            g2.dispose();

            customCursor = TOOLKIT.createCustomCursor(
                    bigCursor,
                    new Point(cursorSize.width / 2, cursorSize.height / 2),
                    "CustomCursor");
        }

        return customCursor;
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
     * 
     * @param popupFrame    The popup window
     * @return              The centred location as a Point
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
            if (Alchemy.PLATFORM == MACOSX) {
                Class fileMgr = Class.forName("com.apple.eio.FileManager");
                Method openURL = fileMgr.getDeclaredMethod("openURL",
                        new Class[]{String.class});
                openURL.invoke(null, new Object[]{url});

            } else if (Alchemy.PLATFORM == WINDOWS) {
                Runtime.getRuntime().exec("rundll32 url.dll,FileProtocolHandler " + url);

            } else if (Alchemy.PLATFORM == LINUX) {
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

    /** Open a local pdf in the default application
     * 
     * @param pdf   A file pointing to the pdf to open
     */
    public static void openPDF(File pdf) {
        final String errMsg = "Error attempting to launch pdf";
        try {
            if (Alchemy.PLATFORM == MACOSX) {
                Runtime.getRuntime().exec("open " + pdf.getAbsolutePath());
            } else if (Alchemy.PLATFORM == WINDOWS) {
                Runtime.getRuntime().exec("rundll32 url.dll,FileProtocolHandler " + pdf.getAbsolutePath());
            } else if (Alchemy.PLATFORM == LINUX) {
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

    /** Show a confirmation dialog specific to the OS style 
     *  The title and message are taken from the localised Alchemy bundle
     * 
     * @param title     Title of the dialog
     * @param message   Message of the dialog
     * @param bundle    The bundle to take localised text from
     * @return          True if OK, else false if Cancel
     */
    public static boolean showConfirmDialog(String title, String message, ResourceBundle bundle) {
        String bundleTitle = Alchemy.bundle.getString(title);
        String bundleMessage = Alchemy.bundle.getString(message);
        return showConfirmDialog(bundleTitle, bundleMessage);
    }

    /** Show a confirmation dialog specific to the OS style 
     *  The title and message are taken from the localised Alchemy bundle
     * 
     * @param winTitle      Title of the windows dialog
     * @param winMessage    Message of the windows dialog
     * @param macTitle      Title of the mac dialog 
     * @param macMessage    Message of the mac dialog
     * @return              True if OK, else false if Cancel
     */
    public static boolean showConfirmDialog(String winTitle, String winMessage, String macTitle, String macMessage) {
        if (Alchemy.PLATFORM == MACOSX) {
            return showConfirmDialog(macTitle, macMessage);
        } else {
            return showConfirmDialog(winTitle, winMessage);
        }
    }

    /** Show a confirmation dialog specific to the OS style 
     *  The title and message are taken from the localised Alchemy bundle
     * 
     * @param winTitle      Title of the windows dialog
     * @param winMessage    Message of the windows dialog
     * @param macTitle      Title of the mac dialog 
     * @param macMessage    Message of the mac dialog
     * @param bundle        The bundle to take localised text from
     * @return              True if OK, else false if Cancel
     */
    public static boolean showConfirmDialog(String winTitle, String winMessage, String macTitle, String macMessage, ResourceBundle bundle) {
        if (Alchemy.PLATFORM == MACOSX) {
            return showConfirmDialog(Alchemy.bundle.getString(macTitle), Alchemy.bundle.getString(macMessage));
        } else {
            return showConfirmDialog(Alchemy.bundle.getString(winTitle), Alchemy.bundle.getString(winMessage));
        }
    }

    /** Show a confirmation dialog specific to the OS style
     *  The title and message are taken from the localised Alchemy bundle
     * 
     * @param title     Title of the dialog
     * @param message   Message of the dialog
     * @return          True if OK, else false if Cancel
     */
    public static boolean showConfirmDialog(String title, String message) {

        if (Alchemy.PLATFORM == MACOSX) {
            message =
                    "<html>" + UIManager.get("OptionPane.css") +
                    "<b>" + title + "</b>" +
                    "<p>" + message;
            title = "";
        }

        //Object[] options = {Alchemy.bundle.getString("ok"), Alchemy.bundle.getString("cancel")};
        int result = JOptionPane.showOptionDialog(
                Alchemy.window,
                message,
                title,
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                null,
                null);

        if (result == JOptionPane.YES_OPTION) {
            return true;
        } else {
            return false;
        }
    }

    /** Ask for a location with a file chooser. 
     *  @return                     file/folder selected by the user
     */
    public static File showFileChooser() {
        return showFileChooser(null, null, false);
    }

    /** Ask for a location with a file chooser. 
     *  @param defaultDir           the default directory
     *  @return                     file/folder selected by the user
     */
    public static File showFileChooser(File defaultDir) {
        return showFileChooser(null, defaultDir, false);
    }

    /** Ask for a location with a file chooser. 
     *  @param  foldersOnly         to select only folders or not
     *  @return                     file/folder selected by the user
     */
    public static File showFileChooser(boolean foldersOnly) {
        return showFileChooser(null, null, foldersOnly);
    }

    /** Ask for a location with a file chooser. 
     *  @param defaultDir           the default directory
     *  @param  foldersOnly         to select only folders or not
     *  @return                     file/folder selected by the user
     */
    public static File showFileChooser(File defaultDir, boolean foldersOnly) {
        return showFileChooser(null, defaultDir, foldersOnly);
    }

    /** Ask for a location with a file chooser. 
     *  @param  title               the name of the popup title
     *  @param  foldersOnly         to select only folders or not
     *  @return                     file/folder selected by the user
     */
    public static File showFileChooser(String title, boolean foldersOnly) {
        return showFileChooser(title, null, foldersOnly);
    }

    /** Ask for a location with a file chooser. 
     *  @param  title               the name of the popup title
     *  @param defaultDir           the default directory
     *  @return                     file/folder selected by the user
     */
    public static File showFileChooser(String title, File defaultDir) {
        return showFileChooser(title, defaultDir, false);
    }

    /** Ask for a location with a file chooser. 
     *  @param  title               the name of the popup title
     *  @param  foldersOnly         to select only folders or not
     *  @param defaultDir           the default directory
     *  @return                     file/folder selected by the user
     */
    public static File showFileChooser(String title, File defaultDir, boolean foldersOnly) {
        AlcFileChooser fc = null;

        if (defaultDir != null && defaultDir.exists()) {
            fc = new AlcFileChooser(defaultDir);
        } else {
            fc = new AlcFileChooser();
        }

        if (foldersOnly) {
            fc.setFileSelectionMode(AlcFileChooser.DIRECTORIES_ONLY);
        }

        if (title != null) {
            fc.setDialogTitle(title);
        }

        // in response to a button click:
        int returnVal = fc.showOpenDialog(Alchemy.window);

        if (returnVal == AlcFileChooser.APPROVE_OPTION) {
            return fc.getSelectedFile();

        } else {
            return null;
        }
    }

    //////////////////////////////////////////////////////////////
    // COLOUR
    //////////////////////////////////////////////////////////////
    /** Get the brightness of a colour
     * 
     * @param rgb   An rgb colour (bit-shifted int format)
     * @return      The brightness as an int
     */
    public static int getColorBrightness(int rgb) {
        int oldR = (rgb >>> 16) & 255;
        int oldG = (rgb >>> 8) & 255;
        int oldB = (rgb >>> 0) & 255;
        return (222 * oldR + 707 * oldG + 71 * oldB) / 1000;
    }

    //////////////////////////////////////////////////////////////
    // DEBUGGING
    //////////////////////////////////////////////////////////////
    /** Print a float array
     * 
     * @param array Float array
     */
    public static void printFloatArray(float[] array) {
        for (int i = 0; i < array.length; i++) {
            System.out.println("Array [" + String.valueOf(i) + "] = " + String.valueOf(array[i]));
        }
    }

    /** Print a string array 
     * 
     * @param array String array
     */
    public static void printStringArray(String[] array) {
        for (int i = 0; i < array.length; i++) {
            System.out.println("Array [" + String.valueOf(i) + "] = " + array[i]);
        }
    }
}
