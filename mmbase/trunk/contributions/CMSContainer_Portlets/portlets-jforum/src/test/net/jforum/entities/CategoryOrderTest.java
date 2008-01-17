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
 * Created on 18/12/2004 11:43:02
  * The JForum Project
 * http://www.jforum.net
 */
package net.jforum.entities;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import junit.framework.TestCase;
import net.jforum.ConfigLoader;
import net.jforum.TestCaseUtils;
import net.jforum.repository.ForumRepository;
import net.jforum.repository.SecurityRepository;
import net.jforum.security.PermissionControl;
import net.jforum.security.Role;
import net.jforum.security.RoleCollection;
import net.jforum.security.RoleValue;
import net.jforum.security.SecurityConstants;
import net.jforum.util.CategoryOrderComparator;

/**
 * @author Rafael Steil
 * @version $Id: CategoryOrderTest.java,v 1.1 2008-01-17 07:48:24 mguo Exp $
 */
public class CategoryOrderTest extends TestCase 
{
	private List unorderedCategories = new ArrayList();
	private String[] expectedNames = { "Cat 1", "Cat 2", "Cat 3", "Cat 4", "Cat 5"  };
	private int[] ids = {1, 5, 2, 4, 3};
	
	/** 
	 * @see junit.framework.TestCase#setUp()
	 */
	protected void setUp() throws Exception 
	{
		String[] names = { "Cat 1", "Cat 5", "Cat 2", "Cat 4", "Cat 3" };
		int[] orders = { 1, 5, 2, 4, 3 };
		
		for (int i = 0; i < names.length; i++) {
			Category c = new Category(names[i], this.ids[i]);
			c.setOrder(orders[i]);
			
			this.unorderedCategories.add(c);
		}
		
		TestCaseUtils.loadEnvironment();
		ConfigLoader.startCacheEngine();
	}

	public void testOrder()
	{
		Set categories = new TreeSet(new CategoryOrderComparator());

		for (Iterator iter = this.unorderedCategories.iterator(); iter.hasNext(); ) {
			Category c = (Category)iter.next();
			
			categories.add(c);
		}
		
		this.checkExpectedCategories(this.expectedNames, categories);
	}
	
	public void testUsingRepository()
	{
		this.fillRepository();
		
		List categories = ForumRepository.getAllCategories(1);
		this.checkExpectedCategories(this.expectedNames, categories);
	}
	
	public void testReloadUsingRepository()
	{
		this.fillRepository();
		
		Category c4 = new Category(ForumRepository.getCategory(1, 4));
		c4.setOrder(1);
		ForumRepository.reloadCategory(c4);
		
		String[] names = { "Cat 4", "Cat 2", "Cat 3", "Cat 1", "Cat 5" };
		this.checkExpectedCategories(names, ForumRepository.getAllCategories(1));
		
		Category c2 = new Category(ForumRepository.getCategory(1, 2));
		c2.setOrder(4);
		ForumRepository.reloadCategory(c2);

		names = new String[]{ "Cat 4", "Cat 1", "Cat 3", "Cat 2", "Cat 5" };
		this.checkExpectedCategories(names, ForumRepository.getAllCategories(1));
		
		c4 = new Category(ForumRepository.getCategory(1, 4));
		c4.setOrder(4);
		ForumRepository.reloadCategory(c4);
		this.checkExpectedCategories(new String[] { "Cat 2", "Cat 1", "Cat 3", "Cat 4", "Cat 5" }, ForumRepository.getAllCategories(1));
	}
	
	private void checkExpectedCategories(String[] names, Collection c)
	{
		int i = 0;
		for (Iterator iter = c.iterator(); iter.hasNext();) {
			Category current = (Category)iter.next();
			assertEquals("Category #" + current.getId() + " has a wrong order", 
					names[i++], current.getName());
		}
	}
	
	private void fillRepository()
	{
		for (Iterator iter = this.unorderedCategories.iterator(); iter.hasNext(); ) {
			ForumRepository.addCategory((Category)iter.next());
		}
		
		this.activatePermissionControl(1);
	}
	
	private void activatePermissionControl(int userId)
	{
		int allow = PermissionControl.ROLE_ALLOW;
		int[] rights = { allow, allow, allow, allow, allow };
		
		PermissionControl pc = new PermissionControl();
		RoleCollection rc = new RoleCollection();
		
		// Category
		Role role = new Role();
		role.setId(1);
		role.setName(SecurityConstants.PERM_CATEGORY);
		
		for (int i = 0; i < this.ids.length; i++) {
			RoleValue rv = new RoleValue();
			rv.setType(rights[i]);
			rv.setValue(Integer.toString(this.ids[i]));
			
			role.getValues().add(rv);
		}
		
		rc.add(role);
		pc.setRoles(rc);
		
		SecurityRepository.add(userId, pc);
	}
}
