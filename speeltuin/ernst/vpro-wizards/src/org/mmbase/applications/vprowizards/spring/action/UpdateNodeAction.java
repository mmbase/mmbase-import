package org.mmbase.applications.vprowizards.spring.action;

import java.util.*;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.mmbase.applications.vprowizards.spring.GlobalError;
import org.mmbase.applications.vprowizards.spring.cache.CacheFlushHint;
import org.mmbase.bridge.Node;
import org.mmbase.bridge.NotFoundException;
import org.mmbase.bridge.Transaction;

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
