package org.mmbase.applications.vprowizards.spring.cache;

import java.util.ArrayList;
import java.util.List;

/**
 * dummy cache wrapper for testing
 * @author ebunders
 *
 */
public class DummyCacheWrapper implements CacheWrapper {
    
    private List<String> names = new ArrayList<String>();

    public void flushForName(String flushname) {
        names.add(flushname);
    }
    
    public List<String> getNames(){
        return names;
    }

}
