<%--
  This template shows the contents of a mailbox using the <di:table> tag.
  A link is created for every email to the 'email.jsp' page, where the user
  can view the email and do other actions.
--%>
<%@taglib uri="http://www.didactor.nl/ditaglib_1.0" prefix="di" %>
<%@taglib uri="http://www.mmbase.org/mmbase-taglib-1.1" prefix="mm" %>
<mm:content postprocessor="reducespace" expires="0">
<mm:cloud loginpage="/login.jsp" jspvar="cloud">

<%@include file="/shared/setImports.jsp"%>
<mm:import externid="mailbox">-1</mm:import>

<mm:treeinclude page="/cockpit/cockpit_header.jsp" objectlist="$includePath" referids="$referids">
  <mm:param name="extraheader">
    <title><di:translate key="email.email" /></title>
  </mm:param>
</mm:treeinclude>

<mm:node number="$user">
  <mm:relatednodes type="mailboxes" orderby="type" directions="up" id="mymailboxes">
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

<mm:import externid="ids" vartype="List"/>
<mm:present referid="ids">
    <mm:import externid="action_delete.x" from="parameters" id="action_delete"/>
    <mm:present referid="action_delete">
	<mm:redirect page="/email/deleteitems.jsp" referids="$referids,mailbox,ids">
	    <mm:param name="callerpage">/email/index.jsp</mm:param>
	</mm:redirect>
    </mm:present>

    <mm:import externid="action_move.x"  from="parameters" id="action_move"/>
	<mm:present referid="action_move">
	<mm:import id="currenttime"><%= System.currentTimeMillis() %></mm:import>
	<mm:redirect page="/email/moveitems.jsp" referids="$referids,mailbox,ids,currenttime">
	    <mm:param name="callerpage">/email/index.jsp</mm:param>
	</mm:redirect>
    </mm:present>
</mm:present>



<div class="rows">

<div class="navigationbar">
  <div class="titlebar">
    <img src="<mm:treefile write="true" page="/gfx/icon_email.gif" objectlist="$includePath" />" width="25" height="13" border="0" title="<di:translate key="email.email" />" alt="<di:translate key="email.email" />" /> <di:translate key="email.email" />
  </div>
</div>


<div class="folders">
  <div class="folderHeader">
    <di:translate key="email.mailboxes" />
  </div>
  <div class="folderBody">
    <a href="<mm:treefile page="/email/createmailbox.jsp" objectlist="$includePath" referids="$referids">
	               <mm:param name="mailbox"><mm:write referid="mailbox"/></mm:param>
	               <mm:param name="callerpage">/email/index.jsp</mm:param>
	             </mm:treefile>">
	  <img src="<mm:treefile page="/email/gfx/map maken.gif" objectlist="$includePath" referids="$referids"/>" border="0" title="<di:translate key="email.createfolder" />" alt="<di:translate key="email.createfolder" />"/></a>

    <a href="<mm:treefile page="/email/changemailbox.jsp" objectlist="$includePath" referids="$referids">
                 <mm:param name="mailbox"><mm:write referid="mailbox"/></mm:param>
	             <mm:param name="callerpage">/email/index.jsp</mm:param>
	           </mm:treefile>">
      <img src="<mm:treefile page="/email/gfx/map hernoemen.gif" objectlist="$includePath" referids="$referids"/>" border="0" title="<di:translate key="email.renamefolder" />" alt="<di:translate key="email.renamefolder" />"/></a>

    <a href="<mm:treefile page="/email/deletemailbox.jsp" objectlist="$includePath" referids="$referids">
                 <mm:param name="mailbox"><mm:write referid="mailbox"/></mm:param>
                 <mm:param name="callerpage">/email/index.jsp</mm:param>
               </mm:treefile>">
      <img src="<mm:treefile page="/email/gfx/verwijder map.gif" objectlist="$includePath" referids="$referids"/>" border="0" title="<di:translate key="email.deletefolder" />" alt="<di:translate key="email.deletefolder" />"/></a>

    <br/><br/>

    <mm:treeinclude page="/email/mailboxes.jsp" objectlist="$includePath" referids="$referids" />
  </div>
</div>


<form action="<mm:treefile page="/email/index.jsp" objectlist="$includePath" referids="$referids"/>" method="POST">
    <input type="hidden" name="mailbox" value="<mm:write referid="mailbox"/>">


<div class="mainContent">
  <div class="contentHeader">
    <mm:write referid="mailboxname"/>
  </div>
  <div class="contentSubHeader">
   <%-- <a href="<mm:treefile page="/email/write/write.jsp" objectlist="$includePath" referids="$referids"/>">
      <img src="<mm:treefile write="true" page="/gfx/icon_emailschrijven.gif" objectlist="$includePath" />" width="50" height="28" border="0" alt="<di:translate key="email.writenewemail" />" />
      </a> --%>



      <a href="<mm:treefile page="/email/write/write.jsp" objectlist="$includePath" referids="$referids">
                 <mm:param name="mailboxname"><mm:write referid="mailboxname"/></mm:param>
                
               </mm:treefile>">
      <img src="<mm:treefile write="true"  page="/gfx/icon_emailschrijven.gif" objectlist="$includePath" referids="$referids"/> "width="50" height="28" border="0" title="<di:translate key="email.deletefolder" />" alt="<di:translate key="email.writenewemail" />"/></a>


       <input type="image" src="<mm:treefile page="/email/gfx/verplaats geselecteerde.gif" objectlist="$includePath" referids="$referids"/>" border="0" title="<di:translate key="email.moveselected" />" alt="<di:translate key="email.moveselected" />" name="action_move" value=move"/>

       <input type="image" src="<mm:treefile page="/email/gfx/verwijder geselecteerde.gif" objectlist="$includePath" referids="$referids"/>" border="0" title="<di:translate key="email.deleteselected" />" alt="<di:translate key="email.deleteselected" />" name="action_delete" value="delete"/>

       <a href="<mm:treefile page="/email/mailrule.jsp" objectlist="$includePath" referids="$referids"/>"><img src="<mm:treefile page="/email/gfx/mail_rule.gif" objectlist="$includePath" referids="$referids"/>" title="<di:translate key="email.mailrules" />" alt="<di:translate key="email.mailrules" />" border="0"></a>
       
  </div>
  <div class="contentBody">
    <mm:treeinclude page="/email/mailbox/mailbox.jsp" objectlist="$includePath" referids="$referids" />
  </div>
</div>
</div>
</form>
<mm:treeinclude page="/cockpit/cockpit_footer.jsp" objectlist="$includePath" referids="$referids" />
</mm:cloud>
</mm:content>
