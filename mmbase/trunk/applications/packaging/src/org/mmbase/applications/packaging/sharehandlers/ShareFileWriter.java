/*
 *  This software is OSI Certified Open Source Software.
 *  OSI Certified is a certification mark of the Open Source Initiative.
 *  The license (Mozilla version 1.0) can be read at the MMBase site.
 *  See http://www.MMBase.org/license
 */
package org.mmbase.applications.packaging.sharehandlers;

import java.io.*;
import java.util.*;
import org.mmbase.module.core.*;
import org.mmbase.applications.packaging.*;
import org.mmbase.applications.packaging.packagehandlers.*;
import org.mmbase.applications.packaging.bundlehandlers.*;

import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 * @author     danielockeloen
 * @created    July 20, 2004
 */
public class ShareFileWriter {

    private static Logger log = Logging.getLoggerInstance(ShareFileWriter.class);


    /**
     *  Description of the Method
     *
     * @return    Description of the Return Value
     */
    public static boolean write() {
        String body =
                "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<!DOCTYPE shared PUBLIC \"-//MMBase/DTD shared config 1.0//EN\" \"http://www.mmbase.org/dtd/shared_1_0.dtd\">\n";
        body += "<shared>\n";

        body += writeSharedBundles();
        body += writeSharedPackages();
        body += writeUsers();
        body += writeGroups();
        body += writeSettings();
        body += writeProvidingPaths();

        body += "</shared>\n";

        String filename = MMBaseContext.getConfigPath() + File.separator + "packaging" + File.separator + "sharing.xml";
        saveFile(filename, body);
        return true;
    }


    /**
     *  Description of the Method
     *
     * @return    Description of the Return Value
     */
    private static String writeSharedPackages() {
        String result = "\t<packages>\n";
        Iterator e = ShareManager.getSharedPackages();
        while (e.hasNext()) {
            PackageContainer p = (PackageContainer) e.next();
            ShareInfo shareinfo = p.getShareInfo();
            result += "\t\t<package name=\"" + p.getName() + "\" maintainer=\"" + p.getMaintainer() + "\" ";
            result += "type=\"" + p.getType() + "\" ";
            result += "versions=\"best\" ";
            result += "active=\"" + shareinfo.isActive() + "\" ";
            result += ">\n";
            Iterator e2 = shareinfo.getShareUsers();
            while (e2.hasNext()) {
                ShareUser su = (ShareUser) e2.next();
                result += "\t\t\t<login user=\"" + su.getName() + "\" />\n";
            }
            e2 = shareinfo.getShareGroups();
            while (e2.hasNext()) {
                ShareGroup sg = (ShareGroup) e2.next();
                result += "\t\t\t<login group=\"" + sg.getName() + "\" />\n";
            }
            result += "\t\t</package>\n";
        }
        result += "\t</packages>\n";
        return result;
    }


    /**
     *  Description of the Method
     *
     * @return    Description of the Return Value
     */
    private static String writeSharedBundles() {
        String result = "\t<bundles>\n";
        Iterator e = ShareManager.getSharedBundles();
        while (e.hasNext()) {
            BundleContainer b = (BundleContainer) e.next();
            ShareInfo shareinfo = b.getShareInfo();
            result += "\t\t<bundle name=\"" + b.getName() + "\" maintainer=\"" + b.getMaintainer() + "\" ";
            result += "type=\"" + b.getType() + "\" ";
            result += "versions=\"best\" ";
            result += "active=\"" + shareinfo.isActive() + "\" ";
            result += ">\n";
            Iterator e2 = shareinfo.getShareUsers();
            while (e2.hasNext()) {
                ShareUser su = (ShareUser) e2.next();
                result += "\t\t\t<login user=\"" + su.getName() + "\" />\n";
            }
            e2 = shareinfo.getShareGroups();
            while (e2.hasNext()) {
                ShareGroup sg = (ShareGroup) e2.next();
                result += "\t\t\t<login group=\"" + sg.getName() + "\" />\n";
            }
            result += "\t\t</bundle>\n";
        }
        result += "\t</bundles>\n";
        return result;
    }


    /**
     *  Description of the Method
     *
     * @return    Description of the Return Value
     */
    private static String writeUsers() {
        String result = "\t<users>\n";
        Iterator e = ShareManager.getShareUsers();
        while (e.hasNext()) {
            ShareUser su = (ShareUser) e.next();
            result += "\t\t<user name=\"" + su.getName() + "\" password=\"" + su.getPassword() + "\" ";
            if (su.getMethod() != null) {
                result += "method=\"" + su.getMethod() + "\" ";
            }
            if (su.getHost() != null) {
                result += "ip=\"" + su.getHost() + "\" ";
            }
            result += "/>\n";
        }
        result += "\t</users>\n";
        return result;
    }


    /**
     *  Description of the Method
     *
     * @return    Description of the Return Value
     */
    private static String writeProvidingPaths() {
        String result = "\t<providingpaths>\n";
        HashMap has = ShareManager.getProvidingPaths();
        Iterator e = has.keySet().iterator();
        while (e.hasNext()) {
            String key = (String) e.next();
            String path = (String) has.get(key);
            result += "\t\t<providingpath method=\"" + key + "\" path=\"" + path + "\" />\n";
        }
        result += "\t</providingpaths>\n";
        return result;
    }


    /**
     *  Description of the Method
     *
     * @return    Description of the Return Value
     */
    private static String writeGroups() {
        String result = "\t<groups>\n";
        Iterator e = ShareManager.getShareGroups();
        while (e.hasNext()) {
            ShareGroup sg = (ShareGroup) e.next();
            result += "\t\t<group name=\"" + sg.getName() + "\">\n";
            Iterator e2 = sg.getMembers();
            while (e2.hasNext()) {
                ShareUser su = (ShareUser) e2.next();
                result += "\t\t\t<member user=\"" + su.getName() + "\" />\n";
            }
            result += "\t\t</group>\n";
        }
        result += "\t</groups>\n";
        return result;
    }


    /**
     *  Description of the Method
     *
     * @return    Description of the Return Value
     */
    private static String writeSettings() {
        String result = "\t<settings>\n";
        result += "\t\t<providername>" + ShareManager.getProviderName() + "</providername>\n";
        result += "\t\t<callbackurl>" + ShareManager.getCallbackUrl() + "</callbackurl>\n";
        result += "\t</settings>\n";
        return result;
    }


    /**
     *  Description of the Method
     *
     * @param  filename  Description of the Parameter
     * @param  value     Description of the Parameter
     * @return           Description of the Return Value
     */
    static boolean saveFile(String filename, String value) {
        File sfile = new File(filename);
        try {
            DataOutputStream scan = new DataOutputStream(new FileOutputStream(sfile));
            scan.writeBytes(value);
            scan.flush();
            scan.close();
        } catch (Exception e) {
            log.error(Logging.stackTrace(e));
        }
        //log.info("FILE="+value);
        return true;
    }

}

