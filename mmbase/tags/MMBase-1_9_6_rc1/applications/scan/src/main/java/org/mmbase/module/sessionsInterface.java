/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.module;

import java.util.*;

import org.mmbase.module.core.MMObjectNode;
import org.mmbase.util.*;

/**
 * @application SCAN
 * @rename SessionsInterface
 * @author vpro
 * @version $Id$
 */
public interface sessionsInterface {
    public void init();
    public sessionInfo getSession(scanpage sp,String session);
    public String replace(scanpage sp,String session);
    public String getValue(sessionInfo session,String wanted);
    public String setValue(sessionInfo session,String key,String value);
    public void addSetValue(sessionInfo session,String key,String value);
    public void addSetValues(sessionInfo session,String key,Vector<Object> values);
    public void setValueFromNode(sessionInfo session, MMObjectNode node );
    public String saveValue(sessionInfo session,String key);
    public void forgetSession(String key);
}
