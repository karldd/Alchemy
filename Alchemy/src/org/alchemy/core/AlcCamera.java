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
package org.alchemy.core;

import quicktime.*;
import quicktime.qd.*;
import quicktime.std.*;
import quicktime.std.sg.*;
import quicktime.util.RawEncodedImage;

/**
 * AlcCamera
 * Access to the pixels of the camera.
 * Using Quicktime on a Mac based on the Processing Video library:
 * http://dev.processing.org/source/index.cgi/trunk/processing/video/src/processing/video/Capture.java?view=markup
 * 
 * To make this compilable on both platforms, is it worthwhile creating a Dynamic Proxy Class?
 * http://www.michael-clarke-blog.com/2008/04/03/java-and-c-dynamic-class-loading/
 * http://java.sun.com/j2se/1.3/docs/guide/reflection/proxy.html
 * http://www.javaworld.com/javaworld/jw-11-2000/jw-1110-proxy.html
 * 
 * 
 * @author Karl D.D. Willis
 */
public class AlcCamera {

    /** The parent module calling the camera class */
    private AlcCamInterface parent;
    /** Size of the camera image - default 640x480 */
    private int width = 640;
    private int height = 480;
    /** Length of the pixel array */
    private int pixelCount = width * height;
    /** Array of pixel data */
    public int pixels[] = new int[pixelCount];
    /** Camera refresh rate in millisec - default every half a second */
    private int refreshRate = 500;
    /** Thread getting the camera image */
    private Thread camThread;
    /** Thread running or not */
    private boolean running = false;
    /** The raw camera image */
    private RawEncodedImage raw;
    /** Quicktime sequence grabber */
    private SequenceGrabber capture;
    /** Quicktime video channel */
    private SGVideoChannel channel;
    /** Size of the camera image */
    private QDRect qdrect;

    public AlcCamera(AlcCamInterface parent) {
        this.parent = parent;
    }

    public AlcCamera(AlcCamInterface parent, int refreshRate) {
        this.parent = parent;
        this.refreshRate = refreshRate;
    }

    public AlcCamera(AlcCamInterface parent, int width, int height) {
        this.parent = parent;
        setSize(width, height);
    }

    public AlcCamera(AlcCamInterface parent, int width, int height, int refreshRate) {
        this.parent = parent;
        setSize(width, height);
        this.refreshRate = refreshRate;
    }

    private void setSize(int width, int height) {
        this.width = width;
        this.height = height;
        this.pixelCount = width * height;
        pixels = new int[pixelCount];
    }

    public void start() {

        try {
            QTSession.open();
        } catch (QTException e) {
            e.printStackTrace();
            return;
        }

        try {
            qdrect = new QDRect(width, height);
            // workaround for bug with the intel macs
            QDGraphics qdgraphics = null; //new QDGraphics(qdrect);
            if (quicktime.util.EndianOrder.isNativeLittleEndian()) {
                qdgraphics = new QDGraphics(QDConstants.k32BGRAPixelFormat, qdrect);
            } else {
                qdgraphics = new QDGraphics(QDGraphics.kDefaultPixelFormat, qdrect);
            }

            capture = new SequenceGrabber();
            capture.setGWorld(qdgraphics, null);

            channel = new SGVideoChannel(capture);

//            DigitizerInfo info = channel.getDigitizerComponent().getDigitizerInfo();
//            System.out.println("MIN: " + info.getMinDestWidth() + " " + info.getMinDestHeight());
//            System.out.println("MAX: " + info.getmaxDestWidth() + " " + info.getmaxDestHeight());

            channel.setBounds(qdrect);
            channel.setUsage(2);  // what is this usage number?
            capture.startPreview();  // maybe this comes later?
            running = true;

            PixMap pixmap = qdgraphics.getPixMap();
            raw = pixmap.getPixelData();

            camThread = new Thread() {

                @Override
                public void run() {
                    try {
                        //Loop until running is turned off
                        while (running && (capture != null)) {
                            capture.idle();
                            raw.copyToArray(0, pixels, 0, width * height);

                            // Call the parent to report a new frame
                            parent.cameraEvent();
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    try {
                        Thread.sleep(refreshRate);
                    } catch (InterruptedException e) {
                    }
                }
            };

            camThread.start();

        } catch (QTException qte) {
            qte.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    /** Stops Camera Input */
    public void stop() {
        if (capture != null) {
            try {
                capture.stop(); // stop the "preview"
            } catch (StdQTException e) {
                e.printStackTrace();
            }
            capture = null;
        }
        running = false;
        camThread = null;
        QTSession.close();
    }
}
