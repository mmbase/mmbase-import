<%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm"%>
<html>
<mm:import id="a" jspvar="a">A</mm:import>
<mm:import externid="b" jspvar="b" vartype="list">X</mm:import>
<mm:import id="c" jspvar="c">C</mm:import>

<mm:import externid="dummy" />

<%@include file="import.jsp"%>

<head>
<mm:notpresent referid="dummy">
  <meta http-equiv="refresh" content="1; url=<mm:url><mm:param name="b" value="B" /><mm:param name="a" value="X" /><mm:param name="dummy" value="" /></mm:url>" />
</mm:notpresent>
</head>
<body>
<mm:stringlist referid="parameters">
 <mm:index />:  <mm:write />: <mm:write referid="$_" /><br />
</mm:stringlist>


<mm:import id="l" vartype="list" jspvar="l">A,B</mm:import>

<table>
  <tr><th>Test</th><th>Should be</th><th>Is</th></tr>
  <tr><td>Writing local vars</td><td>A, B, A,B</td><td><mm:write referid="a" />, <mm:write referid="b" />, <mm:write referid="l" /></td></tr>

  <tr><th colspan="3">mm:include with referids</th></tr>
  <tr><td>Simple include</td><td>A | A,X</td><td><mm:include debug="html" page="writea.jsp" referids="a" /></td></tr>
  <tr><td>Simple include/import</td><td>B | B,X</td><td><mm:include debug="html" page="writea.jsp" referids="b@a" /></td></tr>
  <tr><td>Simple include/import</td><td>C | C,X </td><td><mm:include debug="html" page="writea.jsp" referids="c@a" /></td></tr>
  <tr><td>Simple include of list</td><td>A, B | A,B,X</td><td><mm:include debug="html" page="writea.jsp" referids="l@a" /></td></tr>
  <tr><th colspan="3">mm:include with mm:write request </th></tr>
  <tr><td>Simple include</td><td>A | A</td><td><mm:write request="b" referid="a" /><mm:include debug="html" page="writeb.jsp" /></td></tr>
  <tr><td>Simple include/import</td><td>B | B</td><td><mm:write request="b" referid="b" /><mm:include debug="html" page="writeb.jsp" /></td></tr>
  <tr><td>Simple include</td><td>B | B</td><td><mm:write request="b" referid="b" /><mm:include debug="html" page="writeb_noimport.jsp" /></td></tr>
  <tr><td>Simple include/import</td><td>C | C </td><td><mm:write request="b" referid="c" /><mm:include debug="html" page="writeb.jsp" /></td></tr>
  <tr><td>Simple include of list</td><td>A, B | A,B</td><td><mm:write request="b" referid="l" /><mm:include debug="html" page="writeb.jsp"  /></td></tr>
  <tr><th colspan="3">mm:include with 'attributes' attribute </th></tr>
  <tr><td>Simple include</td><td>A | A</td><td><mm:include debug="html" attributes="a@b" page="writeb.jsp" /></td></tr>
  <tr><td>Simple include/import</td><td>B | B</td><td><mm:include debug="html" attributes="b" page="writeb.jsp" /></td></tr>
  <tr><td>Simple include/import</td><td>C | C </td><td><mm:include debug="html" attributes="c@b" page="writeb.jsp" /></td></tr>
  <tr><td>Simple include of list</td><td>A, B | A,B</td><td><mm:include debug="html" attributes="l@b" page="writeb.jsp"  /></td></tr>
  
  <tr><th colspan="3">jsp:include</th></tr>
  <tr><td>Simple jsp:include</td><td>A | A,X</td><td><jsp:include page="writea.jsp"><jsp:param name="a" value="<%=a%>" /></jsp:include></td></tr>
  <tr><td>Simple jsp:include/import</td><td>B | B,X</td><td><jsp:include page="writea.jsp"><jsp:param name="a" value="<%=b%>" /></jsp:include></td></tr>
  <tr><td>Simple jsp:include/import</td><td>C | C,X</td><td><jsp:include page="writea.jsp"><jsp:param name="a" value="<%=c%>" /></jsp:include></td></tr>
  <tr><td>Simple jsp:include of list</td><td>A,B | A,B,X</td><td><jsp:include page="writea.jsp"><jsp:param name="a" value="<%=l%>" /></jsp:include></td></tr>
  <tr><th colspan="3">jsp:include with mm:write request </th></tr>
  <tr><td>Simple jsp:include</td><td>A | A</td><td><mm:write request="b" referid="a" /><jsp:include page="writeb.jsp" /></td></tr>
  <tr><td>Simple jsp:include/import</td><td>B | B</td><td><mm:write request="b" referid="b" /><jsp:include page="writeb.jsp" /></td></tr>
  <tr><td>Simple jsp:include/import</td><td>C | C </td><td><mm:write request="b" referid="c" /><jsp:include page="writeb.jsp" /></td></tr>
  <tr><td>Simple jsp:include of list</td><td>A, B | A,B</td><td><mm:write request="b" referid="l" /><jsp:include page="writeb.jsp"  /></td></tr>

  <tr><th colspan="3">Standard attributes</th></tr>
  <tr><td>javax.servlet.include.servlet_path</td><td>null</td><td><%=request.getAttribute("javax.servlet.include.servlet_path")%></td></tr>
  <tr><td>org.mmbase.taglib.includeLevel</td><td>null</td><td><%=request.getAttribute("org.mmbase.taglib.includeLevel")%></td></tr>
  <tr><th colspan="3">Standard attributes in an mm:include</th></tr>
  <% String dir = new java.net.URL(new java.net.URL("http", "localhost", request.getServletPath()), ".").getFile(); %>
  <tr><td>javax.servlet.include.servlet_path</td><td><%=dir%>showattribute.jsp</td><td><mm:write request="attribute" value="javax.servlet.include.servlet_path" /><mm:include debug="html" page="showattribute.jsp" /></td></tr>
  <tr><td>org.mmbase.taglib.includeLevel</td><td>1</td><td><td><mm:write request="attribute" value="org.mmbase.taglib.includeLevel" /><mm:include debug="html" page="showattribute.jsp" /></td></tr>

  <tr><th colspan="3">Standard attributes in an mm:include (recursive)</th></tr>
  <tr><td>javax.servlet.include.servlet_path</td><td><%=dir%>includeshowattribute.jsp | <%=dir%>showattribute.jsp</td><td><mm:write request="attribute" value="javax.servlet.include.servlet_path" /><mm:include debug="html" page="includeshowattribute.jsp" /></td></tr>
  <tr><td>org.mmbase.taglib.includeLevel</td><td>1 | 2</td><td><td><mm:write request="attribute" value="org.mmbase.taglib.includeLevel" /><mm:include debug="html" page="includeshowattribute.jsp" /></td></tr>

  <tr><th colspan="3">Standard attributes in an jsp:include</th></tr>
  <tr><td>javax.servlet.include.servlet_path</td><td><%=dir%>showattribute.jsp</td><td><mm:write request="attribute" value="javax.servlet.include.servlet_path" /><jsp:include page="showattribute.jsp" /></td></tr>
  <tr><td>org.mmbase.taglib.includeLevel</td><td>null</td><td><td><mm:write request="attribute" value="org.mmbase.taglib.includeLevel" /><jsp:include page="showattribute.jsp" /></td></tr>
  <tr><th colspan="3">Standard attributes in an jsp:include (recursive)</th></tr>
  <tr><td>javax.servlet.include.servlet_path</td><td><%=dir%>includeshowattribute.jsp | <%=dir%>showattribute.jsp</td><td><mm:write request="attribute" value="javax.servlet.include.servlet_path" /><jsp:include page="includeshowattribute.jsp" /></td></tr>
  <tr><td>org.mmbase.taglib.includeLevel</td><td>null | 1</td><td><td><mm:write request="attribute" value="org.mmbase.taglib.includeLevel" /><jsp:include page="includeshowattribute.jsp" /></td></tr>
  <tr><th colspan="3">from root</th></tr>
  <tr><td>Simple include</td><td>A | A,X</td><td><mm:include debug="html" page='<%=dir + "writea.jsp"%>' referids="a" /></td></tr>
</table>  
</body>
</html>