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
 * net.jforum.util.concurrent.executor.ReadyResult.java
 * The JForum Project
 * http://www.jforum.net
 * 
 * $Id: ReadyResult.java,v 1.1 2008-01-17 08:04:52 mguo Exp $
 */
package net.jforum.util.concurrent.executor;

import net.jforum.util.concurrent.Result;

/**
 * @author Rodrigo Kumpera
 */
class ReadyResult implements Result 
{
	final Object result;
	final Exception exception;

	public ReadyResult(final Object result, final Exception exception) 
	{
		this.result = result;
		this.exception = exception;
	}

	public boolean hasThrown() throws IllegalStateException 
	{
		return exception != null;
	}

	public Object getResult() throws IllegalStateException
	{
		if(hasThrown()) {
			throw new IllegalStateException("task has thrown an exception");
		}
		
		return result;
	}

	public Exception getException() throws IllegalStateException 
	{
		if(!hasThrown()) {
			throw new IllegalStateException("task has not thrown an exception");
		}
			
		return exception;
	}

	public void waitResult() throws InterruptedException 
	{
		if(Thread.interrupted()) {
			throw new InterruptedException();
		}
	}

	public boolean poolResult(long timeout) throws InterruptedException 
	{
		if(Thread.interrupted()) {
			throw new InterruptedException();
		}

		return true;
	}
}