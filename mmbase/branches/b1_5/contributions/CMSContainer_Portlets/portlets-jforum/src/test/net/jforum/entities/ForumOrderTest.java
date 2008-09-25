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
 * Created on 17/11/2004 22:03:01
 * The JForum Project
 * http://www.jforum.net
 */
package net.jforum.entities;

import java.util.Iterator;

import junit.framework.TestCase;
import net.jforum.ConfigLoader;
import net.jforum.TestCaseUtils;
import net.jforum.repository.ForumRepository;
import net.jforum.security.PermissionControl;

/**
 * @author Rafael Steil
 * @version $Id: ForumOrderTest.java,v 1.1 2008-01-17 07:48:24 mguo Exp $
 */
public class ForumOrderTest extends TestCase 
{
	private Category category;

	/**
	 * @see TestCase#setUp()
	 */
	protected void setUp() throws Exception 
	{
		TestCaseUtils.loadEnvironment();
		ConfigLoader.startCacheEngine();
		
		this.category = new CategoryTestCommon().createCategoryAndForums();
	}
	
	public void testForumOrder()
	{
		String[] expectedNames = { "Forum 1", "Forum 2", "Forum 3", "Forum 4", "Forum 5" };
		this.checkExpectedNames(expectedNames);
	}
	
	public void testReload()
	{
		Forum f1 = new Forum(this.category.getForum(1));
		f1.setOrder(3);
		this.category.changeForumOrder(f1);
		this.checkExpectedNames(new String[] { "Forum 3", "Forum 2", "Forum 1", "Forum 4", "Forum 5" });
		
		assertEquals(5, this.category.getForums().size());
		
		Forum f5 = new Forum(this.category.getForum(5));
		f5.setOrder(2);
		this.category.changeForumOrder(f5);
		this.checkExpectedNames(new String[] { "Forum 3", "Forum 5", "Forum 1", "Forum 4", "Forum 2" });
		
		assertEquals(5, this.category.getForums().size());
		
		f1 = new Forum(this.category.getForum(1));
		f1.setOrder(1);
		this.category.changeForumOrder(f1);
		this.checkExpectedNames(new String[] { "Forum 1", "Forum 5", "Forum 3", "Forum 4", "Forum 2" });
	}
	
	public void testReloadUsingRepository()
	{
		this.configureRepository();
		
		Forum f = new Forum(ForumRepository.getForum(3));
		f.setOrder(1);
		ForumRepository.getCategory(this.pc(), f.getCategoryId()).changeForumOrder(f);
		this.checkExpectedNames(new String[] { "Forum 3", "Forum 2", "Forum 1", "Forum 4", "Forum 5" });
		assertEquals(5, ForumRepository.getCategory(this.pc(), f.getCategoryId()).getForums().size());
		
		f = new Forum(ForumRepository.getForum(4));
		f.setOrder(2);
		ForumRepository.getCategory(this.pc(), f.getCategoryId()).changeForumOrder(f);
		this.checkExpectedNames(new String[] { "Forum 3", "Forum 4", "Forum 1", "Forum 2", "Forum 5" });
		assertEquals(5, ForumRepository.getCategory(this.pc(), f.getCategoryId()).getForums().size());
	}
	
	private void checkExpectedNames(String[] expectedNames)
	{
		int i = 0; 
		for (Iterator iter = this.category.getForums().iterator(); iter.hasNext(); ) {
			Forum f = (Forum)iter.next();
			assertEquals(expectedNames[i++], f.getName());
		}
	}
	
	private void configureRepository()
	{
		ForumRepository.addCategory(this.category);
	}
	
	private PermissionControl pc()
	{
		return new PermissionControl() {
			public boolean canAccess(String roleName, String roleValue) {
				return true;
			}
		};
	}
}
