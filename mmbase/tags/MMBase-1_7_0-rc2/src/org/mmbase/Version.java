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
 * @version $Id: Version.java,v 1.24 2004-02-11 20:43:23 keesj Exp $
 */
public class Version {

    /**
     * Returns the 'name' part of the MMBase version. This will normall be 'MMBase'.
     * @since MMBase-1.6
     */
    public static String getName() {
        return "MMBase";
    }

    /**
     * Returns the major version number of this MMBase.
     * @since MMBase-1.6
     */
    public static int getMajor() {
        return 1;
    }
    /**
     * Returns the minor version number of this MMBase.
     * @since MMBase-1.6
     */
    public static int getMinor() {
        return 7;
    }

    /**
     * Returns the patch level numer of this MMBase.
     * @since MMBase-1.6
     */
    public static int getPatchLevel() {
        return 0;
    }

    /**
     * Returns the build date of this MMBase. During the build, the
     * value of this is stored in builddate.properties.
     *
     * @since MMBase-1.6
     */
    public static String getBuildDate() {
        String resource = "";
        InputStream builddate = Version.class.getResourceAsStream("builddate.properties");
        if (builddate != null) {
            try {
                BufferedReader buffer = new BufferedReader(new InputStreamReader(builddate));
                resource = "." + buffer.readLine();
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
     * @since MMBase-1.6
     */
    public static String getNumber() {
        return getMajor() + "." + getMinor() + "." + getPatchLevel() + (isRelease() ? "-rc2" : getBuildDate());
    }

    /**
     * Returns if this is a release version of MMBase. If this is false this MMBase is only a CVS snapshot.
     * @since MMBase-1.6
     */
    public static boolean isRelease() {
        return true;
    };

    /**
     * Returns the version of this MMBase.
     * @since MMBase-1.6
     */
    public static String get() {
        return getName() + " " + getNumber();
    }


    /**
     * Prints the version of this mmbase on stdout.
     * can be usefull on command line:
     * <code>java -jar mmbase.jar<code>
     */
    public static void main(String args[]) {
        System.out.println(get());
    }

}
