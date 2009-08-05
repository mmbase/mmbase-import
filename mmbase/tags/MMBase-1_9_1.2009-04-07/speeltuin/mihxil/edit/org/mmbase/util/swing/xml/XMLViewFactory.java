package org.mmbase.util.swing.xml;

import javax.swing.text.*;

/**
 * Base class for XML ViewFactories.
 *
 * @author Michiel Meeuwissen
 */

public class XMLViewFactory implements ViewFactory  {

    /**
     * Creates a view from an element.
     *
     * @param elem the element
     * @return the view
     */
    public View create(Element elem) {
        System.out.println("\nxmlviewfactory\n--- Creating View for -- " + elem + " " + (elem.isLeaf() ? "leaf" : "branch"));
        String kind = (String) elem.getAttributes().getAttribute("view");
        System.out.println("kind: " + elem + ":" + kind);
       
        if (kind != null) {            
            if (kind.equals("label")) {
                System.out.println("label view " + kind);
                return new LabelView(elem);
            } else if (kind.equals("paragraph")) {
                System.out.println("paragraph view " + kind);
                return new ParagraphView(elem);
                //return new LabelView(elem);
            } else if (kind.equals("box")) {
                System.out.println("box view ");
                return new BoxView(elem, View.Y_AXIS);
            } 
        }
        System.out.println("return default Label view");
       
        // default to text display
        return new LabelView(elem);
       
    }

}
