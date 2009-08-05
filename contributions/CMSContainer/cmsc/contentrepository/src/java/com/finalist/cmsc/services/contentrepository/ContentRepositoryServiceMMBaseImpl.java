/*

 This software is OSI Certified Open Source Software.
 OSI Certified is a certification mark of the Open Source Initiative.

 The license (Mozilla version 1.0) can be read at the MMBase site.
 See http://www.MMBase.org/license

 */
package com.finalist.cmsc.services.contentrepository;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletConfig;

import net.sf.mmapps.commons.bridge.CloudUtil;
import net.sf.mmapps.modules.cloudprovider.CloudProvider;
import net.sf.mmapps.modules.cloudprovider.CloudProviderFactory;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mmbase.bridge.*;

import com.finalist.cmsc.beans.MMBaseNodeMapper;
import com.finalist.cmsc.beans.NodetypeBean;
import com.finalist.cmsc.beans.om.ContentChannel;
import com.finalist.cmsc.beans.om.ContentElement;
import com.finalist.cmsc.repository.ContentElementUtil;
import com.finalist.cmsc.repository.RepositoryUtil;
import com.finalist.cmsc.security.SecurityUtil;
import com.finalist.cmsc.security.UserRole;
import com.finalist.cmsc.services.Properties;

/**
 * This class is a static accessor for a <code>ContentRepositoryService</code>
 * implementation.
 *
 * @author Wouter Heijke
 */
public class ContentRepositoryServiceMMBaseImpl extends ContentRepositoryService {
	private static Log log = LogFactory.getLog(ContentRepositoryServiceMMBaseImpl.class);

	private CloudProvider cloudProvider;

	 @Override
    protected void init(ServletConfig aConfig, Properties aProperties) throws Exception {
		this.cloudProvider = CloudProviderFactory.getCloudProvider();

		log.info("ContentRepositoryService STARTED");
	}

    private int countContentElements(Node channel, List<String> contenttypes, String orderby, String direction, boolean useLifecycle, String archive, int offset, int maxNumber, int year, int month, int day) {
        if (channel != null) {
            return RepositoryUtil.countLinkedElements(channel, contenttypes, orderby, direction, useLifecycle, archive, offset, maxNumber, year, month, day);
        }
        return -1;
    }

	private List<ContentElement> getContentElements(Node channel, List<String> contenttypes, String orderby, String direction, boolean useLifecycle, String archive, int offset, int maxNumber, int year, int month, int day) {
		List<ContentElement> result = new ArrayList<ContentElement>();
		if (channel != null) {
			NodeList l = RepositoryUtil.getLinkedElements(channel, contenttypes, orderby, direction, useLifecycle, archive, offset, maxNumber, year, month, day);
			for (int i = 0; i < l.size(); i++) {
				Node currentNode = l.getNode(i);
				ContentElement e = MMBaseNodeMapper.copyNode(currentNode, ContentElement.class);
				result.add(e);
			}
		}
		return result;
	}

    public List<ContentElement> getContentElements(Node channel) {
        return getContentElements(channel, null, null, null, false, null, -1, -1, -1, -1, -1);
    }

	@Override
    public List<ContentElement> getContentElements(ContentChannel channel) {
		Cloud cloud = getCloud();
		if (channel != null) {
			Node nc = cloud.getNode(channel.getId());
			return getContentElements(nc);
		}
		return null;
	}

    @Override
    public List<ContentElement> getContentElements(String channel) {
        Cloud cloud = getCloud();
        if (channel != null) {
            Node chan = cloud.getNode(channel);
            return getContentElements(chan);
        }
        return null;
    }

    @Override
    public int countContentElements(String channel, List<String> contenttypes, String orderby, String direction, boolean useLifecycle, String archive, int offset, int maxNumber, int year, int month, int day) {
        Cloud cloud = getCloud();
        if (channel != null) {
            Node chan = cloud.getNode(channel);
            return countContentElements(chan, contenttypes, orderby, direction, useLifecycle, archive, offset, maxNumber, year, month, day);
        }
        return -1;
    }

    @Override
    public int countContentElements(String channel, List<String> contenttypes, String orderby, String direction,
          boolean useLifecycle, String archive, int offset, int maxNumber, int year, int month, int day, int maxDays) {
       Cloud cloud = getCloud();
       if (channel != null) {
           Node chan = cloud.getNode(channel);
           return countContentElements(chan, contenttypes, orderby, direction, useLifecycle, archive, offset, maxNumber, year, month, day, maxDays);
       }
       return -1;
    }
   private int countContentElements(Node channel, List<String> contenttypes, String orderby, String direction,
         boolean useLifecycle, String archive, int offset, int maxNumber, int year, int month, int day, int maxDays) {
      if (channel != null) {
         return RepositoryUtil.countLinkedElements(channel, contenttypes, orderby, direction, useLifecycle, archive, offset, maxNumber, year, month, day, maxDays);
     }
     return -1;
   }

