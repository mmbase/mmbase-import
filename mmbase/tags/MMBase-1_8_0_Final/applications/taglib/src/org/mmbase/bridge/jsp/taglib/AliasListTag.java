/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.bridge.jsp.taglib;

import java.util.*;

import javax.servlet.jsp.JspTagException;
import org.mmbase.bridge.Node;


/**
 * This class makes a tag which can list the aliases of a Node.
 *
 * @author Michiel Meeuwissen
 * @version $Id: AliasListTag.java,v 1.20 2003-08-05 18:42:55 michiel Exp $ 
 */

public class AliasListTag extends StringListTag implements ListProvider, Writer {

    protected List getList() throws JspTagException {
        Node node = getNode();
        return node.getAliases();
    }
}

