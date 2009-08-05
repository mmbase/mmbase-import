
package org.mmbase.util.swing.mmxf;

import javax.swing.text.*;

import org.mmbase.util.swing.xml.TagType;

/**
 * This class describes how an MMXF xml document is presented.
 *
 *
 * @author Michiel Meeuwissen
 */

public class MMXFStyle extends StyleContext {

    protected void addTagStyle(TagType tt) {

    }
    public MMXFStyle() {
        super();
        Style def = new NamedStyle(getStyle("default"));
        {
            Style root = addStyle("root", def);
            root.addAttribute("xmltag",   MMXF.MMXF);
            root.addAttribute("view", "box");
        }
        
        
        {
            Style section = addStyle("section", def);
            section.addAttribute("xmltag", MMXF.SECTION); // the tag associated with this style.
            section.addAttribute("view", "box");
        }

        {
            Style sectiontitle = addStyle("sectiontitle", def);
            sectiontitle.addAttribute("xmltag", MMXF.SECTION); // the tag associated with this style.
            sectiontitle.addAttribute("view", "paragraph");
            StyleConstants.setBold(sectiontitle, true);        
            StyleConstants.setForeground(sectiontitle, java.awt.Color.red); 
        }
        
        {
            Style p    = addStyle("p", def);
            //StyleConstants.setBold(p, false);        
            //StyleConstants.setForeground(p, java.awt.Color.black); 
            //StyleConstants.setItalic(p, false);        
            p.addAttribute("xmltag", MMXF.P);
            p.addAttribute("view", "box");
        }

        {
            Style emph    = addStyle(MMXF.EM.getStyle(), def);
            StyleConstants.setBold(emph, false);        
            StyleConstants.setForeground(emph, java.awt.Color.blue); 
            StyleConstants.setItalic(emph, true);        
            emph.addAttribute("xmltag", MMXF.EM);
            emph.addAttribute("view", "label");
        }

        {
            Style label    = addStyle("label", def);
            StyleConstants.setBold(label, false);        
            StyleConstants.setForeground(label, java.awt.Color.black); 
            StyleConstants.setItalic(label, false);        
            label.addAttribute("xmltag", MMXF.NONE);
            label.addAttribute("view", "label");
        }
        
    }

}
