/*
 *  This software is OSI Certified Open Source Software.
 *  OSI Certified is a certification mark of the Open Source Initiative.
 *  The license (Mozilla version 1.0) can be read at the MMBase site.
 *  See http://www.MMBase.org/license
 */
package org.mmbase.applications.packaging.providerhandlers;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.mmbase.applications.packaging.BundleManager;
import org.mmbase.applications.packaging.PackageManager;
import org.mmbase.applications.packaging.ShareManager;
import org.mmbase.applications.packaging.bundlehandlers.BundleInterface;
import org.mmbase.applications.packaging.packagehandlers.PackageInterface;
import org.mmbase.applications.packaging.projects.creators.CreatorInterface;
import org.mmbase.applications.packaging.util.ExtendedDocumentReader;
import org.mmbase.util.Encode;
import org.mmbase.util.xml.EntityResolver;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.xml.sax.InputSource;

/**
 * HttpProvider, Handler for Http Providers. gets packages and bundles from
 * the provider and feeds them to the package and bundle managers.
 *
 * @author     Daniel Ockeloen (MMBased)
 */
public class HttpProvider extends BasicProvider implements ProviderInterface {
    private static Logger log = Logging.getLoggerInstance(HttpProvider.class);

    private String name;
    private String method;
    private String maintainer;
    private String account = "guest";
    private String password = "guest";

    /**
     * DTD resource filename of the sharedpackages DTD version 1.0
     */
    public final static String DTD_SHAREDPACKAGES_1_0 = "sharedpackages_1_0.dtd";

    /**
     * Public ID of the sharedpackages DTD version 1.0
     */
    public final static String PUBLIC_ID_SHAREDPACKAGES_1_0 = "-//MMBase//DTD sharedpackages config 1.0//EN";


    /**
     * Register the Public Ids for DTDs used by DatabaseReader
     * This method is called by EntityResolver.
     */
    public static void registerPublicIDs() {
        EntityResolver.registerPublicID(PUBLIC_ID_SHAREDPACKAGES_1_0, DTD_SHAREDPACKAGES_1_0, HttpProvider.class);
    }



    /**
     *Constructor for the HttpProvider object
     */
    public HttpProvider() { }


    /**
     *  Description of the Method
     *
     * @param  n           Description of the Parameter
     * @param  name        Description of the Parameter
     * @param  method      Description of the Parameter
     * @param  maintainer  Description of the Parameter
     */
    public void init(org.w3c.dom.Node n, String name, String method, String maintainer) {
        super.init(n, name, method, maintainer);
        org.w3c.dom.Node n2 = xmlnode.getFirstChild();
        while (n2 != null) {
            if (n2.getNodeName().equals("path")) {
                org.w3c.dom.Node n3 = n2.getFirstChild();
                path = n3.getNodeValue();
            }
            if (n2.getNodeName().equals("description")) {
                org.w3c.dom.Node n3 = n2.getFirstChild();
                if (n3 != null) {
                    description = n3.getNodeValue();
                }
            }
            if (n2.getNodeName().equals("account")) {
                org.w3c.dom.Node n3 = n2.getFirstChild();
                account = n3.getNodeValue();
            }
            if (n2.getNodeName().equals("password")) {
                org.w3c.dom.Node n3 = n2.getFirstChild();
                password = n3.getNodeValue();
            }
            n2 = n2.getNextSibling();
        }
        baseScore = 2000;
    }


    /**
     *  Description of the Method
     *
     * @param  name        Description of the Parameter
     * @param  method      Description of the Parameter
     * @param  maintainer  Description of the Parameter
     * @param  path        Description of the Parameter
     */
    public void init(String name, String method, String maintainer, String path) {
        super.init(name, method, maintainer, path);
        // this.account=account;
        // this.password=password;
        baseScore = 2000;
    }


    /**
     *  Sets the account attribute of the HttpProvider object
     *
     * @param  account  The new account value
     */
    public void setAccount(String account) {
        this.account = account;
    }


