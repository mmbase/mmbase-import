/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

/**
 * Support utility for MMObjectBuilder.getSPartPath
 * This filter filters files with the specified 
 * number in its name
 */

package org.mmbase.util;

import java.io.File;
import java.io.FilenameFilter;

public class SPartFileFilter implements FilenameFilter {
	
	private String nodeNumber;
	
	public SPartFileFilter(String nodeNumber) {
		this.nodeNumber = nodeNumber;
	}
	
	public boolean accept(File dir, String name) {
		int pos = name.indexOf(nodeNumber);
		if (pos<0) return false;
		// Check char before found number, if digit return false
		int c;
		if (pos>0) {
			c = name.charAt(pos-1);
			if ((c>='0') && (c<='9')) return false;
		}
		// Check char after found number, if digit return false
		pos+=nodeNumber.length();
		if (pos<name.length()) {
			c = name.charAt(pos);
			if ((c>='0') && (c<='9')) return false;
		}
		return true;
	}

}
