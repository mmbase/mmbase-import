package org.mmbase.module.builders;

import java.util.*;
import java.sql.*;

import org.mmbase.module.database.*;
import org.mmbase.module.core.*;
import org.mmbase.util.*;

/**
 * @author Daniel Ockeloen
 * @version 12 Mar 1997
 */
public class ImageAliases extends MMObjectBuilder {

	public String getDefaultUrl(int src) {
		MMObjectNode node=getNode(src);
		String url=node.getStringValue("url");
		return(url);
	}
}
