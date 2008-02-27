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
 * Created on 02/10/2005 20:04:15
 * The JForum Project
 * http://www.jforum.net
 */
package net.jforum.view.forum;

import net.jforum.Command;
import net.jforum.JForumExecutionContext;
import net.jforum.util.preferences.TemplateKeys;

/**
 * @author Rafael Steil
 * @version $Id: ModerationAction.java,v 1.1 2008-01-17 08:04:50 mguo Exp $
 */
public class ModerationAction extends Command
{
	/**
	 * @throws UnsupportedOperationException always
	 * @see net.jforum.Command#list()
	 */
	public void list() throws Exception
	{
		throw new UnsupportedOperationException();
	}

	public void doModeration() throws Exception
	{
		String returnUrl = this.request.getParameter("returnUrl");

		new ModerationHelper().doModeration(returnUrl);

		this.context.put("returnUrl", returnUrl);

		if (JForumExecutionContext.getRequest().getParameter("topicMove") != null) {
			this.setTemplateName(TemplateKeys.MODERATION_MOVE_TOPICS);
		}
	}

	public void moveTopic() throws Exception
	{
		new ModerationHelper().moveTopicsSave(this.request.getParameter("returnUrl"));
	}

	public void moderationDone() throws Exception
	{
		this.setTemplateName(new ModerationHelper().moderationDone(this.request.getParameter("returnUrl")));
	}
}
