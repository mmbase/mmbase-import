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
	private TransactionManagerInterface transactionManager;
	private TemporaryNodeManagerInterface tmpObjectManager;
	
	public TransactionHandler() {
	}
	
	/**
	 * init
	 */
	public void init(){
		if (_debug) debug(">> init TransactionHandler Module ", 0);
		mmbase=(MMBase)getModule("MMBASEROOT");
		transactionManager = new TransactionManager(mmbase);
		tmpObjectManager = new TemporaryNodeManager(mmbase);
	}

	/**	
	 * onLoad
	 */	
	public void onload(){
		if (_debug) debug(">> onload TransactionHandler Module ", 0);
	}

	/**
	 * xmlHeader
	 */
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
		// get handle to transactions of user
		String user = session.getCookie();
		UserTransactionInfo uti = userInfo(user); 
		parse(null, is, uti);
	}

	/**
	 * Begin parsing the document
	 */	
	private void parse(String xFile, InputSource iSource, UserTransactionInfo userTransactionInfo) {
		Document document;
		Element docRootElement;
		NodeList transactionContextList;
		
		DOMParser parser = new DOMParser();
		
		try {
			if (xFile !=  null) {
		   		if (_debug) debug("parsing file: " + xFile, 0);
		   		parser.parse(xFile);
		   	} else {
				if (iSource !=  null) {
		   			if (_debug) debug("parsing input: " + iSource.toString(), 0);
		   			parser.parse(iSource);
				} else {
		   			debug("No xFile and no iSource file received!", 0);
				}
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
			// evaluate all these transactions
			evaluateTransactions(transactionContextList, userTransactionInfo);
		
		} catch (TransactionHandlerException t) {
			debug(""+t,0);
			debug("parsing stopped",0);
		}
		if (_debug) debug("exiting parse method",0);
	}


	private void evaluateTransactions(NodeList transactionContextList, UserTransactionInfo userTransactionInfo) 
		throws TransactionHandlerException {
		Node currentTransactionArgumentNode;
		String currentTransactionContext;
		boolean anonymousTransaction = true;
		Node transactionContext;
		TransactionInfo transactionInfo = null;

		for (int i = 0; i < transactionContextList.getLength(); i++) {
			// XML Parsing part
			currentTransactionArgumentNode = null;
			currentTransactionContext = null;
			String id = null, commit = null, time = null;

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
				//commitOnClose
				currentTransactionArgumentNode = nm.getNamedItem("commitOnClose");
				if (currentTransactionArgumentNode != null) {
					commit = currentTransactionArgumentNode.getNodeValue();
				}
				//timeOut
				currentTransactionArgumentNode = nm.getNamedItem("timeOut");
				if (currentTransactionArgumentNode != null) {
					time = currentTransactionArgumentNode.getNodeValue();
				}
			}
			// XML Parsing done
			
			// Execution of XML 
			if (id == null) {
				anonymousTransaction = true;
				id = uniqueId();
			} else {
				anonymousTransaction = false;
			}
			if (commit==null) commit="true";
			if (time==null) time="6";

			if (_debug) debug("-> " + tName + " id(" + id + ") commit(" + commit + ") time(" + time + ")", 1);
		
			// CREATE TRANSACTION
			if (tName.equals("createTransaction")) {
				// Check if the transaction already exists.
				if (userTransactionInfo.knownTransactionContexts.get(id) != null) {
					throw new TransactionHandlerException(tName + " transaction already exists id = " + id);
				}
				// actually create and administrate if not anonymous
				currentTransactionContext = transactionManager.create(userTransactionInfo.user, id);
				transactionInfo = new TransactionInfo(currentTransactionContext);
				if (!anonymousTransaction) {
					userTransactionInfo.knownTransactionContexts.put(id, transactionInfo);
				}
			} 
			if (tName.equals("openTransaction")) { // no-op we only need currentTransactionContext
			}
			if (tName.equals("commitTransaction")) { //no-op, we do on exit
			} 
			if (tName.equals("deleteTransaction")) {
				transactionManager.cancel(userTransactionInfo.user, id);
				currentTransactionContext = null;
				userTransactionInfo.knownTransactionContexts.remove(id);
			} 


			// DO OBJECTS
			//do for all object contexts (create-, open-, get- and deleteObject)
			NodeList objectContextList = transactionContext.getChildNodes();
			// Evaluate all objects
			evaluateObjects(objectContextList, userTransactionInfo, currentTransactionContext, transactionInfo);


			// ENDING TRANSACTION		
			if (tName.equals("deleteTransaction")) {
			} 
			if (tName.equals("createTransaction") || tName.equals("openTransaction")) {
				if(commit.equals("true")) {
					transactionManager.commit(userTransactionInfo.user, currentTransactionContext);
				}
			} 
			if (tName.equals("commitTransaction")) {
				transactionManager.commit(userTransactionInfo.user, currentTransactionContext);
			} 
			if (_debug) debug("<- " + tName + " id(" + id + ") commit(" + commit + ") time(" + time + ")", 1);
			// End execution of XML
		}
	}

	/**
	 * Evaluate and execute object methods
	 */
	private void evaluateObjects(NodeList objectContextList, UserTransactionInfo userTransactionInfo, String currentTransactionContext, TransactionInfo transactionInfo) 
		throws TransactionHandlerException {
		Node currentObjectArgumentNode = null; 
		Node objectContext;
		NodeList fieldContextList;
		String currentObjectContext;
		boolean anonymousObject = true;

		for (int j = 0; j < objectContextList.getLength(); j++) {
			String id = null, type = null, oMmbaseId = null;
			currentObjectContext = null;
			
				
			// XML thingies
			objectContext = objectContextList.item(j);
			String oName = objectContext.getNodeName();

			if (oName.equals("#text")) continue;
				
			//get attributes
			NamedNodeMap nm2 = objectContext.getAttributes();
			if (nm2 != null) {
				currentObjectArgumentNode = nm2.getNamedItem("id");
				if (currentObjectArgumentNode != null) id = currentObjectArgumentNode.getNodeValue();
				//type
				currentObjectArgumentNode = nm2.getNamedItem("type");
				if (currentObjectArgumentNode != null) type = currentObjectArgumentNode.getNodeValue();
				//mmbaseId
				currentObjectArgumentNode = nm2.getNamedItem("mmbaseId");
				if (currentObjectArgumentNode != null) oMmbaseId = currentObjectArgumentNode.getNodeValue();
			}
			if (id == null) {
				id = uniqueId();
				anonymousObject = true;
			} else {
				anonymousObject = false;
			}

			if (_debug) debug("-> " + oName + " id(" + id + ") type(" + type + ") oMmbaseId(" + oMmbaseId + ")", 2);

			// create object, if no Id create one, remember it's anonymous
			if (oName.equals("createObject")) {
					System.out.println("$$$ transactioninfo"+transactionInfo);
				// check for existence
				if (transactionInfo.knownObjectContexts.get(id) != null) {
					throw new TransactionHandlerException(oName + " Object id already exists: " + id);
				}
				// actually create and administrate if not anonymous
				currentObjectContext = tmpObjectManager.createTmpNode(type, userTransactionInfo.user.getName(), id);
				if (!anonymousObject) {
					transactionInfo.knownObjectContexts.put(id, currentObjectContext);
				}
				// add to tmp cloud
				transactionManager.addNode(currentTransactionContext, tmpObjectManager.getTmpKey(userTransactionInfo.user.getName(),currentObjectContext));
			} 
			if (oName.equals("getObject")) {
				// check for existence
				if (transactionInfo.knownObjectContexts.get(id) != null) {
					throw new TransactionHandlerException(oName + " Object id already exists: " + id);
				}
				if (oMmbaseId == null) {
					throw new TransactionHandlerException(oName + " no MMbase id: ");
				}
				// actually get and administrate if not anonymous
				currentObjectContext = tmpObjectManager.getObject(userTransactionInfo.user.getName(),id,oMmbaseId);
				//get Node succeed?
				if (!anonymousObject)
					transactionInfo.knownObjectContexts.put(id, currentObjectContext);
				// add to tmp cloud
				transactionManager.addNode(currentTransactionContext, tmpObjectManager.getTmpKey(userTransactionInfo.user.getName(),currentObjectContext));
			}
			if (oName.equals("openObject")) {
				// no-op we only need current object context
			}
			if (oName.equals("deleteObject")) {
				//delete from temp cloud
				transactionManager.removeNode(currentTransactionContext, tmpObjectManager.getTmpKey(userTransactionInfo.user.getName(),currentObjectContext));
				// destroy
				tmpObjectManager.deleteTmpNode(userTransactionInfo.user.getName(),currentObjectContext);
				currentObjectContext = null;
				transactionInfo.knownObjectContexts.remove(id);
			}
			

			// DO FIELDS
			//do for all field contexts (setField)
			fieldContextList = objectContext.getChildNodes();
			// Evaluate Fields
			evaluateFields(fieldContextList, userTransactionInfo, id ,currentObjectContext);


			if (oName.equals("deleteObject")) {
			}
			if (oName.equals("createObject")) {
			}
			if (oName.equals("openObject")) {
			}
			if (oName.equals("getObject")) {
			} 
			
			if (_debug) debug("<- " + oName + " id(" + id + ") type(" + type + ") oMmbaseId(" + oMmbaseId + ")", 2);
		}
	}

	private void evaluateFields(NodeList fieldContextList, UserTransactionInfo userTransactionInfo, String oId, String currentObjectContext)
		throws TransactionHandlerException {

		for (int k = 0; k < fieldContextList.getLength(); k++) {
			String fieldName = null;
			String fieldValue = "";
					
			Node fieldContext = fieldContextList.item(k);
			if (fieldContext.getNodeName().equals("#text")) continue;

			//get attributes
			NamedNodeMap nm3 = fieldContext.getAttributes();
			if (nm3 != null) {
				Node currentObjectArgumentNode = nm3.getNamedItem("name");
				if (currentObjectArgumentNode != null) {
					fieldName = currentObjectArgumentNode.getNodeValue();
				}
				if (fieldName==null) {
					 throw new TransactionHandlerException("<setField name=\"fieldname\">value</setField> is missing the NAME attribute!");
				}
				Node setFieldValue = fieldContext.getFirstChild();
				if(setFieldValue!=null) {
					fieldValue = setFieldValue.getNodeValue();
				}
				if (_debug) debug("-X Object " + oId + ": [" + fieldName + "] set to: " + fieldValue, 3);
		
				//check that we are inside object context
				if (currentObjectContext == null) {
					 throw new TransactionHandlerException(oId + " set field " + fieldName + " to " + fieldValue);
				}
				tmpObjectManager.setObjectField(userTransactionInfo.user.getName(),currentObjectContext, fieldName, fieldValue);
			}
		}
	}

	
	private UserTransactionInfo userInfo(String user) {
		if (!cashUser.containsKey(user)) {
			if (_debug) debug("Create UserTransactionInfo for user "+user,0);
			// make acess to all variables indexed by user;
			UserTransactionInfo uti = new UserTransactionInfo();
			cashUser.put(user, uti);
			uti.user = new User(user);
		} else {
			if (_debug) debug("UserTransactionInfo already known for user "+user,0);
		}
		return ((UserTransactionInfo) cashUser.get(user));
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
	 * container class for transaction per user
	 */
	class UserTransactionInfo {
		// contains all known transactions of a user
		public Hashtable knownTransactionContexts = new Hashtable(); 
		// The user
		public User user = null;		
	}

	/**
	 * container class for objects per transaction
	 */
	class TransactionInfo {
		// The transaction 
		String transactionContext = null;		
		// All objects belonging to a certain transaction
		Hashtable knownObjectContexts = new Hashtable();
		// Needed to timeout transaction
		long startTime = 0;		

		TransactionInfo (String t) {
			this.transactionContext = t;
			startTime = java.lang.System.currentTimeMillis();
		}
	}
}
