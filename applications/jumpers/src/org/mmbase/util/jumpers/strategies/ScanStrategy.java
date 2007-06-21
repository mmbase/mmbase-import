/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.util.jumpers.strategies;

import org.mmbase.module.core.MMObjectNode;

/**
* This is the default scan-strategy.
* It will mimmic the default behaviour as defined in de getDefaultUrl()-methods in the builders.
* This is now implemented in a strategy, so that builders that exists only to implement the
* getDefaultUrl() can be removed.
*
* This strategy handles the following builders:
*
*   - cassettes     - /winkel/cassette.shtml?number
*   - cds           - /winkel/cd.shtml?number
*   - imagealiases  - url-field of the imagealias-node
*   - urls          - url-field of the url-node
*
*
* @see org.mmbase.module.builders.Urls#getDefaultUrl(int)
* 
* This code depends on non-generic builders (as use @vpro), and can only be considered example code
* for others. 
*
* @author Marcel Maatkamp, VPRO Digitaal
* @version $Id: ScanStrategy.java,v 1.2 2007-06-21 16:04:56 nklasens Exp $
*/

public class ScanStrategy extends JumperStrategy { 

    private String[] builders = new String[] {
        "cassettes", "cds", "imagealiases", "urls"
    };

    /** {@inheritDoc} */
    public boolean contains(MMObjectNode node) {
        if(node == null) { 
            throw new IllegalArgumentException("node(" + node + ") is null!");
        }

        for (String element : builders) { 
            if(node.getBuilder().getTableName().equals(element)) {
                return true;
            }
        }
        return false;
    }

    /** {@inheritDoc} */
    public String calculate(MMObjectNode node) { 
        if(node == null) {
            throw new IllegalArgumentException("node("+node+") is null!");
        }

        if(node.getBuilder().getTableName().equals("cassettes")) {
            return "/winkel/cassette.shtml?"+node.getNumber();
        }
        if(node.getBuilder().getTableName().equals("cds")) {
            return "/winkel/cd.shtml?"+node.getNumber();
        }
        if(node.getBuilder().getTableName().equals("imagealiases")) {
            return node.getStringValue("url");
        }

        if(node.getBuilder().getTableName().equals("urls")) {
            return node.getStringValue("url");
        }
        return null;
    }
}
