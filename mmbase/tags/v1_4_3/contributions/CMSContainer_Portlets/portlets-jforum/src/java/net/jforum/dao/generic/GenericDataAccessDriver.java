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
 * This file creation date: Mar 3, 2003 / 2:19:47 PM
 * The JForum Project
 * http://www.jforum.net
 */
package net.jforum.dao.generic;

import net.jforum.dao.AttachmentDAO;
import net.jforum.dao.BannerDAO;
import net.jforum.dao.BookmarkDAO;
import net.jforum.dao.CategoryDAO;
import net.jforum.dao.ConfigDAO;
import net.jforum.dao.ForumDAO;
import net.jforum.dao.GroupDAO;
import net.jforum.dao.KarmaDAO;
import net.jforum.dao.ModerationDAO;
import net.jforum.dao.PollDAO;
import net.jforum.dao.PostDAO;
import net.jforum.dao.PrivateMessageDAO;
import net.jforum.dao.RankingDAO;
import net.jforum.dao.ScheduledSearchIndexerDAO;
import net.jforum.dao.SearchDAO;
import net.jforum.dao.SmilieDAO;
import net.jforum.dao.SummaryDAO;
import net.jforum.dao.TopicDAO;
import net.jforum.dao.TreeGroupDAO;
import net.jforum.dao.UserDAO;
import net.jforum.dao.UserSessionDAO;
import net.jforum.dao.generic.security.GenericGroupSecurityDAO;
import net.jforum.dao.security.GroupSecurityDAO;

/**
 * @author Rafael Steil
 * @version $Id: GenericDataAccessDriver.java,v 1.1 2008-01-17 08:04:50 mguo Exp $
 */
public class GenericDataAccessDriver extends net.jforum.dao.DataAccessDriver 
{
	private static GroupDAO groupDao = new GenericGroupDAO();
	private static PostDAO postDao = new GenericPostDAO();
	private static PollDAO pollDao = new GenericPollDAO();
	private static RankingDAO rankingDao = new GenericRankingDAO();
	private static TopicDAO topicDao = new GenericTopicDAO();
	private static UserDAO userDao = new GenericUserDAO();
	private static TreeGroupDAO treeGroupDao = new GenericTreeGroupDAO();
	private static SmilieDAO smilieDao = new GenericSmilieDAO();
	private static SearchDAO searchDao = new GenericSearchDAO();
	private static GroupSecurityDAO groupSecurityDao = new GenericGroupSecurityDAO();
	private static PrivateMessageDAO privateMessageDao = new GenericPrivateMessageDAO();
	private static UserSessionDAO userSessionDao = new GenericUserSessionDAO();
	private static KarmaDAO karmaDao = new GenericKarmaDAO();
	private static BookmarkDAO bookmarkDao = new GenericBookmarkDAO();
	private static AttachmentDAO attachmentDao = new GenericAttachmentDAO();
	private static ModerationDAO moderationDao = new GenericModerationDAO();
	private static ForumDAO forumDao = new GenericForumDAO();
	private static CategoryDAO categoryDao = new GenericCategoryDAO();
	private static ConfigDAO configDao = new GenericConfigDAO();
	private static ScheduledSearchIndexerDAO ssiDao = new GenericScheduledSearchIndexerDAO();
	private static BannerDAO bannerDao = new GenericBannerDAO();
    private static SummaryDAO summaryDao = new GenericSummaryDAO();

	/**
	 * @see net.jforum.dao.DataAccessDriver#getForumModel()
	 */
	public net.jforum.dao.ForumDAO newForumDAO() 
	{
		return forumDao;	
	}

	/**
	 * @see net.jforum.dao.DataAccessDriver#getGroupModel()
	 */
	public net.jforum.dao.GroupDAO newGroupDAO() 
	{
		return groupDao;
	}

	/**
	 * @see net.jforum.dao.DataAccessDriver#getPostModel()
	 */
	public net.jforum.dao.PostDAO newPostDAO() 
	{
		return postDao;
	}

	/**
	 * @see net.jforum.dao.DataAccessDriver#getPollModel()
	 */
	public net.jforum.dao.PollDAO newPollDAO() 
	{
		return pollDao;
	}

