package com.finalist.cmsc.resources.forms;

import org.mmbase.bridge.NodeManager;
import org.mmbase.bridge.NodeQuery;

public class UrlAction extends SearchAction {

   public static final String NAME_FIELD = "title";
   public static final String DESCRIPTION_FIELD = "description";
   public static final String URL_FIELD = "url";
   public static final String VALID_FIELD = "valid";


   @Override
   protected void addConstraints(SearchForm searchForm, NodeManager nodeManager,
         QueryStringComposer queryStringComposer, NodeQuery query) {
      UrlForm form = (UrlForm) searchForm;
      addField(nodeManager, queryStringComposer, query, NAME_FIELD, form.getName());
      addField(nodeManager, queryStringComposer, query, DESCRIPTION_FIELD, form.getDescription());
      addField(nodeManager, queryStringComposer, query, URL_FIELD, form.getUrl());
      addField(nodeManager, queryStringComposer, query, VALID_FIELD, form.getValid());
   }

}
