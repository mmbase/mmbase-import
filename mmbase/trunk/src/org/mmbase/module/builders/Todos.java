/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.module.builders;

import org.mmbase.module.core.*;

/**
 * @javadoc
 * @version $Id: Todos.java,v 1.10 2003-03-10 11:50:21 pierre Exp $
 */
public class Todos extends MMObjectBuilder {

    /**
     * @javadoc
     * @language customize output for status field based on language
     */
    private String getStatusString( int status ) {
        switch(status) {
            case 1: return "Described";
            case 2: return "Claimed";
            case 3: return "Researched";
            case 4: return "Underway";
            case 5: return "Stopped";
            case 6: return "Testing";
            case 7: return "Finished";
            case 8: return "Rejected";
            default: return "Unknown";
        }
    }

    /**
     * @javadoc
     * @language customize output for status field based on language
     */
    public String getGUIIndicator(String field,MMObjectNode node) {
        if (field.equals("status")) {
            return getStatusString( node.getIntValue("status"));
        }
        return null;
    }

    /**
     * @deprecated use 'gui(status)' instead of 'showstatus'
     */
    public Object getValue(MMObjectNode node, String field) {
        if (field.equals("showstatus")) {
            return getStatusString( node.getIntValue("status") );
        } else {
            return super.getValue( node, field );
        }
    }
}
