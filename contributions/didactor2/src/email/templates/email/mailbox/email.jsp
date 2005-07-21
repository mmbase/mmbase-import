<%--
  TODO:
  - open the 'inbox' always if there is no mailbox given
  - clickthrough to the 'show the email' page
  - finish other functionality mentioned in FO
--%>
<%@taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" %>
<%@taglib uri="http://www.didactor.nl/ditaglib_1.0" prefix="di" %>
<mm:content postprocessor="reducespace">
<mm:cloud loginpage="/login.jsp" jspvar="cloud">
<mm:import externid="mailbox">-1</mm:import>
<mm:import externid="email">-1</mm:import>
<%@include file="/shared/setImports.jsp" %>

<mm:node number="$email" notfound="skip">
      <mm:field name="type" write="false">
        <mm:compare value="2">
            <mm:setfield name="type">0</mm:setfield>
        </mm:compare>
      </mm:field>
</mm:node> 


<mm:treeinclude page="/cockpit/cockpit_header.jsp" objectlist="$includePath" referids="$referids">
  <mm:param name="extraheader">
    <title>Webmail</title>
  </mm:param>
</mm:treeinclude>

<div class="rows">

<div class="navigationbar">
  <div class="titlebar">
    Webmail
  </div>
</div>

<div class="folders">
  <div class="folderHeader">
    Mailboxes
  </div>
  <div class="folderBody">
    <mm:treeinclude page="/email/mailboxes.jsp" objectlist="$includePath" referids="$referids" />
  </div>
</div>

<div class="mainContent">
  <div class="contentHeader">
    &nbsp;
  </div>
  <div class="contentSubHeader">
	<a href="<mm:treefile page="/email/write/write.jsp" objectlist="$includePath" referids="$referids">
	    <mm:param name="reply"><mm:write referid="email"/></mm:param>
	</mm:treefile>"><img src="<mm:treefile page="/email/gfx/reply_mail.gif" objectlist="$includePath" referids="$referids"/>" alt="<di:translate id="reply">Beantwoorden</di:translate>" border="0"></a>
    &nbsp; &nbsp;&nbsp;	

	<a href="<mm:treefile page="/email/write/write.jsp" objectlist="$includePath" referids="$referids">
            <mm:param name="replyAll"><mm:write referid="email"/></mm:param>
        </mm:treefile>"><img src="<mm:treefile page="/email/gfx/reply_all_mail.gif" objectlist="$includePath" referids="$referids"/>" alt="<di:translate id="replyall">Beantwoorden aan groep</di:translate>" border="0"></a>
	    &nbsp; &nbsp;&nbsp;	

	<a href="<mm:treefile page="/email/write/write.jsp" objectlist="$includePath" referids="$referids">
	    <mm:param name="forward"><mm:write referid="email"/></mm:param>
	</mm:treefile>"><img src="<mm:treefile page="/email/gfx/forward_mail.gif" objectlist="$includePath" referids="$referids"/>" alt="<di:translate id="forward">Doorsturen</di:translate>" border="0"></a>
    
  </div>
  <div class="contentBodywit">
    <br><br><br>
    <mm:node number="$email" notfound="skip">
      Van: <mm:field name="from" /> <br />
      Aan: <mm:field name="to" /> <br />
      Datum: <mm:field name="gui(date)" /> <br />
      Onderwerp: <mm:field name="subject"/> <br />
      <hr />
      Tekst:
      <br />
      <mm:field name="body" jspvar="dummy" vartype="String" escape="text/plain">
        <%= dummy.replaceAll("\n","<br/>\n") %>
      </mm:field>
      <hr />
      <mm:relatednodes type="attachments">
        <mm:first>
          <mm:import id="gfx_attachment"><mm:treefile page="/email/gfx/attachment.gif" objectlist="$includePath" referids="$referids" /></mm:import>
          Bijlage:
        </mm:first>
        <br />
        <img src="<mm:write referid="gfx_attachment"/>"/> <a href="<mm:attachment/>"><mm:field name="title"/></a>
      </mm:relatednodes>

   </mm:node>
  </div>
</div>
</div>
<mm:treeinclude page="/cockpit/cockpit_footer.jsp" objectlist="$includePath" referids="$referids" />
</mm:cloud>
</mm:content>
