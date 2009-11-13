/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.util;

import java.io.File;
import java.io.FilenameFilter;

/**
 * Support utility for MMObjectBuilder.getSMartPath
 * This filter filters files with the specified
 * number in its name.
 *
 * @todo move this code to a SmartPathFunction class?
 * @author Wilbert Hengst
 * @version $Id$
 */
public class SPartFileFilter implements FilenameFilter {

    /**
     * The number to check on.
     * Note: Should be a number, but this is not enforced.
     */
    private String nodeNumber;

    /**
     * Creates the file filter.
     * @param nodeNumber the number to filter on.
     */
    public SPartFileFilter(String nodeNumber) {
        this.nodeNumber = nodeNumber;
    }

    /**
     * Checks whether a file has the node number in its file path.
     * This checks on the exact number - so if the number to search on is '100',
     * If the path contains a number  such as '1001' or '1100' it will return <code>false</code>.
     * @param dir The directory as a File (unused in this filter)
     * @param name The file name to check
     * @return <code>true</code> if the number is in the path, <code>false</code> otherwise.
     */
    public boolean accept(File dir, String name) {
        int pos = name.indexOf(nodeNumber);
        if (pos<0) return false;
        // Check char before found number, if digit return false
        int c;
        if (pos>0) {
            c = name.charAt(pos-1);
            if ((c>='0') && (c<='9')) return false;
        }
        // Check char after found number, if digit return false
        pos+=nodeNumber.length();
        if (pos<name.length()) {
            c = name.charAt(pos);
            if ((c>='0') && (c<='9')) return false;
        }
        return true;
    }
}
