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
 * This file creation date: 04/03/2004 - 20:32:13
 * The JForum Project
 * http://www.jforum.net
 */
package net.jforum.util.mail;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.jforum.entities.Topic;
import net.jforum.entities.User;
import net.jforum.entities.Post;
import net.jforum.util.preferences.ConfigKeys;
import net.jforum.util.preferences.SystemGlobals;
import net.jforum.view.forum.common.PostCommon;
import freemarker.template.SimpleHash;

/**
 * @author Rafael Steil
 * @version $Id: TopicSpammer.java,v 1.1 2008-01-17 08:04:50 mguo Exp $
 */
public class TopicSpammer extends Spammer 
{
	public TopicSpammer(Topic topic, Post post, List users)
	{
		// Prepare the users. In this current version, the email
		// is not personalized, so then we'll just use his address
		List recipients = new ArrayList();
		for (Iterator iter = users.iterator(); iter.hasNext(); ) {
			User u = (User)iter.next();
			
			recipients.add(u.getEmail());
		}
		
		// Make the topic url
		String page = "";
		int postsPerPage = SystemGlobals.getIntValue(ConfigKeys.POST_PER_PAGE);
		if (topic.getTotalReplies() > postsPerPage) {
			page += (((topic.getTotalReplies() / postsPerPage)) * postsPerPage) + "/";
		}
		
		String forumLink = SystemGlobals.getValue(ConfigKeys.FORUM_LINK);
		if (!forumLink.endsWith("/")) {
			forumLink += "/";
		}

		String path = forumLink + "posts/list/" + page + topic.getId() 
			+ SystemGlobals.getValue(ConfigKeys.SERVLET_EXTENSION) + "#" + topic.getLastPostId();
		
		String unwatch = forumLink + "posts/unwatch/" + topic.getId()
			+ SystemGlobals.getValue(ConfigKeys.SERVLET_EXTENSION);
		
		SimpleHash params = new SimpleHash();
		params.put("topic", topic);
		params.put("path", path);
		params.put("forumLink", forumLink);
		params.put("unwatch", unwatch);

        //jahia fork
        if (post != null) {
			post = PostCommon.preparePostForDisplay(post);
			params.put("message", post.getText());
            params.put("postUsername", post.getPostUsername());
        }

        super.prepareMessage(recipients, params,
			MessageFormat.format(SystemGlobals.getValue(ConfigKeys.MAIL_NEW_ANSWER_SUBJECT), new Object[] { topic.getTitle() }),
			SystemGlobals.getValue(ConfigKeys.MAIL_NEW_ANSWER_MESSAGE_FILE));
	}
}
