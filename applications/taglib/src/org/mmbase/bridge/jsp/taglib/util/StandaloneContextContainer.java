/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.bridge.jsp.taglib.util;

import org.mmbase.bridge.jsp.taglib.*;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.PageContext;
import javax.servlet.http.*;

import java.util.*;

import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 * This ContextContainer provides its own 'backing', it is used as 'subcontext' in other contextes.
 *
 * @author Michiel Meeuwissen
 * @version $Id: StandaloneContextContainer.java,v 1.1 2004-12-10 19:05:36 michiel Exp $
 * @since MMBase-1.8
 **/

public class StandaloneContextContainer extends ContextContainer {

    
    private Map backing = new HashMap();

    protected  Map getBacking() {
        return backing;        
    }

    /**
     * Since a ContextContainer can contain other ContextContainer, it
     * has to know which ContextContainer contains this. And it also
     * has an id.
     */

    public StandaloneContextContainer(String i, ContextContainer p) {
        super(i, p);
    }


}
