/**
 * AlcComboBox.java
 *
 * Created on November 27, 2007, 9:23 AM
 *
 * @author  Karl D.D. Willis
 * @version 1.0
 */

package alchemy.ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import javax.swing.*;
import javax.swing.plaf.basic.*;

public class AlcComboBox extends JComboBox{
    
    AlcUi parent;
    
    /** Creates a new instance of AlcComboBox */
    public AlcComboBox(AlcUi parent, Object[] list) {
        
        // TODO delete this?
        
        this.parent = parent;
        
        //BasicComboBoxUI basic = new BasicComboBoxUI();
        //MetalComboBoxUI metal =
        //this.setUI(new MetalComboBoxUI());
        this.setUI(new BasicComboBoxUI());
        
        
        
        for (int i = 0; i < list.length; i++) {
            this.addItem(list[i]);
        }
        
        //createComboBox.setSelectedIndex(2);
        //createComboBox.setEnabled(true);
        //this.setEditable(true);
        //BasicComboBoxEditor editor = (BasicComboBoxEditor)createComboBox.getEditor();
        // JComponent myComponent = (JComponent)this.getEditor().getEditorComponent();
        
        this.setFont(new Font("sansserif", Font.PLAIN, parent.getUiTextSize()));
        //createComboBox.setEnabled(false);
        
        
        //myComponent.setMinimumSize(new Dimension(200, 75));
        this.setPreferredSize(new Dimension(120, 25));
        
        
        this.setBackground(parent.getUiBgColour());
        //createComponent.setFocusPainted(false);
        //this.setForeground(Color.white);
        
    }
    
}

/* TRY THIS
 * Checkboxes inside ComboBox
 *
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
 
public class CheckCombo implements ActionListener
{
    public void actionPerformed(ActionEvent e)
    {
        JComboBox cb = (JComboBox)e.getSource();
        CheckComboStore store = (CheckComboStore)cb.getSelectedItem();
        CheckComboRenderer ccr = (CheckComboRenderer)cb.getRenderer();
        ccr.checkBox.setSelected((store.state = !store.state));
    }
 
    private JPanel getContent()
    {
        String[] ids = { "north", "west", "south", "east" };
        Boolean[] values =
        {
            Boolean.TRUE, Boolean.FALSE, Boolean.FALSE, Boolean.FALSE
        };
        CheckComboStore[] stores = new CheckComboStore[ids.length];
        for(int j = 0; j < ids.length; j++)
            stores[j] = new CheckComboStore(ids[j], values[j]);
        JComboBox combo = new JComboBox(stores);
        combo.setRenderer(new CheckComboRenderer());
        combo.addActionListener(this);
        JPanel panel = new JPanel();
        panel.add(combo);
        return panel;
    }
 
    public static void main(String[] args)
    {
        JFrame f = new JFrame();
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.getContentPane().add(new CheckCombo().getContent());
        f.setSize(300,160);
        f.setLocation(200,200);
        f.setVisible(true);
    }
}
 
// adapted from comment section of ListCellRenderer api
class CheckComboRenderer implements ListCellRenderer
{
    JCheckBox checkBox;
 
    public CheckComboRenderer()
    {
        checkBox = new JCheckBox();
    }
    public Component getListCellRendererComponent(JList list,
                                                  Object value,
                                                  int index,
                                                  boolean isSelected,
                                                  boolean cellHasFocus)
    {
        CheckComboStore store = (CheckComboStore)value;
        checkBox.setText(store.id);
        checkBox.setSelected(((Boolean)store.state).booleanValue());
        checkBox.setBackground(isSelected ? Color.red : Color.white);
        checkBox.setForeground(isSelected ? Color.white : Color.black);
        return checkBox;
    }
}
 
class CheckComboStore
{
    String id;
    Boolean state;
 
    public CheckComboStore(String id, Boolean state)
    {
        this.id = id;
        this.state = state;
    }
}
 */
