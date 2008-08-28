package com.finalist.cmsc.egemmail.forms;

import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("serial")
public class EgemExportForm extends EgemSearchForm {

   public static final String EXPORT = "export";
   public static final String SEARCH = "search";

   private String forward = SEARCH;
   private final Map<Integer, Boolean> selectedNodes = new HashMap<Integer, Boolean>();


   public String getForward() {
      return forward;
   }


   public Map<Integer, Boolean> getSelectedNodes() {
      return selectedNodes;
   }


   public void setForward(String action) {
      this.forward = action;
   }
}