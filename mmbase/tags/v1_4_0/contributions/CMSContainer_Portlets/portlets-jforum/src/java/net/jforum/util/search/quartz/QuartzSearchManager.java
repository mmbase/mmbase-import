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
 * Created on Mar 11, 2005 11:52:24 AM
 * The JForum Project
 * http://www.jforum.net
 */
package net.jforum.util.search.quartz;

import net.jforum.entities.Post;
import net.jforum.exceptions.SearchInstantiationException;
import net.jforum.util.preferences.ConfigKeys;
import net.jforum.util.preferences.SystemGlobals;
import net.jforum.util.search.SearchManager;

import org.apache.log4j.Logger;
import org.quartz.CronTrigger;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.Trigger;
import org.quartz.impl.StdSchedulerFactory;

/**
 * @author Rafael Steil
 * @version $Id: QuartzSearchManager.java,v 1.1 2008-01-17 08:04:51 mguo Exp $
 */
public class QuartzSearchManager implements SearchManager
{
	private static Logger logger = Logger.getLogger(QuartzSearchManager.class);
	private static Scheduler scheduler;
	/**
	 * @see net.jforum.util.search.SearchManager#init()
	 */
	public void init()
	{
		try {
			String filename = SystemGlobals.getValue(ConfigKeys.SEARCH_INDEXER_QUARTZ_CONFIG);
			
			SystemGlobals.loadAdditionalDefaults(filename);
			String cronExpression = SystemGlobals.getValue(
					ConfigKeys.QUARTZ_CONTEXT + ConfigKeys.SEARCH_INDEXER_CRON_EXPRESSON);
			
			scheduler = new StdSchedulerFactory(filename).getScheduler();
			Trigger trigger = new CronTrigger(QuartzSearchIndexerJob.class.getName(), 
					"indexer", 
					cronExpression);
			
			logger.info("Starting quartz search manager using expression " + cronExpression);
			
			scheduler.scheduleJob(new JobDetail(QuartzSearchIndexerJob.class.getName(), 
					"indexer", 
					QuartzSearchIndexerJob.class), 
				trigger);
			scheduler.start();
		}
		catch (Exception e) {
			if (e.toString().indexOf("org.quartz.ObjectAlreadyExistsException") == -1) {
				throw new SearchInstantiationException("Error while trying to start " + this.getClass().getName() + ": " + e);
			}
		}
	}

	/**
	 * @see net.jforum.util.search.SearchManager#index(net.jforum.entities.Post)
	 */
	public void index(Post post) {}
}
