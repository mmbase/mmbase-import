package org.mmbase.util.swing;

import java.awt.*;
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

        MMEditorPane editor = new MMEditorPane();
        StringReader in = null;
        String mime = null;;


        if (args.length > 0 ) {
            mime = args[0];             
            if (mime.equals("text/html")) {
                in = new StringReader("<html><head><title>tadaam</title></head><body><h1>hoasdfo</h1>Hier <em>staat</em> dan wel wat text.</body></html>");
            } else if (mime.equals("text/mmxf")) {
                //in = new StringReader("<mmxf><section title=\"hoi\">hallo <em>daag</em> goeiendag</section></mmxf>");
                in = new StringReader("<mmxf><p>Paragraaf 1</p><p>Paragraaf 2</p></mmxf>");
            } 
        }
        if (mime == null) mime = "text/plain";
        if (in   == null) in = new StringReader("<ietsanders>altijd leuk</ietsanders>");


        try {
            editor.setContentType(mime);
            editor.setBackground(Color.white);
            editor.setFont(new Font("Courier", 0, 15));
            editor.setEditable(true);	                  
            editor.read(in, "my string");

            ((AbstractDocument) editor.getDocument()).dump(System.out);
            
            Writer out = new OutputStreamWriter(System.out);                
            editor.write(out);
            out.flush();
            
            JScrollPane scroller = new JScrollPane();
            JViewport vp = scroller.getViewport();
            vp.add(editor);
            vp.setBackingStoreEnabled(true);
            
            JFrame f = new JFrame("EditorKit for " + mime);
            f.getContentPane().setLayout(new BorderLayout());
            f.getContentPane().add("Center", scroller);
            f.pack();
            f.setSize(600, 600);
            f.setVisible(true);
        } catch (Throwable e) {
            e.printStackTrace();
            System.out.println("heeeeee");
            System.exit(1);
        }    
    }

}
