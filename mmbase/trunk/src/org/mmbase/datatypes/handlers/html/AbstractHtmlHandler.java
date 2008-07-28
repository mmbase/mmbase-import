/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.datatypes.handlers.html;

import org.mmbase.datatypes.handlers.*;
import org.mmbase.bridge.*;
import org.mmbase.util.transformers.Xml;
import org.mmbase.util.transformers.CharTransformer;

/**
 * Handlers can be associated to DataTypes, but different Handler can be associated with different
 * content types. The main implementation will of course be one that produces HTML, like forms, and
 * post and things like that.
 *
 * @author Michiel Meeuwissen
 * @version $Id: AbstractHtmlHandler.java,v 1.1 2008-07-28 16:47:31 michiel Exp $
 * @since MMBase-1.9.1
 */

public abstract class AbstractHtmlHandler  extends AbstractHandler<String> {

    protected static final CharTransformer XML = new Xml(Xml.ESCAPE);

    protected void appendClasses(StringBuilder buf, Node node, Field field) {
        buf.append("mm_validate");
        if (field instanceof org.mmbase.bridge.util.DataTypeField) {
            buf.append(" mm_dt_").append(field.getDataType().getName());
        } else {
            buf.append(" mm_f_").append(field.getName()).append(" mm_nm_").append(field.getNodeManager().getName());
        }
        if (node != null) buf.append(" mm_n_").append(node.getNumber());
    }

    protected void appendNameId(StringBuilder buf, Request request, Field field) {
        buf.append("name=\"").append(request.getName(field)).append("\" ");
        buf.append("id=\"").append(id(request.getName(field))).append("\" ");
    }




}
