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
 * @$Revision: 1.8 $ $Date: 2001-04-05 12:35:19 $
 */
public class g2encoders extends ServiceBuilder implements MMBaseObserver {

    private static Logger log = Logging.getLoggerInstance(g2encoders.class.getName());

	public boolean nodeRemoteChanged(String number,String builder,String ctype) {
		boolean result = false;
		log.debug("nodeRemoteChanged("+number+","+builder+","+ctype+"): Calling super");

		super.nodeRemoteChanged(number,builder,ctype);
		result = nodeChanged(number,builder,ctype);

		log.debug("nodeRemoteChanged("+number+","+builder+","+ctype+"): return("+result+")");
		return result;
	}

	public boolean nodeLocalChanged(String number,String builder,String ctype) {
		boolean result = false;
		log.debug("nodeLocalChanged("+number+","+builder+","+ctype+"): Calling super");

		super.nodeLocalChanged(number,builder,ctype);
		result = nodeChanged(number,builder,ctype);

		log.debug("nodeLocalChanged("+number+","+builder+","+ctype+"): returning("+result+")");
		return result;
	}

	public boolean nodeChanged(String number,String builder,String ctype) {
		boolean result = true;
		log.debug("nodeChanged("+number+","+builder+","+ctype+"), do nothing, return("+result+")");
		return result;
	}
}
