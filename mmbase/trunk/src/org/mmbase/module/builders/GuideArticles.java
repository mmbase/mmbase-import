/*

VPRO (C)

This source file is part of mmbase and is (c) by VPRO until it is being
placed under opensource. This is a private copy ONLY to be used by the
MMBase partners.

*/
package org.mmbase.module.builders;

import java.util.*;
import java.util.Date;
import java.sql.*;

import org.mmbase.module.database.*;
import org.mmbase.module.core.*;
import org.mmbase.util.*;

/**
 * @author Daniel Ockeloen
 * @version 12 Mar 1997
 */
public class GuideArticles extends MMObjectBuilder {

	
	public String getDefaultUrl(int src) {
		return("/data/gids/artikel-index.shtml?"+src);
	}



	// used to create a default teaser by any builder
	public MMObjectNode getDefaultTeaser(MMObjectNode node,MMObjectNode tnode) {
		String title=node.getStringValue("title");
		String body=node.getStringValue("intro");
		tnode.setValue("title",title);
		tnode.setValue("body",body);
		tnode.setValue("state",3);
		tnode.setValue("value",4);
		return(tnode);
	}
}
