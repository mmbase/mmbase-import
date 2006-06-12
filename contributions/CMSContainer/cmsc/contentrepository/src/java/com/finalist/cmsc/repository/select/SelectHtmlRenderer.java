/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package com.finalist.cmsc.repository.select;

import com.finalist.tree.TreeModel;
import com.finalist.tree.html.HTMLTreeCellRenderer;
import com.finalist.tree.html.HTMLTreeElement;


public class SelectHtmlRenderer extends SelectRenderer implements HTMLTreeCellRenderer {
    
    public SelectHtmlRenderer(String linkPattern, String target) {
        super(linkPattern, target);
    }

    public HTMLTreeElement getElement(TreeModel model, Object node, String id) {
        return (HTMLTreeElement) super.getElement(model, node, id);
    }
    
    protected HTMLTreeElement createElement(String icon, String id, String name, String fragment) {
        return new HTMLTreeElement(icon, id, name, fragment);
    }

}
