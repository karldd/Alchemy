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
public class AlcSliderCustom extends JComponent implements MouseListener, MouseMotionListener, MouseWheelListener, KeyListener, AlcNumberDialogInterface, AlcConstants {

    int width, height;
    /** Minimum / Maximum / Display Position of the slider */
    int min, max, displayValue;
    /** Actual Value */
    int trueValue;
    boolean mouseDown;
    /** Border painting on/off */
    boolean borderPainting = true;
    /** Fill painting on/off */
    boolean fillPainting = true;
    /** Background Image */
    Image bgImage;
    /** Size of one step */
    private float step;
    private float scale;
    private Color line = Color.GRAY;
    /** The ChangeEvent that is passed to all listeners of this slider. */
    protected transient ChangeEvent changeEvent;
    private AlcNumberDialog dialog;
    private String title;

    AlcSliderCustom(String title, int width, int height, int min, int max, int initialSliderValue) {
        this.width = width;
        this.height = height;
        this.title = title;
        addMouseListener(this);
        addMouseMotionListener(this);
        addMouseWheelListener(this);
        addKeyListener(this);
        //addFocusListener(this);
        this.setOpaque(false);
        this.setPreferredSize(new Dimension(width, height));
        setup(min, max);
        setValue(initialSliderValue);
        dialog = new AlcNumberDialog(this);
    }

    public void setup(int min, int max) {
        this.min = min;
        this.max = max;
        this.scale = (max - min);
        this.step = scale / (float) width;
        // Recalculate the true slider value
        moveSlider(displayValue);
        fireStateChanged();
    }

    public String getTitle() {
        return this.title;
    }

    @Override
    public void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        if (bgImage != null) {
            g2.drawImage(bgImage, 0, 0, null);
        } else {
            // BACKGROUND FILL
            g2.setColor(COLOR_UI_START);
            g2.fillRect(1, 1, width - 2, height - 2);
        }
        g2.setColor(Color.LIGHT_GRAY);
        if (borderPainting) {
            if (fillPainting) {
                g2.fillRect(1, 1, displayValue, height - 2);
            }

            AlcUtil.drawSoftRect(g2, 0, 0, width, height);

        } else {
            if (fillPainting) {
                g2.fillRect(0, 0, displayValue, height);
            }
        }

        if (bgImage != null) {
            g2.setColor(Color.WHITE);
            int displayValueMinus = displayValue - 1;
            if (displayValueMinus >= 0) {
                g2.drawLine(displayValueMinus, 0, displayValueMinus, height);
            }
            g2.setColor(Color.BLACK);
        } else {
            g2.setColor(line);
        }
        //g2.setStroke(new BasicStroke(2, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL));
        g2.drawLine(displayValue, 0, displayValue, height);

    }

    private void moveSlider(int x) {
        if (x < 0) {
            displayValue = 0;
        } else if (x > width) {
            displayValue = width;
        } else if (x >= 0 && x < width) {
            displayValue = x;
        }
        trueValue = min + Math.round(step * displayValue);
        this.repaint();
    }

    /** Turn border painting on or off */
    void setBorderPainted(boolean b) {
        borderPainting = b;
    }

    /** Turn fill painting on or off */
    void setFillPainted(boolean b) {
        fillPainting = b;
    }

    /** Set the background image */
    void setBgImage(Image bgImage) {
        this.bgImage = bgImage;
    }

    boolean getValueIsAdjusting() {
        return mouseDown;
    }

    int getValue() {
        return trueValue;
    }

    void setValue(int value) {
        if (value >= min && value <= max) {
            trueValue = value;
            displayValue = Math.round((width / scale) * value);
            this.repaint();
        }
    }

    void addChangeListener(ChangeListener listener) {
        listenerList.add(ChangeListener.class, listener);
    }

    void removeChangeListener(ChangeListener listener) {
        listenerList.remove(ChangeListener.class, listener);
    }

    /**
     * This method is called whenever the model fires a ChangeEvent. It should
     * propagate the ChangeEvent to its listeners with a new ChangeEvent that
     * identifies the slider as the source.
     */
    void fireStateChanged() {
        // Repaint here incase the UI element is triggered by a shortcut
        this.repaint();
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
        // Check for a CTRL click
        boolean showDialog = false;
        if (Alchemy.OS == OS_MAC) {
            if (e.isMetaDown()) {
                showDialog = true;
            }
        } else {
            if (e.isControlDown()) {
                showDialog = true;
            }
        }

        // SHOW THE NUMBER DIALOG
        if (showDialog) {
            if (!Alchemy.preferences.simpleToolBar) {
                dialog.show(this.min, this.max);
            }
        }
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

    public void mouseWheelMoved(MouseWheelEvent e) {
        int scrollAmount = Math.round(e.getUnitsToScroll() * step) * -1;
        this.setValue(trueValue + scrollAmount);
    }
}
