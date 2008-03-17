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
 * Class created on Jul 15, 2005
 * The JForum Project
 * http://www.jforum.net
 */
package net.jforum.summary;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import net.jforum.dao.DataAccessDriver;
import net.jforum.dao.SummaryDAO;
import net.jforum.util.mail.Spammer;
import net.jforum.util.preferences.ConfigKeys;
import net.jforum.util.preferences.SystemGlobals;
import net.jforum.view.forum.common.ViewCommon;

import org.apache.log4j.Logger;

import freemarker.template.SimpleHash;

/**
 * Manage the Summary sends.
 * 
 * @see net.jforum.summary.SummaryJob
 * @see net.jforum.summary.SummaryScheduler
 * 
 * @author Franklin S. Dattein (<a href="mailto:franklin@hp.com">franklin@hp.com</a>)
 * @version $Id: SummaryModel.java,v 1.1 2008-01-17 08:04:51 mguo Exp $
 */
public class SummaryModel extends Spammer
{
	private SummaryDAO dao;

	private static Logger logger = Logger.getLogger(SummaryModel.class);

	public SummaryModel()
	{
		this.dao = DataAccessDriver.getInstance().newSummaryDAO();
	}

	public void sendPostsSummary(List recipients) throws Exception
	{
		logger.info("Sending Weekly summary...");

		// Gets a Date seven days before now
		int daysBefore = Integer.parseInt(SystemGlobals.getValue(ConfigKeys.SUMMARY_DAYS_BEFORE));

		// New date "X" days before now, where "X" is the number set on the variable daysBefore
		long dateBefore = Calendar.getInstance().getTimeInMillis() - (daysBefore * 1000 * 60 * 60 * 24);

		List posts = listPosts(new Date(dateBefore), new Date());

		String forumLink = ViewCommon.getForumLink();

		SimpleHash params = new SimpleHash();
		params.put("posts", posts);
		params.put("url", forumLink);

		String subject = SystemGlobals.getValue(ConfigKeys.MAIL_SUMMARY_SUBJECT);
		
		// FIXME: use real recipients
		// recipients = null;
		// recipients = new ArrayList();
		// recipients.add("franklin@hp.com");

		this.prepareMessage(recipients, params, subject, SystemGlobals.getValue(ConfigKeys.MAIL_SUMMARY_FILE));
		super.dispatchMessages();
	}

	/**
	 * List all recipients able to receive posts summaries.
	 * 
	 * @return List of users
	 * @throws Exception
	 */
	public List listRecipients() throws Exception
	{
		return this.dao.listRecipients();
	}

	/**
	 * List last posts of a period like a week, day or month.
	 * 
	 * @param firstDate First date of a period.
	 * @param lastDate Last date of a period.
	 * @return List of Posts
	 * @throws Exception
	 */
	public List listPosts(Date firstDate, Date lastDate) throws Exception
	{
		return this.dao.selectLastPosts(firstDate, lastDate);
	}
}
