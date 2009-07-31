/*
 *  This software is OSI Certified Open Source Software.
 *  OSI Certified is a certification mark of the Open Source Initiative.
 *  The license (Mozilla version 1.0) can be read at the MMBase site.
 *  See http://www.MMBase.org/license
 */
package org.mmbase.applications.packaging.sharehandlers;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.util.Hashtable;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.mmbase.applications.packaging.BundleManager;
import org.mmbase.applications.packaging.InstallManager;
import org.mmbase.applications.packaging.PackageManager;
import org.mmbase.applications.packaging.ProviderManager;
import org.mmbase.applications.packaging.ShareManager;
import org.mmbase.applications.packaging.bundlehandlers.BundleContainer;
import org.mmbase.applications.packaging.bundlehandlers.BundleInterface;
import org.mmbase.applications.packaging.packagehandlers.PackageContainer;
import org.mmbase.applications.packaging.packagehandlers.PackageInterface;
import org.mmbase.module.core.MMBaseContext;
import org.mmbase.servlet.BridgeServlet;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 * @author     Daniel Ockeloen
 */
public class ShareServlet extends BridgeServlet {

    private static Logger log;

    private static String packagepath = "/mmpm/download/package.mmp";

    private Hashtable<String, Object> postValues = new Hashtable<String, Object>();

    private boolean isRequestDecoded = false;
    private long postid;
    private HttpServletRequest req = null;
    int maxLoop = 20000;
    public static final int DEFAULT_MAX_REQUEST_SIZE = 4 * 1024 * 1024; // 4 MB
    public static final int DEFAULT_MAX_PARAMETER_SIZE = 4 * 1024 * 1024;//4 MB
    public static final int DEFAULT_MAX_IN_MEMORY_PARAMETER_SIZE =4 * 1024 * 1024; // 2 MB

//    private int maxRequestSize = DEFAULT_MAX_REQUEST_SIZE;
    private int maxParameterSize = DEFAULT_MAX_PARAMETER_SIZE;
    private int maxInMemoryParameterSize = DEFAULT_MAX_IN_MEMORY_PARAMETER_SIZE;
    private String uploadDir = "/tmp/";
    private boolean isPostedToDisk = false;


    /**
     * @exception  ServletException  Description of the Exception
     */

    public void init() throws ServletException {
        super.init();
        log = Logging.getLoggerInstance(ShareServlet.class);
        packagepath = getInitParameter("packagepath");
    }


    /**
     * Called by the server when a request is done.
     *
     * @param  req                   Description of the Parameter
     * @param  res                   Description of the Parameter
     * @exception  ServletException  Description of the Exception
     * @exception  IOException       Description of the Exception
     * @javadoc
     */
    public synchronized void doGet(HttpServletRequest req, HttpServletResponse res) {
        // Set the content type of this request
        res.setContentType("application/x-binary");

        String request = req.getRequestURI();
        if (request.equals(packagepath)) {
            if (!InstallManager.isRunning()) {
                InstallManager.init();
            }
            String id = req.getParameter("id");
            String version = req.getParameter("version");

//            String user = req.getParameter("user");
//            String password = req.getParameter("password");

            // check if its a package, if not cont. for bundle check
            PackageContainer p = (PackageContainer) PackageManager.getPackage(id);
            if (p != null) {
                PackageInterface rp = p.getPackageByScore(version);
                BufferedInputStream in = rp.getJarStream();
                int buffersize = 10240;
                byte[] buffer = new byte[buffersize];
                OutputStream out = null;
                try {
                    out = new BufferedOutputStream(res.getOutputStream());
                    int len;
                    while ((len = in.read(buffer, 0, buffersize)) != -1) {
                        out.write(buffer, 0, len);
                    }
                    out.flush();
                    out.close();
                } catch (java.io.IOException e) {
                    log.error(Logging.stackTrace(e));
                }
            }

            BundleContainer b = (BundleContainer) BundleManager.getBundle(id);
            if (b != null) {
                BundleInterface rb = b.getBundleByScore(version);
                BufferedInputStream in = rb.getJarStream();
                int buffersize = 10240;
                byte[] buffer = new byte[buffersize];
                OutputStream out = null;
                try {
                    out = new BufferedOutputStream(res.getOutputStream());
                    int len;
                    while ((len = in.read(buffer, 0, buffersize)) != -1) {
                        out.write(buffer, 0, len);
                    }
                    out.flush();
                    out.close();
                } catch (java.io.IOException e) {
                    log.error(Logging.stackTrace(e));
                }
            }
        }
    }

