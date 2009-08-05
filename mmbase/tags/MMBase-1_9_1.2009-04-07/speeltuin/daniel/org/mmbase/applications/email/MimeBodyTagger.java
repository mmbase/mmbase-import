/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.applications.email;

import java.lang.*;
import java.net.*;
import java.util.*;
import java.io.*;

import org.mmbase.module.database.*;
import org.mmbase.module.core.*;
import org.mmbase.util.*;

import org.mmbase.util.logging.Logging;
import org.mmbase.util.logging.Logger;

/**
 * @author Daniel Ockeloen
 * 
 */
public class MimeBodyTagger {

    // logger
    static private Logger log = Logging.getLoggerInstance(MimeBodyTagger.class.getName()); 

    public static Enumeration getMimeBodyParts(String body) {
	String startkey="<multipart ";
	String endkey="</multipart>";

	Vector results=new Vector();

	int pos=body.indexOf(startkey);
	while (pos!=-1) {
		String part=body.substring(pos);
		int endpos=part.indexOf(endkey);
		part=part.substring(startkey.length(),endpos);
		String atr=part.substring(0,part.indexOf(">"));
		part=part.substring(part.indexOf(">")+1);
		StringTagger atrtagger=new StringTagger(atr);

		MimeBodyTag tag=new MimeBodyTag();

		String type=(String)atrtagger.Value("type");
		if (type!=null) tag.setType(type);

		String formatter=(String)atrtagger.Value("formatter");
		if (formatter!=null) tag.setFormatter(formatter);

		String alt=(String)atrtagger.Value("alt");
		if (alt!=null) tag.setAlt(alt);

		String id=(String)atrtagger.Value("id");
		if (id!=null) tag.setId(id);

		String related=(String)atrtagger.Value("related");
		if (related!=null) tag.setRelated(related);

		String file=(String)atrtagger.Value("file");
		if (file!=null) tag.setFile(file);

		String filename=(String)atrtagger.Value("filename");
		if (filename!=null) tag.setFileName(filename);

		String attachment=(String)atrtagger.Value("attachment");
		if (attachment!=null) tag.setAttachment(attachment);

		tag.setText(part);
		
		results.addElement(tag);

		// set body ready for the new part
		endpos=body.indexOf(endkey);
		body=body.substring(endpos+endkey.length());
		pos=body.indexOf(startkey);
	}	
	return results.elements();
    }

}
