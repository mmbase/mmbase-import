/*
 * BugReport.java
 *
 * Created on June 7, 2002, 9:01 AM
 */

package org.mmbase.applications.bugtracker;

/**
 * Bugtracker is the mail class for the bugtracker
 * it contains the root bugtracker category and information
 * about the maintainer of the bugtracker
 * @mmbase-application-name Bugtracker
 *
 * @mmbase-nodemanager-name bugtracker
 * @mmbase-nodemanager-field name string 50
 *
 * @mmbase-relationmanager-name maintainerrel
 * @mmbase-relationmanager-source bugtracker
 * @mmbase-relationmanager-destination bugtrackeruser
 *
 * @mmbase-relationmanager-name subcategoryrel
 * @mmbase-relationmanager-nodemanager catrel
 * @mmbase-relationmanager-directionality unidirectional
 * @mmbase-relationmanager-source bugtracker
 * @mmbase-relationmanager-destination bugcategory
 */
public class Bugtracker {
    
    /** Creates a new instance of BugReport */
    public Bugtracker() {
    }
    
    /**
     * bugreports are stored in a hirachical scruture
     * this method returns an empty root category with the
     * containing the sub categories
     **/
    public BugCategory getRootCategory(){
        return new BugCategory();
    }
    
    /**
     * @return the list of maintainers of the bugtracker
     **/
    public BugtrackerUsers getBugtrackerMaintainers(){
        return new BugtrackerUsers();
    }
    
    public String getVersion(){
        return "$Id: Bugtracker.java,v 1.4 2002-06-27 19:20:29 kees Exp $";
    }
}
