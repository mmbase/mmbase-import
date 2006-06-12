/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package com.finalist.cmsc.taglib;

import java.util.List;

import com.finalist.cmsc.portalImpl.services.contentrepository.ContentRepository;

/**
 * List the available content channels
 * 
 * @author Wouter Heijke
 * @version $Revision: 1.1 $
 */
public class ListContentTypesTag extends AbstractListTag {

	protected List getList() {
		return ContentRepository.getContentTypes();
	}
}
