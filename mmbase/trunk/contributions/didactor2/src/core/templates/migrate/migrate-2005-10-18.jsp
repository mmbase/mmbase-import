<html>
<%@taglib uri="http://www.mmbase.org/mmbase-taglib-1.1" prefix="mm" %>
<%@taglib uri="http://www.didactor.nl/ditaglib_1.0" prefix="di" %>
<mm:content postprocessor="reducespace" expires="0">
<mm:cloud loginpage="/login.jsp" jspvar="cloud">
<%@include file="/shared/setImports.jsp" %>
<mm:nodeinfo type="number" nodetype="educations" write="false" id="t_edu" />
<mm:nodeinfo type="number" nodetype="classes" write="false" id="t_cls" />
<mm:listnodescontainer type="reldef">
  <mm:constraint field="sname" value="classrel" />
  <mm:listnodes>
    <mm:field name="number" id="t_clsrel"/>
  </mm:listnodes>
</mm:listnodecontainer>
  
<mm:nodeinfo type="number" nodetype="classes" write="false" id="t_cls" />

<mm:listnodescontainer type="typerel">
  <mm:constraint field="snumber" referid="t_edu" />
  <mm:constraint field="dnumber" referid="t_cls" />
  <mm:listnodes>
    <mm:setfield name="rnumber"><mm:write referid="t_clsrel" /></mm:setfield>
    Updated reldef between Educations and Classes
  </mm:listnodes>
</mm:listnodescontainer>
</mm:cloud>
</mm:content>
</html>

