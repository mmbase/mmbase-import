/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

import java.lang.*;
import java.io.*;
import java.awt.*;

/**
 * Class startAppTool
 * 
 * @javadoc
 * @rename StartAppTool
  */

public class startAppTool extends Object {

	public static void main(String[] args) {
		System.out.println("Starting MMAppTool");
		if (args.length>0) {
			org.mmbase.module.tools.MMAppTool.MMAppTool app=new org.mmbase.module.tools.MMAppTool.MMAppTool(args[0]);
		} else {
			org.mmbase.module.tools.MMAppTool.MMAppTool app=new org.mmbase.module.tools.MMAppTool.MMAppTool();
		}
	}

}
