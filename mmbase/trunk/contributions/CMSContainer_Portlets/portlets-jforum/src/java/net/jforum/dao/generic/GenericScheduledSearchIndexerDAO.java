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
 * Created on Mar 11, 2005 5:33:54 PM
 * The JForum Project
 * http://www.jforum.net
 */
package net.jforum.dao.generic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import net.jforum.dao.DataAccessDriver;
import net.jforum.dao.SearchIndexerDAO;
import net.jforum.entities.Post;
import net.jforum.util.preferences.SystemGlobals;

import org.apache.log4j.Logger;

/**
 * @author Rafael Steil
 * @version $Id: GenericScheduledSearchIndexerDAO.java,v 1.1 2008-01-17 08:04:50 mguo Exp $
 */
public class GenericScheduledSearchIndexerDAO implements net.jforum.dao.ScheduledSearchIndexerDAO
{
	private static Logger logger = Logger.getLogger(GenericScheduledSearchIndexerDAO.class);
	
	/**
	 * @see net.jforum.dao.ScheduledSearchIndexerDAO#index(int)
	 */
	public int index(int step, Connection conn) throws Exception
	{
		Timestamp timestamp = new Timestamp(System.currentTimeMillis());
		
		// Get the last post id so far
		PreparedStatement p = conn.prepareStatement(SystemGlobals.getSql("SearchModel.maxPostIdUntilNow"));
		p.setTimestamp(1, timestamp);

		ResultSet rs = p.executeQuery();
		int maxPostId = -1;
		
		if (rs.next()) {
			maxPostId = rs.getInt(1);
		}
		
		// Get the latest indexed post
		rs.close();
		p.close();
		
		int latestIndexedPostId = -1;
		
		p = conn.prepareStatement(SystemGlobals.getSql("SearchModel.lastIndexedPostId"));
		rs = p.executeQuery();
		
		if (rs.next()) {
			latestIndexedPostId = rs.getInt(1);
		}
		
		rs.close();
		p.close();
		
		if (maxPostId == -1 || latestIndexedPostId == -1 || maxPostId <= latestIndexedPostId) {
			logger.info("No posts found to index. Leaving...");
			return 0;
		}
		
		if (logger.isInfoEnabled()) {
		    logger.info("Going to index posts from " + latestIndexedPostId + " to " + maxPostId);
		}
		
		// Count how many posts we have to index
		p = conn.prepareStatement(SystemGlobals.getSql("SearchModel.howManyToIndex"));
		p.setTimestamp(1, timestamp);
		p.setInt(2, latestIndexedPostId);
		
		rs = p.executeQuery();
		rs.next();
		
		int total = rs.getInt(1);
		
		rs.close();
		p.close();
		
		// Do the dirty job
		SearchIndexerDAO sim = DataAccessDriver.getInstance().newSearchIndexerDAO();
		sim.setConnection(conn);
		
		int start = 0;
		while (true) {
			List posts = this.getPosts(start, step, latestIndexedPostId, maxPostId, conn);
			
			if (posts.size() == 0) {
				break;
			}
			
			logger.info("Indexing range [" + start + ", " + (start + step) + "] from a total of " + total);
			
			sim.insertSearchWords(posts);
			
			start += step;
		}
		
		return maxPostId;
	}
	
	public List getPosts(int start, int count, int minPostId, int maxPostId, Connection conn) throws Exception
	{
		List l = new ArrayList();
		
		PreparedStatement p = conn.prepareStatement(SystemGlobals.getSql("SearchModel.getPostsToIndex"));
		p.setInt(1, minPostId);
		p.setInt(2, maxPostId);
		p.setInt(3, start);
		p.setInt(4, count);
		
		ResultSet rs = p.executeQuery();
		while (rs.next()) {
			l.add(this.makePost(rs));
		}
		
		rs.close();
		p.close();
		
		return l;
	}
	
	protected String getPostTextFromResultSet(ResultSet rs) throws Exception
	{
		return rs.getString("post_text");
	}
	
	protected Post makePost(ResultSet rs) throws Exception
	{
		Post p = new Post();
		p.setId(rs.getInt("post_id"));
		p.setText(this.getPostTextFromResultSet(rs));
		p.setSubject("post_subject");
		
		return p;
	}
}