	/**
	 * @see net.jforum.dao.DataAccessDriver#getRankingModel()
	 */
	public net.jforum.dao.RankingDAO newRankingDAO() 
	{	
		return rankingDao;
	}

	/**
	 * @see net.jforum.dao.DataAccessDriver#getTopicModel()
	 */
	public net.jforum.dao.TopicDAO newTopicDAO() 
	{
		return topicDao;
	}

	/**
	 * @see net.jforum.dao.DataAccessDriver#getUserModel()
	 */
	public net.jforum.dao.UserDAO newUserDAO() 
	{
		return userDao;
	}

	/**
	 * @see net.jforum.dao.DataAccessDriver#newCategoryDAO()
	 */
	public net.jforum.dao.CategoryDAO newCategoryDAO() 
	{
		return categoryDao;
	}

	/**
	 * @see net.jforum.dao.DataAccessDriver#newTreeGroupDAO()
	 */
	public net.jforum.dao.TreeGroupDAO newTreeGroupDAO() 
	{
		return treeGroupDao;
	}
	
	/** 
	 * @see net.jforum.dao.DataAccessDriver#newSmilieDAO()
	 */
	public net.jforum.dao.SmilieDAO newSmilieDAO() 
	{
		return smilieDao;
	}
	
	/** 
	 * @see net.jforum.dao.DataAccessDriver#newSearchDAO()
	 */
	public net.jforum.dao.SearchDAO newSearchDAO() 
	{
		return searchDao;
	}
	
	/** 
	 * @see net.jforum.dao.DataAccessDriver#newSearchIndexerDAO()
	 */
	public net.jforum.dao.SearchIndexerDAO newSearchIndexerDAO() 
	{
		return new GenericSearchIndexerDAO();
	}
	
	/** 
	 * @see net.jforum.dao.DataAccessDriver#newGroupSecurityDAO()
	 */
	public net.jforum.dao.security.GroupSecurityDAO newGroupSecurityDAO() 
	{
		return groupSecurityDao;
	}

	/** 
	 * @see net.jforum.dao.DataAccessDriver#newPrivateMessageDAO()
	 */
	public net.jforum.dao.PrivateMessageDAO newPrivateMessageDAO() 
	{
		return privateMessageDao;
	}
	
	/** 
	 * @see net.jforum.dao.DataAccessDriver#newUserSessionDAO()
	 */
	public net.jforum.dao.UserSessionDAO newUserSessionDAO()
	{
		return userSessionDao;
	}
	
	/** 
	 * @see net.jforum.dao.DataAccessDriver#newConfigDAO()
	 */
	public net.jforum.dao.ConfigDAO newConfigDAO()
	{
		return configDao;
	}
	
	/** 
	 * @see net.jforum.dao.DataAccessDriver#newKarmaDAO()
	 */
	public net.jforum.dao.KarmaDAO newKarmaDAO()
	{
		return karmaDao;
	}
	
	/** 
	 * @see net.jforum.dao.DataAccessDriver#newBookmarkDAO()
	 */
	public net.jforum.dao.BookmarkDAO newBookmarkDAO()
	{
		return bookmarkDao;
	}
	
	/** 
	 * @see net.jforum.dao.DataAccessDriver#newAttachmentDAO()
	 */
	public net.jforum.dao.AttachmentDAO newAttachmentDAO()
	{
		return attachmentDao;
	}
	
	/** 
	 * @see net.jforum.dao.DataAccessDriver#newModerationDAO()
	 */
	public net.jforum.dao.ModerationDAO newModerationDAO()
	{
		return moderationDao;
	}
	
	/**
	 * @see net.jforum.dao.DataAccessDriver#newScheduledSearchIndexerDAO()
	 */
	public net.jforum.dao.ScheduledSearchIndexerDAO newScheduledSearchIndexerDAO()
	{
		return ssiDao;
	}

    /**
     * @see net.jforum.dao.DataAccessDriver#newBannerDAO()
     */
	public net.jforum.dao.BannerDAO newBannerDAO()
	{
		return bannerDao;
	}
    
    /**
     * @see net.jforum.dao.DataAccessDriver#newSummaryDAO()
     */
    public SummaryDAO newSummaryDAO()
    {
        return summaryDao;
    }
}
