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
 * Created on 13/11/2004 11:44:48
 * The JForum Project
 * http://www.jforum.net
 */
package net.jforum.repository;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import junit.framework.TestCase;
import net.jforum.ConfigLoader;
import net.jforum.JForumExecutionContext;
import net.jforum.SessionFacade;
import net.jforum.TestCaseUtils;
import net.jforum.dao.CategoryDAO;
import net.jforum.dao.ForumDAO;
import net.jforum.entities.Category;
import net.jforum.entities.Forum;
import net.jforum.security.PermissionControl;
import net.jforum.view.forum.common.ForumCommon;

/**
 * @author Rafael Steil
 * @version $Id: ForumRepositoryTest.java,v 1.1 2008-01-17 07:48:25 mguo Exp $
 */
public class ForumRepositoryTest extends TestCase 
{
	// Categories
	private static final String PRIVATE_CATEGORY = "Private Category";
	private static final String OPEN_CATEGORY = "Open Category";
	private static final String MIXED_CATEGORY = "Mixed Category";
	
	private static final int PRIVATE_CATEGORY_ID = 1;
	private static final int OPEN_CATEGORY_ID = 2;
	private static final int MIXED_CATEGORY_ID = 3;
	
	// Forums
	private static final String PRIVATE_FORUM_1 = "Private Forum 1";
	private static final String OPEN_FORUM_1 = "Open Forum 1";
	private static final String OPEN_FORUM_2 = "Open Forum 2";
	private static final String MIXED_PRIVATE_FORUM = "Mixed Private Forum";
	private static final String MIXED_OPEN_FORUM = "Mixed Open Forum";
	
	private static final int PRIVATE_FORUM_1_ID = 1;
	private static final int OPEN_FORUM_1_ID = 2;
	private static final int OPEN_FORUM_2_ID = 3;
	private static final int MIXED_PRIVATE_FORUM_ID = 4;
	private static final int MIXED_OPEN_FORUM_ID = 5;
	
	private static final int PF_1_CAT = PRIVATE_CATEGORY_ID;
	private static final int OF_1_CAT = OPEN_CATEGORY_ID;
	private static final int OF_2_CAT = OPEN_CATEGORY_ID;
	private static final int MF_P_CAT = MIXED_CATEGORY_ID;
	private static final int MF_O_CAT = MIXED_CATEGORY_ID;
	
	private static boolean loaded = false;
	
	private static CategoryDAO cm;
	private static ForumDAO fm;
	
	private static String[] categoryNames;
	private static int[] categoryIds;
	private static String[] forumNames;
	private static int[] forumIds;
	private static int[] forumCatIds;
	
	private static final int SUPER_USER = 1;
	private static final int GENERAL_USER = 2;
	
	private static int[] superUserCategoryRights;
	private static int[] generalUserCategoryRights;
	
	private static int[] superUserForumRights;
	private static int[] generalUserForumRights;
	
	/** 
	 * @see junit.framework.TestCase#setUp()
	 */
	protected void setUp() throws Exception 
	{
		if (!loaded) {
			categoryNames = new String[] { PRIVATE_CATEGORY, OPEN_CATEGORY, MIXED_CATEGORY };
			categoryIds = new int[] { PRIVATE_CATEGORY_ID, OPEN_CATEGORY_ID, MIXED_CATEGORY_ID };
			
			forumNames = new String[] { PRIVATE_FORUM_1, OPEN_FORUM_1, OPEN_FORUM_2, 
					MIXED_PRIVATE_FORUM, MIXED_OPEN_FORUM };
			forumIds = new int[] { PRIVATE_FORUM_1_ID, OPEN_FORUM_1_ID, OPEN_FORUM_2_ID, 
					MIXED_PRIVATE_FORUM_ID, MIXED_OPEN_FORUM_ID };
			forumCatIds = new int[] { PF_1_CAT, OF_1_CAT, OF_2_CAT, MF_P_CAT, MF_O_CAT };
			
			// Permissions
			int allow = PermissionControl.ROLE_ALLOW;
			int deny = PermissionControl.ROLE_DENY;

			superUserCategoryRights = new int[] { allow, allow, allow };
			superUserForumRights = new int[] { allow, allow, allow, allow, allow };
			
			generalUserCategoryRights = new int[] { deny, allow, allow };
			generalUserForumRights = new int[] { deny, allow, allow, deny, allow };
			
			cm = this.createCategoryModel();
			fm = this.createForumModel();
			
			TestCaseUtils.loadEnvironment();
			ConfigLoader.startCacheEngine();
			
			SecurityRepository.add(SUPER_USER, TestCaseUtils.createForumCategoryPermissionControl(categoryIds, superUserCategoryRights, 
					forumIds, superUserForumRights));
			
			SecurityRepository.add(GENERAL_USER, TestCaseUtils.createForumCategoryPermissionControl(categoryIds, generalUserCategoryRights, 
					forumIds, generalUserForumRights));
			
			loaded = true;
		}
		
		TestCaseUtils.createThreadLocalData(SUPER_USER);
	}
	
