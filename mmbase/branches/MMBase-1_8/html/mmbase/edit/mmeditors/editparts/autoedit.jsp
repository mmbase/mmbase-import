<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@include file="../header.jsp" %><html xmlns="http://www.w3.org/1999/xhtml">
  <mm:cloud name="mmbase" method="http" jspvar="cloud" rank="$rank">
  <% Stack states = (Stack)session.getValue("mmeditors_states");
     Properties state = (Properties)states.peek();
     String transactionID = state.getProperty("transaction");
     String managerName = state.getProperty("manager");
     String nodeID = state.getProperty("node");
     String currentState = state.getProperty("state");
     Module mmlanguage;
     try {
        mmlanguage = cloud.getCloudContext().getModule("mmlanguage");
     } catch (NotFoundException nfe) {
        mmlanguage = null;
     }
  %>
  <mm:import externid="field" required="true" />
  <head>
    <title>Editors</title>
    <link rel="stylesheet" href="../css/mmeditors.css" type="text/css" />
  </head>
<mm:import externid="action" />
<mm:notpresent referid="action">
  <body>
  <mm:transaction name="<%=transactionID%>" commitonclose="false">
    <mm:node number="<%=nodeID%>">
      <table class="fieldeditor">
        <tr ><td class="fieldcaption"><%=translate(mmlanguage,"change_field")%> : <mm:write referid="field" /></td></tr>
        <tr>
          <td class="editfield">
            <form method="get" action="<mm:url page="autoedit.jsp" />" target="contentarea">
              <p class="editfield">
              <mm:field name="$field">
                <mm:fieldinfo type="input" />
              </mm:field>
              </p>
              <input type="hidden" name="field" value="<mm:write referid="field" />" />
              <input type="image" class="button" name="action" value="commit" src="../gfx/btn.red.gif" /> <%=translate(mmlanguage,"ok")%>
            </form>
          </td>
        </tr>
      </table>
<%@include file="fieldhelp.jsp" %>
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

