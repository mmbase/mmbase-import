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
//import org.xml.sax.SAXException;

import test.*;


/**
 * TransactionHandler Module
 *
 * @author  $Author: vpro $ 
 * @version $Revision: 1.1 $ $Date: 2000-10-13 09:39:25 $
 */
 
public class TransactionHandler 
	extends Module 
	implements TransactionHandlerInterface {
	
	class User {
		private String name;
		public User(String name) {
			this.name= name;
		}
		
		String getName() { return name;}
	}

	MMBase mmbase;
	TransactionManagerInterface transactionManager;
	TemporaryNodeManagerInterface tmpObjectManager;
	
	
	public TransactionHandler() {
	}
	
	/*
	 * handleTransaction is the method that is called externally
	 * by scanparser. It is the start of the whole chain.
	 */
 public void handleTransaction(
		String template, 
		sessionInfo session, 
		scanpage sp) {
			
		System.out.println(" >>>>handleTransaction in TransactionHandler ");
		System.out.println(template);
		System.out.println(session);
		System.out.println(sp);
		
		InputSource is = new InputSource();
		is.setCharacterStream(new StringReader(template));
		parse(null, is);
	}

	/**
	 * Inits the module (startup final step 2).
	 * This is called second on startup, the module is expected
	 * to read the environment variables it needs. Startup threads,
	 * open connections etc.
	 */
	public void init(){
		System.out.println(">> init TransactionHandler Module ");
		mmbase=(MMBase)getModule("MMBASEROOT");
		transactionManager = new MyTransactionManager(mmbase);
		tmpObjectManager = new MyTemporaryNodeManager(mmbase);
	}
	
	public void onload(){
		System.out.println(">> onload TransactionHandler Module ");
	}


	public static void main(String[] args) {
		TransactionHandler pt = new TransactionHandler();
	
		pt.parse("file:///java/sources/test/Transactions.xml", null);
	  pt.handleTransaction(localString, null, null);
	}
	
	private void parse(String xFile, InputSource iSource) {
	
	
		MMBase mmbase;
		TransactionManagerInterface transactionManager;
		TemporaryNodeManagerInterface tmpObjectManager;
	
		DOMParser parser = new DOMParser();
		
		try {
		   if (xFile ==  null) parser.parse(iSource);
		   if (iSource ==  null) parser.parse(xFile);
		} catch (SAXException se) {
		   se.printStackTrace();
		   System.exit(-1);
		} catch (IOException ioe) {
		   ioe.printStackTrace();
		   System.exit(-2);
		}
		
		
		try {  //catch TransactionHandlerException's here
			
		Document document = parser.getDocument();
		
		// get <Transactions> context
		Element docRootElement = document.getDocumentElement();
		
		// do for all transaction contexts (create-, open-, commit- and deleteTransaction)
		NodeList transactionContextList = docRootElement.getChildNodes();
		for (int i = 0; i < transactionContextList.getLength(); i++) {
			String id = null, commit = null, time = null;
			boolean noId = true;
			Node currentTransactionArgumentNode = null;

			Node transactionContext = transactionContextList.item(i);
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
				//timeOut
				currentTransactionArgumentNode = nm.getNamedItem("timeOut");
				if (currentTransactionArgumentNode != null)
					time = currentTransactionArgumentNode.getNodeValue();
			}
			initTransactionContext(tName, id, commit, time);
			
			//do for all object contexts (create-, open-, get- and deleteObject)
			NodeList objectContextList = transactionContext.getChildNodes();
			for (int j = 0; j < objectContextList.getLength(); j++) {
				String oId = null, oType = null, oMmbaseId = null;
				boolean noOId = true;
				Node currentObjectArgumentNode = null;
				
				Node objectContext = objectContextList.item(j);
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
				NodeList fieldContextList = objectContext.getChildNodes();
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
	}
	
	
	User user = new User("name-of-user");
	
	private boolean anonymousTransaction = true;
	private String currentTransactionContext = null;
	private Hashtable knownTransactionContexts = new Hashtable();
	
	private boolean anonymousObject = true;
	private String currentObjectContext = null;
	private Hashtable knownObjectContexts = new Hashtable();

	private void initTransactionContext(String tName, String id, String commit, String time)
		throws TransactionHandlerException {
			
		debug(" -> " + tName + " id(" + id + ") commit(" + commit + ") time(" + time + ")");
		
		// create transaction, if no Id create one, remember it's anonymous
		if (tName.equals("createTransaction")) {
			if (id == null) {
				id = uniqueId();
				anonymousTransaction = true;
			}
			else {
				anonymousTransaction = false;
			}
			// check for existence
			if (knownTransactionContexts.get(id) != null) {
				throw new TransactionHandlerException(tName + " transaction id already exists: " + id);
			}
			// actually create and administrate if not anonymous
			currentTransactionContext = transactionManager.create(user, id);
			if (!anonymousTransaction)
				knownTransactionContexts.put(id, currentTransactionContext);
			return;
		} // end createTransaction
		
		// except for create (above), all (open commit, cancel) need existing id
		if (id == null) throw 
				new TransactionHandlerException("id is null for " + tName);
		currentTransactionContext = (String) knownTransactionContexts.get(id);
		if (currentTransactionContext == null) throw 
				new TransactionHandlerException(tName + "id is not known " + id);
				
		if (tName.equals("openTransaction")) {
			// no-op we only need currentTransactionContext
		} // end openTransaction
		
		if (tName.equals("commitTransaction")) {
			//no-op, we do on exit
		} // end commitTransaction
		
		if (tName.equals("deleteTransaction")) {
			transactionManager.cancel(user, id);
			currentTransactionContext = null;
			knownTransactionContexts.remove(id);
		} // end deleteTransaction
		
	} 
	
	private void initObjectContext(String oName, String id, String type, String oMmbaseId)
		throws TransactionHandlerException {
			
		debug("\t -> " + oName + " id(" + id + ") type(" + type + ") oMmbaseId(" + oMmbaseId + ")");
		// check if we are inside transaction context, if not error
		if (currentTransactionContext == null) throw
				new TransactionHandlerException(oName + " id " + id + " : not in tansaction context");
		// create object, if no Id create one, remember it's anonymous
		if (oName.equals("createObject")) {
			if (id == null) {
				id = uniqueId();
				anonymousObject = true;
			}
			else {
				anonymousObject = false;
			}
			// check for existence
			if (knownObjectContexts.get(id) != null) {
				throw new TransactionHandlerException(oName + " Object id already exists: " + id);
			}
			// actually create and administrate if not anonymous
			currentObjectContext = tmpObjectManager.createTmpNode(type, user.getName(), id);
			if (!anonymousObject)
				knownObjectContexts.put(id, currentObjectContext);
			// add to tmp cloud
			transactionManager.addNode(currentTransactionContext, currentObjectContext);
			return;
		} // end createObject
		
		if (oName.equals("getObject")) {
			if (id == null) {
				id = uniqueId();
				anonymousObject = true;
			}
			else {
				anonymousObject = false;
			}
			// check for existence
			if (knownObjectContexts.get(id) != null) {
				throw new TransactionHandlerException(oName + " Object id already exists: " + id);
			}
			if (oMmbaseId == null) {
				throw new TransactionHandlerException(oName + " no MMbase id: ");
			}
			// actually get and administrate if not anonymous
			MMObjectNode m  = tmpObjectManager.getNode(oMmbaseId);
			currentObjectContext = tmpObjectManager.getObject(oMmbaseId);
			if (!anonymousObject)
				knownObjectContexts.put(id, currentObjectContext);
			// add to tmp cloud
			transactionManager.addNode(currentTransactionContext, currentObjectContext);
			return;
		} // end getObject
		
		// except for create, get (above), all (open, delete) need existing id
		if (id == null) throw 
				new TransactionHandlerException("id is null for " + oName);
		currentObjectContext = (String) knownObjectContexts.get(id);
		if (currentObjectContext == null) throw 
				new TransactionHandlerException(oName + "id is not known " + id);
				
		if (oName.equals("openObject")) {
			// no-op we only need current object context
		} // end openObject
		
		if (oName.equals("deleteObject")) {
			//delete from temp cloud
			transactionManager.removeNode(currentTransactionContext, currentObjectContext);
			// destroy
			tmpObjectManager.deleteTmpNode(currentObjectContext);
			currentObjectContext = null;
			knownObjectContexts.remove(id);
		} // end deleteObject
		
	}
	
	private void exitTransactionContext(String tName, String id, String commit, String time) 
		throws TransactionHandlerException {
			
		// we're supposed to have a 'currentTransactionContext', so check
		if (currentTransactionContext == null) throw 
				new TransactionHandlerException(tName + " no current transaction for " + id);
			
		if ((tName.equals("createTransaction")) || (tName.equals("openTransaction"))) {
			if (commit.equals("true")) {
				transactionManager.commit(user, currentTransactionContext);
			}
		} // end createTransaction & openTransaction
		
		if (tName.equals("commitTransaction")) {
				transactionManager.commit(user, currentTransactionContext);
		} // end commitTransaction
		
		if (tName.equals("deleteTransaction")) {
				transactionManager.cancel(user, currentTransactionContext);
		} // end deleteTransaction
		
		//out of context now
		debug(" <- " + tName + " id(" + id + ") commit(" + commit + ") time(" + time + ")");
		currentTransactionContext = null;
 }
	
	private void exitObjectContext(String oName, String id, String type, String oMmbaseId) 
		throws TransactionHandlerException {
		
		if (oName.equals("deleteObject")) {
			return;
		} // end deleteTransaction
		
		// we're supposed to have a 'currentObjectContext' for the others, so check
		if (currentObjectContext == null) throw 
				new TransactionHandlerException(oName + " no current object context for " + id);
			
		if (oName.equals("createObject")) {
		} // end createObject
		if (oName.equals("openObject")) {
		} // end openObject
		
		if (oName.equals("getObject")) {
		} // end getObject
		
		//out of context now
		debug("\t <- " + oName + " id(" + id + ") type(" + type + ") oMmbaseId(" + oMmbaseId + ")\n");
		currentObjectContext = null;

	}
	
	private void executeFieldContext(String oId, String fieldName, String fieldValue) throws
		TransactionHandlerException {

		debug("\t\t -X Object " + oId + ": [" + fieldName + "] set to: " + fieldValue);
		
		//check that we are inside object context
		if (currentObjectContext == null) throw
				new TransactionHandlerException(oId + " set field " + fieldName + " to " + fieldValue);
		
		tmpObjectManager.setObjectField(oId, fieldName, fieldValue);
	}
	
	private String uniqueId() {
		return "U" + System.currentTimeMillis();
	}
	
	private void debug(String s) {
		System.out.println(s);
	}
	
	private void debug1(String s) {
		System.out.print(s + " ");
	}
	
	
	private static String localString =
	"<?xml version='1.0'?> <!DOCTYPE Transactions SYSTEM \"Transactions.dtd\">";
	
}
