<%@page session="true" language="java" contentType="text/html; charset=UTF-8"
%><%@taglib uri="http://www.didactor.nl/ditaglib_1.0" prefix="di"
%><%@taglib uri="http://www.mmbase.org/mmbase-taglib-2.0" prefix="mm"
%><mm:cloud>
  <mm:content postprocessor="reducespace" language="$language" expires="0">

  <mm:treeinclude page="/cockpit/cockpit_intro_header.jsp" objectlist="$includePath" referids="$referids">
    <mm:param name="extraheader">
      <link rel="stylesheet" type="text/css" href="${mm:treefile('/register/css/register.css', pageContext,  includePath)}"  />
      <jsp:directive.include file="/mmbase/validation/javascript.jspxf" />
    </mm:param>
  </mm:treeinclude>

  <mm:import externid="formsubmit">false</mm:import>

  <mm:compare referid="formsubmit" value="true">
    <mm:import externid="nextform" />
    <mm:log>Submitted, next form '<mm:write referid="nextform" />'</mm:log>
    <mm:isempty referid="nextform">
      <mm:treeinclude page="/register/register_done.jsp" objectlist="$includePath" referids="$referids" />
    </mm:isempty>
    <mm:isnotempty referid="nextform">
      <mm:log>Next form</mm:log>
      <mm:treeinclude page="/register/register_form.jsp" objectlist="$includePath" referids="$referids,nextform@formId" />
    </mm:isnotempty>.
  </mm:compare>
  <mm:compare referid="formsubmit" value="false">
    <mm:treeinclude page="/register/register_form.jsp" objectlist="$includePath" referids="$referids" />
  </mm:compare>

</mm:content>
</mm:cloud>
