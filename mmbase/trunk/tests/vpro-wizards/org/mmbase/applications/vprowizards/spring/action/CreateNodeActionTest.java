package org.mmbase.applications.vprowizards.spring.action;

import java.net.URL;
import java.util.*;

import org.mmbase.tests.MMBaseTest;

public class CreateNodeActionTest extends MMBaseTest {
	static{
		System.setProperty("mmbase.config", "/home/ebunders/workspaces/workspace/mmbasehead/tests/config");
		System.setProperty("user.dir", "/home/ebunders/workspaces/workspace/mmbasehead/tests/work");
	}

	public void testSomething(){
		try {
			startMMBase(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
//		URL url =  this.getClass().getClassLoader().getResource("/org/mmbase/storage/implementation/database/resources/lookup.xml");
//		if(url == null){
//			fail();
//		}
		
	}
}
