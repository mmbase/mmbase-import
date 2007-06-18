/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.util.jumpers.strategies;

import java.util.Vector;
import java.util.Iterator;

import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

import org.mmbase.module.core.MMObjectNode;
import org.mmbase.module.core.MMObjectBuilder;

import org.mmbase.util.jumpers.JumperCalculator;

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
* @version $Id: ScanStrategy.java,v 1.1 2007-06-18 16:15:25 michiel Exp $
*/

public class ScanStrategy extends JumperStrategy { 

    private static Logger log = Logging.getLoggerInstance(ScanStrategy.class);

    private String[] builders = new String[] {
        "cassettes", "cds", "imagealiases", "urls"
    };

    /** {@inheritDoc} */
    public boolean contains(MMObjectNode node) {
        if(node == null) { 
            throw new IllegalArgumentException("node(" + node + ") is null!");
        }

        for(int i = 0; i < builders.length; i++) { 
            if(node.getBuilder().getTableName().equals(builders[i])) {
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
