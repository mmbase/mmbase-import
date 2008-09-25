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
 * Created on 13/11/2004 11:49:26
 * The JForum Project
 * http://www.jforum.net
 */
package net.jforum.repository;

import java.util.List;

import net.jforum.dao.ForumDAO;
import net.jforum.entities.Forum;
import net.jforum.entities.ForumStats;
import net.jforum.entities.LastPostInfo;

/**
 * @author Rafael Steil
 * @version $Id: ForumModelDummy.java,v 1.1 2008-01-17 07:48:25 mguo Exp $
 */
class ForumModelDummy implements ForumDAO {
	
	public List getModeratorList(int forumId) throws Exception
	{
		// TODO Auto-generated method stub
		return null;
	}

	/** 
	 * @see net.jforum.dao.ForumDAO#selectById(int)
	 */
	public Forum selectById(int forumId) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}
	
	public ForumStats getBoardStatus() throws Exception
	{
		// TODO Auto-generated method stub
		return null;
	}

	/** 
	 * @see net.jforum.dao.ForumDAO#selectAll()
	 */
	public List selectAll() throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	/** 
	 * @see net.jforum.dao.ForumDAO#setOrderUp(int)
	 */
	public Forum setOrderUp(Forum f, Forum r) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	/** 
	 * @see net.jforum.dao.ForumDAO#setOrderDown(int)
	 */
	public Forum setOrderDown(Forum f, Forum r) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	/** 
	 * @see net.jforum.dao.ForumDAO#delete(int)
	 */
	public void delete(int forumId) throws Exception {
		// TODO Auto-generated method stub

	}

	/** 
	 * @see net.jforum.dao.ForumDAO#update(net.jforum.entities.Forum)
	 */
	public void update(Forum forum) throws Exception {
		// TODO Auto-generated method stub

	}

	/** 
	 * @see net.jforum.dao.ForumDAO#addNew(net.jforum.entities.Forum)
	 */
	public int addNew(Forum forum) throws Exception {
		// TODO Auto-generated method stub
		return 0;
	}

	/** 
	 * @see net.jforum.dao.ForumDAO#setLastPost(int, int)
	 */
	public void setLastPost(int forumId, int postId) throws Exception {
		// TODO Auto-generated method stub

	}

	/** 
	 * @see net.jforum.dao.ForumDAO#incrementTotalTopics(int, int)
	 */
	public void incrementTotalTopics(int forumId, int count) throws Exception {
		// TODO Auto-generated method stub

	}

	/** 
	 * @see net.jforum.dao.ForumDAO#decrementTotalTopics(int, int)
	 */
	public void decrementTotalTopics(int forumId, int count) throws Exception {
		// TODO Auto-generated method stub

	}

	/** 
	 * @see net.jforum.dao.ForumDAO#getLastPostInfo(int)
	 */
	public LastPostInfo getLastPostInfo(int forumId) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	/** 
	 * @see net.jforum.dao.ForumDAO#getTotalMessages()
	 */
	public int getTotalMessages() throws Exception {
		// TODO Auto-generated method stub
		return 0;
	}

	/** 
	 * @see net.jforum.dao.ForumDAO#getTotalTopics(int)
	 */
	public int getTotalTopics(int forumId) throws Exception {
		// TODO Auto-generated method stub
		return 0;
	}

	/** 
	 * @see net.jforum.dao.ForumDAO#getMaxPostId(int)
	 */
	public int getMaxPostId(int forumId) throws Exception {
		// TODO Auto-generated method stub
		return 0;
	}

	/** 
	 * @see net.jforum.dao.ForumDAO#moveTopics(java.lang.String[], int, int)
	 */
	public void moveTopics(String[] topics, int fromForumId, int toForumId) throws Exception {
		// TODO Auto-generated method stub

	}

	/**
	 * @see net.jforum.dao.ForumDAO#checkUnreadTopics(int, long)
	 */
	public List checkUnreadTopics(int forumId, long lastVisit) throws Exception
	{
		// TODO Auto-generated method stub
		return null;
	}
	
	/**
	 * @see net.jforum.dao.ForumDAO#setModerated(int, boolean)
	 */
	public void setModerated(int categoryId, boolean status) throws Exception
	{
		// TODO Auto-generated method stub

	}

	// codes below are added by socialnetwork@gmail.com for "watch forum" purpose
	/**
	 * @see net.jforum.dao.ForumDAO#notifyUsers(int, boolean)
	 */
	public List notifyUsers(Forum forum) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	public void subscribeUser(int forumId, int userId) throws Exception {
		// TODO Auto-generated method stub
		
	}

	public boolean isUserSubscribed(int forumId, int userId) throws Exception {
		// TODO Auto-generated method stub
		return false;
	}

	public void removeSubscription(int forumId, int userId) throws Exception {
		// TODO Auto-generated method stub
		
	}

	public void removeSubscriptionByForum(int forumId) throws Exception {
		// TODO Auto-generated method stub
		
	}
	
}
