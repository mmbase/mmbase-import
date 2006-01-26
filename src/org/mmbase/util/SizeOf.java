/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.util;

// necessary for SizeOf
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;

import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 *<p>
 * Implementation of 'sizeof'. It is very hard to get reasonable estimations of how much memory your
 * structures take, this class it trying it any way. Often it's quite a heavy operation, so don't
 * use it too much.
 *</p>
 * <p>
 * A count of the byte size of an object is done recursively and for every count a SizeOf instance
 * must be instantiated. The static {@link #getByteSize(Object)} does that for you.
 *</p>
 * <p>
 * The core of the SizeOf object is then {@link #sizeof(Object)} plus a (private) 'countedObjects'
 * collection. The sizeof method returns the size the given Object would increase the size of this
 * countedObjects administration. This means that it returns 0 if the Object was already measured by the SizeOf
 * instance.
 * </p>
 * <p>
 * This means that it can be tricky to interpret the results of sizeof. The basic rule is that you
 * should take the size of a lot of similar objects with the same SizeOf instance, and take the
 * average.
 * </p>
 * <p>
 * A good example is {@link org.mmbase.module.core.MMObjectNode}. The first one counted will also
 * count a {@link org.mmbase.module.core.MMObjectBuilder} object - because that is linked to it, so
 * its memory is also taken (indirectly) - and will therefor give an unexpectedly large value like 20
 * kb or so. The second MMObjectNode, of the same type, that you'd count would however give a much
 * better estimation of the memory used by one Node in MMBase. The MMObjectBuilder is not counted
 * any more in this second Object, because it was already counted because of the first one.
 * </p>
 * <p>
 * For every individual entity there are several strategies for guessing the size.
 * <ul>
 *  <li>For atomic types (boolean, byte, char, short, int, long, float, double) a constant value is
 * returned</li>
 *  <li>If the entity implements {@link SizeMeasurable} it uses {@link SizeMeasurable#getByteSize(SizeOf)}</li>
 *  <li>For a limited number of known classes, a reasonable guess is done. E.g. if it is a {@link
 * java.util.Collection} it will simply sum the results of sizeof of its elements, and for a String
 * it will return getBytes().length().</li>
 *  <li>If all that fails, reflection will be used to sum the results of sizeof of all readable
 * members.</li>
 * </ul>
 * </p>
 * <p>
 * Don't forget to dereference or clear the SizeOf after use, otherwise it itself is a memory leak.
 *</p>
 *
 * @author Michiel Meeuwissen
 * @since  MMBase-1.6
 * @version $Id: SizeOf.java,v 1.15 2006-01-26 16:05:26 michiel Exp $
 * @todo   We need to know how well this actually works...
 */
public class SizeOf {
    private static final Logger log = Logging.getLoggerInstance(SizeOf.class);

    private static final int SZ_REF = 4;
    private static int size_prim(Class t) {
        if      (t == Boolean.TYPE)   return 1;
        else if (t == Byte.TYPE)      return 1;
        else if (t == Character.TYPE) return 2;
        else if (t == Short.TYPE)     return 2;
        else if (t == Integer.TYPE)   return 4;
        else if (t == Long.TYPE)      return 8;
        else if (t == Float.TYPE)     return 4;
        else if (t == Double.TYPE)    return 8;
        else if (t == Void.TYPE)      return 0;
        else return SZ_REF;
    }

    public static int sizeof(boolean b) { return 1; }
    public static int sizeof(byte b)    { return 1; }
    public static int sizeof(char c)    { return 2; }
    public static int sizeof(short s)   { return 2; }
    public static int sizeof(int i)     { return 4; }
    public static int sizeof(long l)    { return 8; }
    public static int sizeof(float f)   { return 4; }
    public static int sizeof(double d)  { return 8; }

    // To avoid infinite loops (cyclic references):
    private Set countedObjects = new HashSet();

    public static int getByteSize(Object obj) {
        return new SizeOf().sizeof(obj);
    }
    
    /**
     * @since MMBase-1.8
     */
    public void clear() {
        countedObjects.clear();
    }
    /**
     * @return The size in bytes obj structure will take, or <code>0</code> if the object was
     * already counted by this SizeOf object.
     */
    public int sizeof(Object obj) {
        if (obj == null) {
            return 0;
        }

        if (countedObjects.contains(obj)) {
            log.trace("already counted");
            return 0;
        } else {
            log.trace("adding to countedObject");
            countedObjects.add(obj);
        }

        Class c = obj.getClass();

        if (c.isArray()) {
            log.debug("an array");
            return size_arr(obj, c);
        } else {
            if (log.isDebugEnabled()) {
                log.debug("an object " + obj);
            }
            try {
                if (SizeMeasurable.class.isAssignableFrom(c)) return sizeof((SizeMeasurable) obj);
                if (javax.servlet.http.HttpSession.class.isAssignableFrom(c))   return sizeof((javax.servlet.http.HttpSession) obj);
                if (org.w3c.dom.Node.class.isAssignableFrom(c))   return sizeof((org.w3c.dom.Node) obj);
                if (Map.class.isAssignableFrom(c))      return sizeof((Map) obj);
                if (Collection.class.isAssignableFrom(c))      return sizeof((Collection) obj);
                if (String.class.isAssignableFrom(c))   return sizeof((String) obj);
                // more insteresting stuff can be added here.
            } catch (Throwable e) {
                log.debug("Error during determination of size of " + obj + " :" + e);
            }
            return size_inst(obj, c);
        }
    }

    private int sizeof(Map m) {
        log.debug("sizeof Map");
        int len = size_inst(m, m.getClass());
        Iterator i = m.entrySet().iterator();
        while (i.hasNext()) {
            Map.Entry entry = (Map.Entry) i.next();
            log.trace("key");
            len += sizeof(entry.getKey());
            log.trace("value");
            len += sizeof(entry.getValue());
        }
        return len;
    }

    private int sizeof(Collection m) {
        log.debug("sizeof List");
        int len = size_inst(m, m.getClass());
        Iterator i = m.iterator();
        while (i.hasNext()) {
            len += sizeof(i.next());
        }
        return len;
    }

    private int sizeof(javax.servlet.http.HttpSession session) {
        log.debug("sizeof HttpSession");
        int len = size_inst(session, session.getClass());
        Enumeration e = session.getAttributeNames();
        while (e.hasMoreElements()) {
            String attribute = (String) e.nextElement();
            len += sizeof(attribute);
            len += sizeof(session.getAttribute(attribute));
        }
        return len;
    }

    private int sizeof(org.w3c.dom.Node node) {
        log.debug("sizeof Node");
        // a little hackish...
        return sizeof(org.mmbase.util.xml.XMLWriter.write(node, false));
    }

    private int sizeof(String m) {
        log.debug("sizeof String " + m);
        int len = size_inst(m, m.getClass());
        return len + m.getBytes().length;
    }

    private int sizeof(SizeMeasurable m) {
        log.debug("sizeof SizeMeasureable " + m);
        int len = size_inst(m, m.getClass());
        return len + m.getByteSize(this);
    }


    private int size_inst(Object obj, Class c) {
        Field flds[] = c.getDeclaredFields();
        int sz = 0;

        for (int i = 0; i < flds.length; i++) {
            Field f = flds[i];
            if (!c.isInterface() &&  (f.getModifiers() & Modifier.STATIC) != 0) {
                continue;
            }
            sz += size_prim(f.getType());
            //f.setAccessible(true);
            if (f.isAccessible()) {
                try {
                    sz += sizeof(f.get(obj)); // recursion
                    if (log.isDebugEnabled()) log.debug("found an (accessible) field " + f);
                    
                } catch (java.lang.IllegalAccessException e) {
                    // well...
                    log.trace(e);                    
                }
            }
        }

        if (c.getSuperclass() != null) {
            sz += size_inst(obj, c.getSuperclass());
        }

        Class cv[] = c.getInterfaces();
        for (int i = 0; i < cv.length; i++) {
            sz += size_inst(obj, cv[i]);
        }

        return sz;
    }

    private int size_arr(Object obj, Class c) {
        Class ct = c.getComponentType();
        int len = Array.getLength(obj);

        if (ct.isPrimitive()) {
            return len * size_prim(ct);
        }
        else {
            int sz = 0;
            for (int i = 0; i < len; i++) {
                sz += SZ_REF;
                Object obj2 = Array.get(obj, i);
                if (obj2 == null)
                    continue;
                Class c2 = obj2.getClass();
                if (!c2.isArray())
                    continue;
                sz += size_arr(obj2, c2);
            }
            return sz;
        }
    }
}