    public synchronized void doPost(HttpServletRequest req, HttpServletResponse res) {
	this.req=req;

        String filename = getPostParameter("filename");
        String account = getPostParameter("account");
        String password = getPostParameter("password");
//        String sharepassword = getPostParameter("sharepassword");
	
	// check the account and password
	ShareUser user = ShareManager.getShareUser(account);
	if ( user != null ) {
		if ( user.getPassword().equals(password) ) {
        	byte[] bytes = getPostParameterBytes("handle");

       		String path=MMBaseContext.getConfigPath()+File.separator+"packaging"+File.separator+"import"+File.separator;

        	File file = new File(path+filename);
      		try {
            		DataOutputStream f = new DataOutputStream(new FileOutputStream(file));
            		f.write(bytes);
            		f.flush();
            		f.close();
       		 } catch (Exception e) {
            		log.error(Logging.stackTrace(e));
        	} 
		ProviderManager.resetSleepCounter();
		} else {
			log.info("publish upload with wrong password '"+account+"'");
		}
	} else {
		log.info("publish upload with the user name '"+account+"', use not found");
	}
    }


    /**
    * This method returns the value of the postparameter as a String.
    * If it is a parameter with multiple values it returns the first one.
    * @see #getPostMultiParameter
    * @see #checkPostMultiParameter
    */
    public String getPostParameter(String name)  {
        Object obj = null;
        Vector<byte[]> v;

        if (!isRequestDecoded) {
            decodePost(req);
        }

        if ((obj = postValues.get(name)) != null) {
            try {
                if (obj instanceof String) {
                    log.error("Use getPostParameterFile");
                }
                // Catch the exception right here, it should be thrown but
                // that's against the Servlet-API Interface
            } catch (Exception e) {
                log.error("getPostParameter(" + name + "): " + e);
            }

            if (obj instanceof Vector) {
                v = (Vector<byte[]>)obj;
                Object elem = v.elementAt(0);
                if (elem instanceof String) {
                    return (String)elem;
                } else {
                    return new String(v.elementAt(0));
                }
            } else {
                return new String((byte[])obj);
            }
        } else {
            return null;
        }
    }


    private void decodePost(HttpServletRequest req)  {
        isRequestDecoded = true;
        byte[] postbuffer = null;

        if (req.getHeader("Content-length") != null || req.getHeader("Content-Length") != null) {
            postbuffer = readContentLength(req);
            String line = req.getHeader("Content-type");
            if (line == null) {
                line = req.getHeader("Content-Type");
            }

            if (line != null) {
                if (line.indexOf("application/x-www-form-urlencoded") != -1) {
                    readPostUrlEncoded(postbuffer, postValues);
                } else if (line.indexOf("multipart/form-data") != -1) {
                    if (!isPostedToDisk) {
                        readPostFormData(postbuffer, postValues, line);
                    } else {
                        readPostFormData( new File(uploadDir,"form_" + postid).getPath(), postValues, line);
                    }
                } else {
                    log.error("decodePost(): found no 'post' tag in post.");
                }
            } else {
                log.error("decodePost(): can't find Content-Type");
            }
        } else {
            log.error("decodePost(): found no 'content-length' tag in post.");
        }
    }


