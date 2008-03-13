/*

 This software is OSI Certified Open Source Software.
 OSI Certified is a certification mark of the Open Source Initiative.

 The license (Mozilla version 1.0) can be read at the MMBase site.
 See http://www.MMBase.org/license

 */
package com.finalist.tree.ajax;

import java.io.*;

import net.sf.mmapps.commons.util.XmlUtil;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.finalist.tree.*;

public class AjaxTree extends Tree {

    protected AjaxTreeCellRenderer cellRenderer;

    public AjaxTree(TreeModel model, AjaxTreeCellRenderer cellRenderer, TreeInfo info) {
        super(model, info);
        this.cellRenderer = cellRenderer;
    }

    public AjaxTreeCellRenderer getCellRenderer() {
        return cellRenderer;
    }

    public void render(PrintWriter out) {
        Document doc = XmlUtil.createDocument();
        Element tree = XmlUtil.createRoot(doc, "tree");

        Object rootNode = getModel().getRoot();
        renderNode(rootNode, tree);
        XmlUtil.createAttribute(tree, "behavior", "classic");

        out.write(XmlUtil.serializeDocument(doc));
    }

    public void renderChildren(PrintWriter out, String root) {
        Document doc = XmlUtil.createDocument();
        Element tree = XmlUtil.createRoot(doc, "tree");

        Object rootNode = getModel().getNode(root);
        int count = getModel().getChildCount(rootNode);
        if (count > 0) {
            for (int i = 0; i < count; i++) {
                Object child = getModel().getChild(rootNode, i);
                Element item = XmlUtil.createChild(tree, "item");
                renderNode(child, item);
            }
        }
        out.write(XmlUtil.serializeDocument(doc));
    }

    private boolean showChildren(Object o) {
        return getInfo().isOpen(o) && !getModel().isLeaf(o); //
    }

    protected void renderNode(Object node, Element element) {
        AjaxTreeElement te = getCellRenderer().getElement(getModel(), node, null);
        te.render(element, getImgBaseUrl());
        if (getModel().isLeaf(node)) {
            XmlUtil.createAttribute(element, "loaded", true);
        }
        else {
            if (showChildren(node)) {
                XmlUtil.createAttribute(element, "loaded", true);
                XmlUtil.createAttribute(element, "open", true);
    
                int count = getModel().getChildCount(node);
                if (count > 0) {
                    for (int i = 0; i < count; i++) {
                        Object child = getModel().getChild(node, i);
                        Element item = XmlUtil.createChild(element, "item");
                        renderNode(child, item);
                    }
                }
            }
            else {
                XmlUtil.createAttribute(element, "loaded", false);
                XmlUtil.createAttribute(element, "open", false);
            }
        }
    }

}
