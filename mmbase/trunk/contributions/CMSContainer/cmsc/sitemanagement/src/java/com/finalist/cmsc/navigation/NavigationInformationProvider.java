package com.finalist.cmsc.navigation;

import org.mmbase.bridge.Node;

import com.finalist.cmsc.beans.om.NavigationItem;
import com.finalist.cmsc.security.UserRole;
import com.finalist.tree.TreeElement;
import com.finalist.tree.TreeOption;

public interface NavigationInformationProvider {

	String getOpenAction(Node parentNode, boolean secure);

	TreeElement createElement(NavigationItem item, UserRole role, String action);

	TreeOption createOption(String icon, String message, String action);

}
