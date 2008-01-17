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
 * This file creation date: Apr 24, 2003 / 10:15:07 PM
 * The JForum Project
 * http://www.jforum.net
 */
package net.jforum.view.forum;

import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.jforum.Command;
import net.jforum.JForumExecutionContext;
import net.jforum.SessionFacade;
import net.jforum.dao.DataAccessDriver;
import net.jforum.dao.ForumDAO;
import net.jforum.dao.ModerationDAO;
import net.jforum.dao.SearchData;
import net.jforum.entities.Forum;
import net.jforum.entities.MostUsersEverOnline;
import net.jforum.entities.Topic;
import net.jforum.entities.UserSession;
import net.jforum.repository.ForumRepository;
import net.jforum.repository.SecurityRepository;
import net.jforum.security.SecurityConstants;
import net.jforum.util.I18n;
import net.jforum.util.preferences.ConfigKeys;
import net.jforum.util.preferences.SystemGlobals;
import net.jforum.util.preferences.TemplateKeys;
import net.jforum.view.admin.ModerationAction;
import net.jforum.view.forum.common.ForumCommon;
import net.jforum.view.forum.common.PostCommon;
import net.jforum.view.forum.common.TopicsCommon;
import net.jforum.view.forum.common.ViewCommon;
/**
 * @author Rafael Steil
 * @version $Id: ForumAction.java,v 1.1 2008-01-17 07:48:24 mguo Exp $
 */
public class ForumAction extends Command 
{
	/**
	 * List all the forums (first page of forum index)?
	 */
	public void list() throws Exception
	{
		this.setTemplateName(TemplateKeys.FORUMS_LIST);
		
		this.context.put("allCategories", ForumCommon.getAllCategoriesAndForums(true));
		this.context.put("topicsPerPage",  new Integer(SystemGlobals.getIntValue(ConfigKeys.TOPICS_PER_PAGE)));
		this.context.put("rssEnabled", SystemGlobals.getBoolValue(ConfigKeys.RSS_ENABLED));
		
		this.context.put("totalMessages", I18n.getMessage("ForumListing.totalMessagesInfo", 
						new Object[] {new Integer( ForumRepository.getTotalMessages() )}));
		
		this.context.put("totalUsers", I18n.getMessage("ForumListing.registeredUsers", 
				new Object[] { ForumRepository.totalUsers() }));
		this.context.put("lastUser", ForumRepository.lastRegisteredUser());
		
		SimpleDateFormat df = new SimpleDateFormat(SystemGlobals.getValue(ConfigKeys.DATE_TIME_FORMAT));
		GregorianCalendar gc = new GregorianCalendar();
		this.context.put("now", df.format(gc.getTime()));
		
		this.context.put("lastVisit", df.format(SessionFacade.getUserSession().getLastVisit()));
		this.context.put("fir", new ForumRepository());
		
		// Online Users
		this.context.put("totalOnlineUsers", new Integer(SessionFacade.size()));
		int aid = SystemGlobals.getIntValue(ConfigKeys.ANONYMOUS_USER_ID);
	
		List onlineUsersList = SessionFacade.getLoggedSessions();
		
		// Check for an optional language parameter
		UserSession currentUser = SessionFacade.getUserSession();
		
		if (currentUser.getUserId() == aid) {
			String lang = this.request.getParameter("lang");
			
			if (lang != null && I18n.languageExists(lang)) {
				currentUser.setLang(lang);
			}
		}

		// If there are only guest users, then just register
		// a single one. In any other situation, we do not
		// show the "guest" username
		if (onlineUsersList.size() == 0) {
			UserSession us = new UserSession();
			
			us.setUserId(aid);
			us.setUsername(I18n.getMessage("Guest"));
			
			onlineUsersList.add(us);
		}
		
		int registeredSize = SessionFacade.registeredSize();
		int anonymousSize = SessionFacade.anonymousSize();
		int totalUsers = registeredSize + anonymousSize;
		
		this.context.put("userSessions", onlineUsersList);
		this.context.put("usersOnline", I18n.getMessage("ForumListing.numberOfUsersOnline", 
			new Object[] {
					   new Integer(totalUsers),
					   new Integer(registeredSize),
					   new Integer(anonymousSize)
			}));
		
		// Most users ever online
		MostUsersEverOnline mostUsersEverOnline = ForumRepository.getMostUsersEverOnline();
		
		if (totalUsers > mostUsersEverOnline.getTotal()) {
			mostUsersEverOnline.setTotal(totalUsers);
			mostUsersEverOnline.setTimeInMillis(System.currentTimeMillis());

			ForumRepository.updateMostUsersEverOnline(mostUsersEverOnline);
		}
		
		this.context.put("mostUsersEverOnline", I18n.getMessage("ForumListing.mostUsersEverOnline",
				new String[] { Integer.toString(mostUsersEverOnline.getTotal()), mostUsersEverOnline.getDate() }));
	}
	
