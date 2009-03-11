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
 * Created on 02/12/2005 19:23:32
 * The JForum Project
 * http://www.jforum.net
 */
package net.jforum.entities;

import junit.framework.TestCase;

/**
 * @author Rafael Steil
 * @version $Id: PoolChangesTest.java,v 1.1 2008-01-17 08:04:52 mguo Exp $
 */
public class PoolChangesTest extends TestCase
{
	public void testLabelOnlyShouldHaveChanged()
	{
		Poll p1 = new Poll();
		p1.setLabel("Label");
		p1.addOption(new PollOption(1, "Option 1", 0));
		
		Poll p2 = new Poll();
		p2.setLabel("Label 2");
		p2.addOption(new PollOption(1, "Option 1", 0));
		
		PollChanges changes = new PollChanges(p1, p2);

		assertTrue(changes.hasChanges());
		assertEquals(0, changes.getChangedOptions().size());
		assertEquals(0, changes.getDeletedOptions().size());
		assertEquals(0, changes.getNewOptions().size());
	}
	
	public void testShouldHave1Update()
	{
		Poll p1 = new Poll();
		p1.setLabel("Label");
		p1.addOption(new PollOption(1, "Option 1", 0));
		p1.addOption(new PollOption(2, "Option 2", 0));
		
		Poll p2 = new Poll();
		p2.setLabel("Label");
		p2.addOption(new PollOption(1, "Option 1", 0));
		p2.addOption(new PollOption(2, "Option 2 changed", 0));
		
		PollChanges changes = new PollChanges(p1, p2);

		assertTrue(changes.hasChanges());
		assertEquals(1, changes.getChangedOptions().size());
		assertEquals(new PollOption(2, "Option 2 changed", 0), changes.getChangedOptions().get(0));
	}
	
	public void testShouldHave3NewOptions()
	{
		Poll p1 = new Poll();
		p1.setLabel("Label");
		p1.addOption(new PollOption(1, "Option 1", 0));
		
		Poll p2 = new Poll();
		p2.setLabel("Label");
		p2.addOption(new PollOption(1, "Option 1", 0));
		p2.addOption(new PollOption(2, "Option 2", 0));
		p2.addOption(new PollOption(3, "Option 3", 0));
		p2.addOption(new PollOption(4, "Option 4", 0));
		
		PollChanges changes = new PollChanges(p1, p2);
		
		assertTrue(changes.hasChanges());
		assertEquals(3, changes.getNewOptions().size());
		
		assertEquals(new PollOption(2, "Option 2", 0), changes.getNewOptions().get(0));
		assertEquals(new PollOption(3, "Option 3", 0), changes.getNewOptions().get(1));
		assertEquals(new PollOption(4, "Option 4", 0), changes.getNewOptions().get(2));
	}
	
	public void testShouldHave2DeletedOptions()
	{
		Poll p1 = new Poll();
		p1.setLabel("Label");
		p1.addOption(new PollOption(1, "Option 1", 0));
		p1.addOption(new PollOption(2, "Option 2", 0));
		p1.addOption(new PollOption(3, "Option 3", 0));
		p1.addOption(new PollOption(4, "Option 4", 0));
		
		Poll p2 = new Poll();
		p2.setLabel("Label");
		p2.addOption(new PollOption(1, "Option 1", 0));
		p2.addOption(new PollOption(2, "Option 2", 0));
		
		PollChanges changes = new PollChanges(p1, p2);
		
		assertTrue(changes.hasChanges());
		assertEquals(2, changes.getDeletedOptions().size());
		
		assertEquals(new PollOption(3, "Option 3", 0), changes.getDeletedOptions().get(0));
		assertEquals(new PollOption(4, "Option 4", 0), changes.getDeletedOptions().get(1));
	}
	
	public void testShouldHaveNoChanges()
	{
		Poll p1 = new Poll();
		p1.setLabel("Label");
		p1.addOption(new PollOption(1, "Option 1", 0));
		p1.addOption(new PollOption(2, "Option 2", 0));
		
		Poll p2 = new Poll();
		p2.setLabel("Label");
		p2.addOption(new PollOption(1, "Option 1", 0));
		p2.addOption(new PollOption(2, "Option 2", 0));
		
		PollChanges changes = new PollChanges(p1, p2);
		assertFalse(changes.hasChanges());
	}
}
