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
 * Created on 08/12/2004 23:18:22
 * The JForum Project
 * http://www.jforum.net
 */
package net.jforum.entities;

import java.util.Iterator;

import junit.framework.TestCase;
import net.jforum.ConfigLoader;
import net.jforum.TestCaseUtils;

/**
 * Test some general <code>net.jforum.entities.Category</code> methods.
 * This is not some best practice test case, since it relies on the
 * ThreadLocal settings and system roles. But, anyway, it helps
 * to test :)
 * 
 * @author Rafael Steil
 * @version $Id: CategoryTest.java,v 1.1 2008-01-17 07:48:24 mguo Exp $
 */
public class CategoryTest extends TestCase 
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
	
	public void testReloadForum()
	{
		Forum f1 = this.category.getForum(1);
		Forum f2 = this.category.getForum(4);
		
		// Now, change its order and names
		f1.setName("Forum 1 changed");
		f1.setOrder(4);
		
		f2.setName("Forum 4 changed");
		f2.setOrder(1);
		
		// Updates and check if the order changed as well
		this.category.reloadForum(f1);
		
		String[] expectedNames = { "Forum 4 changed", "Forum 2", "Forum 3", "Forum 1 changed", "Forum 5" };
		this.checkExpectedNames(expectedNames);
	}
	
	public void testRemoveForum()
	{
		this.category.removeForum(3);
		this.checkExpectedNames(new String[]{ "Forum 1", "Forum 2", "Forum 4", "Forum 5" });
	}
	
	private void checkExpectedNames(String[] expectedNames)
	{
		int i = 0; 
		for (Iterator iter = this.category.getForums().iterator(); iter.hasNext(); ) {
			Forum f = (Forum)iter.next();
			assertEquals(expectedNames[i++], f.getName());
		}
	}
}
