/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
/*
$Id: MMHypersonic2Node.java,v 1.4.6.1 2002-03-26 22:42:36 gerard Exp $

$Log: not supported by cvs2svn $
Revision 1.4  2000/07/15 15:33:38  daniel
removed more to MMSQL92.node

Revision 1.3  2000/06/25 13:12:09  wwwtech
Daniel.. changed/added method for getConnection

Revision 1.17  2000/06/25 00:39:14  wwwtech
Daniel... removed some debug

Revision 1.16  2000/06/24 23:19:29  wwwtech
Daniel.. changed init call for XML parser

Revision 1.15  2000/06/24 18:42:46  wwwtech
Daniel.. added auto convertor for illegal fieldnames

Revision 1.14  2000/06/23 09:52:29  wwwtech
Daniel. fix for handle BYTE

Revision 1.13  2000/06/22 22:32:44  wwwtech
Daniel : added byte support

Revision 1.12  2000/06/22 13:29:45  wwwtech
daniel

Revision 1.11  2000/06/22 12:56:37  wwwtech
Daniel

Revision 1.10  2000/06/22 12:21:04  install
Daniel, if something goes wrong blame me

Revision 1.9  2000/06/20 14:32:26  install
Rob: turned off some debug information

Revision 1.8  2000/06/20 09:32:46  wwwtech
fixed otype first run bug for xml config

Revision 1.7  2000/06/20 08:49:16  wwwtech
better config loading

Revision 1.6  2000/06/20 08:20:14  wwwtech
changed create calls to xml

Revision 1.5  2000/06/06 20:36:25  wwwtech
added XML create and convert code

Revision 1.4  2000/05/15 14:47:48  wwwtech
Rico: fixed double close() bug in getDBText en getDBBtye()

Revision 1.3  2000/04/18 23:16:17  wwwtech
new decodefield routine

Revision 1.2  2000/04/15 21:31:33  wwwtech
daniel: removed overrriden methods

Revision 1.1  2000/04/15 20:42:44  wwwtech
fixes for informix and split in sql92 poging 2

Revision 1.9  2000/04/12 11:34:57  wwwtech
Rico: built type of builder detection in create phase

Revision 1.8  2000/03/31 16:01:49  wwwtech
Davzev: Fixed insert() for when node builder is typedef

Revision 1.7  2000/03/31 15:15:27  wwwtech
Davzev: Changend insert() debug code for DBState checking

Revision 1.6  2000/03/30 13:11:43  wwwtech
Rico: added license

Revision 1.5  2000/03/29 10:44:51  wwwtech
Rob: Licenses changed

Revision 1.4  2000/03/20 16:16:43  wwwtech
davzev: Changed insert method, now insert will be done depending on DBState.

*/
package org.mmbase.module.database.support;

import java.util.*;
import java.io.*;
import java.sql.*;

import org.mmbase.module.database.*;
import org.mmbase.module.core.*;
import org.mmbase.module.corebuilders.*;
import org.mmbase.util.*;


//XercesParser
import org.apache.xerces.parsers.*;
import org.xml.sax.*;

/**
* MMSQL92Node implements the MMJdbc2NodeInterface for
* sql92 types of database this is the class used to abstact the query's
* needed for mmbase for each database.
*
* It is now deprecated and only kept for people using the old
* hypersonic database instead of the new version Hsqldb.
*
* @deprecated use {@link MMHsqldb2Node}
* @author Daniel Ockeloen
* @$Revision: 1.4.6.1 $ $Date: 2002-03-26 22:42:36 $
*/
public class MMHypersonic2Node extends MMSQL92Node {


	public MMObjectNode decodeDBnodeField(MMObjectNode node,String fieldname, ResultSet rs,int i,String prefix) {
		fieldname=fieldname.toLowerCase();
		return(super.decodeDBnodeField(node,fieldname,rs,i,prefix));
	}


	public MultiConnection getConnection(JDBCInterface jdbc) throws SQLException {
		MultiConnection con=jdbc.getConnection("jdbc:HypersonicSQL:"+jdbc.getDatabaseName(),jdbc.getUser(),jdbc.getPassword());

		return(con);
	}

}