	public void moderation() throws Exception
	{
		this.context.put("openModeration", true);
		this.show();
	}

	/**
	 * Display all topics in a forum
	 * @throws Exception
	 */
	public void show() throws Exception
	{
		int forumId = this.request.getIntParameter("forum_id");
		ForumDAO fm = DataAccessDriver.getInstance().newForumDAO();
		
		// The user can access this forum?
		Forum forum = ForumRepository.getForum(forumId);
		
		if (forum == null || !ForumRepository.isCategoryAccessible(forum.getCategoryId())) {
			new ModerationHelper().denied(I18n.getMessage("ForumListing.denied"));
			return;
		}
		
		int start = ViewCommon.getStartPage();
		
		List tmpTopics = TopicsCommon.topicsByForum(forumId, start);
		
		this.setTemplateName(TemplateKeys.FORUMS_SHOW);
		
		// Moderation
		boolean canApproveMessages = (SessionFacade.isLogged() 
			&& SessionFacade.getUserSession().isModerator(forumId)
			&& SecurityRepository.canAccess(SecurityConstants.PERM_MODERATION_APPROVE_MESSAGES));
		
		Map topicsToApprove = new HashMap();
		
		if (canApproveMessages) {
			ModerationDAO mdao = DataAccessDriver.getInstance().newModerationDAO();
			topicsToApprove = mdao.topicsByForum(forumId);
			this.context.put("postFormatter", PostCommon.getInstance());
		}

		this.context.put("topicsToApprove", topicsToApprove);
		
		this.context.put("attachmentsEnabled", SecurityRepository.canAccess(SecurityConstants.PERM_ATTACHMENTS_ENABLED,
				Integer.toString(forumId)) 
				|| SecurityRepository.canAccess(SecurityConstants.PERM_ATTACHMENTS_DOWNLOAD));
		
		this.context.put("topics", TopicsCommon.prepareTopics(tmpTopics));
		this.context.put("allCategories", ForumCommon.getAllCategoriesAndForums(false));
		this.context.put("forum", forum);
		this.context.put("rssEnabled", SystemGlobals.getBoolValue(ConfigKeys.RSS_ENABLED));
		this.context.put("pageTitle", forum.getName());
		this.context.put("canApproveMessages", canApproveMessages);
		this.context.put("replyOnly", !SecurityRepository.canAccess(SecurityConstants.PERM_REPLY_ONLY, 
				Integer.toString(forum.getId())));

		this.context.put("readonly", !SecurityRepository.canAccess(SecurityConstants.PERM_READ_ONLY_FORUMS, 
				Integer.toString(forumId)));
		
		this.context.put("watching", fm.isUserSubscribed(forumId, SessionFacade.getUserSession().getUserId()));
		
		// Pagination
		int topicsPerPage = SystemGlobals.getIntValue(ConfigKeys.TOPICS_PER_PAGE);
		int postsPerPage = SystemGlobals.getIntValue(ConfigKeys.POST_PER_PAGE);
		int totalTopics = ForumRepository.getTotalTopics(forumId);
		
		ViewCommon.contextToPagination(start, totalTopics, topicsPerPage);
		this.context.put("postsPerPage", new Integer(postsPerPage));

		TopicsCommon.topicListingBase();
	}

	// Make an URL to some action
	private String makeRedirect(String action)
	{
		String path = this.request.getContextPath() + "/forums/" + action + "/";
		String thisPage = this.request.getParameter("start");
		
		if (thisPage != null && !thisPage.equals("0")) {
			path += thisPage + "/";
		}
		
		String forumId = this.request.getParameter("forum_id");
		
		if (forumId == null) {
			forumId = this.request.getParameter("persistData"); 
		}

		path += forumId + SystemGlobals.getValue(ConfigKeys.SERVLET_EXTENSION);
		
		return path;
	}
	
