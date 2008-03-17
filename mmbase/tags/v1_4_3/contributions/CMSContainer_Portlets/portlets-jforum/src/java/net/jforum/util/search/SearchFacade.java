/*
 * Copyright (c) Rafael Steil
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, 
 * with or without modification, are permitted provided 
 * that the following conditions are met:
 * 
 * 1) Redistributions of source code must retain the above 
 * copyright notice, this list of conditions and the 
 * following  disclaimer.
 * 2)  Redistributions in binary form must reproduce the 
 * above copyright notice, this list of conditions and 
 * the following disclaimer in the documentation and/or 
 * other materials provided with the distribution.
 * 3) Neither the name of "Rafael Steil" nor 
 * the names of its contributors may be used to endorse 
 * or promote products derived from this software without 
 * specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT 
 * HOLDERS AND CONTRIBUTORS "AS IS" AND ANY 
 * EXPRESS OR IMPLIED WARRANTIES, INCLUDING, 
 * BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF 
 * MERCHANTABILITY AND FITNESS FOR A PARTICULAR 
 * PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL 
 * THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE 
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, 
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES 
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF 
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, 
 * OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER 
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER 
 * IN CONTRACT, STRICT LIABILITY, OR TORT 
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN 
 * ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF 
 * ADVISED OF THE POSSIBILITY OF SUCH DAMAGE
 * 
 * Created on Mar 11, 2005 12:01:47 PM
 * The JForum Project
 * http://www.jforum.net
 */
package net.jforum.util.search;

import net.jforum.entities.Post;
import net.jforum.exceptions.SearchInstantiationException;
import net.jforum.util.preferences.ConfigKeys;
import net.jforum.util.preferences.SystemGlobals;

import org.apache.log4j.Logger;

/**
 * @author Rafael Steil
 * @version $Id: SearchFacade.java,v 1.1 2008-01-17 08:04:52 mguo Exp $
 */
public class SearchFacade
{
	private static SearchManager searchManager;
	private static Logger logger = Logger.getLogger(SearchFacade.class);
	
	public static void init()
	{
		if (!SystemGlobals.getBoolValue(ConfigKeys.SEARCH_INDEXING_ENABLED)) {
			logger.info("Search indexation is disabled. 'Will try to create a SearchManager "
					+ "instance for runtime configuration changes");
		}
		
		String clazz = SystemGlobals.getValue(ConfigKeys.SEARCH_INDEXER_IMPLEMENTATION);
		
		if (clazz != null && !"".equals(clazz)) {
			try {
				searchManager = (SearchManager)Class.forName(clazz).newInstance();
			}
			catch (Exception e) {
				e.printStackTrace();
				throw new SearchInstantiationException("Error while tring to start the search manager: " + e);
			}
			
			searchManager.init();
		}
		else {
			logger.info(ConfigKeys.SEARCH_INDEXER_IMPLEMENTATION + " is not defined. Skipping.");
		}
	}
	
	public static void index(Post post)
	{
		if (SystemGlobals.getBoolValue(ConfigKeys.SEARCH_INDEXING_ENABLED)) {
			searchManager.index(post);
		}
	}
}
