package org.mmbase.util;
import org.mmbase.util.transformers.CharTransformer;
import java.util.*;

/**
 * @since MMBase-2.0
 */
public class BasicCaster implements Caster {

    @Override
    public <C> C toType(Class<C> type, Object cloud, Object value) throws NotRecognized {
        throw NotRecognized.INSTANCE;
    }
    @Override
    public Object wrap(final Object o, final CharTransformer escaper) throws NotRecognized {
        throw NotRecognized.INSTANCE;
    }
    @Override
    public Object unWrap(final Object o) throws NotRecognized {
        throw NotRecognized.INSTANCE;
    }
    @Override
    public Map toMap(Object o) throws NotRecognized {
        throw NotRecognized.INSTANCE;
    }
    @Override
    public int toInt(Object i) throws NotRecognized {
        throw NotRecognized.INSTANCE;
    }
    @Override
    public long toLong(Object i) throws NotRecognized {
        throw NotRecognized.INSTANCE;
    }
    @Override
    public float toFloat(Object i) throws NotRecognized {
        throw NotRecognized.INSTANCE;
    }
    @Override
    public double toDouble(Object i) throws NotRecognized {
        throw NotRecognized.INSTANCE;
    }
    @Override
    public String toString(Object o) throws NotRecognized {
        throw NotRecognized.INSTANCE;
    }
    @Override
    public boolean toBoolean(Object o) throws NotRecognized {
        throw NotRecognized.INSTANCE;
    }

    @Override
    public boolean isStringRepresentable(Class<?> type) {
        return false;
    }


}