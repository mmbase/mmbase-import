package com.finalist.cmsc.openoffice;

import com.finalist.cmsc.openoffice.service.OdtFileTranster;
import junit.framework.TestCase;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

public class ODTFileTransforTest extends TestCase {

    public void testSaveAllImageToCMSC() {
        try {
            InputStream fis = this.getClass().getResourceAsStream("test.odt");
            Map map = OdtFileTranster.saveAllImageToCMSC(fis,"");
            assertEquals(1, map.size());
        } catch (IOException e) {
            e.printStackTrace();
            fail("Exception throws");
        }
    }

}
