/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package speeltuin.flax.builders;

import java.io.*;
import java.util.*;

import org.mmbase.storage.search.implementation.*;

import org.mmbase.module.core.MMBaseContext;
import org.mmbase.module.core.MMObjectNode;
import org.mmbase.module.core.MMObjectBuilder;

import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 * Builder that provides an interface to the jars that are used by MMBase
 * @javadoc
 * @author Eduard Witteveen
 * @version $Id: Libraries.java,v 1.1 2004-05-08 17:33:17 eduard Exp $
 */
public class Libraries extends MMObjectBuilder {
    private static Logger log=Logging.getLoggerInstance(Libraries.class.getName());
    private org.mmbase.util.Encode encoder = null;

    public boolean init() {		
        if (!super.init()) return false;
		
		// make sure that all the jar's are added to the builder
		synchronizeLibraries();

		return true;
    }

//	/**
//	 * Node's may not be changed,... only created and deleted
//	 */
//	public boolean setValue(MMObjectNode node,String fieldname, Object originalValue) 
//	{
//		// exising nodes may not be modified!
//		Object newValue = node.values.get(fieldname);
//		if(originalValue!=null && !originalValue.equals(newValue)) 
//		{
//			// restore the original value...
//			node.values.put(fieldname,originalValue);
//			return false;
//		}
//		return true;
//	}

	public int insert(String owner, MMObjectNode node) 
	{
		// DONT USE GETBYTEVALUE, IT TRIGGERS GETVALUE, WHICH IS OVERRIDDEN!
		// byte[] buffer = node.getByteValue("bytes");
		byte[] buffer = (byte[])node.values.get("bytes");
		File file = new File(getAbsoluteFileName(node.getStringValue("filename")));
		if (buffer != null && buffer.length > 0)
		{
			log.info("found bytes for node with filename:" + file.getAbsolutePath() + " trying to save them to disk");
			// look if file doesnt exist,...
			if (file.exists()) 
			{
				log.error("file:" + file.getAbsolutePath() + " did already exist!");
				// we dont overwrite existing files!
				return -1;
			}
			// create the dir structure,...
			if (!file.getParentFile().exists())
			{
				file.getParentFile().mkdirs();
			}
			// we have bytes, so store them into a file....
			setFileBytes(file, buffer);
			log.info("file:" + file.getAbsolutePath() + " has been saved");
		}
		// store otherthings, when there...
		int result = super.insert(owner, node);
		
		if(result < 0) 
		{
			// insert failed,... remove the file we just created,...
			file.delete();
			log.info("file:" + file.getAbsolutePath() + " has been deleted, commit of node failed");
		}
		return result;
	}

	/**
	 * Commit changes to this node to the database. This method indirectly calls {@link #preCommit}.
	 * Use only to commit changes - for adding node, use {@link #insert}.
	 * @param node The node to be committed
	 * @return true if commit successful
	 */
	public boolean commit(MMObjectNode node) 
	{
		// DONT USE GETBYTEVALUE, IT TRIGGERS GETVALUE, WHICH IS OVERRIDDEN!
		// byte[] buffer = node.getByteValue("bytes");
		byte[] buffer = (byte[])node.values.get("bytes");
		File file = new File(getAbsoluteFileName(node.getStringValue("filename")));
		if (buffer != null && buffer.length > 0)
		{
			log.info("found bytes for node with filename:" + file.getAbsolutePath() + " trying to save them to disk");
			// look if file doesnt exist,...
			if (file.exists()) 
			{
				log.error("file:" + file.getAbsolutePath() + " did already exist!");
				// we dont overwrite existing files!
				return false;
			}
			// create the dir structure,...
			if (!file.getParentFile().exists())
			{
				file.getParentFile().mkdirs();
			}
			// we have bytes, so store them into a file....
			setFileBytes(file, buffer);
			log.info("file:" + file.getAbsolutePath() + " has been saved");
		}
		// store otherthings, when there...
		boolean result = super.commit(node);
		
		if(!result) 
		{
			// insert failed,... remove the file we just created,...
			file.delete();
			log.info("file:" + file.getAbsolutePath() + " has been deleted, commit of node failed");
		}
		return result;	
	}

	/**
	 * return the correct information
	 */
	public Object getValue(MMObjectNode node,String field) 
	{
		// return the bytes from the file we opened....
		if (field.equals("bytes")) 
		{
			// return the bytes from the file
			// which can be found on the disk of the location,...
			String filename = node.getStringValue("filename");
			filename = getAbsoluteFileName(filename);
			log.debug("returning bytes from the file:" + filename);
			// look for the file....
			File file = new File(filename);
			// check if we can read the file
			if (!file.canRead()) return null;
			// return the bytes
			return getFileBytes(file);
		}
		return super.getValue(node, field);
	}

