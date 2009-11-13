/*
 *  This software is OSI Certified Open Source Software.
 *  OSI Certified is a certification mark of the Open Source Initiative.
 *  The license (Mozilla version 1.0) can be read at the MMBase site.
 *  See http://www.MMBase.org/license
 */
package org.mmbase.applications.packaging.providerhandlers;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.mmbase.applications.packaging.BundleManager;
import org.mmbase.applications.packaging.PackageManager;
import org.mmbase.applications.packaging.bundlehandlers.BundleInterface;
import org.mmbase.applications.packaging.packagehandlers.PackageInterface;
import org.mmbase.applications.packaging.util.ExtendedDocumentReader;
import org.mmbase.util.xml.EntityResolver;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.xml.sax.InputSource;

/**
 * DiskProvider, Handler for Disk Providers. gets packages and bundles from
 * the provider and feeds them to the package and bundle managers.
 *
 * @author     Daniel Ockeloen (MMBased)
 */
public class DiskProvider extends BasicProvider implements ProviderInterface {
    private static Logger log = Logging.getLoggerInstance(DiskProvider.class);

    /**
     * DTD resource filename of the package DTD version 1.0
     */
    public final static String DTD_PACKAGE_1_0 = "package_1_0.dtd";
    /**
     *  Description of the Field
     */
    public final static String DTD_BUNDLE_1_0 = "bundle_1_0.dtd";

    /**
     * Public ID of the package DTD version 1.0
     */
    public final static String PUBLIC_ID_PACKAGE_1_0 = "-//MMBase//DTD package config 1.0//EN";
    /**
     *  Description of the Field
     */
    public final static String PUBLIC_ID_BUNDLE_1_0 = "-//MMBase//DTD bundle config 1.0//EN";


    /**
     * Register the Public Ids for DTDs used by DatabaseReader
     * This method is called by EntityResolver.
     */
    public static void registerPublicIDs() {
        EntityResolver.registerPublicID(PUBLIC_ID_PACKAGE_1_0, DTD_PACKAGE_1_0, DiskProvider.class);
        EntityResolver.registerPublicID(PUBLIC_ID_BUNDLE_1_0, DTD_BUNDLE_1_0, DiskProvider.class);
    }


