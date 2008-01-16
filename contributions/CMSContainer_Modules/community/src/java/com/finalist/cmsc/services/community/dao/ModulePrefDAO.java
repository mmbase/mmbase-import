package com.finalist.cmsc.services.community.dao;

import java.util.List;
import java.util.Map;

import com.finalist.cmsc.services.community.data.ModulePref;

public interface ModulePrefDAO extends DAO<ModulePref>{
   
   public abstract Map<String, Map<String,List<String>>> getPreferences(String module, String userId, String key, String value);
}
