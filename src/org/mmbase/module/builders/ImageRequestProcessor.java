/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.module.builders;

import java.util.*;
import org.mmbase.module.builders.*;
import org.mmbase.module.core.*;
import org.mmbase.util.*;
import org.mmbase.util.logging.*;

/**
 * @author Rico Jansen
 * @version $Id: ImageRequestProcessor.java,v 1.6 2001-03-26 09:44:30 install Exp $
 */
public class ImageRequestProcessor implements Runnable {

    private static Logger log = Logging.getLoggerInstance(ImageRequestProcessor.class.getName());
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

		if (inputpicture==null || inputpicture.length==0) {
			log.debug("processRequest : input is empty : "+id);
			picture=new byte[0];
		} else {
			picture=convert.ConvertImage(inputpicture,params);
			if (picture!=null) {
				MMObjectNode newnode=images.getNewNode("imagesmodule");
				newnode.setValue("ckey",ckey);
				newnode.setValue("id",id);
				newnode.setValue("handle",picture);
				newnode.setValue("filesize",picture.length);
				int i=newnode.insert("imagesmodule");
				if (i<0) {
					log.warn("processRequest: Can't insert cache entry id="+id+" key="+ckey);
				}
			} else {
				log.warn("processRequest(): Convert problem params : "+params);
				picture=new byte[0];
			}
		}
		req.setOutput(picture);
		table.remove(ckey);
	}
}