	/**
	 * retieve the filename of a file
	 */
	private String getAbsoluteFileName(String filename)
	{
		return MMBaseContext.getHtmlRoot() + File.separator + "WEB-INF" + File.separator + "lib" + File.separator  + filename;
	}

	/**
	 * retieve the filename of a file
	 */
	private String getRelativeFileName(File file)
	{		
		String jarroot = getAbsoluteFileName("");
		String filepath = file.getAbsolutePath();
		if(filepath.startsWith(jarroot))
		{
			return filepath.substring(jarroot.length() - 1);
		}
		else
		{
			// error,... but we need to return something to keep it workign
			log.error("file:" + filepath + " was not an file under the jarroot(" + jarroot + ")");
			return filepath;
		}
	}

	/**
	 * retrieve the file in a byte array
	 */
	private byte[] getFileBytes(File bfile) 
	{
		int filesize = (int)bfile.length();
		byte[] buffer = new byte[filesize];
		try 
		{
			FileInputStream scan = new FileInputStream(bfile);
			int len=scan.read(buffer,0,filesize);
			scan.close();
		} 
		catch(FileNotFoundException e) 
		{
			log.error(e.toString());
		} 
		catch(IOException e) {
			log.warn(e.toString());
		}
		return buffer;
	}
	/**
	 * store the byte array in the file.
	 */
	private boolean setFileBytes(File sfile, byte[] value) 
	{
		try 
		{
			DataOutputStream scan = new DataOutputStream(new FileOutputStream(sfile));
			scan.write(value);
			scan.flush();
			scan.close();
		} 
		catch(Exception e) 
		{
			log.error(Logging.stackTrace(e));
			return false;
		}
		return true;
	}

	public boolean synchronizeLibraries()
	{
		// we need to scan the whole directory root,... 
		// and check if the directories match,...
		// for now done in a very simple way,...
		// we iterate tru all the filestructure,and builder
		// which is sorted on filename,....
		// when a file is not found on the file system,...
		// the node will be removed,... when a file is found but
		// no node, the node will be added....
		File jarroot = new File(getAbsoluteFileName(""));
		SortedSet filesSet = new TreeSet();
		addFileStructure(jarroot, filesSet, true);
		List files = new ArrayList(filesSet);

		// now also get all file's from this builder...
		List nodes;
		try 
		{
			NodeSearchQuery query = new NodeSearchQuery(this);
			BasicStepField sortField = query.getField(getField("filename"));
			query.addSortOrder(sortField);
			nodes = getNodes(query);
		} 
		catch (org.mmbase.storage.search.SearchQueryException e) 
		{
			log.error(e.toString());
			return false;
		}
		
		int filePos = 0;
		int nodePos = 0;
		MMObjectNode node;

		log.info("synchronizing #" + files.size() + " files with #" + nodes.size() + " nodes");

		while(filePos < files.size() && nodePos < nodes.size())
		{
			String fileName = (String)files.get(filePos);
			node = (MMObjectNode)nodes.get(nodePos);
			String nodeName = node.getStringValue("filename");
			int compare = fileName.compareTo(nodeName);
			if (compare < 0) 
			{
				// filename is greater than the current node,...
				// this means that the file no longer exist on disk
				// thus remove the node!
				log.info("removing node for file:" + nodeName + "(next filename:" + fileName + ")");
				removeNode(node);
				nodePos++;
			}
			else if (compare > 0) 
			{
				// filename is smaller than the current node,...
				// this means that the file is not yet known to our system,..
				// we need to add this node..
				node = getNewNode("system");
				node.setValue("filename",fileName);
				log.info("inserting node for file:" + fileName + "(next nodename:" + nodeName + ")");
				insert("system", node);
				filePos++;
			}
			else
			{
				// equals,.. go to next file/node combo
				filePos ++;
				nodePos ++;
			}
		}
		// there are still files that need to be added
		while(filePos < files.size())
		{			
			String fileName = (String)files.get(filePos);
			node = getNewNode("system");
			node.setValue("filename",fileName);
			log.info("inserting node for file:" + fileName + "(no more nodes..)");
			insert("system", node);
			filePos++;
		}
		// there are still nodes that need to be removed
		while(nodePos < nodes.size())
		{
			node = (MMObjectNode)nodes.get(nodePos);
			log.info("removing node for file:" + node.getStringValue("filename") + "(no more files..)");
			removeNode(node);
			nodePos++;
		}
		return true;
	}

	private void addFileStructure(File file, Set info, boolean isRoot)
	{
		// look if we may read the info
		if (!file.canRead()) return;
		// when it is a file,.. simply add...
		if (file.isFile()) 
		{
			info.add(getRelativeFileName(file));
			return;
		}
		// when it is an directory,.. add the file's that are in sub-dirs
		if (file.isDirectory()) 
		{
			File[] files = file.listFiles();
			// for each file in files..
			for(int i =0; i < files.length; i++)
			{
				addFileStructure(files[i], info, false);
			}
		}
	}
}
