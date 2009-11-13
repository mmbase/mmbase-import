/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.bridge.jsp.taglib.util;

import javax.servlet.jsp.PageContext;

/**
 * This ContextContainer provides its own 'backing', it is used as 'subcontext' in other contextes.
 *
 * @author Michiel Meeuwissen
 * @version $Id$
 * @since MMBase-1.8
 **/

public class StandaloneContextContainer extends ContextContainer {


    /**
     * A simple map, which besides to itself also registers to page-context.
     */
    protected final BasicBacking backing;

    /**
     * Since a ContextContainer can contain other ContextContainer, it
     * has to know which ContextContainer contains this. And it also
     * has an id.
     */
    public StandaloneContextContainer(PageContext pc, String i, ContextContainer p) {
        super(i, p);
        backing = createBacking(pc);
        // values must fall through to PageContext, otherwise you always must prefix by context, even in it.
    }

    /**
     * @since MMBase-1.9
     */
    public StandaloneContextContainer(String i, java.util.Map<String, Object> values, boolean ignoreEL) {
        super(i, null);
        backing = new BasicBacking(values, ignoreEL);

    }
    protected BasicBacking createBacking(PageContext pc) {
        return new BasicBacking(pc, false);
    }


    public  final Backing getBacking() {
        return backing;
    }

    public void release(PageContext pc, ContextContainer p) {
        super.release(pc, p);
        backing.release();
    }



    protected boolean checkJspVar(String jspvar, String id) {
        return true;
    }

}
