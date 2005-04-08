<%@taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" %>
<mm:content postprocessor="reducespace" expires="0">
<mm:cloud loginpage="/login.jsp" jspvar="cloud">

<%@include file="/shared/setImports.jsp"%>
<fmt:bundle basename="nl.didactor.component.email.EmailMessageBundle">
<%@taglib uri="http://www.didactor.nl/ditaglib_1.0" prefix="di" %>
	<mm:import externid="mailbox">-1</mm:import>

	<mm:compare referid="mailbox" value="-1">
	  <mm:node number="$user">
	    <mm:relatednodes id="inbox" type="mailboxes" max="1" constraints="mailboxes.m_type=0">
	      <mm:remove referid="mailbox"/>
	      <mm:field name="number" id="mailbox" write="false"/>
  	    </mm:relatednodes>
	  </mm:node>
	</mm:compare>

<div class="rows">

<div class="navigationbar">
  <div class="titlebar">
    <img src="<mm:treefile write="true" page="/gfx/icon_email.gif" objectlist="$includePath" />" width="25" height="13" border="0" alt="<fmt:message key="EMAIL" />" /> <fmt:message key="EMAIL" />
  </div>
</div>


<div class="folders">
  <div class="folderHeader">
    <fmt:message key="MAILBOXES" />
  </div>
  <div class="folderBody">

  </div>
</div>


    
<div class="mainContent">
  <div class="contentHeader">
    <fmt:message key="MAILRULES"/>
  </div>
  <div class="contentSubHeader">
  </div>
  <div class="contentBody">
 
    <mm:node number="$mailbox" notfound="skip">
      <table class="listTable">
          <tr>
            <th class="listHeader"><input type="checkbox" onclick="selectAllClicked(this.form,this.checked)"></input></th>
            <th class="listHeader"><fmt:message key="MATCHWHAT"/></th>
            <th class="listHeader"><fmt:message key="SUBSTRING"/></th>
            <th class="listHeader"><fmt:message key="FOLDER"/></th>
          </tr>
      <mm:list nodes="$mailbox" path="mailboxes1,mailrule,mailboxes2">
	<mm:field name="mailboxes2.name" id="destbox" write="false"/>
	<mm:field name="mailrule.number" id="mailrule" write="false"/>
	<mm:field name="mailrule.rule" id="rule" write="false"/>		  <mm:field name="mailrule.rnumber" id="rulereldef" write="false"/>
	
            <tr>
              <td class="listItem"><input type="checkbox" name="ids" value="<mm:write referid="mailrule"/>"></input></td>
              <td class="listItem">
		<mm:node number="$rulereldef">
		<mm:field id="ruletype" name="sname" write="false"/>
		<mm:compare referid="ruletype" value="subjectmailrule">
		    <fmt:message key="SUBJECT"/>
		</mm:compare>
		<mm:compare referid="ruletype" value="sendermailrule">
		    <fmt:message key="SENDER"/>
		</mm:compare>
		</mm:node>
              </td>
              <td class="listItem"><mm:write referid="rule" /></td>
              <td class="listItem">
	      <mm:write referid="destbox"/>
	      </td>
            </tr>
          </mm:list>
        </table>
    </mm:node>
<script>

      function selectAllClicked(frm, newState) {
	  if (frm.elements['ids'].length) {
	    for(var count =0; count < frm.elements['ids'].length; count++ ) {
		var box = frm.elements['ids'][count];
		box.checked=newState;
	    }
	  }
	  else {
	      frm.elements['ids'].checked=newState;
	  }
      }

</script>
</div>
</div>
</div>
<mm:treeinclude page="/cockpit/cockpit_footer.jsp" objectlist="$includePath" referids="$referids" />

</fmt:bundle>
</mm:cloud>
</mm:content>
