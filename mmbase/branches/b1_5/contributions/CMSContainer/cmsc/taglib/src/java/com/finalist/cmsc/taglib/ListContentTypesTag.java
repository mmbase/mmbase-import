/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

 */
package com.finalist.cmsc.taglib;

import java.util.List;

import com.finalist.cmsc.beans.NodetypeBean;
import com.finalist.cmsc.services.contentrepository.ContentRepository;

/**
 * List the available content channels
 *
 * @author Wouter Heijke
 */
public class ListContentTypesTag extends AbstractListTag<NodetypeBean> {

   @Override
   protected List<NodetypeBean> getList() {
      return ContentRepository.getContentTypes();
   }
}
