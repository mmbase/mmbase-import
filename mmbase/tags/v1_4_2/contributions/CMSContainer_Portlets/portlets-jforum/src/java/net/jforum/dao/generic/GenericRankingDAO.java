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
 * This file creation date: Mar 23, 2003 / 7:52:13 PM
 * The JForum Project
 * http://www.jforum.net
 */
package net.jforum.dao.generic;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import net.jforum.JForumExecutionContext;
import net.jforum.entities.Ranking;
import net.jforum.util.preferences.SystemGlobals;

/**
 * @author Rafael Steil
 * @version $Id: GenericRankingDAO.java,v 1.1 2008-01-17 08:04:50 mguo Exp $
 */
public class GenericRankingDAO implements net.jforum.dao.RankingDAO 
{
	/** 
	 * @see net.jforum.dao.RankingDAO#selectById(int)
	 */
	public Ranking selectById(int rankingId) throws Exception 
	{
		Ranking ranking = new Ranking();
		
		PreparedStatement p = JForumExecutionContext.getConnection().prepareStatement(SystemGlobals.getSql("RankingModel.selectById"));		
		p.setInt(1, rankingId);
		
		ResultSet rs = p.executeQuery();
		if (rs.next()) {
				ranking.setId(rankingId);
				ranking.setTitle(rs.getString("rank_title"));
				ranking.setImage(rs.getString("rank_image"));
				ranking.setMin(rs.getInt("rank_min"));
				ranking.setSpecial(rs.getInt("rank_special"));
		}
		
		rs.close();
		p.close();
		
		return ranking;
	}

	/**
	 * @see net.jforum.dao.RankingDAO#selectAll()
	 */
	public List selectAll() throws Exception 
	{
		List l = new ArrayList();
		PreparedStatement p = JForumExecutionContext.getConnection().prepareStatement(SystemGlobals.getSql("RankingModel.selectAll"));
		ResultSet rs = p.executeQuery();
		
		while (rs.next()) {
			Ranking ranking = new Ranking();
			
			ranking.setId(rs.getInt("rank_id"));
			ranking.setTitle(rs.getString("rank_title"));
			ranking.setImage(rs.getString("rank_image"));
			ranking.setMin(rs.getInt("rank_min"));
			ranking.setSpecial(rs.getInt("rank_special"));
			
			l.add(ranking);			
		}
		
		rs.close();
		p.close();
		
		return l;
	}

	/** 
	 * @see net.jforum.dao.RankingDAO#delete(int)
	 */
	public void delete(int rankingId) throws Exception 
	{
		PreparedStatement p = JForumExecutionContext.getConnection().prepareStatement(SystemGlobals.getSql("RankingModel.delete"));
		p.setInt(1, rankingId);
		
		p.executeUpdate();
		p.close();
	}

	/** 
	 * @see net.jforum.dao.RankingDAO#update(net.jforum.Ranking)
	 */
	public void update(Ranking ranking) throws Exception 
	{
		PreparedStatement p = JForumExecutionContext.getConnection().prepareStatement(SystemGlobals.getSql("RankingModel.update"));
		
		p.setString(1, ranking.getTitle());
		p.setString(2, ranking.getImage());
		p.setInt(3, ranking.getSpecial());
		p.setInt(4, ranking.getMin());
		p.setInt(5, ranking.getId());
		
		p.executeUpdate();
		p.close();
	}

	/** 
	 * @see net.jforum.dao.RankingDAO#addNew(net.jforum.Ranking)
	 */
	public void addNew(Ranking ranking) throws Exception 
	{
		PreparedStatement p = JForumExecutionContext.getConnection().prepareStatement(SystemGlobals.getSql("RankingModel.addNew"));
		
		p.setString(1, ranking.getTitle());
		p.setInt(2, ranking.getMin());
		
		p.executeUpdate();
		p.close();
	}

}
