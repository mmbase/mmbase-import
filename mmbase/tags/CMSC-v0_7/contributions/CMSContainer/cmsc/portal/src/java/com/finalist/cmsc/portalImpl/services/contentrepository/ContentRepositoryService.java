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
import com.finalist.pluto.portalImpl.services.Service;

/**
 * @author Wouter Heijke
 */
public abstract class ContentRepositoryService extends Service {

	abstract public List<ContentElement> getContentElements(ContentChannel channel);

	abstract public List<NodetypeBean> getContentTypes();

    abstract public List<ContentElement> getContentElements(String channel);

    abstract public int countContentElements(String channel, List<String> contenttypes, String orderby, String direction, boolean useLifecycle, int offset, int maxNumbers);

	abstract public List<ContentElement> getContentElements(String channel, List<String> contenttypes, String orderby, String direction, boolean useLifecycle, int offset, int maxNumbers);

    abstract public List<ContentChannel> getContentChannels(ContentChannel channel);

    abstract public List<ContentChannel> getContentChannels(String channel);

    abstract public boolean mayEdit(String number);

}
