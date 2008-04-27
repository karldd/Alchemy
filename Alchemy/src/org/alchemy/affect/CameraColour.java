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
package org.alchemy.affect;

import java.awt.Color;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import org.alchemy.core.*;

/**
 * CameraColour
 * @author Karl D.D. Willis
 */
public class CameraColour extends AlcModule implements AlcCamInterface {

    private AlcCamera cam;
    private int width = 640;
    private int height = 480;
    private BufferedImage cameraImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
    private boolean displayImage = false;
    private AlcSubToolBarSection subToolBarSection;

    public CameraColour() {

    }

    @Override
    public void setup() {
        cam = new AlcCamera(this, width, height);
        cam.start();

        createSubToolBarSection();
        toolBar.addSubToolBarSection(subToolBarSection);
    }

    @Override
    public void deselect() {
        cam.stop();
        cam = null;
    }

    @Override
    public void reselect() {
        cam = new AlcCamera(this, width, height);
        cam.start();
        toolBar.addSubToolBarSection(subToolBarSection);
    }

    public void createSubToolBarSection() {
        subToolBarSection = new AlcSubToolBarSection(this);

        // Show Camera
        AlcSubButton cameraButton = new AlcSubButton("Display Image", AlcUtil.getUrlPath("imagedisplay.png", getClassLoader()));
        cameraButton.setToolTipText("Display the camera image temporarily");

        cameraButton.addActionListener(
                new ActionListener() {

                    public void actionPerformed(ActionEvent e) {
                        displayImage = true;
                        int x = (canvas.getWidth() - width) >> 1;
                        int y = (canvas.getHeight() - height) >> 1;
                        canvas.setImageLocation(x, y);
                        canvas.setImageDisplay(true);
                    }
                });
        subToolBarSection.add(cameraButton);
    }

    public void cameraEvent() {
        cameraImage.setRGB(0, 0, width, height, cam.pixels, 0, width);
        if (displayImage) {
            canvas.setImage(cameraImage);
            canvas.redraw();
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {
        if (displayImage) {
            displayImage = false;
            canvas.setImageDisplay(false);
            canvas.resetImageLocation();
            canvas.redraw();
        }

        Point p = e.getPoint();

        // Centre the image
        int x = (canvas.getWidth() - width) >> 1;
        int y = (canvas.getHeight() - height) >> 1;
        Rectangle camBounds = new Rectangle(x, y, width, height);

        // If the mouse point is inside the centred image then set the colour
        if (camBounds.contains(p)) {
            int colour = cameraImage.getRGB(p.x - x, p.y - y);
            canvas.setColour(new Color(colour));
        }
    }
    }
