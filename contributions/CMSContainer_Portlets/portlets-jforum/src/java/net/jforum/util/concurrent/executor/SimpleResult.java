/*
 * Copyright (c) 2003, Rafael Steil
 * All rights reserved.

 * Redistribution and use in source and binary forms, 
 * with or without modification, are permitted provided 
 * that the following conditions are met:

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
 * net.jforum.util.concurrent.executor.SimpleResult.java
 * The JForum Project
 * http://www.jforum.net
 * 
 * $Id: SimpleResult.java,v 1.1 2008-01-17 08:04:52 mguo Exp $
 */
package net.jforum.util.concurrent.executor;

import net.jforum.util.concurrent.Result;
import net.jforum.util.concurrent.Task;

/**
 * @author Rodrigo Kumpera
 */
class SimpleResult implements Result 
{
	private final Task task;
	private Exception exception;
	private Object result;
	private boolean ready = false;
	private final Object lock = new Object();
	
	SimpleResult(Task task) 
	{
		this.task = task;
	}		

	public boolean hasThrown() throws IllegalStateException 
	{
		synchronized(lock) {
			if(!ready) {
				throw new IllegalStateException("task has not completed");
			}
			
			return exception != null;
		}
	}

	public Object getResult() throws IllegalStateException 
	{
		synchronized(lock) {
			if(!ready) {
				throw new IllegalStateException("task has not completed");
			}
				
			if(exception != null) {
				throw new IllegalStateException("task has thrown an exception");
			}
			
			return result;
		}
	}

	public Exception getException() throws IllegalStateException 
	{
		synchronized(lock) {
			if(!ready) {
				throw new IllegalStateException("task has not completed");
			}
			
			if(exception == null) {
				throw new IllegalStateException("task has not thrown an exception");
			}
			
			return exception;
		}
	}

	public synchronized void waitResult() throws InterruptedException 
	{
		if(Thread.interrupted()) {
			throw new InterruptedException();
		}
		
		synchronized(lock) {
			if(ready) {
				return;
			}
			
			while(!ready) {
				lock.wait();
			}
		}
	}

	public synchronized boolean poolResult(long timeout) throws InterruptedException 
	{
		if(Thread.interrupted()) {
			throw new InterruptedException();
		}
		
		synchronized(lock) {
			if(ready) {
				return true;
			}
			
			if(timeout <= 0) {
				return false;
			}

			long remaining = timeout;
			long start = System.currentTimeMillis();
			
			for(;;) {
				lock.wait(remaining);
				if(ready) {
					return true;
				}
				
				remaining = timeout - (System.currentTimeMillis() - start);
				if(remaining <= 0) {
					return false;
				}
			}
		}
	}

	void setException(Exception exception) 
	{
		synchronized(lock) {
			if(ready) {
				throw new IllegalStateException("task allready completed");
			}
			
			this.exception = exception;
			ready = true;
			lock.notifyAll();
		}
	}

	void setResult(Object result) 
	{
		synchronized(lock) {
			if(ready) {
				throw new IllegalStateException("task allready completed");
			}
			
			this.ready = true;
			this.result = result;
			lock.notifyAll();
		}
	}

	Task getTask() 
	{
		return task;
	}

}