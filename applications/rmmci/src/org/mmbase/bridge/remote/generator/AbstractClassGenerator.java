/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.bridge.remote.generator;
import java.io.*;
import java.lang.reflect.*;
import java.util.*;


/**
 * @javadoc
 *
 * @since MMBase-1.9
 * @author Pierre van Rooden
 * @version $Id: AbstractClassGenerator.java,v 1.1 2006-09-29 08:59:07 pierre Exp $
 */
abstract public class AbstractClassGenerator extends AbstractGenerator {

    protected Class currentClass = null;
    protected Map<Type,Type> currentTypeSet = new HashMap<Type,Type>();

    public AbstractClassGenerator(Class c) {
        super();
        currentClass = c;
    }

    public Type getComponentType(Type t) {
        if (currentTypeSet.containsKey(t)) {
            t = getComponentType(currentTypeSet.get(t));
        } else if (t instanceof GenericArrayType) {
            t = getComponentType(((GenericArrayType) t).getGenericComponentType());
        } if (t instanceof ParameterizedType) {
            t = getComponentType(((ParameterizedType) t).getRawType());
        } else if (t instanceof TypeVariable) {
            for (Type bounds : ((TypeVariable)t).getBounds()) {
                if (!bounds.equals(Object.class)) {
                    t = getComponentType(bounds);
                    break;
                }
            }
        }
        return t;
    }

    public void appendTypeInfo(Type t) {
        appendTypeInfo(t, false);
    }

    public void appendTypeInfo(Type t, boolean showNameOnly) {
        appendTypeInfo(t, false, true);
    }

    public void appendTypeInfo(Type t, boolean showNameOnly, boolean wrapRemoteClasses) {
        if (currentTypeSet.containsKey(t)) {
            t = currentTypeSet.get(t);
            showNameOnly = true;
        }
        if (t instanceof GenericArrayType) {
            GenericArrayType gat = (GenericArrayType)t;
            appendTypeInfo(gat.getGenericComponentType(), showNameOnly, wrapRemoteClasses);
            buffer.append("[]");
        } else if (t instanceof Class) {
            Class c = (Class)t;
            if (c.isArray()) {
                appendTypeInfo(c.getComponentType(), showNameOnly, wrapRemoteClasses);
                buffer.append("[]");
            } else {
                if (wrapRemoteClasses && needsRemote(c)) {
                    buffer.append("Remote" + getShortName(c));
                } else {
                    buffer.append(c.getName());
                }
                // replace with Remote!
            }
        } else if (t instanceof ParameterizedType) {
            ParameterizedType pt = (ParameterizedType)t;
            Type ot = pt.getOwnerType();
            if (ot !=null) {
                appendTypeInfo(ot);
                buffer.append(".");
            }
            Type raw = pt.getRawType();
            appendTypeInfo(raw, showNameOnly, wrapRemoteClasses);
            Type[] arguments = pt.getActualTypeArguments();
            appendTypeParameters(pt.getActualTypeArguments(), showNameOnly, wrapRemoteClasses);
        } else if (t instanceof TypeVariable) {
            TypeVariable tv = (TypeVariable)t;
            buffer.append(tv.getName());
            if (!showNameOnly) {
                for (Type bounds : tv.getBounds()) {
                    if (!bounds.equals(Object.class)) {
                        buffer.append(" extends ");
                        appendTypeInfo(bounds, showNameOnly, wrapRemoteClasses);
                    }
                }
            }
        } else if (t instanceof WildcardType) {
            WildcardType wc = (WildcardType)t;
            buffer.append("?");
            if (!showNameOnly) {
                for (Type bounds : wc.getUpperBounds()) {
                    if (!bounds.equals(Object.class)) {
                        buffer.append(" extends ");
                        appendTypeInfo(bounds, showNameOnly, wrapRemoteClasses);
                    }
                }
                for (Type bounds : wc.getLowerBounds()) {
                    buffer.append(" super ");
                    appendTypeInfo(bounds, showNameOnly, wrapRemoteClasses);
                }
            }
        }
    }

    public void appendTypeArray(Type[] t) {
        appendTypeArray(t, false);
    }

    public void appendTypeArray(Type[] t, boolean showNameOnly) {
        appendTypeArray(t, showNameOnly, true);
    }

    public void appendTypeArray(Type[] t, boolean showNameOnly, boolean wrapRemoteClasses) {
        for (int i = 0; i < t.length; i++) {
            appendTypeInfo(t[i], showNameOnly, wrapRemoteClasses);
            if (i < t.length-1) {
                buffer.append(",");
            }
        }
    }

