/*
 * This file is part of the Alchemy project - http://al.chemy.org
 * 
 * Copyright (c) 2007-2010 Karl D.D. Willis
 * 
 * Alchemy is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * Alchemy is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with Alchemy.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.alchemy.create;

import org.alchemy.core.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import foxtrot.*;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.io.File;
import java.io.IOException;
import java.net.*;
import java.util.*;
import java.util.logging.*;
import javax.imageio.ImageIO;
import javax.swing.event.*;
import javax.xml.parsers.*;
import org.w3c.dom.*;
import org.xml.sax.SAXException;

/**
 * TraceShapes
 * @author Karl D.D. Willis
 */
public class TraceShapes extends AlcModule implements AlcConstants {

    private int halfArea = 30;
    private int tolerance = 100;
    private Rectangle imageSize;
    private AlcToolBarSubSection subToolBarSection;
    private BufferedImage image;
//    private int[] pixels;
//    private boolean pixelsLoaded = false;
    private boolean moduleActive = false;
    private boolean imageDisplay = false;

    public TraceShapes() {
    }

    @Override
    protected void setup() {
        moduleActive = true;
        canvas.setImageDisplay(false);
        createSubToolBarSection();
        toolBar.addSubToolBarSection(subToolBarSection);
        downloadImage();
    }

    @Override
    protected void reselect() {
        moduleActive = true;
        // Add this modules toolbar to the main ui
        toolBar.addSubToolBarSection(subToolBarSection);
        downloadImage();
    }

    @Override
    protected void deselect() {
        moduleActive = false;
//        pixelsLoaded = false;
//        pixels = null;
        image = null;
        canvas.setImage(null);
        //canvas.setImageDisplay(false);
        canvas.redraw();
    }

    private void downloadImage() {

        final String random = AlcUtil.zeroPad((int) math.random(10000), 5);
        //System.out.println(random);
        BufferedImage flickrImage = null;
//        pixelsLoaded = false;

        try {
            flickrImage = (BufferedImage) Worker.post(new Task() {

                public Object run() throws Exception {
                    return TraceShapesFlickr.getInstance().search(random);
                }
            });
        } catch (Exception ignored) {
        }


        if (flickrImage != null) {

            // Make sure the module has not been deselected while loading
            if (moduleActive) {
                setImage(flickrImage);
            }

        } else {
            // Tell the user that there was a problem loading the image
            AlcUtil.showConfirmDialog("Error Connecting", "Please check your internet connection.");
        }

    }

    private void loadImage() {
        File file = AlcUtil.showFileChooser(new File(DIR_DESKTOP), false);
        if (file != null && file.exists()) {
            Image newImage = null;
            try {
                newImage = AlcUtil.getImage(file.toURI().toURL());
            } catch (Exception ex) {
                // Ignore
            }
            if (newImage != null) {
                setImage(AlcUtil.getBufferedImage(newImage));
            } else {
                AlcUtil.showConfirmDialogFromBundle("imageErrorDialogTitle", "imageErrorDialogMessage");
            }
        }
    }

    private void setImage(BufferedImage newImage) {
        // Scale the image to the screen size
        imageSize = canvas.getVisibleRect();

        double scale;
        double widthScale = (float) imageSize.width / (float) newImage.getWidth();
        double heightScale = (float) imageSize.height / (float) newImage.getHeight();

        // Use the smaller scale
        if (widthScale < heightScale) {
            scale = widthScale;
        } else {
            scale = heightScale;
        }
        // Calculate the new size               
        int newWidth = (int) Math.round(newImage.getWidth() * scale);
        int newHeight = (int) Math.round(newImage.getHeight() * scale);
        imageSize.x = (imageSize.width - newWidth) / 2;
        imageSize.y = (imageSize.height - newHeight) / 2;
        imageSize.width = newWidth;
        imageSize.height = newHeight;

        Image scaledImage = newImage.getScaledInstance(imageSize.width, imageSize.height, Image.SCALE_FAST);
        image = AlcUtil.getBufferedImage(scaledImage);

        canvas.setImageLocation(imageSize.x, imageSize.y);
        canvas.setImageDisplay(imageDisplay);
        canvas.setImage(image);
        canvas.redraw();
    }

