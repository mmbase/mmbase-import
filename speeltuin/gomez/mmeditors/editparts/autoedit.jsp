<%@include file="../header.jsp" %>
  <mm:cloud name="mmbase" method="http" jspvar="cloud">
  <%  Stack states = (Stack)session.getValue("mmeditors_states");
      Properties state = (Properties)states.peek();
      String transactionID = state.getProperty("transaction");
      String managerName = state.getProperty("manager");
      String nodeID = state.getProperty("node");
      String currentState = state.getProperty("state");
      Module mmlanguage = cloud.getCloudContext().getModule("mmlanguage");
  %>
  <mm:import externid="field" required="true" />
  <head>
    <title>Editors</title>
    <link rel="stylesheet" href="../css/mmeditors.css" type="text/css" />
    <style>
<%@include file="../css/mmeditors.css" %>     
    </style>
  </head>
<mm:import externid="action" />  
<mm:notpresent referid="action">  
  <body>
  <mm:transaction name="<%=transactionID%>" commitonclose="false"> 
    <mm:node number="<%=nodeID%>">
      <table class="fieldeditor">
        <tr ><td class="fieldcaption"><%=mmlanguage.getInfo("GET-change_field")%> : <mm:write referid="field" /></td></tr>
        <tr>
          <td class="editfield">
            <form method="get" action="<mm:url page="autoedit.jsp" />" target="contentarea">
              <p class="editfield">
              <mm:field name="$field">
                <mm:fieldinfo type="input" />
              </mm:field>
              </p>
              <input type="hidden" name="field" value="<mm:write referid="field" />" /> 
              <input type="image" class="button" name="action" value="commit" src="../gfx/btn.red.gif" /> <%=mmlanguage.getInfo("GET-ok")%>
            </form>
          </td>
        </tr>
      </table>
      
      <mm:field name="$field">
        <mm:fieldinfo type="description" >
          <mm:isnotempty>
            <table class="fieldeditor">
              <tr>
                <td class="editfield">
                  <mm:write />
                </td>
              </tr>
            </table>
          </mm:isnotempty>
        </mm:fieldinfo>
      </mm:field>
    </mm:node>
  </mm:transaction>
  </body>
</mm:notpresent>

<mm:present referid="action">
  <mm:compare referid="action" value="commit" >
    <mm:transaction name="<%=transactionID%>" commitonclose="false">
      <mm:node number="<%=nodeID%>">
        <mm:field name="$field">
          <mm:fieldinfo type="useinput" />
          <% if (!"new".equals(currentState)) { 
                currentState="edit";
                state.put("state",currentState);
             } 
          %>
        </mm:field>
      </mm:node>
    </mm:transaction>
  </mm:compare>
<%@include file="nextfield.jsp" %> 
</mm:present>  
  </mm:cloud>
</html>

