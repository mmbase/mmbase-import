<%--
  This template deletes a existing mailrule
--%>
<%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@taglib uri="http://www.mmbase.org/mmbase-taglib-1.1" prefix="mm" %>

<mm:content postprocessor="reducespace" expires="0">
<mm:cloud loginpage="/login.jsp" jspvar="cloud">

<%@include file="/shared/setImports.jsp" %>
<fmt:bundle basename="nl.didactor.component.email.EmailMessageBundle">
<mm:treeinclude page="/cockpit/cockpit_header.jsp" objectlist="$includePath" referids="$referids">
  <mm:param name="extraheader">
    <title><fmt:message key="DELETERULES" /></title>
  </mm:param>
</mm:treeinclude>

<mm:import externid="ids" vartype="List"/>
<mm:import externid="callerpage"/>
<mm:import externid="action1"/>
<mm:import externid="action2"/>

<%-- Check if the yes button is pressed --%>
<mm:import id="action1text"><fmt:message key="DELETEYES" /></mm:import>
<mm:compare referid="action1" referid2="action1text">

    <mm:listnodes type="mailrule" constraints="number IN ($ids)">
	<mm:deletenode deleterelations="false"/>
    </mm:listnodes>
  <%-- Show the previous page --%>
  <mm:redirect referids="$referids" page="$callerpage"/>

</mm:compare>


<%-- Check if the no button is pressed --%>
<mm:import id="action2text"><fmt:message key="DELETENO" /></mm:import>
<mm:compare referid="action2" referid2="action2text">
  <mm:redirect referids="$referids,ids" page="$callerpage"/>
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
    <fmt:message key="DELETEFOLDER" />
  </div>

  <div class="contentBodywit">

    <%-- Show the form --%>
    <form name="deletemailboxform" method="post" action="<mm:treefile page="/email/deleterules.jsp" objectlist="$includePath" referids="$referids"/>">

      <fmt:message key="DELETERULESYESNO" />
      <p/>

      <input type="hidden" name="ids" value="<mm:write referid="ids"/>"/>
      <input type="hidden" name="callerpage" value="<mm:write referid="callerpage"/>"/>
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
