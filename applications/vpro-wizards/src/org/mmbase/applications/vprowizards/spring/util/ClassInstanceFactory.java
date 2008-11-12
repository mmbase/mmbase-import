package org.mmbase.applications.vprowizards.spring.util;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;

/**
 * @javadoc
 * @author Ernst Bunders
 * @version $Id: ClassInstanceFactory.java,v 1.3 2008-11-12 19:41:28 michiel Exp $
 */

public class ClassInstanceFactory<T> {

    private Class<? extends T> clazz;

    public Class<? extends T> getClazz() {
        return clazz;
    }

    @SuppressWarnings("unchecked")
    public void setClassName(String className) {
        try {
            Class<? extends T> c = (Class<? extends T>) Class.forName(className);
            boolean pubNoarg = false;
            for (Constructor<?> m : c.getConstructors()) {
                if (m.getParameterTypes().length == 0 && Modifier.isPublic(m.getModifiers())) {
                    pubNoarg = true;
                    this.clazz = c;
                }
            }
            if (!pubNoarg) {
                throw new IllegalArgumentException(String.format("Class '%s' has no public no-arg constructor",
                        className));
            }
        } catch (ClassNotFoundException e) {
            throw new IllegalArgumentException(String.format("Class name '%s' can not be loaded", className));
        }
    }

    public T newInstance() {
        try {
            return clazz.newInstance();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

}
