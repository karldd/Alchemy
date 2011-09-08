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

public class AlcColourLovers implements AlcConstants{
    private Color c;  
    private String titleReg = "\\s*?<title><!\\[CDATA\\[(.+?)\\]\\]><\\/title>\\s*?";
    private String authorReg = "\\s*?<userName><!\\[CDATA\\[(.+?)\\]\\]><\\/userName>\\s*?";
    private String hexReg = "\\s*?<hex>(\\S+?)<\\/hex>\\s*?";
    private Pattern title = Pattern.compile(titleReg);
    private Pattern author = Pattern.compile(authorReg);
    private Pattern hex = Pattern.compile(hexReg);
    private String titleString;
    private String authorString;
    private String hexString;
    private ArrayList<Color> colours;
    
    AlcColourLovers(){
        c = new Color(255,0,255);
        hexString = "FFFFFF";
        colours = new ArrayList<Color>();
    }
    
    public void getColourLovers(){

    }
    public void setSwatch(int i){
        colours.clear();
        getColourLoversData(i);
        
        JFrame frame = new JFrame();
        
        //Custom button text
        Object[] options = {"Forget It","Sounds Good"};
        int q = JOptionPane.showOptionDialog(frame,
               "Found a Colourlovers.com palette:\n\n"+
                "Author: "+authorString+"\n"
               +"Title: "+titleString,
                "Fetching Colourlovers.com Palette",
               JOptionPane.YES_NO_CANCEL_OPTION,
               JOptionPane.PLAIN_MESSAGE,
               null,
               options,
               options[1]);
        
        if(q==1){
            if(!colours.isEmpty()){
                Alchemy.canvas.swatch.clear();
                int n = 0;
                while (n<colours.size()){
                    Alchemy.canvas.swatch.add(colours.get(n));
                    n++;
                }
                Alchemy.canvas.activeSwatchIndex=0;
            }
        }
    }
    private void getColourLoversData(int i){
       System.out.println(hexReg);
       String nextLine;  
       URL url = null;
       URLConnection urlConn = null;
       InputStreamReader inStream = null;
       BufferedReader buff = null;
       Matcher m;
       try{
          url  = new URL("http://www.colourlovers.com/api/palettes/top?numResults=1&resultOffset="+
                          Integer.toString(i) );
          urlConn = url.openConnection();
          inStream = new InputStreamReader( 
                           urlConn.getInputStream());
          buff= new BufferedReader(inStream);
        
          // Read and print the lines from index.html
          while (true){
             nextLine =buff.readLine();  
             if (nextLine !=null){
                
               m = title.matcher(nextLine);
               if (m.matches()) {
                  titleString = m.group(1);
                  System.out.println("found "+titleString);
               }
               m = author.matcher(nextLine);
               if (m.matches()) {
                  authorString = m.group(1);
                  System.out.println("found "+authorString);
               }
               m = hex.matcher(nextLine);
               if (m.matches()) {
                  
                  colours.add(Color.decode("#"+m.group(1)));
                   
                  System.out.println("found "+hexString);
               }
               hexString = "000000";
               System.out.println(nextLine);
                
             }else{
               break;
             } 
          }
       }catch(MalformedURLException e){
          System.out.println("Please check the URL:" + 
                                           e.toString() );
       }catch(IOException  e1){
          System.out.println("Can't read from the Internet: "+ 
                                          e1.toString() );  // end of 'finally' clause
       }
    }
    
}