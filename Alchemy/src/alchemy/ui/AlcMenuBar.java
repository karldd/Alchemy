/**
 * AlcMenuBar.java
 *
 * Created on November 24, 2007, 3:08 PM
 *
 * @author  Karl D.D. Willis
 * @version 1.0
 */
package alchemy.ui;

import alchemy.AlcMain;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class AlcMenuBar extends JMenuBar {

    AlcToolBar parent;
    AlcMain root;
    JMenu menu;
    JMenuItem menuItem;

    /** Creates a new instance of AlcMenuBar */
    public AlcMenuBar(AlcToolBar parent, AlcMain root) {

        this.parent = parent;
        this.root = root;


        this.setBackground(parent.toolBarBgColour);
        //this.setOpaque(false);
        //this.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));


        //Build the first menu.
        menu = new JMenu("A Menu");
        // TODO - fix up the menu - add PDF stuff
        menu.setBackground(parent.toolBarHighlightColour);
        menu.setFont(new Font("sansserif", Font.PLAIN, parent.getToolBarTextSize()));
        menu.setOpaque(false);

        menu.setMnemonic(KeyEvent.VK_A);
        menu.getAccessibleContext().setAccessibleDescription(
                "The only menu in this program that has menu items");
        this.add(menu);

        //a group of JMenuItems
        menuItem = new JMenuItem("A text-only menu item",
                KeyEvent.VK_T);
        //menuItem.setMnemonic(KeyEvent.VK_T); //used constructor instead
        menuItem.setAccelerator(KeyStroke.getKeyStroke(
                KeyEvent.VK_1, ActionEvent.ALT_MASK));
        menuItem.getAccessibleContext().setAccessibleDescription(
                "This doesn't really do anything");
        //menuItem.addActionListener(this);
        menuItem.setFont(new Font("sansserif", Font.PLAIN, parent.getToolBarTextSize()));
        menuItem.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));
        menu.add(menuItem);

    }

    // Override the paint component to draw the gradient bg
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        //int panelWidth = getWidth();

        GradientPaint gradientPaint = new GradientPaint(0, 0, new Color(215, 215, 215), 0, this.getHeight(), new Color(207, 207, 207), true);
        if (g instanceof Graphics2D) {
            Graphics2D g2 = (Graphics2D) g;
            // Turn on text antialias - windows does not use it by default
            //g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            g2.setPaint(gradientPaint);
            g2.fillRect(0, 0, root.getWindowSize().width, this.getHeight());
            g2.setPaint(parent.toolBarHighlightColour);
            g2.drawLine(0, 0, root.getWindowSize().width, 0);
            g2.setPaint(parent.toolBarLineColour);
            g2.drawLine(0, this.getHeight() - 1, root.getWindowSize().width, this.getHeight() - 1);
        }
    }
}
