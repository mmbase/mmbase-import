/*

  This software is OSI Certified Open Source Software.
  OSI Certified is a certification mark of the Open Source Initiative.

  The license (Mozilla version 1.0) can be read at the MMBase site.
  See http://www.MMBase.org/license

*/

package org.mmbase.util;

import org.junit.*;
import org.springframework.mock.web.*;
import org.springframework.core.io.*;
import javax.servlet.ServletContext;


/**
 *
 * @author Michiel Meeuwissen
 * @version $Id$
 */
public class MMBaseContextTest  {


    @BeforeClass
    public static void setup() throws Exception {
        ServletContext sx = new MockServletContext("/src/test/files/",  new FileSystemResourceLoader()) {
                @Override
                public ServletContext getContext(String uriPath) {
                    return this;
                }
            };
        MMBaseContext.init(sx);
    }



    @Test
    public void getHtmlRoot() throws Exception {
        System.out.println(MMBaseContext.getHtmlRoot());
    }

}
