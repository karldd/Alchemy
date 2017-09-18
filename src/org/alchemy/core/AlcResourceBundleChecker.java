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

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Point;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.LinkedHashMap;
import java.util.Set;
import javax.swing.*;

/** Debug class to compare and list missing keys */
class AlcResourceBundleChecker extends JDialog {

    AlcResourceBundleChecker() {

        // Read properties file.
        LinkedHashMap<String, String> rootBundle = new LinkedHashMap<String, String>();
        try {
            URI bundleURI = Alchemy.class.getResource("/org/alchemy/core/AlcResourceBundle.properties").toURI();
            rootBundle = load(new FileInputStream(new File(bundleURI)));

        } catch (Exception ex) {
            System.err.println("Error loading the default resource bundle");
            ex.printStackTrace();
            
        }

        File[] bundleFiles = null;

        try {
            URI bundleURI = Alchemy.class.getResource("/org/alchemy/core").toURI();
            File bundleDir = new File(bundleURI);

            // Get all bundleFiles
            bundleFiles = bundleDir.listFiles(new FilenameFilter() {

                public boolean accept(File dir, String name) {
                    System.out.println(name);
                    return name.startsWith("AlcResourceBundle_") && !name.contains("_en");
                }
            });

            for (int i = 0; i < bundleFiles.length; i++) {
                File file = bundleFiles[i];
                System.out.println(file.getAbsolutePath());

            }

            System.out.println("Total Files: " + bundleFiles.length);

        } catch (URISyntaxException ex) {
            System.err.println("Error scanning for bundles");
            ex.printStackTrace();
        }


        String text = new String();
        Set<String> keySet = rootBundle.keySet();

        if (bundleFiles != null && bundleFiles.length > 0) {

            for (int i = 0; i < bundleFiles.length; i++) {

                try {
                    LinkedHashMap<String, String> bundle = new LinkedHashMap<String, String>();
                    bundle = load(new FileInputStream(bundleFiles[i]));

                    text += bundleFiles[i].getName() + "\n\n";

                    for (String key : keySet) {

                        if (!bundle.containsKey(key) && !key.startsWith("copyright") && !key.startsWith("version")) {
                            text += key + " = " + rootBundle.get(key) + "\n";
                        }
                    }
                    text += "\n---------------\n\n";


                } catch (Exception ex) {
                    System.err.println("Error loading bundle");
                    ex.printStackTrace();

                }
            }

        }



        JPanel contentPanel = new JPanel(new BorderLayout());
        JTextArea textArea = new JTextArea(text, 5, 30);
        JScrollPane scrollPane = new JScrollPane(textArea);
        contentPanel.add(scrollPane, BorderLayout.CENTER);

        this.setPreferredSize(new Dimension(500, 500));
        this.getContentPane().add(contentPanel);
        this.pack();

        Point loc = AlcUtil.calculateCenter(this);
        this.setLocation(loc.x, loc.y);
        this.setVisible(true);



    }

    /** Adapted from Processing:
     * http://dev.processing.org/source/index.cgi/trunk/processing/app/src/processing/app/Preferences.java?view=markup
     */
    static protected LinkedHashMap<String, String> load(InputStream input) throws IOException {
        LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
        String[] lines = loadStrings(input);  // Reads as UTF-8
        for (String line : lines) {
            if ((line.length() == 0) ||
                    (line.charAt(0) == '#')) {
                continue;
            }

            // this won't properly handle = signs being in the text
            int equals = line.indexOf('=');
            if (equals != -1) {
                String key = line.substring(0, equals).trim();
                String value = line.substring(equals + 1).trim();
                map.put(key, value);
            }
        }
        return map;
    }

    /** From Processing:
     *  http://dev.processing.org/source/index.cgi/trunk/processing/core/src/processing/core/PApplet.java?view=markup
     */
    static public String[] loadStrings(InputStream input) {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(input, "UTF-8"));

            String lines[] = new String[100];
            int lineCount = 0;
            String line = null;
            while ((line = reader.readLine()) != null) {
                if (lineCount == lines.length) {
                    String temp[] = new String[lineCount << 1];
                    System.arraycopy(lines, 0, temp, 0, lineCount);
                    lines = temp;
                }
                lines[lineCount++] = line;
            }
            reader.close();

            if (lineCount == lines.length) {
                return lines;
            }

            // resize array to appropriate amount for these lines
            String output[] = new String[lineCount];
            System.arraycopy(lines, 0, output, 0, lineCount);
            return output;

        } catch (IOException e) {
            e.printStackTrace();
            //throw new RuntimeException("Error inside loadStrings()");
        }
        return null;
    }

}
