/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.util.images;

import java.util.*;

import org.junit.*;
import static org.junit.Assert.*;

/**
 *
 * @author Michiel Meeuwissen
 */


public class MoreImagingTest {

    @Test
    public void testPredictDimension() {
        assertEquals(new Dimension(300, 150), Imaging.predictDimension(new Dimension(600, 300), Arrays.asList(new String[] {"s(300x300)"})));
        assertEquals(new Dimension(300, 300), Imaging.predictDimension(new Dimension(600, 300), Arrays.asList(new String[] {"s(300x300!)"})));
        assertEquals(new Dimension(30, 30), Imaging.predictDimension(new Dimension(300, 300), Arrays.asList(new String[] {"s(900@)"})));
    }

}
