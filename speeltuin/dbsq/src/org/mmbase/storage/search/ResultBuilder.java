package org.mmbase.storage.search;

import java.util.*;
import org.mmbase.module.core.*;
import org.mmbase.module.corebuilders.*;


/**
 * A <code>ResultBuilder</code> is a builder for 
 * {@link ResultNode ResultNodes}, that represent the results of executing
 * an arbitrary search query.
 * <p>
 * This builder contains info on the fields of the resultnodes.
 *
 * @author  Rob van Maris
 * @version $Revision: 1.1 $
 * @since MMBase-1.7
 */
// TODO: move to org.mmbase.module.core?
public class ResultBuilder extends VirtualBuilder {
    
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
            fieldsByAlias.put(field.getAlias(), field);
        }
    }
    
    // javadoc is inherited 
    public int getDBType(String fieldName) {
        int result;
        StepField stepField = (StepField) fieldsByAlias.get(fieldName);
        if (stepField == null) {
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
