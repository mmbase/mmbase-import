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
 * Created on Feb 22, 2005 4:24:18 PM
 * The JForum Project
 * http://www.jforum.net
 */
package net.jforum.dao.generic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;
import net.jforum.entities.Post;
import net.jforum.util.preferences.ConfigKeys;
import net.jforum.util.preferences.SystemGlobals;
import org.apache.log4j.Logger;

/**
 * @author Rafael Steil
 * @version $Id: GenericSearchIndexerDAO.java,v 1.1 2008-01-17 08:04:50 mguo Exp $
 */
public class GenericSearchIndexerDAO extends AutoKeys implements net.jforum.dao.SearchIndexerDAO
{
	private static final Logger log = Logger.getLogger(GenericSearchIndexerDAO.class);
	
	private Connection conn;
	
	/**
	 * @see net.jforum.dao.SearchIndexerDAO#setConnection(java.sql.Connection)
	 */
	public void setConnection(Connection conn)
	{
		this.conn = conn;
	}
	
	/**
	 * @see net.jforum.dao.SearchIndexerDAO#indexSearchWords(java.util.List)
	 */
	public void insertSearchWords(List posts) throws Exception
	{
		int minWordSize = SystemGlobals.getIntValue(ConfigKeys.SEARCH_MIN_WORD_SIZE);
		int maxWordSize = SystemGlobals.getIntValue(ConfigKeys.SEARCH_MAX_WORD_SIZE);
		int searchMaxWordsMessage = SystemGlobals.getIntValue(ConfigKeys.SEARCH_MAX_WORDS_MESSAGE);
		String wordFilterRegex = SystemGlobals.getValue(ConfigKeys.SEARCH_WORD_FILTER_REGEX);
		StringBuffer sb = new StringBuffer(512);
		
		// Allow for a set of words to be excluded from indexing...
		String excludeWordsString = SystemGlobals.getValue("search.exclude.words");
		Set excludeWords = new HashSet();
		if (excludeWordsString != null) {
			String[] words = excludeWordsString.split(",");
			for (int i = 0; words != null && i < words.length; i++) {
				excludeWords.add(words[i].trim());
			}
		}
		
		String matchSql = SystemGlobals.getSql("SearchModel.associateWordToPost");
		PreparedStatement words = this.conn.prepareStatement(SystemGlobals.getSql("SearchModel.insertWords"));
		
		for (Iterator iter = posts.iterator(); iter.hasNext(); ) {
			Post p = (Post)iter.next();

			String text = new StringBuffer(p.getText()).append(" ")
				.append(p.getSubject()).toString();
			
			text = text.toLowerCase().replaceAll("[\\.\\\\\\/~\\^\\&\\(\\)\\-_+=!@;#\\$%\"\'\\[\\]\\{\\}\\?<\\:>,\\*\n\r\t]", " ");

			Set allWords = new HashSet();

			sb.delete(0, sb.length());
			
			StringTokenizer st = new StringTokenizer(text, " ");
			
			// Go through all words
			while (st.hasMoreTokens() && 
					(searchMaxWordsMessage < 1 || allWords.size() < searchMaxWordsMessage)) {
				String w = st.nextToken().trim();
				
				if (w.length() < minWordSize) {
					continue;
				}
				else if (w.length() > maxWordSize) {
					w = w.substring(0, maxWordSize);
				}
				
				if (!allWords.contains(w) && !excludeWords.contains(w) && 
						(wordFilterRegex == null || w.matches(wordFilterRegex))) {
					allWords.add(w);
					sb.append('\'').append(w).append('\'').append(",");
				}
			}

			String in = sb.substring(0, sb.length() - 1);
			
			String sql = SystemGlobals.getSql("SearchModel.selectExistingWords");
			sql = sql.replaceAll("#IN#", in);
			
			Statement s = this.conn.createStatement();
			ResultSet rs = s.executeQuery(sql);
			
			List newWords = new ArrayList();
			
			while (rs.next()) {
				newWords.add(rs.getString("word"));
			}
			
			rs.close();
			s.close();
			
			allWords.removeAll(newWords);
			
			// Insert the remaining words
			for (Iterator witer = allWords.iterator(); witer.hasNext(); ) {
				String ww = (String)witer.next();
				
				words.setString(1, ww);
				words.setInt(2, ww.hashCode());
				
				try {
					words.executeUpdate();
				} 
				catch (SQLException e) {
					log.error("Cannot index word: \"" + ww + "\"", e);
					throw e;
				}
			}
			
			sql = matchSql.replaceAll("#ID#", String.valueOf(p.getId())).replaceAll("#IN#", in);
			
			Statement match = this.conn.createStatement();
			match.executeUpdate(sql);
			match.close();
		}
		
		words.close();
	}
	
	/**
	 * @see net.jforum.dao.SearchIndexerDAO#insertSearchWords(net.jforum.entities.Post)
	 */
	public void insertSearchWords(final Post post) throws Exception
	{
		this.insertSearchWords(Arrays.asList(new Post[] { post }));
	}
}