    public byte[] readContentLength(HttpServletRequest req) {
        int len, len2, len3;
        byte buffer[] = null;
        DataInputStream connect_in = null;

        int i = 0;

        try {
            connect_in = new DataInputStream(req.getInputStream());
        } catch (Exception e) {
            log.error("readContentLength(): can't open input stream");
        }

        len = req.getContentLength();
        // Maximum postsize
        if (len < maxInMemoryParameterSize) {
            log.debug("readContentLength(): writing to memory.");
            try {
                buffer = new byte[len];
                len2 = connect_in.read(buffer, 0, len);
                while (len2 < len && i < maxLoop) {
                    log.debug("readContentLength(): found len2( " + len2 + ")");
                    len3 = connect_in.read(buffer, len2, len - len2);
                    if (len3 == -1) {
                        log.debug("readContentLength(): WARNING: EOF while not Content Length");
                        break;
                    } else {
                        len2 += len3;
                    }
                    i++;
                }
                if (i >= maxLoop) {
                    log.info("readContentLength(): (memory) broken out of loop after " + i + " times");
                }
            } catch (Exception e) {
                log.error("readContentLength(): Can't read post msg from client");
                log.error(Logging.stackTrace(e));
                buffer[len - 1] = -1;
                // just to return _something_...
                // Mozilla 0.9.7 (and 0.9.6?) had a bug here. Now they are only slow, but work, if you don't supply a file...

            }
        } else if (len < maxParameterSize) {
            log.debug("readContentLength(): writing to disk");
            try {
                isPostedToDisk = true;
                RandomAccessFile raf = new RandomAccessFile(new File(uploadDir,"form_" + postid), "rw");
                int bufferlength = 64000;
                buffer = new byte[bufferlength];
                int index1 = 0, totallength = 0;

                index1 = connect_in.read(buffer);
                raf.write(buffer, 0, index1);
                totallength += index1;
                log.debug("readContentLength(): writing to disk: +");

                while (totallength < len && i < maxLoop) {
                    index1 = connect_in.read(buffer);
                    raf.write(buffer, 0, index1);
                    if (index1 == -1) {
                        log.error("readContentLength(): EOF while not Content Length");
                        break;
                    } else {
                        totallength += index1;
                    }
                    i++;
                }
                if (i >= maxLoop) {
                    log.info("readContentLength(): (disk) broken out of loop after " + i + " times");
                }
                log.info(" written(" + totallength + ")");
                raf.close();
            } catch (Exception e) {
                log.error("readContentLength(): " + e);
            }
        } else {
            log.error("readContentLength(): post too large: " + len + " size");
        }
        return buffer;
    }


    private boolean readPostUrlEncoded(byte[] postbuffer, Hashtable<String, Object> post_header) {
        String mimestr = "";
        int i = 0, idx;
        char letter;

        String buffer = new String(postbuffer);
        buffer = buffer.replace('+', ' ');
        StringTokenizer tok = new StringTokenizer(buffer, "&");
        int z = 0;
        while (tok.hasMoreTokens() && i < maxLoop) {
            mimestr = tok.nextToken();
            if ((idx = mimestr.indexOf('=')) != -1) {
                while ((i = mimestr.indexOf('%', i)) != -1) {
                    // Unescape the 'invalids' in the buffer (%xx) form
                    try {
                        letter = (char)Integer.parseInt(mimestr.substring(i + 1, i + 3), 16);
                        mimestr = mimestr.substring(0, i) + letter + mimestr.substring(i + 3);
                    } catch (Exception e) {}

                    i++;
                }
                addpostinfo2(post_header, mimestr.substring(0, idx), mimestr.substring(idx + 1));
            } else {
                addpostinfo2(post_header, mimestr, "");
            }
            z++;
        }
        if (z >= maxLoop) {
            log.info("readPostUrlEncoded: broken out of loop after " + z + " times");
        }
        return true;
    }


    private final void addpostinfo2(Hashtable<String, Object> postinfo, String name, String values) {
        Object obj;
        Vector<Object> v = null;

        byte[] value = new byte[values.length()];
        values.getBytes(0, values.length(), value, 0);

        if (postinfo.containsKey(name)) {
            obj = postinfo.get(name);
            if (obj instanceof byte[]) {
                v = new Vector<Object>();
                v.addElement(obj); // Add the first one
                v.addElement(value); // Then add the current one
                postinfo.put(name, v);
            } else if (obj instanceof Vector) {
                v = (Vector<Object>)obj;
                v.addElement(value);
            } else {
                log.error("addpostinfo(" + name + "," + value + "): object " + v + " is not Vector or byte[]");
            }
        } else {
            postinfo.put(name, value);
        }
    }


