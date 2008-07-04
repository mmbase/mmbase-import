/*
 * Copyright (c) JForum Team
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
 * Created on Mar 28, 2005 7:22:52 PM
 * The JForum Project
 * http://www.jforum.net
 */
package net.jforum.sso;

import net.jforum.context.RequestContext;
import net.jforum.entities.UserSession;

/**
 * @author Rafael Steil
 * @author Daniel Campagnoli
 * @version $Id: SSO.java,v 1.3 2008-07-04 00:31:15 kevinshen Exp $
 */
public interface SSO
{
	/**
	 * Authenticates an user. 
	 * This method should check if the incoming user is authorized
	 * to access the forum. 
	 * @param request The request object
	 * @return The username, if authentication succeded, or <code>nulll</code> 
	 * otherwise. 
	 */
	public String authenticateUser(RequestContext request);
   
   
    /**
     * Check to see if the user for the current {@link UserSession} is the same user by
     * single sign on mechanisim.
     * @param userSession the current user session
     * @param request the current request
     * @return if the UserSession is valid
     */
    public boolean isSessionValid(UserSession userSession, RequestContext request);
}
