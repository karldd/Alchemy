/*
 *  This file is part of the Alchemy project - http://al.chemy.org
 * 
 *  Copyright (c) 2007-2010 Karl D.D. Willis
 * 
 *  Alchemy is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 * 
 *  Alchemy is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 * 
 *  You should have received a copy of the GNU General Public License
 *  along with Alchemy.  If not, see <http://www.gnu.org/licenses/>.
 * 
 */
package org.alchemy.core;

import java.awt.*;
import java.awt.event.*;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.image.BufferedImage;
import javax.swing.BoxLayout;
import javax.swing.JComponent;

//import java.awt.Graphics;
//import java.awt.Graphics2D;
import java.awt.geom.RoundRectangle2D;

/**
 * AlcSwatchColorButton
 * This big "button" shows all the swatch colors, interprets clicks by location
 */
class AlcSwatchColorButton extends JComponent implements MouseListener, AlcConstants{

    // COLOR PANEL
    private JComponent colorPanel;
    private Image colorPanelImage;
    
    private Color trans ;
    private Color opaque;

    
    /** Creates a new instance of AlcSwatchColorButton */
    AlcSwatchColorButton() {

        this.setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));

        // COLOR PANEL
        colorPanel = new JComponent() {

        @Override
           public void paintComponent(Graphics g) {
              g.drawImage(colorPanelImage, 0, 0, null);
           }
        };
        
        colorPanel.addComponentListener(new ComponentListener() {
            public void componentHidden(ComponentEvent e) {}

            public void componentMoved(ComponentEvent e) {}

            public void componentResized(ComponentEvent e) {
                refresh();
            }

            public void componentShown(ComponentEvent e) {}

        });
        
        colorPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        colorPanel.addMouseListener(this);

        this.add(colorPanel);        
    }


       /** Refresh the color panel */
    void refresh() {
        colorPanel.revalidate();     
        colorPanelImage = getColorPanelImage();
        colorPanel.repaint();
    }
    
    void clear(){
        Dimension d = colorPanel.getSize();
        BufferedImage image = new BufferedImage((int)d.getWidth(), (int)d.getHeight(), BufferedImage.TYPE_INT_ARGB);
        colorPanelImage = image;
        colorPanel.repaint();
    }


    /** Get the color panel image */
    private Image getColorPanelImage() {
        Dimension d = colorPanel.getSize();
        BufferedImage image = new BufferedImage((int)d.getWidth(), (int)d.getHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = image.createGraphics();
        int baseWidth;
        int m;
        // prevent division by 0 exception
        if(Alchemy.canvas.swatch.isEmpty()){
            baseWidth = 0;  
            m=0;           
        }else{
            baseWidth = (int)d.getWidth()/Alchemy.canvas.swatch.size();

            //modulus to find how many extra width pixels
            m = (int)d.getWidth()%Alchemy.canvas.swatch.size();
        }
        
        //keeps track of how many of the mudulus pixels we've filled
        int mCounter = 0;
        //how much taller is the active swatch element than its peers?
        int highlightSize = 10;
        //the size of the tray that shows each colors alpha
        int alphaTraySize = 12;        
        //the height we will pass to the paint method
        int h;
        //the starting y location we will pass to the paint method
        int h2;
        //the width we will pass to the paint method
        int w;
        
        int lastEdge=0;
        RoundRectangle2D rrect;
        
        //loop that steps through building all swatch colors
        for(int n=0; n<Alchemy.canvas.swatch.size(); n++){
           
            trans = Alchemy.canvas.swatch.get(n);
            
            if(Alchemy.canvas.isAlphaLocked()){
                trans = new Color(trans.getRed(),trans.getGreen(),trans.getBlue(),Alchemy.canvas.getAlpha());
            }
            
            g.setColor(trans);
            
           
           if(n==Alchemy.canvas.activeSwatchIndex){
               h = (int)d.getHeight() - alphaTraySize - 1;
               h2 = 0;
           }else{
               h = (int)d.getHeight() - alphaTraySize - highlightSize -1;
               h2 = highlightSize;
           }
           
           if(mCounter<m){
               w=baseWidth+1;
               m++;
           }else{
               w=baseWidth;
           }
           
           //paint alpha tray
           g.fillRect(lastEdge,h2+h,w,alphaTraySize);
           
           opaque = new Color (trans.getRed(),trans.getGreen(),trans.getBlue(),255);
           
           //set opaque
           g.setColor(opaque);
           
           //paint opaque top
           if(n==Alchemy.canvas.activeSwatchIndex){
              g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                                 RenderingHints.VALUE_ANTIALIAS_ON);
              g.fillRoundRect(lastEdge,h2,w,h,highlightSize*2,highlightSize*2);
              g.fillRect(lastEdge,h-highlightSize,w,highlightSize);
           }else{
              g.fillRect(lastEdge,h2,w,h);
           }

           lastEdge+=w;
           
        }
            
        return image;
    }

    public void mouseClicked(MouseEvent e) {
    }

    public void mousePressed(MouseEvent e) {
        int x = e.getX();
        //int y = e.getY();
        Dimension d = colorPanel.getSize();
        
        int baseWidth = (int)d.getWidth()/Alchemy.canvas.swatch.size();
        //modulus to find how many extra width pixels
        int m = (int)d.getWidth()%Alchemy.canvas.swatch.size();
        int n;
        int modSize = (baseWidth*m)+m;
        
        if(x<=modSize){
            n = x/(baseWidth+1);
        }else{
            n = ((x-modSize)/baseWidth)+m;
        }
             
        trans = Alchemy.canvas.swatch.get(n);
        
        Alchemy.canvas.setColor(trans);
        
        if(Alchemy.canvas.isAlphaLocked()){
            
        }else{
           Alchemy.canvas.setAlpha(trans.getAlpha());
        }
        
        Alchemy.canvas.activeSwatchIndex=n;
        
        Alchemy.toolBar.setSwatchLRButtons();
        
        refresh();

        Alchemy.toolBar.refreshColorButton();
    }

    public void mouseReleased(MouseEvent e) {
    }

    public void mouseEntered(MouseEvent e) {
    }

    public void mouseExited(MouseEvent e) {
    }
}
