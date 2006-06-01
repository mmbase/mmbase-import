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
import org.mmbase.bridge.Cloud;
import org.mmbase.bridge.Node;
import org.mmbase.bridge.NodeList;
import org.mmbase.bridge.NodeManager;
import org.mmbase.cache.NodeCache;

/**
 * Represents a Table (aka Builder) in MMBase and it's fields and relations
 * 
 * @author Wouter Heijke
 * @version $Revision: 1.1 $
 */
public class SourceTable {
	private static Log log = LogFactory.getLog(SourceTable.class);

	// name of the builder/table this object represents
	private String tableName;

	// list of fields
	private List fieldList = new ArrayList();

	// list of related objects
	private List relatedList = new ArrayList();

	/**
	 * Collect all nodes in the table this object represents
	 * 
	 * @param cloud MMBase cloud to use for indexing
	 * @param writer Lucene document writer
	 */
	protected void collectAll(Cloud cloud, IndexHelper writer) {
		NodeManager nm = cloud.getNodeManager(tableName);
		NodeList currentElements = nm.getList(null, null, null);

		for (int i = 0; i < currentElements.size(); i++) {
			Node currentNode = currentElements.getNode(i);
			collectOne(currentNode, writer);
		}
	}

	/**
	 * Collect and index a single MMBase Node
	 * 
	 * @param node MMBase node to index
	 * @param writer Lucene writer to use for indexing this node
	 */
	public void collectOne(Node node, IndexHelper writer) {
		try {
			Document doc = new Document();
			int number = node.getNumber();

			log.trace("collect node:'" + number + "'");

			// always index the builder name and the node number for later
			doc.add(new Field(Constants.SOURCE_FIELD, "mmbase", Field.Store.YES, Field.Index.UN_TOKENIZED));
			doc.add(new Field(Constants.BUILDER_FIELD, tableName, Field.Store.YES, Field.Index.UN_TOKENIZED));
			doc.add(new Field(Constants.NODE_FIELD, String.valueOf(number), Field.Store.YES, Field.Index.UN_TOKENIZED));

			for (int f = 0; f < fieldList.size(); f++) {
				DataField fld = (DataField) fieldList.get(f);
				if (fld != null) {
					Field result = fld.collectField(node);
					if (result != null) {
						doc.add(result);
					}
				}
			}

			for (int r = 0; r < relatedList.size(); r++) {
				DataRelation rel = (DataRelation) relatedList.get(r);
				rel.collectAll(doc, node);
			}

			clearNodeFromCache(node);
			
			writer.write(doc);
		} catch (Exception e) {
			log.error("Problem: '" + e.getMessage() + "' on: '" + tableName + "'");
		}
	}

	/**
	 * Set's the name of the MMBase builder/table this object represents
	 * 
	 * @param newName Name of the builder/table
	 */
	public void setName(String newName) {
		tableName = newName;
	}

	/**
	 * Get's the name of the MMBase builder/table this object represents
	 * 
	 * @return name The name
	 */
	public String getName() {
		return tableName;
	}

	/**
	 * Add a field from this table
	 * 
	 * @param field Field object representing information about the field
	 */
	public void addField(DataField field) {
		log.debug("FIELD: " + field.getName());
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
	 * Add a related table
	 * 
	 * @param relation DataRelation object holding the information on the
	 *        related table
	 */
	public void addRelated(DataRelation relation) {
		log.info("RELATED: " + relation.getName());
		relatedList.add(relation);
	}

	/**
	 * @return list of related elements
	 */
	public List getRelated() {
		return relatedList;
	}

	/**
	 * A way to clear nodes from MMBase cache and keep it's memory usage at a
	 * acceptable level.
	 * 
	 * @param node
	 */
	private void clearNodeFromCache(Node node) {
		try {
			// clear the current node from the cache
			NodeCache c = NodeCache.getCache();
			if (c != null) {
				Object obj = node.getValue("number");
				if (obj != null) {
					c.remove(obj);
				}
			}

		} catch (Exception e) {
			log.debug("Can't clear node: '" + node.getNumber() + "' from cache.");
		}
	}
}
