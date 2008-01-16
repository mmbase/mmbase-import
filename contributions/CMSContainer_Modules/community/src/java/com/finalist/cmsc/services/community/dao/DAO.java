package com.finalist.cmsc.services.community.dao;

import java.util.List;
import java.util.Map;

/**
 * DAO, this is a generic hibernate DAO/transaction interface.
 * This interface is implemented by the implementation classes.
 * This is the genericDAO interface. It contains the methods
 * being executed by the genericDAO class.
 * 
 * @author menno menninga
 */
public interface DAO<T> {
   public abstract List<String> getObject(Map<String, String> preferences);
   public abstract List<String> insertByObject(T t) throws Exception;
   public abstract boolean deleteByCriteria(String module, String userId, String key);
}
