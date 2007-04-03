<%@ page import="org.mmbase.module.core.MMBase" 
%><%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" %>
<mm:cloud rank="administrator" loginpage="login.jsp">
<div
  class="component ${requestScope.className}"
  id="${requestScope.componentId}">
<mm:import externid="application" />
<mm:import externid="cmd" />
<mm:import externid="path" />

<h3>Applications results</h3>

<table summary="results" border="0" cellspacing="0" cellpadding="3">
  <caption>
    Results of your ${cmd} action on application <mm:write referid="application" />.
  </caption>
  <tr>
    <th colspan="2">Results</th>
  </tr><tr>
    <td colspan="2">
	  <mm:compare referid="cmd" value="LOAD">
		<mm:nodefunction module="mmadmin" name="LOAD" referids="application">
		  <mm:field name="RESULT" escape="p" />
		</mm:nodefunction>      
	  </mm:compare>
	  <mm:compare referid="cmd" value="SAVE">
		<mm:nodefunction module="mmadmin" name="SAVE" referids="application,path">
		  <mm:field name="RESULT" escape="p" />
		</mm:nodefunction>      
	  </mm:compare>
    </td>
  </tr><tr>
    <td>
      <mm:link page="applications" component="core">
        <a href="${_}"><img src="<mm:url page="/mmbase/style/images/back.png" />" alt="back" /></a>
      </mm:link>
    </td>
    <td>Return to Applications Administration</td>
  </tr>
  </table>
</div>
</mm:cloud>
