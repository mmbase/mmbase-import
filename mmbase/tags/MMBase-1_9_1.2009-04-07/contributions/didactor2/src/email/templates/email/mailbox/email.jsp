<%--
  TODO:
  - open the 'inbox' always if there is no mailbox given
  - clickthrough to the 'show the email' page
  - finish other functionality mentioned in FO
--%>
<%@taglib uri="http://www.mmbase.org/mmbase-taglib-2.0" prefix="mm" 
%><%@taglib uri="http://www.didactor.nl/ditaglib_1.0" prefix="di" 
%><mm:content postprocessor="reducespace">
<mm:cloud rank="didactor user">
  <mm:import externid="mailbox">-1</mm:import>
  <mm:import externid="sf" />
  <mm:import externid="so" />
  <mm:import externid="email">-1</mm:import>
  <jsp:directive.include file="/shared/setImports.jsp" />
  
  <mm:node number="$email" notfound="skip">
    <mm:field name="type">
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
        <a href="<mm:treefile page="/email/write/write.jsp" objectlist="$includePath" referids="$referids,mailbox?,so?,sf?">
          <mm:param name="reply"><mm:write referid="email"/></mm:param>
          </mm:treefile>"><img src="<mm:treefile page="/email/gfx/reply_mail.gif" objectlist="$includePath" referids="$referids"/>"  title="<di:translate key="email.reply"/>" alt="<di:translate key="email.reply" />" border="0" />
        </a>
        &nbsp; &nbsp;&nbsp;<!-- well, why use CSS if it can be done like this -->
          
        <a href="<mm:treefile page="/email/write/write.jsp" objectlist="$includePath" referids="$referids,mailbox?,so?,sf?">
          <mm:param name="replyAll"><mm:write referid="email"/></mm:param>
          </mm:treefile>"><img src="<mm:treefile page="/email/gfx/reply_all_mail.gif" objectlist="$includePath" referids="$referids"/>" title="<di:translate key="email.replyall"/>" alt="<di:translate key="email.replyall" />" border="0" />
        </a>
        &nbsp; &nbsp;&nbsp;                
        <a href="<mm:treefile page="/email/write/write.jsp" objectlist="$includePath" referids="$referids,mailbox?,so?,sf?">
          <mm:param name="forward"><mm:write referid="email"/></mm:param>
          </mm:treefile>"><img src="<mm:treefile page="/email/gfx/forward_mail.gif" objectlist="$includePath" referids="$referids"/>" title="<di:translate key="email.forward"/>" alt="<di:translate key="email.forward" />" border="0" />
        </a>
          
      </div>
      <div class="contentBodywit email">
        <br /><br /><br /><!-- sigh -->
        <mm:node number="$email" notfound="skip">
          <di:translate key="email.from" />: <mm:field name="from" /> <br />
          <di:translate key="email.to_caption" />: <mm:field name="to" /> <br />
          <di:translate key="email.date" />: <mm:field name="date"><mm:time format=":FULL.FULL" /></mm:field> <br />
          <di:translate key="email.subject" />: <mm:field name="subject"/> <br />
          <mm:functioncontainer>
            <mm:param name="field">headers</mm:param>
            <mm:function name="gui" escape="trimmer">
              <mm:compare value="<br />" inverse="true">
                <div class="headers hide" id="email_headers">
                  <a class="show" href="javascript:return void(0);" onclick="document.getElementById('email_headers').className = 'headers show';">+</a>
                  <a class="hide" href="javascript:return void(0);" onclick="document.getElementById('email_headers').className = 'headers hide';">-</a>
                  <div class="values">
                    <mm:write escape="none"/>
                  </div>
                </div>
              </mm:compare>
            </mm:function>
          </mm:functioncontainer>
          <hr />
          <di:translate key="email.text"/>:
          <br />
          <mm:functioncontainer>
            <mm:param name="field">body</mm:param>
            <mm:function name="gui" escape="links" />
          </mm:functioncontainer>
          <hr />
          <mm:relatednodes type="attachments">
            <mm:first>
              <mm:import id="gfx_attachment"><mm:treefile page="/email/gfx/attachment.gif" objectlist="$includePath" referids="$referids" /></mm:import>
              <di:translate key="email.attachments" />:
            </mm:first>
            <br />
            <a href="<mm:attachment/>">
            <img src="<mm:write referid="gfx_attachment"/>"/> 
              <mm:field name="title" write="true">
                <mm:isempty>
                  <mm:field name="filename" write="true" />
                </mm:isempty>
              </mm:field>
          </a>
          </mm:relatednodes>
        </mm:node>
      </div>
    </div>
  </div>
  <mm:treeinclude page="/cockpit/cockpit_footer.jsp" objectlist="$includePath" referids="$referids" />
</mm:cloud>
</mm:content>
