/* -*- tab-width: 4; -*-

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.module.builders;

import java.util.*;

import org.mmbase.module.core.*;
import org.mmbase.util.*;

import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 * The pccard builder contains pccard that MMBase can use.
 * These pccard will implement the ImageInterface so that the Image builder
 * can access the pccard. The pccard will use a implementation that will
 * also be defined by a builder.
 * 
 * @author Rob Vermeulen
 * @date 12 juli 2000
 */

/**
 */
public class PCCards extends MMObjectBuilder implements MMBaseObserver {

    private static Logger log = Logging.getLoggerInstance(PCCards.class.getName()); 
	public final static String buildername = "pccard";
	public static java.util.Properties driveprops= null;

	public PCCards() {
	}

	public Vector getList(scanpage sp, StringTagger tagger, StringTokenizer tok) throws org.mmbase.module.ParseException {
		String pccard ="";
        String path = "";
        Vector result = new Vector();

		try {
			pccard = tok.nextToken();
		} catch (Exception e) {
			log.error("Syntax of LIST commando = <LIST BUILDER-pccard-[pccardname]");	
		}

       	String comparefield = "modtime";
       	DirectoryLister imglister = new DirectoryLister(); 
		Enumeration g = search("WHERE name='"+pccard+"'");
        while (g.hasMoreElements()) {
          	MMObjectNode pccardnode=(MMObjectNode)g.nextElement();
           	path=pccardnode.getStringValue("directory");
        }

		Vector unsorted = null;
		Vector sorted = null;
		try {
           	unsorted = imglister.getDirectories(path);  //Retrieve all filepaths
            sorted = imglister.sortDirectories(unsorted,comparefield);
        	result = imglister.createThreeItems(sorted,tagger);
		} catch (Exception e) {
			log.error("Something went wrong in the directory listner,  probably "+path+" does not exists needed by "+pccard);
            log.error(Logging.stackTrace(e));

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

        return (result);
    }

}
