/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package com.finalist.cmsc.repository.select;

import com.finalist.tree.TreeModel;
import com.finalist.tree.ajax.AjaxTreeCellRenderer;
import com.finalist.tree.ajax.AjaxTreeElement;

public class SelectAjaxRenderer extends SelectRenderer implements AjaxTreeCellRenderer {

    public SelectAjaxRenderer(String linkPattern, String target) {
        super(linkPattern, target);
    }

    public AjaxTreeElement getElement(TreeModel model, Object node, String id) {
        return (AjaxTreeElement) super.getElement(model, node, id);
    }
    
    protected AjaxTreeElement createElement(String icon, String id, String name, String fragment) {
        return new AjaxTreeElement(icon, id, name, fragment);
    }
}
