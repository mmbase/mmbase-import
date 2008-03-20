<%@include file="includes/templateheader.jsp" %>
<%@include file="includes/functions.jsp" %>
<%@include file="includes/image_vars.jsp" %>
<mm:cloud logon="admin" pwd="<%= (String) com.finalist.mmbase.util.CloudFactory.getAdminUserCredentials().get("password") %>" method="pagelogon" jspvar="cloud">
<mm:log jspvar="log">
  <table cellpadding="0" cellspacing="0" border="0" style="width:780px;">
  <%@include file="includes/nav.jsp" %>
  <tr>
    <td></td>
    <td></td>
    <td></td>
    <td colspan="21">
    <table cellpadding="3" cellspacing="0" border="1" class="content">
      <tr class="contact"><td colspan="2"></td></tr>
      <% String defaultEmailAddress = ""; %>
      <mm:node number="<%= paginaID %>">
        <mm:field name="kortetitel" jspvar="dummy" vartype="String" write="false">
          <% defaultEmailAddress = dummy; %>
        </mm:field>
      </mm:node>
      <% 
      if(!artikelID.equals("2")){
         %><form name="mailform" method="post" action="contact.jsp?p=<%= paginaID %>&a=2">
            <tr>
              <td class="def" style="width:100px;"><bean:message bundle="<%= "VANHAM." + language %>" key="contact.name" /></td>
              <td class="def"><input type="text" name="n" style="width:400px;" /></td>
            </tr>
            <tr>
              <td class="def" style="width:100px;"><bean:message bundle="<%= "VANHAM." + language %>" key="contact.email" /></td>
              <td class="def"><input type="text" name="e" style="width:400px;" /></td>
            </tr>
            <tr>
              <td class="def" style="width:100px;"><bean:message bundle="<%= "VANHAM." + language %>" key="contact.text" />
              </td><td class="def"><textarea rows="4" name="d" style="width:400px;overflow:auto;"></textarea></td>
            </tr>
            <tr>
              <td class="def" style="width:100px;"></td>
              <td class="def"><input type="submit" value="<bean:message bundle="<%= "VANHAM." + language %>" key="contact.button" />" class="contact" /></td>
            </tr>
           </form>
         <% 
      } else if(emailId!=null&&!emailId.equals("")&&emailId.indexOf("@")>1){
         %><mm:createnode type="email" id="websitemail"
              ><mm:setfield name="subject">Vraag om informatie van de website</mm:setfield
              ><mm:setfield name="from"><%= emailId %></mm:setfield
              ><mm:setfield name="to"><%= defaultEmailAddress %></mm:setfield
              ><mm:setfield name="replyto"><%= emailId %></mm:setfield
              ><mm:setfield name="body"><HTML><%=  
                 "Het volgende bericht is verzonden vanaf de website:<br><br>" 
                  + "&#149; Naam: " + nameId + "<br><br>"
                  + "&#149; Vraag: " + textId + "<br><br>"
             %></HTML></mm:setfield
          ></mm:createnode
          ><mm:node referid="websitemail"
	  	><mm:field name="mail(oneshotkeep)"
	  /></mm:node>
	  <tr>
            <td class="def" style="width:690px;padding-top:26px;padding-bottom:14px;">
              <div align="left">
                <h3><bean:message bundle="<%= "VANHAM." + language %>" key="contact.ok.title" /></h3>
                <bean:message bundle="<%= "VANHAM." + language %>" key="contact.ok.message" />
              </div>
            </td>
          </tr>
        <% 
       } else { 
        %>
        <tr><td class="def" style="width:690px;padding-top:26px;padding-bottom:14px;">
          <div align="left">
            <h3><bean:message bundle="<%= "VANHAM." + language %>" key="contact.warning.title" /></h3>
            <bean:message bundle="<%= "VANHAM." + language %>" key="contact.warning.message" /><br><br>
            <a href="javascript:history.go(-1);"><bean:message bundle="<%= "VANHAM." + language %>" key="contact.warning.back" /></a>
          </div>
        </td><tr/>
        <% 
      } %>
    </table>
  </td>
  </tr>
  </table>
  <br/>
</mm:log>
</mm:cloud>
<%@include file="includes/templatefooter.jsp" %>