    public boolean readPostFormData(byte[] postbuffer, Hashtable<String, Object> post_header, String line) {
        int i2, i3, i4, start2, end2;
        String r;
        String templine = "--" + line.substring(line.indexOf("boundary=") + 9);
        byte[] marker = new byte[templine.length()];
        byte[] marker2 = new byte[4];
        byte[] marker3 = new byte[1];
        byte[] marker4 = new byte[1];
        byte[] dest;
        marker2[0] = (byte)'\r';
        marker2[1] = (byte)'\n';
        marker2[2] = (byte)'\r';
        marker2[3] = (byte)'\n';
        marker3[0] = (byte)'=';
        marker4[0] = (byte)'\"';
        templine.getBytes(0, templine.length(), marker, 0);

        start2 = indexOf(postbuffer, marker, 0) + marker.length;

        int z = 0;
        do {
            // hunt second one
            end2 = indexOf(postbuffer, marker, start2);
            if (end2 < 0) {
                log.error("readPostFormData(): postbuffer < 0 !!!! ");
                break;
            }

            // get all the data in between
            i2 = indexOf(postbuffer, marker2, start2);

            // [begin] I guess that the "content-disposition" is between the start2 and i2, -cjr
            // XXX Quite messy, this code (until [end]) stands outside of the rest of the extraction code and
            // partly overlaps, e.g. now name of field is extracted twice!
            /**
             * The code we are parsing here is like:
             *
             *   Content-Disposition: form-data; name="file"; filename="cees.gif"
             *   Content-type: image/gif
             *
             */
            String disposition = new String(postbuffer, start2 + 2, i2 - (start2 + 2));
            int separator = indexOf(postbuffer, new byte[] {(byte)'\r', (byte)'\n' }, start2 + 2);

            int filesize = (end2 - i2 - 6);
            if (filesize < 0)
                filesize = 0; // some browser bugs
            /**
             * No separator in Content-disposition: .. implies that there is only a name,
             * no filename, and hence that it is no file upload. The code under "if" above
             * is applicable for file uploads only.
             */
            if (separator > start2 + 2 && separator < i2) {
                try {
                    String[] dispositionInfo = extractDispositionInfo(disposition.substring(0, separator - (start2 + 2)));
                    String mimetype = extractContentType(disposition.substring(separator - (start2 + 2) + 2));
                    String fieldname = dispositionInfo[1];
                    String filename = dispositionInfo[2];
                    Vector<byte[]> v1 = new Vector<byte[]>();
                    v1.addElement(mimetype.getBytes());
                    addpostinfo(post_header, fieldname + "_type", v1);
                    Vector<byte[]> v2 = new Vector<byte[]>();
                    v2.addElement(filename.getBytes());
                    addpostinfo(post_header, fieldname + "_name", v2);

                    Vector<String> v3 = new Vector<String>();
                    v3.addElement("" + filesize);
                    addpostinfo(post_header, fieldname + "_size", v3);

                    if (log.isDebugEnabled()) {
                        log.debug("mimetype = " + mimetype);
                        log.debug("filename = " + dispositionInfo[2]);
                        log.debug("filesize = " + filesize);
                    }
                } catch (IOException e) {
                    log.error(e.getMessage());
                }
            }
            // [end]

            i3 = indexOf(postbuffer, marker3, start2 + 2) + 2;
            i4 = indexOf(postbuffer, marker4, i3 + 2);
            //r =  new String(postbuffer,)
            r = new String(postbuffer, i3, (i4 - i3)); // extraction of fieldname
            // log.debug("readPostFormData(): r="+r);
            // copy it to a buffer
            dest = new byte[filesize];
            System.arraycopy(postbuffer, i2 + 4, dest, 0, filesize);
            // r2=new String(postbuffer,0,i2+4,((end2-i2))-6);

            addpostinfo(post_header, r, dest);
            start2 = end2 + marker.length;
            z++;
        } while (postbuffer[start2] != '-' && z < maxLoop);
        if (z >= maxLoop) {
            log.info("readPostFormData: broken out of loop after " + z + " times");
        }
        return false;
    }