	// Mark all topics as read
	public void readAll() throws Exception
	{
		SearchData sd = new SearchData();
		sd.setTime(SessionFacade.getUserSession().getLastVisit());
		
		String forumId = this.request.getParameter("forum_id");
		if (forumId != null) {
			sd.setForumId(Integer.parseInt(forumId));
		}
		
		List allTopics = DataAccessDriver.getInstance().newSearchDAO().search(sd);
		for (Iterator iter = allTopics.iterator(); iter.hasNext(); ) {
			Topic t = (Topic)iter.next();
			
			((Map)SessionFacade.getAttribute(ConfigKeys.TOPICS_TRACKING)).put(new Integer(t.getId()), 
				new Long(t.getLastPostDate().getTime()));
		}
		
		if (forumId != null) {
			JForumExecutionContext.setRedirect(this.makeRedirect("show"));
		}
		else {
			JForumExecutionContext.setRedirect(this.request.getContextPath() + "/forums/list"
				+ SystemGlobals.getValue(ConfigKeys.SERVLET_EXTENSION));
		}
	}
	
	// Messages since last visit
	public void newMessages() throws Exception
	{
		this.request.addParameter("post_time", Long.toString(SessionFacade.getUserSession().getLastVisit().getTime()));
		this.request.addParameter("clean", "true");
		this.request.addParameter("sort_by", "t." + SystemGlobals.getValue(ConfigKeys.TOPIC_TIME_FIELD));
		this.request.addParameter("sort_dir", "DESC");
		
		new SearchAction(this.request, this.response, this.context).search();		
		this.setTemplateName(TemplateKeys.SEARCH_NEW_MESSAGES);
	}
	
	public void approveMessages() throws Exception
	{
		if (SessionFacade.getUserSession().isModerator(this.request.getIntParameter("forum_id"))) {
			new ModerationAction(this.context, this.request).doSave();
		}
		
		JForumExecutionContext.setRedirect(this.request.getContextPath()
			+ "/forums/show/" + this.request.getParameter("forum_id")
			+ SystemGlobals.getValue(ConfigKeys.SERVLET_EXTENSION));
	}
	
	/**
	 * Action when users click on "watch this forum"
	 * It gets teh forum_id and userId, and put them into a watch_forum table in the database;
	 * To do: may need change other codes to "create/update" the new table.
	 * 
	 * @throws Exception
	 */
	public void watchForum() throws Exception {
		int forumId = this.request.getIntParameter("forum_id");
		int userId = SessionFacade.getUserSession().getUserId();

		this.watchForum(DataAccessDriver.getInstance().newForumDAO(), forumId, userId);

		JForumExecutionContext.setRedirect(this.redirectLinkToShowAction(forumId));
	}
	
	private String redirectLinkToShowAction(int forumId)
	{
		int start = ViewCommon.getStartPage();
		
		return this.request.getContextPath()
			+ "/forums/show/"
			+ (start > 0 ? start + "/" : "")
			+ forumId
			+ SystemGlobals.getValue(ConfigKeys.SERVLET_EXTENSION);
	}
	
	/**
	 * 
	 * @param dao
	 * @param topicId
	 * @param userId
	 * @throws Exception
	 */
	private void watchForum(ForumDAO dao, int forumId, int userId) throws Exception {
		if (SessionFacade.isLogged() && !dao.isUserSubscribed(forumId, userId)) {
			dao.subscribeUser(forumId, userId);
		}
	}
	
	/**
	 * Unwatch the forum watched.
	 * @throws Exception
	 */
	public void unwatchForum() throws Exception{
		if (SessionFacade.isLogged()) {
			int forumId = this.request.getIntParameter("forum_id");
			int userId = SessionFacade.getUserSession().getUserId();

			DataAccessDriver.getInstance().newForumDAO().removeSubscription(forumId, userId);
			
			String returnPath = this.redirectLinkToShowAction(forumId);
			
			this.setTemplateName(TemplateKeys.POSTS_UNWATCH);
			this.context.put("message", I18n.getMessage("ForumBase.forumUnwatched", new String[] { returnPath }));
		}
		else {
			this.setTemplateName(ViewCommon.contextToLogin());
		}	
	}
}
