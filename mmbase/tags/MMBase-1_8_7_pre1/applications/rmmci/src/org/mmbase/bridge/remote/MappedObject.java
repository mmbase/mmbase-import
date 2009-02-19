package org.mmbase.bridge.remote;
/**
 * interface that the remote implementation classes should
 * implement.
 * @author Kees Jongenburger <keesj@dds.nl>
 **/
public interface MappedObject {
    public String getMapperCode();
    public Object getWrappedObject();
}

