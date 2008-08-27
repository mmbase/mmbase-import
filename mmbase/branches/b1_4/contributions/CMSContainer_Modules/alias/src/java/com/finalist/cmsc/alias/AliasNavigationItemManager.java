package com.finalist.cmsc.alias;

import net.sf.mmapps.commons.beans.MMBaseNodeMapper;

import org.apache.commons.lang.StringUtils;
import org.mmbase.bridge.*;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

import com.finalist.cmsc.alias.beans.om.Alias;
import com.finalist.cmsc.alias.publish.AliasPublisher;
import com.finalist.cmsc.alias.tree.AliasTreeItemRenderer;
import com.finalist.cmsc.alias.util.AliasUtil;
import com.finalist.cmsc.beans.om.NavigationItem;
import com.finalist.cmsc.navigation.*;

public class AliasNavigationItemManager implements NavigationItemManager {

   private static final Logger log = Logging.getLoggerInstance(AliasNavigationItemManager.class.getName());

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

        Alias alias = MMBaseNodeMapper.copyNode(node, Alias.class);
        
        Node page = AliasUtil.getPage(node);
        if (page != null) {
            alias.setPage(page.getNumber());
        }
        else {
            String externalUrl = AliasUtil.getUrlStr(node);
            if (StringUtils.isNotEmpty(externalUrl)) {
                alias.setUrl(externalUrl);
            }
        }

        return alias;
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
      pageNode.delete(true);	//Also delete related items
   }
}
