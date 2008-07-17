package com.finalist.cmsc.openoffice.service;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


public class Parserfactory {

    public Node creatNode(Document doc, HashMap hs, Node node) {
        Node NewNode = null;
        String style = null;
        Set tag = hs.keySet();
        Iterator it = tag.iterator();
        while (it.hasNext()) {
            style = it.next().toString();
            Element theNewNode = doc.createElement(style);
            NewNode = theNewNode.cloneNode(true);
            NewNode.appendChild(node.cloneNode(true));
        }
        return NewNode;
    }

    public void process(Document doc, Node node, HashMap hs,Map mapping) {
        HashMap styleMap = new HashMap();
        String nodeName = node.getNodeName();
        if (nodeName.equals("p")) {
            styleMap = changePnode(node, hs);
            Element firstNode = doc.createElement("br");
            Node otherNode = creatNode(doc, styleMap, node);
            if (otherNode != null) {
                firstNode.appendChild(otherNode.cloneNode(true));      
                node.getParentNode().replaceChild(otherNode, node);
            }
        } else if (nodeName.equals("span")) {
            styleMap = changeSpannode(node, hs);

            Node firstNode = creatNode(doc, styleMap, node);
            if (firstNode != null) {
            	node.getParentNode().replaceChild(firstNode, node);
            }
        } else if (nodeName.equals("ul")) {
            String classStyle = node.getAttributes().getNamedItem("class").getNodeValue();
            if (null != hs.get(classStyle)) {
                String style = hs.get(classStyle).toString();
                if (style.equals("ol")) {
                    Element ol = doc.createElement("ol");
                    NodeList childs = node.getChildNodes();
                    for (int i = 0; i < childs.getLength(); i++){
                        ol.appendChild(childs.item(i).cloneNode(true));
                    }
                    node.getParentNode().replaceChild(ol, node);
                }
            }
        } else if (nodeName.equals("table")) {
            Attr border = doc.createAttribute("border");
            border.setValue("1");
            Attr width = doc.createAttribute("width");
            width.setValue("100%");
            Attr cellspacing = doc.createAttribute("cellspacing");
            cellspacing.setValue("1");
            Attr cellpadding = doc.createAttribute("cellpadding");
            cellpadding.setValue("1");
            Element table = doc.createElement("table");
            table.setAttributeNode(border);
            table.setAttributeNode(width);
            table.setAttributeNode(cellspacing);
            table.setAttributeNode(cellpadding);
            NodeList childs = node.getChildNodes();
            for (int i = 0; i < childs.getLength(); i++) {
                table.appendChild(childs.item(i).cloneNode(true));
            }
            node.getParentNode().replaceChild(table, node);
        }
        else if(nodeName.equals("img")){
			String oldMapping = node.getAttributes().getNamedItem("src").getNodeValue();
			String newMapping = mapping.get(oldMapping).toString();
			Element image = doc.createElement("img");
			Attr src = doc.createAttribute("src");
			src.setValue(newMapping);
			image.setAttributeNode(src);
			node.getParentNode().replaceChild(image, node);
		}
    }

    public HashMap changePnode(Node node, HashMap hs) {
        String classStyle = null;
        HashMap styleHs = new HashMap();
        if (node.getAttributes().getNamedItem("class") != null) {
            classStyle = node.getAttributes().getNamedItem("class").getNodeValue();
        }
        String styleValue = null;
        if (hs.get(classStyle) != null) {
            styleValue = hs.get(classStyle).toString();
            styleHs = getTagFormStyle(styleValue);
        }
        return styleHs;
    }

    public HashMap changeSpannode(Node node, HashMap hs) {
        HashMap styleHs = new HashMap();
        String classStyle = "";
        Node styleNode = node.getAttributes().getNamedItem("class");
        if (null != styleNode) {
            classStyle = styleNode.getNodeValue();
        }
        String styleValue = null;
        if (hs.get(classStyle) != null) {
            styleValue = hs.get(classStyle).toString();
            styleHs = getTagFormStyle(styleValue);
        }
        return styleHs;
    }
    public HashMap getTagFormStyle(String style) {
        HashMap tag = new HashMap();
        if (style != null) {
            String bold = String.valueOf(style.charAt(0));
            String italics = String.valueOf(style.charAt(1));
            String underline = String.valueOf(style.charAt(2));
            String throughline = String.valueOf(style.charAt(3));
            String supscript = String.valueOf(style.charAt(4));
            if (bold.equals("1")) {
                tag.put("strong", "strong");
            }

            if (italics.equals("1")) {
                tag.put("em", "em");
            }

            if (underline.equals("1")) {
                tag.put("u", "u");
            }

            if (throughline.equals("1")) {
                tag.put("del", "del");
            }

            if (supscript.equals("1")) {
                tag.put("sup", "sup");

            } else if (supscript.equals("2")) {
                tag.put("sub", "sub");
            }
        }
        return tag;
    }
}