    /**
     *  Gets the account attribute of the HttpProvider object
     *
     * @return    The account value
     */
    public String getAccount() {
        return account;
    }


    /**
     *  Sets the password attribute of the HttpProvider object
     *
     * @param  password  The new password value
     */
    public void setPassword(String password) {
        this.password = password;
    }


    /**
     *  Gets the password attribute of the HttpProvider object
     *
     * @return    The password value
     */
    public String getPassword() {
        return password;
    }


    /**
     *  Gets the packages attribute of the HttpProvider object
     */
    public void getPackages() {
        signalUpdate();

        String url = path + "?user=" + account + "&password=" + password;
        if (ShareManager.getCallbackUrl() != null) {
            url += "&callbackurl=" + Encode.encode("ESCAPE_URL_PARAM",ShareManager.getCallbackUrl());
        }
        try {
            URL includeURL = new URL(url);
            HttpURLConnection connection = (HttpURLConnection) includeURL.openConnection();
            BufferedInputStream input = new BufferedInputStream(connection.getInputStream());
            ExtendedDocumentReader reader = new ExtendedDocumentReader(new InputSource(input), HttpProvider.class);
            if (reader != null) {
                try {
                    for (Element e: reader.getChildElements("sharedpackages", "package")) {
                        NamedNodeMap nm = e.getAttributes();
                        if (nm != null) {
                            String name = null;
                            String type = null;
                            String version = null;
                            String date = null;

                            // decode name
                            org.w3c.dom.Node n2 = nm.getNamedItem("name");
                            if (n2 != null) {
                                name = n2.getNodeValue();
                            }

                            // decode the type
                            n2 = nm.getNamedItem("type");
                            if (n2 != null) {
                                type = n2.getNodeValue();
                            }

                            // decode the maintainer
                            n2 = nm.getNamedItem("maintainer");
                            if (n2 != null) {
                                maintainer = n2.getNodeValue();
                            }

                            // decode the version
                            n2 = nm.getNamedItem("version");
                            if (n2 != null) {
                                version = n2.getNodeValue();
                            }

                            // decode the creation date
                            n2 = nm.getNamedItem("creation-date");
                            if (n2 != null) {
                                date = n2.getNodeValue();
                            }

                            Element e2 = reader.getElementByPath(e, "package.path");
                            org.w3c.dom.Node pathnode = e2.getFirstChild();
                            String pkgpath = pathnode.getNodeValue();
                            if (type.indexOf("bundle/") == 0) {
                                BundleInterface bun = BundleManager.foundBundle(this, e, name, type, maintainer, version, date, pkgpath);
                                // check for included packages in the bundle
                                findIncludedPackages(bun, e, pkgpath, date);
                            } else {
                                PackageManager.foundPackage(this, e, name, type, maintainer, version, date, pkgpath);
                            }
                        }
                    }
                } catch (Exception f) {
                    log.error("something went wrong while decoding sharedpackagefile : " + url);
                    f.printStackTrace();
                }
            } else {
                log.error("can't get a valid reader for sharedpackagefile : " + url);
            }
            setState("up");
        } catch (Exception e) {
            // ignoring errors since well that servers are down is
            // not a error in this concept.
            log.error("can't get sharedpackagefile : " + url);
            //e.printStackTrace();
            setState("down");
        }
    }


