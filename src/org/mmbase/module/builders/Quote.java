package org.mmbase.module.builders;

import java.sql.*;

import org.mmbase.module.database.*;
import org.mmbase.module.core.*;
import org.mmbase.util.*;

/**
 * @author Arjan Houtman
 */
public class Quote extends MMObjectBuilder {

	public String getGUIIndicator (MMObjectNode node) {
		String str = node.getStringValue ("context");
		if (str.length () > 15) {
			return (str.substring (0,12) + "...");
		} else {
			return (str);
		}
	}

}
