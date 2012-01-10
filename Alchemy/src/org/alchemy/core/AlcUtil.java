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

import com.sun.pdfview.*;
import eu.medsea.util.MimeUtil;
import java.awt.*;
import java.awt.datatransfer.*;
import java.awt.event.KeyEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.awt.image.BufferedImage;
import java.io.*;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.*;
import javax.swing.*;
import java.util.regex.*;

/**
 * Static utility methods used in Alchemy
 * Used to manipulate strings, load images, and general stuff
 */
public class AlcUtil implements AlcConstants {

    private final static Clipboard CLIPBOARD = TOOLKIT.getSystemClipboard();
    //////////////////////////////////////////////////////////////
    // STRING FUNCTIONS
    //////////////////////////////////////////////////////////////
    /** Checks a name for a file extension and adds one if not present 
     * 
     * @param file  The file to add the extenstion to
     * @param ext   The extension to add (without the leading dot)
     * @return      The new file with extension
     */
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

    /** Returns just the class name -- no package info. 
     * 
     * @param object    The object      
     * @return          The name of the class
     */
    public static String getClassName(Object object) {
        String classString = object.getClass().getName();
        int dotIndex = classString.lastIndexOf(".");
        return classString.substring(dotIndex + 1);
    }

    /** Function to append a string to the end of a given URL 
     * 
     * @param url       The url
     * @param append    The string to append
     * @return          The new url with string appended
     */
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

    /** Returns a string date stamp according to the format given 
     * 
     * @param format    Format for the date stamp see: SimpleDateFormat
     * @return          A string containing a formatted date
     * @throws IllegalArgumentException     if the given pattern is invalid
     */
    public static String dateStamp(String format) throws IllegalArgumentException {
        try {
            SimpleDateFormat formatter = new SimpleDateFormat(format, LOCALE);
            Date today = new Date();
            return formatter.format(today);
        } catch (IllegalArgumentException ex) {
            throw ex;
        }
    }

