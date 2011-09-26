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

import javax.swing.JOptionPane;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JComboBox;
import javax.swing.JButton;
import javax.swing.JTextField;
import javax.swing.AbstractAction;
import java.io.*;
import java.net.*;
import java.awt.Color;
import java.awt.Dialog.ModalityType;
import java.awt.event.ActionEvent;
import java.util.regex.*;
import java.util.ArrayList;
import java.nio.*;
import java.nio.charset.Charset;
import javax.swing.Box;
import javax.swing.BoxLayout; 

public class AlcColourIO implements AlcConstants{ 
    private String titleReg = "\\s*?<title><!\\[CDATA\\[(.+?)\\]\\]><\\/title>\\s*?";
    private String authorReg = "\\s*?<userName><!\\[CDATA\\[(.+?)\\]\\]><\\/userName>\\s*?";
    private String hexReg = "\\s*?<hex>(\\S+?)<\\/hex>\\s*?";
    private Pattern title = Pattern.compile(titleReg);
    private Pattern author = Pattern.compile(authorReg);
    private Pattern hex = Pattern.compile(hexReg);
    private String titleString;
    private String authorString;
    private ArrayList<Color> colours;
    private String urlString;
    private int errorType;
    private String errorText;
    private final Charset UTF8_CHARSET = Charset.forName("UTF-8");

    AlcColourIO(){
        colours = new ArrayList<Color>();      
        errorType = 0;
        errorText = null; 
        
    }
    
