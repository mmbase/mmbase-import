/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

 */

package org.mmbase.mojo.remote;

import java.util.*;
import java.io.File;


/**
 * @javadoc
 *
 * @since MMBase-1.9
 * @author Pierre van Rooden
 * @version $Id$
 */
public class ObjectWrapperGenerator extends AbstractGenerator {

    List<Class<?>> objectsToWrap;

    @SuppressWarnings("unchecked")
    Map<Class<?>,List<Class>> superClassLists = new HashMap<Class<?>,List<Class>>();

    public ObjectWrapperGenerator(List<Class<?>> lc) {
        super();

        for(Iterator<Class<?>> i = lc.iterator(); i.hasNext();) {
            if (!needsRemote(i.next())) {
                i.remove();
            }
        }

        objectsToWrap = new ArrayList<Class<?>>();

        // now handle more specific classes
        int currentSize = objectsToWrap.size() - 1;
        while (lc.size() > 0) {
            if (objectsToWrap.size() == currentSize) {
                System.err.println("ERROR: Could not resolve order in ObjectWrapperHelper");
                objectsToWrap.addAll(lc);
                break;
            }
            currentSize = objectsToWrap.size();
            for (Iterator<Class<?>> i = lc.iterator(); i.hasNext();) {
                Class<?> c = i.next();
                if (objectsToWrap.containsAll(getSuperClasses(c))) {
                    objectsToWrap.add(0, c);
                    i.remove();
                }
            }
        }
    }

    @SuppressWarnings("unchecked")
    protected List<Class> getSuperClasses(Class<?> c) {
        List<Class> retval = superClassLists.get(c);
        if (retval == null) {
            List interfaces = Arrays.asList(c.getInterfaces());
            retval = new ArrayList<Class>(interfaces);
            for(Iterator<Class> i = retval.iterator(); i.hasNext();) {
                if (!needsRemote(i.next())) {
                    i.remove();
                }
            }
            superClassLists.put(c,retval);
        }
        return retval;
    }

    public void generateHeader() {
        generateLicense();
        buffer.append("package org.mmbase.bridge.remote;");
        buffer.append("import java.util.*;\n");
        buffer.append("import java.rmi.*;\n");

        buffer.append("import org.mmbase.bridge.*;\n");
        buffer.append("import org.mmbase.bridge.Module;\n");
        buffer.append("import org.mmbase.cache.*;\n");
        buffer.append("import org.mmbase.datatypes.processors.*;\n");
        buffer.append("import org.mmbase.datatypes.*;\n");
        buffer.append("import org.mmbase.security.*;\n");
        buffer.append("import org.mmbase.bridge.remote.*;\n");
        buffer.append("import org.mmbase.bridge.remote.rmi.*;\n");
        buffer.append("import org.mmbase.bridge.remote.proxy.*;\n");

        buffer.append("import org.mmbase.storage.search.*;\n");
        buffer.append("import org.mmbase.util.functions.*;\n");
        buffer.append("import org.mmbase.util.logging.*;\n");

        buffer.append("/**\n");
        buffer.append(" * @author generated by " + this.getClass().getName() + "\n");
        buffer.append(" */\n");
        buffer.append(" //DO NOT EDIT THIS FILE, IT IS GENERATED by " + this.getClass().getName() + "\n");
    }

    @Override
    public void generate() {
        generateHeader();
        buffer.append("public abstract class ObjectWrapperHelper {\n");

        buffer.append("    public static Object localToRMIObject(Object o, int port) throws RemoteException {\n");
        buffer.append("        Object retval = null;\n");

        boolean isFirst = true;
        for (Class<?> c : objectsToWrap) {
            if (!isFirst) {
                buffer.append("    } else");
            }
            String name = getShortName(c);
            String remoteName = "Remote" + name;
            buffer.append("    if (o instanceof " + name + ") {\n");
            buffer.append("        retval = new " + remoteName + "_Rmi((" + name + ")o, port);\n");
            isFirst = false;
        }
        buffer.append("    }\n    return retval;\n  }\n\n");

        buffer.append("  public static Object rmiObjectToRemoteProxy(Object o) throws RemoteException {\n");
        buffer.append("      Object retval = null;\n");

        isFirst = true;
        for (Class<?> c : objectsToWrap) {
            if (!isFirst) {
                buffer.append("    } else");
            }
            String remoteName = "Remote" + getShortName(c);
            buffer.append("    if (o instanceof " + remoteName + ") {\n");
            buffer.append("      retval = new " + remoteName + "_Proxy((" + remoteName + ")o);\n");
            isFirst = false;
        }
        buffer.append("    }\n    return retval;\n  }\n\n");

        buffer.append("}\n");
    }

    public void generate(File remoteDir) {
        generate();
        writeSourceFile(new File(remoteDir, "ObjectWrapperHelper.java"));
    }

}
