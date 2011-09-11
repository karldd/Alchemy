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
        FileInputStream fis = null;
        BufferedInputStream bis = null;
        DataInputStream dis = null;
        String colReg = "\\s*?(\\d+?)\\s+?(\\d+?)\\s+?(\\d+?)\\s+?[\\d\\D]*";
        Pattern colLine = Pattern.compile(colReg);
        colours.clear();
        Matcher m;
        String line;
        try {
          fis = new FileInputStream(file);

          // Here BufferedInputStream is added for fast reading.
          bis = new BufferedInputStream(fis);
          dis = new DataInputStream(bis);

          // dis.available() returns 0 if the file does not have more lines.
          while (dis.available() != 0) {
          line = dis.readLine();
          m = colLine.matcher(line);
                 if (m.matches()) {
                     //System.out.println(m.group(1)+" "+m.group(2)+" "+m.group(3)+"\n"+line+"\n\n");
                     colours.add(new Color(Integer.parseInt(m.group(1)),
                                           Integer.parseInt(m.group(2)),
                                           Integer.parseInt(m.group(3))));
                 }
          }

          // dispose all the resources after using them.
          fis.close();
          bis.close();
          dis.close();

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
    public void exportSwatch(){
        File file = AlcUtil.showFileChooser();
        String extensionReg = "[\\d\\D]+?(\\.\\w*)$";
        Pattern extension = Pattern.compile(extensionReg);
        Matcher m;
        
        m = extension.matcher(file.getName());
            if (m.matches()){
               if (m.group(1).equals(".gpl")){
                   writeGPL(file);
               }
               
            }else{
                
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
}