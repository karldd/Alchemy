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

import java.awt.Color;
import java.awt.event.MouseEvent;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.alchemy.core.*;

/**
 *
 * RibbonShapes.java
 * 
 * Based on James Alliban's code
 * http://jamesalliban.wordpress.com/2008/12/04/2d-ribbons/
 *
 * Which is based on Eric Natzke's code
 * http://www.flickr.com/photos/natzke/2343461294/
 * 
 */
public class RibbonShapes extends AlcModule implements AlcConstants {

    private int ribbonAmount = 1;
    private int ribbonParticleAmount = 20;
    private float randomness = 0.2F;
    private RibbonManager ribbonManager;
    //
    private AlcToolBarSubSection subToolBarSection;
    //
    private int spacing = 25;
    private long time;
    // 
    private final int initialGravity = 15;
    private final int initialFriction = 110;
    private final int initialSize = 50;

    private int size = initialSize;
    private float gravity = initialGravity * 0.01F;
    private float friction = initialFriction * 0.01F;
    private int maxDistance = 40;
    private float drag = 2F;
    private float dragFlare = 0.015F;

    public RibbonShapes() {
    }

    @Override
    public void setup() {
        ribbonManager = new RibbonManager(ribbonAmount, ribbonParticleAmount, randomness);
        setupRibbonManager();
//        ribbonManager.setRadiusMax(initialSize);                 // default = 8
//        float divide = (100 - size) / 5F;
//        ribbonManager.setRadiusDivide(divide);              // default = 10
//        ribbonManager.setGravity(gravity);                   // default = .03
//        ribbonManager.setFriction(friction);                  // default = 1.1
//        ribbonManager.setMaxDistance(maxDistance);               // default = 40
//        ribbonManager.setDrag(drag); //1.8                     // default = 2
//        ribbonManager.setDragFlare(dragFlare);                 // default = .008

        createSubToolBarSection();
        toolBar.addSubToolBarSection(subToolBarSection);
    }

    @Override
    protected void reselect() {
        toolBar.addSubToolBarSection(subToolBarSection);
    }

    @Override
    protected void cleared() {
        setupRibbonManager();
    }

    private void createSubToolBarSection() {
        subToolBarSection = new AlcToolBarSubSection(this);
        
        // Size Slider
        final AlcSubSpinner sizeSpinner = new AlcSubSpinner("Size", 1, 100, initialSize, 1 );
        sizeSpinner.setToolTipText("Adjust the ribbon size");
        sizeSpinner.addChangeListener(
                new ChangeListener() {

                    public void stateChanged(ChangeEvent e) {
                        if (!sizeSpinner.getValueIsAdjusting()) {
                            int value = sizeSpinner.getValue();
                            size = value;
                            ribbonManager.setRadiusMax(size);
                            float divide = (100 - value) / 5F;
                            //System.out.println(value + " " + divide);
                            ribbonManager.setRadiusDivide(divide);
                        }
                    }
                });
        subToolBarSection.add(sizeSpinner);
        
        
        // Spacing Slider
        final AlcSubSpinner spacingSpinner = new AlcSubSpinner("Spacing", 1, 100, spacing, 1);
        spacingSpinner.setToolTipText("Adjust the spacing interval");
        spacingSpinner.addChangeListener(
                new ChangeListener() {

                    public void stateChanged(ChangeEvent e) {
                        if (!spacingSpinner.getValueIsAdjusting()) {
                            spacing = spacingSpinner.getValue();
                        }
                    }
                });
        subToolBarSection.add(spacingSpinner);
        


        // Friction Slider
        final AlcSubSlider frictionSlider = new AlcSubSlider("Friction", 1, 200, initialFriction);
        frictionSlider.setToolTipText("Adjust the ribbon friction");
        frictionSlider.addChangeListener(
                new ChangeListener() {

                    public void stateChanged(ChangeEvent e) {
                        if (!frictionSlider.getValueIsAdjusting()) {
                            int value = frictionSlider.getValue();
                            friction = value * 0.01F;
                            ribbonManager.setFriction(friction);
                        }
                    }
                });
        subToolBarSection.add(frictionSlider);

        // Gravity Slider
        final AlcSubSlider gravitySlider = new AlcSubSlider("Gravity", 0, 200, initialGravity);
        gravitySlider.setToolTipText("Adjust the ribbon gravity");
        gravitySlider.addChangeListener(
                new ChangeListener() {

                    public void stateChanged(ChangeEvent e) {
                        if (!gravitySlider.getValueIsAdjusting()) {
                            int value = gravitySlider.getValue();
                            gravity = value * 0.01F;
                            //System.out.println(gravity);
                            ribbonManager.setGravity(gravity);
                        }
                    }
                });
        subToolBarSection.add(gravitySlider);

        // Drag Slider
//        float initialDrag = AlcMath.map(dragFlare, 0.001f, 0.05f, 1, 100);
//        final AlcSubSlider dragSlider = new AlcSubSlider("Drag", 0, 100,(int) initialDrag);
//        dragSlider.setToolTipText("Adjust the ribbon drag");
//        dragSlider.addChangeListener(
//                new ChangeListener() {
//
//                    public void stateChanged(ChangeEvent e) {
//                        if (!dragSlider.getValueIsAdjusting()) {
//                            int value = dragSlider.getValue();
//                            drag = AlcMath.map(value, 1, 100, 0.001f, 0.05f);
//                            ribbonManager.setDrag(drag);
//                        }
//                    }
//                });
//        subToolBarSection.add(dragSlider);


    }

