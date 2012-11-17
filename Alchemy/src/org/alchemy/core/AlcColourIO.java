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

import java.io.*;
import java.net.*;
import java.awt.*;
import java.awt.Dialog.ModalityType;
import java.awt.event.*;
import java.util.regex.*;
import java.util.ArrayList;
import java.nio.*;
import java.nio.charset.Charset;
import javax.swing.*; 
import java.util.Random;
import java.awt.image.BufferedImage;

public class AlcColourIO implements AlcConstants{ 
    
    private ArrayList<Color> colours;

    private volatile int errorType;
    private volatile String errorText;
    
    private final Charset UTF8_CHARSET = Charset.forName("UTF-8");
    
    //Remember Color Modulation Values here...
    private float[] modPercents = {0.0f,0.0f,0.0f,0.0f};
    private boolean[] modAmounts = {false,false,false,false};
    private boolean[] modEnabled = {false,false,false,false};
    private boolean[] modVaried = {false,false,false,false};
    private int[] modDirection = {0,0,0,0};
    
    public ColourLovers clc = new ColourLovers();

    AlcColourIO(){
        colours = new ArrayList<Color>();      
        errorType = 0;
        errorText = null;    
        clc.setupDialog();
    }
    
    public class ColourLovers{
        
       JDialog cLDialog = new JDialog();
        
       Box headingBox = new Box(BoxLayout.X_AXIS);
       Box infoBox = new Box(BoxLayout.X_AXIS);
       Box colorsBox = new Box(BoxLayout.X_AXIS); 
       
       JPanel buttonsPanel = new JPanel();
       JComponent colorPanel;
       
       JButton addColors = new JButton();
       JButton replaceColors = new JButton();
       JButton refreshColors = new JButton();
       JButton cancel = new JButton();
       JButton loadCancel = new JButton();
       
       int random;
       
        //pull strings from Colour Lovers XML files
        private String titleReg = "\\s*?<title><!\\[CDATA\\[(.+?)\\]\\]><\\/title>\\s*?";
        private String authorReg = "\\s*?<userName><!\\[CDATA\\[(.+?)\\]\\]><\\/userName>\\s*?";
        private String hexReg = "\\s*?<hex>(\\S+?)<\\/hex>\\s*?";
        private Pattern title = Pattern.compile(titleReg);
        private Pattern author = Pattern.compile(authorReg);
        private Pattern hex = Pattern.compile(hexReg);    
        private String titleString;
        private String authorString;
        private String urlString;
        
        JLabel loadingText = new JLabel("Fetching Palette from Colourlovers.com...");
        JLabel colorHeading = new JLabel(getS("swatchRetrieved"));
        JLabel colorAuthor = new JLabel();
        JLabel colorTitle = new JLabel();
        
        //-------------------------------------------|
        // "Get random swatch" menu item points here |
        //-------------------------------------------|
        public void getCL(int r){
          
            random = r;
                        
            //prep and start network thread
            fetchAndUpdate();
            
            //show dialog
            cLDialog.setVisible(true);
            
        }
        
        public void fetchAndUpdate(){
            //reset error status
            errorType = 0;
            errorText = null;
            
            //increment random number in case we didnt get a new one...
            random +=1;     
            
            //show loading screen in dialog
            setLoadingState();
            
            Thread fetchAndUpdateThread = new Thread(new FetchAndUpdateRunnable());
            fetchAndUpdateThread.start();
        }
        
        //Runnable, executed by fetchAndUpdateThread in fetchAndUpdate method
        public class FetchAndUpdateRunnable implements Runnable{
            public void run(){
                
                colours.clear();
                System.out.println("getting swatch from colourlovers.com");
                getColourLoversData(random);
                
                //if the window was already closed/canceled - dont bother
                if(cLDialog.isVisible()){                           
                    //handle errors here! error type/text set in getColourLoversData
                    if(errorType>0){
                        closeDialog();
                        AlcUtil.showMessageDialog(errorText, getS("errorTitle"), JOptionPane.ERROR_MESSAGE);
                    //not canceled, no errors, lets show what we found
                    }else{
                        setCLState();  
                    }
                }            
                
                System.out.println("finished swatch retrieval thread");   
            }
        }
        
        //Preps the Colour Lovers dialog box cLDialog
        public void setupDialog(){
            
           cLDialog.setModalityType(ModalityType.APPLICATION_MODAL);
           cLDialog.setDefaultCloseOperation(2);
           cLDialog.setSize(500,250);
           cLDialog.setTitle(getS("colourLoversImportTitle"));
           cLDialog.setLocationRelativeTo(null);

           cLDialog.setLayout(new BoxLayout(cLDialog.getContentPane(), BoxLayout.Y_AXIS));

           buttonsPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 5));
           
           resetColorPanel();

