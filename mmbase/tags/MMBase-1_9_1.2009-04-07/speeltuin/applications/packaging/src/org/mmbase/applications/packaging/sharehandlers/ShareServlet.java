/*
 *  This software is OSI Certified Open Source Software.
 *  OSI Certified is a certification mark of the Open Source Initiative.
 *  The license (Mozilla version 1.0) can be read at the MMBase site.
 *  See http://www.MMBase.org/license
 */
package org.mmbase.applications.packaging.sharehandlers;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.mmbase.bridge.*;
import org.mmbase.module.core.*;
import org.mmbase.util.*;
import org.mmbase.servlet.*;
import org.mmbase.applications.packaging.*;
import org.mmbase.applications.packaging.packagehandlers.*;
import org.mmbase.applications.packaging.bundlehandlers.*;

import java.io.*;

import java.util.*;
import java.util.jar.*;
import java.util.zip.*;

import org.mmbase.util.StringObject;

import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 * @author     danielockeloen
 * @created    July 20, 2004
 */
public class ShareServlet extends BridgeServlet {

    private static Logger log;

    private static String packagepath = "/mmpm/download/package.mmp";


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
    public synchronized void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        // Set the content type of this request
        res.setContentType("application/x-binary");

        String request = req.getRequestURI();
        if (request.equals(packagepath)) {
            if (!InstallManager.isRunning()) {
                InstallManager.init();
            }
            String id = req.getParameter("id");
            String version = req.getParameter("version");

            String user = req.getParameter("user");
            String password = req.getParameter("password");

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

    public synchronized void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
	HttpPost post =  new HttpPost(req);

        String filename = post.getPostParameter("filename");
        String account = post.getPostParameter("account");
        String password = post.getPostParameter("password");
        String sharepassword = post.getPostParameter("sharepassword");

        byte[] bytes = post.getPostParameterBytes("handle");

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
    }

}

