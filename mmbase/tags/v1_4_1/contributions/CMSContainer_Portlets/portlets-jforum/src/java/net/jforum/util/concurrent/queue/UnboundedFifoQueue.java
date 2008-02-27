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
 * net.jforum.util.concurrent.queue.UnboundedFifoQueue.java
 * The JForum Project
 * http://www.jforum.net
 * 
 * $Id: UnboundedFifoQueue.java,v 1.1 2008-01-17 08:04:52 mguo Exp $
 */
package net.jforum.util.concurrent.queue;

import java.security.InvalidParameterException;
import java.util.LinkedList;

import net.jforum.util.concurrent.Queue;

/**
 * @author Rodrigo Kumpera
 */
public class UnboundedFifoQueue implements Queue 
{
	private final LinkedList queue = new LinkedList();
	
	public UnboundedFifoQueue() { }

	public Object get() throws InterruptedException 
	{
		if(Thread.interrupted()) {
			throw new InterruptedException();
		}
		
		synchronized(queue) {
			try {
				while(queue.isEmpty()) {
					queue.wait();
				}
				
				return queue.removeFirst();
			} 
			catch(InterruptedException e) {
				queue.notify();
				throw e;
			}
		}
	}

	public Object pool(final long timeout) throws InterruptedException
	{
		if(Thread.interrupted()) {
			throw new InterruptedException();
		}
		
		synchronized(queue) {
			if(!queue.isEmpty()) {
				return queue.removeFirst();
			}
			
			if(timeout <= 0) {
				return null;
			}
			
			try {
				long remaining = timeout;
				long start = System.currentTimeMillis();
				
				for(;;) {
					queue.wait(remaining);
					
					if(!queue.isEmpty()) {
						return queue.removeFirst();
					}
					
					remaining = timeout - (System.currentTimeMillis() - start);
					
					if(remaining <= 0) {
						return null;	
					}
				}				
			} 
			catch(InterruptedException e) {
				queue.notify();
				throw e;
			}
		}
	}

	public void put(Object obj) throws InterruptedException 
	{
		offer(obj, 0);
	}

	public boolean offer(Object obj, long timeout) throws InterruptedException 
	{
		if(obj == null) {
			throw new InvalidParameterException("obj is null");
		}
		
		if(Thread.interrupted()) {
			throw new InterruptedException();
		}
		
		synchronized(queue) {
			queue.add(obj);
			queue.notify();
		}
		return true;
	}

	public int size() 
	{
		synchronized(queue) {
			return queue.size();
		}
	}
}
