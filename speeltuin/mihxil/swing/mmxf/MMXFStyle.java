
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
    
    public MMXFStyle() {
        super();

        {
            Style root = addStyle("root", new NamedStyle(getStyle("default")));
            root.addAttribute("xmltag", MMXF.MMXF);
            root.addAttribute("view", "box");
        }
        
        
        {
            Style section = addStyle("section", new NamedStyle(getStyle("default")));
            section.addAttribute("xmltag", MMXF.SECTION); // the tag associated with this style.
            section.addAttribute("view", "box");
        }

        {
            Style sectiontitle = addStyle("sectiontitle", new NamedStyle(getStyle("default")));
            sectiontitle.addAttribute("xmltag", MMXF.SECTION); // the tag associated with this style.
            sectiontitle.addAttribute("view", "paragraph");
            StyleConstants.setBold(sectiontitle, true);        
            StyleConstants.setForeground(sectiontitle, java.awt.Color.red); 
        }
        
        {
            Style p    = addStyle(MMXF.P.getStyle(), new NamedStyle(getStyle("default")));
            StyleConstants.setBold(p, false);        
            StyleConstants.setForeground(p, java.awt.Color.black); 
            StyleConstants.setItalic(p, false);        
            p.addAttribute("xmltag", MMXF.P);
            p.addAttribute("view", "paragraph");
        }

        {
            Style emph    = addStyle(MMXF.EM.getStyle(), new NamedStyle(getStyle("default")));
            StyleConstants.setBold(emph, false);        
            StyleConstants.setForeground(emph, java.awt.Color.black); 
            StyleConstants.setItalic(emph, true);        
            emph.addAttribute("xmltag", MMXF.EM);
            emph.addAttribute("view", "label");
        }

        {
            Style label    = addStyle("label", new NamedStyle(getStyle("default")));
            StyleConstants.setBold(label, false);        
            StyleConstants.setForeground(label, java.awt.Color.black); 
            StyleConstants.setItalic(label, false);        
            label.addAttribute("xmltag", MMXF.NONE);
            label.addAttribute("view", "label");
        }
        
    }

}
