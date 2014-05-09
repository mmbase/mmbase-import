<%@include file="../header.jsp" %>
  <mm:cloud name="mmbase" method="http" jspvar="cloud" rank="$rank">
  <%  Stack states = (Stack)session.getValue("mmeditors_states");
      Properties state = (Properties)states.peek();
      String transactionID = state.getProperty("transaction");
      String managerName = state.getProperty("manager");
      String nodeID = state.getProperty("node");
      Module mmlanguage;
      try {
         mmlanguage = cloud.getCloudContext().getModule("mmlanguage");
      } catch (NotFoundException nfe) {
         mmlanguage = null;
      }
  %>
  <mm:import externid="field" required="true" />
  <mm:import externid="action" />
  <mm:notpresent referid="action">
    <mm:transaction name="<%=transactionID%>" commitonclose="false">
      <mm:node number="<%=nodeID%>">

      <form method="post" action="imgdevice-upload.jsp" target="contentarea" enctype="multipart/form-data">
        <table class="fieldeditor">
          <tr ><td class="fieldcaption">Selecteer een nieuwe afbeelding :</td></tr>
          <tr>
            <td class="editfield">
              <p class="editfield">
              <mm:field name="$field">
                <mm:fieldinfo type="input" />
              </mm:field></p>
                <input type="hidden" name="device" value="upload" />
                <input type="hidden" name="field" value="<mm:write referid="field" />" />
                <input type="image" class="button" name="action" value="commit" src="../gfx/btn.red.gif" /> <%=translate(mmlanguage,"ok")%>
           </td>
         </tr>
        </table>
      </form>
<%@include file="fieldhelp.jsp" %>
      </mm:node>
    </mm:transaction>
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




