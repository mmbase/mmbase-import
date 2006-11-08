<%@taglib uri="http://www.mmbase.org/mmbase-taglib-2.0" prefix="mm" 
%><%@taglib uri="http://www.didactor.nl/ditaglib_1.0" prefix="di" 
%><mm:content postprocessor="none"><%-- no reducespace: it messes with the textarea --%>
<mm:cloud rank="basic user">
  <jsp:directive.include file="/shared/setImports.jsp" />
  
  <mm:import externid="back"/>
  <mm:present referid="back">
    <mm:redirect page="/email/index.jsp"/>
  </mm:present>
  
<mm:import externid="mailbox"/>
<mm:import id="emaildomain" escape="trimmer"><mm:treeinclude write="true" page="/email/init/emaildomain.jsp" objectlist="$includePath" referids="$referids" /></mm:import>
<mm:import id="to"></mm:import>
<mm:import id="cc"></mm:import>
<mm:import id="bcc"></mm:import>
<mm:import id="subject"></mm:import> 
<mm:import id="body"></mm:import>
<mm:import id="emailok">0</mm:import>

<%-- setup data according to some already existing mail --%>
<mm:import id="reply" externid="reply"/>
<mm:present referid="reply">
  <mm:import id="loadOld"><mm:write referid="reply"/></mm:import>
</mm:present>

<mm:import id="replyAll" externid="replyAll"/>
<mm:present referid="replyAll">
  <mm:import id="loadOld"><mm:write referid="replyAll"/></mm:import>
</mm:present>

<mm:import id="forward" externid="forward"/>
<mm:present referid="forward">
  <mm:import id="loadOld"><mm:write referid="forward"/></mm:import>
</mm:present>
  
<mm:present referid="loadOld">
  <mm:node referid="loadOld">
    <mm:present referid="reply">
      <mm:import id="to" reset="true"><mm:field name="from" escape="none"/></mm:import>
      <mm:import id="subject" reset="true">Re:<mm:field name="subject" escape="none"/></mm:import>
    </mm:present>
    <mm:present referid="replyAll">
      <mm:import id="to" reset="true"><mm:field name="from" escape="none"/></mm:import>
      <mm:import id="cc" reset="true"><mm:field name="cc" escape="none"/></mm:import>
      <mm:import id="bcc" reset="true"><mm:field name="bcc" escape="none"/></mm:import>
      <mm:import id="subject" reset="true">Re:<mm:field name="subject" escape="none"/></mm:import>
    </mm:present>
    <mm:present referid="forward">
      <mm:import id="subject" reset="true">Fw:<mm:field name="htmlsubject" escape="none"/></mm:import>
    </mm:present>

    <mm:field name="body" jspvar="body" vartype="String" write="false">
      <mm:field name="headers" jspvar="headers" vartype="String" write="false">
        <%
           // Strip XSS
           body = body.replaceAll("(?i)<[\\s]*/?script.*?>|<[\\s]*/?embed.*?>|<[\\s]*/?object.*?>|<[\\s]*a[\\s]*href[^>]*javascript[\\s]*:[^(^)^>]*[(][^)]*[)][^>]*>[^<]*(<[\\s]*/[\\s]*a[^>]*>)*", "");
        %>
        <mm:import id="body" reset="true"><%=body%></mm:import> 
      </mm:field>
    </mm:field>
  </mm:node>
</mm:present>

<mm:list nodes="$user" path="people,mailboxes" fields="mailboxes.number" constraints="[mailboxes.type]=1">
  <mm:field name="mailboxes.number" id="mailboxNumber" write="false"/>
  <mm:node referid="mailboxNumber" id="mailboxNode"/>
</mm:list>

<mm:list nodes="$user" path="people,mailboxes" fields="mailboxes.number" constraints="[mailboxes.type]=11">
  <mm:field name="mailboxes.number" id="draftMailboxNumber" write="false"/>
  <mm:node referid="draftMailboxNumber" id="draftMailboxNode"/>
</mm:list>

<mm:notpresent referid="draftMailboxNode">
  <mm:import id="draftMailboxNode"><mm:write referid="mailboxNode"/></mm:import>
</mm:notpresent>
 
<%-- edit existing email (not yet sent) --%>
<mm:import externid="id"/>
<mm:isnotempty referid="id">
  <mm:node number="$id" id="emailNode">
    <mm:import id="to" reset="true"><mm:field name="to" escape="none"/></mm:import>
    <mm:import id="cc" reset="true"><mm:field name="cc" escape="none"/></mm:import>
    <mm:import id="bcc" reset="true"><mm:field name="bcc" escape="none"/></mm:import>
    <mm:import id="subject" reset="true"><mm:field name="subject" escape="none"/></mm:import>
    <mm:import id="body" reset="true"><mm:field name="body" escape="none"/></mm:import>
  </mm:node>
