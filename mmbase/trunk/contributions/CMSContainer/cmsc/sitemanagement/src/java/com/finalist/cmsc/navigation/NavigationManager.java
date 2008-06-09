package com.finalist.cmsc.navigation;

import java.util.ArrayList;
import java.util.List;

import org.mmbase.bridge.*;

import com.finalist.cmsc.beans.om.NavigationItem;

/**
 * TODO: when the component frame work is done, this should be done by this
 * framework
 *
 * @author freek
 */
public final class NavigationManager {

   private static List<NavigationItemManager> managers = new ArrayList<NavigationItemManager>();

   private NavigationManager() {
      // Access object for navigation managers
   }

   public static void registerNavigationManager(NavigationItemManager manager) {
       if (manager.isRoot()) {
           managers.add(0, manager);
       }
       else {
           managers.add(manager);
       }
   }

   public static List<NavigationItemManager> getNavigationManagers() {
      return managers;
   }

    public static NavigationItemManager getNavigationManager(Node parentNode) {
        NodeManager nm = parentNode.getNodeManager();
        String managerName = nm.getName();
        try {
            while (!"object".equals(managerName)) {
                NavigationItemManager manager = getNavigationManager(managerName);
                if (manager != null) {
                    return manager;
                }
                nm = nm.getParent();
                managerName = nm.getName();
            }
        }
        catch (NotFoundException nfe) {
           // Ran out of NodeManager parents
        }

        return null;
    }

    public static NavigationItemManager getNavigationManager(String managerName) {
        for (NavigationItemManager manager : managers) {
            if (manager.getTreeManager().equals(managerName)) {
                return manager;
            }
        }
        return null;
    }

    public static com.finalist.cmsc.navigation.NavigationItemRenderer getRenderer(NavigationItem item) {
        for (NavigationItemManager manager : managers) {
            if (manager.getItemClass().equals(item.getClass())) {
                return manager.getRenderer();
            }
        }
        return null;
    }
}
