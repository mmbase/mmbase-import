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
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 * A helper class for Lists, to implement ContextProvider.
 *
 * @author Michiel Meeuwissen
 * @version $Id: ContextCollector.java,v 1.10 2005-01-04 13:44:43 michiel Exp $
 * @since MMBase-1.7
 */
public class  ContextCollector  {
    private static final Logger log = Logging.getLoggerInstance(ContextCollector.class);
    private ContextContainer contextContainer;
    private Map              collector;

    private ContextProvider  parentTag;


    public ContextCollector(ContextProvider parent) throws JspTagException {
        this.parentTag      = parent;
        contextContainer = new Container(parent.getContextContainer());
        collector        = new HashMap();
    }

    public ContextContainer getContextContainer() {
        return contextContainer;
    }


    public void doAfterBody() throws JspTagException {        
        ContextContainer parent = contextContainer.getParent();

        Iterator keySet = contextContainer.keySet(false).iterator();
        while(keySet.hasNext()) {
            String key = (String) keySet.next();
            if (collector.containsKey(key)) {
                parent.unRegister(key);
            }
        }

        contextContainer.release();

        // now, put the new stuff in:
        parent.registerAll(contextContainer);
     
        // remember what was ours:
        collector.putAll(contextContainer);       

        // and make the container ready for the next iteration
        contextContainer.clear();

    }


    private class Container extends StandaloneContextContainer {
        Container(ContextContainer parent) {
            super(((ContextReferrerTag) ContextCollector.this.parentTag).getPageContext(), null, parent);
        }
        public void unRegister(String key) throws JspTagException {
            super.unRegister(key);
            parent.unRegister(key);

        }
        protected void register(String newid, Object n, boolean check, boolean checkParent) throws JspTagException {
            super.register(newid, n, check, checkParent);
            if (! check) {
                parent.unRegister(newid);
            }

        }
        public String toString() {
            String id = ContextCollector.this.parentTag.getId();
            return "context-collector for tag " + ContextCollector.this.parentTag.getClass() + (id == null ? " (no id)" : " (with id '" + id + "')");

    }
    }

}
