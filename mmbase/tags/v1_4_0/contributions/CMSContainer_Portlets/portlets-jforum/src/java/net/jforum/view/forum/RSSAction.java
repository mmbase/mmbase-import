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
 * Created on 13/10/2004 23:47:06
 * The JForum Project
 * http://www.jforum.net
 */
package net.jforum.view.forum;

import java.util.List;

import javax.servlet.http.HttpServletResponse;

import net.jforum.ActionServletRequest;
import net.jforum.Command;
import net.jforum.JForumExecutionContext;
import net.jforum.SessionFacade;
import net.jforum.dao.DataAccessDriver;
import net.jforum.dao.PostDAO;
import net.jforum.dao.TopicDAO;
import net.jforum.dao.UserDAO;
import net.jforum.entities.Forum;
import net.jforum.entities.Topic;
import net.jforum.entities.User;
import net.jforum.repository.ForumRepository;
import net.jforum.util.I18n;
import net.jforum.util.preferences.ConfigKeys;
import net.jforum.util.preferences.SystemGlobals;
import net.jforum.util.preferences.TemplateKeys;
import net.jforum.util.rss.ForumRSS;
import net.jforum.util.rss.RSSAware;
import net.jforum.util.rss.RecentTopicsRSS;
import net.jforum.util.rss.TopicPostsRSS;
import net.jforum.util.rss.TopicRSS;
import net.jforum.util.rss.UserPostsRSS;
import net.jforum.util.rss.UserTopicsRSS;
import net.jforum.view.forum.common.ForumCommon;
import net.jforum.view.forum.common.TopicsCommon;
import freemarker.template.SimpleHash;
import freemarker.template.Template;

/**
 * @author Rafael Steil
 * @version $Id: RSSAction.java,v 1.1 2008-01-17 08:04:50 mguo Exp $
 */
public class RSSAction extends Command 
{
	/**
	 * RSS for all forums.
	 * Show rss syndication containing information about
	 * all available forums
	 * @throws Exception
	 */
	public void forums() throws Exception
	{
		String title = I18n.getMessage("RSS.Forums.title");
		String description = I18n.getMessage("RSS.Forums.description");
		
		RSSAware rss = new ForumRSS(title, description, ForumCommon.getAllCategoriesAndForums(false));
		this.context.put("rssContents", rss.createRSS());
	}
	
	/**
	 * RSS for all N first topics for some given forum
	 * @throws Exception
	 */
	public void forumTopics() throws Exception
	{
		int forumId = this.request.getIntParameter("forum_id"); 
		if (!TopicsCommon.isTopicAccessible(forumId)) {
			JForumExecutionContext.requestBasicAuthentication();
            return;
		}
		
		List topics = TopicsCommon.topicsByForum(forumId, 0);
		Forum forum = ForumRepository.getForum(forumId);
		
		String[] p = { forum.getName() };
		
		RSSAware rss = new TopicRSS(I18n.getMessage("RSS.ForumTopics.title", p),
				I18n.getMessage("RSS.ForumTopics.description", p),
				forumId, 
				topics);
		this.context.put("rssContents", rss.createRSS());
	}
	
	/**
	 * RSS for all N first posts for some given topic
	 * @throws Exception
	 */
	public void topicPosts() throws Exception
	{
		int topicId = this.request.getIntParameter("topic_id");

		PostDAO pm = DataAccessDriver.getInstance().newPostDAO();
		TopicDAO tm = DataAccessDriver.getInstance().newTopicDAO();
		
		Topic topic = tm.selectById(topicId);
		
		if (!TopicsCommon.isTopicAccessible(topic.getForumId()) || topic.getId() == 0) {
			JForumExecutionContext.requestBasicAuthentication(); 
            return;
		}
		
		tm.incrementTotalViews(topic.getId());
		
		List posts = pm.selectAllByTopic(topicId);
		
		String[] p = { topic.getTitle() };
		
		String title = I18n.getMessage("RSS.TopicPosts.title", p);
		String description = I18n.getMessage("RSS.TopicPosts.description", p);

		RSSAware rss = new TopicPostsRSS(title, description, topic.getForumId(), posts);
		this.context.put("rssContents", rss.createRSS());
	}
	
	public void recentTopics() throws Exception
	{
		String title = I18n.getMessage("RSS.RecentTopics.title");
		String description = I18n.getMessage("RSS.RecentTopics.description");

		RSSAware rss = new RecentTopicsRSS(title, description, 
				new RecentTopicsAction().topics());
		this.context.put("rssContents", rss.createRSS());
	}

	public void showTopicsByUser() throws Exception
	{
		int uid = this.request.getIntParameter("user_id");
		
		UserDAO um = DataAccessDriver.getInstance().newUserDAO();
		User u = um.selectById(uid);
		
		if (u == null || u.getId() == 0) {
			return;
		}
		
		List topics = DataAccessDriver.getInstance().newTopicDAO().selectByUserByLimit(u.getId(), 
				0, 
				SystemGlobals.getIntValue(ConfigKeys.TOPICS_PER_PAGE));
		
		String[] p = { u.getUsername() };
		
		String title = I18n.getMessage("RSS.TopicsByUser.title", p);
		String description = I18n.getMessage("RSS.TopicsByUser.description", p);
		
		RSSAware rss = new UserTopicsRSS(title, description, u.getId(), topics);
		this.context.put("rssContents", rss.createRSS());
	}
	
	public void showPostsByUser() throws Exception
	{
		int uid = this.request.getIntParameter("user_id");
		
		UserDAO um = DataAccessDriver.getInstance().newUserDAO();
		User u = um.selectById(uid);
		
		if (u == null || u.getId() == 0) {
			return;
		}
		
		List topics = DataAccessDriver.getInstance().newPostDAO().selectByUserByLimit(u.getId(), 
				0, 
				SystemGlobals.getIntValue(ConfigKeys.POST_PER_PAGE));
		
		String[] p = { u.getUsername() };
		
		String title = I18n.getMessage("RSS.PostsByPosts.title", p);
		String description = I18n.getMessage("RSS.PostsByPosts.description", p);
		
		RSSAware rss = new UserPostsRSS(title, description, u.getId(), topics);
		this.context.put("rssContents", rss.createRSS());
	}
	
	/** 
	 * @see net.jforum.Command#list()
	 */
	public void list() throws Exception 
	{
		this.forums();
	}
	
	/** 
	 * @see net.jforum.Command#process()
	 */
	public Template process(ActionServletRequest request, 
			HttpServletResponse response, 
			SimpleHash context) throws Exception 
	{
        if (!SessionFacade.isLogged() && UserAction.hasBasicAuthentication(request)) {
            new UserAction().validateLogin(request);
            JForumExecutionContext.setRedirect(null);
        }

        JForumExecutionContext.setContentType("text/xml");
		super.setTemplateName(TemplateKeys.RSS);

		return super.process(request, response, context);
	}

}
