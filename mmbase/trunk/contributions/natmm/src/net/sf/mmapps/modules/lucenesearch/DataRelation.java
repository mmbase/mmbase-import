/*
 * MMBase Lucene module
 *
 * The contents of this file are subject to the Mozilla Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://www.mozilla.org/MPL/
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 */
package net.sf.mmapps.modules.lucenesearch;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.mmbase.bridge.Node;
import org.mmbase.bridge.NodeList;

/**
 * The Relation object, holds objects related to a table, these objects are
 * tables also.
 *
 * @author Wouter Heijke
 * @version $Revision: 1.2 $
 */
public class DataRelation {
	private static Log log = LogFactory.getLog(DataRelation.class);

	private String tableName;

	private String type;

	private String role = null;

	private String searchDir = null;

	private List fieldList = new ArrayList();

	private List relatedList = new ArrayList();

	/**
	 * Collects all related nodes and their fields
	 *
	 * @param doc Lucene Document to put the collected fields in
	 * @param node Node of the parent, the starting node to find the related
	 *        nodes from
	 */
	protected void collectAll(Document doc, Node node) {
		NodeList currentElements = node.getRelatedNodes(tableName, role, searchDir);

		for (int i = 0; i < currentElements.size(); i++) {
			Node currentNode = currentElements.getNode(i);

			for (int f = 0; f < fieldList.size(); f++) {
				DataField fld = (DataField) fieldList.get(f);
				if (fld != null) {
					Field result = null;
					try {
						result = fld.collectField(currentNode);
					} catch (Exception e) {
						log.warn("IO Problem: '" + e.getMessage() + "'");
					}
					if (result != null) {
						doc.add(result);
					}
				}
			}

			for (int r = 0; r < relatedList.size(); r++) {
				DataRelation rel = (DataRelation) relatedList.get(r);
				rel.collectAll(doc, currentNode);
			}
		}
	}

	/**
	 * Adds a field to this relation
	 *
	 * @param field DataField object to add
	 */
	public void addField(DataField field) {
		fieldList.add(field);
	}

	/**
	 * @return list of fields
	 */
	public List getFields() {
		return fieldList;
	}

	/**
	 * @return collection of fields and fields of related elements
	 */
	public Collection getAllFields() {
		Set result = new HashSet();
		result.addAll(getFields());
		for (Iterator it = relatedList.iterator(); it.hasNext();) {
			DataRelation rd = (DataRelation) it.next();
			result.addAll(rd.getAllFields());
		}
		return result;
	}

	/**
	 * @param relation
	 */
	public void addRelated(DataRelation relation) {
		relatedList.add(relation);
	}

	/**
	 * @return The name of this Relation
	 */
	public String getName() {
		return tableName;
	}

	/**
	 * @return The type of this Relation
	 */
	public String getType() {
		return type;
	}

	/**
	 * @param string
	 */
	public void setName(String string) {
		tableName = string;
	}

	/**
	 * @param string
	 */
	public void setType(String string) {
		type = string;
	}

	/**
	 * Sets the role to use for related nodes
	 *
	 * @param role
	 */
	public void setRole(String role) {
		this.role = role;
	}

	/**
	 * Sets the search direction for related nodes
	 *
	 * @param searchDir
	 */
	public void setSearchDir(String searchDir) {
		this.searchDir = searchDir;
	}
}
