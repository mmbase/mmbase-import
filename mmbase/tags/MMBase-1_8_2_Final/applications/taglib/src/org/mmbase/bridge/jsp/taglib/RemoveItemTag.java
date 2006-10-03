/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.bridge.jsp.taglib;

import javax.servlet.jsp.JspTagException;

/**
 * Removes the current item from the list. In a 'reuse' of the list, the item will not be present any
 * more.
 *
 * @author Michiel Meeuwissen
 * @version $Id: RemoveItemTag.java,v 1.4 2005-01-30 16:46:35 nico Exp $ 
 */

public class RemoveItemTag extends ListReferrerTag {

    public int doStartTag() throws JspTagException{
        ListProvider list = getList();
        list.remove();
        return SKIP_BODY;
    }


}
