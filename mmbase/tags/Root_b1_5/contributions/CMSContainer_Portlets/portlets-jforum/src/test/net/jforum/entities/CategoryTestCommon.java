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
 * Created on 08/12/2004 23:20:44
 * The JForum Project
 * http://www.jforum.net
 */
package net.jforum.entities;

import net.jforum.TestCaseUtils;
import net.jforum.repository.SecurityRepository;
import net.jforum.security.PermissionControl;

/**
 * Common methods for testing categories.
 * 
 * @author Rafael Steil
 * @version $Id: CategoryTestCommon.java,v 1.1 2008-01-17 07:48:24 mguo Exp $
 */
public class CategoryTestCommon 
{
	/**
	 * Creates a <code>net.jforum.entities.Category</code> instance 
	 * and populates it with some forums. 
	 * The roles are set to "allow all" to an user_id equals to "1"
	 * 
	 * @return The category instance with all forums and roles set
	 * @throws Exception
	 */
	public Category createCategoryAndForums() throws Exception
	{
		Category category = new Category();
		
		int allow = PermissionControl.ROLE_ALLOW;
		int super_user = 1;
		
		int[] categoryIds = { 1 };
		int[] categoryRights = { allow };
		
		int[] forumIds = { 1, 2, 3, 4, 5 };
		int[] forumRights = { allow, allow, allow, allow, allow };
		
		SecurityRepository.add(super_user, TestCaseUtils.createForumCategoryPermissionControl(categoryIds, categoryRights, 
				forumIds, forumRights));

		TestCaseUtils.loadEnvironment();
		TestCaseUtils.createThreadLocalData(super_user);
		
		category = new Category();
		category.setId(1);
		
		Forum f = new Forum();
		f.setId(2);
		f.setIdCategories(1);
		f.setName("Forum 2");
		f.setOrder(2);
		category.addForum(f);
		
		f = new Forum();
		f.setId(5);
		f.setIdCategories(1);
		f.setName("Forum 5");
		f.setOrder(5);
		category.addForum(f);
		
		f = new Forum();
		f.setId(1);
		f.setIdCategories(1);
		f.setName("Forum 1");
		f.setOrder(1);
		category.addForum(f);
		
		f = new Forum();
		f.setId(3);
		f.setIdCategories(1);
		f.setName("Forum 3");
		f.setOrder(3);
		category.addForum(f);
		
		f = new Forum();
		f.setId(4);
		f.setIdCategories(1);
		f.setName("Forum 4");
		f.setOrder(4);
		category.addForum(f);
		
		return category;
	}
}
