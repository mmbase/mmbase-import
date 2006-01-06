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
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 * You should extend this class to implement your own EditTag. 
 * Create an implementation to serve information about the MMBase (sub)cloud 
 * and/or pages you want to access.
 * 
 * @author Andr&eacute; van Toly
 * @author Michiel Meeuwissen
 * @version $Id: Editor.java,v 1.9 2006-01-06 14:45:47 andre Exp $
 * @see EditTag
 * @see YAMMEditor
 * @since MMBase-1.8
 */
abstract public class Editor {
    
    private static final Logger log = Logging.getLoggerInstance(Editor.class);
    
    protected List queryList  = new ArrayList();      // query List
    protected List nodenrList = new ArrayList();      // nodenr List
    protected List fieldList = new ArrayList();       // fieldname List
    protected List parameters = new ArrayList();
        
    /**
     * @params A List with the parameters of the EditTag.
     */
    public void setParameters(List params) {
       this.parameters = params;
    }    
    public void setQueryList(List qlist) {
        this.queryList = qlist;
    }
    public void setNodenrList(List nrlist) {
        this.nodenrList = nrlist;
    }
    public void setFieldList(List flist) {
        this.fieldList = flist;
    }

    /**
     * Here is were the EditTag registers the lists containing the queries,
     * nodenumbers and fieldnames it received from the FieldTags with the 
     * implementing editor class.
     *
     * @param queryList     List with SearchQuery's that delivered the field
     * @param nodenrList    List with nodenumbers of the node the field belongs to
     * @param fieldList     List with the fieldnames
     */ 
    public abstract void registerFields(List queryList, List nodenrList, List fieldList); 
    
    /**
     * Should create a link to an editor or some other way to edit the data displayed.
     *
     * @param   context The PageContext
     * @return  A String which gives access to the editor
     *
    */
    public abstract void getEditorHTML(PageContext context) throws IOException;
    
}
