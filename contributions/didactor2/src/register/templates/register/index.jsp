<%@page session="true" language="java" contentType="text/html; charset=UTF-8"
%><%@taglib uri="http://www.didactor.nl/ditaglib_1.0" prefix="di" 
%><%@taglib uri="http://www.mmbase.org/mmbase-taglib-2.0" prefix="mm" 
%>
<mm:cloud  authenticate="asis">
  <jsp:directive.include file="/shared/setImports.jsp" />
  <mm:content postprocessor="reducespace" language="$language" expires="0">
  <mm:treeinclude page="/cockpit/cockpit_intro_header.jsp" objectlist="$includePath" referids="$referids">
    <mm:param name="extraheader">
      <link rel="stylesheet" type="text/css" href="${mm:treefile('/register/css/register.css', pageContext,  includePath)}"  />
    </mm:param>
  </mm:treeinclude>

  <mm:import externid="formsubmit">false</mm:import>

  <mm:compare referid="formsubmit" value="true">
    <mm:treeinclude page="/register/register_done.jsp" objectlist="$includePath" referids="$referids">
      <mm:param name="uname">${requestScope.person.username}</mm:param>
      <mm:param name="password">${requestScope.password}</mm:param>
    </mm:treeinclude>
  </mm:compare>
  <mm:compare referid="formsubmit" value="false">
    <mm:treeinclude page="/register/register_form.jsp" objectlist="$includePath" referids="$referids" />
  </mm:compare>

</mm:content>
</mm:cloud>

