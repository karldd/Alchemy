/*
 * This file is part of the Alchemy project - http://al.chemy.org
 * 
 * Copyright (c) 2008 Karl D.D. Willis
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

import java.awt.*;
import java.awt.event.*;
import javax.swing.JComponent;

/**
 * AlcSliderCustom
 * @author Karl D.D. Willis
 */
public class AlcSliderCustom extends JComponent implements MouseListener, MouseMotionListener {

    private int width = 75;
    private int height = 15;
    private int widthMinusOne = width - 1;
    private int heightMinusOne = height - 1;
    private int min,  max;
    private float step,  stepValue;
    private final Color bg = new Color(228, 228, 228);
    private final Color line = new Color(140, 140, 140);

    public AlcSliderCustom(int min, int max, int value) {
        this.min = min;
        this.max = max;
        //this.value = value;
        addMouseListener(this);
        addMouseMotionListener(this);
        this.setOpaque(false);
        this.setPreferredSize(new Dimension(width, height));
        step = (max - min) / width;
        stepValue = step * value;
    }

    @Override
    public void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setColor(bg);
        g2.fillRect(0, 0, width, height);
        g2.setColor(line);
        g2.drawRect(0, 0, widthMinusOne, heightMinusOne);
        g2.setColor(Color.BLACK);
        g2.drawLine((int) stepValue, 0, (int) stepValue, height);
    }

    private void moveSlider(int x) {
        if (x >= 0 && x < width) {
            stepValue = step * x;
            this.repaint();
        }
    }

    public void mouseClicked(MouseEvent e) {
    }

    public void mousePressed(MouseEvent e) {
    }

    public void mouseReleased(MouseEvent e) {
        moveSlider(e.getX());
    }

    public void mouseEntered(MouseEvent e) {
    }

    public void mouseExited(MouseEvent e) {
    }

    public void mouseDragged(MouseEvent e) {
        moveSlider(e.getX());
    }

    public void mouseMoved(MouseEvent e) {
    }
}
