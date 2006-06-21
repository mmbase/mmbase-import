/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package com.finalist.cmsc.navigation;

import javax.servlet.http.HttpServletRequest;

import com.finalist.tree.TreeModel;
import com.finalist.tree.TreeOption;
import com.finalist.tree.html.*;


public class NavigationHtmlRenderer extends NavigationRenderer implements HTMLTreeCellRenderer{

    private boolean optionLabel = false;
    private boolean hideIcons = false;
    private String spanClass = "navigation";

    @Override
    public HTMLTreeElement getElement(TreeModel model, Object node, String id) {
        return (HTMLTreeElement) super.getElement(model, node, id);
    }
    
    public NavigationHtmlRenderer(HttpServletRequest request, String target, boolean hideIcons) {
        super(request, target);
        this.hideIcons = hideIcons;
        this.optionLabel = hideIcons;
    }

    public NavigationHtmlRenderer(HttpServletRequest request, String target) {
        super(request, target);
    }
    
    protected TreeOption createOption(String icon, String label, String action, String target) {
        return new HTMLTreeOption(icon, label, action, target, optionLabel);
    }

    protected HTMLTreeElement createElement(String icon, String id, String name, String fragment, String action, String target) {
        HTMLTreeElement element;
        if (hideIcons) {
            element = new PopitElement(icon, id, name, fragment, spanClass);
        }
        else {
            element = new HTMLTreeElement(icon, id, name, fragment, spanClass);
        }
        element.setLink(action);
        element.setTarget(target);

        return element;
    }

}
