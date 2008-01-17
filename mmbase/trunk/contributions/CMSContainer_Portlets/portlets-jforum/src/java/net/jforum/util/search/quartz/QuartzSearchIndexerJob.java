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
 * Created on Mar 11, 2005 2:32:25 PM
 * The JForum Project
 * http://www.jforum.net
 */
package net.jforum.util.search.quartz;

import java.io.FileInputStream;
import java.sql.Connection;
import java.util.Properties;

import net.jforum.DBConnection;
import net.jforum.cache.CacheEngine;
import net.jforum.cache.Cacheable;
import net.jforum.dao.DataAccessDriver;
import net.jforum.dao.ScheduledSearchIndexerDAO;
import net.jforum.util.preferences.ConfigKeys;
import net.jforum.util.preferences.SystemGlobals;

import org.apache.log4j.Logger;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

/**
 * @author Rafael Steil
 * @version $Id: QuartzSearchIndexerJob.java,v 1.1 2008-01-17 08:04:51 mguo Exp $
 */
public class QuartzSearchIndexerJob implements Job, Cacheable
{
	private static final String FQN = "quartz";
	private static final String INDEXING = "indexing";
	private static Logger logger = Logger.getLogger(QuartzSearchIndexerJob.class);
	private static CacheEngine cache;
	
	/**
	 * @see net.jforum.cache.Cacheable#setCacheEngine(net.jforum.cache.CacheEngine)
	 */
	public void setCacheEngine(CacheEngine engine)
	{
		cache = engine;
	}
	
	/**
	 * @see org.quartz.Job#execute(org.quartz.JobExecutionContext)
	 */
	public void execute(JobExecutionContext context) throws JobExecutionException
	{
		if ("1".equals(cache.get(FQN, INDEXING))) {
			logger.info("Indexing is already running. Going home...");
			return;
		}
		
		Properties p = this.loadConfig();
		
		if (p == null) {
			return;
		}
		
		int step = Integer.parseInt(p.getProperty(ConfigKeys.QUARTZ_CONTEXT + ConfigKeys.SEARCH_INDEXER_STEP));
		
		Connection conn = null;
		boolean autoCommit = false;
		
		try {
			conn = DBConnection.getImplementation().getConnection();
			autoCommit = conn.getAutoCommit();
			conn.setAutoCommit(true);
			
			cache.add(FQN, INDEXING, "1");
			
			ScheduledSearchIndexerDAO dao = DataAccessDriver.getInstance().newScheduledSearchIndexerDAO();
			dao.index(step, conn);
		}
		catch (Exception e) {
			logger.error("Error while trying to index messages. Cannot proceed. " + e);
			e.printStackTrace();
		}
		finally {
			cache.remove(FQN, INDEXING);
			
			if (conn != null) {
				try { conn.setAutoCommit(autoCommit); } catch (Exception e) {}
				DBConnection.getImplementation().releaseConnection(conn);
			}
		}
	}
	
	private Properties loadConfig()
	{
		String filename = SystemGlobals.getValue(ConfigKeys.SEARCH_INDEXER_QUARTZ_CONFIG);
		
		try {
			Properties p = new Properties();
			p.load(new FileInputStream(filename));

			return p;
		}
		catch (Exception e) {
			logger.warn("Failed to load " + filename + ": " + e, e);
			return null;
		}
	}
}
