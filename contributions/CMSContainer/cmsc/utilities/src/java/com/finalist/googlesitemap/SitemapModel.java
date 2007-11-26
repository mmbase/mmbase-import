/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

 */
package com.finalist.googlesitemap;

import java.io.File;
import java.util.Date;
import java.util.List;

public interface SitemapModel {

   Object getRoot();


   List<File> getChildren(Object root);


   boolean isUrl(Object root);


   String getLocation(Object root);


   Date getLastModified(Object root);


   String getChangeFrequency(Object root);

}
