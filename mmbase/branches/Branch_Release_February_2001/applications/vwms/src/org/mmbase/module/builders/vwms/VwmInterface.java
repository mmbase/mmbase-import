/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.module.builders.vwms;

import java.util.*;
import java.net.*;
import java.sql.*;

import org.mmbase.module.core.*;
import org.mmbase.module.builders.*;

public interface VwmInterface {
	public void init(MMObjectNode node, Vwms Vwms);
	public boolean addClient(VwmCallBackInterface client);
	public boolean releaseClient(VwmCallBackInterface client);
	public boolean nodeRemoteChanged(String number,String builder,String ctype);
	public boolean nodeLocalChanged(String number,String builder,String ctype);
}
