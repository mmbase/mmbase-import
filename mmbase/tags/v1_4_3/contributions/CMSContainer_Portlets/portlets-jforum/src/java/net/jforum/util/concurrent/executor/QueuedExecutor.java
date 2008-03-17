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
 * This file creation date: Jan 28, 2004
 * The JForum Project
 * http://www.jforum.net
 */
package net.jforum.util.concurrent.executor;

import net.jforum.util.concurrent.Executor;
import net.jforum.util.concurrent.Queue;
import net.jforum.util.concurrent.Result;
import net.jforum.util.concurrent.Task;
import net.jforum.util.concurrent.queue.UnboundedFifoQueue;
import net.jforum.util.preferences.ConfigKeys;
import net.jforum.util.preferences.SystemGlobals;

import org.apache.log4j.Logger;

/**
 * @author Rodrigo Kumpera
 * @version $Id: QueuedExecutor.java,v 1.1 2008-01-17 08:04:52 mguo Exp $
 */
public class QueuedExecutor implements Executor 
{
	private Thread currentThread;
	private final Queue queue;
	private final Object lock = new Object();
	private static final Logger logger = Logger.getLogger(QueuedExecutor.class);
	
	private static QueuedExecutor instance = new QueuedExecutor(new UnboundedFifoQueue());
	
	private QueuedExecutor(Queue queue) 
	{
		logger.info("Setting queue...");
		this.queue = queue;
	}
	
	public static QueuedExecutor getInstance()
	{
		return instance;
	}
	
	private class WorkerThread extends AbstractWorker 
	{
		protected Object take() throws InterruptedException 
		{
			return queue.get();
		}
		
		protected void cleanup() 
		{
			synchronized(lock) {
				currentThread = null;
				logger.info("Cleaning up the thread...");
			}
		}
	}

	public void execute(Task task) throws InterruptedException 
	{
		if (SystemGlobals.getBoolValue(ConfigKeys.BACKGROUND_TASKS)) {
			queue.put(task);
			synchronized(lock) {
				if(currentThread == null) {
					logger.info("Creating a new thread...");
					
					currentThread = new Thread(new WorkerThread(), "jforum");
					currentThread.setDaemon(true);
					currentThread.start();	
				}
			}
		}
		else {
			try {
				task.execute();
			}
			catch (Exception e) {
				logger.warn("Error while executing a task: " + e);
			}
		}
	}
	
	public Result executeWithResult(Task task) throws InterruptedException 
	{
		SimpleResult result = new SimpleResult(task);
		queue.put(result);
		
		synchronized(lock) {
			if(currentThread == null) {
				currentThread = new Thread(new WorkerThread(), "jforum");
				currentThread.setDaemon(true);
				currentThread.setName(this.getClass().getName() + "Thread");

				currentThread.start();	
			}
		}

		return result;
	}
}