    private void setupRibbonManager() {
        ribbonManager.setRadiusMax(size);
        float divide = (100 - size) / 5F;
        ribbonManager.setRadiusDivide(divide);
        ribbonManager.setFriction(friction);
        ribbonManager.setGravity(gravity);
        ribbonManager.setMaxDistance(maxDistance);
        ribbonManager.setDrag(drag);
        ribbonManager.setDragFlare(dragFlare);
    }


    @Override
    public void mousePressed(MouseEvent e) {
        canvas.createShapes.add(new AlcShape());
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        if (System.currentTimeMillis() - time >= spacing) {
            canvas.commitShapes();
            canvas.createShapes.add(new AlcShape());
            time = System.currentTimeMillis();

        } else {
            ribbonManager.update(e.getX(), e.getY());
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        ribbonManager.init();
        setupRibbonManager();
        //System.out.println(canvas.createShapes.size());
        canvas.commitShapes();
        canvas.redraw();
    }

    class Ribbon {

        int ribbonAmount;
        float randomness;
        int ribbonParticleAmount;         // length of the Particle Array (max number of points)
        int particlesAssigned = 0;        // current amount of particles currently in the Particle array                                
        float radiusMax = 8;              // maximum width of ribbon
        float radiusDivide = 10;          // distance between current and next point / this = radius for first half of the ribbon
        float gravity = 0.03F;              // gravity applied to each particle
        float friction = 1.1F;             // friction applied to the gravity of each particle
        int maxDistance = 40;             // if the distance between particles is larger than this the drag comes into effect
        float drag = 2;                   // if distance goes above maxDistance - the points begin to grag. high numbers = less drag
        float dragFlare = 0.008F;           // degree to which the drag makes the ribbon flare out
        RibbonParticle[] particles;       // particle array
        Color ribbonColor;

        Ribbon(int ribbonParticleAmount, Color ribbonColor, float randomness) {
            this.ribbonParticleAmount = ribbonParticleAmount;
            this.ribbonColor = ribbonColor;
            this.randomness = randomness;
            init();
        }

        void init() {
            particles = new RibbonParticle[ribbonParticleAmount];
        }

        void update(float randX, float randY) {
            addParticle(randX, randY);
            drawCurve();
        }

        void addParticle(float randX, float randY) {
            // If all particle slots full
            if (particlesAssigned == ribbonParticleAmount) {
                // Shift the particles over
                for (int i = 1; i < ribbonParticleAmount; i++) {
                    particles[i - 1] = particles[i];
                }
                // Add the new particle to the last slot
                particles[ribbonParticleAmount - 1] = new RibbonParticle(randomness, this);
                particles[ribbonParticleAmount - 1].px = randX;
                particles[ribbonParticleAmount - 1].py = randY;
                return;

            // If the particle slots are not yet full
            } else {
                // Add a particle to the end of the array
                particles[particlesAssigned] = new RibbonParticle(randomness, this);
                particles[particlesAssigned].px = randX;
                particles[particlesAssigned].py = randY;
                ++particlesAssigned;
            }
            if (particlesAssigned > ribbonParticleAmount) {
                ++particlesAssigned;
            }
        }

        void drawCurve() {

            for (int i = 1; i < particlesAssigned - 1; i++) {
                RibbonParticle p = particles[i];
                p.calculateParticles(particles[i - 1], particles[i + 1], ribbonParticleAmount, i);
            }

            if (particlesAssigned > 1) {

                AlcShape shape = canvas.getCurrentCreateShape();
                //GeneralPath path = shape.getPath();
                GeneralPath path = new GeneralPath();
                ArrayList<Point2D.Float> spine = new ArrayList<Point2D.Float>(particles.length - 4);



                RibbonParticle p0 = particles[1];
                path.moveTo(p0.lcx2, p0.lcy2);
                spine.add(new Point2D.Float(p0.px, p0.py));

                for (int i = 2; i < particlesAssigned - 4; i++) {

                    RibbonParticle p = particles[i];
                    spine.add(new Point2D.Float(p.px, p.py));
                    path.curveTo(p.leftPX, p.leftPY, p.lcx2, p.lcy2, p.lcx2, p.lcy2);
                //path.lineTo(p.leftPX, p.leftPY);

                }

                for (int i = particlesAssigned - 4; i > 1; i--) {

                    RibbonParticle p = particles[i];
                    RibbonParticle pm1 = particles[i - 1];
                    spine.add(new Point2D.Float(p.px, p.py));
                    path.curveTo(p.rightPX, p.rightPY, pm1.rcx2, pm1.rcy2, pm1.rcx2, pm1.rcy2);
                //path.lineTo(p.rightPX, p.rightPY);

                }

                path.closePath();
                shape.setPath(path);
                canvas.redraw();
            }

//            for (int i = particlesAssigned - 4; i > 1; i--) {
//                RibbonParticle p = particles[i];
//                RibbonParticle pm1 = particles[i - 1];
//
//                // System.out.println((i - 1) + " : " + pm1.rcx2 + " " + pm1.rcy2 + " " + pm1.lcx2 + "" + pm1.lcy2);
//                System.out.println(i + " " + (i - 1));
//                
//                AlcShape shape = canvas.createShapes.get(i-1);
//                GeneralPath path = new GeneralPath();
//
//                path.moveTo(p.lcx2, p.lcy2);
//                path.curveTo(p.leftPX, p.leftPY, pm1.lcx2, pm1.lcy2, pm1.lcx2, pm1.lcy2);
//                path.lineTo(pm1.rcx2, pm1.rcy2);
//                path.curveTo(p.rightPX, p.rightPY, p.rcx2, p.rcy2, p.rcx2, p.rcy2);
//                //path.lineTo(p.lcx2, p.lcy2);
//                //path.closePath();
//                shape.setPath(path);
//            }

        }
    }

    class RibbonParticle {

        float px, py;                                       // x and y position of particle (this is the bexier point)
        float xSpeed, ySpeed = 0;                           // speed of the x and y positions
        float cx1, cy1, cx2, cy2;                           // the avarage x and y positions between px and py and the points of the surrounding Particles
        float leftPX, leftPY, rightPX, rightPY;             // the x and y points of that determine the thickness of this segment
        float lpx, lpy, rpx, rpy;                           // the x and y points of the outer bezier points
        float lcx1, lcy1, lcx2, lcy2;                       // the avarage x and y positions between leftPX and leftPX and the left points of the surrounding Particles
        float rcx1, rcy1, rcx2, rcy2; // the avarage x and y positions between rightPX and rightPX and the right points of the surrounding Particles
        float radius;                                       // thickness of current particle
        float randomness;
        Ribbon ribbon;

        RibbonParticle(float randomness, Ribbon ribbon) {
            this.randomness = randomness;
            this.ribbon = ribbon;
        }

        void calculateParticles(RibbonParticle pMinus1, RibbonParticle pPlus1, int particleMax, int i) {
            float div = 2;
            cx1 = (pMinus1.px + px) / div;
            cy1 = (pMinus1.py + py) / div;
            cx2 = (pPlus1.px + px) / div;
            cy2 = (pPlus1.py + py) / div;

            // calculate radians (direction of next point)
            float dx = cx2 - cx1;
            float dy = cy2 - cy1;

            float pRadians = (float) Math.atan2(dy, dx);

            float distance = (float) Math.sqrt(dx * dx + dy * dy);

            if (distance > ribbon.maxDistance) {
                float oldX = px;
                float oldY = py;
                px = (float) (px + ((ribbon.maxDistance / ribbon.drag) * Math.cos(pRadians)));
                py = (float) (py + ((ribbon.maxDistance / ribbon.drag) * Math.sin(pRadians)));
                xSpeed += (px - oldX) * ribbon.dragFlare;
                ySpeed += (py - oldY) * ribbon.dragFlare;
            }

            ySpeed += ribbon.gravity;
            xSpeed *= ribbon.friction;
            ySpeed *= ribbon.friction;
            px += xSpeed + math.random(0.3F);
            py += ySpeed + math.random(0.3F);

            float randX = ((randomness / 2) - math.random(randomness)) * distance;
            float randY = ((randomness / 2) - math.random(randomness)) * distance;
            px += randX;
            py += randY;

            //float radius = distance / 2;
            //if (radius > radiusMax) radius = ribbon.radiusMax;

            if (i > particleMax / 2) {
                radius = distance / ribbon.radiusDivide;
            } else {
                radius = pPlus1.radius * 0.9F;
            }

            if (radius > ribbon.radiusMax) {
                radius = ribbon.radiusMax;
            }
            if (i == particleMax - 2 || i == 1) {
                if (radius > 1) {
                    radius = 1;
                }
            }

            // calculate the positions of the particles relating to thickness
            leftPX = (float) (px + Math.cos(pRadians + (MATH_HALF_PI * 3)) * radius);
            leftPY = (float) (py + Math.sin(pRadians + (MATH_HALF_PI * 3)) * radius);
            rightPX = (float) (px + Math.cos(pRadians + MATH_HALF_PI) * radius);
            rightPY = (float) (py + Math.sin(pRadians + MATH_HALF_PI) * radius);

            // left and right points of current particle
            lpx = (pMinus1.lpx + lpx) / div;
            lpy = (pMinus1.lpy + lpy) / div;
            rpx = (pPlus1.rpx + rpx) / div;
            rpy = (pPlus1.rpy + rpy) / div;

            // left and right points of previous particle
            lcx1 = (pMinus1.leftPX + leftPX) / div;
            lcy1 = (pMinus1.leftPY + leftPY) / div;
            rcx1 = (pMinus1.rightPX + rightPX) / div;
            rcy1 = (pMinus1.rightPY + rightPY) / div;

            // left and right points of next particle
            lcx2 = (pPlus1.leftPX + leftPX) / div;
            lcy2 = (pPlus1.leftPY + leftPY) / div;
            rcx2 = (pPlus1.rightPX + rightPX) / div;
            rcy2 = (pPlus1.rightPY + rightPY) / div;
        }
    }

    class RibbonManager {

        int ribbonAmount;
        int ribbonParticleAmount;
        float randomness;
        String imgName;
        Ribbon[] ribbons;       // ribbon array

        RibbonManager(int ribbonAmount, int ribbonParticleAmount, float randomness) {
            this.ribbonAmount = ribbonAmount;
            this.ribbonParticleAmount = ribbonParticleAmount;
            this.randomness = randomness;
            init();
        }

        void init() {
            addRibbon();
        }

        void addRibbon() {
            ribbons = new Ribbon[ribbonAmount];
            for (int i = 0; i < ribbonAmount; i++) {
                int r = (int) math.random(0, 255);
                int g = (int) math.random(0, 255);
                int b = (int) math.random(0, 255);
                Color randomColor = new Color(r, g, b);
                ribbons[i] = new Ribbon(ribbonParticleAmount, randomColor, randomness);
            }
        }

        void update(int currX, int currY) {
            for (int i = 0; i < ribbonAmount; i++) {
                //float randX = currX + (randomness / 2) - random(randomness);
                //float randY = currY + (randomness / 2) - random(randomness);

                float randX = currX;
                float randY = currY;

                ribbons[i].update(randX, randY);
            }
        }

        void setRadiusMax(float value) {
            for (int i = 0; i < ribbonAmount; i++) {
                ribbons[i].radiusMax = value;
            }
        }

        void setRadiusDivide(float value) {
            for (int i = 0; i < ribbonAmount; i++) {
                ribbons[i].radiusDivide = value;
            }
        }

        void setGravity(float value) {
            for (int i = 0; i < ribbonAmount; i++) {
                ribbons[i].gravity = value;
            }
        }

        void setFriction(float value) {
            for (int i = 0; i < ribbonAmount; i++) {
                ribbons[i].friction = value;
            }
        }

        void setMaxDistance(int value) {
            for (int i = 0; i < ribbonAmount; i++) {
                ribbons[i].maxDistance = value;
            }
        }

        void setDrag(float value) {
            for (int i = 0; i < ribbonAmount; i++) {
                ribbons[i].drag = value;
            }
        }

        void setDragFlare(float value) {
            for (int i = 0; i < ribbonAmount; i++) {
                ribbons[i].dragFlare = value;
            }
        }
    }
}