    public void appendTypeParameters(Type[] t) {
        appendTypeParameters(t, false);
    }

    public void appendTypeParameters(Type[] t, boolean showNameOnly) {
        appendTypeParameters(t, showNameOnly, true);
    }

    public void appendTypeParameters(Type[] t, boolean showNameOnly, boolean wrapRemoteClasses) {
        if (t.length > 0) {
            buffer.append("<");
            appendTypeArray(t, showNameOnly, wrapRemoteClasses);
            buffer.append(">");
        }
    }

    public void resolveTypeParameters(Type[] tarr) {
        for (Type t :  tarr) {
            if (t instanceof Class) {
                Class c = (Class)t;
                resolveTypeParameters(c.getGenericInterfaces());
            } else if (t instanceof ParameterizedType) {
                ParameterizedType pt = (ParameterizedType) t;
                Class raw = (Class)pt.getRawType();
                Type[] arguments = pt.getActualTypeArguments();
                Type[] superArguments = raw.getTypeParameters();
                int i = 0;
                for(Type tv : arguments) {
                    if (i<superArguments.length) {
                        currentTypeSet.put(superArguments[i], tv);
                    }
                    i++;
                }
                resolveTypeParameters(raw.getGenericInterfaces());
            }
        }
    }

    public void appendInterfaces(Type[] t) {
        appendInterfaces(t,true);
    }

    public void appendInterfaces(Type[] t, boolean wrapRemoteClasses) {
        resolveTypeParameters(t);
        for (int i = 0; i < t.length; i++) {
            appendTypeInfo(t[i], true, wrapRemoteClasses);
            if (i < t.length-1) {
                buffer.append(",");
            }
        }
    }

    boolean isBasicType(Type t) {
        return t.equals(java.lang.Object.class) || t.equals(java.util.List.class) || t.equals(java.util.SortedSet.class);
    }

    boolean needtoWrap(Type t) {
        Type ct = getComponentType(t);
        return needsRemote(ct) || isBasicType(ct);
    }

    protected boolean isBasicMethod(Method m) {
        String name = m.getName();
        return name.equals("equals") || name.equals("hashCode") || name.equals("toString") || name.equals("clone");
    }

    protected void appendMethodHeader(Method m, boolean wrapGenericMethodName, boolean wrapRemoteClasses) {
        buffer.append("  public ");
        Type[] typeParams = m.getTypeParameters();
        if (typeParams.length > 0) {
            appendTypeParameters(typeParams, wrapRemoteClasses);
            buffer.append(" ");
        }
        appendTypeInfo(m.getGenericReturnType(), false, wrapRemoteClasses);
        String name = m.getName();
        if (wrapGenericMethodName && isBasicMethod(m)) {
            name = "wrapped_" + name;
        }
        buffer.append(" " + name +"(");

        Type[] t = m.getGenericParameterTypes();
        for (int i = 0; i < t.length; i++) {
            if (i == t.length-1 && m.isVarArgs()) {
                if (t[i] instanceof Class && ((Class)t[i]).isArray()) {
                    appendTypeInfo(((Class)t[i]).getComponentType(), false, wrapRemoteClasses);
                } else if (t[i] instanceof GenericArrayType) {
                    appendTypeInfo(((GenericArrayType)t[i]).getGenericComponentType(), false, wrapRemoteClasses);
                } else {
                    appendTypeInfo(t[i], false, wrapRemoteClasses);
                }
                buffer.append("...");
            } else {
                appendTypeInfo(t[i], false, wrapRemoteClasses);
            }
            buffer.append(" arg" + i);
            if (i < t.length-1) {
                buffer.append(",");
            }
        }

        buffer.append(")");
    }

    abstract protected void appendMethod(Method m);

    protected void appendMethods() {
        Set<String> methodsAdded = new TreeSet<String>();
        for (Method m : currentClass.getMethods()) {
            if (Modifier.isStatic(m.getModifiers())) continue;
            appendMethod(m);
            methodsAdded.add(m.getName());
        }
        // methods that always need to be added
        try {
            if (!methodsAdded.contains("toString")) {
                appendMethod(Object.class.getMethod("toString"));
            }
            if (!methodsAdded.contains("hashCode")) {
                appendMethod(Object.class.getMethod("hashCode"));
            }
            if (!methodsAdded.contains("equals")) {
                appendMethod(Object.class.getMethod("equals", Object.class));
            }
        } catch (java.lang.NoSuchMethodException nsme) {
            // can not occur?
        }
    }

}