    /** Zero Pad an int 
     * 
     * @param i     The number to pad
     * @param len   The length required
     * @return      The padded number
     */
    public static String zeroPad(int i, int len) {
        // converts integer to left-zero padded string, len chars long.
        String s = Integer.toString(i);
        if (s.length() > len) {
            return s.substring(0, len);
        // pad on left with zeros
        } else if (s.length() < len) {
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
     * @param path  The name to the resource
     * @return      URL to the resource or null if invalid
     */
    public static URL getUrlPath(String path) {
        return getUrlPath(path, null);
    }

    /** Returns a URL from a String, or null if the name was invalid.
     * 
     * @param path          The name to the resource
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

    /** Returns an ImageIcon from the Alchemy data folder, or null if the name was invalid.
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

    /** Returns an Image from the Alchemy data folder, or null if the name was invalid.
     * 
     * @param name  The name to the image
     * @return      Image or null if invalid
     */
    public static Image getImage(String name) {
        URL imgUrl = Alchemy.class.getResource("/org/alchemy/data/" + name);
        if (imgUrl != null) {
            return getImageIcon(imgUrl).getImage();
        } else {
            System.err.println("Couldn't find file: " + name);
            return null;
        }
    }

    /** Returns an Image from a URL, or null if the name was invalid.
     * 
     * @param imgUrl    The URL to the image
     * @return          Image or null if invalid
     */
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

    /** Returns a BufferedImage from a URL, or null if the name was invalid.
     * 
     * @param imgUrl  The URL to the image
     * @return      BufferedImage or null if invalid
     */
    public static BufferedImage getBufferedImage(URL imgUrl) {
        Image image = getImage(imgUrl);
        if (image != null) {
            return getBufferedImage(image);
        }
        return null;
    }

    /** Returns a BufferedImage from a String, or null if the name was invalid.
     * 
     * @param name  The name to the image
     * @return      BufferedImage or null if invalid
     */
    public static BufferedImage getBufferedImage(String name) {
        Image image = getImage(name);
        if (image != null) {
            return getBufferedImage(image);
        }
        return null;
    }

    /** Convert an Image into a BufferedImage
     * 
     * @param image     The Image to be converted
     * @return          The Buffered Image or null if invalid
     */
    public static BufferedImage getBufferedImage(Image image) {
        if (image != null) {
            BufferedImage bufferedImage = new BufferedImage(image.getWidth(null), image.getHeight(null), BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2 = bufferedImage.createGraphics();
            g2.drawImage(image, 0, 0, image.getWidth(null), image.getHeight(null), null);
            g2.dispose();
            return bufferedImage;
        }
        return null;
    }

    /** Create a custom cursor from a given image file
     * 
     * @param name  The image file to use as the cursor
     * @return      A Custom cursor or the default cursor if the image can not be found
     */
    public static Cursor getCursor(String name) {

        // Cursor size differs depending on the platform
        // Add padding based on the best cursor size
        final Cursor customCursor;
        Image smallCursor = AlcUtil.getImage(name);

        // Check that the image returns correctly
        if (smallCursor == null) {
            // If the image is null return the default cursor
            return Cursor.getDefaultCursor();
        }

        Dimension smallCursorSize = new Dimension(smallCursor.getWidth(null), smallCursor.getHeight(null));
        Dimension cursorSize = TOOLKIT.getBestCursorSize(smallCursorSize.width, smallCursorSize.height);

        // If this cursor is the right size or custom cursors are not supported...    
        if (cursorSize.equals(smallCursorSize) || cursorSize.width <= 0 || cursorSize.height <= 0) {
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

    /** Return an array of AlcShape from PDF files in the shapes folder
     *  If no shapes are found a dialog is displayed telling the user to
     *  add some shapes to their shapes folder
     * 
     * @return  An array of AlcShapes or null if no shapes found
     */
    public static AlcShape[] getShapes() {
        ArrayList<AlcShape> shapes = new ArrayList<AlcShape>();

        // Folder of the plugins
        File shapesDir = new File("shapes");
        // Filter to check the MIME type, not just the file extension
        FilenameFilter pdfFilter = new FilenameFilter() {

            public boolean accept(File dir, String name) {

                File file = new File(dir, name);
                String mime = MimeUtil.getMimeType(file.getAbsoluteFile());
                return (mime.equals("application/pdf")) ? true : false;
            }
        };

        // Get the list of PDF files
        File[] pdfs = listFilesAsArray(shapesDir, pdfFilter, true);
        // For each pdf add the shapes to the array list
        for (int i = 0; i < pdfs.length; i++) {
            shapes.addAll(getPDFShapes(pdfs[i], true, 0));
        }
        if (shapes.size() > 0) {
            AlcShape[] arr = new AlcShape[shapes.size()];
            return shapes.toArray(arr);
        }
        showNoShapesDialog();
        return null;
    }

    /** Show a dialog informing the user there are no shapes loaded */
    public static void showNoShapesDialog() {
        String message = Alchemy.bundle.getString("noShapesMessage1") + "<br>" +
                Alchemy.preferences.shapesPath + "<br>" +
                Alchemy.bundle.getString("noShapesMessage2");
        showConfirmDialog(Alchemy.bundle.getString("noShapesTitle"), message);
    }

    /** 
     * Get a set of vector paths (shapes) from a PDF file.
     * Does not return clipping paths or
     * any shape that is bigger or the same size as the page
     * 
     * This is a rather long and hacky way using the swing labs
     * PDFRenderer library.
     * It uses reflection to access private variables but seems to be working...
     * for now anyway. 
     * 
     * @param file              The PDF file to retrive the shapes from
     * @param resetLocation     Reset the location of each path to 0,0
     * @return                  An array of AlcShapes from the PDF, else null
     */
    public static AlcShape[] getPDFShapesAsArray(File file, boolean resetLocation) {
        return getPDFShapesAsArray(file, resetLocation, 0);
    }

        /**
     * Get a set of vector paths (shapes) from a PDF file.
     * Does not return clipping paths or
     * any shape that is bigger or the same size as the page
     *
     * This is a rather long and hacky way using the swing labs
     * PDFRenderer library.
     * It uses reflection to access private variables but seems to be working...
     * for now anyway.
     *
     * @param file              The PDF file to retrive the shapes from
     * @param resetLocation     Reset the location of each path to 0,0
     * @param pixelSize         Pixel size to scale the shapes to. No scaling if less than zero.
     * @return                  An array of AlcShapes from the PDF, else null
     */
    public static AlcShape[] getPDFShapesAsArray(File file, boolean resetLocation, int pixelSize) {
        Collection<AlcShape> shapes = getPDFShapes(file, resetLocation, pixelSize);
        if (shapes != null) {
            AlcShape[] arr = new AlcShape[shapes.size()];
            return shapes.toArray(arr);
        }
        return null;
    }

    /** 
     * Get a set of vector paths (shapes) from a PDF file.
     * 
     * @param file              The PDF file to retrive the shapes from
     * @param resetLocation     Reset the location of each path to 0,0
     * @param pixelSize         Pixel size to scale the shapes to. No scaling if less than zero.
     * @return                  A Collection of AlcShapes from the PDF, else null
     */
    public static Collection<AlcShape> getPDFShapes(File file, boolean resetLocation, int pixelSize) {
        // set up the PDF reading
        PDFFile pdfFile = null;
        PDFPage pdfPage = null;
        int totalPages = 0;

        try {
            RandomAccessFile raf = new RandomAccessFile(file, "r");
            FileChannel channel = raf.getChannel();
            java.nio.ByteBuffer buf = channel.map(FileChannel.MapMode.READ_ONLY, 0, channel.size());
            pdfFile = new PDFFile(buf);
            totalPages = pdfFile.getNumPages();


        } catch (Exception ex) {
            System.err.println("Failed to load file");
            ex.printStackTrace();
            return null;
        }

        // Create an arraylist to populate with just the shapes
        ArrayList<AlcShape> shapeList = new ArrayList<AlcShape>(totalPages * 10);

        // Go through each of the pages
        for (int p = 0; p < totalPages; p++) {
            pdfPage = pdfFile.getPage(p);

            // Token size because we are not actually rendering
            int size = 1;
            BufferedImage buffImage = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2 = buffImage.createGraphics();
            PDFRenderer renderer = new PDFRenderer(pdfPage, g2, new Rectangle(0, 0, size, size), null, null);

            java.awt.Rectangle pageBounds = new java.awt.Rectangle(0, 0, (int) pdfPage.getWidth(), (int) pdfPage.getHeight());

            // Have to run the renderer to populate the command list fully 
            try {
                pdfPage.waitForFinish();
                renderer.run();
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }

            int totalCommands = pdfPage.getCommandCount();

            try {
                for (int c = 0; c < totalCommands; c++) {
                    PDFCmd command = pdfPage.getCommand(c);
                    if (command instanceof PDFShapeCmd) {
                        PDFShapeCmd shapeCommand = (PDFShapeCmd) command;

                        // Hack into the command to get the path
                        Class shapeClass = shapeCommand.getClass();
                        Field pathField = shapeClass.getDeclaredField("gp");
                        pathField.setAccessible(true);
                        GeneralPath gp = (GeneralPath) pathField.get(shapeCommand);
                        java.awt.Rectangle gpBounds = gp.getBounds();
                        // Hack into the command to get the style
                        Field styleField = shapeClass.getDeclaredField("style");
                        styleField.setAccessible(true);
                        int style = (Integer) styleField.get(shapeCommand);

                        // If the style is not a clipping path
                        if (style != STYLE_CLIP) {
                            // Save the shape if it is within the page size
                            if (gpBounds.width < pageBounds.width && gpBounds.height < pageBounds.height && !pageBounds.equals(gpBounds)) {
                                
                                // Scale to a set pixel size
                                if(pixelSize > 0){
                                    // Figure out the longest side
                                    int longestSize = (gpBounds.width > gpBounds.height) ? gpBounds.width : gpBounds.height;
                                    // Create the scaling factor
                                    double scale = (float) pixelSize / longestSize;
                                    AffineTransform scaleTransform = new AffineTransform();
                                    scaleTransform.scale(scale, scale);
                                    gp = (GeneralPath) gp.createTransformedShape(scaleTransform);
                                    gpBounds = gp.getBounds();
                                }

                                // For some reason the shapes come in flipped, o re-flip them here
                                AffineTransform transform = new AffineTransform();
                                int axis = (gpBounds.y * 2) + gpBounds.height;
                                // Move the reflection into place and reset to 0,0 if required
                                if (resetLocation) {
                                    transform.translate(0 - gpBounds.x, axis - gpBounds.y);
                                } else {
                                    transform.translate(0, axis);
                                }

                                // Reflect it using a negative scale
                                transform.scale(1, -1);
                                GeneralPath transformedPath = (GeneralPath) gp.createTransformedShape(transform);

                               
                                AlcShape shape = new AlcShape(transformedPath);
                                shape.recalculateTotalPoints();
                                if (style == STYLE_BOTH) {
                                    shape.setStyle(STYLE_FILL);
                                } else {
                                    shape.setStyle(style);
                                }
                                shapeList.add(shape);
                            }
                        }

                    }
                }


            } catch (Exception ex) {
                ex.printStackTrace();
                return null;
            } finally {
                g2.dispose();
            }
        }
        // If there are shapes, then return them as an  array
        if (shapeList.size() > 0) {
            return shapeList;
        }
        return null;
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

    /** List all files within a folder and all its sub folders
     * 
     * @param directory The directory to list
     * @param filter    The filter to use
     * @param recurse   To list all sub folders or not
     * @return          An array of files
     */
    public static File[] listFilesAsArray(File directory, FilenameFilter filter, boolean recurse) {
        Collection<File> files = listFiles(directory, filter, recurse);
        File[] arr = new File[files.size()];
        return files.toArray(arr);
    }

    private static Collection<File> listFiles(File directory, FilenameFilter filter, boolean recurse) {
        // List of files / directories
        ArrayList<File> files = new ArrayList<File>();

        // Get files / directories in the directory
        File[] entries = directory.listFiles();

        // Go over entries
        for (File entry : entries) {

            // If there is no filter or the filter accepts the 
            // file / directory, add it to the list
            if (filter == null || filter.accept(directory, entry.getName())) {
                files.add(entry);
            }

            // If the file is a directory and the recurse flag
            // is set, recurse into the directory
            if (recurse && entry.isDirectory()) {
                files.addAll(listFiles(entry, filter, recurse));
            }
        }

        // Return collection of files
        return files;
    }
    
    //////////////////////////////////////////////////////////////
    // UI FUNCTIONS
    //////////////////////////////////////////////////////////////

    /**
     *  Calculate the centre of the screen with multiple monitors
     *
     * @param popup         The popup window
     * @return              The centred location as a Point
     */
    public static Point calculateCenter(Container popup) {
        return calculateCenter(popup, false);
    }
    
    /**
     *  Calculate the centre of the screen with multiple monitors
     * 
     * @param popup         The popup window
     * @param palette       Popup in the window of the palette
     * @return              The centred location as a Point
     */
    public static Point calculateCenter(Container popup, boolean palette) {
        GraphicsConfiguration grapConfig = popup.getGraphicsConfiguration();
        Rectangle mainWindowBounds = grapConfig.getBounds();
        // Each screen device
        GraphicsDevice[] devices = GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices();
        // The popup window bounds
        Rectangle popupBounds = popup.getBounds();
        // The parent bounds
        Rectangle parentBounds = Alchemy.window.getBounds();
        // If we want to popup in the palette window
        if(palette && Alchemy.preferences.paletteAttached){
            parentBounds = Alchemy.palette.getBounds();
        }

        // The bounds of the window containing the parent
        Rectangle windowBounds = null;

        if (devices.length > 1) {
            for (int i = 0; i < devices.length; i++) {
                Rectangle screenBounds = devices[i].getDefaultConfiguration().getBounds();
                if (screenBounds.contains(parentBounds.x + parentBounds.width/2, parentBounds.y + parentBounds.height/2)) {
                    windowBounds = screenBounds;
                }
            }
        }

        if(windowBounds == null){
            windowBounds = mainWindowBounds;
        }

        int x = windowBounds.x + (windowBounds.width - popupBounds.width) / 2;
        int y = windowBounds.y + (windowBounds.height - popupBounds.height) / 2;
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
            if (Alchemy.OS == OS_MAC) {
                Class fileMgr = Class.forName("com.apple.eio.FileManager");
                Method openURL = fileMgr.getDeclaredMethod("openURL",
                        new Class[]{String.class});
                openURL.invoke(null, new Object[]{url});

            } else if (Alchemy.OS == OS_WINDOWS) {
                Runtime.getRuntime().exec("rundll32 url.dll,FileProtocolHandler " + url);

            } else if (Alchemy.OS == OS_LINUX) {
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
    static String openLauncher;

    /** Open a local pdf in the default application
     * 
     * @param pdf   A file pointing to the pdf to open
     */
    public static void openPDF(File pdf) {
        final String errMsg = "Error attempting to launch pdf";
        try {
            if (Alchemy.OS == OS_MAC) {
                Runtime.getRuntime().exec("open " + pdf.getAbsolutePath());
            } else if (Alchemy.OS == OS_WINDOWS) {
                Runtime.getRuntime().exec("rundll32 url.dll,FileProtocolHandler " + pdf.getAbsolutePath());
            } else if (Alchemy.OS == OS_LINUX) {

                if (openLauncher == null) {
                    // Attempt to use gnome-open
                    try {
                        Process p = Runtime.getRuntime().exec(new String[]{"gnome-open"});
                        /*int result =*/ p.waitFor();
                        // Not installed will throw an IOException (JDK 1.4.2, Ubuntu 7.04)
                        openLauncher = "gnome-open";
                    } catch (Exception e) {
                    }
                }
                if (openLauncher == null) {
                    // Attempt with kde-open
                    try {
                        Process p = Runtime.getRuntime().exec(new String[]{"kde-open"});
                        /*int result =*/ p.waitFor();
                        openLauncher = "kde-open";
                    } catch (Exception e) {
                    }
                }
                if (openLauncher == null) {
                    System.err.println("Could not find gnome-open or kde-open, " +
                            "the command may not work.");
                }
                if (openLauncher != null) {
                    // params = new String[]{openLauncher};
                    Runtime.getRuntime().exec(openLauncher + " " + pdf.getAbsolutePath());
                }

            //Runtime.getRuntime().exec("gnome-open " + pdf.getAbsolutePath());
            }
        } catch (Exception e) {
            System.err.println(errMsg + ":\n" + e.getLocalizedMessage());
        }
    }

    /**
     * Sets the current contents of the clipboard to the specified transferable object and registers the specified clipboard owner as the owner of the new contents. 
     * Shows warning message if an exception occured
     * @param contents
     * @param owner 
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
    
     /** Show a notification dialog specific to the OS style
     *  The title and message are taken from the localised Alchemy bundle
     * 
     * @param title     Title of the dialog
     * @param message   Message of the dialog
     * @type            JOptionPane Dialog Type (example JOptionPane.ERROR_MESSAGE)
     * 
     */
    public static void showMessageDialog(String title, String message, int type) {

        if (Alchemy.OS == OS_MAC) {
            message =
                    "<html>" + UIManager.get("OptionPane.css") +
                    "<b>" + title + "</b>" +
                    "<p>" + message;
            title = "";
        }
        
        JOptionPane.showMessageDialog(null, title, message, type);
        
    }

    /** Show a confirmation dialog specific to the OS style
     *  The title and message are taken from the localised Alchemy bundle
     * 
     * @param title     Title of the dialog
     * @param message   Message of the dialog
     * @return          True if OK, else false if Cancel
     */
    public static boolean showConfirmDialog(String title, String message) {

        if (Alchemy.OS == OS_MAC) {
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

    /** Show a confirmation dialog specific to the OS style 
     *  The title and message are taken from the localised Alchemy bundle
     * 
     * @param title     Title of the dialog
     * @param message   Message of the dialog
     * @return          True if OK, else false if Cancel
     */
    public static boolean showConfirmDialogFromBundle(String title, String message) {
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
        if (Alchemy.OS == OS_MAC) {
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
     * @return              True if OK, else false if Cancel
     */
    public static boolean showConfirmDialogFromBundle(String winTitle, String winMessage, String macTitle, String macMessage) {
        if (Alchemy.OS == OS_MAC) {
            return showConfirmDialogFromBundle(macTitle, macMessage);
        } else {
            return showConfirmDialogFromBundle(winTitle, winMessage);
        }
    }

    /** Ask for a location with a file chooser. 
     *  @return                     file/folder selected by the user
     */
    public static File showFileChooser() {
        return showFileChooser(null, null, false, null);
    }

    /** Ask for a location with a file chooser. 
     *  @param  title               the name of the popup title
     *  @return                     file/folder selected by the user
     */
    public static File showFileChooser(String title) {
        return showFileChooser(title, null, false, null);
    }

    /** Ask for a location with a file chooser. 
     *  @param defaultDir           the default directory
     *  @return                     file/folder selected by the user
     */
    public static File showFileChooser(File defaultDir) {
        return showFileChooser(null, defaultDir, false, null);
    }

    /** Ask for a location with a file chooser. 
     *  @param  foldersOnly         to select only folders or not
     *  @return                     file/folder selected by the user
     */
    public static File showFileChooser(boolean foldersOnly) {
        return showFileChooser(null, null, foldersOnly, null);
    }

    /** Ask for a location with a file chooser. 
     *  @param  foldersOnly      to select only folders or not
     *  @param parent            the parent component of the dialog, can be null; see showDialog for details
     * @return                   file/folder selected by the user
     */
    public static File showFileChooser(boolean foldersOnly, Component parent) {
        return showFileChooser(null, null, foldersOnly, parent);
    }

    /** Ask for a location with a file chooser. 
     *  @param defaultDir           the default directory
     *  @param  foldersOnly         to select only folders or not
     *  @return                     file/folder selected by the user
     */
    public static File showFileChooser(File defaultDir, boolean foldersOnly) {
        return showFileChooser(null, defaultDir, foldersOnly, null);
    }

    /** Ask for a location with a file chooser. 
     *  @param  title               the name of the popup title
     *  @param  foldersOnly         to select only folders or not
     *  @return                     file/folder selected by the user
     */
    public static File showFileChooser(String title, boolean foldersOnly) {
        return showFileChooser(title, null, foldersOnly, null);
    }

    /** Ask for a location with a file chooser. 
     *  @param  title               the name of the popup title
     *  @param defaultDir           the default directory
     *  @return                     file/folder selected by the user
     */
    public static File showFileChooser(String title, File defaultDir) {
        return showFileChooser(title, defaultDir, false, null);
    }

    /** Ask for a location with a file chooser. 
     *  @param  title               the name of the popup title
     *  @param  foldersOnly         to select only folders or not
     *  @param defaultDir           the default directory
     *  @param parent               the parent component of the dialog, can be null; see showDialog for details
     * @return                     file/folder selected by the user
     */
    public static File showFileChooser(String title, File defaultDir, boolean foldersOnly, Component parent) {
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
        int returnVal = (parent == null) ? fc.showOpenDialog(Alchemy.window) : fc.showOpenDialog(parent);

        if (returnVal == AlcFileChooser.APPROVE_OPTION) {
            return fc.getSelectedFile();

        } else {
            return null;
        }
    }

    /**
     * Registers key events for a Ctrl-W and ESC with an ActionListener
     * that will take care of disposing the window.
     * @param root          The window
     * @param closeAction   The action to be called 
     */
    public static void registerWindowCloseKeys(JRootPane root, Action closeAction) {
        // Shortcut to close with escape
        root.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "Close Window");
        // Shortcut to close with a modifier - w
        root.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_W, KEY_MODIFIER), "Close Window");
        // Assign the action for the two keys
        root.getActionMap().put("Close Window", closeAction);
    }
    //////////////////////////////////////////////////////////////
    // DRAWING
    //////////////////////////////////////////////////////////////
    public static void drawSoftRect(Graphics g, int x, int y, int width, int height) {
        // CORNERS
        g.setColor(COLOR_UI_LINE_ALPHA);
        // Top Left
        g.drawLine(x, y, x, y);
        // Top Right
        g.drawLine(x + width - 1, y, x + width - 1, y);
        // Bottom Left
        g.drawLine(x, y + height - 1, x, y + height - 1);
        // Bottom Right
        g.drawLine(x + width - 1, y + height - 1, x + width - 1, y + height - 1);

        // LINES
        g.setColor(COLOR_UI_LINE);
        // Top 
        g.drawLine(x + 1, y, x + width - 2, y);
        // Right
        g.drawLine(x + width - 1, y + 1, x + width - 1, y + height - 2);
        // Bottom
        g.drawLine(x + width - 2, y + height - 1, x + 1, y + height - 1);
        // Left
        g.drawLine(x, y + height - 2, x, y + 1);
    }
    //////////////////////////////////////////////////////////////
    // COLOR
    //////////////////////////////////////////////////////////////
    /** Get the brightness of a color
     * 
     * @param rgb   An rgb color (bit-shifted int format)
     * @return      The brightness as an int
     */
    public static int getColorBrightness(int rgb) {
        int oldR = (rgb >>> 16) & 255;
        int oldG = (rgb >>> 8) & 255;
        int oldB = (rgb >>> 0) & 255;
        return (222 * oldR + 707 * oldG + 71 * oldB) / 1000;
    }

    //////////////////////////////////////////////////////////////
    // HEX/BINARY CONVERSION
    //////////////////////////////////////////////////////////////
    /**
     * Converts a byte, char, int, or color to a String containing the equivalent hexadecimal notation. 
     * For example color(0, 102, 153, 255) will convert to the String "FF006699".
     * @param i
     * @return
     */
    public static String hex(byte i) {
        return hex(i, 2);
    }

    public static String hex(char c) {
        return hex(c, 4);
    }

    public static String hex(int i) {
        return hex(i, 8);
    }

    public static String hex(int i, int digits) {
        String stuff = Integer.toHexString(i).toUpperCase();

        int length = stuff.length();
        if (length > digits) {
            return stuff.substring(length - digits);

        } else if (length < digits) {
            return "00000000".substring(8 - (digits - length)) + stuff;
        }
        return stuff;
    }

    public static int unhex(String s) {
        // has to parse as a Long so that it'll work for numbers bigger than 2^31
        return (int) (Long.parseLong(s, 16));
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
