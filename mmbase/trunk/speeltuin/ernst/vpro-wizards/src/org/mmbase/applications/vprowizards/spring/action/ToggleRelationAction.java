package org.mmbase.applications.vprowizards.spring.action;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.mmbase.applications.vprowizards.spring.cache.CacheFlushHint;
import org.mmbase.applications.vprowizards.spring.util.DateTime;
import org.mmbase.bridge.Node;
import org.mmbase.bridge.NodeList;
import org.mmbase.bridge.Transaction;
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
	private String relate;
	private boolean isNodeCreated = false;
	private List<Integer> nodesDelted = new ArrayList<Integer>();

	public String getRelate() {
		return relate;
	}

	public void setRelate(String relate) {
		this.relate = relate;
	}

	@Override
	protected Node doCreateNode(Transaction transaction, Map<String, Node> idMap, HttpServletRequest request) {
		// if relate is not empty and there is not a relation between source and destination yet, create it.
		NodeList nl = sourceNode.getNodeManager()
				.getRelatedNodes(destinationNode.getNodeManager(), role, "destination");
		if (nl.size() > 0) {
			// we have a relation
			if (!StringUtils.isBlank(relate) && "true".equals(relate.toLowerCase())) {
				// and we must relate: do nothing
			} else {
				// we must undo the relation
				for (int i = 0; i < nl.size(); i++) {
					nodesDelted.add(nl.getNode(i).getNumber());
					nl.getNode(i).delete();
				}
			}

		} else {
			// we don't have relation yet
			if (!StringUtils.isBlank(relate) && "true".equals(relate.toLowerCase())) {
				// we must create a relation
				super.doCreateNode(transaction, idMap, request);
				if (!hasErrors()) {
					isNodeCreated = true;
				}
			} else {
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
