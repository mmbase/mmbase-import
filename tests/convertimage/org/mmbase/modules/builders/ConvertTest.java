package org.mmbase.module.builders;

import junit.framework.*;
import java.util.*;
import java.io.*;
import org.mmbase.util.logging.Logging;

/**
 * JUnit tests for convertimage-interface implementation.
 
 * @author  Michiel Meeuwissen 
 * @version $Id: ConvertTest.java,v 1.1 2003-03-11 19:53:41 michiel Exp $
 */
public class ConvertTest extends TestCase {

    ImageConvertInterface imageMagick;
    List images;

    public void testConvert() {
        
    }
    

    /**
     * Sets up before each test.
     */
    public void setUp() throws Exception {
        Logging.configure(System.getProperty("mmbase.config") + File.separator + "log" + File.separator + "log.xml");
        imageMagick = new ConvertImageMagick();
        imageMagick.init(new HashMap()); // defaults should work

        
    }
    
}
    
