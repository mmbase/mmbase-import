/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.storage.search;

import java.util.List;

import org.mmbase.cache.Cacheable;
/**

 * @author Michiel Meeuwissen
 * @since MMBase-1.9.2
 */
public interface SearchQueryWrapper  {


    SearchQuery unwrap();



}
