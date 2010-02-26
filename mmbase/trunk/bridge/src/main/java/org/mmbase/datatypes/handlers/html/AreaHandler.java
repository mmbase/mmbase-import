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
 * This is a texthandler that produces multiline input form entries (textarea's).
 *
 * @author Michiel Meeuwissen
 * @version $Id$
 * @since MMBase-1.9.1
 */

public class AreaHandler extends TextHandler {

    private int cols = 80;
    private int rows = -1;

    public void setCols(int c) {
        cols = c;
    }

    public void setRows(int r) {
        rows = r;
    }

    protected int getCols(Field field) {
        return cols;
    }
    protected int getRows(Field field) {
        return rows == -1 ? (field.getMaxLength() > 2048 ? 10 : 5) : rows;
    }

    @Override
    public String input(Request request, Node node, Field field, boolean search) {
        if (search) {
            return super.input(request, node, field, search);
        } else {
            StringBuilder buffer =  new StringBuilder();
            buffer.append("<textarea class=\"");
            buffer.append(field.getMaxLength() > 2048 ? "big " : "small ");
            appendClasses(buffer, node, field);
            buffer.append("\" ");
            appendNameId(buffer, request, field);
            buffer.append(">");
            Object value = getFieldValue(request, node, field, ! search);
            if ("".equals(value)) {
                // This can be needed because:
                // If included, e.g. with xmlhttprequest,
                // the textarea can collaps: <textarea />
                // This does not work in either FF or IE if the contenttype is text/html
                // The more logical contenttype application/xml or text/xml would make it behave normally in FF,
                // but that is absolutely not supported by IE. IE sucks. FF too, but less so.
                //
                // Any how, in short, sometimes you _must_ output one space here if empty otherwise.
                // I _reall_ cannot think of anything more sane then this.
                // e.g. <!-- empty --> would simply produce a textarea containing that...
                // also <![CDATA[]]> produces a textarea containing that...
                //
                // HTML is broken.

                buffer.append(' ');
            } else {
                buffer.append(XML.transform(Casting.toString(value)));
            }
            buffer.append("</textarea>");
            return buffer.toString();
        }
    }
}
