package org.mmbase.applications.mmbob.gui;

import java.util.*;

import org.mmbase.applications.mmbob.*;

/**
 * This class contians all the function methods concerning properties.
 * @author ebunders
 *
 */
public class PropertiesController {
    /**
     * get the properties of a given forum
     * @param forumid
     * @return
     */
    public List getForumProperties(String forumid) {
        List properties = new ArrayList();
        Forum forum = ForumManager.getForum(forumid);
        if (forum != null) {
            Map property;
            for (Iterator i = forum.getPropertyNames(); i.hasNext();) {
                String key = (String) i.next();
                property = new HashMap(2);
                property.put("name", key);
                property.put("value", forum.getProperty(key));
                properties.add(property);
            }
        }
        return properties;
    }

    /**
     * get the globally configured properties
     * @param forumid
     * @return
     */
    public  List getGlobalProperties() {
        List properties = new ArrayList();
        Map property;
        for (Iterator i = ForumManager.getPropertyNames(); i.hasNext();) {
            String key = (String) i.next();
            property = new HashMap(2);
            property.put("name", key);
            property.put("value", ForumManager.getProperty(key));
            properties.add(property);
        }
        return properties;
    }
    
    /**
     * Dous this forum have properties
     * @param forumid
     * @return
     */
    public  boolean forumHasProperties(String forumid){
        Forum forum = ForumManager.getForum(forumid);
        if (forum != null) {
            return forum.hasProperties();
        }
        return false;
    }
    
    
    /**
     * Are there any properties in the global forum configuration?
     * @return
     */
    public  boolean globalHasProperties(){
        return ForumManager.hasProperties();
    }
    
    /**
     * Are there any properties in either the given forum or the 
     * global forum configuration?
     * @param forumid
     * @return
     */
    public  boolean hasProperties(String forumid){
        return forumHasProperties(forumid) || globalHasProperties();
    }
    
    
    /**
     * set a property on a specific forum
     * Can only be done by administrator
     * @param forumid
     * @param posterid
     * @param propertyname
     * @param propertyvalue
     */
    public  void setForumProperty(Integer forumid, Integer posterid, String propertyname, String propertyvalue){
        Forum forum = ForumManager.getForum(forumid.toString());
        if (isAdministrator(forumid, posterid)) {
            forum.setProperty(propertyname, propertyvalue);
            forum.saveConfig();
        }
    }
    
    /**
     * set a property on the global forum configuration
     * Can only be done by administrator
     * @param forumid
     * @param posterid
     * @param propertyname
     * @param propertyvalue
     */
    public  void setGlobalProperty(Integer forumid, Integer posterid, String propertyname, String propertyvalue){
        if(isAdministrator(forumid, posterid)){
            ForumManager.setProperty(propertyname, propertyvalue);
            ForumManager.saveConfig();
        }
    }
    
    protected  boolean isAdministrator(Integer forumid, Integer posterid){
            Forum forum = ForumManager.getForum(forumid.toString());
            if (forum != null) {
                Poster activePoster = forum.getPoster(posterid.intValue());
                if (forum.isAdministrator(activePoster.getNick())) {
                    return true;
                }
            }
        return false;
    }

}
