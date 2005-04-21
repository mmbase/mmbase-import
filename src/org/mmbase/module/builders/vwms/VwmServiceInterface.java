/*

VPRO (C)

This source file is part of mmbase and is (c) by VPRO until it is being
placed under opensource. This is a private copy ONLY to be used by the
MMBase partners.

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
