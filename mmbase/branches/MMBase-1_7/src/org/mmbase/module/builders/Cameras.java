/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.module.builders;

import java.util.*;

import org.mmbase.module.core.*;

import org.mmbase.util.*;
import org.mmbase.util.logging.*;

/**
 * The camera builder contains camera that MMBase can use.
 * These camera will implement the ImageInterface so that the Image builder
 * can access the camera. The camera will use a implementation that will
 * also be defined by a builder (?).
 *
 * @author Rob Vermeulen
 * @version $Id: Cameras.java,v 1.5 2003-03-04 14:12:22 nico Exp $
 */
public class Cameras extends MMObjectBuilder implements MMBaseObserver {
    private static Logger log = Logging.getLoggerInstance(Cameras.class.getName());

    public Cameras() {
    }

    public Vector getList(scanpage sp, StringTagger tagger, StringTokenizer tok) throws org.mmbase.module.ParseException {
        String camera ="";
        String path = "";
       	String comparefield = "modtime";
        Vector result = new Vector();

	try {
            camera = tok.nextToken();
	} catch (Exception e) {
	    log.error("Syntax of LIST commando = <LIST BUILDER-camera-[cameraname]");
	}

       	DirectoryLister imglister = new DirectoryLister();
	Enumeration g = search("WHERE name='"+camera+"'");
        while (g.hasMoreElements()) {
            MMObjectNode cameranode=(MMObjectNode)g.nextElement();
            path=cameranode.getStringValue("directory");
        }

	Vector unsorted = null;
	Vector sorted = null;
	try {
            unsorted = imglister.getDirectories(path);  //Retrieve all filepaths
            sorted = imglister.sortDirectories(unsorted,comparefield);
            result = imglister.createThreeItems(sorted,tagger);
	} catch (Exception e) {
	    log.error("Something went wrong in the directory listner, probably "+path+" does not exists needed by "+camera);
	}
        tagger.setValue("ITEMS", "3");
        String reverse = tagger.Value("REVERSE");
        if (reverse!=null){
            if(reverse.equals("YES")){
               	int items = 3;
                result = imglister.reverse(result,items);
            }
        }

	// This is very ugly, but I don't want to change the class Directorylistner.
	// This code removes the htmlroot from the 3th argument
	String htmlroot = MMBaseContext.getHtmlRoot();
	for(int i=2; i<= result.size(); i+=3) {
            String s = (String)result.remove(i);
            s="/"+s.substring(htmlroot.length());
            result.insertElementAt(s,i);
	}

        return result;
    }

}
