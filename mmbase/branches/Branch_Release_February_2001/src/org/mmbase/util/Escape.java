/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.util;

public class Escape {

	static public String singlequote(String str) {
		 String line=null,obj;
		 int idx;
		if (str!=null) {
		 /* Single ' protection */
		 line=new String("");
		 obj=new String(str);
		 while((idx=obj.indexOf('\''))!=-1) {
			 line+=obj.substring(0,idx)+"''";
			 obj=obj.substring(idx+1);
		 }
		 line=line+obj;
		}
		 return(line);
	}	
}
