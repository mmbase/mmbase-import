/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
/*
	$Id: Images.java,v 1.36 2000-11-13 18:01:25 eduard Exp $

	$Log: not supported by cvs2svn $
	Revision 1.35  2000/08/06 11:59:35  daniel
	removed some debug
	
	Revision 1.34  2000/08/04 08:59:54  install
	Rob recovered
	
	Revision 1.33  2000/07/22 11:44:21  daniel
	Removed some debug
	
	Revision 1.32  2000/07/20 14:30:32  daniel
	Changed because of a missing call, rob did i delete somthing on the port ?
	
	Revision 1.31  2000/07/17 12:22:36  install
	Rob
	
	Revision 1.30  2000/07/13 15:57:31  install
	Rob: Dynamic scanner support almost finished
	
	Revision 1.29  2000/07/13 09:40:28  install
	Rob
	
	Revision 1.28  2000/07/12 12:39:50  install
	Rob: added getDevices method
	
	Revision 1.27  2000/07/12 10:41:36  install
	Rob: Image knows which devices it can use to gain images
	
	Revision 1.26  2000/07/11 12:27:45  install
	Rob: support for different image devices
	
	Revision 1.25  2000/07/06 08:51:29  install
	Rico: fixed property reading
	
	Revision 1.24  2000/07/06 08:40:07  install
	Rico: added params to Images builder
	
	Revision 1.23  2000/06/15 16:54:39  wwwtech
	Rob: added error message
	
	Revision 1.22  2000/06/14 15:21:34  wwwtech
	Rico: fixed array bug
	
	Revision 1.21  2000/06/08 18:00:12  wwwtech
	Rico: reduced/switched-off debug
	
	Revision 1.20  2000/06/07 17:27:44  wwwtech
	Rico: fixed debug message
	
	Revision 1.19  2000/06/06 21:31:58  wwwtech
	Rico: fixed a serious bug in which incorrect icaches entries where created
	
	Revision 1.18  2000/06/05 15:42:15  wwwtech
	Rico: fixed count in number of requests
	
	Revision 1.17  2000/06/05 14:42:15  wwwtech
	Rico: image queuing built in plus parallel converters
	
	Revision 1.16  2000/06/02 11:20:45  wwwtech
	Rico: made the param checking more robust
	
	Revision 1.15  2000/06/02 10:57:48  wwwtech
	Rico: seperated conversion from the builder
	
	Revision 1.10  2000/04/05 11:52:16  wwwtech
	Rico: added debug, so you can see you need to load icaches as well
	
	Revision 1.9  2000/03/30 14:15:04  wwwtech
	Rico: added static string to reference the Path to the converter as per suggestion of Arjen de Vries, NOTE that this has to be configurable instead of static like this
	
	Revision 1.8  2000/03/30 13:11:32  wwwtech
	Rico: added license
	
	Revision 1.7  2000/03/29 10:59:22  wwwtech
	Rob: Licenses changed
	
	Revision 1.6  2000/03/24 14:33:58  wwwtech
	Rico: total recompile
	
	Revision 1.5  2000/03/14 12:50:16  wwwtech
	Rico: changed gamma params from / seperator to , due to image cache problems
	
	Revision 1.4  2000/03/09 10:02:01  wwwtech
	Rico: added extra debug in case of failure
	
	Revision 1.3  2000/03/08 11:04:47  wwwtech
	Rico: added synchroniztion to images calculation
	
	Revision 1.2  2000/02/24 14:03:27  wwwtech
	Rico: fixed serveral debug messages plus added some
	
*/
package org.mmbase.module.builders;

import java.util.*;
import java.io.*;
import java.sql.*;

import org.mmbase.module.builders.*;
import org.mmbase.module.database.*;
import org.mmbase.module.core.*;
import org.mmbase.util.*;

/**
 *
 * images holds the images and provides ways to insert, retract and
 * search on them.
 *
 * @author Daniel Ockeloen, Rico Jansen
 * @version $Id: Images.java,v 1.36 2000-11-13 18:01:25 eduard Exp $
 */
public class Images extends MMObjectBuilder {
	private String classname = getClass().getName();
	private boolean debug = false;

	ImageConvertInterface imageconvert=null;
	Hashtable ImageConvertParams=new Hashtable();

	// Currenctly only ImageMagick works / this gets parameterized soon
	protected static String ImageConvertClass="org.mmbase.module.builders.ConvertImageMagick";
	protected int MaxConcurrentRequests=2;

	protected int MaxRequests=32;
	protected Queue imageRequestQueue=new Queue(MaxRequests);
	protected Hashtable imageRequestTable=new Hashtable(MaxRequests);
	protected ImageRequestProcessor ireqprocessors[];

