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
	private void 	debug( String msg, int indent) {
		for (int i = 1; i < indent; i++) System.out.print("\t");
		System.out.println( msg );
	}

	// hashtable used to cache per user for thread safety
	// the construct ((UserTransactionInfo) cashUser.get(currentUser))
	// this is hided if function userInfo(), just for readability
	// is used to refer to the current info indexed per user
	private static Hashtable cashUser;
	
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
		
		// initialize static hashtable if necc.
		if (cashUser == null) cashUser = new Hashtable();
		
		mmbase=(MMBase)getModule("MMBASEROOT");
		
		if (rand == null) rand = new Random();
		synchronized (cashUser) {
	  	// hack
			// the next construction generates a new user for every shmtl page
			// should come from MMbase system later on
			currentUser = new User("U" + rand.nextInt(10000000));
			// end hack
			
			// make acess of all variables indexed by user;
			user = new UserTransactionInfo();
			cashUser.put(currentUser, user);
		}
		
		user.transactionManager = new TransactionManager(mmbase);
		user.tmpObjectManager = new TemporaryNodeManager(mmbase);
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
			
		if (_debug) debug(" >>>>handleTransaction in TransactionHandler ", 0);
		if (_debug) debug(template, 0);
		//if (_debug) debug(session.toString(), 0);
		//if (_debug) debug(sp.toString(), 0);
		
		InputSource is = new InputSource();
		is.setCharacterStream(new StringReader(template));
		parse(null, is);
	}


	private void parse(String xFile, InputSource iSource) {
		
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
		} catch (SAXException se) {
		   se.printStackTrace();
			 return;
		   //System.exit(-1);
		} catch (IOException ioe) {
		   ioe.printStackTrace();
		   return;
		   //System.exit(-2);
		}
		
		
		try {  //catch TransactionHandlerException's here
			
		document = parser.getDocument();
		
		// get <Transactions> context
		docRootElement = document.getDocumentElement();
		
		// do for all transaction contexts (create-, open-, commit- and deleteTransaction)
		transactionContextList = docRootElement.getChildNodes();
		for (int i = 0; i < transactionContextList.getLength(); i++) {
			String id = null, commit = null, time = null;
			boolean noId = true;
			currentTransactionArgumentNode = null;

			transactionContext = transactionContextList.item(i);
			String tName = transactionContext.getNodeName();
			if (tName.equals("#text")) continue;
			
			//get attributes for transaction
			NamedNodeMap nm = transactionContext.getAttributes();
			if (nm != null) {
				//id
				currentTransactionArgumentNode = nm.getNamedItem("id");
				if (currentTransactionArgumentNode != null)
					id = currentTransactionArgumentNode.getNodeValue();
				if (id == null) {
					noId = true;
					id = uniqueId();
				} else {
					noId = false;
				}
				//commitOnClose
				currentTransactionArgumentNode = nm.getNamedItem("commitOnClose");
				if (currentTransactionArgumentNode != null)
					commit = currentTransactionArgumentNode.getNodeValue();
				if (commit==null) commit="true";
				//timeOut
				currentTransactionArgumentNode = nm.getNamedItem("timeOut");
				if (currentTransactionArgumentNode != null)
					time = currentTransactionArgumentNode.getNodeValue();
				if (time==null) time="6";
				
			}
			initTransactionContext(tName, id, commit, time);
			
			//do for all object contexts (create-, open-, get- and deleteObject)
			NodeList objectContextList = transactionContext.getChildNodes();
			for (int j = 0; j < objectContextList.getLength(); j++) {
				String oId = null, oType = null, oMmbaseId = null;
				boolean noOId = true;
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
						noOId = true;
						oId = uniqueId();
					} else {
						noOId = false;
					}
					//type
					currentObjectArgumentNode = nm2.getNamedItem("type");
					if (currentObjectArgumentNode != null) oType = currentObjectArgumentNode.getNodeValue();
					//mmbaseId
					currentObjectArgumentNode = nm2.getNamedItem("mmbaseId");
					if (currentObjectArgumentNode != null) oMmbaseId = currentObjectArgumentNode.getNodeValue();
				}
				initObjectContext(oName, oId, oType, oMmbaseId);
				
				//do for all field contexts (setField)
				fieldContextList = objectContext.getChildNodes();
				for (int k = 0; k < fieldContextList.getLength(); k++) {
					String fieldName = null, fieldValue = null;
					
					Node fieldContext = fieldContextList.item(k);
					if (fieldContext.getNodeName().equals("#text")) continue;
					//get attributes
					NamedNodeMap nm3 = fieldContext.getAttributes();
					if (nm3 != null) {
							currentObjectArgumentNode = nm3.getNamedItem("par");
							if (currentObjectArgumentNode != null)
								fieldName = currentObjectArgumentNode.getNodeValue();
							currentObjectArgumentNode = nm3.getNamedItem("val");
							if (currentObjectArgumentNode != null)
								fieldValue = currentObjectArgumentNode.getNodeValue();
							executeFieldContext(oId, fieldName, fieldValue);
					}
				}
				
				exitObjectContext(oName, oId, oType, oMmbaseId);
			}
			
			exitTransactionContext(tName, id, commit, time);
		}
		
		} catch (TransactionHandlerException t) {
			System.out.println(t);
			System.out.println("parsing stopped");
		}
	System.out.println("exciting parse method");
	}
	
	
	private UserTransactionInfo userInfo() {
		return ((UserTransactionInfo) cashUser.get(currentUser));
	}
		
	private void initTransactionContext(String tName, String id, String commit, String time)
		throws TransactionHandlerException {
			
		if (_debug) debug("-> " + tName + " id(" + id + ") commit(" + commit + ") time(" + time + ")", 1);
		
		// create transaction, if no Id create one, remember it's anonymous
		if (tName.equals("createTransaction")) {
			if (id == null) {
				id = uniqueId();
				userInfo().anonymousTransaction = true;
			}
			else {
				userInfo().anonymousTransaction = false;
			}
			// check for existence
			if (userInfo().knownTransactionContexts.get(id) != null) {
				throw new TransactionHandlerException(tName + " transaction id already exists: " + id);
			}
			// actually create and administrate if not anonymous
			userInfo().currentTransactionContext =
				userInfo().transactionManager.create(currentUser, id);
			if (!userInfo().anonymousTransaction)
				userInfo().knownTransactionContexts.put(id, userInfo().currentTransactionContext);
			return;
		} // end createTransaction
		
		// except for create (above), all (open commit, cancel) need existing id
		if (id == null) throw 
				new TransactionHandlerException("id is null for " + tName);
		userInfo().currentTransactionContext = (String) userInfo().knownTransactionContexts.get(id);
		if (userInfo().currentTransactionContext == null) throw 
				new TransactionHandlerException(tName + "id is not known " + id);
				
		if (tName.equals("openTransaction")) {
			// no-op we only need userInfo().currentTransactionContext
		} // end openTransaction
		
		if (tName.equals("commitTransaction")) {
			//no-op, we do on exit
		} // end commitTransaction
		
		if (tName.equals("deleteTransaction")) {
			userInfo().transactionManager.cancel(currentUser, id);
			userInfo().currentTransactionContext = null;
			userInfo().knownTransactionContexts.remove(id);
		} // end deleteTransaction
		
	} 
	
	private void initObjectContext(String oName, String id, String type, String oMmbaseId)
		throws TransactionHandlerException {
			
		if (_debug) debug("-> " + oName + " id(" + id + ") type(" + type + ") oMmbaseId(" + oMmbaseId + ")", 2);
		// check if we are inside transaction context, if not error
		if (userInfo().currentTransactionContext == null) throw
				new TransactionHandlerException(oName + " id " + id + " : not in tansaction context");
		// create object, if no Id create one, remember it's anonymous
		if (oName.equals("createObject")) {
			if (id == null) {
				id = uniqueId();
				userInfo().anonymousObject = true;
			}
			else {
				userInfo().anonymousObject = false;
			}
			// check for existence
			if (userInfo().knownObjectContexts.get(id) != null) {
				throw new TransactionHandlerException(oName + " Object id already exists: " + id);
			}
			// actually create and administrate if not anonymous
			userInfo().currentObjectContext =
				userInfo().tmpObjectManager.createTmpNode(type, currentUser.getName(), id);
			if (!userInfo().anonymousObject)
				userInfo().knownObjectContexts.put(id, userInfo().currentObjectContext);
			// add to tmp cloud
			userInfo().transactionManager.addNode(userInfo().currentTransactionContext, userInfo().currentObjectContext);
			return;
		} // end createObject
		
		if (oName.equals("getObject")) {
			if (id == null) {
				id = uniqueId();
				userInfo().anonymousObject = true;
			}
			else {
				userInfo().anonymousObject = false;
			}
			// check for existence
			if (userInfo().knownObjectContexts.get(id) != null) {
				throw new TransactionHandlerException(oName + " Object id already exists: " + id);
			}
			if (oMmbaseId == null) {
				throw new TransactionHandlerException(oName + " no MMbase id: ");
			}
			// actually get and administrate if not anonymous
			//MMObjectNode m  = userInfo().tmpObjectManager.getNode(oMmbaseId);
			userInfo().currentObjectContext =
				userInfo().tmpObjectManager.getObject(oMmbaseId, currentUser.getName() + id);
			//get Node succeed?
			if (userInfo().currentObjectContext==null) throw 
				new TransactionHandlerException("could not get MMbase object id:  " + oMmbaseId);
			if (!userInfo().anonymousObject)
				userInfo().knownObjectContexts.put(id, userInfo().currentObjectContext);
			// add to tmp cloud
			userInfo().transactionManager.addNode(userInfo().currentTransactionContext, userInfo().currentObjectContext);
			return;
		} // end getObject
		
		// except for create, get (above), all (open, delete) need existing id
		if (id == null) throw 
				new TransactionHandlerException("id is null for " + oName);
		userInfo().currentObjectContext = (String) userInfo().knownObjectContexts.get(id);
		if (userInfo().currentObjectContext == null) throw 
				new TransactionHandlerException(oName + "id is not known " + id);
				
		if (oName.equals("openObject")) {
			// no-op we only need current object context
		} // end openObject
		
		if (oName.equals("deleteObject")) {
			//delete from temp cloud
			userInfo().transactionManager.removeNode(userInfo().currentTransactionContext, userInfo().currentObjectContext);
			// destroy
			userInfo().tmpObjectManager.deleteTmpNode(userInfo().currentObjectContext);
			userInfo().currentObjectContext = null;
			userInfo().knownObjectContexts.remove(id);
		} // end deleteObject
		
	}
	
	private void exitTransactionContext(String tName, String id, String commit, String time) 
		throws TransactionHandlerException {
			
		if (tName.equals("deleteTransaction")) {
			return;
		} // end deleteTransaction

		// we're supposed to have a 'userInfo().currentTransactionContext', so check
		if (userInfo().currentTransactionContext == null) throw 
				new TransactionHandlerException(tName + " no current transaction for " + id);
			
		if ((tName.equals("createTransaction")) || (tName.equals("openTransaction"))) {
			if (commit.equals("true")) {
				userInfo().transactionManager.commit(currentUser, userInfo().currentTransactionContext);
			}
		} // end createTransaction & openTransaction
		
		if (tName.equals("commitTransaction")) {
				userInfo().transactionManager.commit(currentUser, userInfo().currentTransactionContext);
		} // end commitTransaction
		
		
		//out of context now
		if (_debug) debug("<- " + tName + " id(" + id + ") commit(" + commit + ") time(" + time + ")", 1);
		userInfo().currentTransactionContext = null;
 }
	
	private void exitObjectContext(String oName, String id, String type, String oMmbaseId) 
		throws TransactionHandlerException {
		
		if (oName.equals("deleteObject")) {
			return;
		} // end deleteTransaction
		
		// we're supposed to have a 'userInfo().currentObjectContext' for the others, so check
		if (userInfo().currentObjectContext == null) throw 
				new TransactionHandlerException(oName + " no current object context for " + id);
			
		if (oName.equals("createObject")) {
		} // end createObject
		if (oName.equals("openObject")) {
		} // end openObject
		
		if (oName.equals("getObject")) {
		} // end getObject
		
		//out of context now
		if (_debug) debug("<- " + oName + " id(" + id + ") type(" + type + ") oMmbaseId(" + oMmbaseId + ")", 2);
		userInfo().currentObjectContext = null;

	}
	
	private void executeFieldContext(String oId, String fieldName, String fieldValue) throws
		TransactionHandlerException {

		if (_debug) debug("-X Object " + oId + ": [" + fieldName + "] set to: " + fieldValue, 3);
		
		//check that we are inside object context
		if (userInfo().currentObjectContext == null) throw
				new TransactionHandlerException(oId + " set field " + fieldName + " to " + fieldValue);
		
		userInfo().tmpObjectManager.setObjectField(userInfo().currentObjectContext, fieldName, fieldValue);
	}
	
	private String uniqueId() {
		return "ID" + rand.nextInt(10000);
	}
	///
	// actual code ends here, rest is temporary or for testing
	//
	
	/*
	 * Inner classes
	 * Inner classes
	 * Inner classes
	 * Inner classes
	 *
	 * a work around for the user problem
	 *
	 */
	class User {
		private String name;
		public User(String name) {
			this.name= name;
		}
		
		String getName() { return name;}
	}

	private User currentUser;
	private static Random rand;

	/*
	 * own exception class
	 */
	class TransactionHandlerException extends Exception {
		TransactionHandlerException(String s) { super(s); }
	}
	
	/* 
	 *container class for info per user for thread safety
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
	}
	
		/*
		 * use to test parser without running MMbase
		 * use to test parser without running MMbase
		 * use to test parser without running MMbase
		 * use to test parser without running MMbase
		 * use to test parser without running MMbase
		 */
	public static void main(String[] args) {
		
		TransactionHandler pt = new TransactionHandler();
		pt.init();
		pt.setDymmyHandlers();
		pt.onload();
	
		pt.parse("file:///java/xml/Transactions.xml", null);
		System.out.println("END OF MAIN");
		// if we don't exit the system doesn't ????
		System.exit(0);
	}
	
	private void setDymmyHandlers() {
		user.transactionManager = new MyTransactionManager(mmbase);
		user.tmpObjectManager = new MyTemporaryNodeManager(mmbase);
	}
	
}