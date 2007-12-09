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

import alchemy.AlcModule;
import alchemy.AlcShape;
import java.awt.event.MouseEvent;
import java.awt.geom.GeneralPath;
import java.awt.geom.PathIterator;

/**
 * Random
 * 
 * 
 */
public class Random extends AlcModule {

    private float noisiness;
    private float noiseScale = 0.0F;
    private int doubleScale,  scale;

    public Random() {

    }

    @Override
    public void setup() {

    }

    @Override
    public void deselect() {

    }

    @Override
    public void reselect() {

    }

    public AlcShape randomiseShape(AlcShape shape) {
        noisiness = root.math.random(-0.01F, 0.1F);

        GeneralPath randomisedShape = randomise(shape.getShape());

        // Make a new shape from the random shape
        return new AlcShape(randomisedShape, shape.getColour(), shape.getAlpha(), shape.getStyle(), shape.getLineWidth());

    }

    private GeneralPath randomise(GeneralPath shape) {

        GeneralPath newShape = new GeneralPath();
        PathIterator cut = shape.getPathIterator(null);
        float[] cutPts = new float[6];
        int cutType;

        while (!cut.isDone()) {
            cutType = cut.currentSegment(cutPts);

            switch (cutType) {
                case PathIterator.SEG_MOVETO:
                    newShape.moveTo(cutPts[0], cutPts[1]);
                    break;
                case PathIterator.SEG_LINETO:
                    newShape.lineTo(mess(cutPts[0]), mess(cutPts[1]));
                    //newShape.lineTo(cutPts[0], cutPts[1]);
                    break;
                case PathIterator.SEG_QUADTO:
                    newShape.quadTo(mess(cutPts[0]), mess(cutPts[1]), mess(cutPts[2]), mess(cutPts[3]));
                    //newShape.quadTo(cutPts[0], cutPts[1], cutPts[2], cutPts[3]);
                    break;
                case PathIterator.SEG_CUBICTO:
                    // Randomising the curves tends to generate errors and unresposiveness
                    newShape.curveTo(mess(cutPts[0]), mess(cutPts[1]), mess(cutPts[2]), mess(cutPts[3]), mess(cutPts[4]), mess(cutPts[5]));
                    //newShape.curveTo(cutPts[0], cutPts[1], cutPts[2], cutPts[3], cutPts[4], cutPts[5]);
                    break;
                case PathIterator.SEG_CLOSE:
                    newShape.closePath();
                    break;
            }
            cut.next();
        }

        return newShape;

    }

    public float mess(float f) {
        noiseScale += 0.1F;
        // TODO - make this settable
        float n = (root.math.noise(noiseScale) * 100F) - 50F;
        //n = n * 0.5F;
        //System.out.println(n);
        return n + f;
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if (root.canvas.getCurrentShape() != null) {
            canvas.setCurrentShape(randomiseShape(root.canvas.getCurrentShape()));
        }
    }
}