    private final void addpostinfo(Hashtable<String, Object> postinfo, String name, Object value) {
        Object obj;
        Vector<Object> v = null;

        if (postinfo.containsKey(name)) {
            obj = postinfo.get(name);
            if (obj instanceof byte[]) {
                v = new Vector<Object>();
                v.addElement(obj); // Add the first one
                v.addElement(value); // Then the one given
                postinfo.put(name, v);
            } else if (obj instanceof Vector) {
                v = (Vector<Object>)obj;
                v.addElement(value);
            } else {
                log.error("addpostinfo(" + name + "," + value + "): object " + v + " is not Vector or byte[]");
            }
        } else {
            postinfo.put(name, value);
        }
    }


    private int indexOf(byte v1[], byte v2[], int fromIndex) {

        int max = (v1.length - v2.length);

        // Yikes !!! Bij gebruik van continue test wordt de variable i (in de for) niet
        // opnieuw gedeclareerd. continue test kan gezien worden als ga verder met de for.
        // test is dus zeker GEEN label.
        test : for (int i = ((fromIndex < 0) ? 0 : fromIndex); i <= max; i++) {
            int n = v2.length;
            int j = i;
            int k = 0;
            while (n-- != 0) {
                if (v1[j++] != v2[k++]) {
                    continue test;
                }
            }
            return i;
        }
        return -1;
    }


    private String[] extractDispositionInfo(String line) throws IOException {
        String[] retval = new String[3];

        // Convert the line to a lowercase string without the ending \r\n
        // Keep the original line for error messages and for variable names
        String origline = line;
        line = origline.toLowerCase();

        // Get the content disposition, should be "form-data"
        int start = line.indexOf("content-disposition: ");
        int end = line.indexOf(";");
        if (start == -1 || end == -1) {
            throw new IOException("Content disposition corrupt: " + origline);
        }
        String disposition = line.substring(start + 21, end);
        if (!disposition.equals("form-data")) {
            throw new IOException("Invalid content disposition: " + disposition);
        }

        // Get the field name
        start = line.indexOf("name=\"", end); // start at last semicolon
        end = line.indexOf("\"", start + 7); // skip name=\"
        if (start == -1 || end == -1) {
            throw new IOException("Content disposition corrupt: " + origline);
        }
        String name = origline.substring(start + 6, end);

        // Get the filename, if give
        String filename = null;
        start = line.indexOf("filename=\"", end + 2); // start after name
        end = line.indexOf("\"", start + 10); // skip filename=\"
        if (start != -1 && end != -1) { // note the !=
            filename = origline.substring(start + 10, end);
            // The filename may contain a full path. Cut to just the filename
            int slash = Math.max(filename.lastIndexOf('/'), filename.lastIndexOf('\\'));
            if (slash > -1) {
                filename = filename.substring(slash + 1); // past last slash
            }

            if (filename.equals("")) {
                filename = "unknown"; // sanity check
            }

        }

        // Return a String array: disposition, fieldname, filename
        retval[0] = disposition;
        retval[1] = name;
        retval[2] = filename;
        return retval;
    }


    private String extractContentType(String line) throws IOException {
        String contentType = null;

        // Convert the line to a lowercase string
        String origline = line;
        line = origline.toLowerCase();

        // Get the content-type if any
        if (line.startsWith("content-type")) {
            int start = line.indexOf(" ");
            if (start == -1) {
                throw new IOException("Content type corrupt: " + origline);
            }
            contentType = line.substring(start + 1);
        } else if (line.length() != 0) { // no content type, so should be empty
            throw new IOException("Malformed line after disposition: " + origline);
        }

        return contentType;
    }


