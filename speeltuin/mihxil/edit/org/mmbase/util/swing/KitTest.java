package org.mmbase.util.swing;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import javax.swing.*;
import javax.swing.text.*;

/**
 * Only a test for editorkits. 
 *
 * @author Michiel Meeuwissen
 */

public class KitTest {

    public static void main(String[] args) throws Exception {

        StringReader in = null;
        String mime = null;;

        if (args.length > 0 ) {
            mime = args[0];             
            if (mime.equals("text/html")) {
                in = new StringReader("<html><head><title>tadaam</title></head><body><h1>hoasdfo</h1>Hier <em>staat</em> dan wel wat text.</body></html>");
            } else if (mime.equals("text/mmxf")) {
                in = new StringReader("<mmxf><section title=\"hoi\"><p>hallo <em>daag</em> goeiendag</p></section></mmxf>");
                //in = new StringReader("<mmxf><p>Paragraaf 1</p><p>Paragraaf 2</p></mmxf>");    
            } 
        }
        if (mime == null) mime = "text/plain";
        if (in   == null) in = new StringReader("<ietsanders>altijd leuk</ietsanders>");

        
        JFrame frame = new JFrame("test for " + mime);
        MMPanel content = new MMPanel(in, mime);        
        frame.setContentPane(content);
        frame.setSize(600, 300);
        frame.setVisible(true);
    }

}
 
