/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.cache;
import org.mmbase.module.core.MMObjectBuilder;
import java.util.*;

/**
 * The 'blob cache' is used in MMObjectNode to cache small byte-array field values. it is a
 * replacement for the 'handle cache' which was present in MMBase <1.8.
 *
 * @author  Michiel Meeuwissen
 * @version $Id: BlobCache.java,v 1.1 2005-05-02 12:51:42 michiel Exp $
 * @since MMBase 1.8
 */
public abstract class BlobCache extends Cache {

    public BlobCache(int size) {
        super(size);
    }

    protected int getDefaultMaxEntrySize() {
        return 100 * 1024;

    }

    public String getName() {
        return "Blob Cache";
    }

    public String getDescription() {
        return "Node number - field Name-> ByteArray";
    }

    public final String getKey(int nodeNumber, String fieldName) {
        return "" + nodeNumber + '-' + fieldName;
    }
    
    public Object put(Object key, Object value) {        
        if (! isActive()) return null;
        if (value instanceof byte[]) {
            int max = getMaxEntrySize();
            byte[] b = (byte[]) value;
            if (max > 0 && b.length > max) return null;
        } else if (value instanceof String) {
            int max = getMaxEntrySize();
            String s = (String) value;
            if (max > 0 && s.length() > getMaxEntrySize()) return null;            
        }
        return super.put(key, value);
    }


}
