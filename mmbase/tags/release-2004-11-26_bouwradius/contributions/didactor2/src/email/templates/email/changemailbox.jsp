<%--
  This template changes a existing mailbox.
--%>
<%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" %>

<%-- expires is set so renaming a folder does not show the old name --%>
<mm:content postprocessor="reducespace" expires="0">
<mm:cloud loginpage="/login.jsp" jspvar="cloud">

<%@include file="/shared/setImports.jsp" %>
<fmt:bundle basename="nl.didactor.component.email.EmailMessageBundle">
<mm:treeinclude page="/cockpit/cockpit_header.jsp" objectlist="$includePath" referids="$referids">
  <mm:param name="extraheader">
    <title><fmt:message key="RENAMEFOLDER" /></title>
  </mm:param>
</mm:treeinclude>

<mm:import externid="mailbox"/>
<mm:import externid="callerpage"/>
<mm:import externid="action1"/>
<mm:import externid="action2"/>


<%-- Retrieve the mailbox and set the new name --%>
<mm:import externid="submitted"/>
<mm:present referid="submitted">
 <mm:notpresent referid="action2">

  <%-- check if a mailboxname is given --%>
  <mm:import id="mailboxname" externid="_name"/>
  <mm:compare referid="mailboxname" value="" inverse="true">

    <mm:node number="$mailbox">
      <mm:fieldlist type="all" fields="name">
        <mm:fieldinfo type="useinput" />
      </mm:fieldlist>
    </mm:node>

    <mm:redirect referids="$referids,mailbox" page="$callerpage"/>

  </mm:compare>
  <mm:compare referid="mailboxname" value="">
    <mm:import id="error">1</mm:import>
  </mm:compare>

 </mm:notpresent>
</mm:present>


<%-- Check if the back button is pressed --%>
<mm:import id="action2text"><fmt:message key="BACK" /></mm:import>
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
    <fmt:message key="RENAMEFOLDER" />
  </div>

  <div class="contentBodywit">

    <%-- Show the form --%>
    <form name="changemailboxform" method="post" action="<mm:treefile page="/email/changemailbox.jsp" objectlist="$includePath" referids="$referids"/>">
    <input type="hidden" name="submitted" value="true">

      <mm:node number="$mailbox">
        <table class="Font">
        <mm:fieldlist fields="name">
          <tr>
          <td><mm:fieldinfo type="guiname"/></td>
          <td><mm:fieldinfo type="input"/></td>
          </tr>
        </mm:fieldlist>
        </table>
	  </mm:node>

      <input type="hidden" name="callerpage" value="<mm:write referid="callerpage"/>"/>
      <input type="hidden" name="mailbox" value="<mm:write referid="mailbox"/>"/>
      <input class="formbutton" type="submit" name="action1" value="<fmt:message key="RENAME" />"/>
      <input class="formbutton" type="submit" name="action2" value="<fmt:message key="BACK" />"/>

      <mm:present referid="error">
	    <p/>
	    <h1><fmt:message key="MAILBOXNAMENOTEMPTY" /></h1>
	  </mm:present>

    </form>

  </div>
</div>
</div>
<mm:treeinclude page="/cockpit/cockpit_footer.jsp" objectlist="$includePath" referids="$referids" />

</fmt:bundle>
</mm:cloud>
</mm:content>
