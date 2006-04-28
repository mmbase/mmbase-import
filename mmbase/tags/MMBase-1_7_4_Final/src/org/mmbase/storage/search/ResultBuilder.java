/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.storage.search;

import java.util.*;
import org.mmbase.module.core.*;
import org.mmbase.module.corebuilders.*;
import org.mmbase.util.logging.*;

/**
 * A <code>ResultBuilder</code> is a builder for 
 * {@link ResultNode ResultNodes}, that represent the results of executing
 * an arbitrary search query.
 * <p>
 * This builder contains info on the fields of the resultnodes.
 *
 * @author  Rob van Maris
 * @version $Id: ResultBuilder.java,v 1.5 2003-09-02 19:56:52 michiel Exp $
 * @since MMBase-1.7
 */
public class ResultBuilder extends VirtualBuilder {

    private static final Logger log = Logging.getLoggerInstance(ResultBuilder.class);    

    /** Map, maps fields by field alias. */
    private Map fieldsByAlias = null;
    
    /** 
     * Creator.
     * Creates new <code>ResultBuilder</code> instance, used to represent 
     * the results of executing a search query.
     *
     * @param mmbase MMBase instance.
     * @param query The search query that defines the search.
     */
    public ResultBuilder(MMBase mmbase, SearchQuery query) {
        super(mmbase);
        
        // Create fieldsByAlias map.
        List fields = query.getFields();
        fieldsByAlias = new HashMap(16);
        Iterator iFields = fields.iterator();
        while (iFields.hasNext()) {
            StepField field = (StepField) iFields.next();
            String fieldAlias = field.getAlias();
            if (fieldAlias == null) {
                fieldAlias = field.getFieldName();
            }
            fieldsByAlias.put(fieldAlias, field);
        }
    }
    
    // javadoc is inherited 
    public int getDBType(String fieldName) {
        int result;
        StepField stepField = (StepField) fieldsByAlias.get(fieldName);
        if (stepField == null) {
            log.error("not a known stepfield with name " + fieldName + " " + fieldsByAlias);
            result = FieldDefs.TYPE_UNKNOWN;
        } else {
            result = stepField.getType();
        }
        return result;
    }
    
    // javadoc is inherited
    public MMObjectNode getNewNode(String owner) {
        return new ResultNode(this);
    }

}
