package com.finalist.cmsc.alias;

import net.sf.mmapps.commons.beans.MMBaseNodeMapper;

import org.mmbase.bridge.Cloud;
import org.mmbase.bridge.Node;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

import com.finalist.cmsc.alias.beans.om.Alias;
import com.finalist.cmsc.alias.publish.AliasPublisher;
import com.finalist.cmsc.alias.tree.AliasTreeItemRenderer;
import com.finalist.cmsc.alias.util.AliasUtil;
import com.finalist.cmsc.beans.om.NavigationItem;
import com.finalist.cmsc.navigation.NavigationItemManager;
import com.finalist.cmsc.navigation.NavigationItemRenderer;
import com.finalist.cmsc.navigation.NavigationTreeItemRenderer;

public class AliasNavigationItemManager implements NavigationItemManager {

    private static Logger log = Logging.getLoggerInstance(AliasNavigationItemManager.class.getName());
	
	private NavigationItemRenderer renderer = new AliasNavigationRenderer();
	private NavigationTreeItemRenderer treeRenderer = new AliasTreeItemRenderer();

	public NavigationItemRenderer getRenderer() {
		return renderer;
	}

	public String getTreeManager() {
		return AliasUtil.ALIAS;
	}

    public boolean isRoot() {
        return false;
    }

	public NavigationItem loadNavigationItem(Integer key, Node node) {
        if (node == null || !AliasUtil.isAliasType(node)) {
            log.debug("Alias not found: " + key);
            return null;
        }
        
        return (Alias) MMBaseNodeMapper.copyNode(node, Alias.class);
	}

	public Object getPublisher(Cloud cloud, String type) {
		if(type.equals(getTreeManager())) {
			return new AliasPublisher(cloud);
		}
		else {
			return null;
		}
	}

    public NavigationTreeItemRenderer getTreeRenderer() {
        return treeRenderer;
    }

    public Class<? extends NavigationItem> getItemClass() {
        return Alias.class;
    }

   public void deleteNode(Node pageNode) {
      pageNode.delete();
   }
}
