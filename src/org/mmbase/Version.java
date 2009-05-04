/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase;

import java.io.*;

/**
 * MMBase version reporter. The only goal of this class is providing the current version of
 * MMBase. The function 'get' will return it as one String.
 *
 * @author Daniel Ockeloen
 * @author Michiel Meeuwissen
 * @version $Id$
 */
public class Version {

    /**
     * Returns the 'name' part of the MMBase version. This will normally be 'MMBase'.
     * @return Name part of version
     * @since MMBase-1.6
     */
    public static String getName() {
        //String cvsTag = "$Name: not supported by cvs2svn $";
        //String name = cvsTag.substring(6, cvsTag.length() - 1).trim();
        //return name.equals("") ? "MMBase" : name;
        return "MMBase";
    }

    /**
     * Returns the major version number of this MMBase.
     * @return major version number
     * @since MMBase-1.6
     */
    public static int getMajor() {
        return 1;
    }
    /**
     * Returns the minor version number of this MMBase.
     * @return minor version number
     * @since MMBase-1.6
     */
    public static int getMinor() {
        return 8;
    }

    /**
     * Returns the patch level number of this MMBase.
     * @return patch level number
     * @since MMBase-1.6
     */
    public static int getPatchLevel() {
        return 7;
    }

    /**
     * Returns the build date of this MMBase. During the build, the
     * value of this is stored in builddate.properties.
     * @return build date of this MMBase
     *
     * @since MMBase-1.6
     */
    public static String getBuildDate() {
        String resource = "";
        InputStream builddate = Version.class.getResourceAsStream("builddate.properties");
        if (builddate != null) {
            try {
                BufferedReader buffer = new BufferedReader(new InputStreamReader(builddate));
                resource =  buffer.readLine();
                buffer.close();
            } catch (IOException e) {
                // error
                resource = "" + e;
            }
        }
        return resource;
    }

    /**
     * Returns the version number of this MMBase.
     * @return version number
     * @since MMBase-1.6
     */
    public static String getNumber() {
        return getMajor() + "." + getMinor() + "." + getPatchLevel() + (isRelease() ? "-" + getReleaseStatus() + " "  : ".")  + getBuildDate();
    }

    /**
     * Returns if this is a release version of MMBase. If this is false this MMBase is only a CVS snapshot.
     * @return is a release version
     * @since MMBase-1.6
     */
    public static boolean isRelease() {
        return false;
    };

    /**
     * A String describing the status of this release. Like 'final' or 'rc3'.
     * @return status of this release
     * @since MMBase-1.7
     */
    public static String getReleaseStatus() {
        return "";
    };

    /**
     * Returns the version of this MMBase.
     * @return version of this MMBase
     * @since MMBase-1.6
     */
    public static String get() {
        String name = getName();
        return name + ("MMBase".equals(name) ? " " + getNumber() : "");
    }


    /**
     * Prints the version of this mmbase on stdout.
     * can be usefull on command line:
     * <code>java -jar mmbase.jar<code>
     *
     * @param args command line args
     */
    public static void main(String args[]) {
        System.out.println(get());
    }

}
