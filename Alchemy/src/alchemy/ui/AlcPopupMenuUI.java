package alchemy.ui;

import java.awt.*;
import java.awt.image.*;
import javax.swing.*;
import javax.swing.plaf.*;
import javax.swing.plaf.metal.*;
import javax.swing.plaf.basic.*;

public class AlcPopupMenuUI extends BasicPopupMenuUI {
    
    public static ComponentUI createUI(JComponent c) {
        return new AlcPopupMenuUI();
    }
    
    public void installUI(JComponent c) {
        super.installUI(c);
        popupMenu.setOpaque(false);
    }
    
    public Popup getPopup(JPopupMenu popup, int x, int y) {
        Popup pp = super.getPopup(popup,x,y);
        JPanel panel = (JPanel)popup.getParent();
        panel.setOpaque(false);
        return pp;
    }
    
}
