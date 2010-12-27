/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.util;
import java.io.*;
import java.util.*;
import org.junit.*;
import static org.junit.Assert.*;
import org.apache.commons.fileupload.disk.DiskFileItem;


/**
 *
 * @author Michiel Meeuwissen
 * @verion $Id$
 */
public class SerializableInputStreamTest  {



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
        //System.out.println("Found " + size + " byte for " + is);
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

    protected SerializableInputStream getRandomInstance() throws IOException {
        final int length = 10000;
        return new SerializableInputStream(new RandomInputStream(length), length);
    }
    protected SerializableInputStream getNullInstance() throws IOException {
        final int length = 10000;
        return new SerializableInputStream(new NullInputStream(length), length);
    }

    protected SerializableInputStream getDiskItemInstanceBig() throws IOException {
        DiskFileItem di = new DiskFileItem("file", "application/octet-stream", false, "foobar", 100, new File(System.getProperty("java.io.tmpdir")));
        OutputStream os = di.getOutputStream();
        for (int i = 1; i < 10000; i++) {
            os.write( (i % 100) + 20);
        }
        os.close();
        //System.out.println("Found size " + di.getSize());
        return new SerializableInputStream(di);
    }
    protected File getTestFile() throws IOException {
        File file = File.createTempFile(getClass().getName(), ".testfile");
        OutputStream os = new FileOutputStream(file);
        for (int i = 1; i < 10000; i++) {
            os.write( (i % 100) + 20);
        }
        os.close();
        file.deleteOnExit();
        return file;
    }

    protected SerializableInputStream getFileInstance() throws IOException {
        return new SerializableInputStream(getTestFile());
    }

    @Test
    public void testBasic() {
        SerializableInputStream instance = getByteArrayInstance();
        assertEquals(3, instance.getSize());
        assertNull(instance.getName());
        String contentType = instance.getContentType();
        assertNull(contentType, contentType);
        //assertEquals("unknown/unknown", contentType); // hmm, something changed here

    }

    @Test
    public void testEquals() throws IOException {
        //assertEquals(new byte[] {0, 1, 2}, new byte[] {0, 1, 2});
        assertEquals(getByteArrayInstance(), getByteArrayInstance());

        SerializableInputStream i = getByteArrayInstance();
        assertTrue(Arrays.equals(new byte[] {0, 1, 2}, i.get()));
        assertTrue(Arrays.equals(new byte[] {0, 1, 2}, i.get()));
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


    protected void testSerializableMany(SerializableInputStream l, String id) throws IOException, java.lang.ClassNotFoundException {
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
        final File f = File.createTempFile(getClass().getName() + "." + id + ".", ".many");
        f.deleteOnExit();
        l.moveTo(f);
        testSerializable(l);
        testSerializable(l);
        //System.out.println("" + f + " of " + l);
        after = l.get();
        assertTrue("" + before.length + " " + after.length, Arrays.equals(before, after));
        l.close();
        l = new SerializableInputStream(new FileInputStream(f), before.length);
        after = l.get();
        assertTrue("" + before.length + " " + after.length, Arrays.equals(before, after));

        assertTrue(l.getSize() > 0);

    }


    @Test
    public void testSerializableA() throws IOException, ClassNotFoundException {
        SerializableInputStream a = getByteArrayInstance();
        testSerializableMany(a, "A");
    }
    @Test
    public void testSerializableB() throws IOException, ClassNotFoundException {
        SerializableInputStream b = getInputStreamInstance();
        testSerializableMany(b, "B");

    }
    @Test
    public void testSerializableC() throws IOException, ClassNotFoundException {
        SerializableInputStream c = getDiskItemInstance();
        testSerializableMany(c, "C");
    }
    @Test
    public void testSerializableD() throws IOException, ClassNotFoundException {
        SerializableInputStream c = getDiskItemInstanceBig();
        testSerializableMany(c, "D");
    }
    @Test
    public void testSerializableE() throws IOException, ClassNotFoundException {
        SerializableInputStream c = getFileInstance();
        testSerializableMany(c, "E");
    }

    @Test
    public void testSerializableF() throws IOException, ClassNotFoundException {
        SerializableInputStream c = getRandomInstance();
        testSerializableMany(c, "F");
    }
    @Test
    public void testSerializableG() throws IOException, ClassNotFoundException {
        SerializableInputStream c = getNullInstance();
        testSerializableMany(c, "G");
    }

    public File testCopy(SerializableInputStream l) throws IOException {
        File f = File.createTempFile(getClass().getName(), ".copy");
        IOUtil.copy(l, new FileOutputStream(f));
        //l.close();
        f.deleteOnExit();
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

    @Test
    public void testResetA() throws IOException, ClassNotFoundException {
        testReset(getByteArrayInstance());
    }

    @Test
    public void testResetB() throws IOException, ClassNotFoundException {
        testReset(getInputStreamInstance());
    }

    @Test
    public void testResetC() throws IOException, ClassNotFoundException {
        testReset(getDiskItemInstance());
    }
    @Test
    public void testResetD() throws IOException, ClassNotFoundException {
        testReset(getDiskItemInstanceBig());
    }
    @Test
    public void testResetE() throws IOException, ClassNotFoundException {
        testReset(getFileInstance());
    }

    @Test
    public void testCopyConstructor() throws IOException, ClassNotFoundException  {
        SerializableInputStream is1 = getInputStreamInstance();
        SerializableInputStream is2 = new SerializableInputStream(is1);
        is1.finalize();
        testSerializableMany(is2, "COPY");

    }



}
