package org.mmbase.applications.templateengine;
import java.util.*;

/**
 * Collection for Components
 * @author keesj
 */
public class Components extends Vector implements Comparator{
    public Component getComponent(int index) {
        return (Component)get(index);
    };
    
    public Component getComponentByName(String name){
    	for (int x = 0 ;x < size() ; x++){
    		Component c = getComponent(x);
    		if (c.getName().equals(name)) return c;
    	}
    	return null;
    }

    
    public int compare(Object o1, Object o2) {
    	return ((Component)o1).getName().compareTo(((Component)o2).getName());
    }
    
    
    
}
