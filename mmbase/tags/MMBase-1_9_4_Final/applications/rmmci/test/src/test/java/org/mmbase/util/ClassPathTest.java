package org.mmbase.util;

import org.junit.*;
import static org.junit.Assert.*;

public class ClassPathTest {

    @Test
    public void classPath() {
        try {
            Class.forName("org.mmbase.module.core.MMObjectBuilder");
            fail("MMBase core should not be in path for these tests");
        } catch (ClassNotFoundException cnfe) {
        }
    }

}