package com.finalist.cmsc.community;

import java.util.ArrayList;
import java.util.List;

public class CommunityManager {
   private static List<CommunityListener> listeners = new ArrayList<CommunityListener>();

   private CommunityManager() {
      // Access object for  managers
   }
   
   public static void notify(Long auId) {
      for(CommunityListener listener : listeners) {
         listener.notify(auId);
      }
   }
   public static void registerListener(CommunityListener listener) {
      listeners.add(listener);
  }
}
