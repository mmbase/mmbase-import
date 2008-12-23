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
import java.util.Set;

import net.sf.mmapps.modules.cloudprovider.CloudProvider;
import net.sf.mmapps.modules.cloudprovider.CloudProviderFactory;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.lucene.analysis.Analyzer;
import org.mmbase.bridge.Cloud;
import org.mmbase.module.Module;
import org.mmbase.module.core.MMBase;
import org.mmbase.module.core.MMBaseObserver;

/**
 * This object represents the root of the Lucene index, it holds all the tables
 * defined for indexing
 * 
 * @author Wouter Heijke
 * @version $Revision: 1.2 $
 */
public class SearchIndex extends IndexHelper implements MMBaseObserver {
	private static Log log = LogFactory.getLog(SearchIndex.class);

	private static CloudProvider cloudProvider = CloudProviderFactory.getCloudProvider();

	/**
	 * Available tables in this index
	 */
	private ArrayList tableList = new ArrayList();

	/** The mmbase instance */
	private MMBase mmbase = null;

	/**
	 * 
	 */
	public SearchIndex() {
		mmbase = (MMBase) Module.getModule("mmbaseroot", true);
	}

	/**
	 * Collects all tables
	 */
	protected void collectAll(boolean smartIndexing) {
		Cloud cloud = cloudProvider.getCloud();
		if (smartIndexing) {
			boolean isopen = exists();
			if (isopen) {
				if (size() > 0) {
					log.info("No indexing needed, " + size() + " indexed documents in " + getName());
					return;
				}
			}
		}

		if (create()) {
			// process all tables stored in this object
			for (int t = 0; t < tableList.size(); t++) {
				SourceTable tab = (SourceTable) tableList.get(t);
				tab.collectAll(cloud, this);
			}

			log.info(size() + " indexed documents in " + getName());
			close();
		}
	}

	/**
	 * @return All fields defined in this index
	 */
	public Collection getAllFields() {
		Set result = new HashSet();
		for (Iterator it = tableList.iterator(); it.hasNext();) {
			SourceTable tab = (SourceTable) it.next();
			result.addAll(tab.getAllFields());
		}
		return result;
	}

	/**
	 * @return All fulltext fields defined in this index
	 */
	public Collection getFulltextFields() {
		Collection result = getAllFields();
		for (Iterator it = result.iterator(); it.hasNext();) {
			DataField df = (DataField) it.next();
			if (!df.isFulltext()) {
				it.remove();
			}
		}
		return result;
	}

	/**
	 * Getter for the full name of the Index
	 * 
	 * @return String containing path to the Lucene index
	 */
	public String getIndex() {
		return getPath() + getName();
	}

	/**
	 * Add a Table object to this object
	 * 
	 * @param table SourceTable object
	 */
	public void addTable(SourceTable table) {
		log.info("TABLE: " + table.getName());
		tableList.add(table);
		// add mmbase node change observer
		log.debug("Add MMBase observer for: '" + table.getName() + "'");
		mmbase.addLocalObserver(table.getName(), this);
	}

	/**
	 * Find a table by name in the list of tables attatched to this index
	 * 
	 * @param name Name of the builder/table wer're looking for
	 * @return SourceTable object or null of nothign was found.
	 */
	public SourceTable getTableByName(String name) {
		for (int t = 0; t < tableList.size(); t++) {
			SourceTable tab = (SourceTable) tableList.get(t);
			if (tab.getName().equalsIgnoreCase(name)) {
				return tab;
			}
		}
		return null;
	}

	/**
	 * Sets the Analyzer Class
	 * 
	 * @param className the full qualified name of the analyzer class
	 * @throws InstantiationException when the analyzer class can not be
	 *         instantiated
	 * @throws IllegalAccessException when the analyzer class may not be
	 *         instantiated
	 * @throws ClassNotFoundException when the analyzer class is not available
	 */
	public void setAnalyzerClass(String className) throws InstantiationException, IllegalAccessException, ClassNotFoundException {
		Analyzer analyzer = (Analyzer) Class.forName(className).newInstance();
		setAnalyzer(analyzer);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.mmbase.module.core.MMBaseObserver#nodeRemoteChanged(java.lang.String,
	 *      java.lang.String, java.lang.String, java.lang.String)
	 */
	public boolean nodeRemoteChanged(String machine, String number, String builder, String ctype) {
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.mmbase.module.core.MMBaseObserver#nodeLocalChanged(java.lang.String,
	 *      java.lang.String, java.lang.String, java.lang.String)
	 */
	public boolean nodeLocalChanged(String machine, String number, String builder, String ctype) {
      // This code does not work together with the Evenementen Forms (next to that it means a big loss in performance)
      // 
      // java.lang.NullPointerException
      // at net.sf.mmapps.modules.lucenesearch.IndexHelper.delete(IndexHelper.java:242)
      // at net.sf.mmapps.modules.lucenesearch.SearchIndex.nodeLocalChanged(SearchIndex.java:189)
      // ...
      // at nl.leocms.builders.ContentElementBuilder.commit(ContentElementBuilder.java:88)
      // at nl.leocms.builders.ContentEvenement.commit(ContentEvenement.java:113)
	   //
      //
      // java.lang.NullPointerException
		// at net.sf.mmapps.modules.lucenesearch.IndexHelper.close(IndexHelper.java:160)
		// at net.sf.mmapps.modules.lucenesearch.SearchIndex.nodeLocalChanged(SearchIndex.java:201)
      // ...
	   // at org.mmbase.bridge.implementation.BasicNode.commit(BasicNode.java:598)
	   // at nl.leocms.evenementen.forms.EvenementAction.execute(EvenementAction.java:165)
		// // delete the index entry in any case
		// if (ctype.equals("c") || ctype.equals("r") || ctype.equals("d")) { // delete
		// 	log.debug("Delete document from index");
		// 	Term term = new Term(Constants.NODE_FIELD, number);
		// 	delete(term);
		// }
		// // and create a new one
		// if (ctype.equals("c") || ctype.equals("r") || ctype.equals("n")) {
		// 	Cloud c = cloudProvider.getCloud();
		// 	if (c != null) {
		// 		log.debug("Change and/or relation: '" + ctype + "' for node: '" + number + "'");
		// 		SourceTable table = getTableByName(builder);
		// 		if (table != null) {
		// 			open();
		// 			Node node = c.getNode(number);
		// 			table.collectOne(node, this);
		// 			close();
		// 		} else {
		// 			log.debug("Builder '" + builder + "' not indexed.");
		// 		}
		// 	} else {
		// 		log.warn("No cloud.");
		// 	}
		// } else if (ctype.equals("d")) {
		// 	// nothing
		// } else {
		// 	log.debug("Unknown action: '" + ctype + "'");
		// }
		return true;
	}

}
