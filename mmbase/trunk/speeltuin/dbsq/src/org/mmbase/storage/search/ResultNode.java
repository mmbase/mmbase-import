package org.mmbase.storage.search;

import java.util.*;
import org.mmbase.module.core.*;
import org.mmbase.module.corebuilders.*;


/**
 * A <code>ResultNode</code> is a virtual node, used to represent 
 * the result of executing an arbitrary search query.
 * <p>
 * The fields of this node correspond to the fields of the
 * result of the query. Consequently, and unlike real a real node, a 
 * <code>ResultNode</code> does not necessarilly have number, 
 * owner or otype fields. 
 * <p>
 * Additionally, the fields of this node can have arbitrary names,
 * specified by the field aliases in the search query.
 * <p>
 * The parent builder of a <code>ResultNode</code> is always a 
 * {@link ResultBuilder ResultBuilder}, that contains info on the node's fields.
 *
 * @author  Rob van Maris
 * @version $Revision: 1.1 $
 * @since MMBase-1.7
 */
// TODO: move to org.mmbase.module.core?
public class ResultNode extends VirtualNode {
    
    /**
     * Contructor.
     *
     * @param parent The node's parent.
     */
    public ResultNode(ResultBuilder parent) {
        super(parent);
    }
    
    // javadoc is inherited
    public int getDBType(String fieldName) {
        return parent.getDBType(fieldName);
    }

}
