/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.util;
import java.util.*;
import org.junit.*;
import static org.junit.Assert.*;

/**
 *
 * @author Michiel Meeuwissen
 * @verion $Id$
 */
public class SortedBundleTest {


    @Test
    public void enums() {
        Map<String, Object> map = SortedBundle.getConstantsProvider(org.mmbase.framework.WindowState.class);
        assertEquals(3, map.size());

    }

}
