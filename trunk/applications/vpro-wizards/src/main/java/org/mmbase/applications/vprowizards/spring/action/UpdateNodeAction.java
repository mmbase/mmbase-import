/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/ 
package org.mmbase.applications.vprowizards.spring.action;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.mmbase.applications.vprowizards.spring.cache.CacheFlushHint;
import org.mmbase.bridge.*;

/**
 * This class finds the node that is identified by the field 'nodenr', and updates it with the given field values. 
 * @author Ernst Bunders
 *
 */
public class UpdateNodeAction extends AbstractNodeAction {

	private String nodenr;

	@Override
	protected final void createCacheFlushHints() {
		CacheFlushHint cacheFlushHint = new CacheFlushHint(CacheFlushHint.TYPE_NODE);
		cacheFlushHint.setNodeNumber(getNode().getNumber());
		addCachFlushHint(cacheFlushHint);
	}

	@Override
	protected final Node createNode(Transaction transaction, Map<String,Node>idMap, HttpServletRequest request) {
		if (StringUtils.isBlank(nodenr)) {
			addGlobalError("error.property.required", new String[] { "nodenr", this.getClass().getName() });
			return null;
		} else {
			try {
				return transaction.getNode(nodenr);
			} catch (NotFoundException e) {
				addGlobalError("error.node.notfound", new String[] { nodenr });
				return null;
			}
		}
	}

	public final void setNodenr(String nodenr) {
		this.nodenr = nodenr;
	}

	public final String getNodenr() {
		return this.nodenr;
	}

}