	/** 
	 * @see junit.framework.TestCase#tearDown()
	 */
	protected void tearDown() throws Exception 
	{
		JForumExecutionContext.finish();
	}
	
	/*
	 * Repository startup
	 */
	public void testStart() throws Exception
	{
		ForumRepository.start(fm, cm, new ConfigModelDummy());
		
		// Categories
		for (int i = 0; i < categoryIds.length; i++) {
			assertNotNull("Cache does not contains category #" + categoryIds[i], 
					ForumRepository.getCategory(categoryIds[i]));
		}
		
		// Forums
		for (int i = 0; i < forumNames.length; i++) {
			Forum f = ForumRepository.getForum(forumIds[i]);
			assertTrue("Forum #" + forumIds[i] + " not found", f != null);
			assertEquals("Forum #" + f.getId() + " has an invalid category id", forumCatIds[i], f.getCategoryId());
		}
	}
	
	/*
	 * Check if the forums are associated to the 
	 * right categories
	 */
	public void testCategoryForums() 
	{
		List categories = ForumRepository.getAllCategories(SUPER_USER);
		for (Iterator iter = categories.iterator(); iter.hasNext(); ) {
			Category c = (Category)iter.next();
			
			for (Iterator forumIter = c.getForums().iterator(); forumIter.hasNext(); ) {
				Forum f = (Forum)forumIter.next();
				
				int index = this.getForumArrayIndex(f.getId());
				assertTrue("Wrong index for forum #" + f.getId(), index > -1);
				
				assertEquals("Forum #" + f.getId() + " has an invalid category id", forumCatIds[index], f.getCategoryId());
			}
		}
	}

	/*
	 * Check access rights for an user with full access to the categories
	 */
	public void testSuperCategoryUserRights()
	{
		List categories = ForumRepository.getAllCategories(SUPER_USER);
		assertEquals(categoryNames.length, categories.size());
		
		for (int i = 0; i < categoryNames.length; i++) {
			Category c = new Category(categoryNames[i], categoryIds[i]);
			assertTrue("Cached categories does not contain category #" + c.getId(), categories.contains(c));
		}
	}
	
	/*
	 * Gest access right for an user with full access to the forums
	 */
	public void testSuperUserForumRights()
	{
		List categories = ForumRepository.getAllCategories(SUPER_USER);
		
		for (Iterator iter = categories.iterator(); iter.hasNext(); ) {
			Category c = (Category)iter.next();
			
			for (int i = 0; i < forumNames.length; i++) {
				int index = this.getForumArrayIndex(forumIds[i]);
				assertTrue(index > -1);
				
				int catId = forumCatIds[index];
				if (catId != c.getId()) {
					continue;
				}
				
				Forum f = new Forum();
				f.setId(forumIds[i]);
				
				assertNotNull("Category #" + c.getId() + " does not contain forum #" + f.getId(), c.getForum(f.getId()));
			}
		}
	}
	
	/*
	 * Check access rights for the regular user
	 */
	public void testGeneralUserCategoryRights()
	{
		List categories = ForumRepository.getAllCategories(GENERAL_USER);
		
		for (int i = 0; i < generalUserCategoryRights.length; i++) {
			if (generalUserCategoryRights[i] == PermissionControl.ROLE_ALLOW) {
				Category c = new Category(categoryNames[i], categoryIds[i]);
				assertTrue("Cached category list does not contains category #" + i, categories.contains(c));
			}
		}
	}
	