           AbstractAction addColorsAction = new AbstractAction() {
               public void actionPerformed(ActionEvent e) {
                   Alchemy.canvas.activeSwatchIndex=Alchemy.canvas.swatch.size();
                   int n = 0;
                   while (n<colours.size()){
                      Alchemy.canvas.swatch.add(colours.get(n));
                      n++;
                   }
                   closeDialog();
               }
           };

           AbstractAction replaceColorsAction = new AbstractAction() {
               public void actionPerformed(ActionEvent e) { 
                   Alchemy.canvas.swatch.clear();
                   int n = 0;
                   while (n<colours.size()){
                      Alchemy.canvas.swatch.add(colours.get(n));
                      n++;
                   }
                   Alchemy.canvas.activeSwatchIndex=0;
                   closeDialog();
               }

           };

           AbstractAction refreshColorsAction = new AbstractAction() {
               public void actionPerformed(ActionEvent e) {               
                   fetchAndUpdate();
               }
           };

           AbstractAction cancelAction = new AbstractAction() {
               public void actionPerformed(ActionEvent e) {
                   closeDialog();
               }
           };
      
           addColors.setAction(addColorsAction);
           addColors.setText(getS("add"));
           addColors.setMnemonic(KeyEvent.VK_ENTER);
         
           replaceColors.setAction(replaceColorsAction);
           replaceColors.setText(getS("replace"));
          
           refreshColors.setAction(refreshColorsAction);
           refreshColors.setText(getS("refresh"));
          
           cancel.setAction(cancelAction);
           cancel.setText(getS("cancel"));
           cancel.setMnemonic(KeyEvent.VK_ESCAPE);
           
           loadCancel.setAction(cancelAction);
           loadCancel.setText(getS("cancel"));
           loadCancel.setMnemonic(KeyEvent.VK_ESCAPE);
           loadCancel.setAlignmentX(Component.CENTER_ALIGNMENT);
           
           loadingText.setAlignmentX(Component.CENTER_ALIGNMENT);

           colorsBox.add(Box.createHorizontalStrut(10));
           colorsBox.add(colorPanel);
           colorsBox.setPreferredSize(new Dimension(490,80));
           colorsBox.setMinimumSize(new Dimension(490,80));
          
           headingBox.add(colorHeading);
           infoBox.add(colorAuthor);
           infoBox.add(colorTitle);
           
