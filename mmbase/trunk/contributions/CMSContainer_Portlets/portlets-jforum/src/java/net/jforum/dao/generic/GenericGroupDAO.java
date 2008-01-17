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
 * This file creation date: Mar 3, 2003 / 1:35:30 PM
 * The JForum Project
 * http://www.jforum.net
 */
package net.jforum.dao.generic;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import net.jforum.JForumExecutionContext;
import net.jforum.entities.Group;
import net.jforum.util.preferences.SystemGlobals;

/**
 * @author Rafael Steil
 * @version $Id: GenericGroupDAO.java,v 1.1 2008-01-17 08:04:50 mguo Exp $
 */
public class GenericGroupDAO implements net.jforum.dao.GroupDAO 
{
	/**
	 * @see net.jforum.dao.GroupDAO#selectById(int)
	 */
	public Group selectById(int groupId) throws Exception 
	{
		PreparedStatement p = JForumExecutionContext.getConnection().prepareStatement(SystemGlobals.getSql("GroupModel.selectById"));		
		p.setInt(1, groupId);
		
		ResultSet rs = p.executeQuery();
		
		Group g = new Group();
		
		if (rs.next()) {
			g = this.getGroup(rs);
		}
		
		rs.close();
		p.close();

		return g;
	}

	/**
	 * @see net.jforum.dao.GroupDAO#canDelete(int)
	 */
	public boolean canDelete(int groupId) throws Exception
	{
		PreparedStatement p = JForumExecutionContext.getConnection().prepareStatement(SystemGlobals.getSql("GroupModel.canDelete"));
		p.setInt(1, groupId);
		
		boolean status = false;
		
		ResultSet rs = p.executeQuery();
		if (!rs.next() || rs.getInt("total") < 1) {
			status = true;
		}
		
		rs.close();
		p.close();
		
		return status;
	}

	/**
	 * @see net.jforum.dao.GroupDAO#delete(int)
	 */
	public void delete(int groupId) throws Exception 
	{
		PreparedStatement p = JForumExecutionContext.getConnection().prepareStatement(SystemGlobals.getSql("GroupModel.delete"));
		p.setInt(1, groupId);
		
		p.executeUpdate();
		p.close();
	}

	/**
	 * @see net.jforum.dao.GroupDAO#update(net.jforum.Group)
	 */
	public void update(Group group) throws Exception 
	{
		PreparedStatement p = JForumExecutionContext.getConnection().prepareStatement(SystemGlobals.getSql("GroupModel.update"));
		p.setString(1, group.getName());
		p.setInt(2, group.getParentId());
		p.setString(3, group.getDescription());
		p.setInt(4, group.getId());
		
		p.executeUpdate();
		p.close();
	}

	/**
	 * @see net.jforum.dao.GroupDAO#addNew(net.jforum.Group)
	 */
	public void addNew(Group group) throws Exception 
	{
		PreparedStatement p = JForumExecutionContext.getConnection().prepareStatement(SystemGlobals.getSql("GroupModel.addNew"));
		p.setString(1, group.getName());
		p.setString(2, group.getDescription());
		p.setInt(3, group.getParentId());
		
		p.executeUpdate();
		p.close();
	}

	/** 
	 * @see net.jforum.dao.GroupDAO#selectUsersIds(int)
	 */
	public List selectUsersIds(int groupId) throws Exception 
	{
		ArrayList l = new ArrayList();
		
		PreparedStatement p = JForumExecutionContext.getConnection().prepareStatement(SystemGlobals.getSql("GroupModel.selectUsersIds"));
		p.setInt(1, groupId);
		
		ResultSet rs = p.executeQuery();
		while (rs.next()) {
			l.add(new Integer(rs.getInt("user_id")));
		}
		
		rs.close();
		p.close();

		return l;
	}
	
	protected List fillGroups(ResultSet rs) throws Exception
	{
		List l = new ArrayList();
		
		while (rs.next()) {
			l.add(this.getGroup(rs));
		}
		
		return l;
	}
	
	protected Group getGroup(ResultSet rs) throws Exception
	{
		Group g = new Group();
		
		g.setId(rs.getInt("group_id"));
		g.setDescription(rs.getString("group_description"));
		g.setName(rs.getString("group_name"));
		g.setParentId(rs.getInt("parent_id"));
		
		return g;
	}

	/** 
	 * @see net.jforum.dao.GroupDAO#selectAll()
	 */
	public List selectAll() throws Exception 
	{
		PreparedStatement p = JForumExecutionContext.getConnection().prepareStatement(SystemGlobals.getSql("GroupModel.selectAll"));
		ResultSet rs = p.executeQuery();
		
		List l = this.fillGroups(rs);
		
		rs.close();
		p.close();
		
		return l;
	}

}
