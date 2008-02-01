/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package com.finalist.cmsc.navigation;

import java.util.ArrayList;
import java.util.List;

import org.mmbase.bridge.Node;

import com.finalist.tree.TreeInfo;


public class NavigationInfo implements TreeInfo {
    
    protected List<Integer> openItems = new ArrayList<Integer>();

    public NavigationInfo() {
        // Default
    }
    
    public NavigationInfo(NavigationInfo navigationInfo) {
        for (Integer open : navigationInfo.openItems) {
            openItems.add(open);
        }
    }
    
    public void expand(Object o) {
        Integer number = null;
        if (o instanceof Node) {
            Node node = (Node) o;
            number = node.getNumber();
        }
        if (o instanceof Integer) {
            number = (Integer) o;
        }
        if (!openItems.contains(number)) {
            openItems.add(number);
        }
    }

    public void collapse(Object o) {
        Integer number = null;
        if (o instanceof Node) {
            Node node = (Node) o;
            number = node.getNumber();
        }
        if (o instanceof Integer) {
            number = (Integer) o;
        }

        if (openItems.contains(number)) {
            openItems.remove(new Integer(number));
        }
    }

    public boolean isOpen(Object o) {
        if (o instanceof Node) {
            Node node = (Node) o;
            return openItems.contains(node.getNumber());
        }
        if (o instanceof Integer) {
            Integer integer = (Integer) o;
            return openItems.contains(integer);
        }
        return false;
    }

}