    private Point checkSnap(Point p) {
        if (image != null && imageSize.contains(p)) {
            Point aP = new Point(p.x - imageSize.x, p.y - imageSize.y);
            // The pixel value under the cursor
            int xy = AlcUtil.getColorBrightness(image.getRGB(aP.x, aP.y));

            // Where to look
            int startX = aP.x - halfArea;
            if (startX < 0) {
                startX = 0;
            }
            int startY = aP.y - halfArea;
            if (startY < 0) {
                startY = 0;
            }
            int endX = aP.x + halfArea;
            if (endX > imageSize.width) {
                endX = imageSize.width;
            }
            int endY = aP.y + halfArea;
            if (endY > imageSize.height) {
                endY = imageSize.height;
            }

            int contrast = 0;
            Point bestContrastPoint = null;

            for (int x = startX; x < endX; x++) {
                for (int y = startY; y < endY; y++) {
                    int thisPixel = AlcUtil.getColorBrightness(image.getRGB(x, y));
                    int difference = Math.abs(thisPixel - xy);
                    if (difference > tolerance) {
                        if (difference > contrast) {
                            contrast = difference;
                            bestContrastPoint = new Point(x + imageSize.x, y + imageSize.y);
                        }
                    }
                }
            }
            if (bestContrastPoint != null) {
                //System.out.println("Contrast: " + contrast);
                return bestContrastPoint;
            }
        }

        return p;
    }
//
//    private int getPixel(int x, int y) {
//        return pixels[y * imageSize.width + x];
//    }
    public void createSubToolBarSection() {
        subToolBarSection = new AlcToolBarSubSection(this);

        // Image Display Button
        final AlcSubToggleButton imageDisplayButton = new AlcSubToggleButton("Display Image", AlcUtil.getUrlPath("imagedisplay.png", getClassLoader()));
        imageDisplayButton.setToolTipText("Turn image display on or off");
        imageDisplayButton.addActionListener(
                new ActionListener() {

                    public void actionPerformed(ActionEvent e) {
                        imageDisplay = imageDisplayButton.isSelected();
                        canvas.setImageDisplay(imageDisplay);
                        canvas.redraw();
                    }
                });
        subToolBarSection.add(imageDisplayButton);

        // Download Image Button
        AlcSubButton downloadImage = new AlcSubButton("Download Image", AlcUtil.getUrlPath("download.png", getClassLoader()));
        downloadImage.setToolTipText("Download a new image from the internet");
        downloadImage.addActionListener(
                new ActionListener() {

                    public void actionPerformed(ActionEvent e) {
                        downloadImage();
                    }
                });
        subToolBarSection.add(downloadImage);


        // Load Image Button
        AlcSubButton loadImage = new AlcSubButton("Load Image", AlcUtil.getUrlPath("load.png", getClassLoader()));
        loadImage.setToolTipText("Load an image from your computer");
        loadImage.addActionListener(
                new ActionListener() {

                    public void actionPerformed(ActionEvent e) {
                        loadImage();
                    }
                });
        subToolBarSection.add(loadImage);

        // Snap Distance Slider
        final AlcSubSlider snapSlider = new AlcSubSlider("Snap Distance", 1, 100, halfArea);
        snapSlider.setToolTipText("Adjust the snap distance");
        snapSlider.addChangeListener(
                new ChangeListener() {

                    public void stateChanged(ChangeEvent e) {
                        if (!snapSlider.getValueIsAdjusting()) {
                            halfArea = snapSlider.getValue();

                        }
                    }
                });
        subToolBarSection.add(snapSlider);

        // Tolerance Slider
        final AlcSubSlider toleranceSlider = new AlcSubSlider("Tolerance", 0, 200, tolerance);
        toleranceSlider.setToolTipText("Adjust the snapping tolerance");
        toleranceSlider.addChangeListener(
                new ChangeListener() {

                    public void stateChanged(ChangeEvent e) {












                        if (!toleranceSlider.getValueIsAdjusting()) {
                            tolerance = toleranceSlider.getValue();

                        }
                    }
                });
        subToolBarSection.add(toleranceSlider);
    }

    @Override
    public void mousePressed(MouseEvent e) {
        Point p = e.getPoint();
        canvas.createShapes.add(new AlcShape(p));
        canvas.redraw();
    }

    @Override
    public void mouseDragged(MouseEvent e) {

        // Need to test if it is null incase the shape has been auto-cleared
        if (canvas.hasCreateShapes()) {
            Point p = e.getPoint();
            canvas.getCurrentCreateShape().curveTo(checkSnap(p));
            canvas.redraw();
        }

    }

