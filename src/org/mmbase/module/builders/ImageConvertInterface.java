/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.module.builders;

/*
	$Id: ImageConvertInterface.java,v 1.1 2000-06-02 10:57:48 wwwtech Exp $

	$Log: not supported by cvs2svn $
*/

import java.util.*;

public interface ImageConvertInterface {

	public void init(Hashtable params);
	public byte[] ConvertImage(byte[] input,Vector commands);
}
