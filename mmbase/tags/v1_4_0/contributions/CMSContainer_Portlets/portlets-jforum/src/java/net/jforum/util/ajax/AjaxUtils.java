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
 * Created on May 29, 2005 1:45:36 PM
 * The JForum Project
 * http://www.jforum.net
 */
package net.jforum.util.ajax;

import java.util.ArrayList;
import java.util.Date;

import net.jforum.entities.Post;
import net.jforum.util.SafeHtml;
import net.jforum.util.mail.Spammer;
import net.jforum.util.preferences.ConfigKeys;
import net.jforum.util.preferences.SystemGlobals;
import net.jforum.view.forum.common.PostCommon;
import freemarker.template.SimpleHash;

/**
 * General AJAX utility methods. 
 * 
 * @author Rafael Steil
 * @version $Id: AjaxUtils.java,v 1.1 2008-01-17 07:48:25 mguo Exp $
 */
public class AjaxUtils
{
	/**
	 * Sends a test message
	 * @param sender The sender's email address
	 * @param host the smtp host
	 * @param auth if need authorization or not
	 * @param username the smtp server username, if auth is needed
	 * @param password the smtp server password, if auth is needed
	 * @param to the recipient
	 * @return The status message
	 */
	public static String sendTestMail(String sender, String host, String auth, 
			String  username, String password, String to)
	{
		// Save the current values
		String originalHost = SystemGlobals.getValue(ConfigKeys.MAIL_SMTP_HOST);
		String originalAuth = SystemGlobals.getValue(ConfigKeys.MAIL_SMTP_AUTH);
		String originalUsername = SystemGlobals.getValue(ConfigKeys.MAIL_SMTP_USERNAME);
		String originalPassword = SystemGlobals.getValue(ConfigKeys.MAIL_SMTP_PASSWORD);
		String originalSender = SystemGlobals.getValue(ConfigKeys.MAIL_SENDER);
		
		// Now put the new ones
		SystemGlobals.setValue(ConfigKeys.MAIL_SMTP_HOST, host);
		SystemGlobals.setValue(ConfigKeys.MAIL_SMTP_AUTH, auth);
		SystemGlobals.setValue(ConfigKeys.MAIL_SMTP_USERNAME, username);
		SystemGlobals.setValue(ConfigKeys.MAIL_SMTP_PASSWORD, password);
		SystemGlobals.setValue(ConfigKeys.MAIL_SENDER, sender);
		
		// Send the test mail
		class TestSpammer extends Spammer {
			public TestSpammer(final String to) {
				super.prepareMessage(new ArrayList() {{ add(to); }}, null, "JForum Test Mail", null);
			}
			
			protected String getMessageText(SimpleHash params, String messageFile) throws Exception {
				return ("Test mail from JForum Admin Panel. Sent at " + new Date());
			}
		}
		
		Spammer s = new TestSpammer(to);
		
		try {
			s.dispatchMessages();
		}
		catch (Exception e) {
			return e.toString();
		}
		finally {
			// Restore the original values
			SystemGlobals.setValue(ConfigKeys.MAIL_SMTP_HOST, originalHost);
			SystemGlobals.setValue(ConfigKeys.MAIL_SMTP_AUTH, originalAuth);
			SystemGlobals.setValue(ConfigKeys.MAIL_SMTP_USERNAME, originalUsername);
			SystemGlobals.setValue(ConfigKeys.MAIL_SMTP_PASSWORD, originalPassword);
			SystemGlobals.setValue(ConfigKeys.MAIL_SENDER, originalSender);
		}
		
		return "OK";
	}
	
	/**
	 * Prepares a message for previwing
	 * @param p the post to preview
	 * @return the formatted post
	 */
	public static Post previewPost(Post p)
	{
		p = PostCommon.preparePostForDisplay(p);
		
		if (p.isHtmlEnabled()) {
			p.setText(SafeHtml.makeSafe(p.getText()));
		}
		
		return p;
	}
}
