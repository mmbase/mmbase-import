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
 * This file creation date: 13/01/2004 / 11:57:50
 * The JForum Project
 * http://www.jforum.net
 */
package net.jforum.dao;

import java.util.List;

import net.jforum.entities.Smilie;

/**
 * @author Rafael Steil
 * @version $Id: SmilieDAO.java,v 1.1 2008-01-17 08:04:51 mguo Exp $
 */
public interface SmilieDAO 
{
	/**
	 * Insert a new Smilie
	 * 
	 * @param smilie
	 * @return The Smilie id
	 * @throws Exception
	 */
	public int addNew(Smilie smilie) throws Exception;
	
	/**
	 * Deletes a smilie
	 * 
	 * @param id
	 * @throws Exception
	 */
	public void delete(int id) throws Exception;
	
	/**
	 * Updates a smilie
	 * 
	 * @param smilie
	 * @throws Exception
	 */
	public void update(Smilie smilie) throws Exception;
	
	/**
	 * Select all smilies
	 * 
	 * @return
	 * @throws Exception
	 */
	public List selectAll() throws Exception;
	
	/**
	 * Gets some specific smilie.
	 * 
	 * @param id
	 * @return 
	 * @throws Exception
	 */
	public Smilie selectById(int id) throws Exception;
}
