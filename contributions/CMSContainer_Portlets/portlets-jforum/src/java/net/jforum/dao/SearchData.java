/*
 * Copyright (c) Rafael Steil
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
 * This file creation date: 25/02/2004 - 19:16:25
 * net.jforum.model.SearchData.java
 * The JForum Project
 * http://www.jforum.net
 * 
 * $Id: SearchData.java,v 1.1 2008-01-17 08:04:51 mguo Exp $
 */
package net.jforum.dao;

import java.util.Date;

/**
 * @author Rafael Steil
 */
public class SearchData 
{
	private String keywords = "";
	private boolean useAllWords;
	private String author;
	private int forumId;
	private int categoryId;
	private String orderBy = "ASC";
	private String orderByField;
	private Date time;
	private boolean searchStarted;
	
	public void setKeywords(String keywords)
	{
		this.keywords = keywords;
	}
	
	public void setUseAllWords(boolean b)
	{
		this.useAllWords = b;
	}
	
	public void setAuthor(String author)
	{
		this.author = author;
	}
	
	public void setForumId(int forumId)
	{
		this.forumId = forumId;
	}
	
	public void setOrderByField(String f)
	{
		this.orderByField = f;
	}
	
	public void setSearchStarted(boolean started)
	{
		this.searchStarted = started;
	}
	
	public void setCategoryId(int categoryId)
	{
		this.categoryId = categoryId;
	}
	
	public void setOrderBy(String orderBy)
	{
		this.orderBy = (orderBy == null ? "ASC" : orderBy);
	}
	
	public void setTime(Date time) 
	{
		this.time = time;
	}
	
	public String[] getKeywords()
	{
		if (this.keywords == null) {
			return new String[] {};
		}

		return this.keywords.split(" ");
	}
	
	public boolean getUseAllWords()
	{
		return this.useAllWords;
	}
	
	public String getAuthor()
	{
		return this.author;
	}
	
	public int getForumId()
	{
		return this.forumId;
	}
	
	public int getCategoryId()
	{
		return this.categoryId;
	}
	
	public String getOrderBy()
	{
		return this.orderBy;
	}
	
	public String getOrderByField()
	{
		return this.orderByField;
	}
	
	public Date getTime()
	{
		return this.time;
	}
	
	public boolean getSearchStarted()
	{
		return this.searchStarted;
	}
}
