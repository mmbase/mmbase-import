/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.module.builders;

import java.util.*;
import java.sql.*;
import java.io.*;

import javax.servlet.http.*;

import org.mmbase.module.*;
import org.mmbase.module.builders.*;
import org.mmbase.module.core.*;
import org.mmbase.util.*;

/**
 * The scanners builder contains scanners that MMBase can use.
 * These scanners will implement the ImageInterface so that the Image builder
 * can access the scanners. The scanner will use a implementation that will
 * also be defined by a builder.
 * 
 * @author Rob Vermeulen
 * @date 12 juli 2000
 */

/**
 */
public class scanners extends MMObjectBuilder implements MMBaseObserver {

	public final static String buildername = "scanners";
	public static java.util.Properties driveprops= null;

	public scanners() {
	}

	public Vector getList(scanpage sp, StringTagger tagger, StringTokenizer tok) throws org.mmbase.module.ParseException {
		String scanner ="";
        String path = "";
        Vector result = new Vector();

		try {
			scanner = tok.nextToken();
		} catch (Exception e) {
			debug("Syntax of LIST commando = <LIST BUILDER-scanner-[scannername]");	
		}
		System.out.println("scanners scanner="+scanner);

       	String comparefield = "modtime";
       	DirectoryLister imglister = new DirectoryLister(); 
		Enumeration g = search("WHERE name='"+scanner+"'");
        while (g.hasMoreElements()) {
          	MMObjectNode scannernode=(MMObjectNode)g.nextElement();
           	path=scannernode.getStringValue("directory");
        }
		System.out.println(path);

		Vector unsorted = null;
		Vector sorted = null;
		try {
           	unsorted = imglister.getDirectories(path);  //Retrieve all filepaths
            sorted = imglister.sortDirectories(unsorted,comparefield);
        	result = imglister.createThreeItems(sorted,tagger);
		} catch (Exception e) {
			debug("Something went wrong in the directory listner, probably "+path+" does not exists needed by "+scanner);
		}
        tagger.setValue("ITEMS", "3");
        String reverse = tagger.Value("REVERSE");
        if (reverse!=null){
         	if(reverse.equals("YES")){
               	int items = 3;
                result = imglister.reverse(result,items);
            }
        }
        return (result);
    }

}
