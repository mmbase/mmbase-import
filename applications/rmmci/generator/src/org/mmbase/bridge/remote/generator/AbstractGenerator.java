/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.bridge.remote.generator;

import java.io.*;
import java.lang.reflect.*;


/**
 * @javadoc
 *
 * @since MMBase-1.9
 * @author Pierre van Rooden
 * @version $Id: AbstractGenerator.java,v 1.3 2009-02-20 14:01:18 michiel Exp $
 */
abstract public class AbstractGenerator {

    protected Class<?> currentClass = null;
    protected StringBuilder buffer = new StringBuilder();

    public AbstractGenerator() {
    }

    public void generateLicense() {
        buffer.append("/*\n");
        buffer.append("\n");
        buffer.append("This software is OSI Certified Open Source Software.\n");
        buffer.append("OSI Certified is a certification mark of the Open Source Initiative.\n");
        buffer.append("\n");
        buffer.append("The license (Mozilla version 1.0) can be read at the MMBase site.\n");
        buffer.append("See http://www.MMBase.org/license\n");
        buffer.append("\n");
        buffer.append("\n");
        buffer.append("*/\n");
    }

    public void indent2() {
        buffer.append("  ");
    }

    public void indent4() {
        buffer.append("    ");
    }

    public void indent6() {
        buffer.append("      ");
    }

    public void indent8() {
        buffer.append("        ");
    }

    public boolean needsRemote(Type t) {
        return RMMCI.needsRemote(t);
    }

    public String getShortName(Class<?> c) {
        String className = c.getName();
        int shortIndex = className.lastIndexOf(".");
        if (c.getDeclaringClass() != null) {
            shortIndex = className.lastIndexOf(".", shortIndex);
        }
        return className.substring(shortIndex + 1);
    }

    public void writeSourceFile(File file) {
        try {
            //System.out.println("Generating remote " + file);

            FileOutputStream fos = new FileOutputStream(file);
            fos.write(buffer.toString().getBytes());
            fos.flush();
            fos.close();
        } catch (Exception e) {
            System.err.println("writeSourceFile" + e.getMessage());
        }
    }

    abstract public void generate();

}
