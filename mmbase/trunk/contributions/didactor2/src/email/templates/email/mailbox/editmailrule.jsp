<%@taglib uri="http://www.didactor.nl/ditaglib_1.0" prefix="di" %>
<%@taglib uri="http://www.mmbase.org/mmbase-taglib-2.0" prefix="mm" %>
<mm:content postprocessor="reducespace" expires="0">
<mm:cloud method="delegate" jspvar="cloud">

<%@include file="/shared/setImports.jsp"%>

<mm:treeinclude page="/cockpit/cockpit_header.jsp" objectlist="$includePath" referids="$referids">
  <mm:param name="extraheader">
    <title><di:translate key="email.email" /></title>
  </mm:param>
</mm:treeinclude>

<div class="rows">

  <div class="navigationbar">
    <div class="titlebar">
      <img src="<mm:treefile write="true" page="/gfx/icon_email.gif" objectlist="$includePath" />" width="25" height="13" border="0"  title="<di:translate key="email.email" />" alt="<di:translate key="email.email" />" /> <di:translate key="email.email" />
    </div>
  </div>

  <div class="folders">
    <div class="folderHeader">
      <di:translate key="email.mailrules" />
    </div>
    <div class="folderBody">
  
    </div>
  </div>


  <div class="mainContent">
    <div class="contentHeader">
      
    </div>
    <div class="contentSubHeader">
         
    </div>
    <div class="contentBodywit">
      <br><br><br>
      <mm:import externid="rule"/>
      <mm:import externid="type"/>
      <mm:import externid="folder"/>
      <mm:import externid="action_commit"/>
      <mm:present referid="action_commit">
        <mm:present referid="rule">
          <mm:isempty referid="rule" inverse="true">
            <mm:node number="$user">
              <mm:relatednodes type="mailboxes" constraints="m_type=0" max="1">
                <mm:field name="number" id="sfolder"/>
              </mm:relatednodes>
        
              <mm:createrelation source="sfolder" destination="folder" role="$type">
                <mm:setfield name="rule"><mm:write referid="rule"/></mm:setfield>
              </mm:createrelation>
            </mm:node>
            <mm:redirect page="/email/mailrule.jsp" referids="$referids">
              <mm:param name="callerpage">/email/mailrule.jsp</mm:param>
            </mm:redirect>
          </mm:isempty>
        </mm:present>
      </mm:present>

      <mm:import externid="action_back"/>
      <mm:present referid="action_back">
        <mm:redirect page="/email/mailrule.jsp" referids="$referids">
          <mm:param name="callerpage">/email/mailrule.jsp</mm:param>
        </mm:redirect>
      </mm:present>

      <form action="editmailrule.jsp" method="POST">
        <table class="Font">
          <tr>
            <th><di:translate key="email.matchwhat" /></th>
            <td>
              <select name="type">
                <option value="subjectmailrule"><di:translate key="email.subject" /></option>
                <option value="sendermailrule"><di:translate key="email.sender" /></option>
              </select>
            </td>
          </tr>
          <tr>
            <th><di:translate key="email.substring" /></th>
            <td><input type="text" name="rule" size="80" class="formbutton"></td>
          </tr>
          <tr>
            <th><di:translate key="email.folder" /></th>
            <td>
              <select name="folder">
                <mm:node number="$user">
                  <mm:relatednodes type="mailboxes" orderby="type, name">
                    <option value="<mm:field name="number"/>"><mm:field name="name"/></option>
                  </mm:relatednodes>
                </mm:node>
              </select>
            </td>
          </tr>
          <tr><td></td><td><input class="formbutton" type="submit" name="action_back" value="<di:translate key="email.back"/>" /> <input type="submit" name="action_commit" value="Ok" class="formbutton"></td></tr>
        </table>
      </form>
    </div>
  </div>
</div>

<mm:treeinclude page="/cockpit/cockpit_footer.jsp" objectlist="$includePath" referids="$referids" />
</mm:cloud>
</mm:content>
