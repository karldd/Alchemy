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
package org.alchemy.affect;

import java.awt.event.MouseEvent;
import java.awt.geom.GeneralPath;
import java.awt.geom.PathIterator;
import java.beans.*;
import java.io.*;
import org.alchemy.core.*;

/**
 *
 * ShapeSwap.java
 */
public class ShapeSwap extends AlcModule {

    private int minPoints = 25;
    private boolean mouseDown = false;

    public ShapeSwap() {
    }

    @Override
    protected void setup() {
    }

    @Override
    protected void reselect() {
    }

    @Override
    protected void cleared() {

        try {
            // Create file input stream.
            FileInputStream fstream = new FileInputStream("foo.xml");

            try {
                // Create XML decoder.
                XMLDecoder istream = new XMLDecoder(fstream);

                try {
                    // Read object.
                    Object obj = istream.readObject();
                    if(obj instanceof AlcShape){
                        AlcShape shape = (AlcShape)obj;
                        canvas.createShapes.add(shape);
                        System.out.println(shape.getColor());
                        System.out.println(shape.getAlpha());
                    }
                    
                    //System.out.println(obj);
                } finally {
                    // Close object stream.
                    istream.close();
                }
            } finally {
                // Close file stream.
                fstream.close();
            }
        } catch (Exception ex) {
            System.out.println(ex);
        }


    }

    @Override
    protected void affect() {
    }

    @Override
    public void mousePressed(MouseEvent e) {
        mouseDown = true;
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        mouseDown = false;

        AlcShape currentShape = canvas.getCurrentShape();

        if (currentShape != null) {

           // Only add shape that have more than a certain number of points
            if (currentShape.getTotalPoints() > minPoints) {

                try {

                    // Create file output stream.
                    FileOutputStream fstream = new FileOutputStream("foo.xml");

                    try {
                        // Create XML encoder.
                        XMLEncoder encoder = new XMLEncoder(fstream);

                        try {
                            // TODO - General Path is not Serialised!
                            // Use this? http://java.sun.com/products/jfc/tsc/articles/persistence4/
                            // http://java.sys-con.com/node/37550
                            // e.setPersistenceDelegate(GeneralPath.class, new GeneralPathDelegate());
                            // or http://forums.sun.com/thread.jspa?threadID=144581&forumID=20

                            encoder.setPersistenceDelegate(AlcShape.class, new DefaultPersistenceDelegate(new String[]{"path", "color", "alpha", "style", "lineWidth"}));
                            encoder.setPersistenceDelegate(GeneralPath.class, new GeneralPathDelegate());
                            //(Point p, Color color, int alpha, int style, float lineWidth) {

                            System.out.println("Writing XML");
                            // Write object.
                            encoder.writeObject(currentShape);
                            encoder.flush();
                        } finally {
                            // Close object stream.
                            encoder.close();
                        }
                    } finally {
                        // Close file stream.
                        fstream.close();
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }

            }
        }
    }

    public static final class GeneralPathDelegate extends PersistenceDelegate {

        protected Expression instantiate(Object oldInstance, Encoder out) {
            return new Expression(oldInstance, GeneralPath.class, "new", new Object[0]);
        }

        @Override
        protected void initialize(Class<?> type, Object oldInstance, Object newInstance, Encoder out) {
            GeneralPath a = (GeneralPath) oldInstance;
            PathIterator itr = a.getPathIterator(null);
            out.writeStatement(new Statement(a, "setWindingRule", new Object[]{a.getWindingRule()}));

            while (!itr.isDone()) {
                float[] segment = new float[6];
                int pathType = itr.currentSegment(segment);

                switch (pathType) {
                    case PathIterator.SEG_MOVETO:
                        out.writeStatement(new Statement(a, "moveTo", new Object[]{segment[0], segment[1]}));
                        break;
                    case PathIterator.SEG_LINETO:
                        out.writeStatement(new Statement(a, "lineTo", new Object[]{segment[0], segment[1]}));
                        break;
                    case PathIterator.SEG_QUADTO:
                        out.writeStatement(new Statement(a, "quadTo", new Object[]{segment[0], segment[1], segment[2], segment[3]}));
                        break;
                    case PathIterator.SEG_CUBICTO:
                        out.writeStatement(new Statement(a, "curveTo", new Object[]{segment[0], segment[1], segment[2], segment[3], segment[4], segment[5]}));
                        break;
                    case PathIterator.SEG_CLOSE:
                        out.writeStatement(new Statement(a, "closePath", new Object[0]));
                        break;
                }
                itr.next();
            }
        }
    }
}


