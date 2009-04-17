/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.datatypes.handlers.html;

import org.mmbase.datatypes.handlers.*;
import org.mmbase.bridge.*;
import org.mmbase.util.Casting;

/**
 *
 * @author Michiel Meeuwissen
 * @version $Id: TextHandler.java,v 1.2 2009-04-17 16:54:49 michiel Exp $
 * @since MMBase-1.9.1
 */

public class TextHandler extends HtmlHandler  {

    /**
     * @see TypeHandler#htmlInput(Node, Field, boolean)
     */
    @Override
    public String input(Request request, Node node, Field field, boolean search)  {
        StringBuilder show =  new StringBuilder("<input type=\"text\" class=\"small ");
        appendClasses(show, node, field);
        show.append("\" size=\"80\" ");
        //addExtraAttributes(show);
        appendNameId(show, request, field);
        Object value = getFieldValue(request, node, field, ! search);
        show.append("value=\"");
        show.append((value == null ? "" : Casting.toString(value)));
        show.append("\" />");
        return show.toString();
    }

}
