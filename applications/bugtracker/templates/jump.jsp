<%@taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" %><%@
page language="java" contentType="text/html; charset=utf-8"%>
<mm:cloud>
  <%@include file="parameters.jsp" %>
  <mm:import externid="id" required="true"/>
  <mm:listnodescontainer type="bugreports">
    <mm:constraint field="bugid" value="$id"/>
    <mm:maxnumber value="1"/>
    <mm:listnodes jspvar="node">
        <mm:url id="url" write="false" page="index.jsp" referids="parameters,$parameters"><mm:param name="template" value="fullview.jsp" /><mm:param name="bugreport"><mm:field name="number" /></mm:param></mm:url>
        <mm:redirect referid="url"/>
    </mm:listnodes>
  </mm:listnodescontainer>
</mm:cloud>

