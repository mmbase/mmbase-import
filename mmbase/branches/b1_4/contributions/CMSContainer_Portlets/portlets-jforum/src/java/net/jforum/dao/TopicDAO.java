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
 * This file creating date: Feb 23, 2003 / 2:56:58 PM
 * The JForum Project
 * http://www.jforum.net
 */
package net.jforum.dao;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import net.jforum.entities.Topic;

/**
* Model interface for {@link net.jforum.Topic}.
 * This interface defines methods which are expected to be
 * implementd by a specific data access driver. The intention is
 * to provide all functionality needed to update, insert, delete and
 * select some specific data.
 *
 * @author Rafael Steil
 * @version $Id: TopicDAO.java,v 1.1 2008-01-17 08:04:51 mguo Exp $
 */
public interface TopicDAO 
{
	/**
	 * Fixes the fields <i>topic_first_post_id</i> and 
	 * <i>topic_last_post_id</i>.
	 * 
	 * @param topicId The topic id to fix
	 * @throws Exception
	 */
	public void fixFirstLastPostId(int topicId) throws Exception;
	
	/**
	 * Gets a specific <code>Topic</code>.
	 * 
	 * @param topicId The Topic ID to search
	 * @return <code>Topic</code>object containing all the information
	 * @throws Exception
	 * @see #selectAll
	 */
	public Topic selectById(int topicId) throws Exception;
	
	/**
	 * Gets a topic's information from the topics table only.
	 * No other information, like usernames, are fetched. 
	 * 
	 * @param topicId The topic id to get
	 * @return A topic instance
	 * @throws Exception
	 */
	public Topic selectRaw(int topicId) throws Exception;
	
	/**
	 * Selects all topics associated to a specific forum
	 * 
	 * @param forumId The forum id to select the topics
	 * @return <code>ArrayList</code> with all topics found. Each entry is a <code>net.jforum.Topic</code> object
	 * @throws Exception
	 */
	public List selectAllByForum(int forumId) throws Exception;
	
	public List selectTopicTitlesByIds(Collection idList) throws Exception;
	
	/**
	 * Selects all topics associated to a specific forum, limiting the total number
	 * of records returned.
	 * 
	 * @param forumId The forum id to select the topics
	 * @return <code>ArrayList</code> with all topics found. Each entry is a <code>net.jforum.Topic</code> object
	 * @throws Exception
	 */
	public List selectAllByForumByLimit(int forumId, int startFrom, int count) throws Exception;

       /**
	 * Selects all topics associated to a specific user and belonging to 
	 * given forums
	 * @param userId User ID.
	 * @throws Exception
	 */
	public List selectByUserByLimit(int userId,int startFrom, int count) throws Exception;

	/**
	 * How many topics were created by a given user
	 * @param userId the user id to check
	 * @return the number of topics created by the user
	 * @throws Exception
	 */
	public int countUserTopics(int userId) throws Exception;

	/**
	 * Selects the last <code>count</code> topics postted. 
	 * 
	 * @param count The desired total to retrieve
	 * @return <code>ArrayList</code> with all topics found. Each entry is a <code>net.jforum.Topic</code> object
	 * @throws Exception
	 */
	public List selectLastN(int count) throws Exception;
	
	/**
	 * Delete a Topic.
	 * 
	 * @param topicId The Topic ID to delete
	 * @return The total of related posts removed
	 * @throws Exception
	 * @see #canDelete(int)
	 */
	public void delete(Topic topic) throws Exception;
	
	/**
	 * Deletes a set of topics
	 * @param topics The topics to delete. Each entry must be
	 * an instance of net.jforum.entities.Topic
	 * @throws Exception
	 */
	public void deleteTopics(List topics) throws Exception;
	
	/**
	 * Deletes all topics from a forum
	 * @param forumId
	 * @throws Exception
	 */
	public void deleteByForum(int forumId) throws Exception;
	
	/**
	 * Updates a Topic.
	 * 
	 * @param topic Reference to a <code>Topic</code> object to update
	 * @throws Exception
	 * @see #update(int)
	 */
	public void update(Topic topic) throws Exception;
	
	/**
	 * Adds a new Topic.
	 * 
	 * @param topic Reference to a valid and configured <code>Topic</code> object
	 * @return The new ID
	 * @throws Exception
	 */
	public int addNew(Topic topic) throws Exception;
	
	/**
	 * Increments the number of times the topic was saw
	 * 
	 * @param topicId The topic ID to increment the total number of views
	 * @throws Exception
	 */
	public void incrementTotalViews(int topicId) throws Exception;
	
