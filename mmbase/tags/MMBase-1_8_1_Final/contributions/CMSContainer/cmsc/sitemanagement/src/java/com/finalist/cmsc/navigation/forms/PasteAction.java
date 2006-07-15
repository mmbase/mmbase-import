/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package com.finalist.cmsc.navigation.forms;

import org.mmbase.bridge.Node;

import com.finalist.cmsc.navigation.NavigationUtil;
import com.finalist.cmsc.struts.TreePasteAction;


public class PasteAction extends TreePasteAction {

    protected void copy(Node sourcePage, Node destPage) {
        NavigationUtil.copyPage(sourcePage, destPage);
    }

    protected void move(Node sourcePage, Node destPage) {
        NavigationUtil.movePage(sourcePage, destPage);
    }

}
