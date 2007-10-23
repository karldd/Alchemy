/**
 * UnZipIt.java - a class for reading files in a zip
 * @author  Yonas Sandbæk
 * @version 1.0
 */

package alchemy;

import processing.core.*;

import java.io.*;
import java.util.zip.*;
import java.awt.MediaTracker;
import java.awt.Toolkit;
import java.awt.Image;

public class UnZipIt {
    PApplet parent;
    File zip;
    
    /**
     * Constructor #1
     * @param X float X-position
     * @param Y float Y-position
     */
    public UnZipIt(String Zipfile, PApplet parent) {
        this.parent = parent;
        parent.registerDispose(this);
        zip = new File(Zipfile);
    }
    
    /**
     * Get Zip
     * @return File zip-file
     */
    public File getZip(){
        // returns a referance to the zip-file
        return zip;
    }
    
    /**
     * Get Zip Entry
     * @return ZipEntry of a requested file
     */
    public ZipEntry getZipEntry(String fname){
        // returns ZipEntry of a requested file
        try {
            // Open the ZIP file
            ZipInputStream in = new ZipInputStream(new FileInputStream(zip.getAbsolutePath()));
            
            // Get the entry that matches
            ZipEntry entry = in.getNextEntry();
            while(entry != null && !entry.getName().toLowerCase().equals(fname.toLowerCase())){
                in.closeEntry();
                entry = in.getNextEntry();
            }
            
            if(entry == null){
                System.out.println("No such file: "+fname);
                return null;
            }
            return entry;
        } catch (IOException e) {
            System.out.println(e);
        }
        return null;
    }
    
    /**
     * Check file exists
     * @return boolean
     */
    public boolean fileExists(String fname){
        // returns ZipEntry of a requested file
        try {
            // Open the ZIP file
            ZipInputStream in = new ZipInputStream(new FileInputStream(zip.getAbsolutePath()));
            
            // Get the entry that matches
            ZipEntry entry = in.getNextEntry();
            while(entry != null && !entry.getName().toLowerCase().equals(fname.toLowerCase())){
                in.closeEntry();
                entry = in.getNextEntry();
            }
            
            if(entry == null){
                //System.out.println("No such file: "+fname);
                return false;
            } else {
                return true;
            }
        } catch (IOException e) {
            System.out.println(e);
        }
        return false;
    }
    
    /**
     * Get Filenames
     * @return String[] filenames
     */
    public String[] getFilenames(){
        // returns a String array of all the files in the zip-file
        try {
            // Open the ZIP file
            ZipInputStream in = new ZipInputStream(new FileInputStream(zip.getAbsolutePath()));
            
            // Get the entry that matches
            String[] ret = new String[0];
            ZipEntry entry = in.getNextEntry();
            while(entry != null){
                ret = parent.append(ret,entry.getName());
                in.closeEntry();
                entry = in.getNextEntry();
            }
            
            return ret;
        } catch (IOException e) {
            System.out.println(e);
        }
        return null;
    }
    
    /**
     * Load a new Zip
     * @return File zip-file
     */
    public void loadZip(String Zipfile){
        // loads a new zip-file
        zip = new File(Zipfile);
    }
    
    /**
     * Load Bytes from a requested file
     * @param fname String Filename Inside Zip
     * @return Byte[] Contents of requested file
     */
    public byte[] loadBytes(String fname){
        // returns a Byte array from the requested file, if it exists inside the zip-file
        try {
            // Open the ZIP file
            ZipInputStream in = new ZipInputStream(new FileInputStream(zip.getAbsolutePath()));
            
            // Get the entry that matches
            ZipEntry entry = in.getNextEntry();
            while(entry != null && !entry.getName().toLowerCase().equals(fname.toLowerCase())){
                in.closeEntry();
                entry = in.getNextEntry();
            }
            
            if(entry == null){
                System.out.println("No such file: "+fname);
                return null;
            }
            
            // Transfer bytes from the ZIP file to the bytearrayoutputstream
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            byte[] buf = new byte[1024];
            int len;
            while ((len = in.read(buf)) > 0) {
                out.write(buf,0,len);
            }
            byte[] ret = out.toByteArray();
            
            // Close the stream
            in.close();
            out.close();
            return ret;
        } catch (IOException e) {
            System.out.println(e);
        }
        return null;
    }
    
    /**
     * Load String from a requested file
     * @param fname String Filename Inside Zip
     * @return String Contents of requested file
     */
    public String loadString(String fname) {
        // returns a String object of the requested files Byte array
        byte[] ret = loadBytes(fname);
        return new String(ret);
    }
    
    /**
     * Load Image from a requested file
     * @param fname String Filename Inside Zip
     * @return PImage Image
     */
    public PImage loadImage(String fname){
        // returns a PImage object of the requested files Byte array
        byte[] ret = loadBytes(fname);
        if(ret != null){
            Image img = Toolkit.getDefaultToolkit().createImage(ret);
            MediaTracker t = new MediaTracker(parent);
            t.addImage(img, 0);
            try {
                t.waitForAll();
            } catch (Exception e) {
                System.out.println(e);
            }
            return new PImage(img);
        }
        return null;
    }
    
    /**
     * Load Font from a requested file
     * @param fname String Filename Inside Zip
     * @return PFont Font
     */
    public PFont loadFont(String fname) {
        // returns a PFont object of the requested files Byte array
        try {
            ZipInputStream in = new ZipInputStream(new FileInputStream(zip.getAbsolutePath()));
            InputStream input;
            
            // Get the entry that matches
            ZipEntry entry = in.getNextEntry();
            while(entry != null && !entry.getName().toLowerCase().equals(fname.toLowerCase())){
                in.closeEntry();
                entry = in.getNextEntry();
            }
            
            if(entry == null){
                System.out.println("No such file: "+fname);
                return null;
            }
            
            if (!entry.getName().toLowerCase().endsWith(".vlw")){
                System.out.println("Only works with fonts ending with .vlw");
                return null;
            }
            
            return new PFont(in);
        } catch (Exception e) {
            System.out.println(e);
        }
        return null;
    }
    
    /**
     * Unpacks a requested file to a requested location
     * @param fname String Filename Inside Zip
     * @param newname String New Filename for the file to be saved
     * @return String Filename of the file saved
     */
    public String unpackFile(String fname, String newname) {
        byte[] ret = loadBytes(fname);
        if(ret != null){
            File output = new File(newname);
            try {
                OutputStream out = new FileOutputStream(output.getAbsolutePath());
                out.write(ret,0,ret.length);
                out.close();
            }catch(Exception e){
                return e.toString();
            }
            return output.getAbsolutePath();
        }
        return null;
    }
    
    /**
     * Dispose
     */
    public void dispose() {
    }
}