    public boolean readPostFormData(String formFile, Hashtable<String, Object> post_header, String line) {
        FileInputStream fis = null;
        RandomAccessFile raf = null;
        try {
            fis = new FileInputStream(formFile);
        } catch (Exception e) {
            System.out.println("WorkerPostHandler -> File " + formFile + " not exist");
        }
        int i, i2, i3, i4, start2, end2;
        String r;
        String templine = "--" + line.substring(line.indexOf("boundary=") + 9);
        byte[] marker = new byte[templine.length()];
        byte[] marker2 = new byte[4];
        byte[] marker3 = new byte[1];
        byte[] marker4 = new byte[1];
        byte[] dest;
        marker2[0] = (byte)'\r';
        marker2[1] = (byte)'\n';
        marker2[2] = (byte)'\r';
        marker2[3] = (byte)'\n';
        marker3[0] = (byte)'=';
        marker4[0] = (byte)'\"';
        templine.getBytes(0, templine.length(), marker, 0);
        log.info("readPostFormData(): begin");

        int offset = 0;
        //        int temp=0;
        int len = 64000;
        byte postbuffer[] = new byte[len];
        try {
            // Lees eerst stuk van het bestand.
            len = fis.read(postbuffer);

            // find first magic cookie
            start2 = indexOf(postbuffer, marker, 0) + marker.length;
            i = 0;
            do {
                // Get keyword
                i3 = indexOf(postbuffer, marker3, start2 + 2) + 2;
                i4 = indexOf(postbuffer, marker4, i3 + 2);
                r = new String(postbuffer, i3, (i4 - i3));
                log.debug("readPostFormData(): postName=" + r);

                // hunt second one
                end2 = indexOf(postbuffer, marker, start2);
                i2 = indexOf(postbuffer, marker2, start2);

                if (end2 == -1) {
                    log.info("readPostFormData(): writing to postValue: ");
                    File f = new File(uploadDir,"form_" + postid + "_" + r);
                    raf = new RandomAccessFile(f, "rw");
                    addpostinfo(post_header, r, f.getPath());
                    try {
                        raf.write(postbuffer, i2 + 4, len - (i2 + 4));
                    } catch (Exception e) {
                        log.error("readPostFormData(): Cannot write into file(1)" + e);
                    }
                    offset = len - i2 + 4;
                    int j = 0;
                    do {
                        // should we do something with temp? it is never read again
                        //temp =
                        fis.read(postbuffer);

                        end2 = indexOf(postbuffer, marker, 0);
                        if (end2 == -1) {
                            raf.write(postbuffer);
                        } else {
                            raf.write(postbuffer, 0, end2 - 2);
                        }
                        offset += len;
                        j++;
                    } while (end2 == -1 && j < maxLoop);
                    if (j >= maxLoop) {
                        log.info("readPostFormData(): (inner) broken out of loop after " + j + " times");
                    }
                    start2 = end2 + marker.length;
                    raf.close();
                } else {
                    dest = new byte[(end2 - i2) - 6];
                    System.arraycopy(postbuffer, i2 + 4, dest, 0, (end2 - i2) - 6);

                    addpostinfo(post_header, r, dest);
                    start2 = end2 + marker.length;
                }
                i++;
            }
            while (postbuffer[start2] != '-' && i < maxLoop);
            if (i >= maxLoop) {
                log.info("readPostFormData(): (outer) broken out of loop after " + i + " times");
            }
        } catch (Exception e) {
            log.error("readPostFormData(): Reached end of file: " + e);
        }
        return false;
    }


    public byte[] getPostParameterBytes(String name) {
        // decode when not done yet..
        if (!isRequestDecoded) {
            decodePost(req);
        }

        // when the parameter was not found, return null
        Object obj = postValues.get(name);
        if (obj == null) {
            return null;
        }

        // when it is an instance of String throw the exeption
        if (obj instanceof String) {
            String msg = "Use getPostParameterFile";
            log.warn(msg);
        }
        if (obj instanceof Vector) {
            Vector<byte[]> v = (Vector<byte[]>)obj;
            byte[] data = v.elementAt(0);
            return data;
        }
        byte[] data = (byte[])obj;
        return data;
    }
}

