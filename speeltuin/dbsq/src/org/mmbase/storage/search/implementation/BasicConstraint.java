package org.mmbase.storage.search.implementation;

import org.mmbase.storage.search.*;

/**
 * Basic implementation.
 *
 * @author Rob van Maris
 * @version $Revision: 1.1 $
 */
public class BasicConstraint implements Constraint {
    
    /** Inverse property. */
    private boolean inverse = false;
    
    /** Default constructor. */
    protected BasicConstraint() {}
    
    /**
     * Sets inverse.
     *
     * @param invers The inverse value.
     */
    public void setInverse(boolean inverse) {
        this.inverse = inverse;
    }
    
    // javadoc is inherited
    public boolean isInverse() {
        return inverse;
    }
    
    // javadoc is inherited
    public int getBasicSupportLevel() {
        return SearchQueryHandler.SUPPORT_OPTIMAL;
    }
    
    // javadoc is inherited
    public boolean equals(Object obj) {
        // Must be same class (subclasses should override this)!
        if (obj != null && obj.getClass() == getClass()) {
            BasicConstraint constraint = (BasicConstraint) obj;
            return inverse == constraint.isInverse();
        } else {
            return false;
        }
    }
    
    // javadoc is inherited
    public int hashCode() {
        return (inverse? 0: 107);
    }
}
