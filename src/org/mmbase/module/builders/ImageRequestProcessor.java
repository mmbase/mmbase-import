/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
/*
	$Id: ImageRequestProcessor.java,v 1.2 2000-06-06 21:31:58 wwwtech Exp $

	$Log: not supported by cvs2svn $
	Revision 1.1  2000/06/05 14:42:15  wwwtech
	Rico: image queuing built in plus parallel converters
	
*/
package org.mmbase.module.builders;

import java.util.*;
import org.mmbase.module.builders.*;
import org.mmbase.module.core.*;
import org.mmbase.util.*;

/**
 * @author Rico Jansen
 * @version $Id: ImageRequestProcessor.java,v 1.2 2000-06-06 21:31:58 wwwtech Exp $
 */
public class ImageRequestProcessor implements Runnable {
	private String classname = getClass().getName();
	private boolean debug = true;
	private void debug(String msg) { System.out.println(classname+":"+msg);}
	Thread kicker=null;

	MMObjectBuilder images;
	ImageConvertInterface convert;
	Queue queue;
	Hashtable table;

	public ImageRequestProcessor(MMObjectBuilder images,ImageConvertInterface convert,Queue queue,Hashtable table) {
		this.images=images;
		this.convert=convert;
		this.queue=queue;
		this.table=table;
		start();
	}

	public void start() {
		if (kicker == null) {
			kicker = new Thread(this,"ImageConvert");
			kicker.start();
		}
	}
	
	public void stop() {
		/* Stop thread */
		kicker.setPriority(Thread.MIN_PRIORITY);  
		kicker = null;
	}

	public void run() {
		ImageRequest req;

		try {
			while(kicker!=null) {
				req=(ImageRequest)queue.get();
				processRequest(req);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void processRequest(ImageRequest req) {
		Vector params;
		String ckey;
		byte[] picture,inputpicture;
		int id;

		inputpicture=req.getInput();
		params=req.getParams();
		ckey=req.getKey();
		id=req.getId();

		picture=convert.ConvertImage(inputpicture,params);
		if (picture!=null) {
			MMObjectNode newnode=images.getNewNode("imagesmodule");
			newnode.setValue("ckey",ckey);
			newnode.setValue("id",id);
			newnode.setValue("handle",picture);
			newnode.setValue("filesize",picture.length);
			int i=newnode.insert("imagesmodule");
			if (i<0) {
				debug("processRequest: Can't insert cache entry id="+id+" key="+ckey);
			}
		} else {
			debug("processRequest(): Convert problem params : "+params);
		}
		req.setOutput(picture);
		table.remove(ckey);
	}
}
