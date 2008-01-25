/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package com.finalist.cmsc.services.community.preferences;

import java.io.Serializable;
import java.util.Map;


public interface PreferenceService {

    Map<String, Serializable> getPreferences(String module, String userName, String key, String value);
    
    Map<String, Serializable> createPreferences(String module, String userName, String key, String value);
    
    
}