    /**
     *Constructor for the DiskProvider object
     */
    public DiskProvider() { }


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
                if (n3 != null) {
                    path = n3.getNodeValue();
                }
            }
            if (n2.getNodeName().equals("description")) {
                org.w3c.dom.Node n3 = n2.getFirstChild();
                if (n3 != null) {
                    description = n3.getNodeValue();
                }
            }
            n2 = n2.getNextSibling();
        }

        baseScore = 5000;
        getPackages();
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
        this.path = path;
        baseScore = 5000;
    }


    /**
     *  Gets the packages attribute of the DiskProvider object
     */
    public void getPackages() {
        signalUpdate();

        String realpath = path;
        if (realpath.indexOf("~import") == 0) {
            realpath = getImportPath();
        }
        if (realpath.indexOf("~build") == 0) {
            realpath = PackageManager.getConfigPath() + "/packaging/build/";
        }
        File appDir = new File(realpath);

        if (!appDir.isDirectory()) {
            //log.error("DiskProvider the place where *.mmp files should be is not a directory : path="+realpath+" ignoring this Provider");
            setState("down");
            return;
        }

        String files[] = appDir.list();

        for (String filename : files) {
            if (filename.endsWith(".mmp") && filename.indexOf(".") != 0) {

                // open the jar to read the input xml
                try {
                    JarFile jarFile = new JarFile(realpath + filename);
                    JarEntry je = jarFile.getJarEntry("package.xml");
                    if (je != null) {
                        InputStream input = jarFile.getInputStream(je);
                        ExtendedDocumentReader reader = new ExtendedDocumentReader(new InputSource(input), DiskProvider.class);
                        if (reader != null) {
                            String name = reader.getElementAttributeValue("package", "name");
                            String type = reader.getElementAttributeValue("package", "type");
                            String maintainer = reader.getElementAttributeValue("package", "maintainer");
                            String version = reader.getElementAttributeValue("package", "version");
                            String date = reader.getElementAttributeValue("package", "creation-date");
                            Element e = reader.getElementByPath("package");
                            PackageInterface pack = PackageManager.foundPackage(this, e, name, type, maintainer, version, date, realpath + filename);
                        }
                    }
                } catch (Exception e) {
                    //log.error("Can't open jar file "+realpath+filename);
                }
            } else if (filename.endsWith(".mmb")) {

                // open the jar to read the input xml
                try {
                    JarFile jarFile = new JarFile(realpath + filename);
                    JarEntry je = jarFile.getJarEntry("bundle.xml");
                    if (je != null) {
                        InputStream input = jarFile.getInputStream(je);
                        ExtendedDocumentReader reader = new ExtendedDocumentReader(new InputSource(input), DiskProvider.class);
                        if (reader != null) {
                            String name = reader.getElementAttributeValue("bundle", "name");
                            String type = reader.getElementAttributeValue("bundle", "type");
                            String maintainer = reader.getElementAttributeValue("bundle", "maintainer");
                            String version = reader.getElementAttributeValue("bundle", "version");
                            String date = reader.getElementAttributeValue("bundle", "creation-date");
                            Element e = reader.getElementByPath("bundle");
                            BundleInterface bun = BundleManager.foundBundle(this, e, name, type, maintainer, version, date, realpath + filename);
                            // check for included packages in the bundle
                            findIncludedPackages(e, realpath, date, realpath + filename, bun);
                        }
                    }
                } catch (Exception e) {
                    //log.error("Can't open jar file "+realpath+filename);
                }
            }
        }
        setState("up");
    }

    /**
     *  Gets the includedPackageJarFile attribute of the DiskProvider object
     *
     * @param  path            Description of the Parameter
     * @param  id              Description of the Parameter
     * @param  version         Description of the Parameter
     * @param  packageid       Description of the Parameter
     * @param  packageversion  Description of the Parameter
     * @return                 The includedPackageJarFile value
     */
    public JarFile getIncludedPackageJarFile(String path, String id, String version, String packageid, String packageversion) {
        // it should now be in our import dir for us to get the package from
        try {
            JarFile jarFile = new JarFile(path);
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
     *  Gets the jarFile attribute of the DiskProvider object
     *
     * @param  path     Description of the Parameter
     * @param  id       Description of the Parameter
     * @param  version  Description of the Parameter
     * @return          The jarFile value
     */
    public JarFile getJarFile(String path, String id, String version) {
        String realpath = getImportPath();
        try {
            JarFile jarFile = new JarFile(path);
            if (path.endsWith("mmb") && id.indexOf("_bundle_") == -1) {
                JarEntry je = jarFile.getJarEntry(id + "_" + version + ".mmp");
                try {
                    InputStream in = jarFile.getInputStream(je);
                    BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(realpath + "/.temp_" + id + "_" + version + ".mmp"));
                    int val;
                    while ((val = in.read()) != -1) {
                        out.write(val);
                    }
                    out.close();
                } catch (Exception e) {
                    log.error("can't load : " + path);
                    e.printStackTrace();
                }
                JarFile tmpjarfile = new JarFile(realpath + "/.temp_" + id + "_" + version + ".mmp");
                return tmpjarfile;
            }
            return jarFile;
        } catch (Exception e) {
            log.error("can't load : " + path);
            e.printStackTrace();
        }
        return null;
    }


    /**
     *  Gets the jarStream attribute of the DiskProvider object
     *
     * @param  path  Description of the Parameter
     * @return       The jarStream value
     */
    public BufferedInputStream getJarStream(String path) {
        try {
            BufferedInputStream in = new BufferedInputStream(new FileInputStream(path));
            return in;
        } catch (Exception e) {
            log.error("can't load : " + path);
            e.printStackTrace();
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
     * @param  n         Description of the Parameter
     * @param  realpath  Description of the Parameter
     * @param  date      Description of the Parameter
     * @param  filename  Description of the Parameter
     * @param  bun       Description of the Parameter
     */
    private void findIncludedPackages(org.w3c.dom.Node n, String realpath, String date, String filename, BundleInterface bun) {
        org.w3c.dom.Node n2 = n.getFirstChild();
        while (n2 != null) {
            String name = n2.getNodeName();
            // this should me one way defined (remote or local)
            if (name.equals("neededpackages")) {
                org.w3c.dom.Node n3 = n2.getFirstChild();
                while (n3 != null) {
                    name = n3.getNodeName();
                    NamedNodeMap nm = n3.getAttributes();
                    if (nm != null) {
                        String maintainer = null;
                        String type = null;
                        String version = null;
                        boolean included = false;

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
                        n5 = nm.getNamedItem("included");
                        if (n5 != null) {
                            if (n5.getNodeValue().equals("true")) {
                                included = true;
                            }
                        }

                        // done
                        if (included) {
                            PackageInterface pack = PackageManager.foundPackage(this, null, name, type, maintainer, version, date, filename);
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
     *  Gets the importPath attribute of the DiskProvider object
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

}

