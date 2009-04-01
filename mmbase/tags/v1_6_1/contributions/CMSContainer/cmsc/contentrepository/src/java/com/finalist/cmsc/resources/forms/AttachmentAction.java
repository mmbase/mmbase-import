package com.finalist.cmsc.resources.forms;

import org.mmbase.bridge.NodeManager;
import org.mmbase.bridge.NodeQuery;

/**
 * @author Billy
 *
 */
public class AttachmentAction extends SearchAction {

   public static final String TITLE_FIELD = "title";
   public static final String DESCRIPTION_FIELD = "description";
   public static final String FILENAME_FIELD = "filename";


   @Override
   protected void addConstraints(SearchForm searchForm, NodeManager nodeManager,
         QueryStringComposer queryStringComposer, NodeQuery query) {
      AttachmentForm form = (AttachmentForm) searchForm;
      addField(nodeManager, queryStringComposer, query, TITLE_FIELD, form.getTitle());
      addField(nodeManager, queryStringComposer, query, DESCRIPTION_FIELD, form.getDescription());
      addField(nodeManager, queryStringComposer, query, FILENAME_FIELD, form.getFilename());
   }

}
