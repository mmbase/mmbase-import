<%--
  This template delete existing mail from a mailbox.
--%>
<%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@taglib uri="http://www.mmbase.org/mmbase-taglib-1.1" prefix="mm" %>

<%-- expires is set so renaming a folder does not show the old name --%>
<mm:content postprocessor="reducespace" expires="0">
<mm:cloud loginpage="/login.jsp" jspvar="cloud">

<%@include file="/shared/setImports.jsp" %>
<fmt:bundle basename="nl.didactor.component.email.EmailMessageBundle">
<mm:treeinclude page="/cockpit/cockpit_header.jsp" objectlist="$includePath" referids="$referids">
  <mm:param name="extraheader">
    <title><fmt:message key="DELETEFOLDERITEMS" /></title>
  </mm:param>
</mm:treeinclude>

<mm:import externid="mailbox"/>
<mm:import externid="callerpage"/>

<mm:import externid="idCount"/>
<mm:import externid="ids"/>

<mm:import id="list" jspvar="list" vartype="List"><mm:write referid="ids"/></mm:import>

<mm:import externid="action1"/>
<mm:import externid="action2"/>

<%-- Check if the yes button is pressed --%>
<mm:import id="action1text"><fmt:message key="DELETEYES" /></mm:import>
<mm:compare referid="action1" referid2="action1text">

  <%-- Determine the items to be deleted --%>
  <mm:node number="$mailbox">

    <mm:import id="type"><mm:field name="type"/></mm:import>

    <%-- from deleted items mailbox: do a real delete --%>
    <mm:compare referid="type" value="2">
      <mm:relatednodescontainer type="object">
        <mm:constraint field="number" referid="list" operator="IN"/>
        <mm:relatednodes>
          <mm:deletenode deleterelations="true"/>
        </mm:relatednodes>
      </mm:relatednodescontainer>

      <%-- Show the previous page --%>
      <mm:redirect referids="$referids,mailbox" page="$callerpage"/>

    </mm:compare>

    <mm:compare referid="type" value="2" inverse="true">

      <%-- Get the name of the deleted items mailbox --%>
      <mm:node number="$user">
        <mm:relatednodescontainer type="mailboxes">
          <mm:constraint field="type" value="2"/>
          <mm:relatednodes>
            <mm:import id="mailboxname"><mm:field name="name"/></mm:import>
          </mm:relatednodes>
        </mm:relatednodescontainer>
      </mm:node>

      <%-- from any other mailbox: do a move items to deleted items mailbox --%>
      <mm:redirect page="/email/moveitems.jsp" referids="$referids">
	<mm:param name="submitted" value="true"/>
        <mm:param name="action1"><fmt:message key="MOVE" /></mm:param>
        <mm:param name="callerpage"><mm:write referid="callerpage"/></mm:param>
        <mm:param name="mailbox"><mm:write referid="mailbox"/></mm:param>
        <mm:param name="mailboxname"><mm:write referid="mailboxname"/></mm:param>
        <mm:param name="idCount"><mm:write referid="idCount"/></mm:param>
        <mm:param name="ids"><mm:write referid="ids"/></mm:param>
      </mm:redirect>
    </mm:compare>

  </mm:node>

</mm:compare>


<%-- Check if the no button is pressed --%>
<mm:import id="action2text"><fmt:message key="DELETENO" /></mm:import>
<mm:compare referid="action2" referid2="action2text">
  <mm:redirect referids="$referids,mailbox" page="$callerpage"/>
</mm:compare>


<div class="rows">

<div class="navigationbar">
  <div class="titlebar">
    <img src="<mm:treefile write="true" page="/gfx/icon_email.gif" objectlist="$includePath" referids="$referids"/>" width="25" height="13" border="0" alt="<fmt:message key="EMAIL" />"/>
    <fmt:message key="EMAIL" />
  </div>
</div>

<div class="folders">
  <div class="folderHeader">
  </div>
  <div class="folderBody">
  </div>
</div>

<div class="mainContent">

  <div class="contentHeader">
    <fmt:message key="DELETEFOLDERITEMS" />
  </div>

  <div class="contentBodywit">

    <%-- Show the form --%>
    <form name="deletemailboxitemform" method="post" action="<mm:treefile page="/email/deleteitems.jsp" objectlist="$includePath" referids="$referids"/>">

      <fmt:message key="DELETEFOLDERITEMSYESNO" />
      <p/>

      <!-- TODO show data in near future -->
      <table class="Font">
       </table>

      <input type="hidden" name="callerpage" value="<mm:write referid="callerpage"/>"/>
      <input type="hidden" name="mailbox" value="<mm:write referid="mailbox"/>"/>
      <input type="hidden" name="ids" value="<mm:write referid="ids"/>"/>
      <input class="formbutton" type="submit" name="action1" value="<fmt:message key="DELETEYES" />"/>
      <input class="formbutton" type="submit" name="action2" value="<fmt:message key="DELETENO" />"/>
    </form>

  </div>
</div>
</div>

<mm:treeinclude page="/cockpit/cockpit_footer.jsp" objectlist="$includePath" referids="$referids" />

</fmt:bundle>
</mm:cloud>
</mm:content>
