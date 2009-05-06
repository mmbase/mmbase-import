/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.util;
import org.mmbase.util.xml.*;
import org.w3c.dom.*;
import org.xml.sax.*;
import java.io.*;
import java.util.*;
import junit.framework.TestCase;

/**
 *
 * @author Michiel Meeuwissen
 * @verion $Id: LocalizedStringTest.java,v 1.6 2009-04-30 19:56:25 michiel Exp $
 */
public class SerializableInputStreamTest extends TestCase {

    protected SerializableInputStream getInstance() {
        return new SerializableInputStream(new byte[] {0, 1, 2});
    }

    public void testBasic() {
        SerializableInputStream instance = getInstance();
        assertEquals(3, instance.getSize());
        assertNull(instance.getName());
        assertNull(instance.getContentType());

    }

    public void testEquals() throws IOException {
        //assertEquals(new byte[] {0, 1, 2}, new byte[] {0, 1, 2});
        assertEquals(getInstance(), getInstance());

        SerializableInputStream i = getInstance();
        assertTrue(SerializableInputStream.byteArraysEquals(new byte[] { 0, 1, 2}, i.get()));
        assertTrue(SerializableInputStream.byteArraysEquals(new byte[] { 0, 1, 2}, i.get()));
        assertTrue(SerializableInputStream.byteArraysEquals(i.get(), i.get()));
    }



    public void testSerializable() throws IOException, java.lang.ClassNotFoundException {
        SerializableInputStream l = getInstance();

        // serialize
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(out);
        oos.writeObject(l);
        oos.close();


         //deserialize
        InputStream in = new ByteArrayInputStream(out.toByteArray());
        ObjectInputStream ois = new ObjectInputStream(in);
        SerializableInputStream dl  =  (SerializableInputStream) ois.readObject();

        assertEquals(l, dl);
    }


}
