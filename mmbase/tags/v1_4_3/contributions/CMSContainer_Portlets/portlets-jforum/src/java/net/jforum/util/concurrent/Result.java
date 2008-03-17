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
 * This file creation date: Jan 27, 2004
 * net.jforum.util.concurrent.Result.java
 * The JForum Project
 * http://www.jforum.net
 * 
 * $Id: Result.java,v 1.1 2008-01-17 08:04:50 mguo Exp $
 */
package net.jforum.util.concurrent;

/**
 * @author Rodrigo Kumpera
 */
public interface Result 
{	
	/**
	 * Returns if the task has thrown an exception or not
	 * */
	boolean hasThrown() throws IllegalStateException;
	
	/**
	 * Returns the result of the task
	 * */
	Object getResult() throws IllegalStateException;

	/**
	 * Returns the exception thrown by the task
	 * */
	Exception getException() throws IllegalStateException;
	
	/**
	 * Waits the completion of the task
	 * */
	void waitResult() throws IllegalStateException, InterruptedException; 

	/**
	 * Waits the completion of the task
	 * */
	boolean poolResult(long timeout) throws IllegalStateException, InterruptedException; 
}
