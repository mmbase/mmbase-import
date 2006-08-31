package org.mmbase.storage.search.implementation;

import org.mmbase.storage.search.*;

/**
 * Test implementation of the <@link Constraint Constraint> interface, 
 * with fixed support level that is specified when creating an instance.
 *
 * @author Rob van Maris
 * @version $Revision: 1.1 $
 */
public class TestConstraint implements Constraint {
    
    /** The support level. */
    private int support;
    
    /** Creates a new instance of TestConstraint, with the specified support. */
    public TestConstraint(int support) {
        this.support = support;
    }
    
    // javadoc is inherited
    public int getBasicSupportLevel() {
        return support;
    }
    
    // javadoc is inherited
    public boolean isInverse() {
        return false;
    }
    
}
