/*
 * This file is part of the Alchemy project - http://al.chemy.org
 * 
 * Copyright (c) 2007-2008 Karl D.D. Willis
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
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import foxtrot.*;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * TraceShapes
 * @author Karl D.D. Willis
 */
public class TraceShapes extends AlcModule implements AlcConstants {

    private int halfArea = 30;
    private int tolerance = 100;
    private Rectangle imageSize;
    private AlcSubToolBarSection subToolBarSection;
    private int[] pixels;
    private boolean pixelsLoaded = false;
    private boolean moduleActive = false;

    public TraceShapes() {

    }

    @Override
    protected void setup() {
        moduleActive = true;
        canvas.setImageDisplay(false);
        createSubToolBarSection();
        toolBar.addSubToolBarSection(subToolBarSection);
        loadImage();
    }

    @Override
    protected void reselect() {
        moduleActive = true;
        // Add this modules toolbar to the main ui
        toolBar.addSubToolBarSection(subToolBarSection);
        loadImage();
    }

    @Override
    protected void deselect() {
        moduleActive = false;
        pixelsLoaded = false;
        pixels = null;
        canvas.setImage(null);
        //canvas.setImageDisplay(false);
        canvas.redraw();
    }

    private void loadImage() {

        final String random = AlcUtil.zeroPad((int) math.random(10000), 5);
        //System.out.println(random);
        BufferedImage flickrImage = null;
        pixelsLoaded = false;

        try {
            flickrImage = (BufferedImage) Worker.post(new Task() {

                public Object run() throws Exception {
                    return Flickr.getInstance().search(random);
                }
            });
        } catch (Exception ignored) {
        }


        if (flickrImage != null) {
            // Make sure the module has not been deselected while loading
            if (moduleActive) {
                // Scale the image to the screen size
                imageSize = canvas.getBounds();
                BufferedImage scaledImage = new BufferedImage(imageSize.width, imageSize.height, BufferedImage.TYPE_INT_RGB);
                Graphics2D g2 = scaledImage.createGraphics();
                g2.drawImage(flickrImage, 0, 0, imageSize.width, imageSize.height, null);
                g2.dispose();
                flickrImage = null;

                // Load the pixels into an array
                pixels = new int[imageSize.width * imageSize.height];
                scaledImage.getRGB(0, 0, imageSize.width, imageSize.height, pixels, 0, imageSize.width);
                // Then convert them all to grey for easy access
                for (int i = 0; i < pixels.length; i++) {
                    pixels[i] = AlcUtil.getColorBrightness(pixels[i]);
                }
                pixelsLoaded = true;

                canvas.setImage(scaledImage);
                canvas.redraw();
            }
        } else {
            // Tell the user that there was a problem loading the image
            AlcUtil.showConfirmDialog("Error Connecting", "Please check your internet connection.");
        }

    }

    private Point checkSnap(Point p) {
        if (pixelsLoaded && imageSize.contains(p)) {
            // The pixel value under the cursor
            int xy = getPixel(p.x, p.y);

            // Where to look in the array
            int startX = p.x - halfArea;
            if (startX < 0) {
                startX = 0;
            }
            int startY = p.y - halfArea;
            if (startY < 0) {
                startY = 0;
            }
            int endX = p.x + halfArea;
            if (endX > imageSize.width) {
                endX = imageSize.width;
            }
            int endY = p.y + halfArea;
            if (endY > imageSize.height) {
                endY = imageSize.height;
            }

            int contrast = 0;
            Point bestContrastPoint = null;

            for (int x = startX; x < endX; x++) {
                for (int y = startY; y < endY; y++) {
                    int thisPixel = getPixel(x, y);
                    int difference = Math.abs(thisPixel - xy);
                    if (difference > tolerance) {
                        if (difference > contrast) {
                            contrast = difference;
                            bestContrastPoint = new Point(x, y);
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

    private int getPixel(int x, int y) {
        return pixels[y * imageSize.width + x];
    }

    public void createSubToolBarSection() {
        subToolBarSection = new AlcSubToolBarSection(this);

        // Image Display Button
        final AlcSubToggleButton imageDisplayButton = new AlcSubToggleButton("Display Image", AlcUtil.getUrlPath("imagedisplay.png", getClassLoader()));
        imageDisplayButton.setToolTipText("Turn image display on or off");
        imageDisplayButton.addActionListener(
                new ActionListener() {

                    public void actionPerformed(ActionEvent e) {
                        canvas.setImageDisplay(imageDisplayButton.isSelected());
                        canvas.redraw();
                    }
                });
        subToolBarSection.add(imageDisplayButton);

        // Run Button
        AlcSubButton runButton = new AlcSubButton("Load Image", AlcUtil.getUrlPath("load.png", getClassLoader()));
        runButton.setToolTipText("Load a new image");
        runButton.addActionListener(
                new ActionListener() {

                    public void actionPerformed(ActionEvent e) {
                        loadImage();
                    }
                });
        subToolBarSection.add(runButton);

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
        if (canvas.getCurrentCreateShape() != null) {
            Point p = e.getPoint();
            canvas.getCurrentCreateShape().addCurvePoint(checkSnap(p));
            canvas.redraw();
        }

    }

    @Override
    public void mouseReleased(MouseEvent e) {
        canvas.commitShapes();
    }
}
