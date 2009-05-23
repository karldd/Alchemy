/*
 * This file is part of the Alchemy project - http://al.chemy.org
 * 
 * Copyright (c) 2007-2009 Karl D.D. Willis
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
import org.alchemy.core.*;

/**
 *
 * RibbonShapes.java
 */
public class RibbonShapes extends AlcModule implements AlcConstants {

    private boolean TESTING = false;
    private int ribbonAmount = 1;
    private int ribbonParticleAmount = 20;
    private float randomness = 0.2F;
    private RibbonManager ribbonManager;
    //
    private long delayGap = 50;
    private long delayTime;

    public RibbonShapes() {
    }

    @Override
    public void setup() {
        ribbonManager = new RibbonManager(ribbonAmount, ribbonParticleAmount, randomness);
        ribbonManager.setRadiusMax(12);                 // default = 8
        ribbonManager.setRadiusDivide(10);              // default = 10
        ribbonManager.setGravity(0.07F);                   // default = .03
        ribbonManager.setFriction(1.1F);                  // default = 1.1
        ribbonManager.setMaxDistance(40);               // default = 40
        ribbonManager.setDrag(1.8F);                      // default = 2
        ribbonManager.setDragFlare(0.015F);                 // default = .008
    }

    @Override
    public void mousePressed(MouseEvent e) {
        addShapes();
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        if (System.currentTimeMillis() - delayTime >= delayGap) {
            canvas.commitShapes();
            addShapes();
            delayTime = System.currentTimeMillis();

        } else {
            ribbonManager.update(e.getX(), e.getY());
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        //System.out.println(canvas.createShapes.size());
        canvas.commitShapes();
        canvas.redraw();
    }

    private void addShapes() {
        for (int i = 0; i < ribbonParticleAmount - 4; i++) {
            AlcShape shape = new AlcShape();
            canvas.createShapes.add(shape);
        }
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

            for (int i = particlesAssigned - 4; i > 1; i--) {
                RibbonParticle p = particles[i];
                RibbonParticle pm1 = particles[i - 1];

                // System.out.println((i - 1) + " : " + pm1.rcx2 + " " + pm1.rcy2 + " " + pm1.lcx2 + "" + pm1.lcy2);
                System.out.println(i + " " + (i - 1));
                
                AlcShape shape = canvas.createShapes.get(i-1);
                GeneralPath path = new GeneralPath();

                path.moveTo(p.lcx2, p.lcy2);
                path.curveTo(p.leftPX, p.leftPY, pm1.lcx2, pm1.lcy2, pm1.lcx2, pm1.lcy2);
                path.lineTo(pm1.rcx2, pm1.rcy2);
                path.curveTo(p.rightPX, p.rightPY, p.rcx2, p.rcy2, p.rcx2, p.rcy2);
                //path.lineTo(p.lcx2, p.lcy2);
                //path.closePath();
                shape.setPath(path);
            }
            canvas.redraw();
        }
    }

    class RibbonParticle {

        float px,  py;                                       // x and y position of particle (this is the bexier point)
        float xSpeed,  ySpeed = 0;                           // speed of the x and y positions
        float cx1,  cy1,  cx2,  cy2;                           // the avarage x and y positions between px and py and the points of the surrounding Particles
        float leftPX,  leftPY,  rightPX,  rightPY;             // the x and y points of that determine the thickness of this segment
        float lpx,  lpy,  rpx,  rpy;                           // the x and y points of the outer bezier points
        float lcx1,  lcy1,  lcx2,  lcy2;                       // the avarage x and y positions between leftPX and leftPX and the left points of the surrounding Particles
        float rcx1,  rcy1,  rcx2,  rcy2;                       // the avarage x and y positions between rightPX and rightPX and the right points of the surrounding Particles
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
                Color randomColour = new Color(r, g, b);
                ribbons[i] = new Ribbon(ribbonParticleAmount, randomColour, randomness);
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
