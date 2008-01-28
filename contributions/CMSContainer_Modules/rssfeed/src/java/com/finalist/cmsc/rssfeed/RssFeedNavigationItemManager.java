package com.finalist.cmsc.rssfeed;

import java.util.List;

import net.sf.mmapps.commons.beans.MMBaseNodeMapper;

import org.mmbase.bridge.Cloud;
import org.mmbase.bridge.Node;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

import com.finalist.cmsc.beans.om.NavigationItem;
import com.finalist.cmsc.navigation.*;
import com.finalist.cmsc.rssfeed.beans.om.RssFeed;
import com.finalist.cmsc.rssfeed.publish.RssFeedPublisher;
import com.finalist.cmsc.rssfeed.tree.RssFeedTreeItemRenderer;
import com.finalist.cmsc.rssfeed.util.RssFeedUtil;

public class RssFeedNavigationItemManager implements NavigationItemManager {

    private static Logger log = Logging.getLoggerInstance(RssFeedNavigationItemManager.class.getName());
	
	private NavigationItemRenderer renderer = new RssFeedNavigationRenderer();
	private NavigationTreeItemRenderer treeRenderer = new RssFeedTreeItemRenderer();

	public NavigationItemRenderer getRenderer() {
		return renderer;
	}

	public String getTreeManager() {
		return RssFeedUtil.RSSFEED;
	}

    public boolean isRoot() {
        return false;
    }

	public NavigationItem loadNavigationItem(Integer key, Node node) {
        if (node == null || !RssFeedUtil.isRssFeedType(node)) {
            log.debug("RSS Feed not found: " + key);
            return null;
        }
        
        RssFeed rssFeed = MMBaseNodeMapper.copyNode(node, RssFeed.class);

        List<String> types = RssFeedUtil.getAllowedTypes(node);
        for (String type : types) {
            rssFeed.addContenttype(type);
        }

        Node contentChannel = RssFeedUtil.getContentChannel(node);
        if (contentChannel != null) {
            rssFeed.setContentChannel(contentChannel.getNumber());
        }
        
        return rssFeed;
	}

	public Object getPublisher(Cloud cloud, String type) {
		if(type.equals(getTreeManager())) {
			return new RssFeedPublisher(cloud);
		}
		else {
			return null;
		}
	}

    public NavigationTreeItemRenderer getTreeRenderer() {
        return treeRenderer;
    }

    public Class<? extends NavigationItem> getItemClass() {
        return RssFeed.class;
    }

   public void deleteNode(Node pageNode) {
      pageNode.delete(true);
   }
}
