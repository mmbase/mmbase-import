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
 * This file creation date: 29/09/2004 - 18:21:51
 * The JForum Project
 * http://www.jforum.net
 */
package net.jforum.util;

import junit.framework.TestCase;
import net.jforum.TestCaseUtils;
import net.jforum.util.preferences.ConfigKeys;
import net.jforum.util.preferences.SystemGlobals;

/**
 * @author Rafael Steil
 * @version $Id: SafeHtmlTest.java,v 1.1 2008-01-17 08:04:52 mguo Exp $
 */
public class SafeHtmlTest extends TestCase
{
	private static final String WELCOME_TAGS = "a, b, i, u, img";
	private String input;
	private String expected;
	
	/** 
	 * @see junit.framework.TestCase#setUp()
	 */
	protected void setUp() throws Exception
	{
		TestCaseUtils.loadEnvironment();
		
		StringBuffer sb = new StringBuffer(512);
		sb.append("<a href='somelink'>Some Link</a>");
		sb.append("bla <b>bla</b> <pre>code code</pre>");
		sb.append("<script>document.location = 'xxx';</script>");
		sb.append("<img src='imgPath' onLoad='window.close();'>");
		sb.append("<a href='javascript:alert(bleh)'>xxxx</a>");
		sb.append("<img src='javascript:alert(bloh)'>");
		sb.append("<img src=\"&#106ava&#115cript&#58aler&#116&#40&#39Oops&#39&#41&#59\">");
		sb.append("\"> TTTTT <");
		this.input = sb.toString();
		
		sb = new StringBuffer(512);
		sb.append("<a href='somelink'>Some Link</a>");
		sb.append("bla <b>bla</b> &lt;pre&gt;code code&lt;/pre&gt;");
		sb.append("&lt;script&gt;document.location = 'xxx';&lt;/script&gt;");
		sb.append("<img src='imgPath' >");
		sb.append("<a href='#'>xxxx</a>");
		sb.append("<img src='#'>");
		sb.append("<img src=\"&amp;#106ava&amp;#115cript&amp;#58aler&amp;#116&amp;#40&amp;#39Oops&amp;#39&amp;#41&amp;#59\">");
		sb.append("&amp;&gt; TTTTT &lt;");
		this.expected = sb.toString();
	}
	
	public void testIframe() 
	{
		String input = "<iframe src='http://www.google.com' onload='javascript:parent.document.body.style.display=\'none\'; alert(\'where is the forum?\'); ' style='display:none;'></iframe>";
		String output = "&lt;iframe src='http://www.google.com' onload='javascript:parent.document.body.style.display=\'none\'; alert(\'where is the forum?\'); ' style='display:none;'&gt;&lt;/iframe&gt;";
		
		SystemGlobals.setValue(ConfigKeys.HTML_TAGS_WELCOME, WELCOME_TAGS);
		assertEquals(output, SafeHtml.makeSafe(input));
	}
	
	public void testMakeSafe() throws Exception
	{
		SystemGlobals.setValue(ConfigKeys.HTML_TAGS_WELCOME, WELCOME_TAGS);
		assertEquals(this.expected, SafeHtml.makeSafe(this.input));
	}
}
