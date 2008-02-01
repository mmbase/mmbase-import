/*
 * Copyright (c) 2003, 2004 Rafael Steil
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
 * This file creation date: Jan 29, 2004
 * The JForum Project
 * http://www.jforum.net
 * 
 * $Id: PooledExecutor.java,v 1.1 2008-01-17 08:04:52 mguo Exp $
 */
package net.jforum.util.concurrent.executor;

import net.jforum.util.concurrent.Executor;
import net.jforum.util.concurrent.Queue;
import net.jforum.util.concurrent.Result;
import net.jforum.util.concurrent.Task;

/**
 * @author Rodrigo Kumpera
 */
public class PooledExecutor implements Executor 
{
	static final int DEFAULT_MIN_SIZE = Runtime.getRuntime().availableProcessors();
	static final int DEFAULT_MAX_SIZE = 4 * DEFAULT_MIN_SIZE;
	static final int DEFAULT_MAX_IDLE = DEFAULT_MIN_SIZE;
	static final long DEFAULT_KEEP_ALIVE = 60 * 1000;

	//creation delay should avoid bursts 
	static final long DEFAULT_CREATION_DELAY = 1000;
	
	private final Queue queue;
	private final Object lock = new Object();
	
	private int minSize = DEFAULT_MIN_SIZE;
	private int maxSize = DEFAULT_MAX_SIZE;
	private int maxIdle = DEFAULT_MAX_IDLE;
	private long keepAlive = DEFAULT_KEEP_ALIVE;
	private long minCreationDelay = DEFAULT_CREATION_DELAY;

	private int threadCount = 0;
	private long lastCreation = 0;
	private int waiting = 0;
	
	private void createThread() 
	{
		long curtime = System.currentTimeMillis();
		if (threadCount > 0  
			&& minCreationDelay > 0
			&& curtime - lastCreation < minCreationDelay) {
			return;
		}
		
		Thread worker = new Thread(new Worker(),"jforum");
		worker.setDaemon(true);
		worker.start();
		
		lastCreation = curtime;
		++threadCount;
	}
	
	private class Worker extends AbstractWorker 
	{
		protected Object take() throws InterruptedException {			
			synchronized(lock) {
				++waiting;
				
				// Terminate if we have more threads than the limit
				if((threadCount > maxSize && maxSize > 0)
					// Terminate if we have too many idle threads
					// but respect the minSize parameter
					|| (waiting > maxIdle && maxIdle > 0 && threadCount > minSize))
					return null;
			}
			
			try {
				if(keepAlive >= 0) {
					return queue.pool(keepAlive);
				}
				
				return queue.get();
			} finally { 
				synchronized(lock) {
					--waiting;
				}
			}
		}
		
		protected void cleanup() {
			synchronized(lock) {
				if(--threadCount < minSize)
					createThread();
			}
		}
	}

	public PooledExecutor(final Queue queue) {
		this.queue = queue;
	}

	protected void queue(Object obj) throws InterruptedException 
	{
		for(;;) {
			synchronized(lock) {
				if(threadCount < minSize) {
					createThread();
				}
				
				if(queue.offer(obj, 0)) {
					break;
				}
				
				if(threadCount < maxSize) {
					createThread();
				}

				if(queue.offer(obj, 0)) {
					break;
				}
			}
		}
	}

	public void execute(Task task) throws InterruptedException 
	{
		queue(task);
	}

	public Result executeWithResult(Task task) throws InterruptedException 
	{
		SimpleResult result = new SimpleResult(task);
		queue(result);
		
		return result;
	}

	public long getKeepAlive() 
	{
		return keepAlive;
	}

	public int getMaxIdle() 
	{
		return maxIdle;
	}

	public int getMaxSize() {
		return maxSize;
	}

	public long getMinCreationDelay() 
	{
		return minCreationDelay;
	}

	public int getMinSize() 
	{
		return minSize;
	}


	public void setKeepAlive(long l) 
	{
		synchronized(lock) {
			keepAlive = l;
		}
	}

	public void setMaxIdle(int i) 
	{
		synchronized(lock) {
			maxIdle = i;
		}
	}

	public void setMaxSize(int maxSize) 
	{
		synchronized(lock) {
			if(maxSize > 0 && maxSize < minSize) {
				throw new IllegalArgumentException("max size smaller than min size");
			}
			
			this.maxSize = maxSize;
		}
	}

	public void setMinCreationDelay(long l) 
	{
		synchronized(lock) {
			minCreationDelay = l;
		}
	}

	public void setMinSize(int minSize) 
	{
		synchronized(lock) {
			if(minSize < 1 || (maxSize > 0 && maxSize < minSize)) {
				throw new IllegalArgumentException("max size smaller than min size");
			}

			this.minSize = minSize;
		}	
	}

}
