/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.bridge.jsp.taglib.util;

import javax.servlet.jsp.PageContext;
import java.util.*;


/**
 * Implementation of the 'backing' of a {@link ContextContainer}.
 *
 * @author Michiel Meeuwissen
 * @since MMBase-1.8
 * @version $Id: Backing.java,v 1.10 2008-12-29 11:19:17 michiel Exp $
 */

public interface Backing extends Map<String, Object> {
    /**
     * Get the original value as stored in this Map, so without every wrapping which may have been
     * done.
     */
    public Object getOriginal(String key);
    /**
     * Whether this map contains the given key, but by its own, so not because of possible
     * reflection of another structure (like the page context).
     */
    public boolean containsOwnKey(String key);

    /**
     *
     */
    public void setJspVar(PageContext pc, String jspvar, int type, Object value);

    /**
     * When the container gets used in a different page-context (e.g. because it was stored in the
     * request or in the session), the new one must be pushed. And pulled again when ready.
     */
    public void pushPageContext(PageContext pc);

    /**
     * @see #pushPageContext(PageContext)
     */
    public void pullPageContext(PageContext pc);


    /**
     * @since MMBase-1.8.3
     */
    public PageContext getPageContext();

    /**
     * @since MMBase-1.9
     */
    public Map<String, Object> getOriginalMap();

    /**
     * @since MMBase-1.9.1
     */
    public boolean isELIgnored();
}

