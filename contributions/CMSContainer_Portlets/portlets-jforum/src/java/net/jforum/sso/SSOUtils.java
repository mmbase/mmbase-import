/*
 *  Copyright (c) Rafael Steil
 *  All rights reserved.
 *
 *  Redistribution and use in source and binary forms,
 *  with or without modification, are permitted provided
 *  that the following conditions are met:
 *
 *  1) Redistributions of source code must retain the above
 *  copyright notice, this list of conditions and the
 *  following  disclaimer.
 *  2)  Redistributions in binary form must reproduce the
 *  above copyright notice, this list of conditions and
 *  the following disclaimer in the documentation and/or
 *  other materials provided with the distribution.
 *  3) Neither the name of "Rafael Steil" nor
 *  the names of its contributors may be used to endorse
 *  or promote products derived from this software without
 *  specific prior written permission.
 *
 *  THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT
 *  HOLDERS AND CONTRIBUTORS "AS IS" AND ANY
 *  EXPRESS OR IMPLIED WARRANTIES, INCLUDING,
 *  BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF
 *  MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 *  PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL
 *  THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE
 *  FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 *  EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 *  (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 *  SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA,
 *  OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 *  CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER
 *  IN CONTRACT, STRICT LIABILITY, OR TORT
 *  (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN
 *  ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF
 *  ADVISED OF THE POSSIBILITY OF SUCH DAMAGE
 *
 *  Created on Jun 2, 2005 6:56:25 PM
 *  The JForum Project
 *  http://www.jforum.net
 */
package net.jforum.sso;

import net.jforum.dao.DataAccessDriver;
import net.jforum.dao.UserDAO;
import net.jforum.entities.User;
import net.jforum.util.preferences.ConfigKeys;

/**
 *  General utilities to use with SSO.
 *
 *@author     Rafael Steil
 *@version    $Id: SSOUtils.java,v 1.1 2008-01-17 08:04:52 mguo Exp $
 */
public class SSOUtils {
	private String username;
	private boolean exists = true;
	private User user;
	private UserDAO dao;


	/*
	 *  ktlili: Jahia fork
	 */
	/**
	 *  Sets the Role attribute of the SSOUtils object
	 *
	 *@param  role           The new Role value
	 *@exception  Exception  Description of Exception
	 */
	public void setRole(String role) throws Exception {
		//ktlili: Jahia fork
		int[] adminGrouId = {2};
		if (role != null && role.equalsIgnoreCase(ConfigKeys.ROLE_ADMIN)) {
			dao.addToGroup(user.getId(), adminGrouId);
		}
		else {
			dao.removeFromGroup(user.getId(), adminGrouId);
		}

	}


	/**
	 *  Gets the user associated to this class instance.
	 *
	 *@return    the user
	 */
	public User getUser() {
		return this.user;
	}


	/**
	 *  Checks if an user exists in the database
	 *
	 *@param  username    The username to check
	 *@return             <code>true</code> if the user exists. If <code>false</code>
	 *      is returned, then you can insert the user by calling {@link
	 *      #register(String, String)}
	 *@see                #register(String, String)
	 *@see                #getUser()
	 *@throws  Exception
	 */
	public boolean userExists(String username) throws Exception {
		this.username = username;
		this.dao = DataAccessDriver.getInstance().newUserDAO();

		this.user = this.dao.selectByName(username);

		this.exists = this.user != null;

		return this.exists;
	}


	/**
	 *  Registers a new user. This method should be used together with {@link
	 *  #userExists(String)}.
	 *
	 *@param  password    the user's password. It <em>should</em> be the real /
	 *      final password. In other words, the data passed as password is the
	 *      data that'll be written to the database
	 *@param  email       the user's email
	 *@param  role        Description of Parameter
	 *@see                #getUser()
	 *@throws  Exception
	 */
	public void register(String password, String email, String role) throws Exception {
		if (this.exists) {
			return;
		}

		// Is a new user for us. Register him
		this.user = new User();
		user.setUsername(this.username);
		user.setPassword(password);
		user.setEmail(email);
		user.setActive(1);
		user.getGroupsList().add(null);

		this.dao.addNew(user);

		//ktlili: Jahia fork
		if (role != null && role.equalsIgnoreCase(ConfigKeys.ROLE_ADMIN)) {
			int[] adminGrouId = {2};
			dao.addToGroup(user.getId(), adminGrouId);
		}

	}
}
