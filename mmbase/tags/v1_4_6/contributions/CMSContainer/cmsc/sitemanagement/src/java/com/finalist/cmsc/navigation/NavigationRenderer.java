package com.finalist.cmsc.navigation;

import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.mmbase.bridge.Node;

import com.finalist.cmsc.security.UserRole;
import com.finalist.cmsc.util.bundles.JstlUtil;
import com.finalist.tree.TreeCellRenderer;
import com.finalist.tree.TreeElement;
import com.finalist.tree.TreeModel;
import com.finalist.tree.TreeOption;

/**
 * Renderer of the Site management tree.
 * 
 * @author Nico Klasens (Finalist IT Group)
 */
public abstract class NavigationRenderer implements TreeCellRenderer {

    protected static final String FEATURE_WORKFLOW = "workflowitem";
	
	private String target = null;
    private HttpServletRequest request;
    private HttpServletResponse response;

    public NavigationRenderer(HttpServletRequest request, HttpServletResponse response, String target) {
        this.request = request;
        this.response = response;
        this.target = target;
    }

    /**
     * Render a node of a tree.
     * 
     * @see com.finalist.tree.TreeCellRenderer#getElement(TreeModel, Object, String)
     */
    public TreeElement getElement(TreeModel model, Object node, String id) {
        Node parentNode = (Node) node;
        if (id == null) {
            id = String.valueOf(parentNode.getNumber());
        }
        
        NavigationItemManager manager = NavigationManager.getNavigationManager(parentNode);
    	if(manager != null) {
    		TreeElement treeElement = manager.getTreeRenderer().getTreeElement(this, parentNode, model);
			return treeElement;
    	}
        return null;
    }

    public void addParentOptions(TreeElement treeElement, String id) {
        for (NavigationItemManager managerOption : NavigationManager.getNavigationManagers()) {
        	managerOption.getTreeRenderer().addParentOption(this, treeElement, id);
        }
    }

    private String getUrl(String url) {
        return response.encodeURL(url);
    }

    public String getIcon(Object node, UserRole role) {
        Node n = (Node) node;
        return getIcon(n.getNodeManager().getName(), role);
    }

    public String getIcon(String name, UserRole role) {
        return "type/" + name + "_"+role.getRole().getName()+".png";
    }
    
    public String getOpenAction(Node parentNode, boolean secure) {
        String contextPath = request.getContextPath();
        String action = String.format("/editors/site/NavigatorPanel.do?nodeId=%s",parentNode.getNumber());
        return contextPath+action;
    }

    public TreeOption createTreeOption(String icon, String message, String resourcebundle, String action) {
        String label = getLabel(message, resourcebundle);
        if (!action.startsWith("javascript:")) {
            action = getUrl(action);
        }
        return createOption(icon, label,  action, target);      
        
    }

	public String getLabel(String message, String resourcebundle) {
		Locale locale = JstlUtil.getLocale(request);
        String label = JstlUtil.getMessage(resourcebundle, locale, message);
		return label;
	}
    
    public TreeOption createTreeOption(String icon, String message, String action) {
        String label = JstlUtil.getMessage(request, message);
        if (!action.startsWith("javascript:")) {
            action = getUrl(action);
        }
        return createOption(icon, label,  action, target);    	
    }

    public TreeElement createElement(Node parentNode, UserRole role, String name, String fragment, boolean secure) {
        String id = String.valueOf(parentNode.getNumber());
        return createElement(getIcon(parentNode, role), id, name, fragment, getOpenAction(parentNode, secure), target);
    }
    
    public abstract TreeOption createOption(String icon, String label, String action, String target);

    public abstract TreeElement createElement(String icon, String id, String name, String fragment, String action, String target);

    public boolean showChildren(Object node) {
       Node parentNode = (Node) node;
       NavigationItemManager manager = NavigationManager.getNavigationManager(parentNode);

       if(manager != null) {
          return manager.getTreeRenderer().showChildren(parentNode);
       }
       return false;
    }
}