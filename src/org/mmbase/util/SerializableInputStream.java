/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.util;

import java.io.*;
import org.mmbase.util.logging.*;
import org.apache.commons.fileupload.FileItem;

/**
 * Sometimes you need an InputStream to be Serializable. This wraps
 * another InputStream, or some other representation of a 'binary'.
 *
 * @since MMBase-1.9
 * @author Michiel Meeuwissen
 * @version $Id$
 * @todo IllegalStateException or so, if the inputstreas is used (already).
 */

public class SerializableInputStream  extends InputStream implements Serializable  {

    private static final long serialVersionUID = 2L;


    private static final Logger log = Logging.getLoggerInstance(SerializableInputStream.class);

    private long size;


    public static byte[] toByteArray(InputStream stream) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
            byte[] buf = new byte[1024];
            int n;
            while ((n = stream.read(buf)) > -1) {
                bos.write(buf, 0, n);
            }
        } catch (IOException ioe) {
            log.error(ioe);
        }
        return bos.toByteArray();
    }



    private File file = null;
    private long fileMark = 0;
    private boolean tempFile = true;
    private String name;
    private String contentType;
    private transient InputStream wrapped;
    private boolean used = false;

    public SerializableInputStream(InputStream wrapped, long s) {
        this.wrapped = wrapped;
        this.size = s;
        this.name = null;
        if (wrapped == null) throw new NullPointerException();
    }

    public SerializableInputStream(byte[] array) {
        wrapped = new ByteArrayInputStream(array);
        this.size = array.length;
        this.name = null;
    }

    public SerializableInputStream(FileItem fi) throws IOException {
        this.size = fi.getSize();
        this.name = fi.getName();
        this.contentType = fi.getContentType();
        file = File.createTempFile(getClass().getName(), this.name);
        file.deleteOnExit();
        try {
            fi.write(file);
        } catch (Exception e) {
            IOException ioe = new IOException();
            ioe.initCause(e);
            throw ioe;
        }
        this.wrapped = new FileInputStream(file);


    }



    private void use() {
        if (! used) {
            if (log.isTraceEnabled()) {
                log.trace("Using " + this + " because ", new Exception());
            }
            used = true;
            if (! wrapped.markSupported() && file == null) {
                supportMark();
            }
        }
    }


    public long getSize() {
        return size;
    }
    public String getName() {
        return name;
    }

    public String getContentType() {
        return contentType;
    }
    public byte[] get() throws IOException {
        if (wrapped == null) throw new IllegalStateException();
        if (wrapped.markSupported()) {
            byte[] b =  toByteArray(wrapped);
            wrapped.reset();
            return b;
        } else {
            byte[] b =  toByteArray(wrapped);
            wrapped = new ByteArrayInputStream(b);
            return b;
        }
    }

    public void moveTo(File f) {
        if (name == null) {
            name = f.getName();
        }
        log.debug("Moving file to " + f);
        if (file != null) {
            if (file.equals(f)) {
                log.debug("File is already there " + f);
                return;
            } else if (file.renameTo(f)) {
                log.debug("Renamed " + file + " to " + f);
                file = f;
                tempFile = false;
                return;
            } else {
                log.debug("Could not rename " + file + " to " + f + " will copy/delete in stead");
            }
        }
        try {
            FileOutputStream os = new FileOutputStream(f);
            IOUtil.copy(wrapped, os);
            os.close();
            wrapped = new FileInputStream(f);
            if (file != null) {
                file.delete();
            }
            file = f;
            tempFile = false;
        } catch (IOException ioe) {
            throw new RuntimeException(ioe);
        }
    }

    private void writeObject(java.io.ObjectOutputStream out) throws IOException {
        out.writeObject(get());
        out.writeObject(name);
        out.writeObject(contentType);
    }
    private void readObject(java.io.ObjectInputStream oin) throws IOException, ClassNotFoundException {
        byte[] b = (byte[]) oin.readObject();
        wrapped = new ByteArrayInputStream(b);
        size = b.length;
        name = (String) oin.readObject();
        contentType = (String) oin.readObject();

    }

    private FileInputStream supportMark() {
        try {
            assert file == null;
            file = File.createTempFile(getClass().getName(), this.name);
            file.deleteOnExit();
            FileOutputStream os = new FileOutputStream(file);
            IOUtil.copy(wrapped, os);
            os.close();
            FileInputStream fis = new FileInputStream(file);
            wrapped = fis;
            System.out.println("Created " + fis + "" + file.length());
            return fis;
        } catch (IOException ioe) {
            throw new RuntimeException(ioe);
        }

    }


    @Override
    public void finalize() {
        if (file != null && tempFile) {
            log.debug("Deleting " + file);
            file.delete();
        }
    }
    @Override
    public void close() throws IOException {
        wrapped.close();
    }


    @Override
    public void mark(int readlimit) {
        log.debug("Marking" + wrapped, new Exception());

        if (wrapped.markSupported()) {
            wrapped.mark(readlimit);
            return;
        }
        try {
            FileInputStream fis =
                file != null ?  (FileInputStream) wrapped : supportMark();


            fileMark = fis.getChannel().position();

        } catch (IOException ioe) {
            throw new IllegalStateException(ioe);
        }
    }
    @Override
    public boolean markSupported() {
        return true;
    }
    @Override
    public int read() throws IOException {
        use();
        return wrapped.read();
    }
    @Override
    public int read(byte[] b) throws IOException {
        use();
        return wrapped.read(b);
    }
    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        use();
        return wrapped.read(b, off, len);
    }


    @Override
    public long skip(long n) throws IOException {
        return wrapped.skip(n);
    }



    @Override
    public void reset() throws IOException {
        if (wrapped.markSupported()) {
            log.debug("" + wrapped + " supports mark, using it");
            wrapped.reset() ;
        } else if (file != null) {
            log.debug("Resetting " + this + " to " + fileMark + " (" + file + ")");
            wrapped = new FileInputStream(file);
            if (fileMark > 0) {
                wrapped.skip(fileMark);
            }
        } else {
            log.debug("No file yet");
            supportMark();
        }
    }




    @Override
    public String toString() {
        return "SERIALIZABLE " + wrapped + " (" + size + " byte, " +
            (name == null ? "[no name]" : name) +
            ", " +
            (contentType == null ? "[no contenttype]" : contentType) +
            ")";
    }

    protected static boolean inputStreamEquals(SerializableInputStream in1, SerializableInputStream in2) throws IOException {
        in1.mark(Integer.MAX_VALUE);
        in2.mark(Integer.MAX_VALUE);
        try {
            final byte[] buffer1 = new byte[1024];
            final byte[] buffer2 = new byte[1024];
            while (true) {
                int n1 = in1.read(buffer1);
                int n2 = in2.read(buffer2);
                if (n1 != n2) return false;
                if (n1 == -1) break;
                if ( ! java.util.Arrays.equals(buffer1, buffer2)) {
                    return false;
                }
            }
            return true;
        } finally {
            in1.reset();
            in2.reset();
        }

    }


    @Override
    public boolean equals(Object o) {
        if (o instanceof SerializableInputStream) {
            SerializableInputStream s = (SerializableInputStream) o;
            try {
                return
                    (getSize() == s.getSize()) &&
                    (getName() == null ? s.getName() == null : getName().equals(s.getName())) &&
                    (getContentType() == null ? s.getContentType() == null : getContentType().equals(s.getContentType())) &&
                    (fileMark == s.fileMark) &&
                    ((file != null && file.equals(s.file)) || inputStreamEquals(this, s));

            } catch (IOException ioe) {
                log.error(ioe);
                return false;
            }
        } else {
            return false;
        }

    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 43 * hash + (int) (this.size ^ (this.size >>> 32));
        hash = 43 * hash + (this.wrapped != null ? this.wrapped.hashCode() : 0);
        hash = 43 * hash + (this.file != null ? this.file.hashCode() : 0);
        hash = 43 * hash + (this.name != null ? this.name.hashCode() : 0);
        hash = 43 * hash + (this.contentType != null ? this.contentType.hashCode() : 0);
        return hash;
    }
    File getFile() {
        return file;
    }

}
