/*

VPRO (C)

This source file is part of mmbase and is (c) by VPRO until it is being
placed under opensource. This is a private copy ONLY to be used by the
MMBase partners.

*/
package org.mmbase.module;

import java.util.*;
import java.awt.*;
import javax.servlet.http.*;

import org.mmbase.module.core.MMObjectNode;
import org.mmbase.util.*;

public interface sessionsInterface {
	public void init();
	public sessionInfo getSession(scanpage sp,String session);
	public String replace(scanpage sp,String session);
	public String getValue(sessionInfo session,String wanted);
	public String setValue(sessionInfo session,String key,String value);
 	public void addSetValue(sessionInfo session,String key,String value);
 	public void addSetValues(sessionInfo session,String key,Vector values);
	public void setValueFromNode(sessionInfo session, MMObjectNode node );
	public String saveValue(sessionInfo session,String key);
	public void forgetSession(String key);
}