   @Override
    public List<ContentElement> getContentElements(String channel, List<String> contenttypes, String orderby, String direction, boolean useLifecycle, String archive, int offset, int maxNumber, int year, int month, int day) {
		Cloud cloud = getCloud();
		if (channel != null) {
			Node chan = cloud.getNode(channel);
			return getContentElements(chan, contenttypes, orderby, direction, useLifecycle, archive, offset, maxNumber, year, month, day);
		}
		return null;
	}

    private List<ContentChannel> getContentChannels(Node channel) {
        List<ContentChannel> result = new ArrayList<ContentChannel>();
        if (channel != null) {
            NodeList l = RepositoryUtil.getOrderedChildren(channel);
            for (int i = 0; i < l.size(); i++) {
                Node currentNode = l.getNode(i);
                ContentChannel e = MMBaseNodeMapper.copyNode(currentNode, ContentChannel.class);
                result.add(e);
            }
        }
        return result;
    }

    @Override
    public List<ContentChannel> getContentChannels(ContentChannel channel) {
        Cloud cloud = getCloud();
        if (channel != null) {
            Node nc = cloud.getNode(channel.getId());
            return getContentChannels(nc);
        }
        return null;
    }

    @Override
    public List<ContentChannel> getContentChannels(String channel) {
        Cloud cloud = getCloud();
        if (channel != null) {
            Node chan = cloud.getNode(channel);
            return getContentChannels(chan);
        }
        return null;
    }

	/*
	 * (non-Javadoc)
	 *
	 * @see net.sf.mmapps.commons.portalImpl.services.contentrepository.ContentRepositoryService#getContentElements()
	 */
	@Override
    public List<NodetypeBean> getContentTypes() {
		Cloud cloud = getCloud();
		List<NodeManager> types = ContentElementUtil.getContentTypes(cloud);

        List<NodetypeBean> result = new ArrayList<NodetypeBean>();
        for (NodeManager nm : types) {
            NodetypeBean ct = MMBaseNodeMapper.copyNode(nm, NodetypeBean.class);
            result.add(ct);
        }
		return result;
	}

    @Override
    public boolean mayEdit(String number) {
        boolean result = false;
        try {
            UserRole role = null;
            Cloud cloud = getUserCloud();
            Node node = cloud.getNode(number);
            if (RepositoryUtil.isContentChannel(node)) {
                role = RepositoryUtil.getRole(cloud, node, false);
            }

            if (ContentElementUtil.isContentElement(node)) {
                Node channel = RepositoryUtil.getCreationChannel(node);
                role = RepositoryUtil.getRole(cloud, channel, false);
            }
            result = role != null && SecurityUtil.isWriter(role);
        } catch (Exception e) {
            log.error("something went wrong checking edit (" + number + ")");
            if (log.isDebugEnabled()) {
                log.debug(e);
            }
        }
        return result;
    }


    @Override
    public ContentElement getContentElement(String elementId) {
        Cloud cloud = getUserCloud();
        try {
           Node node = cloud.getNode(elementId);
           if (node != null) {
              return MMBaseNodeMapper.copyNode(node, ContentElement.class);
           }
        } catch(NotFoundException e){
           log.debug("Node not found using element:" + elementId);
        }
        
        return null;
    }


    private Cloud getUserCloud() {
        Cloud cloud = CloudUtil.getCloudFromThread();
        if (cloud == null) {
            log.debug("User cloud not found in thread; make sure that the user cloud is bound");
            cloud = cloudProvider.getAdminCloud();
        }
        return cloud;
    }

	private Cloud getCloud() {
		Cloud cloud = cloudProvider.getAnonymousCloud();
		return cloud;
	}

   @Override
   public List<ContentElement> getContentElements(String channel, List<String> contenttypes, String orderby, String direction, boolean useLifecycle, String archive, int offset, int maxNumber, int year, int month, int day,int maxDays) {
      Cloud cloud = getCloud();
      List<ContentElement> result = new ArrayList<ContentElement>();
      if (channel != null) {
         Node chan = cloud.getNode(channel);
      
         if (chan != null) {
            NodeList l = RepositoryUtil.getLinkedElements(chan, contenttypes, orderby, direction, useLifecycle, archive, offset, maxNumber, year, month, day, maxDays);
            for (int i = 0; i < l.size(); i++) {
               Node currentNode = l.getNode(i);
               ContentElement e = MMBaseNodeMapper.copyNode(currentNode, ContentElement.class);
               result.add(e);
            }
         }
      }
      return result;
   }
}