    @Override
    public void mouseReleased(MouseEvent e) {
        canvas.commitShapes();
    }
}

/**
 * Return an Icon for the first Flickr photo that matches a query string.
 * Typical usage:
 * <pre>
 * Icon image = TraceShapesFlickr.getInstance().search("face");
 * myLabel.setIcon(image);
 * </pre>
 *
 */
class TraceShapesFlickr {

    private static TraceShapesFlickr theInstance = null;
    private final Logger logger;
    private final DocumentBuilder xmlParser;

    /* URL format string that specifies a single "medium" sized photo on
     * the Flickr server.  Based on the URL syntax documented here:
     * http://www.flickr.com/services/api/misc.urls.html, i.e.
     * http://farm{farm-id}.static.flickr.com/{server-id}/{id}_{secret}_[mstb].jpg"
     */
    private final String photoURLFormat =
            "http://farm%s.static.flickr.com/%s/%s_%s.jpg";
    /* An HTTP get format string for looking up a single "Photo" that matches
     * a query string.  This request is documented on the Yahoo/TraceShapesFlickr
     * site here: http://www.flickr.com/services/api/flickr.photos.search.html
     */
    private final String searchMethodFormat =
            "http://www.flickr.com/services/rest/?method=flickr.photos.search" +
            "&format=rest" +
            "&api_key=3f44e4e680a2a1b89af1b4bb803057ac" +
            "&per_page=1" + // just send one match back
            //"&sort=interestingness-desc" +
            //"&sort=date-posted-desc" +
            //"&page=3" +
            "&text=";

    private TraceShapesFlickr() throws ParserConfigurationException {
        logger = Logger.getLogger(TraceShapesFlickr.class.getName());
        DocumentBuilderFactory dcb = DocumentBuilderFactory.newInstance();
        this.xmlParser = dcb.newDocumentBuilder();
    }

    static TraceShapesFlickr getInstance() {
        if (theInstance == null) {
            try {
                theInstance = new TraceShapesFlickr();
            } catch (ParserConfigurationException e) {
                throw new Error("fatal error", e);
            }
        }
        return theInstance;
    }

    private URL newURL(String s) {
        try {
            return new URL(s);
        } catch (MalformedURLException e) {
            logger.log(Level.WARNING, "bad URL: " + s + " " + e);
            return null;
        }
    }

    private Document getPage(URL url) {
        Document doc = null;
        try {
            doc = xmlParser.parse(url.toString());
        } catch (SAXException e) {
            logger.log(Level.WARNING, "can't parse value of  URL: " + url + " " + e);
        } catch (IOException e) {
            logger.log(Level.WARNING, "can't load value of  URL: " + url + " " + e);
        }
        return doc;
    }

    private List elementsWithTag(Document doc, String tag) {
        NodeList nodes = doc.getElementsByTagName(tag);
        if ((nodes != null) && (nodes.getLength() > 0)) {
            ArrayList<Element> elements = new ArrayList<Element>(nodes.getLength());
            for (int i = 0; i < nodes.getLength(); i++) {
                elements.add((Element) nodes.item(i));
            }
            return elements;
        } else {
            logger.warning("no elements with tag " + tag + "at " + doc.toString());
            return null;
        }
    }

    private String elementAttribute(Element elt, String attribute) {
        String s = elt.getAttribute(attribute);
        return (s.length() == 0) ? null : s;
    }

    Image search(String keyword) {
        URL searchURL = newURL(searchMethodFormat + keyword);
        if (searchURL == null) {
            return null;
        }
        Document doc = getPage(searchURL);
        if (doc == null) {
            return null;
        }
        List elts = elementsWithTag(doc, "photo");
        if (elts == null) {
            return null;
        }
        Element elt = (Element) elts.get(0);
        String farm = elementAttribute(elt, "farm");
        String server = elementAttribute(elt, "server");
        String id = elementAttribute(elt, "id");
        String secret = elementAttribute(elt, "secret");
        Image image = null;
        if ((farm != null) && (server != null) && (id != null) && (secret != null)) {
            URL imageURL = newURL("http://farm" + farm + ".static.flickr.com/" + server + "/" + id + "_" + secret + ".jpg");
            if (imageURL != null) {
                try {
                    image = ImageIO.read(imageURL);
                } catch (IOException e) {
                    logger.log(Level.WARNING, "couldn't load: " + imageURL + " " + e);
                }
            }
        }
        return image != null ? image : null;
    }
}
