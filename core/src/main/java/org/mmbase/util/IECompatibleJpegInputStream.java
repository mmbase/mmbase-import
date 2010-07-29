/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.util;

import java.io.*;
/**
 * IECompatibleJpegInputStream removes additional information left by PhotoShop 7 in jpegs
 * , this information may crash Internet Exploder. that's why you need to remove it.
 *
 * With PS 7, Adobe decided by default to embed XML-encoded "preview" data into JPEG files,
 * using a feature of the JPEG format that permits embedding of arbitrarily-named "profiles".
 * In theory, these files are valid according to the JPEG specifications.
 * However they break many applications, including Quark and, significantly,
 * various versions of Internet Explorer on various platforms.
 *
 * @since MMBase 1.7
 * @author Kees Jongenburger <keesj@dds.nl>
 * @version $Id$
 */
public class IECompatibleJpegInputStream extends FilterInputStream implements Runnable {

    private PipedInputStream pis;
    private PipedOutputStream pos;

    /**
     * create a new InputStream that parse the content of a jpeg file and removes application headers
     * if the content is not a jpeg the content remains unaffected
     */
    public IECompatibleJpegInputStream(InputStream in) {
        super(in);
        pis = new PipedInputStream();
        pos = new PipedOutputStream();
        try {
            pis.connect(pos);
        } catch (IOException ioe) {
        }
        ThreadPools.filterExecutor.execute(this);
    }

    public void run() {
        try {
            //read the first 2 byte so see if it is a jpeg file
            int magic1 = in.read();
            int magic2 = in.read();
            pos.write(magic1);
            pos.write(magic2);

            if (magic1 == 0xff && magic2 == 0xd8) {
                int b;
                //start reading
                while ((b = in.read()) != -1) {
                    if (b == 0xff) {
                        int marker = in.read();

                        if (marker == 0x00 || marker == 0xd8 || marker == 0xd9) { //some markers have no "size" like the escaping .. start and end of jpeg
                            pos.write(b);
                            pos.write(marker);
                        } else if (marker >= 0xe0 && marker <= 0xef) { //application markers not really required
                            //} else if (marker == 0xed) { //this marker is an application marker used by photoshop. it looks like this is the marker
                            //where photoshop stores the xml stuff that we never wanted so if you only want to remove the xml part of the file this is enough
                            int msb = in.read();
                            int lsb = in.read();

                            int size = msb * 256 + lsb;
                            in.skip(size - 2);
                        } else {
                            int msb = in.read();

                            int lsb = in.read();

                            int size = msb * 256 + lsb;
                            size -= 2;
                            pos.write(b);
                            pos.write(marker);
                            pos.write(msb);
                            pos.write(lsb);
                            while (size > 0) {
                                pos.write(in.read());
                                size--;
                            }
                        }
                    } else {
                        pos.write(b);
                    }
                }
            } else {
                int c = 0;
                byte[] buf = new byte[1024];
                while ((c = in.read(buf)) != -1) {
                    pos.write(buf, 0, c);
                }
            }
            in.close();
            pos.flush();
            pos.close();
        } catch (Exception e) {};
    }

    public int available() throws IOException {
        return pis.available();
    }

    public void close() throws IOException {
        pis.close();
        super.close();
    }

    public int read() throws IOException {
        return pis.read();
    }

    public int read(byte[] b, int off, int len) throws IOException {
        return pis.read(b, off, len);
    }

    public int read(byte[] b) throws IOException {
        return pis.read(b);
    }

    public long skip(long n) throws IOException {
        return pis.skip(n);
    }

    /**
     * Util method that uses the IECompatibleInputStream to convert a byte array
     * if the content is not a jpeg the content is not affected
     * @param in the byte array
     * @return the converted (ie compatible) jpeg
     */
    public static byte[] process(byte[] in) {
        try {
            InputStream inputStream = new IECompatibleJpegInputStream(new ByteArrayInputStream(in));
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            int c = 0;
            byte[] buf = new byte[1024];
            while ((c = inputStream.read(buf)) != -1) {
                out.write(buf, 0, c);
            }
            out.flush();
            return out.toByteArray();
        } catch (IOException e) {}
        return in;
    }

    //command line method
    public static void main(String[] argv) throws IOException {
        if (argv.length == 0) {
            System.err.println(IECompatibleJpegInputStream.class.getName() + " removes headers from jpeg files");
            System.err.println("it requires 2 parameters , the input jpeg and the output jpeg");
            System.exit(1);
        } else if (argv.length == 2) {
            File file = new File(argv[0]);
            if (!file.exists()) {
                System.err.println("can't convert non existing file" + file.getPath());
            }
            File out = new File(argv[1]);
            InputStream in = new IECompatibleJpegInputStream(new FileInputStream(file));
            OutputStream fos = new BufferedOutputStream(new FileOutputStream(out));
            int c = 0;
            while ((c = in.read()) != -1) {
                fos.write(c);
            }
            in.close();
            fos.flush();
            fos.close();
        }
    }
}
