/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.util;

import java.util.*;
import org.mmbase.module.core.*;

/**
 * This class implements the Comparator interface for comparing MMObjectNodes.
 * At forehand you specify in which fields a specified nodes should be compared,
 * these fields may not have a null value.
 *
 * @application Tools
 * @author Pierre van Rooden
 * @version $Id$
 */
public class NodeComparator implements Comparator<MMObjectNode> {

    public final static String UP = "UP";
    public final static String DOWN = "DOWN";

    private List<String> fields;
    private List<String> sortDirs;

    /**
     * Simple constructor that uses the default sort order (UP).
     * @param fields the fields on which the message nodes get compared.
     */
    public NodeComparator(List<String> fields) {
        this.fields = fields;
        sortDirs = new Vector<String>(fields.size());
    }

    /**
     * Constructor in which you specify the sort order (UP or DOWN) per field.
     * @param fields the fields on which the message nodes get compared.
     * @param sortDirs the sort directions (UP or DOWN) for each field.
     */
    public NodeComparator(List<String> fields, List<String> sortDirs) {
        this.fields = fields;
        this.sortDirs = sortDirs;
        for (int i = sortDirs.size(); i < fields.size(); i++) {
            sortDirs.add(UP);
        }
    }

    /**
     * The two message nodes will be compared using the compare function of
     * the values of their fields.
     * Only Comparable values can be used (String, Numbers, Date), as well as
     * Boolean values.
     * In other cases it's assumed that the values cannot be ordered.
     * <br />
     * Note: this class assumes that values in fields are of similar types
     * (comparable to each other).
     *
     * @param o1 the first object to compare
     * @param o2 the second object to compare
     * @return 0 if both objects are equal, -1 if object 1 is 'less than'
     *    object 2, and +1 if object 1 is 'greater than' object 2.
     */
    public int compare(MMObjectNode o1, MMObjectNode o2) {
        Object f1, f2;
        int result=0;
        int fieldnr = 0;
        String field;
        while ((result == 0) && (fieldnr < fields.size())) {
            field = fields.get(fieldnr);
            f1 = o1.getValue(field);
            f2 = o2.getValue(field);
            if (f1 instanceof Comparable) {
                try {
                    result=((Comparable)f1).compareTo(f2);
                } catch (ClassCastException e) {
                    // types do not compare -
                    // possibly the in-memory value type differs from the
                    // database value type (this can occur if you use setValue
                    // with a deviating type).
                    // Solving this could bring this compare to a crawl, so we
                    // don't. Just edit stuff the right way.
                }
            } else if (!f1.equals(f2)) {
                if (f1 instanceof Boolean) {
                    result=((Boolean)f1).booleanValue() ? 1 : -1;
                }
            }
            fieldnr++;
        }
        if ((fieldnr>0) &&
            (fieldnr<=sortDirs.size()) &&
            sortDirs.get(fieldnr-1).equals(DOWN)) {
            result=-result;
        }
        return result;
    }

    /**
     * Returns whether another object is equal to this comparator (that is,
     * compare the same fields in the same order).
     * @param obj the object to compare
     * @return <code>true</code> if the objects are equal
     * @throws ClassCastException
     */
    public boolean equals(Object obj) {
        if (obj instanceof NodeComparator) {
            return (obj.hashCode()==hashCode());
        } else {
            throw new ClassCastException();
        }
    }

    /**
     * Returns the comparator's hash code.
     * @see java.lang.Object#hashCode()
     */
    public int hashCode() {
        return fields.hashCode()^sortDirs.hashCode();
    }
}
