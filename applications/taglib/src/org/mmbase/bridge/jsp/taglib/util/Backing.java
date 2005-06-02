/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.bridge.jsp.taglib.util;

import javax.servlet.jsp.PageContext;
import java.util.*;
import org.mmbase.util.Casting;
import org.mmbase.util.transformers.CharTransformer;
import org.mmbase.bridge.jsp.taglib.ContextTag;
import org.mmbase.bridge.jsp.taglib.ContentTag;


/**
 * @author Michiel Meeuwissen
 * @since MMBase-1.8
 * @version $Id: Backing.java,v 1.2 2005-06-02 21:32:07 michiel Exp $
 */

public interface Backing extends Map {
    public Object getOriginal(Object key);
    public boolean containsOwnKey(Object key);
    public void setJspVar(String jspvar, int type, Object value);
}