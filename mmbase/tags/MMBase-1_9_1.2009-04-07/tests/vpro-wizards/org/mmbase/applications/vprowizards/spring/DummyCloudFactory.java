/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.applications.vprowizards.spring;

import java.util.Collection;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.mmbase.bridge.AlreadyExistsException;
import org.mmbase.bridge.CloudContext;
import org.mmbase.bridge.Node;
import org.mmbase.bridge.NodeList;
import org.mmbase.bridge.NodeManager;
import org.mmbase.bridge.NodeManagerList;
import org.mmbase.bridge.NodeQuery;
import org.mmbase.bridge.NotFoundException;
import org.mmbase.bridge.Query;
import org.mmbase.bridge.Relation;
import org.mmbase.bridge.RelationList;
import org.mmbase.bridge.RelationManager;
import org.mmbase.bridge.RelationManagerList;
import org.mmbase.bridge.StringList;
import org.mmbase.bridge.Transaction;
import org.mmbase.security.Action;
import org.mmbase.security.UserContext;
import org.mmbase.util.functions.Function;
import org.mmbase.util.functions.Parameters;

/**
 * a dummy implementation for testing. it creates an empty implementation of the
 * transaction interface.
 * 
 * @author Ernst Bunders
 * 
 */
public class DummyCloudFactory implements CloudFactory {

	public Transaction createTransaction(HttpServletRequest request) {
		return new Transaction() {
		    
		    public void shutdown(){}

			public void cancel() {

			}

			public boolean commit() {
				return false;
			}

			public String getCloudName() {
				return null;
			}

			public boolean isCanceled() {
				return false;
			}

			public boolean isCommitted() {
				return false;
			}

			public Query createAggregatedQuery() {
				return null;
			}

			public NodeList createNodeList() {
				return null;
			}

			public NodeManagerList createNodeManagerList() {
				return null;
			}

			public NodeQuery createNodeQuery() {
				return null;
			}

			public Query createQuery() {
				return null;
			}

			public RelationList createRelationList() {
				return null;
			}

			public RelationManagerList createRelationManagerList() {
				return null;
			}

			public Transaction createTransaction() {
				return null;
			}

			public Transaction createTransaction(String name) throws AlreadyExistsException {
				return null;
			}

			public Transaction createTransaction(String name, boolean overwrite) throws AlreadyExistsException {
				return null;
			}

			public CloudContext getCloudContext() {
				return null;
			}

			public String getDescription() {
				return null;
			}

			public Function<?> getFunction(String setName, String functionName) {
				return null;
			}

			public Collection<Function<?>> getFunctions(String setName) {
				return null;
			}

			public NodeList getList(String startNodes, String nodePath, String fields, String constraints,
					String orderby, String directions, String searchDir, boolean distinct) {
				return null;
			}

			public NodeList getList(Query query) {
				return null;
			}

			public Locale getLocale() {
				return null;
			}

			public String getName() {
				return null;
			}

			public Node getNode(int number) throws NotFoundException {
				return null;
			}

			public Node getNode(String number) throws NotFoundException {
				return null;
			}

			public Node getNodeByAlias(String alias) throws NotFoundException {
				return null;
			}

			public NodeManager getNodeManager(String name) throws NotFoundException {
				return null;
			}

			public NodeManager getNodeManager(int nodeManagerId) throws NotFoundException {
				return null;
			}

			public NodeManagerList getNodeManagers() {
				return null;
			}

			public StringList getPossibleContexts() {
				return null;
			}

			public Map<Object, Object> getProperties() {
				return null;
			}

			public Object getProperty(Object key) {
				return null;
			}

			public Relation getRelation(int number) throws NotFoundException {
				return null;
			}

			public Relation getRelation(String number) throws NotFoundException {
				return null;
			}

			public RelationManager getRelationManager(int relationManagerId) throws NotFoundException {
				return null;
			}

			public RelationManager getRelationManager(String sourceManagerName, String destinationManagerName,
					String roleName) throws NotFoundException {
				return null;
			}

			public RelationManager getRelationManager(NodeManager sourceManager, NodeManager destinationManager,
					String roleName) throws NotFoundException {
				return null;
			}

			public RelationManager getRelationManager(String roleName) throws NotFoundException {
				return null;
			}

			public RelationManagerList getRelationManagers() {
				return null;
			}

			public RelationManagerList getRelationManagers(String sourceManagerName, String destinationManagerName,
					String roleName) throws NotFoundException {
				return null;
			}

			public RelationManagerList getRelationManagers(NodeManager sourceManager, NodeManager destinationManager,
					String roleName) throws NotFoundException {
				return null;
			}

			public Transaction getTransaction(String name) {
				return null;
			}

			public UserContext getUser() {
				return null;
			}

			public boolean hasNode(int number) {
				return false;
			}

			public boolean hasNode(String number) {
				return false;
			}

			public boolean hasNodeManager(String name) {
				return false;
			}

			public boolean hasRelation(int number) {
				return false;
			}

			public boolean hasRelation(String number) {
				return false;
			}

			public boolean hasRelationManager(String sourceManagerName, String destinationManagerName, String roleName) {
				return false;
			}

			public boolean hasRelationManager(NodeManager sourceManager, NodeManager destinationManager, String roleName) {
				return false;
			}

			public boolean hasRelationManager(String roleName) {
				return false;
			}

			public boolean hasRole(String roleName) {
				return false;
			}

			public boolean may(Action action, Parameters parameters) {
				return false;
			}

			public boolean mayRead(int number) {
				return false;
			}

			public boolean mayRead(String number) {
				return false;
			}

			public void setLocale(Locale locale) {

			}

			public void setProperty(Object key, Object value) {

			}

			public NodeList getNodes() {
				// TODO Auto-generated method stub
				return null;
			}

		};
	}

}
