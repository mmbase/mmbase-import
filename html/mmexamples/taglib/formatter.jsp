<%@page language="java" contentType="text/html;charset=UTF-8" 
%><%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" 
%><%-- @ taglib uri="oscache" prefix="cache"
--%><% response.setContentType("text/html; charset=UTF-8");
	java.util.Locale utf = new java.util.Locale("nl", "NL");
	java.util.Locale.setDefault(utf);
 %><html>
<head>
<title>The formatter tag mm:formatter</title>
<link href="style.css" rel="stylesheet" type="text/css" media="screen"  />
</head>
<body>
<cache:cache time="60">
<%@ include file="menu.jsp"%>
<mm:import id="node">xmltest</mm:import>
<mm:cloud>

<mm:node id="ok" referid="node" notfound="skip" />

<mm:notpresent referid="ok">
  <mm:cloud method="http">
     Creating the test node.
	 <mm:createnode id="ok" type="news">
       <mm:setfield name="title">XML Test Node</mm:setfield>
       <mm:setfield name="subtitle">A nice example</mm:setfield>
       <mm:setfield name="body">$An MMXF field can be divided in sections

 _Don't_ forget to add some relations to this node (this is not done  yet automaticly, an application must be created first).

- dogs
- cats
- mice

$Section 2

Be aware that this examples assume that the body of your news builder is of type XML.

</mm:setfield>
     </mm:createnode>
     <mm:node referid="ok">
			<mm:createalias>xmltest</mm:createalias>
     </mm:node>
  </mm:cloud>
</mm:notpresent>

<mm:present referid="ok">

<mm:node referid="ok">
<h1>The formatter tag mm:formatter</h1>
<p>
  The formatter tag is meant to do basic formatting tasks. It's main
  use is to do XSL transformations, but it can do more.
</p>
<p>
  In this page we demonstrate how to use it, and what it can do. On the left you see
  the code. And on the right to what it results.

</p>
<mm:timer>
<form>
<table border="1" width="100%">
<!--
<tr><td colspan="2">Dates. Without this tag, it was not well possible to nicely format them. With this tag, it is easy.</td></tr>
<tr valign="top">
  <td width="50%"><pre><mm:formatter format="escapexml"><mm:include page="codesamples/date.jsp" /></mm:formatter></pre></td>
  <td width="50%"><%@include file="codesamples/date.jsp" %></td>
</tr>
-->
<tr><td colspan="2">Rich text fields. These fields can contain some basic structure information in XML. We can show XML with the formatter tag.</td></tr>
<tr valign="top">
  <td width="50%"><pre><mm:formatter format="escapexml"><mm:include page="codesamples/showrichfield.jsp" /></mm:formatter></pre></td>
  <td width="50%"><textarea><%@include file="codesamples/showrichfield.jsp" %></textarea></td>
</tr>
<tr><td colspan="2">Rich text fields receive their meaning when you convert this XML format to XHTML.</td></tr>
<tr valign="top">
  <td width="50%"><pre><mm:formatter format="escapexml"><mm:include page="codesamples/userichfield.jsp" /></mm:formatter></pre></td>
  <td width="50%"><%@include file="codesamples/userichfield.jsp" %></td>
</tr>
<tr><td colspan="2">In the previous example the default presentation was overridden (by placing a '2xhtml.xslt' in this directory). It is possible to specify the XSLT by hand.</td></tr>
<tr valign="top">
  <td width="50%"><pre><mm:formatter format="escapexml"><mm:include page="codesamples/userichfieldorg.jsp" /></mm:formatter></pre></td>
  <td width="50%"><%@include file="codesamples/userichfieldorg.jsp" %></td>
</tr>
<tr><td colspan="2">You don't like html? Output it as plain text then. The plain text is `enriched' with newlines and so on.</td></tr>
<tr valign="top">
  <td width="50%"><pre><mm:formatter format="escapexml"><mm:include page="codesamples/userichfieldascii.jsp" /></mm:formatter></pre></td>
  <td width="50%"><textarea><%@include file="codesamples/userichfieldascii.jsp" %></textarea></td>
</tr>
<tr><td colspan="2">You can also format a whole node in this way.</td></tr>
<tr valign="top">
  <td width="50%"><pre><mm:formatter format="escapexml"><mm:include page="codesamples/shownode.jsp" /></mm:formatter></pre></td>
  <td width="50%"><%@include file="codesamples/shownode.jsp" %></td>
</tr>
<tr><td colspan="2">It is also possible to specify the XSLT inline. Here we imitate the 'shorted' function.</td></tr>
<tr valign="top">
  <td width="50%"><pre><mm:formatter format="escapexml"><mm:include page="codesamples/shorted.jsp" /></mm:formatter></pre></td>
  <td width="50%"><%@include file="codesamples/shorted.jsp" %></td>
</tr>
<tr><td colspan="2">Of course you can also extend inline XSLT from other XSLT's</td></tr>
<tr valign="top">
  <td width="50%"><pre><mm:formatter format="escapexml"><mm:include page="codesamples/xsltinline.jsp" /></mm:formatter></pre></td>
  <td width="50%"><%@include file="codesamples/xsltinline.jsp" %></td>
</tr>
<tr><td colspan="2">It can treat relations to the node too. We demonstrate here how the 'a' tags in the mmxf field are used. 'Idrel' relations are pointing to them, and we get links in the text like this. We have added a image and some urls to a paragraph as well.</td></tr>
<tr valign="top">
  <td width="50%"><pre><mm:formatter format="escapexml"><mm:include page="codesamples/shownoderelations.jsp" /></mm:formatter></pre></td>
  <td width="50%"><%@include file="codesamples/shownoderelations.jsp" %></td>
</tr>
<tr><td colspan="2">Treating relations is also possible on a field.</td></tr>
<tr valign="top">
  <td width="50%"><pre><mm:formatter format="escapexml"><mm:include page="codesamples/showfieldrelations.jsp" /></mm:formatter></pre></td>
  <td width="50%"><%@include file="codesamples/showfieldrelations.jsp" %></td>
</tr>
<tr><td colspan="2">Perhaps you only want to urls? Then simply only list the relations to that type.</td></tr>
<tr valign="top">
  <td width="50%"><pre><mm:formatter format="escapexml"><mm:include page="codesamples/shownoderelations2.jsp" /></mm:formatter></pre></td>
  <td width="50%"><%@include file="codesamples/shownoderelations2.jsp" %></td>
</tr>
<tr><td colspan="2">Perhaps you are curious about the intermediate XML which was generated in the last step? No problem.</td></tr>
<tr valign="top">
  <td width="50%"><pre><mm:formatter format="escapexml"><mm:include page="codesamples/shownoderelationsxml.jsp" /></mm:formatter></pre></td>
  <td width="50%"><textarea class="huge"><%@include file="codesamples/shownoderelationsxml.jsp" %></textarea></td>
</tr>
<!--
<tr><td colspan="2">Want to try some XSL yourself?</td></tr>
<tr valign="top">
  <td width="50%"><pre><mm:formatter format="escapexml"><mm:include page="codesamples/tryyourself.jsp" /></mm:formatter></pre></td>
  <td width="50%"><%@include file="codesamples/tryyourself.jsp" %></td>
</tr>
-->
</table>
</form>
</mm:timer>
</mm:node>
</mm:present>
</mm:cloud>
</cache:cache>
</body>
</html>