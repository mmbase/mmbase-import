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
 * This file creation date: 15/08/2003 / 21:03:31
 * The JForum Project
 * http://www.jforum.net
 */
package net.jforum.dao;

import java.util.List;

import net.jforum.entities.Config;


/**
 * Model interface for the {@link net.jforum.Config} class. 
 * 
 * @author Rafael Steil
 * @version $Id: ConfigDAO.java,v 1.1 2008-01-17 07:48:24 mguo Exp $
 */
public interface ConfigDAO 
{
	/**
	 * Insert a new configuration.
	 * 
	 * @param config The data to store.
	 * @throws Exception
	 */
	public void insert(Config config) throws Exception;
	
	/**
	 * Updates some config entry
	 * 
	 * @param config The entry to update
	 * @throws Exception
	 */
	public void update(Config config) throws Exception;

	/**
	 * Deletes some specific configuration
	 * 
	 * @param config The config to delete
	 * @throws Exception
	 */
	public void delete(Config config) throws Exception;
	
	/**
	 * Gets all existing configuration entries
	 * 
	 * @return <code>java.util.List</code> with all records found. 
	 * Each entry is a <code>net.jforum.entities.Config</code> instance.
	 * @throws Exception
	 */
	public List selectAll() throws Exception;
	
	/**
	 * Gets a config by its name
	 * 
	 * @param name The name to search for.
	 * @return
	 * @throws Exception
	 */
	public Config selectByName(String name) throws Exception;
}
