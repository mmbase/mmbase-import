/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.storage.search;

import java.util.*;
import org.mmbase.bridge.*;

/**
 * How there is dealt with SearchQuery object may depend a bit on the actual implementation of the bridge and storage.
 * This is collected in implemetnations of this interface
 *
 *
 * @author Michiel Meeuwissen
 * @version $Id: Step.java 36486 2009-06-29 17:33:41Z michiel $
 * @since MMBase-2.0
 */
public interface QueryContext {

    String getStorageIdentifier(String s);
    Field  getField(String builder, String fieldName);


    public static class Bridge implements QueryContext {

        protected final Cloud cloud;
        public Bridge(Cloud c) {
            cloud = c;
        }
        public  String getStorageIdentifier(String s) {
            return s;
        }
        public  Field getField(String builder, String fieldName) {
            return cloud.getNodeManager(builder).getField(fieldName);
        }
    }


}
