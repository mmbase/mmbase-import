<%@taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" %>
<mm:content postprocessor="reducespace" expires="0">
<mm:cloud loginpage="/login.jsp" jspvar="cloud">

<%@include file="/shared/setImports.jsp"%>
<fmt:bundle basename="nl.didactor.component.email.EmailMessageBundle">
<%@taglib uri="http://www.didactor.nl/ditaglib_1.0" prefix="di" %>

<mm:treeinclude page="/cockpit/cockpit_header.jsp" objectlist="$includePath" referids="$referids">
  <mm:param name="extraheader">
    <title><fmt:message key="EMAIL" /></title>
  </mm:param>
</mm:treeinclude>

<div class="rows">

<div class="navigationbar">
  <div class="titlebar">
    <img src="<mm:treefile write="true" page="/gfx/icon_email.gif" objectlist="$includePath" />" width="25" height="13" border="0" alt="<fmt:message key="EMAIL" />" /> <fmt:message key="EMAIL" />
  </div>
</div>


<div class="folders">
  <div class="folderHeader">
    <fmt:message key="MAILRULES" />
  </div>
  <div class="folderBody">

  </div>
</div>


<div class="mainContent">
  <div class="contentHeader">
    
  </div>
  <div class="contentSubHeader">
   
  </div>
  <div class="contentBodywit">
<br><br><br>
  
<mm:import externid="rule"/>
<mm:import externid="type"/>
<mm:import externid="folder"/>

<mm:import externid="action_commit"/>
<mm:present referid="action_commit">
<mm:present referid="rule"><mm:isempty referid="rule" inverse="true">
    <mm:node number="$user">
	<mm:relatednodes type="mailboxes" constraints="m_type=0" max="1">
	    <mm:field name="number" id="sfolder"/>
	</mm:relatednodes>
	
	<mm:createrelation source="sfolder" destination="folder" role="$type">
	    <mm:setfield name="rule"><mm:write referid="rule"/></mm:setfield>
	</mm:createrelation>
    </mm:node>
    <mm:redirect page="/email/mailrule.jsp" referids="$referids">
        <mm:param name="callerpage">/email/mailrule.jsp</mm:param>
    </mm:redirect>
  </mm:isempty>
</mm:present>
</mm:present>

<mm:import externid="action_back"/>
<mm:present referid="action_back">
<mm:redirect page="/email/mailrule.jsp" referids="$referids">
    <mm:param name="callerpage">/email/mailrule.jsp</mm:param>
</mm:redirect>

</mm:present>

    <form action="editmailrule.jsp" method="POST">
    <table class="Font">
	<tr>
	    <th><fmt:message key="MATCHWHAT"/></th>
	    <td>
	    <select name="type">
		<option value="subjectmailrule"><fmt:message key="SUBJECT"/></option>
		<option value="sendermailrule"><fmt:message key="SENDER"/></option>
	    </select>
	    </td>
	</tr>
	<tr>
	    <th><fmt:message key="SUBSTRING"/></th>
	    <td><input type="text" name="rule" size="80" class="formbutton"></td>
	</tr>
	<tr>
	    <th><fmt:message key="FOLDER"/></th>
	    <td><select name="folder">
		<mm:node number="$user">
		<mm:relatednodes type="mailboxes" orderby="type, name">
		  <option value="<mm:field name="number"/>"><mm:field name="name"/></option>
		</mm:relatednodes>
		</mm:node>
		</select>
	    </td>
	</tr>
	<tr><td></td><td><input class="formbutton" type="submit" name="action_back" value="<fmt:message key="BACK"/>"/> <input type="submit" name="action_commit" value="Ok" class="formbutton"></td></tr>
    </table>
    </form>
		

  </div>
</div>
</div>
<mm:treeinclude page="/cockpit/cockpit_footer.jsp" objectlist="$includePath" referids="$referids" />

</fmt:bundle>
</mm:cloud>
</mm:content>