	/**
	 * Increments the number of replies the topic has
	 * 
	 * @param topicId The topic ID to increment the total number of replies
	 * @throws Exception
	 */
	public void incrementTotalReplies(int topicId) throws Exception;

	/**
	 * Decrements the number of replies the topic has
	 * 
	 * @param topicId The topic ID to decrement the total number of replies
	 * @throws Exception
	 */
	public void decrementTotalReplies(int topicId) throws Exception;
	
	/**
	 * Sets the ID of the last post of the topic
	 * 
	 * @param topicId Topic ID
	 * @param postId Post ID
	 * @throws Exception
	 */
	public void setLastPostId(int topicId, int postId) throws Exception;
	
	/**
	 * Gets the last post id associated to the topic
	 * 
	 * @param topicId The topic id
	 * @throws Exception
	 */
	public int getMaxPostId(int topicId) throws Exception;
	
	/**
	 * Gets the number of posts the topic has.
	 * 
	 * @param topicId The topic id
	 * @return The number of posts
	 * @throws Exception
	 */
	public int getTotalPosts(int topicId) throws Exception;
	
	/**
	 * Get the users to notify
	 * 
	 * @param topic The topic 
	 * @return <code>ArrayList</code> of <code>User</code> objects. Each
	 * entry is an user who will receive the topic anwser notification
	 * @throws Exception
	 * */
	public List notifyUsers(Topic topic) throws Exception;
	
	/**
	 * Subscribe the user for notification of new post on the topic
	 *  
	 * @param topicId The topic id
	 * @param userId The user id
	 * @throws Exception
	 */	
	public void subscribeUser(int topicId, int userId) throws Exception;
	
	/**
	 * Return the subscrition status of the user on the topic.
	 * 
	 * @param topicId The topic id
	 * @param userId The user id
	 * @return true if the user is waiting notification on the topic
	 * @throws Exception
	 */	
	public boolean isUserSubscribed(int topicId, int userId) throws Exception;
	
	/**
	 * Remove the user's subscription of the topic
	 * 
	 * @param topicId The topic id
	 * @param userId the User id
	 * @throws Exception
	 */
	public void removeSubscription(int topicId, int userId) throws Exception;
	
	/**
	 * Clean all subscriptions of some topic
	 * 
	 * @param topicId The topic id
	 * @throws Exception
	 */
	public void removeSubscriptionByTopic(int topicId) throws Exception;
	
	/**
	 * Change the topic read status 
	 * 
	 * @param topicId The topic id
	 * @param userId The user id
	 * @param read <code>true</code> or <code>false</code>
	 * @throws Exception
	 */
	public void updateReadStatus(int topicId, int userId, boolean read) throws Exception;
	
	/**
	 * Lock or unlock a topic. 
	 * 
	 * @param topicId The topic id to perform the action on
	 * @param status Use <code>Topic.STATUS_LOCKED</code> to lock the topic, or
	 * <code>Topic.STATUS_UNLOCKED</code> to unlock. 
	 * @throws Exception
	 */
	public void lockUnlock(int[] topicId, int status) throws Exception;

	/**
	 * Selects recent topics 
	 *
	 * @param limit The number of topics to retrieve
	 * @throws Exception
	 */	
	public List selectRecentTopics (int limit) throws Exception;
	
	/**
	 * Sets the ID of the first post of the topic
	 * 
	 * @param topicId Topic ID
	 * @param postId Post ID
	 * @throws Exception
	 */
	public void setFirstPostId(int topicId, int postId) throws Exception;

	/**
	 * Gets the first post id associated to the topic
	 * 
	 * @param topicId The topic id
	 * @throws Exception
	 */
	public int getMinPostId(int topicId) throws Exception;
	
	/**
	 * Sets the moderatation flag for all topics of a given forum.
	 * 
	 * @param forumId The forum id
	 * @param status
	 * @throws Exception
	 */
	public void setModerationStatus(int forumId, boolean status) throws Exception;

	/**
	 * Sets the moderatation flag for a given topic.
	 * 
	 * @param forumId The topic id
	 * @param status
	 * @throws Exception
	 */
	public void setModerationStatusByTopic(int topicId, boolean status) throws Exception;

	/**
	 * Get all unique posters of some topic
	 * @param topicId
	 * @return A Map instance with all topic posts. Key is the userid, 
	 * value is an {@link net.jforum.entities.User} instance with minimum
	 * data filled
	 * @throws Exception
	 */
	public Map topicPosters(int topicId) throws Exception;
}
