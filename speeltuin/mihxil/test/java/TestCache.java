import org.mmbase.cache.Cache;
import java.util.*;

/**
 * Tests the functionality of Cache (= LRUHashtable)
 *
   jikes TestCache.java 
   java -Dmmbase.config=/home/mmbase/mmbase-app/WEB-INF/config TestCache
 */

public class TestCache {

    static {
        try {
            org.mmbase.module.core.MMBaseContext.init();             
        } catch (Exception e) {
            System.err.println(e.toString());
        } // end of try-catch        
    }

    private static org.mmbase.cache.Cache cache = new org.mmbase.cache.Cache(3) {            
            public String getName()        { return "test cache"; }
            public String getDescription() { return "Test LRUHashtable"; }
            public Object get(Object o) {
                Object r = super.get(o);
                if (r == null) {
                    r = o.toString().toUpperCase();
                    super.put(o, r);
                }
                return r;                
            }
            
        };
    
    private static void show(String comments) {
        System.out.println("Cache contents:");
        System.out.println("(" + comments + ")");
        Iterator i = cache.getOrderedEntries().iterator();
        while (i.hasNext()) {
            Map.Entry entry = (Map.Entry) i.next();
            System.out.println(entry.getKey() + ": " + entry.getValue());
        }
    }


    public static void main (String[] args) throws Exception {

        System.out.println("getting once some things:");
        cache.get("a");
        cache.get("b");
        cache.get("c");
        cache.get("d");
        cache.get("e");
        show("Should see c, d and e");
        cache.clear();
        show("Should be empty");
        cache.get("a");
        cache.get("b");
        cache.get("c");
        cache.get("a");
        cache.get("d");
        show("Should see a, c and d");
        
        Enumeration e = cache.getOrderedElements();
        while (e.hasMoreElements()) {
            Object value = e.nextElement();
            System.out.println("value: " + value);
            
        } 
        show("Should see same values in same order");
 
        System.out.println("Showing while removing");
        e = cache.keys();
        while (e.hasMoreElements()) {
            Object key = e.nextElement();
            System.out.println("removing key: " + key);
            cache.remove(key);
            show("emptier");
            
        }
        show("Should be empty");

        cache.get("a");
        cache.get("b");
        cache.get("c");
        System.out.println("Using while removing");
        e = cache.keys();
        while (e.hasMoreElements()) {
            Object key = e.nextElement();
            System.out.println("removing key: " + key);
            cache.remove(key);
            cache.get("f");
            show("emptier");
            
        }
        show("Should 'f'");
       
     
        
    } 
    
}
