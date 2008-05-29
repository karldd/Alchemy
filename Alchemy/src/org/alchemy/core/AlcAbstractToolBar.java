/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.alchemy.core;

import java.awt.Dimension;
import javax.swing.JPanel;

/**
 *
 * Abstract class to allow multiple toolbar implementations
 * Eg, the regular toolbar as well as a simplified kids one
 * 
 */
public abstract class AlcAbstractToolBar extends JPanel {

    boolean isToolBarVisible() {
        return this.isVisible();
    }

    int getTotalHeight() {
        return this.getHeight();
    }

    void toggleToolBar(int y) {
    }

    void toggleToolBar(int y, boolean startTimer) {
    }

    void setToolBarVisible(boolean visible) {
    }

    void refreshSwapButton() {
    }

    void queueSwapButtonRefresh() {
    }

    void resizeToolBar() {
    }

    void resizeToolBar(Dimension windowSize) {
    }

    void calculateTotalHeight() {
    }

    void detachToolBar() {
    }

    void attachToolBar() {
    }

    void toggleDetachButton(boolean visible) {
    }

    void addPaletteContent() {
    }

    public void addSubToolBarSection(AlcToolBarSubSection subToolBarSection) {
    }
}
