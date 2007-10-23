package com.finalist.cmsc.navigation;

import java.util.ArrayList;
import java.util.List;


/**
 * TODO: when the component frame work is done, this should be done by this framework
 * 
 * @author freek
 *
 */
public class NavigationManager {
	
	private static ArrayList<NavigationItemManager> managers = new ArrayList<NavigationItemManager>();
	
	public static void registerNavigationManager(NavigationItemManager manager) {
		managers.add(manager);
	}
	
	public static List<NavigationItemManager> getNavigationManagers() {
		return managers;
	}
}
