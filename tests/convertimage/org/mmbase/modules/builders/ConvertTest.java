package org.mmbase.module.builders;
import java.util.*;
import java.io.*;

/**
 * JUnit tests for convertimage-interface implementation.
 
 * @author  Michiel Meeuwissen 
 * @version $Id: ConvertTest.java,v 1.2 2003-03-13 10:53:38 michiel Exp $
 */
public class ConvertTest extends org.mmbase.tests.MMBaseTest {
    protected final int REPEATCOUNT = 10;
    ImageConvertInterface imageMagick;
    List images;

    protected List getResizeArgs() {
        List res = new ArrayList();
        res.add("ignorednodenumber");
        res.add("s(100)");
        return res;
    }

    public void testConvertEmptyImage() {
        for (int i = 0; i < REPEATCOUNT; i ++) {
            System.out.write(i);
            imageMagick.convertImage(new byte[0], getResizeArgs());
        }
    }
    

    /**
     * Sets up before each test.
     */
    public void setUp() throws Exception {
        startLogging("writeinfo.xml");
        imageMagick = new ConvertImageMagick();
        imageMagick.init(new HashMap()); // defaults should work       
    }
    
}
    
