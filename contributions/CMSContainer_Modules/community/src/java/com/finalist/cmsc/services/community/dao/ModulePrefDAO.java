package com.finalist.cmsc.services.community.dao;

import java.util.List;
import java.util.Map;

import com.finalist.cmsc.services.community.data.ModulePref;

/**
 * ModulePrefDAO, this is a hibernate DAO/transaction interface.
 * This interface is implemented by the implementation classes.
 * it contains the methods for transactions to the database
 * 
 * @author menno menninga
 */
public interface ModulePrefDAO extends DAO<ModulePref>{
   
   public abstract Map<String, Map<String,List<String>>> getPreferences(String module, String userId, String key, String value);
}
