/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.bridge.jsp.taglib.edit;

import org.mmbase.bridge.Node;

/**
 * To call the method deleteAlias from Node.
 * 
 * @author Michiel Meeuwissen
 * @version $Id: DeleteAliasTag.java,v 1.2 2003-06-06 10:03:20 pierre Exp $
 */

public class DeleteAliasTag extends CreateAliasTag {    
    protected void doJob(Node n, String alias) {
        n.deleteAlias(alias);
    }
}
