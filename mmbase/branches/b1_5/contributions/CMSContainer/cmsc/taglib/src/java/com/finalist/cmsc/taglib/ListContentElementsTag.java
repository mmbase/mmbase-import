/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

 */
package com.finalist.cmsc.taglib;

import java.util.List;

import com.finalist.cmsc.beans.om.ContentChannel;
import com.finalist.cmsc.beans.om.ContentElement;
import com.finalist.cmsc.services.contentrepository.ContentRepository;

/**
 * List the available content elements
 * 
 * @author Wouter Heijke
 */
public class ListContentElementsTag extends AbstractListTag<ContentElement> {

   @Override
   protected List<ContentElement> getList() {
      if (origin != null) {
         if (origin instanceof ContentChannel) {
            return ContentRepository.getContentElements((ContentChannel) origin);
         }
         else if (origin instanceof String) {
            return ContentRepository.getContentElements((String) origin);
         }
      }
      return null;
   }
}