	/*
	 * Forums allowed to the general user
	 */
	public void testGeneralUserForumRights()
	{
		List categories = ForumRepository.getAllCategories(GENERAL_USER);
		assertEquals(2, categories.size());
		
		Category c = new Category(null, PRIVATE_CATEGORY_ID);
		assertFalse(categories.contains(c));
		
		c = new Category(null, OPEN_CATEGORY_ID);
		assertTrue(categories.contains(c));
		
		c = new Category(null, MIXED_CATEGORY_ID);
		assertTrue(categories.contains(c));
		
		// Open Category
		c = ForumRepository.getCategory(GENERAL_USER, OPEN_CATEGORY_ID);
		assertEquals(2, c.getForums().size());
		
		assertNotNull(c.getForum(OPEN_FORUM_1_ID));
		assertNotNull(c.getForum(OPEN_FORUM_2_ID));
		
		// Mixed Category
		assertTrue(ForumRepository.isCategoryAccessible(GENERAL_USER, MIXED_CATEGORY_ID));
		c = ForumRepository.getCategory(GENERAL_USER, MIXED_CATEGORY_ID);
		assertEquals(1, c.getForums(GENERAL_USER).size());
		
		assertNotNull(c.getForum(MIXED_OPEN_FORUM_ID));
		assertFalse(ForumRepository.isForumAccessible(GENERAL_USER, c.getId(), MIXED_PRIVATE_FORUM_ID));
		
		// Private category
		assertFalse(ForumRepository.isCategoryAccessible(GENERAL_USER, PRIVATE_CATEGORY_ID));
		c = ForumRepository.getCategory(GENERAL_USER, PRIVATE_CATEGORY_ID);
		assertNull(c);
	}
	
	/*
	 * Test the method which gets all categories and
	 * forums available to the user. It should not
	 * change internal repository structure.
	 */
	public void testCategoriesAndForumsForListing() throws Exception
	{
		SessionFacade.getUserSession().setUserId(GENERAL_USER);
		SessionFacade.getUserSession().setLastVisit(new Date());

		List categories = ForumCommon.getAllCategoriesAndForums(SessionFacade.getUserSession(), -1, null, false);
		assertEquals(2, categories.size());
		
		assertEquals(2, ForumRepository.getAllCategories(GENERAL_USER).size());
		Category c = ForumRepository.getCategory(GENERAL_USER, MIXED_CATEGORY_ID);
		assertEquals(1, c.getForums().size());
		
		c = new Category(null, PRIVATE_CATEGORY_ID);
		assertFalse(categories.contains(c));
		
		c.setId(MIXED_CATEGORY_ID);
		int index = categories.indexOf(c);
		assertTrue(index > -1);
		c = (Category)categories.get(index);
		
		Forum f = new Forum();
		f.setName("");
		f.setId(MIXED_PRIVATE_FORUM_ID);
		assertFalse(c.getForums().contains(f));
		
		f = new Forum();
		f.setId(MIXED_OPEN_FORUM_ID);
		assertEquals(f, c.getForum(MIXED_OPEN_FORUM_ID));
	}
	
	/**
	 * Gets the position of the given forum id in the
	 * configuration array
	 * @param forumId The forum id to search for the position
	 * @return The position into the array, or -1 if not found.
	 */
	private int getForumArrayIndex(int forumId)
	{
		for (int i = 0; i < forumNames.length; i++) {
			if (forumIds[i] == forumId) {
				return i;
			}
		}
		
		return -1;
	}
	
	private CategoryDAO createCategoryModel()
	{
		return new CategoryModelDummy() {
			private List categories = new ArrayList(); 

			{
				for (int i = 0; i < categoryNames.length; i++) {
					this.categories.add(new Category(categoryNames[i], categoryIds[i])); 
				}
			}
			
			public List selectAll() throws Exception {
				return this.categories;
			}
		};
	}
	
	private ForumDAO createForumModel()
	{
		return new ForumModelDummy() {
			private List forums = new ArrayList();
			
			{
				for (int i = 0; i < forumNames.length; i++) {
					Forum f = new Forum();
					f.setId(forumIds[i]);
					f.setName(forumNames[i]);
					f.setIdCategories(forumCatIds[i]);
					
					this.forums.add(f);
				}
			}
			
			public List selectAll() throws Exception {
				return this.forums;
			}
		};
	}
}