           if (Alchemy.OS == OS_MAC) {               
               buttonsPanel.add(cancel);            
               buttonsPanel.add(refreshColors);
               buttonsPanel.add(replaceColors);
               buttonsPanel.add(addColors);          
           } else {            
               buttonsPanel.add(addColors);
               buttonsPanel.add(replaceColors);
               buttonsPanel.add(refreshColors);
               buttonsPanel.add(cancel);           
           }           
        }
        
        //adjust cLDialog contents to show the loading state
        private void setLoadingState(){
           cLDialog.getContentPane().removeAll();
           cLDialog.add(Box.createVerticalStrut(45));
           cLDialog.add(loadingText);
           cLDialog.add(Box.createVerticalStrut(45));
           cLDialog.add(loadCancel); 
           cLDialog.getContentPane().repaint();
       }
       //adjust cLDialog contents to display the retrieved swatch
       private void setCLState(){
           resetColorPanel();
           
           colorAuthor.setText(getS("author")+" "+authorString);
           colorTitle.setText("      "+getS("title")+" "+titleString );
           
           cLDialog.getContentPane().removeAll();
           cLDialog.add(Box.createVerticalStrut(15));
           cLDialog.add(headingBox);
           cLDialog.add(Box.createVerticalStrut(10));
           cLDialog.add(infoBox);
           cLDialog.add(Box.createVerticalStrut(20));
           cLDialog.add(colorsBox);
           cLDialog.add(buttonsPanel);
           
           // revalidate causes a build error on mac platform   
           //cLDialog.getContentPane().revalidate();
           //cLDialog.getContentPane().invalidate();
           cLDialog.getContentPane().validate();
           
           
           cLDialog.getContentPane().repaint();
       }      
              
       void closeDialog(){
           cLDialog.setVisible(false);
           cLDialog.dispose();
       }
       
       private void resetColorPanel(){
           colorPanel = getColorPanel();
           colorPanel.repaint();
       }
       
       private JComponent getColorPanel() {
           
           JComponent cP;       
           cP = new JComponent() {
              @Override
              public void paintComponent(Graphics g) {
                 g.drawImage(getColorPanelImage(), 0, 0, null);
              }
           };

           return cP;
       }
       
       private Image getColorPanelImage() {
           Dimension d = new Dimension(480,80);
           BufferedImage image = new BufferedImage((int)d.getWidth(), (int)d.getHeight(), BufferedImage.TYPE_INT_ARGB);
           Graphics2D g = image.createGraphics();
           int baseWidth;
           int m;
           baseWidth = (int)d.getWidth()/colours.size();

           //modulus to find how many extra width pixels
           m = (int)d.getWidth()%colours.size();       
           //keeps track of how many of the mudulus pixels we've filled
           int mCounter = 0;
           //the height we will pass to the paint method
           int h = (int)d.getHeight();
           //the width we will pass to the paint method
           int w;

           int lastEdge=0;

           //loop that steps through building all swatch colors
           for(int n=0; n<colours.size(); n++){

              g.setColor(colours.get(n));

              if(mCounter<m){
                  w=baseWidth+1;
                  m++;
              }else{
                  w=baseWidth;
              }     

              g.fillRect(lastEdge,0,w,h);

              lastEdge+=w;

           }          
           return image;
       }
        
        // Gets-Parses XML color data info from colourlovers.com.
        //     -int i = which (by rank) palette to fetch.
        private void getColourLoversData(int i){
            String nextLine;

            urlString="http://www.colourlovers.com/api/palettes/top?numResults=1&resultOffset="+
                        Integer.toString(i);
            URL url = null;
            URLConnection urlConn = null;
            InputStreamReader inStream = null;
            BufferedReader buff = null;
            Matcher m;
            try{
                url  = new URL(urlString);
                urlConn = url.openConnection();
                inStream = new InputStreamReader( 
                                urlConn.getInputStream());
                buff= new BufferedReader(inStream);

                while (true){
                    nextLine =buff.readLine();  
                    if (nextLine !=null){

                        m = title.matcher(nextLine);
                        if (m.matches()) {
                            titleString = m.group(1);
                        }
                        m = author.matcher(nextLine);
                        if (m.matches()) {
                            authorString = m.group(1);
                        }
                        m = hex.matcher(nextLine);
                        if (m.matches()) {                
                            colours.add(Color.decode("#"+m.group(1)));
                        }  
                    }else{
                        break;
                    } 
                }
            }catch(MalformedURLException e){
                errorType=1;
                errorText=getS("getNetDataError1")+urlString;
            }catch(IOException  e1){
                errorType=2;
                errorText=getS("getNetDataError2");
            }
            if(colours.isEmpty()&&errorType==0){
                errorType=3;
                errorText=getS("getNetDataError3")+urlString;
            }
        }
        
    }
    
    // controls import of gpl/ase swatch files.
    public void importFileSwatch(){
       File importFile = AlcUtil.showFileChooser();
       if(importFile==null){
           System.out.println("No File selected for export");
       }else if(!importFile.canRead()){
           AlcUtil.showMessageDialog(getS("noReadError"), getS("errorTitle"), JOptionPane.ERROR_MESSAGE);
       }else{
           int type=-1;
           try{
               type = getFileType(importFile);
               if (type==1){
                   readGPL(importFile);
               }else if(type==2){
                   readASE(importFile);
               }else if(type==-1){
                   AlcUtil.showMessageDialog(getS("invalidImport"), getS("errorTitle"), JOptionPane.ERROR_MESSAGE);     
               }

           }catch (IOException ex) {
                System.err.println(ex);
           } 
       }
    }
    
    // determines whether the file contains a gpl or ase swatch
    private int getFileType(File file){
        FileInputStream fis = null;
        DataInputStream dis = null;
        int type=-1;

        try {
            fis = new FileInputStream(file);
            dis = new DataInputStream(fis);

            byte[] fourByte = new byte[4];

            dis.read(fourByte);
            String header = decodeUTF8(fourByte);
            if (header.equals("GIMP")){
                type = 1;
            }else if(header.equals("ASEF")){
                type = 2;
            }else{
                type = -1;
            }
            fis.close();
            dis.close();
           
        }catch (IOException ex) {
            System.err.println(ex);
        }
        
        return type;
    }
    
    public void exportSwatch(){
       if (Alchemy.canvas.swatch.isEmpty()){
           AlcUtil.showMessageDialog(getS("emptySwatch"), getS("errorTitle"), JOptionPane.ERROR_MESSAGE);
       }else{
           exportDialog();
       }
    }
    
    // pulls up the "modulate swatch" dialog
    public void launchModulateDialog(){      
        if (Alchemy.canvas.swatch.isEmpty()){
           AlcUtil.showMessageDialog(getS("emptySwatch"), getS("errorTitle"), JOptionPane.ERROR_MESSAGE);
       }else{
           modulateDialog mD = new modulateDialog();
           mD.setVisible(true);   
       }        
    }
    
    // this function performs the modulations indicated in the 
    // modulateDialog.  Kinda Messy.  Sorry.
    public void modulateSwatch(){
        Random random = new Random();
        float[] prevPercs = {-1.0f,-1.0f,-1.0f,-1.0f};

        int[] prevDirs= {0,0,0,0};
        int n = 0;
        while(n<4){ //initialize stored values
            if(modDirection[n]==0){ //add
                prevDirs[n] = 1;
            }else if(modDirection[n]==1){//subtract
                prevDirs[n] = -1;
            }

            if(modAmounts[n]){//Equal to, not Up to
                prevPercs[n] = (modPercents[n]/100);
                if (n==0){prevPercs[0]*=360;}
                if (n==3){prevPercs[3]*=255;}
            }
            n++;
       }
       
       float r;  //random holder
       float[] result = {0,0,0,0};//result holder
       float degrees = 0;
       float[] hsbvals = new float[3];
       int alpha = 0;
       
       n = 0;
       while (n<Alchemy.canvas.swatch.size()){
       
           Color c = Alchemy.canvas.swatch.get(n);  
          
           alpha = c.getAlpha();
           Color.RGBtoHSB(c.getRed(),c.getGreen(),c.getBlue(), hsbvals);
           
           degrees=0.0f;
           result[1]=0.0f; //saturation
           result[2]=0.0f; //brightness
           
           //Adjust hue         
           if(modEnabled[0]){
               
               if(!modAmounts[0]){  //"up to" selected               
                   if(modVaried[0]||prevPercs[0]==-1){ //if varied or not already defined
                       //get random amount percent of degs, to degrees
                       prevPercs[0] = ((random.nextFloat()*modPercents[0])/100)*360;
                   }   
               }
               
               if(modDirection[0]==2){ //"either" direction selected
                   if(modVaried[0]||prevDirs[0]==0){ //reassign if varied or undefined
                       r=random.nextFloat();
                       if(r>.5){
                           prevDirs[0] = 1;
                       }else{
                           prevDirs[0] = -1;
                       }
                   }
               }
               if(prevDirs[0]<0){
                   degrees = 360-prevPercs[0];
               }else{
                   degrees = prevPercs[0];
               }
               result[0]=Alchemy.colourIO.addRYBDegrees(hsbvals[0],degrees);
           }else{
               result[0]=hsbvals[0];
           }
           //Adjust Saturation
           if(modEnabled[1]){
               if(!modAmounts[1]){  //"up to"               
                   if(modVaried[1]||prevPercs[1]==-1){//if varied or not already defined
                       //get random amount percent
                       prevPercs[1] = ((random.nextFloat()*modPercents[1])/100);
                   }   
               }
               
               if(modDirection[1]==2){ //either direction
                   if(modVaried[1]||prevDirs[1]==0){//reassign if varied or undefined
                       r=random.nextFloat();
                       if(r>.5){
                           prevDirs[1] = 1;
                       }else{
                           prevDirs[1] = -1;
                       }
                   }
               }
               if(prevDirs[1]<0){
                   result[1] = hsbvals[1]-prevPercs[1];
               }else{
                   result[1] = hsbvals[1]+prevPercs[1];
               }
               if (result[1]<0){result[1]=0.0f;}
               if (result[1]>1){result[1]=1.0f;}
           }else{
               result[1]=hsbvals[1];
           }
           
           //Adjust Brightness
           if(modEnabled[2]){
               
               if(!modAmounts[2]){  //"up to"               
                   if(modVaried[2]||prevPercs[2]==-1){//if varied or not already defined
                       //get random amount percent
                       prevPercs[2] = ((random.nextFloat()*modPercents[2])/100);
                   }   
               }
               
               if(modDirection[2]==2){ //either direction
                   if(modVaried[2]||prevDirs[2]==0){//reassign if varied or undefined
                       r=random.nextFloat();
                       if(r>.5){
                           prevDirs[2] = 1;
                       }else{
                           prevDirs[2] = -1;
                       }
                   }
               }
               if(prevDirs[2]<0){
                   result[2] = hsbvals[2]-prevPercs[2];
               }else{
                   result[2] = hsbvals[2]+prevPercs[2];
               }
               if (result[2]<0){result[2]=0.0f;}
               if (result[2]>1){result[2]=1.0f;}
           }else{
               result[2]=hsbvals[2];
           }
           
           //Adjust Alpha
           if(modEnabled[3]){
               
               if(!modAmounts[3]){  //"up to"               
                   if(modVaried[3]||prevPercs[3]==-1){//if varied or not already defined
                       //get random amount percent
                       prevPercs[3] = ((random.nextFloat()*(modPercents[3])/100)*255);
                   }   
               }
               
               if(modDirection[3]==3){ //either direction
                   if(modVaried[3]||prevDirs[3]==0){//reassign if varied or undefined
                       r=random.nextFloat();
                       if(r>.5){
                           prevDirs[3] = 1;
                       }else{
                           prevDirs[3] = -1;
                       }
                   }
               }
               if(prevDirs[3]<0){
                   result[3] = alpha-prevPercs[3];
               }else{
                   result[3] = alpha+prevPercs[3];
               }
               if (result[3]<0){result[3]=0.0f;}
               if (result[3]>255){result[3]=255;}
               
           }else{
               result[3]=alpha;
           }

           c = Color.getHSBColor(result[0],  result[1], result[2]);
           c = new Color(c.getRed(),c.getGreen(),c.getBlue(),(int)result[3]);
           Alchemy.canvas.swatch.set(n, c);
           n++;
       }
       
    }
    
    // Parses gpl swatch (text) files with regular expressions.
    private void readGPL(File file){
        FileInputStream fis = null;
        BufferedReader buff;
        String colReg = "\\s*?(\\d+?)\\s+?(\\d+?)\\s+?(\\d+?)\\s+?[\\d\\D]*";
        Pattern colLine = Pattern.compile(colReg);
        colours.clear();
        Matcher m;
        String nextLine;
        try {
          fis = new FileInputStream(file);
          buff = new BufferedReader(new InputStreamReader(fis));
          
          while (true){
             nextLine =buff.readLine();  
             if (nextLine != null){        
                m = colLine.matcher(nextLine);
                if (m.matches()) {
                    colours.add(new Color(Integer.parseInt(m.group(1)),
                                          Integer.parseInt(m.group(2)),
                                          Integer.parseInt(m.group(3))));
                } 
             }else{
                 break;
             } 
          }
          fis.close();
          buff.close();

        } catch (FileNotFoundException ex) {
            System.err.println(ex);
        } catch (IOException ex) {
            System.err.println(ex);
        }
        if(!colours.isEmpty()){
            Alchemy.canvas.swatch.clear();
            Alchemy.canvas.activeSwatchIndex=0;
            int n = 0;
            while(n<colours.size()){
                Alchemy.canvas.swatch.add(colours.get(n));
                n++;
            }
        }else{
            AlcUtil.showMessageDialog(getS("badSwatchFile"), getS("errorTitle"), JOptionPane.ERROR_MESSAGE);
        }
    }
    
    public void writeGPL(File file){
        int cols;
        errorType=0;
        errorText=null;
        if (Alchemy.canvas.swatch.size()<10){
            cols = Alchemy.canvas.swatch.size();
        }else{ 
            cols = 10;
        }
        try {

          FileWriter outFile = new FileWriter(file);
          PrintWriter out = new PrintWriter(outFile);
          out.println("GIMP Palette");
          out.println("Name: AlchemyExport");
          out.print("Columns: ");
          out.println(Integer.toString(cols));
          out.println("#");
          int n = 0;
          while(n<Alchemy.canvas.swatch.size()){
              if(Alchemy.canvas.swatch.get(n).getRed()<100){
                  out.print(" ");
              }
              out.print(Integer.toString(Alchemy.canvas.swatch.get(n).getRed()));
              
              if(Alchemy.canvas.swatch.get(n).getGreen()<100){
                  out.print("  ");
              }else{
                  out.print(" ");
              }
              out.print(Integer.toString(Alchemy.canvas.swatch.get(n).getGreen()));
              
              if(Alchemy.canvas.swatch.get(n).getBlue()<100){
                  out.print("  ");
              }else{
                  out.print(" ");
              }
              out.print(Integer.toString(Alchemy.canvas.swatch.get(n).getBlue()));
              
              out.println("\t"+getSwatchHexString(n));
              
              n++;              
          }
          out.close();
          outFile.close();
        }catch (IOException ex) {
           System.err.println(ex);
        }

    }
    private String getSwatchHexString(int n){
        String returnHex = null;
        String r = stringifyWithLeadingZero(Alchemy.canvas.swatch.get(n).getRed());
        String g = stringifyWithLeadingZero(Alchemy.canvas.swatch.get(n).getGreen());
        String b = stringifyWithLeadingZero(Alchemy.canvas.swatch.get(n).getBlue());
        returnHex = r+g+b;
        return returnHex;
    }
    private String stringifyWithLeadingZero(int n){
        String returnHex;
        if(n<16){
            returnHex = "0"+Integer.toHexString(n).toUpperCase();
        }else{
            returnHex = Integer.toHexString(n).toUpperCase();
        }
        return returnHex;
    }
    
    /////////--- ATTRIBUTION NOTE ---//////////////////////////////////////
    // writeASE function code adapted from the Generative Design Library //
    //    http://www.generative-gestaltung.de/codes/generativedesign/    //
    ///////////////////////////////////////////////////////////////////////
    
    private void writeASE(File file) {
            String NUL = new Character((char) 0).toString();
            String SOH = new Character((char) 1).toString();
            String colorName=null;

            int countColors = Alchemy.canvas.swatch.size();

            String ase = "ASEF" + NUL + SOH + NUL + NUL;
            for (int i = 24; i >= 0; i -= 8) {
                    ase += new Character((char) ((countColors >> i) & 0xFF)).toString();
            }
            ase += NUL;

            for (int i = 0; i < Alchemy.canvas.swatch.size(); i++) {
                    colorName= getSwatchHexString(i);
                    ase += SOH + NUL + NUL + NUL;
                    ase += new Character(
                                    (char) ((((colorName.length() + 1) * 2) + 20)))
                                    .toString()
                                    + NUL;
                    ase += new Character((char) (colorName.length() + 1)).toString()
                                    + NUL;

                    for (int j = 0; j < colorName.length(); j++) {
                            ase += colorName.substring(j, j + 1) + NUL;
                    }

                    String r = new String(floatTobytes(Alchemy.canvas.swatch.get(i).getRed() / 255f));
                    String g = new String(floatTobytes(Alchemy.canvas.swatch.get(i).getGreen() / 255f));
                    String b = new String(floatTobytes(Alchemy.canvas.swatch.get(i).getBlue() / 255f));

                    ase += NUL + "RGB ";
                    ase += r.substring(0, 1) + r.substring(1, 2) + r.substring(2, 3)
                                    + NUL;
                    ase += g.substring(0, 1) + g.substring(1, 2) + g.substring(2, 3)
                                    + NUL;
                    ase += b.substring(0, 1) + b.substring(1, 2) + b.substring(2, 3)
                                    + NUL;
                    if ((i + 1) != countColors) {
                            ase += NUL + NUL + NUL;
                    }
            }
            ase += NUL + NUL;

            try{
                FileOutputStream fos = new FileOutputStream(file);
                BufferedOutputStream bos = new BufferedOutputStream(fos);
                bos.write(ase.getBytes());
                bos.flush();
                bos.close();
                fos.close();
            }catch (IOException ex) {
           System.err.println(ex);
        }
    }

    // mini helper function for "writeASE"
    private static byte[] floatTobytes(float theNumber) {
            ByteBuffer buf = ByteBuffer.allocate(4);
            buf.putFloat(theNumber);
            return buf.array();
    }
    
    // reads Adobe Swatch Exchange files. ASE binary format detailed here:
    // http://www.selapa.net/swatches/colors/fileformats.php#adobe_ase
    private void readASE(File file) throws IOException{
        FileInputStream in = null;
        DataInputStream dis = null;
        colours.clear();

        try {
            in = new FileInputStream(file);
            dis = new DataInputStream(in);

            int numberOfBlocks = 0;
            short blockType;// = (char)0;
            int blockLength = 0;
            short nameLength = 0;
            int n = 0;
            String colorMode;
            float red,green,blue;

            byte[] fourByte = new byte[4];

            dis.read(fourByte);
            String header = decodeUTF8(fourByte);

           dis.read(fourByte);
           numberOfBlocks=dis.readInt();
           //System.out.println("number of blocks "+numberOfBlocks);
           while(n < numberOfBlocks){
               blockType=dis.readShort();
               blockLength=dis.readInt();

               // Is this a color block?
               if(blockType==1){
                   nameLength=dis.readShort();
                   dis.skipBytes(nameLength*2);
                   dis.read(fourByte);
                   colorMode = decodeUTF8(fourByte);
                   if(colorMode.equals("RGB ")){
                       red   = dis.readFloat();
                       green = dis.readFloat();
                       blue  = dis.readFloat();
                       colours.add(new Color(red,green,blue));                            
                   }else if (colorMode.equals("CMYK")){
                       dis.skipBytes(16);
                   }else if (colorMode.equals("LAB ")){
                       dis.skipBytes(12);
                   }else if (colorMode.equals("Gray")){
                       dis.skipBytes(4);
                   }
                   dis.skipBytes(2);
               }else{ //not a color block, skip
                   dis.skipBytes(blockLength);
               }

               n++;
           }
           if(!colours.isEmpty()){
               Alchemy.canvas.swatch.clear();
               n = 0;
               while(n<colours.size()){
                   Alchemy.canvas.swatch.add(colours.get(n));
                   n++;
               }
               Alchemy.canvas.activeSwatchIndex=0;
           }else{
               AlcUtil.showMessageDialog(getS("badSwatchFile"), getS("errorTitle"), JOptionPane.ERROR_MESSAGE);
           }  
        in.close();
        dis.close();
        }catch (IOException ex) {
           System.err.println(ex);
        }
    }

    String decodeUTF8(byte[] bytes) {
        return new String(bytes, UTF8_CHARSET);
    }       
    
    // The following RYB-RGB functions convert color hues between a pure 360 degree
    // RGB wheel and a shifted RYBish wheel.  This allows for generating more 
    // "painterly" or subjectively pleasant color relationships.

    public float RGBtoRYB(float hDegs){
        int base;
        int cap;
        int weightBase;
        int weightCap;            

        if(hDegs<35){
            base = 0;
            cap  = 35;
            weightBase = 0;
            weightCap  = 60;
        }else if(hDegs<60){
            base = 35;
            cap  = 60;
            weightBase = 60;
            weightCap  = 120;
        }else if(hDegs<135){
            base = 60;
            cap  = 135;
            weightBase = 120;
            weightCap  = 180;
        }else if(hDegs<225){
            base = 135;
            cap  = 225;
            weightBase = 180;
            weightCap  = 240;
        }else if(hDegs<275){
            base = 225;
            cap  = 275;
            weightBase = 240;
            weightCap  = 300;
        }else{
            base = 275;
            cap  = 360;
            weightBase = 300;
            weightCap  = 360;             
        }
        return processRYBRGB(hDegs,base,cap,weightBase,weightCap);
    }
    public float RYBtoRGB(float wDegs){
        int base;
        int cap;
        int weightBase;
        int weightCap;
        if(wDegs<60){
            base = 0;
            cap  = 35;
            weightBase = 0;
            weightCap  = 60;
        }else if(wDegs<120){
            base = 35;
            cap  = 60;
            weightBase = 60;
            weightCap  = 120;
        }else if(wDegs<180){
            base = 60;
            cap  = 135;
            weightBase = 120;
            weightCap  = 180;
        }else if(wDegs<240){
            base = 135;
            cap  = 225;
            weightBase = 180;
            weightCap  = 240;
        }else if(wDegs<300){
            base = 225;
            cap  = 275;
            weightBase = 240;
            weightCap  = 300;
        }else{
            base = 275;
            cap  = 360;
            weightBase = 300;
            weightCap  = 360;             
        }

        return processRYBRGB(wDegs,weightBase,weightCap,base,cap);
    }
    private float processRYBRGB(float x, float a, float b, float c, float d){
        return c+(x-a)*((d-c)/(b-a));

    }
    public float addRYBDegrees(float hue, float degrees){
        hue=hue*360;
        hue=RGBtoRYB(hue);
        hue=(hue+degrees)%360;
        hue=RYBtoRGB(hue);
        return hue/360;
    }
    
    String getS(String stringName) {
        return Alchemy.bundle.getString(stringName);
    }
   
   private void exportDialog() {

        final AlcFileChooser fc = new AlcFileChooser(Alchemy.preferences.exportDirectory);
        fc.setDialogTitle(Alchemy.bundle.getString("exportFileTitle"));
        fc.setAcceptAllFileFilterUsed(false);
        fc.setFileFilter(new ExportFileFilter("GPL"));
        fc.addChoosableFileFilter(new ExportFileFilter("ASE"));
        fc.setSelectedFile(new File(Alchemy.bundle.getString("defaultFileName")));

        // in response to a button click:
        int returnVal = fc.showSaveDialog(Alchemy.toolBar);
        if (returnVal == AlcFileChooser.APPROVE_OPTION) {
            
            File exportFile = fc.getSelectedFile();

            String format = fc.getFileFilter().getDescription();
            
            if (exportFile==null){
                AlcUtil.showMessageDialog(getS("noFileError"), getS("errorTitle"), JOptionPane.ERROR_MESSAGE);
            }else if(!exportFile.getParentFile().canWrite()){
                AlcUtil.showMessageDialog(getS("noWriteError"), getS("errorTitle"), JOptionPane.ERROR_MESSAGE);
            }else{
                if(format.equals("GPL")) {
                       writeGPL(exportFile);
                } else if(format.equals("ASE")) {
                       writeASE(exportFile);
                }
            }  
        }
    }
   
   private class modulateDialog extends JDialog{
        
        private JCheckBox[] enabled = new JCheckBox[4];
        private JComboBox[] direction = new JComboBox[4];
        private JComboBox[] amount = new JComboBox[4];
        private JSpinner[] percent = new JSpinner[4];
        private JCheckBox[] varied = new JCheckBox[4];
        private String[] directionOps = {getS("add"),getS("subtract"),getS("either")};
        private String[] amountOps = {getS("equalTo"),getS("upTo")};
    
       modulateDialog(){        

           this.setModalityType(ModalityType.APPLICATION_MODAL);
           this.setDefaultCloseOperation(2);
           this.setSize(550,280);
           this.setTitle(getS("modulateSwatchTitle"));
           this.setLocationRelativeTo(null);

           this.setLayout(new FlowLayout(1,15,15));
           
           JPanel huePanel    = buildModPanel(0,getS("hue")+" ");
           JPanel satPanel    = buildModPanel(1,getS("saturation")+" ");
           JPanel brightPanel = buildModPanel(2,getS("brightness")+" ");
           JPanel alphaPanel  = buildModPanel(3,getS("alpha")+" ");
           
           this.add(huePanel);
           this.add(satPanel);
           this.add(brightPanel);
           this.add(alphaPanel);
           
           AbstractAction okAction = new AbstractAction() {
               public void actionPerformed(ActionEvent e) {  
                   ok();
               }
           };
           
           AbstractAction cancelAction = new AbstractAction() {
               public void actionPerformed(ActionEvent e) { 
                   closeDialog();
               }
           };
           
           Box buttonsBox = new Box(BoxLayout.X_AXIS);
           
           // Ok Button
           JButton ok = new JButton();
           ok.setAction(okAction);
           ok.setText(getS("ok"));
                     
           ok.setMnemonic(KeyEvent.VK_ENTER);

           // Cancel Button
           JButton cancel = new JButton();
           cancel.setAction(cancelAction);
           cancel.setText(getS("cancel"));   
           cancel.setMnemonic(KeyEvent.VK_ESCAPE);

        if (Alchemy.OS == OS_MAC) {
            
           buttonsBox.add(cancel);  
           buttonsBox.add(Box.createHorizontalStrut(30));
           buttonsBox.add(ok);
           
        } else {
            
           buttonsBox.add(ok);
           buttonsBox.add(Box.createHorizontalStrut(30));
           buttonsBox.add(cancel);
           
        }
       
        this.add(buttonsBox);
           
       }
             
       private JPanel buildModPanel(final int index, String name){
           JPanel p= new JPanel();
           p.setLayout(new FlowLayout(0,15,5));
           
           Box b1 = new Box(BoxLayout.X_AXIS);
           Box b2 = new Box(BoxLayout.X_AXIS);
           
           SpinnerModel model =
            new SpinnerNumberModel(0,   //initial value
                                   0,   //min
                                 100,   //max
                                  10);  //step
           
           enabled[index] = new JCheckBox();
           if(index==0){enabled[index].setSelected(true);}
           direction[index] = new JComboBox(directionOps);
           amount[index] = new JComboBox(amountOps);
           percent[index] = new JSpinner(model);
           varied[index] = new JCheckBox();
           varied[index].setText(getS("varied"));
           
           enabled[index].addItemListener(new ItemListener() {
               public void itemStateChanged(ItemEvent e) {
                  setEnabled(index);
               }
           });
           direction[index].addItemListener(new ItemListener() {
               public void itemStateChanged(ItemEvent e) {
                  setVaried(index);
               }
           });
           amount[index].addItemListener(new ItemListener() {
               public void itemStateChanged(ItemEvent e) {
                  setVaried(index);
               }
           });
           
           b1.add(enabled[index]);
           b1.add(Box.createHorizontalStrut(5));
           b1.add(new JLabel(name));
           b1.setPreferredSize(new Dimension(110,25));
           p.add(b1);
           
           
           p.add(direction[index]);
           
           
           p.add(amount[index]);
           
           
           b2.add(percent[index]);
           b2.add(Box.createHorizontalStrut(5));
           b2.add(new JLabel("%"));
           p.add(b2);
           
           
           p.add(varied[index]);
           
           setEnabled(index);
           return p;
       }
       private void setEnabled(final int i){
           boolean b=enabled[i].isSelected();
           direction[i].setEnabled(b);
           amount[i].setEnabled(b);
           percent[i].setEnabled(b);
           if(b==true){
               setVaried(i);
           }else{
               varied[i].setEnabled(b);
           }
       }
       private void setVaried(final int i){
           if(direction[i].getSelectedIndex()==2 || amount[i].getSelectedIndex()==1){
               varied[i].setEnabled(true);
           }else{
               varied[i].setEnabled(false);
           }
       }
       private void ok(){
           int n = 0;
           while (n<4){
               
               modEnabled[n]=enabled[n].isSelected();
               
               if (modEnabled[n]){
                   if(amount[n].getSelectedIndex()==0){
                      modAmounts[n]=true;
                   }else{
                      modAmounts[n]=false;
                   }
                   modDirection[n] = direction[n].getSelectedIndex();
                   modPercents[n] = ((Integer)percent[n].getValue()).floatValue();
                   if (modPercents[n]>100){
                       modPercents[n]=100;
                   }else if(modPercents[n]<=0){
                       modPercents[n]=0;
                       modEnabled[n]=false;                       
                   }
                   if(varied[n].isEnabled()){
                       modVaried[n]=varied[n].isSelected();
                   }
               }
               n++;
           }
           modulateSwatch();
           closeDialog();
       }
       private void closeDialog(){
           this.setVisible(false);
           this.dispose();
       }
   }
}