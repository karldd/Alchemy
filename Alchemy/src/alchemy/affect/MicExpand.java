/*
 *   Part of the Alchemy project - http://al.chemy.org
 * 
 *   Copyright (c) 2007 Karl D.D. Willis
 * 
 *   This library is free software; you can redistribute it and/or
 *   modify it under the terms of the GNU Lesser General Public
 *   License as published by the Free Software Foundation; either
 *   version 2.1 of the License, or (at your option) any later version.
 * 
 *   This library is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *   Lesser General Public License for more details.
 * 
 *   You should have received a copy of the GNU Lesser General
 *   Public License along with this library; if not, write to the
 *   Free Software Foundation, Inc., 59 Temple Place, Suite 330,
 *   Boston, MA  02111-1307  USA
 */
package alchemy.affect;

import alchemy.*;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.geom.GeneralPath;
import java.awt.geom.PathIterator;

/**
 * MicExpand
 * 
 * 
 */
public class MicExpand extends AlcModule implements AlcMicInterface {

    AlcMicInput micIn;

    public MicExpand() {

    }

    @Override
    public void setup() {
    }

    @Override
    public void deselect() {
        micIn.stopMicInput();
        micIn = null;
    }

    @Override
    public void reselect() {
        setup();
    }

    private void captureSound(AlcShape shape) {
        int totalPoints = shape.getTotalPoints();
        // TotalPoints has to be an even number
        if (totalPoints % 2 != 0) {
            totalPoints -= 1;
        }
        // Create a new MicInput Object with a buffer equal to the number of points
        micIn = new AlcMicInput(this, totalPoints);
        micIn.startMicInput();
        System.out.println("Total Points :" + totalPoints);
    // 
    }

    private GeneralPath randomise(GeneralPath shape) {

        GeneralPath newShape = new GeneralPath();
        PathIterator path = shape.getPathIterator(null);
        float[] pathPts = new float[6];
        int pathType;
        int pathCount = 0;

        while (!path.isDone()) {
            pathType = path.currentSegment(pathPts);

            switch (pathType) {
                case PathIterator.SEG_MOVETO:
                    newShape.moveTo(pathPts[0], pathPts[1]);
                    break;
                case PathIterator.SEG_LINETO:
                    newShape.lineTo(mess(pathPts[0]), mess(pathPts[1]));
                    //newShape.lineTo(pathPts[0], pathPts[1]);
                    break;
                case PathIterator.SEG_QUADTO:
                    newShape.quadTo(mess(pathPts[0]), mess(pathPts[1]), mess(pathPts[2]), mess(pathPts[3]));
                    //newShape.quadTo(pathPts[0], pathPts[1], pathPts[2], pathPts[3]);
                    break;
                case PathIterator.SEG_CUBICTO:
                    // Randomising the curves tends to generate errors and unresposiveness
                    newShape.curveTo(mess(pathPts[0]), mess(pathPts[1]), mess(pathPts[2]), mess(pathPts[3]), mess(pathPts[4]), mess(pathPts[5]));
                    //newShape.curveTo(pathPts[0], pathPts[1], pathPts[2], pathPts[3], pathPts[4], pathPts[5]);
                    break;
                case PathIterator.SEG_CLOSE:
                    newShape.closePath();
                    break;
            }
            pathCount ++;
            path.next();
        }

        return newShape;

    }
    
    private Point expandPoint(Point pt){
        
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        AlcShape currentShape = root.canvas.getCurrentShape();
        if (currentShape != null) {
            captureSound(currentShape);
        }
    }

    public void bufferFull() {
        deselect();
        System.out.println("Buffer Full");
    }
}
