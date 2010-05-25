/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.bridge.jsp.taglib.editor;

import java.io.IOException;
import java.util.*;

import javax.servlet.jsp.PageContext;

import org.mmbase.bridge.Query;
import org.mmbase.util.functions.*;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 * You should extend this class to implement your own EditTag.
 * Create an implementation to serve information about the MMBase (sub)cloud
 * and/or pages you want to access.
 *
 * @author Andr&eacute; van Toly
 * @author Michiel Meeuwissen
 * @version $Id$
 * @see EditTag
 * @see YAMMEditor
 * @since MMBase-1.8
 */
abstract public class Editor {

    private static final Logger log = Logging.getLoggerInstance(Editor.class);

    protected final List<Query> queryList  = new ArrayList<Query>();      // query List
    protected final List<String> nodenrList = new ArrayList<String>();      // nodenr List
    protected final List<String> fieldList = new ArrayList<String>();       // fieldname List

    protected Parameters parameters = null;

    /**
     * @return A List with the parameters of the EditTag.
     */
    public final Parameters getParameters() {
        if (parameters == null) {
            parameters = new Parameters(getParameterDefinition());
            parameters.setAutoCasting(true);
        }
        return parameters;
    }
    public final List<Query> getQueryList() {
        return queryList;
    }
    public final List<String>  getNodenrList() {
        return nodenrList;
    }
    public final List<String> getFieldList() {
        return fieldList;
    }

    /**
     * Here is were the FieldTag registers its fields and some associated
     * and maybe usefull information with the EditTag.
     *
     * @param query     SearchQuery object that delivered the field
     * @param nodenr    int with the number of the node the field belongs to
     * @param fieldName String with the fieldname
     */
    public void registerField(Query query, int nodenr, String fieldName) {
        if (log.isDebugEnabled()) {
            log.debug("nodenr: " + nodenr);
            log.debug("fieldName: " + fieldName);
            log.debug("query: " + query);
        }
        queryList.add(query);
        nodenrList.add(String.valueOf(nodenr));
        fieldList.add(fieldName);
    }


    protected  Parameter[] getParameterDefinition() {
        return Parameter.emptyArray();
    }

    /**
     * Should create a link to an editor or some other way to edit the data displayed.
     *
     * @param   context The PageContext
     *
    */
    public abstract void getEditorHTML(PageContext context) throws IOException;

}