    public void setCLSwatch(int i){
        errorType=0;
        colours.clear();        
        getColourLoversData(i);
        
        JFrame frame = new JFrame();
        
        if(errorType==0){  //no errors         
            
            //Custom button text
            Object[] options = {"CANCEL","OK"};
            int q = JOptionPane.showOptionDialog(frame,
                   "\nColourLovers.com Swatch Retrieved:  \n\n"+
                    "     Author: "+authorString+"  \n"
                   +"     Title: "+titleString+"  \n\n",
                    "Colourlovers.com Import",
                   JOptionPane.YES_NO_CANCEL_OPTION,
                   JOptionPane.PLAIN_MESSAGE,
                   null,
                   options,
                   options[1]);

            if(q==1){        
                Alchemy.canvas.swatch.clear();
                int n = 0;
                while (n<colours.size()){
                    Alchemy.canvas.swatch.add(colours.get(n));
                    n++;
                }
                Alchemy.canvas.activeSwatchIndex=0;
            }
        }else{  //We got errors. crap.
            
        }
    }
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
          errorText="Somehow we generated a bad URL. Oops.\n"+
                    "Here it is:"+urlString;
       }catch(IOException  e1){
          errorType=2;
          errorText="Couldn't access the internet.";
       }
       if(colours.isEmpty()&&errorType==0){
          errorType=3;
          errorText="Seems like we connected to Colourlovers.com,\n"+
                    "but found no colours.\n\n"+
                    "Maybe Colourlovers.com is down or changed their API\n\n"+
                    "URL:"+urlString;
       }
    }
    public void importFileSwatch(){
       File file = AlcUtil.showFileChooser();
       if (file==null){
       //    JOptionPane.showMessageDialog(null, "File is not defined.", 
       //                                 "Error", JOptionPane.ERROR_MESSAGE);
       }else if(!file.canRead()){
           JOptionPane.showMessageDialog(null, "Not allowed to read that file.", 
                                        "Error", JOptionPane.ERROR_MESSAGE);
       }else{
           int type=-1;
           try{
               type = getFileType(file);
               if (type==1){
                   //System.out.println("Reading GPL swatch file...");
                   readGPL(file);
               }else if(type==2){
                   //System.out.println("Reading ASE swatch file...");
                   readASE(file);
               }else if(type==-1){
                   JOptionPane.showMessageDialog(null, "File type did not seem valid.\n\n"+
                                        "Please Choose an Adobe Swatch Exchange\nor GIMP Palette file.", 
                                        "Error", JOptionPane.ERROR_MESSAGE);
               }

           }catch (IOException ex) {
                System.err.println(ex);
           } 
       }
    }
    
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
           JOptionPane.showMessageDialog(null, "Swatch is empty, there would be no point really...", 
                                        "Oops...", JOptionPane.ERROR_MESSAGE);
       }else{
           exportDialog eD = new exportDialog();
           eD.setVisible(true);
       }
    }
   
    private class exportDialog extends JDialog{
        
       File f;   
       JTextField fileField = new JTextField(25);
       JComboBox comboTypes;
        
       exportDialog(){

           fileField.setText("Select a File...");
           fileField.setHorizontalAlignment(JTextField.CENTER);
           fileField.setEditable(false);

           String[] fileTypes = {"GIMP Palette (.gpl)","Adobe Swatch Exchange (.ase)"};
           comboTypes = new JComboBox(fileTypes);
           comboTypes.setSelectedIndex(0);

           this.setModalityType(ModalityType.APPLICATION_MODAL);
           this.setDefaultCloseOperation(2);
           this.setSize(400,200);
           this.setTitle("Export Swatch...");
           this.setLocationRelativeTo(null);

           this.setLayout(new BoxLayout(this.getContentPane(), BoxLayout.Y_AXIS));
           Box fileStuff = new Box(BoxLayout.X_AXIS);
           Box fileTypeStuff = new Box(BoxLayout.X_AXIS);
           Box Buttons = new Box(BoxLayout.X_AXIS);

           JLabel chooseFile = new JLabel("Choose File: ");

           AbstractAction launchFileChooser = new AbstractAction() {
               public void actionPerformed(ActionEvent e) {                
                   setFile(AlcUtil.showFileChooser());
                   if(f!=null){
                       setCombo(f.getName());
                       updateFileField(f.getName());
                       fileField.setHorizontalAlignment(JTextField.LEFT);
                   }
               }
           };
           
           AbstractAction okAction = new AbstractAction() {
               public void actionPerformed(ActionEvent e) {               
                   if (f==null){
                       JOptionPane.showMessageDialog(null, "No File Selected.", 
                                                    "Oops...", JOptionPane.ERROR_MESSAGE);
                   }else if(!f.canWrite()){
                       JOptionPane.showMessageDialog(null, "Unable to write to that file.", 
                                                    "Oops...", JOptionPane.ERROR_MESSAGE);
                   }else if(f.exists()){  
                       if(JOptionPane.showConfirmDialog(null, "Overwrite Existing File?", "File Exists",0)==0){
                           writeFile();
                       }
                   }else{
                      writeFile();
                   }
               }
           };
           
           AbstractAction cancelAction = new AbstractAction() {
               public void actionPerformed(ActionEvent e) { 
                   closeDialog();
               }
           };
           
           JButton launchChooser = new JButton();
           launchChooser.setAction(launchFileChooser);
           launchChooser.setText("File...");

           JLabel chooseType = new JLabel("File Type: ");  
           JButton ok = new JButton();
           ok.setAction(okAction);
           ok.setText("OK");
           JButton cancel = new JButton();
           cancel.setAction(cancelAction);
           cancel.setText("CANCEL");

           fileStuff.add(Box.createHorizontalStrut(20));
           fileStuff.add(chooseFile);
           fileStuff.add(Box.createHorizontalStrut(20));
           fileStuff.add(fileField);
           fileStuff.add(launchChooser);
           fileStuff.add(Box.createHorizontalStrut(20));

           fileTypeStuff.add(Box.createHorizontalStrut(20));
           fileTypeStuff.add(chooseType);
           fileTypeStuff.add(Box.createHorizontalStrut(20));
           fileTypeStuff.add(comboTypes);
           fileTypeStuff.add(Box.createHorizontalStrut(20));

           Buttons.add(ok);
           Buttons.add(Box.createHorizontalStrut(40));
           Buttons.add(cancel);

           this.add(Box.createVerticalStrut(20));
           this.add(fileStuff);
           this.add(Box.createVerticalStrut(20));
           this.add(fileTypeStuff);
           this.add(Box.createVerticalStrut(20));
           this.add(Buttons);
           this.add(Box.createVerticalStrut(20));
       }
       private void setFile(File file){
          f = file;
       }
       public File getFile(){
           return f;
       }
       private void updateFileField(String s){
          fileField.setText(s);
       }
       private void setCombo(String s){
           String extensionReg = "[\\d\\D]+?(\\.\\w*)$";
           Pattern extension = Pattern.compile(extensionReg);
           Matcher m;
           int type=0;

           m = extension.matcher(f.getName());
           if (m.matches()){
              if (m.group(1).equals(".gpl")){
                      comboTypes.setSelectedIndex(0);
              }else if(m.group(1).equals(".ase")){
                      comboTypes.setSelectedIndex(1);
              }         
           }           
        }
       public int getExportType(){
           return comboTypes.getSelectedIndex();
       }
       private void closeDialog(){
           this.setVisible(false);
           this.dispose();
       } private void writeFile(){
           if (comboTypes.getSelectedIndex()==0){
               writeGPL(f);
           }else if(comboTypes.getSelectedIndex()==1){
               writeASE(f);
           }
           closeDialog();
       }
    }
    
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
            JOptionPane.showMessageDialog(null, "File didn't contain any colors, or was badly formatted.", 
                                        "Error", JOptionPane.ERROR_MESSAGE);
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
                   //System.out.println("block type "+blockType);
                   blockLength=dis.readInt();
                   //System.out.println("block lenght "+blockLength);

                   // Is this a color block?
                   if(blockType==1){
                       nameLength=dis.readShort();
                       //System.out.println("name length "+nameLength);
                       dis.skipBytes(nameLength*2);
                       dis.read(fourByte);
                       colorMode = decodeUTF8(fourByte);
                       //System.out.println(colorMode+".");
                       if(colorMode.equals("RGB ")){
                           red   = dis.readFloat();
                           green = dis.readFloat();
                           blue  = dis.readFloat();
                           //System.out.println("RGB: "+red+", "+green+", "+blue);
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
                   JOptionPane.showMessageDialog(null, "File didn't contain any colors, or was badly formatted.", 
                                                       "Error", JOptionPane.ERROR_MESSAGE);
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

        
//        
//        EXPERIMENTAL RYB COLOR MODULATION BELOW
//        
        
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
}