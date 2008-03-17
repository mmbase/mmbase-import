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
 * Created on 02/07/2005 13:18:34
 * The JForum Project
 * http://www.jforum.net
 */
package net.jforum.dao.mysql.security;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

import net.jforum.JForumExecutionContext;
import net.jforum.dao.generic.security.GenericGroupSecurityDAO;
import net.jforum.util.preferences.SystemGlobals;

/**
 * Mysq 3.23 hacks based on Andy's work
 * 
 * @author Rafael Steil
 * @version $Id: MySQL323GroupSecurityDAO.java,v 1.1 2008-01-17 08:04:52 mguo Exp $
 */
public class MySQL323GroupSecurityDAO extends GenericGroupSecurityDAO
{
	/**
	 * @see net.jforum.dao.security.SecurityDAO#deleteAllRoles(int)
	 */
	public void deleteAllRoles(int id) throws Exception
	{
		PreparedStatement p = JForumExecutionContext.getConnection().prepareStatement(
				SystemGlobals.getSql("PermissionControl.getRoleIdsByGroup"));
		p.setInt(1, id);
		
		String roleIds = this.getCsvIdList(p);
		
		p.close();
		
		if (roleIds.length() == 0) {
			return;
		}
		
		p = this.getStatementForCsv(
				SystemGlobals.getSql("PermissionControl.deleteRoleValuesByRoleId"),
				roleIds);
		
		p.executeUpdate();
		p.close();
	}
	
	/**
	 * Gets a statement to use with some csv data
	 * @param sql The SQL query to execute. It must have an "?", which
	 * will be replaced by <code>csv</code>
	 * @param csv The ids to replace
	 * @return The statement, ready to execute
	 * @throws Exception
	 */
	protected PreparedStatement getStatementForCsv(String sql, String csv) throws Exception
	{
		int index = sql.indexOf('?');
		sql = sql.substring(0, index) + csv + sql.substring(index + 1);
		return JForumExecutionContext.getConnection().prepareStatement(sql);
	}
	
	/**
	 * Gets a set of ids from a statement
	 * The statement is expected to return an id in the first column
	 * @param p The statement to execute
	 * @return The ids, separated by comma
	 * @throws Exception
	 */
	protected String getCsvIdList(PreparedStatement p) throws Exception
	{
		ResultSet rs = p.executeQuery();
		
		StringBuffer sb = new StringBuffer();
		
		while (rs.next()) {
			sb.append(rs.getInt(1)).append(",");
		}
		
		sb.append("-1");
		
		rs.close();
		
		return sb.toString();
	}
}
