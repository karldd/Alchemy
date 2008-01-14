package alchemy.ui;

import java.awt.*;
import java.awt.image.*;
import javax.swing.*;
import javax.swing.plaf.*;
import javax.swing.plaf.metal.*;
import javax.swing.plaf.basic.*;

public class AlcMenuItemUI extends BasicMenuItemUI {

    public static ComponentUI createUI(JComponent c) {
        return new AlcMenuItemUI();
    }

    public void paint(Graphics g, JComponent comp) {
        // paint to the buffered image
        BufferedImage bufimg = new BufferedImage(comp.getWidth(), comp.getHeight(),
                BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = bufimg.createGraphics();
        // restore the foreground color in case the super class needs it
        g2.setColor(g.getColor());
        super.paint(g2, comp);
        // do an alpha composite
        Graphics2D gx = (Graphics2D) g;
        gx.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.8f));
        gx.drawImage(bufimg, 0, 0, null);
    }
}

