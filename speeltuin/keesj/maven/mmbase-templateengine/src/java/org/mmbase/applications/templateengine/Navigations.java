package org.mmbase.applications.templateengine;
import java.util.*;

public class Navigations extends Vector {
    public AbstractNavigation getNavigation(int index) {
        return (AbstractNavigation)get(index);
    };
    
    public Navigation getNavigationByName(String name){
    	for (int x = 0 ; x < size() ; x++){
    			Navigation nav = getNavigation(x);
    			if (nav.getName().equals(name)){
    				return nav;
    			}
    	}
    	return null;
    }
    
	public Navigation getNavigationById(String id){
		 for (int x = 0 ; x < size() ; x++){
				 Navigation nav = getNavigation(x);
				 if (nav.getName().equals(id)){
					 return nav;
				 }
		 }
		 return null;
	 }
}  