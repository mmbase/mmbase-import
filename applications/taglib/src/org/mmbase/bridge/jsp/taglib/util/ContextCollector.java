/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.bridge.jsp.taglib.util;


import java.util.Map;
import java.util.HashMap;
import javax.servlet.jsp.JspTagException;

/**
 * A helper class for Lists, to implement ContextProvider.
 *
 * @author Michiel Meeuwissen
 * @version $Id: ContextCollector.java,v 1.1 2003-08-08 13:30:03 michiel Exp $
 * @since MMBase-1.7
 */
public class  ContextCollector  {

    private ContextContainer contextContainer;
    private Map             collector;

    public ContextCollector(ContextContainer parent) {
        contextContainer = new ContextContainer(null, parent);
        collector        = new HashMap();
    }

    public ContextContainer getContainer() {
        return contextContainer;
    }

    public void doAfterBody() throws JspTagException {        

        // first remove everything this list added itself
        contextContainer.getParent().unRegisterAll(collector);

        // now, put the new stuff in:
        contextContainer.getParent().registerAll(contextContainer);

        // remember what was ours:
        collector.putAll(contextContainer);       

        // ane make the container ready for the next iteration
        contextContainer.clear();

    }

    

}
