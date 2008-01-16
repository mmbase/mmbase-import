package com.finalist.cmsc.services.community.dao;

import java.util.List;
import java.util.Map;

public interface DAO<T> {
   public abstract List<String> getObject(Map<String, String> preferences);
   public abstract List<String> insertByObject(T t) throws Exception;
   public abstract boolean deleteByCriteria(String module, String userId, String key);
}
