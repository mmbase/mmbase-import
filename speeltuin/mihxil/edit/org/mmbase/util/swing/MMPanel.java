package org.mmbase.util.swing;
import org.mmbase.util.swing.xml.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.text.*;
import java.io.*;
import java.util.*;

/**
 * An extension to JPanel, with the necessary buttons.
 *
 * @author Michiel Meeuwissen
 */


public class MMPanel extends JPanel {
    protected MMEditorPane editor; 
    
    protected void createButton(String text, Container buttons, ButtonAction action) {
        JButton but = new JButton(text);
        buttons.add(but);
        but.addActionListener(action);
    }

    /**
     * Adds the buttons to the buttons container
     */
    protected void createButtons(Container buttons, Locale locale) {
        buttons.removeAll();
        ResourceBundle buttonTexts = 
            ResourceBundle.getBundle("org.mmbase.util.swing.resources.buttons", locale);

        createButton(buttonTexts.getString("emph"), buttons, new ButtonAction() {
                public void action() throws Exception {
                    System.out.println("start " + editor.getSelectionStart());
                    System.out.println("end  " + editor.getSelectionEnd());
                    System.out.println("=" + editor.getSelectedText());
                }});
    
        createButton(buttonTexts.getString("a"), buttons, new ButtonAction() {
                public void action() throws Exception {
                    XMLDocument doc = (XMLDocument) editor.getDocument();
                    doc.insertString(0, "[a]", null);
                    editor.select(0, 0);
                    editor.replaceSelection("[a]");
                    
                    
                }});
        createButton(buttonTexts.getString("list"), buttons, new ButtonAction());
        createButton(buttonTexts.getString("dump"), buttons, new ButtonAction() {
                public void action() throws Exception {
                    XMLDocument doc = (XMLDocument) editor.getDocument();
                    doc.dump(System.out);
                }});

        createButton(buttonTexts.getString("toxml"), buttons, new ButtonAction() {
                public void action() throws Exception {
                    XMLDocument doc = (XMLDocument) editor.getDocument();
                    StringWriter string = new StringWriter();
                    if (editor.getSelectedText() != null) {
                        editor.getEditorKit().write(string, doc, editor.getSelectionStart(), editor.getSelectionEnd());
                    } else {
                        editor.getEditorKit().write(string, doc, 0, doc.getLength());
                    }
                    System.out.println(string.toString());                                            
                }});
        createButton("test", buttons, new ButtonAction() {
                public void action() throws Exception {
                    XMLDocument doc = (XMLDocument) editor.getDocument();
                    System.out.println(editor.getSelectedText() + "/" + doc.getText(editor.getSelectionStart(), editor.getSelectionEnd()));
                }});
        createButton(buttonTexts.getString("quit"), buttons, new ButtonAction() {
                public void action() throws Exception {
                    System.exit(0);
                }});
    }

    public MMPanel(Reader in, String mime) throws IOException {
        this(in, mime, new Locale("en", "US")); 
   }
    public MMPanel(Reader in, String mime, Locale locale) throws IOException  {
        super();

        editor = new MMEditorPane();
        editor.setContentType(mime);
        editor.setBackground(Color.white);
        editor.setFont(new Font("Courier", 0, 15));
        editor.setEditable(true);	          
        editor.setCaretColor(Color.red);
        editor.read(in, "my string");
        
        JScrollPane scroller = new JScrollPane();
        JViewport vp = scroller.getViewport();
        vp.add(editor);
        vp.setScrollMode(JViewport.BACKINGSTORE_SCROLL_MODE);
        
        Container buttons = new Container();
        buttons.setLayout(new FlowLayout(FlowLayout.RIGHT));
        createButtons(buttons, locale);

        //getContentPane().setLayout(new ViewPortLayout());
        setLayout(new BorderLayout());
        add("Center", scroller);
        add("North",  buttons);
    }


    class ButtonAction implements ActionListener {
        void action() throws Exception {
            System.out.println("no action defined");
        };
        public void actionPerformed(ActionEvent e) {
            try {
                action();
            } catch (Exception f) {
                System.out.print(f.toString());
            }
        }
    }
    
}