</mm:isnotempty>
  
<%-- default: read data from request --%>
<mm:import id="inputto" externid="to" reset="true"/>
<mm:import id="inputcc" externid="cc" reset="true"/>
<mm:import id="inputbcc" externid="bcc" reset="true"/>
<mm:import id="inputsubject" externid="subject" reset="true"/>
<mm:import id="inputbody" externid="body" reset="true"/>
<mm:present referid="inputto">
  <mm:import id="to" reset="true"><mm:write referid="inputto" escape="none"/></mm:import>
</mm:present>
<mm:present referid="inputcc">
  <mm:import id="cc" reset="true"><mm:write referid="inputcc" escape="none"/></mm:import>
</mm:present>
<mm:present referid="inputbcc">
  <mm:import id="bcc" reset="true"><mm:write referid="inputbcc" escape="none"/></mm:import>
</mm:present>
<mm:present referid="inputsubject">
  <mm:import id="subject" reset="true"><mm:write referid="inputsubject" escape="none"/></mm:import>
</mm:present>
<mm:present referid="inputbody">
  <mm:import id="body" reset="true"><mm:write referid="inputbody" escape="none"/></mm:import>
</mm:present>

<mm:import jspvar="to"><mm:write referid="to" escape="none"/></mm:import>
<% to = to.replaceAll("^\\s*<(\\S+)>\\s*$","$1"); %>
<mm:import id="to" reset="true"><%= to %></mm:import>
 
<mm:import externid="field"/>
<mm:present referid="field"><%-- extra mail addresses from addressbook --%>
  <mm:import externid="ids" vartype="List"/>
  <mm:present referid="ids">
    <mm:listnodes type="people" constraints="number IN ($ids)">
      <mm:import id="tmp_email" reset="true"><mm:field name="email"/></mm:import>
      <mm:isempty referid="tmp_email">
        <mm:import id="tmp_email" reset="true"><mm:field name="username"/></mm:import>
      </mm:isempty>
      <mm:import id="$field" reset="true"><mm:isempty referid="$field" inverse="true"><mm:write referid="$field"/>, </mm:isempty><mm:write referid="tmp_email"/></mm:import>
    </mm:listnodes>
  </mm:present>
</mm:present>

<%-- no node yet, if input, create one --%>
<mm:present referid="emailNode" inverse="true">
  <mm:import id="testforinput"><mm:write referid="to"/><mm:write referid="cc"/><mm:write referid="body"/><mm:write referid="subject"/></mm:import>
  <mm:isnotempty referid="testforinput">
    <mm:createnode type="emails" id="emailNode"/>
    <mm:node referid="emailNode">
      <mm:import id="id" reset="true"><mm:field name="number"/></mm:import>
    </mm:node>
    <mm:createrelation role="related" source="draftMailboxNode" destination="emailNode"/>
  </mm:isnotempty>
</mm:present>

<mm:node number="$user">
  <mm:import id="from">"<mm:field name="firstname"/> <mm:field name="lastname"/>" <<mm:field name="username"/><mm:write referid="emaildomain" />></mm:import>
</mm:node>

 
<mm:isnotempty referid="subject">
  <mm:isnotempty referid="body">
    <mm:import id="ccText" jspvar="ccText"><mm:write referid="cc"/></mm:import>
    <mm:import id="toText" jspvar="toText"><mm:write referid="to"/></mm:import>
    <% if ( toText.trim().length() > 0 ) { %>  
      <mm:import id="emailok" reset="true">1</mm:import>
    <% } %>
  </mm:isnotempty>
</mm:isnotempty>

<mm:present referid="emailNode">
  <mm:node referid="emailNode">
    <mm:setfield name="from"><mm:write referid="from" escape="none"/></mm:setfield>
    <mm:setfield name="to"><mm:write referid="to" escape="none"/></mm:setfield>
    <mm:setfield name="cc"><mm:write referid="cc" escape="none"/></mm:setfield>
    <mm:setfield name="bcc"><mm:write referid="bcc" escape="none"/></mm:setfield>
    <mm:setfield name="subject"><mm:write referid="subject" escape="none"/></mm:setfield>
    <mm:setfield name="body"><mm:write referid="body" escape="none"/></mm:setfield>
    <mm:setfield name="type">0</mm:setfield>
  </mm:node>

  <mm:import id="testattachment" externid="att_handle"/>
  <mm:compare referid="testattachment" value="" inverse="true">
    <mm:import id="attachmentName" externid="att_handle_name" from="multipart"/>
    <mm:compare referid="attachmentName" value="" inverse="true">
      <mm:createnode type="attachments" id="newFile" jspvar="newFile">
        <mm:setfield name="title"><mm:write referid="attachmentName"/></mm:setfield>
        <mm:setfield name="filename"><mm:write referid="attachmentName"/></mm:setfield>
        <mm:fieldlist id="att" nodetype="attachments" fields="handle">
          <mm:fieldinfo type="useinput" />
        </mm:fieldlist>
      </mm:createnode>
      <mm:createrelation role="related" source="emailNode" destination="newFile"/>
      <mm:remove referid="newFile"/>
    </mm:compare>
  </mm:compare>
  <mm:remove referid="attachmentName"/>
    

