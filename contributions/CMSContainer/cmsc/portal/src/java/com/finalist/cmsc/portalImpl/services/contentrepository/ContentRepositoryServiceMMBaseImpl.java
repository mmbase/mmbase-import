/*

 This software is OSI Certified Open Source Software.
 OSI Certified is a certification mark of the Open Source Initiative.

 The license (Mozilla version 1.0) can be read at the MMBase site.
 See http://www.MMBase.org/license

 */
package com.finalist.cmsc.portalImpl.services.contentrepository;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.servlet.ServletConfig;

import net.sf.mmapps.commons.beans.MMBaseNodeMapper;
import net.sf.mmapps.commons.beans.NodetypeBean;
import net.sf.mmapps.commons.bridge.CloudUtil;
import net.sf.mmapps.modules.cloudprovider.CloudProvider;
import net.sf.mmapps.modules.cloudprovider.CloudProviderFactory;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mmbase.bridge.Cloud;
import org.mmbase.bridge.Node;
import org.mmbase.bridge.NodeList;
import org.mmbase.bridge.NodeManager;

import com.finalist.cmsc.beans.om.ContentChannel;
import com.finalist.cmsc.beans.om.ContentElement;
import com.finalist.cmsc.repository.ContentElementUtil;
import com.finalist.cmsc.repository.RepositoryUtil;
import com.finalist.cmsc.security.SecurityUtil;
import com.finalist.cmsc.security.UserRole;
import com.finalist.pluto.portalImpl.util.Properties;

/**
 * This class is a static accessor for a <code>ContentRepositoryService</code>
 * implementation.
 * 
 * @author Wouter Heijke
 */
public class ContentRepositoryServiceMMBaseImpl extends ContentRepositoryService {
	private static Log log = LogFactory.getLog(ContentRepositoryServiceMMBaseImpl.class);

	private CloudProvider cloudProvider;

	 protected void init(ServletConfig aConfig, Properties aProperties) throws Exception {		
		this.cloudProvider = CloudProviderFactory.getCloudProvider();
		log.info("ContentRepositoryService STARTED");
	}

    private int countContentElements(Node channel, List<String> contenttypes, String orderby, String direction, boolean useLifecycle, String archive, int offset, int maxNumber) {
        if (channel != null) {
            return RepositoryUtil.countLinkedElements(channel, contenttypes, orderby, direction, useLifecycle, archive, offset, maxNumber); 
        }
        return -1;
    }
     
	private List<ContentElement> getContentElements(Node channel, List<String> contenttypes, String orderby, String direction, boolean useLifecycle, String archive, int offset, int maxNumber) {
		List<ContentElement> result = new ArrayList<ContentElement>();
		if (channel != null) {
			NodeList l = RepositoryUtil.getLinkedElements(channel, contenttypes, orderby, direction, useLifecycle, archive, offset, maxNumber); 
			for (int i = 0; i < l.size(); i++) {
				Node currentNode = l.getNode(i);
				ContentElement e = (ContentElement) MMBaseNodeMapper.copyNode(currentNode, ContentElement.class);
				result.add(e);
			}
		}
		return result;
	}
	
    public List<ContentElement> getContentElements(Node channel) {
        return getContentElements(channel, null, null, null, false, null, -1, -1);
    }
    
	public List<ContentElement> getContentElements(ContentChannel channel) {
		Cloud cloud = getCloud();
		if (channel != null) {
			Node nc = cloud.getNode(channel.getId());
			return getContentElements(nc);
		}
		return null;
	}

    public List<ContentElement> getContentElements(String channel) {
        Cloud cloud = getCloud();
        if (channel != null) {
            Node chan = cloud.getNode(channel);
            return getContentElements(chan);
        }
        return null;
    }

    public int countContentElements(String channel, List<String> contenttypes, String orderby, String direction, boolean useLifecycle, String archive, int offset, int maxNumber) {
        Cloud cloud = getCloud();
        if (channel != null) {
            Node chan = cloud.getNode(channel);
            return countContentElements(chan, contenttypes, orderby, direction, useLifecycle, archive, offset, maxNumber);
        }
        return -1;
    }
    
	public List<ContentElement> getContentElements(String channel, List<String> contenttypes, String orderby, String direction, boolean useLifecycle, String archive, int offset, int maxNumber) {
		Cloud cloud = getCloud();
		if (channel != null) {
			Node chan = cloud.getNode(channel);
			return getContentElements(chan, contenttypes, orderby, direction, useLifecycle, archive, offset, maxNumber);
		}
		return null;
	}

    private List<ContentChannel> getContentChannels(Node channel) {
        List<ContentChannel> result = new ArrayList<ContentChannel>();
        if (channel != null) {
            NodeList l = RepositoryUtil.getOrderedChildren(channel); 
            for (int i = 0; i < l.size(); i++) {
                Node currentNode = l.getNode(i);
                ContentChannel e = (ContentChannel) MMBaseNodeMapper.copyNode(currentNode, ContentChannel.class);
                result.add(e);
            }
        }
        return result;
    }
    
    public List<ContentChannel> getContentChannels(ContentChannel channel) {
        Cloud cloud = getCloud();
        if (channel != null) {
            Node nc = cloud.getNode(channel.getId());
            return getContentChannels(nc);
        }
        return null;
    }

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
	public List<NodetypeBean> getContentTypes() {
		Cloud cloud = getCloud();
		List types = ContentElementUtil.getContentTypes(cloud);
        
        List<NodetypeBean> result = new ArrayList<NodetypeBean>();
        for (Iterator iter = types.iterator(); iter.hasNext();) {
            NodeManager nm = (NodeManager) iter.next();
            NodetypeBean ct = (NodetypeBean) MMBaseNodeMapper.copyNode(nm, NodetypeBean.class);
            result.add(ct);
        }
		return result;
	}

    public boolean mayEdit(String number) {
        boolean result = false;
        try {
            UserRole role = null;
            Cloud cloud = getUserCloud();
            Node node = cloud.getNode(number);
            if (RepositoryUtil.isChannel(node)) {
                role = RepositoryUtil.getRoleForUser(cloud, node, false);
            }
            
            if (ContentElementUtil.isContentElement(node)) {
                Node channel = RepositoryUtil.getCreationChannel(node);
                role = RepositoryUtil.getRoleForUser(cloud, channel, false);
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
    
    private Cloud getUserCloud() {
        Cloud cloud = CloudUtil.getCloudFromThread();
        if (cloud == null) {
            log.warn("User cloud not found in thread; make sure that the user cloud is bound");
            cloud = cloudProvider.getAdminCloud();
        }
        return cloud;
    }
    
	private Cloud getCloud() {
		Cloud cloud = cloudProvider.getAnonymousCloud();
		return cloud;
	}

}
