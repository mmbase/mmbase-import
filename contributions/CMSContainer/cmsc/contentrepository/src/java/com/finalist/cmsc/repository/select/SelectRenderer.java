/*

 This software is OSI Certified Open Source Software.
 OSI Certified is a certification mark of the Open Source Initiative.

 The license (Mozilla version 1.0) can be read at the MMBase site.
 See http://www.MMBase.org/license

 */
package com.finalist.cmsc.repository.select;

import java.text.MessageFormat;

import net.sf.mmapps.commons.util.StringUtil;

import org.mmbase.bridge.Node;

import com.finalist.cmsc.mmbase.TreeUtil;
import com.finalist.cmsc.repository.RepositoryUtil;
import com.finalist.tree.*;

public abstract class SelectRenderer implements TreeCellRenderer {

    private String linkPattern;
    private String target;
    
    protected SelectRenderer(String linkPattern, String target) {
        this.linkPattern = linkPattern;
        this.target = target;
    }
    
    /**
     * @see com.finalist.tree.TreeCellRenderer#getElement(TreeModel, Object, String)
     */
    public TreeElement getElement(TreeModel model, Object node, String id) {
        Node parentNode = (Node) node;
        if (id == null) {
            id = String.valueOf(parentNode.getNumber());
        }
        String fragment = parentNode.getStringValue( RepositoryUtil.getFragmentFieldname(parentNode) );

        String name = parentNode.getStringValue("name");
        String icon = getIcon(node);
        TreeElement element = createElement(icon, id, name, fragment);
        Object[] arguments = { parentNode.getNumber(), parentNode.getStringValue(TreeUtil.PATH_FIELD) };
        String link = MessageFormat.format(linkPattern, arguments);
        element.setLink(link);
        if (!StringUtil.isEmpty(target)) {
            element.setTarget(target);
        }
        return element;
    }

    protected abstract TreeElement createElement(String icon, String id, String name, String fragment);

    public String getIcon(Object node) {
        Node n = (Node) node;
        return "type/" + n.getNodeManager().getName() + ".gif";
    }
}
