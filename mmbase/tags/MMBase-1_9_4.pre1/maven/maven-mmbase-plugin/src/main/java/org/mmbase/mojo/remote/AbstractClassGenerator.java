/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.mojo.remote;
import java.lang.reflect.*;
import java.util.*;


/**
 * @javadoc
 *
 * @since MMBase-1.9
 * @author Pierre van Rooden
 * @version $Id$
 */
abstract public class AbstractClassGenerator extends AbstractGenerator {

    protected Map<Type,Type> currentTypeSet = new HashMap<Type,Type>();

    public AbstractClassGenerator(Class<?> c) {
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
            for (Type bounds : ((TypeVariable<?>)t).getBounds()) {
                if (!bounds.equals(Object.class)) {
                    t = getComponentType(bounds);
                    break;
                }
            }
        }
        return t;
    }

    protected Type[] getListTypeParameters( Class<?> listClass) {
        return getClassTypeParameters(listClass, List.class);
    }

    protected Type[] getListIteratorTypeParameters(Class<?> iteratorClass) {
        return getClassTypeParameters(iteratorClass, ListIterator.class);
    }

    private Type[] getClassTypeParameters(Class<?> clazz, Class<?> assignableType) {
        Type[] typeParameters = clazz.getTypeParameters();
        while(clazz != null && (typeParameters == null || typeParameters.length == 0)) {
            Type[] interfaces = clazz.getGenericInterfaces();
            for (Type interfaceType : interfaces) {
                if (interfaceType instanceof ParameterizedType) {
                    ParameterizedType paramType = (ParameterizedType) interfaceType;
                    if (assignableType.isAssignableFrom((Class<?>) paramType.getRawType())) {
                        clazz = (Class<?>) paramType.getRawType();
                        typeParameters = paramType.getActualTypeArguments();                 
                    }
                }
            }
        }
        return typeParameters;
    }

    
    public void appendTypeInfo(Type t) {
        appendTypeInfo(t, false);
    }

    public void appendTypeInfo(Type t, boolean showNameOnly) {
        appendTypeInfo(t, showNameOnly, true);
    }

    public void appendTypeInfo(Type t, boolean showNameOnly, boolean wrapRemoteClasses) {
        while (currentTypeSet.containsKey(t)) {
            t = currentTypeSet.get(t);
            showNameOnly = true;
        }
        if (t instanceof GenericArrayType) {
            appendGenricTypeInfo(t, showNameOnly, wrapRemoteClasses);
        } else if (t instanceof Class) {
            appendClassInfo(t, showNameOnly, wrapRemoteClasses);
        } else if (t instanceof ParameterizedType) {
            appendParameterizedTypeInfo(t, showNameOnly, wrapRemoteClasses);
        } else if (t instanceof TypeVariable) {
            appendTypeVariableInfo(t, showNameOnly, wrapRemoteClasses);
        } else if (t instanceof WildcardType) {
            appendWildcardTypeInfo(t, showNameOnly, wrapRemoteClasses);
        }
    }


    public void appendListTypeInfo(Type t, boolean showNameOnly, boolean localFirst) {
        while (currentTypeSet.containsKey(t)) {
            t = currentTypeSet.get(t);
            showNameOnly = true;
        }
        if (t instanceof GenericArrayType) {
            appendGenricTypeInfo(t, showNameOnly, !localFirst);
            buffer.append(",");
            appendGenricTypeInfo(t, showNameOnly, localFirst);
        } else if (t instanceof Class) {
            appendClassInfo(t, showNameOnly, !localFirst);
            buffer.append(",");
            appendClassInfo(t, showNameOnly, localFirst);
        } else if (t instanceof ParameterizedType) {
            appendParameterizedTypeInfo(t, showNameOnly, !localFirst);
            buffer.append(",");
            appendParameterizedTypeInfo(t, showNameOnly, localFirst);
        } else if (t instanceof TypeVariable) {
            appendTypeVariableInfo(t, showNameOnly, !localFirst);
            buffer.append(",");
            appendTypeVariableInfo(t, showNameOnly, localFirst);
        } else if (t instanceof WildcardType) {
            appendWildcardTypeInfo(t, showNameOnly, !localFirst);
            buffer.append(",");
            appendWildcardTypeInfo(t, showNameOnly, localFirst);
        }
    }
    
    private void appendGenricTypeInfo(Type t, boolean showNameOnly, boolean wrapRemoteClasses) {
        GenericArrayType gat = (GenericArrayType)t;
        appendTypeInfo(gat.getGenericComponentType(), showNameOnly, wrapRemoteClasses);
        buffer.append("[]");
    }

    private void appendClassInfo(Type t, boolean showNameOnly, boolean wrapRemoteClasses) {
        Class<?> c = (Class<?>)t;
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
    }

    private void appendParameterizedTypeInfo(Type t, boolean showNameOnly, boolean wrapRemoteClasses) {
        ParameterizedType pt = (ParameterizedType)t;
        Type ot = pt.getOwnerType();
        if (ot !=null) {
            appendTypeInfo(ot);
            buffer.append(".");
        }
        Type raw = pt.getRawType();
        appendTypeInfo(raw, showNameOnly, wrapRemoteClasses);
        appendTypeParameters(pt.getActualTypeArguments(), showNameOnly, wrapRemoteClasses);
    }

    private void appendTypeVariableInfo(Type t, boolean showNameOnly, boolean wrapRemoteClasses) {
        TypeVariable<?> tv = (TypeVariable<?>)t;
        buffer.append(tv.getName());
        if (!showNameOnly) {
            for (Type bounds : tv.getBounds()) {
                if (!bounds.equals(Object.class)) {
                    buffer.append(" extends ");
                    appendTypeInfo(bounds, showNameOnly, wrapRemoteClasses);
                }
            }
        }
    }

    private void appendWildcardTypeInfo(Type t, boolean showNameOnly, boolean wrapRemoteClasses) {
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

    public void appendListTypeArray(Type[] t, boolean showNameOnly, boolean localFirst) {
        for (int i = 0; i < t.length; i++) {
            appendListTypeInfo(t[i], showNameOnly, localFirst);
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

    public void appendListTypeParameters(Type[] t, boolean showNameOnly, boolean localFirst) {
        if (t.length > 0) {
            buffer.append("<");
            appendListTypeArray(t, showNameOnly, localFirst);
            buffer.append(">");
        }
    }

    public void resolveTypeParameters(Type[] tarr) {
        for (Type t :  tarr) {
            if (t instanceof Class) {
                Class<?> c = (Class<?>)t;
                resolveTypeParameters(c.getGenericInterfaces());
            } else if (t instanceof ParameterizedType) {
                ParameterizedType pt = (ParameterizedType) t;
                Class<?> raw = (Class<?>)pt.getRawType();
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

    boolean isBasicTypeVariable(Type t) {
        if (t instanceof TypeVariable) {
            Type[] bounds = ((TypeVariable<?>)t).getBounds();
            return bounds.length == 1 && bounds[0].equals(Object.class);
        } else {
            return false;
        }
    }

    boolean isBasicClass(Type t) {
        return t.equals(java.lang.Object.class) ||
            t.equals(java.util.Collection.class) ||
            t.equals(java.util.List.class) ||
            t.equals(java.util.SortedSet.class);
    }

    protected boolean isList(Class<?> currentClass) {
        return List.class.isAssignableFrom(currentClass);
    }

    protected boolean isListIterator(Class<?> currentClass) {
        return ListIterator.class.isAssignableFrom(currentClass);
    }
    
    boolean needtoWrap(Type t) {
        Type ct = getComponentType(t);
        return needsRemote(ct) || isBasicTypeVariable(ct)|| isBasicClass(ct);
    }

    protected boolean isBasicMethod(Method m) {
        String name = m.getName();
        return name.equals("equals") || name.equals("hashCode") || name.equals("toString");
    }
    
    protected boolean isCloneMethod(Method m) {
        String name = m.getName();
        return name.equals("clone");
    }

    protected void appendMethodHeader(Method m, boolean wrapGenericMethodName, boolean wrapRemoteClasses) {
        if (isBasicMethod(m)) {
            return;
        }
        
        buffer.append("  public ");
        Type[] typeParams = m.getTypeParameters();
        if (typeParams.length > 0) {
            appendTypeParameters(typeParams, wrapRemoteClasses);
            buffer.append(" ");
        }
        appendTypeInfo(m.getGenericReturnType(), true, wrapRemoteClasses);
        String name = m.getName();
        if (wrapGenericMethodName && (isBasicMethod(m) || isCloneMethod(m))) {
            name = "wrapped_" + name;
        }
        buffer.append(" " + name +"(");

        Type[] t = m.getGenericParameterTypes();
        for (int i = 0; i < t.length; i++) {
            if (i == t.length-1 && m.isVarArgs()) {
                if (t[i] instanceof Class && ((Class<?>) t[i]).isArray()) {
                    appendTypeInfo(((Class<?>) t[i]).getComponentType(), false, wrapRemoteClasses);
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
            if (!needsRemote(m.getDeclaringClass()) 
                    && !m.getDeclaringClass().equals(Comparable.class)) continue;
            if (isBasicMethod(m)) continue;
            if (m.getDeclaringClass().getName().equals("org.mmbase.bridge.BridgeList")) continue;
            
            appendMethod(m);
            methodsAdded.add(m.getName());
        }
    }

}
