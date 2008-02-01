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
 * Created on Feb 22, 2005 4:54:31 PM
 * The JForum Project
 * http://www.jforum.net
 */
package net.jforum.util.search.simple;

import java.sql.Connection;

import net.jforum.DBConnection;
import net.jforum.dao.DataAccessDriver;
import net.jforum.dao.SearchIndexerDAO;
import net.jforum.entities.Post;
import net.jforum.util.concurrent.Task;

import org.apache.log4j.Logger;

/**
 * @author Rafael Steil
 * @version $Id: MessageIndexerTask.java,v 1.1 2008-01-17 08:04:52 mguo Exp $
 */
public class MessageIndexerTask implements Task
{
	private static Logger logger = Logger.getLogger(MessageIndexerTask.class);
	private Connection conn;
	private Post post;
	
	public MessageIndexerTask(Post post) throws Exception
	{
		this.post = post;
		this.conn = DBConnection.getImplementation().getConnection();
	}

	/**
	 * @see net.jforum.util.concurrent.Task#execute()
	 */
	public Object execute() throws Exception
	{
		try {
			SearchIndexerDAO indexer = DataAccessDriver.getInstance().newSearchIndexerDAO();
			indexer.setConnection(this.conn);

            indexer.insertSearchWords(this.post);
		}
		catch (Exception e) {
			logger.warn("Error while indexing a post: " + e);
			e.printStackTrace();
		}
		finally {
			if (this.conn != null) {
				try {
					DBConnection.getImplementation().releaseConnection(this.conn);
				}
				catch (Exception e) {}
			}
		}
		
		return null;
	}
}
