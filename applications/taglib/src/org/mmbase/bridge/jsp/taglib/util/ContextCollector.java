/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.bridge.jsp.taglib.util;


import java.util.*;
import javax.servlet.jsp.JspTagException;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 * A helper class for Lists, to implement ContextProvider.
 *
 * @author Michiel Meeuwissen
 * @version $Id: ContextCollector.java,v 1.3 2003-08-12 10:34:41 michiel Exp $
 * @since MMBase-1.7
 */
public class  ContextCollector  {
    private static Logger log = Logging.getLoggerInstance(ContextCollector.class);
    private ContextContainer contextContainer;
    private Map              collector;

    public ContextCollector(ContextContainer parent) {
        contextContainer = new ContextContainer(null, parent);
        collector        = new HashMap();
    }

    public ContextContainer getContextContainer() {
        return contextContainer;
    }

    public void doAfterBody() throws JspTagException {        

        ContextContainer parent = contextContainer.getParent();

        Iterator keySet = contextContainer.myKeySet().iterator();
        while(keySet.hasNext()) {
            String key = (String) keySet.next();
            if (collector.containsKey(key)) {
                parent.unRegister(key);
            }
        }
        // now, put the new stuff in:
        parent.registerAll(contextContainer);

        // remember what was ours:
        collector.putAll(contextContainer);       

        // ane make the container ready for the next iteration
        contextContainer.clear();

    }

    

}
