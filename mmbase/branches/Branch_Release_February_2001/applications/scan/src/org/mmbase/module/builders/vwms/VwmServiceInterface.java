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


import org.mmbase.module.builders.*;

public interface VwmServiceInterface extends VwmInterface {
	public boolean fileChange(String number,String ctype);
	public boolean fileChange(String service,String subservice,String filename);
}