<mm:present referid="emailNode">
  <mm:import externid="delete_attachments" vartype="List"/>
  <mm:present referid="delete_attachments">
    <mm:node number="$emailNode">
      <mm:relatednodes type="attachments" constraints="attachments.number IN ( $delete_attachments )">
        <mm:deletenode deleterelations="true"/>
      </mm:relatednodes>
    </mm:node>
  </mm:present>
</mm:present>

<mm:import externid="send_action"/> <%-- send button pressed --%>
  <mm:present referid="send_action">
    <mm:compare referid="emailok" value="1">
      <mm:node referid="emailNode">
        <mm:setfield name="type">1</mm:setfield>
      </mm:node>
      <mm:list nodes="$emailNode" path="emails,related,mailboxes">
        <mm:node element="related">
          <mm:deletenode deleterelations="false"/> 
        </mm:node>
      </mm:list>
      <mm:list nodes="$user" path="people,mailboxes" fields="mailboxes.number" max="1">
        <mm:remove referid="mailbox"/>
        <mm:field id="mailbox" name="mailboxes.number" write="false"/>
      </mm:list>

      <mm:createrelation role="related" source="mailboxNode" destination="emailNode"/> 
      <mm:treefile jspvar="forward" write="false" page="/email/index.jsp" objectlist="$includePath" referids="$referids">
        <mm:param name="provider" value="$provider"/>
        <mm:param name="mailbox" value="$mailbox"/>
      </mm:treefile>
      <%
        response.sendRedirect(forward);
      %>
    </mm:compare>
  </mm:present>
</mm:present>


<mm:import externid="lookup_to_action"/>
<mm:present referid="lookup_to_action">
  <mm:import id="redirect_url" jspvar="redirect_url"><mm:treefile page="/address/index.jsp" objectlist="$includePath" referids="$referids"/>&mailid=<mm:present referid="emailNode"><mm:write referid="emailNode"/></mm:present>&field=to</mm:import>
  <%    response.sendRedirect(redirect_url); %>
</mm:present>

<mm:import externid="lookup_cc_action"/>
<mm:present referid="lookup_cc_action">
  <mm:import id="redirect_url" jspvar="redirect_url"><mm:treefile page="/address/index.jsp" objectlist="$includePath" referids="$referids"/>&mailid=<mm:present referid="emailNode"><mm:write referid="emailNode"/></mm:present>&field=cc</mm:import>
  <%    response.sendRedirect(redirect_url); %>
</mm:present>

<mm:import externid="nooutput"/>
<mm:notpresent referid="nooutput">
<mm:treeinclude page="/cockpit/cockpit_header.jsp" objectlist="$includePath" referids="$referids">
  <mm:param name="extraheader">
    <title>Send mail</title>
    <script type="text/javascript">
      var editor = null;
      function initEditor() {
        var config = new HTMLArea.Config();
        config.editorURL = "/email/write/htmlarea/";
        config.toolbar = [ 
          [ 
            'forecolor', 'bold', 'italic', 'underline'
          ]
        ];
  
        HTMLArea.replace('body', config);
        //HTMLArea.replaceAll();
        return false;
      }
    </script>
    <script type="text/javascript" src="<mm:treefile page="/email/write/htmlarea/htmlarea.js" objectlist="$includePath" referids="$referids" />"></script>
    <script type="text/javascript" src="<mm:treefile page="/email/write/htmlarea/lang/en.js" objectlist="$includePath" referids="$referids" />"></script>
    <script type="text/javascript" src="<mm:treefile page="/email/write/htmlarea/dialog.js" objectlist="$includePath" referids="$referids" />"></script>
    <style type="text/css">
      @import url(<mm:treefile page="/email/write/htmlarea/css/htmlarea.css" objectlist="$includePath" referids="$referids" />);
    </style>
  </mm:param>
  <mm:param name="extrabody">onload="initEditor();"</mm:param>
</mm:treeinclude>

