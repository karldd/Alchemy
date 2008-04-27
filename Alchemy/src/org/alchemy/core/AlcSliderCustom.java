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

import java.awt.*;
import java.awt.event.*;
import javax.swing.JComponent;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * AlcSliderCustom
 * @author Karl D.D. Willis
 */
class AlcSliderCustom extends JComponent implements MouseListener, MouseMotionListener, KeyListener {

    private int width = 75;
    private int height = 15;
    private int widthMinusOne = width - 1;
    private int heightMinusOne = height - 1;
    /** Minimum / Maximum / Display Position of the slider */
    private int min,  max,  displayValue;
    /** Actual Value */
    int trueValue;
    boolean mouseDown;
    /** Size of one step */
    private float step;
    private float scale;
    private final Color outline = new Color(140, 140, 140);
    private Color bg = new Color(228, 228, 228);
    private Color line = Color.GRAY;
    /** The ChangeEvent that is passed to all listeners of this slider. */
    protected transient ChangeEvent changeEvent;

    AlcSliderCustom(int min, int max, int initialSliderValue) {
        this.min = min;
        this.max = max;
        addMouseListener(this);
        addMouseMotionListener(this);
        addKeyListener(this);
        //addFocusListener(this);
        this.setOpaque(false);
        this.setPreferredSize(new Dimension(width, height));
        // To be sure to reach both the min and max use widthMinusOne
        scale = (max - min);
        step = scale / (float) widthMinusOne;
        setSlider(initialSliderValue);
    }

    @Override
    public void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setColor(bg);
        g2.fillRect(0, 0, width, height);
        g2.setColor(outline);
        g2.drawRect(0, 0, widthMinusOne, heightMinusOne);
        g2.setColor(line);
        g2.drawLine((int) displayValue, 0, (int) displayValue, height);
    }

    private void moveSlider(int x) {
        if (x >= 0 && x < width) {
            displayValue = x;
            trueValue = Math.round(step * x);
            this.repaint();
        }
    }

    private void setSlider(int value) {
        if (value >= min && value < max) {
            trueValue = value;
            displayValue = Math.round((width / scale) * value);
            this.repaint();
        }
    }

    /**
     * This method registers a listener to this slider. The listener will be
     * informed of new ChangeEvents.
     *
     * @param listener The listener to register.
     */
    void addChangeListener(ChangeListener listener) {
        listenerList.add(ChangeListener.class, listener);
    }

    /**
     * This method removes a listener from this slider.
     *
     * @param listener The listener to remove.
     */
    void removeChangeListener(ChangeListener listener) {
        listenerList.remove(ChangeListener.class, listener);
    }

    /**
     * This method is called whenever the model fires a ChangeEvent. It should
     * propagate the ChangeEvent to its listeners with a new ChangeEvent that
     * identifies the slider as the source.
     */
    void fireStateChanged() {
        Object[] changeListeners = listenerList.getListenerList();
        if (changeEvent == null) {
            changeEvent = new ChangeEvent(this);
        }
        for (int i = changeListeners.length - 2; i >= 0; i -= 2) {
            if (changeListeners[i] == ChangeListener.class) {
                ((ChangeListener) changeListeners[i + 1]).stateChanged(changeEvent);
            }
        }
    }

    public void mouseClicked(MouseEvent e) {
    }

    public void mousePressed(MouseEvent e) {
        mouseDown = true;
        this.requestFocus();
    }

    public void mouseReleased(MouseEvent e) {
        moveSlider(e.getX());
        mouseDown = false;
        fireStateChanged();
    }

    public void mouseEntered(MouseEvent e) {
        line = Color.BLACK;
        this.repaint();
    }

    public void mouseExited(MouseEvent e) {
        line = Color.GRAY;
        this.repaint();
    }

    public void mouseDragged(MouseEvent e) {
        moveSlider(e.getX());
        fireStateChanged();
    }

    public void mouseMoved(MouseEvent e) {
    }

    public void keyTyped(KeyEvent e) {
    }

    public void keyPressed(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_LEFT:
            case KeyEvent.VK_DOWN:
                moveSlider(displayValue - 1);
                fireStateChanged();
                break;
            case KeyEvent.VK_UP:
            case KeyEvent.VK_RIGHT:
                moveSlider(displayValue + 1);
                fireStateChanged();
                break;
        }
    }

    public void keyReleased(KeyEvent e) {
    }
}
