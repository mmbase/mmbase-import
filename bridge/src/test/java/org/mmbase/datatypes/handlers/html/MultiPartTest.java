/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.datatypes.handlers.html;

import java.io.ByteArrayOutputStream;

import javax.servlet.http.HttpServletRequest;

import org.junit.Test;
import org.mmbase.util.SerializableInputStream;

import static org.mockito.Mockito.mock;

/**
 * @version $Id$
 */

public  class MultiPartTest {

    private static final String BOUNDARY = "---------------------------1234";

    protected byte[] getContent() throws Exception {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        out.write('\n');
        out.write(BOUNDARY.getBytes());
        out.write("\nContent-Disposition: form-data; name=\"my_form_handle\"; filename=\"ch.gif'\n".getBytes());
        out.write("Content-Length: 10\n\n".getBytes());
        out.write(new byte[] {1, 2, 3, 4, 5, 6, 7, 8, 9, 10});
        out.write(BOUNDARY.getBytes());
        out.write("--\n".getBytes());
        return out.toByteArray();
    }

    @Test
    public void basic() throws Exception {
        HttpServletRequest request = mock(HttpServletRequest.class);
        //request.setContentType("multipart/form-data;  boundary=" + BOUNDARY);
        //request.setContent(getContent());

        System.out.println("" + request.getInputStream());

        MultiPart.MMultipartRequest r = MultiPart.getMultipartRequest(request, "UTF-8");

        //assertEquals(1, r.parametersMap.size());
        SerializableInputStream is = r.getInputStream("my_form_handle");
        //assertNotNull(is);
        //assertEquals("ch.gif", is.getName());



    }


}
