<%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" 
%><%-- @ taglib uri="oscache" prefix="cache" --%><%@page errorPage="error.jsp" session="false"%>
<mm:content type="text/html" expires="300" postprocessor="none">
<html>
<head>
 <title>The formatter tag mm:formatter</title>
<link href="style.css" rel="stylesheet" type="text/css"/>
</head>
<body>
<%@ include file="menu.jsp"%>
<mm:import id="node">xmltest</mm:import>
<mm:cloud>

<mm:node id="ok" referid="node" notfound="skip" />

<mm:notpresent referid="ok">
   The RichText application was not <a href="<mm:url page="/mmbase/admin/default.jsp?category=admin&subcategory=applications" />">deployed</a>. That is necessary for
   this page.
</mm:notpresent>

<h1>The formatter tag mm:formatter</h1>
<p>
  The formatter tag is meant to do basic formatting tasks. It's main
  use is to do XSL transformations, but it can do more.
</p>
<p>
  In this page we demonstrate how to use it, and what it can do. On the left you see
  the code. And on the right to what it results.
</p>
<mm:present referid="ok">

<mm:node referid="ok">
<mm:timer name="formatter performance">
<form>
<table>
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
  <td width="50%"><textarea wrap="soft"><%@include file="codesamples/userichfieldascii.jsp" %></textarea></td>
</tr>                      
<tr><td colspan="2">You can also format a whole node in this way. We
  also see the 'options' attribute applied here. The xslt/2xhtml.xslt
  in this directory accepts a parameter 'subtitle_color'.</td></tr>
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
  <td width="50%"><textarea><%@include file="codesamples/shownoderelationsxml.jsp" %></textarea></td>
</tr>
<tr><td colspan="2">If you want to generate your XML yourself with MMBase data, then you need to inform the tag that it should be
                    'dumb'. If the 'wants' attribute is 'string' then every tag in the
                    body works as if it is not in a formatter tag.</td></tr>
<tr valign="top">
  <td width="50%"><pre><mm:formatter format="escapexml"><mm:include page="codesamples/createmmbasexml.jsp" /></mm:formatter></pre></td>
  <td width="50%"><textarea><%@include file="codesamples/createmmbasexml.jsp" %></textarea></td>
</tr>
<%--
<tr><td colspan="2">Want to try some XSL yourself?</td></tr>
<tr valign="top">
  <td width="50%"><pre><mm:formatter format="escapexml"><mm:include page="codesamples/tryyourself.jsp" /></mm:formatter></pre></td>
  <td width="50%"><%@include file="codesamples/tryyourself.jsp" %></td>
</tr>
--%>
</table>
</form>
</mm:timer>
</mm:node>
</mm:present>
<table>
<tr><td colspan="2">
  <a href="codesamples/node.jspx">Here</a> another nice example of a jspx generating XML..
</td></tr>
<tr valign="top">
  <td width="50%"><pre><mm:include page="codesamples/node.jspx" cite="true" escape="text/xml" /></pre></td>
  <td width="50%"><pre><mm:formatter format="escapexmlpretty"><mm:include page="codesamples/node.jspx"  /></mm:formatter></pre></td>
</tr>
</table>
</mm:cloud>
</body>
</html>
</mm:content>