	public boolean init() {
		super.init();
		
		String tmp;
		int itmp;
		tmp=getInitParameter("ImageConvertClass");
		if (tmp!=null) ImageConvertClass=tmp;
		getImageConvertParams(getInitParameters());
		tmp=getInitParameter("MaxConcurrentRequests");
		if (tmp!=null) {
			try {
				itmp=Integer.parseInt(tmp);
			} 
			catch (NumberFormatException e) {
				itmp=2;
			} MaxConcurrentRequests=itmp;
		}

		imageconvert=loadImageConverter(ImageConvertClass);
		imageconvert.init(ImageConvertParams);

		ImageCaches bul=(ImageCaches)mmb.getMMObject("icaches");
		if(bul==null) {
			debug("Error: Place icaches in objects.def before images");
		}
		// Startup parrallel converters
		ireqprocessors=new ImageRequestProcessor[MaxConcurrentRequests];
		if (debug) debug("Starting "+MaxConcurrentRequests+" Converters");
		for (int i=0;i<MaxConcurrentRequests;i++) {
			ireqprocessors[i]=new ImageRequestProcessor(bul,imageconvert,imageRequestQueue,imageRequestTable);
		}
		return(true);
	}

	public String getGUIIndicator(MMObjectNode node) {
		int num=node.getIntValue("number");
		if (num!=-1) {
			// NOTE that this has to be configurable instead of static like this
			return("<A HREF=\"/img.db?"+node.getIntValue("number")+"\" TARGET=\"_new\"><IMG SRC=\"/img.db?"+node.getIntValue("number")+"+s(100x60)\" BORDER=0></A>");
		}
		return(null);
	}

	public void setDefaults(MMObjectNode node) {
		node.setValue("description","");
	}

	public String getGUIIndicator(String field,MMObjectNode node) {
		if (field.equals("handle")) {
			int num=node.getIntValue("number");
			if (num!=-1) {
				// NOTE that this has to be configurable instead of static like this			
				return("<A HREF=\"/img.db?"+num+"\" TARGET=\"_new\"><IMG SRC=\"/img.db?"+num+"+s(100x60)\" BORDER=0></A>");
			}
		}
		return(null);
	}

	// called by init..used to retrieve all settings
	private void getImageConvertParams(Hashtable params) {
		String key;
		for (Enumeration e=params.keys();e.hasMoreElements();) {
			key=(String)e.nextElement();
			if (key.startsWith("ImageConvert.")) {
				ImageConvertParams.put(key,params.get(key));
			}
		}
	}

	private ImageConvertInterface loadImageConverter(String classname) {
		Class cl;
		ImageConvertInterface ici=null;

		try {
			cl=Class.forName(classname);
			ici=(ImageConvertInterface)cl.newInstance();
			if (debug) debug("loadImageConverter(): loaded : "+classname);
		} catch (Exception e) {
			debug("loadImageConverter(): can't load : "+classname);
		}
		return(ici);
	}


	/**	Will return "jpg" as default type, or one of the strings in params, must contain the following "f(type)" where type will be returned
 	 	*	@param params a <code>Vector</code> of <code>String</code>s, which could contain the "f(type)" string
		*	@return "jpg" by default, or the first occurence of "f(type)"
 	 	*/
	public String getImageMimeType(Vector params) {
		String format=null;
		String key;

		for (Enumeration e=params.elements();e.hasMoreElements();) {
			key=(String)e.nextElement();

			// look if our string is long enough...
			if(key != null && key.length() > 2) {
				// first look if we start with an "f("... format is f(gif)
				if(key.startsWith("f(")) {
					// one search function remaining...
					int pos=key.lastIndexOf(')');
					// we know for sure that our "(" is at pos 1, so we can define this hard...
					format = key.substring(2,pos);
					break;
				}
			}
		}
		if (format==null) format="jpg";
		String mimetype=mmb.getMimeType(format);
		if (debug) debug("getImageMimeType: mmb.getMimeType("+format+") = "+mimetype);
		return(mimetype);
	}	

	// glue method until org.mmbase.servlet.servdb is updated
	/** Returns a picture wich belongs to the given param line, with caching
		* @param sp Not needed at this moment,... 
 	 	*	@param params The name/id of the picture, followed by operations, which can be performed on the picture..
		*	@return null if something goes wrong, otherwise the picture in a byte[]
		* @depricated glue method until org.mmbase.servlet.servdb is updated
		*/	
	public byte[] getImageBytes5(scanpage sp,Vector params) {
		return getImageBytes(sp,params);
	}

	/** Returns a picture wich belongs to the given param line, with caching
		* @param sp Not needed at this moment,... 
 	 	*	@param params The name/id of the picture, followed by operations, which can be performed on the picture..
		*	@return null if something goes wrong, otherwise the picture in a byte[]
		*/
	// should scanpage be removed ???? when yes, must be marked as depricated
	public byte[] getImageBytes(scanpage sp,Vector params) {
		byte[] picture = getCachedImage(params);

		if(picture != null && picture.length > 0) {
			return picture;
		} else {
			return getOriginalImage(params);
		}
	}

	/** 
		* This function will flatten the parameters to an unique key, so that an image can be found in the cache
		* @param params a <code>Vector</code> of <code>String</code>s, with a size greater then 0 and not null
		* @returns a string containing the key for this vector, or <code>null</code>,....
		*/
	private String flattenParameters(Vector params) {
		if (params==null || params.size() == 0) {
			debug("flattenParameters: no parameters");
			return null;				
		}
		// flatten parameters as a 'hashed' key;
		String ckey="";
		Enumeration enum=params.elements();
		while(enum.hasMoreElements()) {
			ckey += (String) enum.nextElement();
		}
		// skip spaces at beginning and ending..
		ckey = ckey.trim();
		if(ckey.length() > 0) {
			return ckey;
		}			
		else {
			debug("flattenParameters: empty parameters");		
			return null;
		}
	}
	
