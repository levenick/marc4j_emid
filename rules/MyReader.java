package org.marc4j.rules;

/*
 * MyReader.java
 * A wrapper for a a BufferedReader including a FileDialog for easy file opening...
 * Created on September 10, 2003, and modified intermittently thereafter
 * @author  jrl
 */
import java.applet.*;
import java.awt.*;
import java.io.*;
import java.net.*;

public class MyReader {

    BufferedReader br;
    String path = "nothing, yet";
    
    public String toString() {
        return "MyReader: filename=" + path;
    }

    /**
     * 
     * @return 
     */
    public String getFilename() {
        return path;
    }      // So you can find out what the pathname was later

    public MyReader() {                                 // default, prompts user for file
        openIt(getPath());
    }

    public MyReader(String filename) {                  // opens the file passed
        if (filename==null) return;
        openIt(filename);
    }
    
    public MyReader(String filename, boolean itsADirectory) {                  // opens the file passed
        openIt(filename, true);
    }
    
    public MyReader(InputStream inputStream) {
    try {
            InputStreamReader isr = new InputStreamReader(inputStream);
            br = new BufferedReader(isr);
        } catch (Exception e) {
            System.out.println("MyReader -- bad inputStream??" + e);
        }
    }

    public MyReader(URL theURL) {
    try {
            InputStreamReader isr = new InputStreamReader(theURL.openStream());
            br = new BufferedReader(isr);
        } catch (Exception e) {
            System.out.println("MyReader -- bad URL??" + e);
        }
    }

    public MyReader(String filename, Applet theApplet) { // opens a file from an Applet!
        try {
            URL theURL = new URL(theApplet.getDocumentBase(), filename);
            InputStreamReader isr = new InputStreamReader(theURL.openStream());

            br = new BufferedReader(isr);
        } catch (Exception e) {
            System.out.println("MyReader -- bad file from net" + e);
        }
    }

    private void openIt(String filename) {
        this.path = filename;       // save the path so you can discover it later (if you want to)
        try {
            br = new BufferedReader(new FileReader(filename));
        } catch (Exception e) {
            System.out.println("MyReader -- can't open " + filename + "!" + e);
            for (Object o: Thread.currentThread().getStackTrace())
                System.out.println(o);
        }
    }

    public String giveMeTheNextLine() {
        try {
            return br.readLine();
        } catch (Exception e) {
            System.out.println("MyReader -- eof?!" + e);
        }
        return "";
    }

    public boolean hasMoreData() {
        try {
            return br.ready();
        } catch (Exception e) {
            System.out.println("MyReader -- disaster!" + e);
        }
        return false;
    }

    public void close() {
        try {
            br.close();
        } catch (Exception e) {
            System.out.println("MyReader -- can't close that!" + e);
        }
    }

    private String getPath() {
        FileDialog fd = new FileDialog(new Frame(), "Select Input File");
        fd.setFile("input");
        fd.show();
        path =  fd.getDirectory() + fd.getFile();  // return the complete path
        return path;
    }
    
    public static void main(String[] args) {
        MyReader mr = new MyReader("/Users/levenick/kelley/kelley2016/data/patterns", true);
        while(mr.hasMoreData()) {
            System.out.println("mr.giveMeTheNextLine() = " + mr.giveMeTheNextLine());
        }
        mr.close();
    }

    private void openIt(String filename, boolean itsADirectory) {
        //do get path, but starting at filename...
        FileDialog fd = new FileDialog(new Frame(), "Select pattern File");
        fd.setFile(filename);
        fd.show();
        filename =  fd.getDirectory() + fd.getFile();  // return the complete path
        openIt(filename);        
    }
}