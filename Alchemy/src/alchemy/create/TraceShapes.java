/*
 * This file is part of the Alchemy project - http://al.chemy.org
 * 
 * Copyright (c) 2007 Karl D.D. Willis
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
package alchemy.create;

import alchemy.*;
import alchemy.ui.AlcSubButton;
import alchemy.ui.AlcSubSlider;
import alchemy.ui.AlcSubToggleButton;
import alchemy.ui.AlcSubToolBarSection;
import java.awt.Image;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import foxtrot.*;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * TraceShapes
 * @author Karl D.D. Willis
 */
public class TraceShapes extends AlcModule {

    private int halfArea = 30;
    private int tolerance = 100;
    private int imageW,  imageH;
    private AlcSubToolBarSection subToolBarSection;
    private int[] pixels;
    private boolean pixelsLoaded = false;

    public TraceShapes() {

    }

    protected void setup() {
        canvas.setDisplayImage(false);
        createSubToolBarSection();
        toolBar.addSubToolBarSection(subToolBarSection);
        loadImage();
    }

    protected void reselect() {
        // Add this modules toolbar to the main ui
        toolBar.addSubToolBarSection(subToolBarSection);
        loadImage();
    }

    protected void deselect() {
        canvas.clearImage();
        canvas.redraw();
    }

    private void loadImage() {

        final String random = AlcUtil.zeroPad((int) root.math.random(10000), 5);
        //System.out.println(random);
        Image flickrImage = null;
        pixelsLoaded = false;

        try {
            flickrImage = (Image) Worker.post(new Task() {

                public Object run() throws Exception {
                    return Flickr.getInstance().search(random);
                }
            });
        } catch (Exception ignored) {
        }


        if (flickrImage != null) {
            BufferedImage flickrBufferedImage = (BufferedImage) flickrImage;

            // Scale the image to the screen size
            imageW = root.getWindowSize().width;
            imageH = root.getWindowSize().height;
            BufferedImage scaledImage = new BufferedImage(imageW, imageH, BufferedImage.TYPE_INT_RGB);
            Graphics2D g = scaledImage.createGraphics();
            AffineTransform scaleTransform = AffineTransform.getScaleInstance(
                    (double) imageW / flickrBufferedImage.getWidth(),
                    (double) imageH / flickrBufferedImage.getHeight());
            g.drawRenderedImage(flickrBufferedImage, scaleTransform);

            // Load the pixels into an array
            pixels = new int[imageW * imageH];
            //int[] rgbs = new int[areaArraySize];
            scaledImage.getRGB(0, 0, imageW, imageH, pixels, 0, imageW);
            // Then convert them all to grey for easy access
            for (int i = 0; i < pixels.length; i++) {
                pixels[i] = convertToGrey(pixels[i]);
            }
            pixelsLoaded = true;

            canvas.setImage(scaledImage);
            canvas.redraw();
        }

    }

    private AlcShape makeShape(Point p) {
        // Make a new shape with the globally defined style etc...
        return new AlcShape(p, canvas.getColour(), canvas.getAlpha(), canvas.getStyle(), canvas.getLineWidth());
    }

    private Point checkSnap(Point p) {
        if (pixelsLoaded && p.x < imageW && p.y < imageH) {
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
            if (endX > imageW) {
                endX = imageW;
            }
            int endY = p.y + halfArea;
            if (endY > imageH) {
                endY = imageH;
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

    private int convertToGrey(int rgb) {
        int r = (rgb >> 16) & 0xff;
        int g = (rgb >> 8) & 0xff;
        int b = rgb & 0xff;
        return (r + g + b) / 3;
    }

    private int getPixel(int x, int y) {
        return pixels[y * imageW + x];
    }

    public void createSubToolBarSection() {
        subToolBarSection = new AlcSubToolBarSection(this);

        // Image Display Button
        final AlcSubToggleButton imageDisplayButton = new AlcSubToggleButton("Display Image", AlcUtil.getUrlPath("imagedisplay.png", getClassLoader()));
        imageDisplayButton.setToolTipText("Turn image display on or off");
        imageDisplayButton.addActionListener(
                new ActionListener() {

                    public void actionPerformed(ActionEvent e) {
                        canvas.setDisplayImage(imageDisplayButton.isSelected());
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
        AlcSubSlider snapSlider = new AlcSubSlider("Snap Distance", 1, 100, halfArea);
        snapSlider.setToolTipText("Adjust the snap distance");
        snapSlider.slider.addChangeListener(
                new ChangeListener() {

                    public void stateChanged(ChangeEvent e) {
                        JSlider source = (JSlider) e.getSource();
                        if (!source.getValueIsAdjusting()) {
                            halfArea = source.getValue();

                        }
                    }
                });
        subToolBarSection.add(snapSlider);

        // Tolerance Slider
        AlcSubSlider toleranceSlider = new AlcSubSlider("Tolerance", 0, 200, tolerance);
        toleranceSlider.setToolTipText("Adjust the snapping tolerance");
        toleranceSlider.slider.addChangeListener(
                new ChangeListener() {

                    public void stateChanged(ChangeEvent e) {
                        JSlider source = (JSlider) e.getSource();
                        if (!source.getValueIsAdjusting()) {
                            tolerance = source.getValue();

                        }
                    }
                });
        subToolBarSection.add(toleranceSlider);
    }

    public void mousePressed(MouseEvent e) {
        Point p = e.getPoint();
        canvas.createShapes.add(makeShape(p));
        canvas.redraw();
    }

    public void mouseDragged(MouseEvent e) {

        // Need to test if it is null incase the shape has been auto-cleared
        if (canvas.getCurrentCreateShape() != null) {
            Point p = e.getPoint();
            canvas.getCurrentCreateShape().addCurvePoint(checkSnap(p));
            canvas.redraw();
        }

    }

    public void mouseReleased(MouseEvent e) {
        canvas.commitShapes();
    }
}
