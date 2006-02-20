<%--
  TODO:
  - open the 'inbox' always if there is no mailbox given
  - clickthrough to the 'show the email' page
  - finish other functionality mentioned in FO
--%>
<%@taglib uri="http://www.mmbase.org/mmbase-taglib-1.1" prefix="mm" %>
<%@taglib uri="http://www.didactor.nl/ditaglib_1.0" prefix="di" %>
<mm:content postprocessor="reducespace">
<mm:cloud method="delegate" jspvar="cloud">
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
    <title><di:translate key="email.email" /></title>
  </mm:param>
</mm:treeinclude>

<div class="rows">
  <div class="navigationbar">
    <div class="titlebar">
      <di:translate key="email.email" /> 
    </div>
  </div>

  <div class="folders">
    <div class="folderHeader">
      <di:translate key="email.mailboxes" />
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
               </mm:treefile>"><img src="<mm:treefile page="/email/gfx/reply_mail.gif" objectlist="$includePath" referids="$referids"/>"  title="<di:translate key="email.reply"/>" alt="<di:translate key="email.reply" />" border="0"></a>
      &nbsp; &nbsp;&nbsp;        

      <a href="<mm:treefile page="/email/write/write.jsp" objectlist="$includePath" referids="$referids">
                 <mm:param name="replyAll"><mm:write referid="email"/></mm:param>
               </mm:treefile>"><img src="<mm:treefile page="/email/gfx/reply_all_mail.gif" objectlist="$includePath" referids="$referids"/>" title="<di:translate key="email.replyall"/>" alt="<di:translate key="email.replyall" />" border="0"></a>
      &nbsp; &nbsp;&nbsp;        

      <a href="<mm:treefile page="/email/write/write.jsp" objectlist="$includePath" referids="$referids">
                 <mm:param name="forward"><mm:write referid="email"/></mm:param>
              </mm:treefile>"><img src="<mm:treefile page="/email/gfx/forward_mail.gif" objectlist="$includePath" referids="$referids"/>" title="<di:translate key="email.forward"/>" alt="<di:translate key="email.forward" />" border="0"></a>
    
  </div>
  <div class="contentBodywit">
    <br><br><br>
    <mm:node number="$email" notfound="skip">
      <di:translate key="email.from" />: <mm:field name="from" /> <br />
      <di:translate key="email.to_caption" />: <mm:field name="to" /> <br />
      <di:translate key="email.date" />: <mm:field name="gui(date)" /> <br />
      <di:translate key="email.subject" />: <mm:field name="subject"/> <br />
      <hr />
      <di:translate key="email.text"/>:
      <br />
      <mm:field name="body" jspvar="dummy" vartype="String" escape="text/plain">
        <%= dummy.replaceAll("\n","<br/>\n") %>
      </mm:field>
      <hr />
      <mm:relatednodes type="attachments">
        <mm:first>
          <mm:import id="gfx_attachment"><mm:treefile page="/email/gfx/attachment.gif" objectlist="$includePath" referids="$referids" /></mm:import>
          <di:translate key="email.attachments" />:
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
