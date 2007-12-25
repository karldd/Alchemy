/**
 * AlcMainToolBar.java
 *
 * Created on November 28, 2007, 9:29 PM
 *
 * @author  Karl D.D. Willis
 * @version 1.0
 */
package alchemy.ui;

import alchemy.*;
import java.awt.*;
import javax.swing.BorderFactory;
import javax.swing.JToolBar;


public class AlcMainToolBar extends JToolBar {

    AlcMain root;
    AlcToolBar parent;
    private int height = 60;

    /** Creates a new instance of AlcMainToolBar */
    public AlcMainToolBar(AlcMain root) {

        this.root = root;
        this.parent = root.toolBar;

        // Allow a transparent background
        this.setOpaque(false);
        this.setName("Toolbar");
        //this.setBackground(AlcToolBar.toolBarBgColour);
        //this.setBorderPainted(false);
        this.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));

        //this.setBounds(0, 0, root.getWindowSize().width, this.height);

        this.setLayout(new FlowLayout(FlowLayout.LEFT));
        //this.setLayout(new BorderLayout());

        //this.setAlignmentX(Component.LEFT_ALIGNMENT);
        //this.setAlignmentY(Component.TOP_ALIGNMENT);

        this.setPreferredSize(new Dimension(root.getWindowSize().width, height));


    }

    // Override the paint component to draw the gradient bg
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        //int panelWidth = getWidth();
        GradientPaint gradientPaint = new GradientPaint(0, 0, AlcToolBar.toolBarBgStartColour, 0, this.height, AlcToolBar.toolBarBgEndColour, true);
        if (g instanceof Graphics2D) {
            Graphics2D g2 = (Graphics2D) g;
            // Turn on text antialias - windows does not use it by default
            //g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            g2.setPaint(gradientPaint);
            g2.fillRect(0, 0, root.getWindowSize().width, this.height);
            g2.setPaint(AlcToolBar.toolBarLineColour);
            g2.drawLine(0, this.height - 1, root.getWindowSize().width, this.height - 1);
        }
    }

    @Override
    public int getHeight() {
        return this.height;
    }
}