<div class="rows">

  <div class="navigationbar">
    <div class="titlebar">
      <img src="<mm:treefile write="true" page="/gfx/icon_email.gif" objectlist="$includePath" />" width="25" height="13" border="0" title="e-mail" alt="e-mail" /> E-mail
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
  
  <script>
    var email = new RegExp("\b.+@.+\b","i");

    function checkFields(frm) {
      if(frm.elements['to'].value.length == 0) {
        alert('<di:translate key="email.toempty" />');
        return false;
      }

      if(frm.elements['subject'].value.length == 0) {
        alert('<di:translate key="email.subjectempty" />');
        return false;
      }
      if (frm.elements['cc'].value.length > 0) {
        if (! email.test(frm.elements['cc'].value)) {
           alert(frm.elements['cc'].value + ': <di:translate key="email.suspiciousaddress" />');
           return false;
        }
      }
      if (frm.elements['bcc'].value.length > 0) {
        if (! email.test(frm.elements['bcc'].value)) {
           alert(frm.elements['bcc'].value + ': <di:translate key="email.suspiciousaddress" />');
           return false;
        }
      }

      return true;
    }
  </script>

  <mm:treeinclude write="true" page="/email/cockpit/menuitem.jsp" objectlist="$includePath">
    <mm:param name="icon">write message</mm:param>
    <mm:param name="text"><di:translate key="email.writemessage" /></mm:param>
  </mm:treeinclude>

  <div class="mainContent">
    <div class="contentHeader">
      <mm:import externid="mailboxname" from="parameters"/><mm:write referid="mailboxname" />
    </div>
    <div class="contentBodywit">
      <br/><br/><br/>
      <form action="<mm:treefile write="true" page="/email/write/write.jsp" objectlist="$includePath">
                      <mm:notpresent referid="course"><mm:param name="provider" value="$provider"/></mm:notpresent>
                    </mm:treefile>" method="post" enctype="multipart/form-data" name="webmailForm">
        <mm:present referid="id">
          <input type="hidden" name="id" value="${id}">
        </mm:present>
        <table class="font">
          <tr>
            <td><di:translate key="email.to" /> :&nbsp;</td>
            <td>
              <input type="text" class="formInput" name="to" value="<mm:write referid="to"/>"> 
              <input type="submit" name="lookup_to_action" value="<di:translate key="email.lookup_to" />" class="formbutton">
            </td>
          </tr>
          <tr>
            <td><di:translate key="email.cc" /> :&nbsp;</td>
            <td>
              <input type="text" class="formInput" name="cc" value="<mm:write referid="cc"/>">
              <input type="submit" name="lookup_cc_action" value="<di:translate key="email.lookup_cc" />" class="formbutton">
            </td>
          </tr>
          <di:getsetting component="email" setting="showbcc">
            <mm:compare value="true">
              <tr>
                <td><di:translate key="email.bcc" /> :&nbsp;</td>
                <td>
                  <input type="text" class="formInput" name="bcc" value="<mm:write referid="bcc"/>">
                  <input type="submit" name="lookup_bcc_action" value="<di:translate key="email.lookup_bcc" />" class="formbutton">
                </td>
              </tr>
            </mm:compare>
          </di:getsetting>
          <tr>
            <td><di:translate key="email.subject" /> :&nbsp;</td>
            <td><input type="text" class="formInput" name="subject" value="<mm:write referid="subject"/>"></td>
          </tr>
          <tr>
            <td></td>
            <td>
              <br/>
              <textarea id="body" name="body" class="formInput"><mm:write referid="body" escape="none"/></textarea>
              <br/>
            </td>
          </tr>
          <tr>
            <td style="vertical-align: top"><di:translate key="email.attachments" /> :&nbsp;</td>
            <td>
              <div class="attachment">
                <table border="0" class="Font">
                  <mm:present referid="emailNode">
                    <mm:node number="$emailNode">
                      <mm:relatednodes type="attachments">
                        <mm:first><tr><td>naam</td><td>wis</td></tr></mm:first>
                        <tr>
                          <td><a href="<mm:attachment/>"><mm:field name="title"/></a></td>
                          <td><input type="checkbox" name="delete_attachments" value="<mm:field name="number"/>"></td>
                        </tr>
                      </mm:relatednodes>
                    </mm:node>
                  </mm:present>
                  <tr>
                    <td colspan="2">
                      <input type="file" class="formInput" name="att_handle" size="30"  class="formbutton">
                    </td>
                    <td>
                      <input type="submit" name="att_attachment_action" value="<di:translate key="email.updateattachments" />" class="formbutton">
                    </td>
                  </tr>
                </table>
              </div>
            </td>
          </tr>
          <tr>
            <td>
              <input type="submit" name="send_action" value="<di:translate key="email.send" />" onclick="return checkFields(this.form)" class="formbutton">
            </td>
            <td>
              <input type="submit" name="back" value="<di:translate key="email.back" />" class="formbutton">
            </td>
          </tr>
        </table>
      </form>
    </div>
  </div>
</div>

<mm:treeinclude page="/cockpit/cockpit_footer.jsp" objectlist="$includePath" referids="$referids" />

</mm:notpresent>
</mm:cloud>
</mm:content>


