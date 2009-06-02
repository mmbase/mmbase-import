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
import org.apache.commons.fileupload.disk.DiskFileItem;


/**
 *
 * @author Michiel Meeuwissen
 * @verion $Id: LocalizedStringTest.java,v 1.6 2009-04-30 19:56:25 michiel Exp $
 */
public class SerializableInputStreamTest extends TestCase {



    protected SerializableInputStream getByteArrayInstance() {
        return new SerializableInputStream(new byte[] {0, 1, 2});
    }

    protected String getResourceName() {
        return SerializableInputStreamTest.class.getName().replace(".", "/") + ".class";
    }
    protected SerializableInputStream getInputStreamInstance() throws IOException {
        InputStream is = getClass().getClassLoader().getResourceAsStream(getResourceName());
        if (is == null) throw new Error("Could not find " + getResourceName());
        long size = 0;
        while (is.read() != -1) {
            size++;
        }
        System.out.println("Found " + size + " byte for " + is);
        return new SerializableInputStream(getClass().getClassLoader().getResourceAsStream(getResourceName()), size);
    }

    protected SerializableInputStream getDiskItemInstance() throws IOException {
        DiskFileItem di = new DiskFileItem("file", "application/octet-stream", false, "foobar", 100, new File(System.getProperty("java.io.tmpdir")));
        OutputStream os = di.getOutputStream();
        for (int i = 1; i < 100; i++) {
            os.write( (i % 100) + 20);
        }
        os.close();
        return new SerializableInputStream(di);
    }
    protected SerializableInputStream getDiskItemInstanceBig() throws IOException {
        DiskFileItem di = new DiskFileItem("file", "application/octet-stream", false, "foobar", 100, new File(System.getProperty("java.io.tmpdir")));
        OutputStream os = di.getOutputStream();
        for (int i = 1; i < 10000; i++) {
            os.write( (i % 100) + 20);
        }
        os.close();
        System.out.println("Found size " + di.getSize());
        return new SerializableInputStream(di);
    }

    public void testBasic() {
        SerializableInputStream instance = getByteArrayInstance();
        assertEquals(3, instance.getSize());
        assertNull(instance.getName());
        assertNull(instance.getContentType());

    }

    public void testEquals() throws IOException {
        //assertEquals(new byte[] {0, 1, 2}, new byte[] {0, 1, 2});
        assertEquals(getByteArrayInstance(), getByteArrayInstance());

        SerializableInputStream i = getByteArrayInstance();
        assertTrue(Arrays.equals(new byte[] { 0, 1, 2}, i.get()));
        assertTrue(Arrays.equals(new byte[] { 0, 1, 2}, i.get()));
        assertTrue(Arrays.equals(i.get(), i.get()));
    }



    protected void testSerializable(SerializableInputStream l) throws IOException, java.lang.ClassNotFoundException {
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
        assertTrue(Arrays.equals(l.get(), dl.get()));
    }


    protected void testSerializableMany(SerializableInputStream l) throws IOException, java.lang.ClassNotFoundException {
        byte[] before = l.get();
        testSerializable(l);
        testSerializable(l);
        byte[] after = l.get();
        assertTrue("" + before.length + " " + after.length, Arrays.equals(before, after));
        l.mark(0);
        testSerializable(l);
        testSerializable(l);
        after = l.get();
        assertTrue("" + before.length + " " + after.length, Arrays.equals(before, after));
        File f = File.createTempFile("foo", ".bar");
        l.moveTo(f);
        testSerializable(l);
        testSerializable(l);
        System.out.println("" + f + " of " + l);
        after = l.get();
        assertTrue("" + before.length + " " + after.length, Arrays.equals(before, after));
        l.close();
        l = new SerializableInputStream(new FileInputStream(f), before.length);
        after = l.get();
        assertTrue("" + before.length + " " + after.length, Arrays.equals(before, after));

        assertTrue(l.getSize() > 0);

    }


    public void testSerializableA() throws IOException, ClassNotFoundException {
        SerializableInputStream a = getByteArrayInstance();
        testSerializableMany(a);
    }
    public void testSerializableB() throws IOException, ClassNotFoundException {
        SerializableInputStream b = getInputStreamInstance();
        testSerializableMany(b);

    }
    public void testSerializableC() throws IOException, ClassNotFoundException {
        SerializableInputStream c = getDiskItemInstance();
        testSerializableMany(c);
    }
    public void testSerializableD() throws IOException, ClassNotFoundException {
        SerializableInputStream c = getDiskItemInstanceBig();
        testSerializableMany(c);
    }

    public File testCopy(SerializableInputStream l) throws IOException {
        File f = File.createTempFile("oof", ".bar");
        IOUtil.copy(l, new FileOutputStream(f));
        l.close();
        return f;
    }


    protected void testReset(SerializableInputStream l) throws IOException {
        long length = l.getSize();
        File file1 = testCopy(l);
        assertEquals("" + file1, length, file1.length());
        l.reset();
        File file2 = testCopy(l);
        assertEquals(length, file2.length());
    }

    public void testResetA() throws IOException, ClassNotFoundException {
        testReset(getByteArrayInstance());
    }

    public void testResetB() throws IOException, ClassNotFoundException {
        testReset(getInputStreamInstance());
    }

    public void testResetC() throws IOException, ClassNotFoundException {
        testReset(getDiskItemInstance());
    }
    public void testResetD() throws IOException, ClassNotFoundException {
        testReset(getDiskItemInstanceBig());
    }



}
