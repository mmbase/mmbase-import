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
 * Created on Jan 3, 2005 1:20:24 PM
 * The JForum Project
 * http://www.jforum.net
 */
package net.jforum.sso;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Map;

import net.jforum.JForumExecutionContext;
import net.jforum.dao.UserDAO;
import net.jforum.entities.User;
import net.jforum.util.MD5;
import net.jforum.util.preferences.SystemGlobals;

/**
 * Default login authenticator for JForum.
 * This authenticator will validate the input against
 * <i>jforum_users</i>.
 *
 * @author Rafael Steil
 * @version $Id: DefaultLoginAuthenticator.java,v 1.1 2008-01-17 07:48:25 mguo Exp $
 */
public class DefaultLoginAuthenticator implements LoginAuthenticator
{
	private UserDAO userModel;

	/**
	 * @see net.jforum.sso.LoginAuthenticator#setUserModel(net.jforum.dao.UserDAO)
	 */
	public void setUserModel(UserDAO userModel)
	{
		this.userModel = userModel;
	}

	/**
	 * @see net.jforum.sso.LoginAuthenticator#validateLogin(java.lang.String, java.lang.String, Object[])
	 */
	public User validateLogin(String username, String password, Map extraParams) throws Exception
	{
		PreparedStatement p = JForumExecutionContext.getConnection().prepareStatement(SystemGlobals.getSql("UserModel.login"));
		p.setString(1, username);
		p.setString(2, MD5.crypt(password));

		User user = null;

		ResultSet rs = p.executeQuery();
		if (rs.next() && rs.getInt("user_id") > 0) {
			user = this.userModel.selectById(rs.getInt("user_id"));
		}

		rs.close();
		p.close();

		if (user != null && !user.isDeleted() && (user.getActivationKey() == null || user.isActive())) {
			return user;
		}

		return null;
	}
}