	/**	Will return null when not in cache, and otherwise a byte [] representing the picture..	
 	 	*	@param params a <code>Vector</code> of <code>String</code>s, containing the name/id of the picture, followed by operations, which can be performed on the picture..
		*	@return null if something goes wrong, otherwise the picture in a byte[]
 	 	*/
	public byte[] getCachedImage(Vector params) {		
		// get a connection to the cache module
		ImageCaches imageCacheBuilder = (ImageCaches) mmb.getMMObject("icaches");
		if(imageCacheBuilder == null) {
			debug("getCachedImage(): ERROR builder icaches not loaded, load it by putting it in objects.def");
			return null;
		}
		
		// get our hashcode
		String ckey = flattenParameters(params);
		if(ckey == null) {
			debug("getCachedImage: no parameters");				
			return null;
		}
		
		// now get the actual bytes
		byte[] cachedPicture = null;
		cachedPicture = imageCacheBuilder.getCkeyNode(ckey);
		return cachedPicture;
	}
	
	/**	Will return null when something goes wrong otherwise, a byte[] whcih represents the picture
 	 	*	@param params a <code>Vector</code> of <code>String</code>s, containing the name/id of the picture, followed by operations, which can be performed on the picture..
		*	@return null if something goes wrong, otherwise the picture in a byte[]
 	 	*/
	public byte[] getOriginalImage(Vector params) {
		if (params==null || params.size() == 0) {
			debug("getOriginalImage: no parameters");
			return null;
		}
	
		// get our hashcode
		String ckey = flattenParameters(params);
		if(ckey == null) {
			debug("getCachedImage: no parameters");				
			return null;
		}		

		// try to resolve the number of our object (first param) (could also be the name)
		int objectId = convertAlias((String)params.elementAt(0));
		if ( objectId < 0 ) {
			// why is 0 a valid object number???
			debug("getOriginalImage: Parameter is not a valid image "+objectId);
			return null;
		}

		// retrieve the original image
		MMObjectNode node;
		node=getNode(objectId);
		
		// get the Object...
		if(node == null) {
			debug("ConvertImage: Image node not found "+objectId);
			return null;
		}
		
		// get  the bytes from the Object (assume in field handle)
		byte[] inputPicture=node.getByteValue("handle");
		if(inputPicture == null) {
			debug("ConvertImage: Image Node is bad "+objectId);
			return null;
		}
		
		// convert the image, this will be done in an special thread,...
		synchronized(imageRequestTable) {
			ImageRequest req = (ImageRequest) imageRequestTable.get(ckey);
			if (req != null) {
				if (debug) debug("ConvertImage: a conversion in progress ("+ckey+")...  (requests="+(req.count()+1)+")");
				return null;
			} else {
				req = new ImageRequest(objectId, ckey, params, inputPicture);
				imageRequestTable.put(ckey,req);
				imageRequestQueue.append(req);
				return req.getOutput();
			}
		}
	}

	public int convertAlias(String num) {
		// check if its a number if not check for name
		int number=-1;
		try {
			number=Integer.parseInt(num);
		} catch(NumberFormatException e) {
			if (num!=null && !num.equals("")) {
				Enumeration g=search("WHERE title='"+num+"'");
				while (g.hasMoreElements()) {
					MMObjectNode imgnode=(MMObjectNode)g.nextElement();
					number=imgnode.getIntValue("number");
				}
			}
		}	
		return(number);
	}

	/**
		*	
	 	*/
 	public Vector getList(scanpage sp, StringTagger tagger, StringTokenizer tok) throws org.mmbase.module.ParseException {
		Vector devices = new Vector();
    
		if (tok.hasMoreTokens()) {
			String cmd=tok.nextToken();

      if (cmd.equals("devices")) {
				if(mmb.getMMObject("scanners")!=null) {
					getDevices("scanners",devices);
				} 
				if(mmb.getMMObject("cameras")!=null) {
					getDevices("cameras",devices);
				} 
				if(mmb.getMMObject("pccards")!=null) {
					getDevices("pccards",devices);
				} 
		    tagger.setValue("ITEMS","2");
				return devices;	
			}
		}
  	return(null);
  }
	

	/**
	 * get all devices of given devicetype
	 * e.g. give all scanners.
	 */
	private void getDevices(String devicetype, Vector devices) {
		MMObjectBuilder mmob = mmb.getMMObject(devicetype);
		Vector v = mmob.searchVector("");	
		Enumeration e = v.elements();
		
		while (e.hasMoreElements()) {
			MMObjectNode mmon = (MMObjectNode)e.nextElement();
			String name  = "" + mmon.getValue("name");
			devices.addElement(devicetype);
			devices.addElement(name);
		}
	}	
}
		
