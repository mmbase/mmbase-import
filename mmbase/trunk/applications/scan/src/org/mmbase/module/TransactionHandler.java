/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.module;

import org.mmbase.util.*;
import org.mmbase.module.core.*;

import java.util.*;
import java.io.*;
import java.lang.*;

import org.apache.xerces.parsers.DOMParser;
import org.w3c.dom.*;
import org.xml.sax.*;


/**
 * TransactionHandler Module
 *
 * @author  John Balder: 3MPS $ 
 * @version 1.2, 22/10/2000
 *
 * This class parses the TML code and calls the appropriate methods
 * in TransactionManager TemporarayNodeManager org.mmabse.module.core
 * Furthermore is does some nameserving.
 *
 */
 
public class TransactionHandler 
	extends Module 
	implements TransactionHandlerInterface {
	
	private boolean _debug=true;
	private void debug( String msg, int indent) {
		System.out.print("TR: ");
		for (int i = 1; i < indent; i++) System.out.print("\t");
		System.out.println(msg);
	}

	// hashtable used to cache per user for thread safety
	// the construct ((UserTransactionInfo) cashUser.get(currentUser))
	// this is hided if function userInfo(), just for readability
	// is used to refer to the current info indexed per user
	private static Hashtable cashUser = new Hashtable();
	
	private MMBase mmbase;
	private UserTransactionInfo user;
	
	public TransactionHandler() {
	}
	
	/**
	 * Inits the module (startup final step 2).
	 * This is called second on startup, the module is expected
	 * to read the environment variables it needs. Startup threads,
	 * open connections etc.
	 */
	public void init(){
		if (_debug) debug(">> init TransactionHandler Module ", 0);
		mmbase=(MMBase)getModule("MMBASEROOT");
	}
	
	public void onload(){
		if (_debug) debug(">> onload TransactionHandler Module ", 0);
	}

	private final String xmlHeader =
	"<?xml version='1.0'?> <!DOCTYPE TRANSACTION SYSTEM \"Transactions.dtd\">";
	
	/*
	 * handleTransaction is the method that is called externally
	 * by scanparser. It is the start of the whole chain.
	 */
	public void handleTransaction(String template, sessionInfo session, scanpage sp) {
			
		if (_debug) debug("Received template is:", 0);
		if (_debug) debug(template, 0);
		
		InputSource is = new InputSource();
		is.setCharacterStream(new StringReader(template));
		// Resolve user.
		String user = session.getCookie();
		UserTransactionInfo uti = userInfo(user); 
		parse(null, is, uti);
	}


	private void parse(String xFile, InputSource iSource, UserTransactionInfo userTransactionInfo) {
		
		Document document;
		Element docRootElement;
		NodeList transactionContextList;
		Node currentTransactionArgumentNode;
		Node transactionContext;
		Node currentObjectArgumentNode;
		Node objectContext;
		NodeList fieldContextList;
		
		DOMParser parser = new DOMParser();
		
		try {
			if (xFile ==  null) {
		   		if (_debug) debug("parsing input: " + iSource.toString(), 0);
		   		parser.parse(iSource);
		   	}
			if (iSource ==  null) {
		   		if (_debug) debug("parsing file: " + xFile, 0);
		   		parser.parse(xFile);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}
		
		try {  //catch TransactionHandlerException's here
			
			document = parser.getDocument();
		
			// get <Transactions> context
			docRootElement = document.getDocumentElement();
		
			// do for all transaction contexts (create-, open-, commit- and deleteTransaction)
			transactionContextList = docRootElement.getChildNodes();
			for (int i = 0; i < transactionContextList.getLength(); i++) {
				String id = null, commit = null, time = null;
				//boolean noId = true;
				currentTransactionArgumentNode = null;

				transactionContext = transactionContextList.item(i);
				String tName = transactionContext.getNodeName();
				if (tName.equals("#text")) continue;
			
				//get attributes for transaction
				NamedNodeMap nm = transactionContext.getAttributes();
				if (nm != null) {
					//id
					currentTransactionArgumentNode = nm.getNamedItem("id");
					if (currentTransactionArgumentNode != null) {
						id = currentTransactionArgumentNode.getNodeValue();
					}
					if (id == null) {
						//noId = true;
						userTransactionInfo.anonymousTransaction = true;
						id = uniqueId();
					} else {
						//noId = false;
						userTransactionInfo.anonymousTransaction = false;
					}
					//commitOnClose
					currentTransactionArgumentNode = nm.getNamedItem("commitOnClose");
					if (currentTransactionArgumentNode != null) {
						commit = currentTransactionArgumentNode.getNodeValue();
					}
					if (commit==null) commit="true";
					//timeOut
					currentTransactionArgumentNode = nm.getNamedItem("timeOut");
					if (currentTransactionArgumentNode != null) {
						time = currentTransactionArgumentNode.getNodeValue();
					}
					if (time==null) time="6";
				}
			initTransactionContext(tName, id, commit, time, userTransactionInfo);
			
			//do for all object contexts (create-, open-, get- and deleteObject)
			NodeList objectContextList = transactionContext.getChildNodes();
			for (int j = 0; j < objectContextList.getLength(); j++) {
				String oId = null, oType = null, oMmbaseId = null;
				//boolean noOId = true;
				currentObjectArgumentNode = null;
				
				objectContext = objectContextList.item(j);
				String oName = objectContext.getNodeName();

				if (oName.equals("#text")) continue;
				
				//get attributes
				NamedNodeMap nm2 = objectContext.getAttributes();
				if (nm2 != null) {
					//oId
					currentObjectArgumentNode = nm2.getNamedItem("id");
					if (currentObjectArgumentNode != null) oId = currentObjectArgumentNode.getNodeValue();
					if (oId == null) {
						//noOId = true;
						oId = uniqueId();
						userTransactionInfo.anonymousObject = true;
					} else {
						//noOId = false;
						userTransactionInfo.anonymousObject = false;
					}
					//type
					currentObjectArgumentNode = nm2.getNamedItem("type");
					if (currentObjectArgumentNode != null) oType = currentObjectArgumentNode.getNodeValue();
					//mmbaseId
					currentObjectArgumentNode = nm2.getNamedItem("mmbaseId");
					if (currentObjectArgumentNode != null) oMmbaseId = currentObjectArgumentNode.getNodeValue();
				}
				initObjectContext(oName, oId, oType, oMmbaseId, userTransactionInfo);
				
				//do for all field contexts (setField)
				fieldContextList = objectContext.getChildNodes();
				for (int k = 0; k < fieldContextList.getLength(); k++) {
					String fieldName = null, fieldValue = "";
					
					Node fieldContext = fieldContextList.item(k);
					if (fieldContext.getNodeName().equals("#text")) continue;
					//get attributes
					NamedNodeMap nm3 = fieldContext.getAttributes();
					if (nm3 != null) {
							currentObjectArgumentNode = nm3.getNamedItem("par");
							if (currentObjectArgumentNode != null) {
								fieldName = currentObjectArgumentNode.getNodeValue();
							}
							Node setFieldValue = fieldContext.getFirstChild();
							if(setFieldValue!=null) {
								fieldValue = setFieldValue.getNodeValue();
							}
							executeFieldContext(oId, fieldName, fieldValue, userTransactionInfo);
					}
				}
				
				exitObjectContext(oName, oId, oType, oMmbaseId, userTransactionInfo);
			}
			
			exitTransactionContext(tName, id, commit, time, userTransactionInfo);
		}
		
		} catch (TransactionHandlerException t) {
			System.out.println(t);
			System.out.println("parsing stopped");
		}
	if (_debug) debug("exiting parse method",0);
	}
	
	private UserTransactionInfo userInfo(String user) {
		if (!cashUser.containsKey(user)) {
			if (_debug) debug("Create UserTransactionInfo for user "+user,0);
			// make acess to all variables indexed by user;
			UserTransactionInfo uti = new UserTransactionInfo();
			cashUser.put(user, uti);
			uti.transactionManager = new TransactionManager(mmbase);
			uti.tmpObjectManager = new TemporaryNodeManager(mmbase);
			uti.user = new User(user);
		} else {
			if (_debug) debug("UserTransactionInfo already known for user "+user,0);
		}
		return ((UserTransactionInfo) cashUser.get(user));
	}
		
		
	private void initTransactionContext(String tName, String id, String commit, String time, UserTransactionInfo userTransactionInfo)
		throws TransactionHandlerException {
			
		if (_debug) debug("-> " + tName + " id(" + id + ") commit(" + commit + ") time(" + time + ")", 1);
		
		// create transaction
		if (tName.equals("createTransaction")) {
			// check for existence
			if (userTransactionInfo.knownTransactionContexts.get(id) != null) {
				throw new TransactionHandlerException(tName + " transaction id already exists: " + id);
			}
			// actually create and administrate if not anonymous
			userTransactionInfo.currentTransactionContext =
				userTransactionInfo.transactionManager.create(userTransactionInfo.user, id);
			if (!userTransactionInfo.anonymousTransaction)
				userTransactionInfo.knownTransactionContexts.put(id, userTransactionInfo.currentTransactionContext);
			return;
		} // end createTransaction
		
		// except for create (above), all (open commit, cancel) need existing id
		if (id == null) throw 
				new TransactionHandlerException("id is null for " + tName);
		userTransactionInfo.currentTransactionContext = (String) userTransactionInfo.knownTransactionContexts.get(id);
		if (userTransactionInfo.currentTransactionContext == null) throw 
				new TransactionHandlerException(tName + "id is not known " + id);
				
		if (tName.equals("openTransaction")) {
			// no-op we only need userTransactionInfo.currentTransactionContext
		} // end openTransaction
		
		if (tName.equals("commitTransaction")) {
			//no-op, we do on exit
		} // end commitTransaction
		
		if (tName.equals("deleteTransaction")) {
			userTransactionInfo.transactionManager.cancel(userTransactionInfo.user, id);
			userTransactionInfo.currentTransactionContext = null;
			userTransactionInfo.knownTransactionContexts.remove(id);
		} // end deleteTransaction
	} 
	
	private void initObjectContext(String oName, String id, String type, String oMmbaseId, UserTransactionInfo userTransactionInfo)
		throws TransactionHandlerException {
			
		if (_debug) debug("-> " + oName + " id(" + id + ") type(" + type + ") oMmbaseId(" + oMmbaseId + ")", 2);
		// check if we are inside transaction context, if not error
		if (userTransactionInfo.currentTransactionContext == null) throw
				new TransactionHandlerException(oName + " id " + id + " : not in tansaction context");
		// create object, if no Id create one, remember it's anonymous
		if (oName.equals("createObject")) {
			// check for existence
			if (userTransactionInfo.knownObjectContexts.get(id) != null) {
				throw new TransactionHandlerException(oName + " Object id already exists: " + id);
			}
			// actually create and administrate if not anonymous

			userTransactionInfo.currentObjectContext =
				userTransactionInfo.tmpObjectManager.createTmpNode(type, userTransactionInfo.user.getName(), id);
			if (!userTransactionInfo.anonymousObject)
				userTransactionInfo.knownObjectContexts.put(id, userTransactionInfo.currentObjectContext);
			// add to tmp cloud
			userTransactionInfo.transactionManager.addNode(userTransactionInfo.currentTransactionContext, userTransactionInfo.currentObjectContext);
			return;
		} // end createObject
		
		if (oName.equals("getObject")) {
			if (id == null) {
				id = uniqueId();
				userTransactionInfo.anonymousObject = true;
			}
			else {
				userTransactionInfo.anonymousObject = false;
			}
			// check for existence
			if (userTransactionInfo.knownObjectContexts.get(id) != null) {
				throw new TransactionHandlerException(oName + " Object id already exists: " + id);
			}
			if (oMmbaseId == null) {
				throw new TransactionHandlerException(oName + " no MMbase id: ");
			}
			// actually get and administrate if not anonymous
			//MMObjectNode m  = userTransactionInfo.tmpObjectManager.getNode(oMmbaseId);
			userTransactionInfo.currentObjectContext =
				userTransactionInfo.tmpObjectManager.getObject(oMmbaseId, userTransactionInfo.user.getName() + id);
			//get Node succeed?
			if (userTransactionInfo.currentObjectContext==null) throw 
				new TransactionHandlerException("could not get MMbase object id:  " + oMmbaseId);
			if (!userTransactionInfo.anonymousObject)
				userTransactionInfo.knownObjectContexts.put(id, userTransactionInfo.currentObjectContext);
			// add to tmp cloud
			userTransactionInfo.transactionManager.addNode(userTransactionInfo.currentTransactionContext, userTransactionInfo.currentObjectContext);
			return;
		} // end getObject
		
		// except for create, get (above), all (open, delete) need existing id
		if (id == null) throw 
				new TransactionHandlerException("id is null for " + oName);
		userTransactionInfo.currentObjectContext = (String) userTransactionInfo.knownObjectContexts.get(id);
		if (userTransactionInfo.currentObjectContext == null) throw 
				new TransactionHandlerException(oName + "id is not known " + id);
				
		if (oName.equals("openObject")) {
			// no-op we only need current object context
		} // end openObject
		
		if (oName.equals("deleteObject")) {
			//delete from temp cloud
			userTransactionInfo.transactionManager.removeNode(userTransactionInfo.currentTransactionContext, userTransactionInfo.currentObjectContext);
			// destroy
			userTransactionInfo.tmpObjectManager.deleteTmpNode(userTransactionInfo.currentObjectContext);
			userTransactionInfo.currentObjectContext = null;
			userTransactionInfo.knownObjectContexts.remove(id);
		} // end deleteObject
		
	}
	
	private void exitTransactionContext(String tName, String id, String commit, String time, UserTransactionInfo userTransactionInfo) 
		throws TransactionHandlerException {
			
		if (tName.equals("deleteTransaction")) {
			return;
		} // end deleteTransaction

		// we're supposed to have a 'userTransactionInfo.currentTransactionContext', so check
		if (userTransactionInfo.currentTransactionContext == null) throw 
				new TransactionHandlerException(tName + " no current transaction for " + id);
			
		if ((tName.equals("createTransaction")) || (tName.equals("openTransaction"))) {
			if (commit.equals("true")) {
				userTransactionInfo.transactionManager.commit(userTransactionInfo.user, userTransactionInfo.currentTransactionContext);
			}
		} // end createTransaction & openTransaction
		
		if (tName.equals("commitTransaction")) {
				userTransactionInfo.transactionManager.commit(userTransactionInfo.user, userTransactionInfo.currentTransactionContext);
		} // end commitTransaction
		
		
		//out of context now
		if (_debug) debug("<- " + tName + " id(" + id + ") commit(" + commit + ") time(" + time + ")", 1);
		userTransactionInfo.currentTransactionContext = null;
 }
	
	private void exitObjectContext(String oName, String id, String type, String oMmbaseId, UserTransactionInfo userTransactionInfo) 
		throws TransactionHandlerException {
		
		if (oName.equals("deleteObject")) {
			return;
		} // end deleteTransaction
		
		// we're supposed to have a 'userTransactionInfo.currentObjectContext' for the others, so check
		if (userTransactionInfo.currentObjectContext == null) throw 
				new TransactionHandlerException(oName + " no current object context for " + id);
			
		if (oName.equals("createObject")) {
		} // end createObject
		if (oName.equals("openObject")) {
		} // end openObject
		
		if (oName.equals("getObject")) {
		} // end getObject
		
		//out of context now
		if (_debug) debug("<- " + oName + " id(" + id + ") type(" + type + ") oMmbaseId(" + oMmbaseId + ")", 2);
		userTransactionInfo.currentObjectContext = null;

	}
	
	private void executeFieldContext(String oId, String fieldName, String fieldValue, UserTransactionInfo userTransactionInfo) throws
		TransactionHandlerException {

		if (_debug) debug("-X Object " + oId + ": [" + fieldName + "] set to: " + fieldValue, 3);
		
		//check that we are inside object context
		if (userTransactionInfo.currentObjectContext == null) throw
				new TransactionHandlerException(oId + " set field " + fieldName + " to " + fieldValue);
		
		userTransactionInfo.tmpObjectManager.setObjectField(userTransactionInfo.currentObjectContext, fieldName, fieldValue);
	}
	

	/**	
 	 * create unique number
	 */
	private synchronized String uniqueId() {
		try {
			Thread.sleep(1); // A bit paranoid, but just to be sure that not two threads steal the same millisecond.
		} catch (Exception e) {
			debug("What's the reason I may not sleep?",0);
		}
		return "ID"+java.lang.System.currentTimeMillis();
	}



	///
	// actual code ends here, rest is temporary or for testing
	//
	
	/**
	 * Dummy User object, this object needs to be replace by
	 * the real User object (when that is finished)
	 */
	class User {
		private String name;

		public User(String name) {
			this.name= name;
		}
		
		String getName() { 
			return name;
		}
	}


	/**
	 * own exception class
	 */
	class TransactionHandlerException extends Exception {
		TransactionHandlerException(String s) { 
			super(s); 
		}
	}
	
	/** 
	 * container class for info per user for thread safety
	 */
	class UserTransactionInfo {
		// the Managers for this user
		public TransactionManagerInterface transactionManager;
		public TemporaryNodeManagerInterface tmpObjectManager;
		
		// parsing variables
		public boolean anonymousTransaction = true;
		public String currentTransactionContext = null;
		public Hashtable knownTransactionContexts = new Hashtable();
		
		public boolean anonymousObject = true;
		public String currentObjectContext = null;
		public Hashtable knownObjectContexts = new Hashtable();

		public User user = null;
	}
}
