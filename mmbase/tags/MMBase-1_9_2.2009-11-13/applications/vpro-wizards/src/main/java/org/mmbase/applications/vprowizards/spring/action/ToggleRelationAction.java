/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/ 
package org.mmbase.applications.vprowizards.spring.action;

import java.util.*;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.mmbase.applications.vprowizards.spring.cache.CacheFlushHint;
import org.mmbase.applications.vprowizards.spring.util.DateTime;
import org.mmbase.bridge.*;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;
import org.springframework.web.multipart.MultipartFile;

/**
 * This action is to replace the the CreateCheckboxRelation and UpdateCheckboxRelation actions from the previous version
 * of the backing code. This action adds one field to the {@link CreateRelationAction}, 'relate'. When this field is
 * set (with 'true') A relation if type 'role' is created between the source and destination. If 'relate' has any other
 * or no value, and there is a relation of given role between the source and destination node, it is deleted. This
 * action can be is used by the CheckboxRelation and RadiobuttonRelation tags. This action is a bit of a strange
 * specialization of the {@link CreateRelationAction} class, because not all things you can do to that action (like
 * setting fields) is useful for this action. This is not briliant design, but to make it a little bit more solid all
 * unsupported setters have been overridden, and do no throw an {@link UnsupportedOperationException}
 * 
 * @author Ernst Bunders
 * 
 */
public class ToggleRelationAction extends CreateRelationAction {
	private static final Logger log = Logging.getLoggerInstance(ToggleRelationAction.class);
	
	private String relate;
	private boolean isNodeCreated = false;
	private List<Integer> nodesDelted = new ArrayList<Integer>();

	public String getRelate() {
		return relate;
	}

	/**
	 * when this is set to 'true' and the relation does not exist yet, the relation is created.
	 * Otherwise the relation is deleted if it exists.
	 * @param relate
	 */
	public void setRelate(String relate) {
		this.relate = relate;
	}

	@Override
	protected Node doCreateNode(Transaction transaction, Map<String, Node> idMap, HttpServletRequest request) {
		// if relate is not empty and there is not a relation between source and destination yet, create it.
//		NodeList nl = sourceNode.getRelatedNodes(destinationNode.getNodeManager(), role, null);
		RelationList rl = sourceNode.getRelations(role, destinationNode.getNodeManager(), "destination");
		if (rl.size() > 0) {
			// we have a relation
			log.debug("relation already exist");
			if (!StringUtils.isBlank(relate) && "true".equals(relate.toLowerCase())) {
				// and we must relate: do nothing
				log.debug("create is true: we do nothing");
			} else {
				// we must undo the relation
				log.debug("create is false: we delete the node");
				for (int i = 0; i < rl.size(); i++) {
					nodesDelted.add(rl.getRelation(i).getNumber());
					rl.getRelation(i).delete(true);
				}
			}

		} else {
			// we don't have relation yet
			log.debug("relation doesn't exist yet");
			if (!StringUtils.isBlank(relate) && "true".equals(relate.toLowerCase())) {
				// we must create a relation
				log.debug("create is true: we must create the relation");
				Node newRelation = super.doCreateNode(transaction, idMap, request);
				if (!hasErrors()) {
					isNodeCreated = true;
					return newRelation;
				}else{
					log.warn("error creating the relation");
				}
			} else {
				log.debug("create is false, do nothing");
				// and we don't have to create: do nothing
			}
		}
		return null;
	}

	@Override
	protected void createCacheFlushHints() {
		if (isNodeCreated) {
			super.createCacheFlushHints();
		} else {
			// perhaps one or more relations are deleted
			for (int nodenr : nodesDelted) {
				CacheFlushHint hint = new CacheFlushHint(CacheFlushHint.TYPE_RELATION);
				hint.setDestinationNodeNumber(destinationNode.getNumber());
				hint.setSourceNodeNumber(sourceNode.getNumber());
				hint.setNodeNumber(nodenr);
				addCachFlushHint(hint);
			}
		}
	}

	/**
	 * for this action node can actually be null
	 */
	@Override
	protected boolean isNodeNullIllegal() {
		return false;
	}

	@Override
	public void setSortField(String sortField) throws UnsupportedOperationException {
		// TODO Auto-generated method stub
		super.setSortField(sortField);
	}

	@Override
	public void setSortPosition(String sortPosition) throws UnsupportedOperationException {
		throw new UnsupportedOperationException("you can not set this property on this action");
	}

	@Override
	public void setDateFields(Map<String, DateTime> dateFields) throws UnsupportedOperationException {
		throw new UnsupportedOperationException("you can not set this property on this action");
	}

	@Override
	public void setFields(Map<String, String> fields) throws UnsupportedOperationException {
		throw new UnsupportedOperationException("you can not set this property on this action");
	}

	@Override
	public void setFile(MultipartFile file) throws UnsupportedOperationException {
		throw new UnsupportedOperationException("you can not set this property on this action");
	}

	@Override
	public void setId(String id) throws UnsupportedOperationException {
		throw new UnsupportedOperationException("you can not set this property on this action");
	}

}
