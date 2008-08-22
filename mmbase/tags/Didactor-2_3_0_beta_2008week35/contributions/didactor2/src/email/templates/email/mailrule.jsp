<%--
  This template shows the contents of a mailbox using the <di:table> tag.
  A link is created for every email to the 'email.jsp' page, where the user
  can view the email and do other actions.
--%>
<%@taglib uri="http://www.didactor.nl/ditaglib_1.0" prefix="di" 
%><%@taglib uri="http://www.mmbase.org/mmbase-taglib-2.0" prefix="mm" 
%><mm:content postprocessor="reducespace" expires="0">
<mm:cloud method="delegate">

<jsp:directive.include file="/shared/setImports.jsp" />
<mm:import externid="mailbox">-1</mm:import>
<mm:import externid="so"/>
<mm:import externid="sf"/>

<mm:treeinclude page="/cockpit/cockpit_header.jsp" objectlist="$includePath" referids="$referids">
  <mm:param name="extraheader">
    <title><di:translate key="email.email" /></title>
  </mm:param>
</mm:treeinclude>

<mm:node number="$user">
  <mm:relatednodes type="mailboxes" orderby="type, name" directions="up, up" id="mymailboxes">
	<mm:field id="mbox" name="number" write="false" />
	<mm:compare referid="mailbox" value="-1">
	  <mm:first>
		<mm:remove referid="mailbox"/>
		<mm:import id="mailbox"><mm:field name="number"/></mm:import>
	  </mm:first>
	</mm:compare>
	<mm:compare referid="mbox" referid2="mailbox">
	  <mm:import id="mailboxname"><mm:field name="name"/></mm:import>
	</mm:compare>
  </mm:relatednodes>
</mm:node>

<mm:import externid="action_back" from="parameters" id="action_back"/>
    <mm:present referid="action_back">
      <mm:redirect page="/email/index.jsp" referids="$referids,mailbox,so?,sf?">
        <mm:param name="callerpage">/email/mailrule.jsp</mm:param>
	</mm:redirect>
</mm:present>



<mm:import externid="ids" vartype="List"/>
<mm:present referid="ids">
    <mm:import externid="action_delete.x" from="parameters" id="action_delete"/>
    <mm:present referid="action_delete">
	<mm:redirect page="/email/deleterules.jsp" referids="$referids,mailbox,ids,so?,sf?">
	    <mm:param name="callerpage">/email/mailrule.jsp</mm:param>
	</mm:redirect>
    </mm:present>

    <mm:import externid="action_create"  from="parameters" id="action_create"/>
	<mm:present referid="action_create">
	<mm:redirect page="/email/mailboxes/editmailrule.jsp" referids="$referids,mailbox,ids,so?,sf?">
	    <mm:param name="callerpage">/email/mailrule.jsp</mm:param>
	</mm:redirect>
    </mm:present>
</mm:present>



<div class="rows">

<div class="navigationbar">
  <div class="titlebar">
    <img src="<mm:treefile write="true" page="/gfx/icon_email.gif" objectlist="$includePath" />" width="25" height="13" border="0" alt="<di:translate key="email.email" />" /> <di:translate key="email.email" />
  </div>
</div>


<div class="folders">
  <div class="folderHeader">
    <di:translate key="email.mailboxes" />
  </div>
  <div class="folderBody">

  </div>
</div>


<form action="<mm:treefile page="/email/mailrule.jsp" objectlist="$includePath" referids="$referids"/>" method="POST">
    <input type="hidden" name="mailbox" value="<mm:write referid="mailbox"/>">
      <mm:present referid="so">
        <input type="hidden" name="so" value="${so}" />
      </mm:present>
      <mm:present referid="sf">
        <input type="hidden" name="sf" value="${sf}" />
      </mm:present>


<div class="mainContent">
  <div class="contentHeader">
    <di:translate key="email.mailrules" />
  </div>
  <div class="contentSubHeader">
    <a href="<mm:treefile page="/email/mailbox/editmailrule.jsp" objectlist="$includePath" referids="$referids"/>">
      <img src="<mm:treefile write="true" page="/gfx/icon_emailschrijven.gif" objectlist="$includePath" />" width="50" height="28" border="0" title="<di:translate key="email.writenewrule" />" alt="<di:translate key="email.writenewrule" />" /></a>

       <input type="image" src="<mm:treefile page="/email/gfx/verwijder geselecteerde.gif" objectlist="$includePath" referids="$referids"/>" border="0" title="<di:translate key="email.deleteselected" />" alt="<di:translate key="email.deleteselected" />" name="action_delete" value="delete"/>
       
  </div>
  <div class="contentBody">
    <mm:treeinclude page="/email/mailbox/mailrules.jsp" objectlist="$includePath" referids="$referids" />

<br/>
  <input type="submit" class="formbutton" name="action_back" value="<di:translate key="email.back"/>" />
  </div>

</div>
</div>
</form>
<mm:treeinclude page="/cockpit/cockpit_footer.jsp" objectlist="$includePath" referids="$referids" />
</mm:cloud>
</mm:content>
