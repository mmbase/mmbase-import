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

import org.mmbase.module.database.*;
import org.mmbase.module.core.*;
import org.mmbase.util.*;
import org.mmbase.util.logging.*;

/**
 * @author Daniel Ockeloen
 * @$Revision: 1.9 $ $Date: 2001-05-03 10:06:07 $
 */
public class g2encoders extends ServiceBuilder implements MMBaseObserver {

    private static Logger log = Logging.getLoggerInstance(g2encoders.class.getName());

	/**
	 * Calls super and nodeChanged to react to change.
	 * @param number object number of node who's state has been changed.
	 * @param builder a String with the buildername of the node that was changed.
	 * @param ctype a String with the node change type.
	 * @return returnvalue of nodeChanged which is either true or false.
	 */
	public boolean nodeRemoteChanged(String number,String builder,String ctype) {
		return super.nodeRemoteChanged(number,builder,ctype);
	}

	/**
	 * Calls super and nodeChanged to react to change.
	 * @param number object number of node who's state has been changed.
	 * @param builder a String with the buildername of the node that was changed.
	 * @param ctype a String with the node change type.
	 * @return true, always.
	 */
	public boolean nodeLocalChanged(String number,String builder,String ctype) {
		return super.nodeLocalChanged(number,builder,ctype);
	}
}
