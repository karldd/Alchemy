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
import javax.swing.JFrame;
import java.io.*;
import java.net.*;
import java.awt.Color;
import java.util.regex.*;
import java.util.ArrayList;
import java.nio.*;
import java.nio.charset.Charset;





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
    
    public void getColourLovers(){

    }
    public void setCLSwatch(int i){
        errorType=0;
        colours.clear();        
        getColourLoversData(i);
        
        JFrame frame = new JFrame();
        
        if(errorType==0){  //no errors         
            
            //Custom button text
            Object[] options = {"Forget It","Sounds Good"};
            int q = JOptionPane.showOptionDialog(frame,
                   "Found a Colourlovers.com palette:\n\n"+
                    "Author: "+authorString+"\n"
                   +"Title: "+titleString,
                    "Colourlovers.com",
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
       //do fileinputsteam here, pass instead of file
       int type = getFileType(file);
       if (type==1){
           readGPL(file);
       }else if(type==2){
           try{
               readASE(file);
           }catch (IOException e) {
               e.printStackTrace();
           }   
       }

    }
    public void exportSwatch(){
       File file = AlcUtil.showFileChooser();
       int type = getFileType(file);
       if (type==1){
           writeGPL(file);
       }else if(type==2){
           writeASE(file);
       }

        
    }
    private int getFileType(File file){
        String extensionReg = "[\\d\\D]+?(\\.\\w*)$";
        Pattern extension = Pattern.compile(extensionReg);
        Matcher m;
        int type=0;
        
        m = extension.matcher(file.getName());
            if (m.matches()){
               if (m.group(1).equals(".gpl")){
                   type = 1;
               }else if(m.group(1).equals(".ase")){
                   type = 2;
               }
               
            }else{
                  
            }
        return type;
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

          // clean up
          fis.close();
          buff.close();


        } catch (FileNotFoundException e) {
          //e.printStackTrace();
        } catch (IOException e) {
          //e.printStackTrace();
        }
        if(!colours.isEmpty()){
            Alchemy.canvas.swatch.clear();
            Alchemy.canvas.activeSwatchIndex=0;
            int n = 0;
            while(n<colours.size()){
                Alchemy.canvas.swatch.add(colours.get(n));
                n++;
            }
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
        } catch (IOException e) {
          e.printStackTrace();
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
                }catch (IOException e) {
                   e.printStackTrace();
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
            System.out.println("trying ase");
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
                
                if (header.equals("ASEF")){
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
                   }
                }
            in.close();
            dis.close();

            } finally {
                if (in != null) {
                    in.close();
                }
                if (dis != null){
                    dis.close();
                }
            }
        }
        
        String decodeUTF8(byte[] bytes) {
            return new String(bytes, UTF8_CHARSET);
        }

        
//        
//        EXPERIMENTAL RYB COLOR MODULATION BELOW
//        

        float cubicInt(float t, float A, float B){
            float weight = t*t*(3-2*t);
            return A + weight*(B-A);
        }
        
        public Color RYBtoRGB(float iR, float iY, float iB){
            float x0=0;// x1, x2, x3, y0, y1, oR, oG, oB;
            float x1=0;
            float x2=0;
            float x3=0;
            float y0=0;
            float y1=0;
            float red=0;
            float green=0;
            float blue=0;
            //red
            x0 = cubicInt(iB, 1.0f, 0.163f);
            x1 = cubicInt(iB, 1.0f, 0.0f);
            x2 = cubicInt(iB, 1.0f, 0.5f);
            x3 = cubicInt(iB, 1.0f, 0.2f);
            y0 = cubicInt(iY, x0, x1);
            y1 = cubicInt(iY, x2, x3);
            red = cubicInt(iR, y0, y1);
            //green
            x0 = cubicInt(iB, 1.0f, 0.373f);
            x1 = cubicInt(iB, 1.0f, 0.66f);
            x2 = cubicInt(iB, 0.0f, 0.0f);
            x3 = cubicInt(iB, 0.5f, 0.094f);
            y0 = cubicInt(iY, x0, x1);
            y1 = cubicInt(iY, x2, x3);
            green = cubicInt(iR, y0, y1);
            //blue
            x0 = cubicInt(iB, 1.0f, 0.6f);
            x1 = cubicInt(iB, 0.0f, 0.2f);
            x2 = cubicInt(iB, 0.0f, 0.5f);
            x3 = cubicInt(iB, 0.0f, 0.0f);
            y0 = cubicInt(iY, x0, x1);
            y1 = cubicInt(iY, x2, x3);
            blue = cubicInt(iR, y0, y1);
            
            Color c = new Color(red,green,blue);
            return c;
        }
        public float hueDegstoWeightedDegs(float hDegs){
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
            return weightBase+(hDegs-base)*((weightCap-weightBase)/(cap-base));
        }
        public float weightedDegstoHueDegs(float wDegs){
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
            //return weightBase+(hDegs-base)*((weightCap-weightBase)/(cap-base));
            return base      +(wDegs-weightBase)*((cap-base)/(weightCap-weightBase));
        }
        public float addWeightedHueDegrees(float hue, float degrees){
            return weightedDegstoHueDegs((hueDegstoWeightedDegs(hue)+degrees)%360);
            
        }
        public float addWeightedHSBDegrees(float hue, float degrees){
            return addWeightedHueDegrees(hue*360,degrees)/360;
            
        }
}