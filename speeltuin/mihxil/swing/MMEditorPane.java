package org.mmbase.util.swing;

import javax.swing.JEditorPane;

/**
 * A JEditorPane with support for MMBase's XML types. 
 *
 * @author Michiel Meeuwissen
 */
public class MMEditorPane extends JEditorPane {

    MMEditorPane() {
        super();
        // I'd say that it also should be possible to add registerEditorKitForContentType in
        // static{}, but that didnt' work.
        this.setEditorKitForContentType("text/mmxf", new org.mmbase.util.swing.mmxf.MMXFEditorKit());
    }

}
