/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.module.builders;

import java.util.*;
import java.io.*;
import java.sql.*;

import org.mmbase.module.builders.*;
import org.mmbase.module.database.*;
import org.mmbase.module.core.*;
import org.mmbase.util.*;
import org.mmbase.util.logging.*;

/**
 *
 * images holds the images and provides ways to insert, retract and
 * search on them.
 *
 * @author Daniel Ockeloen, Rico Jansen
 * @version $Id: Images.java,v 1.42 2001-09-12 14:49:37 eduard Exp $
 */
public class Images extends MMObjectBuilder {
    private static Logger log = Logging.getLoggerInstance(Images.class.getName());

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
            log.error("Error: Place icaches in objects.def before images");
        }
        // Startup parrallel converters
        ireqprocessors=new ImageRequestProcessor[MaxConcurrentRequests];
        log.info("Starting "+MaxConcurrentRequests+" Converters");
        for (int i=0;i<MaxConcurrentRequests;i++) {
            ireqprocessors[i]=new ImageRequestProcessor(bul,imageconvert,imageRequestQueue,imageRequestTable);
        }
        return(true);
    }

    public String getGUIIndicator(MMObjectNode node) {
        int num=node.getIntValue("number");
        if (num!=-1) {
                    // NOTE that this has to be configurable instead of static like this
                    return("<a href=\"/img.db?"+node.getIntValue("number")+"\" target=\"_new\"><img src=\"/img.db?"+node.getIntValue("number")+"+s(100x60)\" border=\"0\" alt=\"" + node.getStringValue("title") + "\" /></a>");
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
                return("<a href=\"/img.db?"+num+"\" target=\"_new\"><img src=\"/img.db?"+num+"+s(100x60)\" border=\"0\" alt=\"*\" /></a>");
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
            log.info("loadImageConverter(): loaded : "+classname);
        } catch (Exception e) {
            log.error("loadImageConverter(): can't load : "+classname);
        }
        return(ici);
    }


    /**    
     * Will return "jpg" as default type, or one of the strings in params, must contain the following "f(type)" where type will be returned
     * @param params a <code>Vector</code> of <code>String</code>s, which could contain the "f(type)" string
     * @return "jpg" by default, or the first occurence of "f(type)"
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
        log.debug("getImageMimeType: mmb.getMimeType("+format+") = "+mimetype);
        return(mimetype);
    }    

    // glue method until org.mmbase.servlet.servdb is updated
    /** Returns a picture wich belongs to the given param line, with caching
     * @param sp Not needed at this moment,... 
     * @param params The name/id of the picture, followed by operations, which can be performed on the picture..
     * @return null if something goes wrong, otherwise the picture in a byte[]
     * @deprecated glue method until org.mmbase.servlet.servdb is updated
     */    
    public byte[] getImageBytes5(scanpage sp,Vector params) {
        return getImageBytes(sp,params);
    }

    /** Returns a picture wich belongs to the given param line, with caching
     * @param sp Not needed at this moment,... 
     * @param params The name/id of the picture, followed by operations, which can be performed on the picture..
     * @return null if something goes wrong, otherwise the picture in a byte[]
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
     * @return a string containing the key for this vector, or <code>null</code>,....
     */
    private String flattenParameters(Vector params) {
        if (params==null || params.size() == 0) {
            log.debug("flattenParameters: no parameters");
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
            log.debug("flattenParameters: empty parameters");        
            return null;
        }
    }
    
    /**    
     * Will return null when not in cache, and otherwise a byte [] representing the picture..    
     * @param params a <code>Vector</code> of <code>String</code>s, containing the name/id of the picture, followed by operations, which can be performed on the picture..
     * @return null if something goes wrong, otherwise the picture in a byte[]
     */
    public byte[] getCachedImage(Vector params) {        
        // get a connection to the cache module
        ImageCaches imageCacheBuilder = (ImageCaches) mmb.getMMObject("icaches");
        if(imageCacheBuilder == null) {
            log.error("getCachedImage(): ERROR builder icaches not loaded, load it by putting it in objects.def");
            return null;
        }
        
        // get our hashcode
        String ckey = flattenParameters(params);
        if(ckey == null) {
            log.debug("getCachedImage: no parameters");                
            return null;
        }
        
        // now get the actual bytes
        byte[] cachedPicture = null;
        cachedPicture = imageCacheBuilder.getCkeyNode(ckey);
        return cachedPicture;
    }
    
    /**    
     * Will return null when something goes wrong otherwise, a byte[] whcih represents the picture
     * @param params a <code>Vector</code> of <code>String</code>s, containing the name/id of the picture, followed by operations, which can be performed on the picture..
     * @return null if something goes wrong, otherwise the picture in a byte[]
     */
    public byte[] getOriginalImage(Vector params) {
        if (params==null || params.size() == 0) {
            log.debug("getOriginalImage: no parameters");
            return null;
        }
    
        // get our hashcode
        String ckey = flattenParameters(params);
        if(ckey == null) {
            log.debug("getCachedImage: no parameters");                
            return null;
        }        

        // try to resolve the number of our object (first param) (could also be the name)
        int objectId = convertAlias((String)params.elementAt(0));
        if ( objectId < 0 ) {
            // why is 0 a valid object number???
            log.warn("getOriginalImage: Parameter is not a valid image "+objectId);
            return null;
        }

        // retrieve the original image
        MMObjectNode node;
        node=getNode(objectId);
        
        // get the Object...
        if(node == null) {
            log.warn("ConvertImage: Image node not found "+objectId);
            return null;
        }
        
        // get  the bytes from the Object (assume in field handle)
        byte[] inputPicture=node.getByteValue("handle");
        if(inputPicture == null) {
            log.warn("ConvertImage: Image Node is bad "+objectId);
            return null;
        }
        
        ImageRequest req;
        // convert the image, this will be done in an special thread,...
        synchronized(imageRequestTable) {
            req = (ImageRequest) imageRequestTable.get(ckey);
            if (req != null) {
                log.info("ConvertImage: a conversion in progress ("+ckey+")...  (requests="+(req.count()+1)+")");
            } else {
                req = new ImageRequest(objectId, ckey, params, inputPicture);
                imageRequestTable.put(ckey,req);
                imageRequestQueue.append(req);
            }
        }
        return req.getOutput();
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
    
    /**
     * Override the MMObjectBuilder commit, to invalidate the Image Cache AFTER a modifation to the 
     * image node.
     * Commit changes to this node to the database. This method indirectly calls {@link #preCommit}.
     * Use only to commit changes - for adding node, use {@link #insert}.
     * @param node The node to be committed
     * @return The committed node.
     */
    public boolean commit(MMObjectNode node) {
    	// look if we need to invalidate the image cache...
    	boolean imageCacheInvalid = node.getChanged().contains("handle");	
	// do the commit
    	if(super.commit(node)) {
	    // when cache is invalide, invalidate
	    if(imageCacheInvalid) {
	    	invalidateImageCache(node);
	    }
	    return true;	    
	}
	return false;
    }

    /**
     * Override the MMObjectBuilder removeNode, to invalidate the Image Cache AFTER a delete-ion of the 
     * image node.
     * Remove a node from the cloud.
     * @param node The node to remove.
     */
    public void removeNode(MMObjectNode node) {
        super.removeNode(node);
    	invalidateImageCache(node);
    }
    
    /**
     * Invalidate the Image Cache, if there is one, for a specific ImageNode
     * @param node The image node, which is the original
     */
    private void invalidateImageCache(MMObjectNode node) {
    	ImageCaches icache = (ImageCaches) mmb.getMMObject("icaches");
	if(icache != null) {
	    // we have a icache that is active...
	    icache.invalidate(node);
	}
    }
}
        
