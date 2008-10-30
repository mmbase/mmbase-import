<%--
  This template creates a new mailbox.
--%>
<%@taglib uri="http://www.didactor.nl/ditaglib_1.0" prefix="di" %>
<%@taglib uri="http://www.mmbase.org/mmbase-taglib-2.0" prefix="mm" %>

<mm:content postprocessor="reducespace" expires="0">
<mm:cloud method="delegate" jspvar="cloud">

<%@include file="/shared/setImports.jsp" %>
<mm:treeinclude page="/cockpit/cockpit_header.jsp" objectlist="$includePath" referids="$referids">
  <mm:param name="extraheader">
    <title><di:translate key="email.createfolder" /></title>
  </mm:param>
</mm:treeinclude>

<mm:import externid="mailbox"/>
<mm:import externid="callerpage"/>
<mm:import externid="action1"/>
<mm:import externid="action2"/>

    <%-- detect double clicks on submit form and redirect off the page --%>
    <mm:import externid="detectclicks" from="parameters"/>
    <mm:import externid="oldclicks" from="session"/>
    <mm:present referid="detectclicks">
	<mm:compare referid="detectclicks" value="$oldclicks">
	    <mm:redirect referids="$referids,mailbox" page="$callerpage"/>
        </mm:compare>
	<mm:write session="oldclicks" referid="detectclicks"/>
    </mm:present>




<mm:notpresent referid="action2"> <%-- back was NOT pressed --%>
  <%-- check if a mailboxname is given --%>
  <mm:import id="mailboxname" externid="_name"/>
  <mm:compare referid="mailboxname" value="" inverse="true">

    <mm:node number="$user" id="myuser">
      <mm:createnode type="mailboxes" id="mymailbox">

        <mm:fieldlist type="all" fields="name">
          <mm:fieldinfo type="useinput" />
        </mm:fieldlist>
	    <mm:setfield name="type">3</mm:setfield>

      </mm:createnode>
      <mm:createrelation role="related" source="myuser" destination="mymailbox"/>
    </mm:node>

    <mm:redirect referids="$referids,mailbox" page="$callerpage"/>

  </mm:compare>
  <mm:compare referid="mailboxname" value="">
    <mm:import id="error">1</mm:import>
  </mm:compare>

</mm:notpresent>


<%-- Check if the back button is pressed --%>
<mm:import id="action2text"><di:translate key="email.back" /></mm:import>
<mm:compare referid="action2" referid2="action2text">
  <mm:redirect referids="$referids,mailbox" page="$callerpage"/>
</mm:compare>

<div class="rows">

<div class="navigationbar">
  <div class="titlebar">
    <img src="<mm:treefile write="true" page="/gfx/icon_email.gif" objectlist="$includePath" referids="$referids"/>" width="25" height="13" border="0"  title="<di:translate key="email.email"/>" alt="<di:translate key="email.email" />"/>
    <di:translate key="email.email" />
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
    <di:translate key="email.createfolder" />
  </div>

  <div class="contentBodywit">

    <%-- Show the form --%>
    <form name="createmailboxform" method="post" action="<mm:treefile page="/email/createmailbox.jsp" objectlist="$includePath" referids="$referids"/>">
	<input type="hidden" name="detectclicks" value="<%= System.currentTimeMillis() %>">

      <table class="Font">
      <mm:fieldlist nodetype="mailboxes" fields="name">
        <tr>
        <td><mm:fieldinfo type="guiname"/></td>
        <td><mm:fieldinfo type="input"/></td>
        </tr>
      </mm:fieldlist>
      </table>
      <script>
      <!--
	document.forms['createmailboxform'].elements['_name'].focus();
	// -->
    </script>

      <input type="hidden" name="mailbox" value="<mm:write referid="mailbox"/>"/>
      <input type="hidden" name="callerpage" value="<mm:write referid="callerpage"/>"/>
      <input class="formbutton" type="submit" name="action1" value="<di:translate key="email.create" />"/>
      <input class="formbutton" type="submit" name="action2" value="<di:translate key="email.back" />"/>

      <mm:present referid="error">
	    <p/>
	    <h1><di:translate key="email.mailboxnamenotempty" /></h1>
	  </mm:present>

    </form>

  </div>
</div>
</div>
<mm:treeinclude page="/cockpit/cockpit_footer.jsp" objectlist="$includePath" referids="$referids" />

</mm:cloud>
</mm:content>
