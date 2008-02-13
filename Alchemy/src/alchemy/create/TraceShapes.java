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
import alchemy.ui.AlcSubToolBarSection;
import java.awt.Image;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

/**
 * TraceShapes
 * @author Karl D.D. Willis
 */
public class TraceShapes extends AlcModule {

    private BufferedImage flickrBufferedImage;
    private int halfArea = 20;
    private int area = halfArea * 2;
    private int areaArraySize = area * area;
    private int imageW,  imageH,  edgeX,  edgeY;
    private AlcSubToolBarSection subToolBarSection;

    public TraceShapes() {

    }

    public void setup() {

        createSubToolBarSection();
        toolBar.addSubToolBarSection(subToolBarSection);

        loadImage();

    }

    private void loadImage() {
        String random = AlcUtil.zeroPad((int) root.math.random(1000), 4);
        //System.out.println(random);
        Image flickrImage = Flickr.getInstance().search(random);
        //Image flickrImage =  AlcUtil.getImage("data/testImage.jpg", root);
        canvas.setImage(flickrImage);
        canvas.redraw();
        System.out.println("IMAGE LOADED");
        flickrBufferedImage = (BufferedImage) flickrImage;
        imageW = flickrBufferedImage.getWidth();
        imageH = flickrBufferedImage.getHeight();
        edgeX = imageW - halfArea;
        edgeY = imageH - halfArea;
    }

    protected void reselect() {
        // Add this modules toolbar to the main ui
        toolBar.addSubToolBarSection(subToolBarSection);
    }

    public void createSubToolBarSection() {
        subToolBarSection = new AlcSubToolBarSection(this);

        // Run Button
        AlcSubButton runButton = new AlcSubButton("Load Image", AlcUtil.getUrlPath("run.png", getClassLoader()));
        runButton.setToolTipText("Load a new image");
        runButton.addActionListener(
                new ActionListener() {

                    public void actionPerformed(ActionEvent e) {
                        loadImage();
                    }
                });
        subToolBarSection.add(runButton);
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

    private AlcShape makeShape(Point p) {
        // Make a new shape with the globally defined style etc...
        return new AlcShape(p, canvas.getColour(), canvas.getAlpha(), canvas.getStyle(), canvas.getLineWidth());
    }

    private Point checkSnap(Point p) {
        // If inside the image
        if (p.x < edgeX && p.y < edgeY) {
            // The pixel under the cursor
            int xy = flickrBufferedImage.getRGB(p.x, p.y);
            int xyGrey = convertToGrey(xy);
            //System.out.println(xyGrey);

            int startX = p.x - halfArea;
            int startY = p.y - halfArea;

            int[] rgbs = new int[areaArraySize];
            flickrBufferedImage.getRGB(startX, startY, area, area, rgbs, 0, area);

            for (int i = 0; i < rgbs.length; i++) {
                int pix = convertToGrey(rgbs[i]);
                if (Math.abs(pix - xyGrey) > 100) {
                    //System.out.println(i);
                    return new Point(startX + (i % area), startY + (i / area));
                }

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
}
