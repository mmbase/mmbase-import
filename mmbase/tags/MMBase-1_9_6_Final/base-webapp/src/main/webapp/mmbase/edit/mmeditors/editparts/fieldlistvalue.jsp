<%--
 This file is to be included if you want to easily make an optionlist-like guitype.
 Just create a hasmap named 'values', with name/value parts corresponding to the value to fill in the field, and the description thereof.

 Note that another way to make optionlists available is to use autoedit,jsp, but to specify the required name/value lists in a propertyfile.
 (in org.mmbase.module.bridge.jsp.taglib.typehandler.resources)
 I.e. for the guitype 'functions', you can create a propertyfile functions.properties, or a language-specific functions_<lang>.properties.

 Note that while this is more generic, it is also more cumbersome to update (may require the webapp to restart, for instance),
 which is why I left this option open.

--%><!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
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
            <form method="get" action="<mm:url page="channellogin.jsp" />" target="contentarea">

              <mm:field name="$field">
                <select name="fieldvalue">
                  <% for (Iterator i=values.keySet().iterator(); i.hasNext();) {
                      String index=(String)i.next();
                  %>
                  <option name="<%=index%>" <mm:compare value="<%=index%>">selected="selected"</mm:compare> >
                  <%=values.get(index)%>
                  </option>
                  <% } %>
                </select>
              </mm:field>

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
      <mm:import externid="fieldvalue" required="true" />
      <mm:node number="<%=nodeID%>">
        <mm:setfield name="$field"><mm:write referid="fieldvalue" /></mm:setfield>
        <% if (!"new".equals(currentState)) {
              currentState="edit";
              state.put("state",currentState);
           }
        %>
      </mm:node>
    </mm:transaction>
  </mm:compare>
<%@include file="nextfield.jsp" %>
</mm:present>
  </mm:cloud>
</html>

