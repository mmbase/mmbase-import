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

import org.mmbase.bridge.*;
import org.mmbase.storage.search.*;
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
 * @version $Id: Editor.java,v 1.12 2006-07-06 11:36:12 michiel Exp $
 * @see EditTag
 * @see YAMMEditor
 * @since MMBase-1.8
 */
abstract public class Editor {

    private static final Logger log = Logging.getLoggerInstance(Editor.class);

    protected final List queryList  = new ArrayList();      // query List
    protected final List nodenrList = new ArrayList();      // nodenr List
    protected final List fieldList = new ArrayList();       // fieldname List

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
    public final List getQueryList() {
        return queryList;
    }
    public final List  getNodenrList() {
        return nodenrList;
    }
    public final List getFieldList() {
        return fieldList;
    }


    protected  Parameter[] getParameterDefinition() {
        return Parameter.EMPTY;
    }

    /**
     * Should create a link to an editor or some other way to edit the data displayed.
     *
     * @param   context The PageContext
     *
    */
    public abstract void getEditorHTML(PageContext context) throws IOException;

}
