/**
 * MicrophoneShapes.java
 *
 * Created on December 6, 2007, 5:39 PM
 *
 * @author  Karl D.D. Willis
 * @version 1.0
 */
package alchemy.create;

import alchemy.*;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

public class MicrophoneShapes extends AlcModule implements AlcConstants {

    AlcMicInput micIn;
    AlcShape tempShape;
    Point oldP;

    /** Creates a new instance of MicrophoneShapes */
    public MicrophoneShapes() {
    }

    @Override
    public void setup() {
        // Create a new MicInput Object with a buffer of 10
        micIn = new AlcMicInput(2);
        micIn.startMicInput();
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

    @Override
    public void mousePressed(MouseEvent e) {
        Point p = e.getPoint();
        canvas.addShape(makeShape(p));
        oldP = p;
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        Point p = e.getPoint();

        byte[] buffer = micIn.getBuffer();


        //Point pt = rightAngle(p, oldP, micIn.getMicLevel());
        Point pt = rightAngle(p, oldP, buffer[0] >> 2);

        // Need to test if it is null incase the shape has been auto-cleared
        if (canvas.getCurrentShape() != null) {
            canvas.getCurrentShape().addCurvePoint(pt);
            canvas.applyAffects();
            oldP = p;
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        Point p = e.getPoint();
        // Need to test if it is null incase the shape has been auto-cleared
        if (canvas.getCurrentShape() != null) {
            canvas.getCurrentShape().addLastPoint(p);
            canvas.applyAffects();
        }
    }

    private Point rightAngle(Point p1, Point p2, double distance) {
        // Calculate the angle between the last point and the new point
        double angle = Math.atan2(p1.x - p2.x, p1.y - p2.y);
        // Conver the polar coordinates to cartesian
        double x = p1.x + (distance * Math.cos(angle));
        double y = p1.y + (distance * Math.sin(angle));

        return new Point((int) x, (int) y);
    }

    private AlcShape makeShape(Point p) {
        // Make a new shape with the globally defined style etc...
        return new AlcShape(p, canvas.getColour(), canvas.getAlpha(), canvas.getStyle(), canvas.getLineWidth());
    }

    // KEY EVENTS
    @Override
    public void keyReleased(KeyEvent e) {
        int keyCode = e.getKeyCode();
        char keyChar = e.getKeyChar();

        //System.out.println(keyChar);
        switch (keyChar) {
            case '[':
                System.out.println("[");


                break;

            case ']':

                System.out.println("]");

                break;
            case 'p':
                System.out.println(micIn.getMicLevel());
                break;
        }
    }
}
