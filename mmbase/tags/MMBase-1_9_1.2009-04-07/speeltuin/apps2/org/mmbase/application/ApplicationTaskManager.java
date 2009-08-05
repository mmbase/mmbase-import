/*
 
This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.
 
The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license
 
 */

package org.mmbase.application;

import org.mmbase.module.core.*;
import org.mmbase.application.tasks.*;
import org.mmbase.module.corebuilders.*;
import org.mmbase.module.builders.*;
import org.mmbase.util.*;
import org.mmbase.util.logging.*;

import java.util.*;
import java.io.*;
import java.lang.*;

import org.w3c.dom.*;

/**
 * Maintains and handles communication to ApplicationTasks for
 * applications. It creates a session like interface for other
 * interfaces to perform actions like install, save, mirror, check
 * with applicaitons in a sync or a-sync way.
 *
 * use of this interface instead of the direct api is prefered
 * because some application tasks are longrunning.
 *
 */

public class ApplicationTaskManager {
    
    private MMBase mmb;
    private ApplicationManager am;
    private int taskidcount=0;
    private Hashtable tasks;
    
    public ApplicationTaskManager(ApplicationManager parent, MMBase mmb) {
        am=parent;
        this.mmb=mmb;
        tasks=new Hashtable();
    }
    
    public int createTask(String type) {
        ApplicationTask task=null;
        if (type.equals("installapplication")) task=(ApplicationTask)new InstallApplication(taskidcount,this,mmb);
        if (type.equals("installreldef")) task=(ApplicationTask)new InstallRelDef(taskidcount,this,mmb);
        if (type.equals("installallreldefs")) task=(ApplicationTask)new InstallAllRelDefs(taskidcount,this,mmb);
        if (type.equals("installtyperel")) task=(ApplicationTask)new InstallTypeRel(taskidcount,this,mmb);
        if (type.equals("installalltyperels")) task=(ApplicationTask)new InstallAllTypeRels(taskidcount,this,mmb);
        if (type.equals("installdataset")) task=(ApplicationTask)new InstallDataSet(taskidcount,this,mmb);
        if (type.equals("installdisplay")) task=(ApplicationTask)new InstallDisplay(taskidcount,this,mmb);
        if (type.equals("uninstalldisplay")) task=(ApplicationTask)new UnInstallDisplay(taskidcount,this,mmb);
        if (type.equals("installbuilder")) task=(ApplicationTask)new InstallBuilder(taskidcount,this,mmb);
        if (type.equals("installallbuilders")) task=(ApplicationTask)new InstallAllBuilders(taskidcount,this,mmb);
        if (task!=null) {
            taskidcount=taskidcount+1;
            tasks.put(new Integer(taskidcount),task);
            task.init();
            return(taskidcount);
            
        }
        return(-1);
    }
    
    public ApplicationTask getTask(int taskid) {
        Object atask=tasks.get(new Integer(taskid));
        if (atask!=null) {
            return((ApplicationTask)atask);
        }
        return(null);
    }
    
    public boolean removeTask(int taskid) {
        if (tasks.containsKey(new Integer(taskid))) {
            tasks.remove(new Integer(taskid));
            return(true);
        }
        return(false);
    }
    
    public Enumeration getTasks() {
        return(null);
    }
    
    public int size() {
        return(tasks.size());
    }
    
    
}
