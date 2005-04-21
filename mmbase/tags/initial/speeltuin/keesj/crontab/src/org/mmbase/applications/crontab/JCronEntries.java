package org.mmbase.applications.crontab;


import java.util.*;
public class JCronEntries extends Vector{
    
    public JCronEntries() {
        super();
    }
    
    public JCronEntry getJCronEntry(int index){
        return (JCronEntry)get(index);
    }
    
    public JCronEntry getJCronEntry(String id){
        for (int x =0 ; x < size() ;x ++){
            JCronEntry entry = getJCronEntry(x);
            if (entry.getID().equals(id)){
                return entry;
            }
        }
        return null;
    }
}
