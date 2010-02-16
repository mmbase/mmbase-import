package org.mmbase.util;
import org.mmbase.util.transformers.CharTransformer;
import java.util.*;

/**
 * @since MMBase-2.0
 */
public class BasicCaster implements Caster {

    public <C> C toType(Class<C> type, Object cloud, Object value) throws NotRecognized {
        throw NotRecognized.INSTANCE;
    }
    public Object wrap(final Object o, final CharTransformer escaper) throws NotRecognized {
        throw NotRecognized.INSTANCE;
    }
    public Object unWrap(final Object o) throws NotRecognized {
        throw NotRecognized.INSTANCE;
    }
    public Map toMap(Object o) throws NotRecognized {
        throw NotRecognized.INSTANCE;
    }
    public int toInt(Object i) throws NotRecognized {
        throw NotRecognized.INSTANCE;
    }
    public long toLong(Object i) throws NotRecognized {
        throw NotRecognized.INSTANCE;
    }
    public float toFloat(Object i) throws NotRecognized {
        throw NotRecognized.INSTANCE;
    }
    public double toDouble(Object i) throws NotRecognized {
        throw NotRecognized.INSTANCE;
    }
    public String toString(Object o) throws NotRecognized {
        throw NotRecognized.INSTANCE;
    }
    public boolean toBoolean(Object o) throws NotRecognized {
        throw NotRecognized.INSTANCE;
    }

    public boolean isStringRepresentable(Class<?> type) {
        return false;
    }


}