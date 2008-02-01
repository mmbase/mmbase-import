/*
 * Copyright (c) 2003, Rafael Steil
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
 * This file creation date: 31/01/2004 - 19:22:42
 * The JForum Project
 * http://www.jforum.net
 */
package net.jforum.util.rss;


import java.util.Iterator;
import java.util.List;

import net.jforum.entities.Category;
import net.jforum.entities.Forum;
import net.jforum.entities.LastPostInfo;
import net.jforum.repository.ForumRepository;
import net.jforum.util.I18n;
import net.jforum.util.preferences.ConfigKeys;
import net.jforum.util.preferences.SystemGlobals;
import net.jforum.view.forum.common.ViewCommon;

import org.apache.log4j.Logger;

/**
 * @author Rafael Steil
 * @version $Id: ForumRSS.java,v 1.1 2008-01-17 07:48:24 mguo Exp $
 */
public class ForumRSS extends GenericRSS 
{
	private static final Logger logger = Logger.getLogger(ForumRSS.class);
	
	private List categories;
	private RSS rss;
	private String forumLink;
	
	public ForumRSS(String title, String description, List categories)
	{
		this.categories = categories;
		this.forumLink = ViewCommon.getForumLink();
		
		this.rss = new RSS(title, description, 
				SystemGlobals.getValue(ConfigKeys.ENCODING ),
				this.forumLink);
		
		this.prepareRSS();
	}
	
	private void prepareRSS()
	{
		try {
			for (Iterator iter = this.categories.iterator(); iter.hasNext(); ) {
				Category category = (Category)iter.next();
				
				for (Iterator fIter = category.getForums().iterator(); fIter.hasNext(); ) {
					Forum forum = (Forum)fIter.next();
					
					LastPostInfo info = ForumRepository.getLastPostInfo(forum.getId());
					
					RSSItem item = new RSSItem();
					item.addCategory(category.getName());
					item.setTitle(forum.getName());
					item.setDescription(forum.getDescription());
					item.setContentType(RSSAware.CONTENT_HTML);
					item.setLink(this.forumLink
							+ "forums/show/" + forum.getId()
							+ SystemGlobals.getValue(ConfigKeys.SERVLET_EXTENSION));
					
					String author = info.getUsername();
					
					item.setAuthor(author != null ? author : I18n.getMessage("Guest"));
					
					String date = info.getPostDate();
					item.setPublishDate(date != null ? RSSUtils.formatDate(date) : "");
					
					this.rss.addItem(item);
				}
			}
		}
		catch (Exception e) {
			logger.warn("Error while generating rss for forums: " + e);
			e.printStackTrace();
		}
		
		super.setRSS(this.rss);
	}
}