    /**
     *  Gets the includedPackageJarFile attribute of the HttpProvider object
     *
     * @param  path            Description of the Parameter
     * @param  id              Description of the Parameter
     * @param  version         Description of the Parameter
     * @param  packageid       Description of the Parameter
     * @param  packageversion  Description of the Parameter
     * @return                 The includedPackageJarFile value
     */
    public JarFile getIncludedPackageJarFile(String path, String id, String version, String packageid, String packageversion) {
        // well first the whole bundle
        getJarFile(path, id, version);

        // it should now be in our import dir for us to get the package from
        try {
            String localname = getImportPath() + id + "_" + version + ".mmb";
            JarFile jarFile = new JarFile(localname);
            JarEntry je = jarFile.getJarEntry(packageid + "_" + packageversion + ".mmp");
            try {
                BufferedInputStream in = new BufferedInputStream(jarFile.getInputStream(je));
                BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(getImportPath() + ".temp_" + packageid + "_" + packageversion + ".mmp"));
                int val;
                while ((val = in.read()) != -1) {
                    out.write(val);
                }
                out.close();
            } catch (Exception e) {
                log.error("can't load : " + path);
                e.printStackTrace();
            }
            JarFile tmpjarfile = new JarFile(getImportPath() + ".temp_" + packageid + "_" + packageversion + ".mmp");
            return tmpjarfile;
        } catch (Exception e) {
            log.error("can't load : " + path);
            e.printStackTrace();
        }
        return null;
    }


    /**
     *  Gets the jarFile attribute of the HttpProvider object
     *
     * @param  path     Description of the Parameter
     * @param  id       Description of the Parameter
     * @param  version  Description of the Parameter
     * @return          The jarFile value
     */
    public JarFile getJarFile(String path, String id, String version) {
        // since this needs to load a package from a remote site, it uses a url connection
        // but since we don't want to reload it each time we create a copy in our
        // import dir, this means if something fails on a install the next install
        // will use the local copy instead of the remote copy keeping network
        // traffic down.
        try {
            // create a new name in the import dir
            String localname = getImportPath() + id + "_" + version + ".mmp";
            // not a very nice way should we have sepr. extentions ?
            if (id.indexOf("_bundle_") != -1) {
                localname = getImportPath() + id + "_" + version + ".mmb";
            }

        File checkfile = new File(localname);
        if (!checkfile.exists()) {

            URL includeURL = new URL(path);
                HttpURLConnection connection = (HttpURLConnection) includeURL.openConnection();
                BufferedInputStream in = new BufferedInputStream(connection.getInputStream());
                int buffersize = 10240;
                byte[] buffer = new byte[buffersize];


                BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(localname));
                StringBuffer string = new StringBuffer();
                int len;
        int totallen = 0;
                while ((len = in.read(buffer, 0, buffersize)) != -1) {
                    out.write(buffer, 0, len);
                if (getInstallStep()!=null) {
            totallen+=len;
            String lenp=" received ";
            if (totallen<1024) {
                lenp+=""+totallen+" bytes";
            } else if (totallen<(1024*1024)) {
                lenp+=""+((float)totallen/1024)+" KB";
            } else {
                lenp+=""+((float)totallen/(1024*1024))+" MB";
            }
            getInstallStep().setUserFeedBack("getting the mmb bundle... "+lenp);
            }
                }
                out.close();
        }

            JarFile jarFile = new JarFile(localname);
            return jarFile;
        } catch (Exception e) {
            log.error("can't load : " + path + " because " + e);
        }
        return null;
    }


    /**
     *  Description of the Method
     *
     * @return    Description of the Return Value
     */
    public boolean close() {
        return super.close();
    }


    /**
     *  Description of the Method
     *
     * @param  bun       Description of the Parameter
     * @param  n         Description of the Parameter
     * @param  realpath  Description of the Parameter
     * @param  date      Description of the Parameter
     */
    private void findIncludedPackages(BundleInterface bun, org.w3c.dom.Node n, String realpath, String date) {

        org.w3c.dom.Node n2 = n.getFirstChild();
        while (n2 != null) {
            String name = n2.getNodeName();
            // this should me one way defined (remote or local)
            if (name.equals("includedpackages")) {
                org.w3c.dom.Node n3 = n2.getFirstChild();
                while (n3 != null) {
                    name = n3.getNodeName();
                    NamedNodeMap nm = n3.getAttributes();
                    if (nm != null) {
                        String maintainer = null;
                        String type = null;
                        String version = null;
                        boolean packed = false;

                        // decode name
                        org.w3c.dom.Node n5 = nm.getNamedItem("name");
                        if (n5 != null) {
                            name = n5.getNodeValue();
                        }

                        // decode the type
                        n5 = nm.getNamedItem("type");
                        if (n5 != null) {
                            type = n5.getNodeValue();
                        }

                        // decode the maintainer
                        n5 = nm.getNamedItem("maintainer");
                        if (n5 != null) {
                            maintainer = n5.getNodeValue();
                        }

                        // decode the version
                        n5 = nm.getNamedItem("version");
                        if (n5 != null) {
                            version = n5.getNodeValue();
                        }

                        // decode the included
                        n5 = nm.getNamedItem("packed");
                        if (n5 != null) {
                            if (n5.getNodeValue().equals("true")) {
                                packed = true;
                            }
                        }

                        // done
                        if (packed) {
                            PackageInterface pack = PackageManager.foundPackage(this, (Element) n3, name, type, maintainer, version, date, realpath);
                            // returns a package if new one
                            if (pack != null) {
                                pack.setParentBundle(bun);
                            }
                        }
                    }
                    n3 = n3.getNextSibling();
                }
            }
            n2 = n2.getNextSibling();
        }
    }


    /**
     *  Gets the importPath attribute of the HttpProvider object
     *
     * @return    The importPath value
     */
    public String getImportPath() {
        String path = PackageManager.getConfigPath() + File.separator + "packaging" + File.separator + "import" + File.separator;
        File dir = new File(path);
        if (!dir.exists()) {
            dir.mkdir();
        }
        return path;
    }


    public boolean publish(CreatorInterface creator,PackageInterface pack,String sharepassword) {
    // should be general code for all types once all is in
    String filename=pack.getId()+"_"+pack.getVersion()+".mmp";

    String posturl = getPath();
    if (posturl.startsWith("http://")) {
        posturl = posturl.substring(7);
    }
    int pos=posturl.indexOf("/");
    if (pos!=-1) {
        posturl = posturl.substring(0,pos);
    }
    posturl = "http://"+posturl + "/mmbase/packagemanager/upload/package.mmp";
        try {
        String boundary =  "*5433***3243";

            // Send data
        URL url = new URL(posturl);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.setUseCaches(false);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "multipart/form-data;boundary="+boundary);

       DataOutputStream out = new DataOutputStream(conn.getOutputStream());

       out.writeBytes("--"+boundary+"\r\n");
       out.writeBytes("Content-Disposition: form-data; name=\"filename\"\r\n\r\n");
       out.writeBytes(filename+"\r\n");

       out.writeBytes("--"+boundary+"\r\n");
       out.writeBytes("Content-Disposition: form-data; name=\"account\"\r\n\r\n");
       out.writeBytes(account+"\r\n");

       out.writeBytes("--"+boundary+"\r\n");
       out.writeBytes("Content-Disposition: form-data; name=\"password\"\r\n\r\n");
       out.writeBytes(password+"\r\n");

       out.writeBytes("--"+boundary+"\r\n");
       out.writeBytes("Content-Disposition: form-data; name=\"sharepassword\"\r\n\r\n");
       out.writeBytes(sharepassword+"\r\n");
       out.writeBytes("--"+boundary+"\r\n");
       out.writeBytes("Content-Disposition: form-data; name=\"handle\"; filename=\"" +"testname" +"\"\r\n\r\n");
       String oldline="";
           if (getPackageStep()!=null) { oldline = getPackageStep().getUserFeedBack(); }

            try {
                BufferedInputStream in = new BufferedInputStream(pack.getJarStream());
                int val;
        int totallen=0;
                while ((val = in.read()) != -1) {
                    out.write(val);
            totallen++;
                if (getPackageStep()!=null) {
            String lenp=" send ";
            if (totallen<1024) {
                lenp+=""+totallen+" bytes";
            } else if (totallen<(1024*1024)) {
                lenp+=""+((float)totallen/1024)+" KB";
            } else {
                lenp+=""+((float)totallen/(1024*1024))+" MB";
            }
            getPackageStep().setUserFeedBack(oldline+lenp);
            }
                }
            } catch (Exception e) {
                log.error("can't load : " + path);
                e.printStackTrace();
            }
        out.writeBytes("\r\n");
        out.writeBytes("--"+boundary+"--\r\n");
        out.flush();
        out.close();

            if (getPackageStep()!=null) getPackageStep().setUserFeedBack(oldline+" server processing");

            // Get the response
            BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line;
            while ((line = rd.readLine()) != null) {
                // Process line...
            }
            rd.close();
        } catch (Exception e) {
        log.error("Publish upload problem to : "+posturl);
        }
    return true;
    }

    public boolean publish(CreatorInterface creator,BundleInterface bundle,String sharepassword) {
    // should be general code for all types once all is in
    String filename=bundle.getId()+"_"+bundle.getVersion()+".mmb";

    String posturl = getPath();
    if (posturl.startsWith("http://")) {
        posturl = posturl.substring(7);
    }
    int pos=posturl.indexOf("/");
    if (pos!=-1) {
        posturl = posturl.substring(0,pos);
    }
    posturl = "http://"+posturl + "/mmbase/packagemanager/upload/package.mmp";
        try {
        String boundary =  "*5433***3243";

            // Send data
        URL url = new URL(posturl);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.setUseCaches(false);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "multipart/form-data;boundary="+boundary);

       DataOutputStream out = new DataOutputStream(conn.getOutputStream());

       out.writeBytes("--"+boundary+"\r\n");
       out.writeBytes("Content-Disposition: form-data; name=\"filename\"\r\n\r\n");
       out.writeBytes(filename+"\r\n");

       out.writeBytes("--"+boundary+"\r\n");
       out.writeBytes("Content-Disposition: form-data; name=\"account\"\r\n\r\n");
       out.writeBytes(account+"\r\n");

       out.writeBytes("--"+boundary+"\r\n");
       out.writeBytes("Content-Disposition: form-data; name=\"password\"\r\n\r\n");
       out.writeBytes(password+"\r\n");

       out.writeBytes("--"+boundary+"\r\n");
       out.writeBytes("Content-Disposition: form-data; name=\"sharepassword\"\r\n\r\n");
       out.writeBytes(sharepassword+"\r\n");
       out.writeBytes("--"+boundary+"\r\n");
       out.writeBytes("Content-Disposition: form-data; name=\"handle\"; filename=\"" +"testname" +"\"\r\n\r\n");
       String oldline="";
           if (getPackageStep()!=null) { oldline = getPackageStep().getUserFeedBack(); }

            try {
                BufferedInputStream in = new BufferedInputStream(bundle.getJarStream());
                int val;
        int totallen=0;
                while ((val = in.read()) != -1) {
                    out.write(val);
            totallen++;
                if (getPackageStep()!=null) {
            String lenp=" send ";
            if (totallen<1024) {
                lenp+=""+totallen+" bytes";
            } else if (totallen<(1024*1024)) {
                lenp+=""+((float)totallen/1024)+" KB";
            } else {
                lenp+=""+((float)totallen/(1024*1024))+" MB";
            }
            getPackageStep().setUserFeedBack(oldline+lenp);
            }
                }
            } catch (Exception e) {
                log.error("can't load : " + path);
                e.printStackTrace();
            }
        out.writeBytes("\r\n");
        out.writeBytes("--"+boundary+"--\r\n");
        out.flush();
        out.close();

            if (getPackageStep()!=null) getPackageStep().setUserFeedBack(oldline+" server processing");

            // Get the response
            BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line;
            while ((line = rd.readLine()) != null) {
                // Process line...
            }
            rd.close();
        } catch (Exception e) {
        log.error("Publish upload problem to : "+posturl);
        }
    return true;
    }
}

