/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.bridge.jsp.taglib.util;

import org.mmbase.bridge.jsp.taglib.ContextProvider;
import org.mmbase.bridge.jsp.taglib.ContextReferrerTag;


import java.util.*;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.PageContext;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 * A helper class for Lists, to implement ContextProvider. This ContextContainer writes every key to
 * it's parent too, so it is 'transparent'.
 *
 * @author Michiel Meeuwissen
 * @version $Id: ContextCollector.java,v 1.11 2005-01-05 20:49:36 michiel Exp $
 * @since MMBase-1.7
 */
public class  ContextCollector extends StandaloneContextContainer {
    private static final Logger log = Logging.getLoggerInstance(ContextCollector.class);
    
    private Set parentCheckedKeys = new HashSet();

    public ContextCollector(ContextProvider p) throws JspTagException {
        super(p.getPageContext(), "CONTEXT-COLLECTOR" + (p.getId() == null ? "" : "-" + p.getId()), p.getContextContainer());
        backing = new BasicBacking() {
                public Object put(Object key, Object value) {
                    if (parentCheckedKeys.contains(key)) {
                        parent.put(key, value);
                    } else {
                        parentCheckedKeys.add(key);
                        try {
                            parent.register((String) key, value);
                        } catch (JspTagException jte) {
                            throw new RuntimeException(jte);
                        }
                    }
                    return super.put(key, value);
                }
            };
    }


    public void unRegister(String key) throws JspTagException {
        super.unRegister(key);
        parent.unRegister(key);
        
    }
    protected void register(String newid, Object n, boolean check, boolean checkParent) throws JspTagException {
        if (! check) {
            parent.unRegister(newid);
        }
        super.register(newid, n, check, checkParent);        

    }

    /**
     * @deprecated
     */
    public ContextContainer getContextContainer() {
        return this;
    }



    public void doAfterBody() throws JspTagException {        
        clear();
    }

}
