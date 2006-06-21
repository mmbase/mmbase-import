/*

 This software is OSI Certified Open Source Software.
 OSI Certified is a certification mark of the Open Source Initiative.

 The license (Mozilla version 1.0) can be read at the MMBase site.
 See http://www.MMBase.org/license

 */
package com.finalist.cmsc.portalImpl.services.contentrepository;

import java.util.List;

import net.sf.mmapps.commons.beans.NodetypeBean;

import com.finalist.cmsc.beans.om.ContentChannel;
import com.finalist.cmsc.beans.om.ContentElement;
import com.finalist.pluto.portalImpl.services.ServiceManager;

/**
 * @author Wouter Heijke
 */
public class ContentRepository {
	private final static ContentRepositoryService cService = (ContentRepositoryService) ServiceManager
			.getService(ContentRepositoryService.class);

	public static List<ContentElement> getContentElements(ContentChannel channel) {
		return cService.getContentElements(channel);
	}
    
    public static List<ContentElement> getContentElements(String channel) {
        return cService.getContentElements(channel);
    }

    public static int countContentElements(String channel, List<String> contenttypes, String orderby, String direction, boolean useLifecycle, int offset, int maxNumbers) {
        return cService.countContentElements(channel, contenttypes, orderby, direction, useLifecycle, offset, maxNumbers);
    }
    
	public static List<ContentElement> getContentElements(String channel, List<String> contenttypes, String orderby, String direction, boolean useLifecycle, int offset, int maxNumbers) {
		return cService.getContentElements(channel, contenttypes, orderby, direction, useLifecycle, offset, maxNumbers);
	}

    public static List<ContentChannel> getContentChannels(ContentChannel channel) {
        return cService.getContentChannels(channel);
    }

    public static List<ContentChannel> getContentChannels(String channel) {
        return cService.getContentChannels(channel);
    }

    
	public static List<NodetypeBean> getContentTypes() {
		return cService.getContentTypes();
	}

    public static boolean mayEdit(String number) {
        // TODO Auto-generated method stub
        return cService.mayEdit(number);
    }

}
