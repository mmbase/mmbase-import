<%@include file="../header.jsp" %>
  <mm:cloud name="mmbase" method="http" jspvar="cloud" rank="$rank">
  <mm:import externid="device" vartype="String" jspvar="device" required="true" />
  <%  Stack states = (Stack)session.getValue("mmeditors_states");
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
      NodeManager imgmanager = cloud.getNodeManager(device);
  %>
  <mm:import externid="field" vartype="String" jspvar="fieldname" required="true" />
  <mm:import externid="devicename" vartype="String" jspvar="devicename" required="true" />
  <mm:import externid="action" />
  <mm:notpresent referid="action">
  <table>
<%    Map m = new HashMap();
      m.put("REVERSE","YES");
      m.put("MAX","60");
      m.put("INDEXSYMBOL","$");
      m.put("TYPEFORMAT","digi$.jpg");
      m.put("PREVIEWFORMAT","digi$-s.jpg");

      NodeList ls = imgmanager.getList(devicename,null);
%>
      <form method="post" action="imgdevice-pccards.jsp" target="contentarea">
        <table class="fieldeditor">
          <tr ><td class="fieldcaption">Selecteer een nieuwe afbeelding :</td></tr>
<%    if (ls.size()>0) {
        for (NodeIterator i = ls.nodeIterator(); i.hasNext();) {
          Node devNode = i.nextNode();
%>
      <tr><td class="editfield">
          <input type="radio" name="chosenimage" value="<%=devNode.getStringValue("item1")%>">
          <a href="JavaScript:openWin('<%=devNode.getStringValue("item3")%>');"><img alt="Afbeelding" src="<%=devNode.getStringValue("item3")%>" width="70" height="50" /></a>
          <%=devNode.getStringValue("item2")%>
      </td></tr>
<%      } %>
          <tr>
            <td class="editfield">
                <input type="hidden" name="device" value="<%=device%>" />
                <input type="hidden" name="devicename" value="<mm:write referid="devicename" />" />
                <input type="hidden" name="field" value="<mm:write referid="field" />" />
                <input type="image" class="button" name="action" value="commit" src="../gfx/btn.red.gif" /> <%=translate(mmlanguage,"ok")%>
           </td>
         </tr>
<%   } else { %>
         <tr><td>Geen afbeeldingen aanwezig</td></tr>
<%   } %>
       </table>
     </form>
    <mm:transaction name="<%=transactionID%>" commitonclose="false">
      <mm:node number="<%=nodeID%>">
<%@include file="fieldhelp.jsp" %>
      </mm:node>
    </mm:transaction>
  </mm:notpresent>
  <mm:present referid="action">
    <mm:compare referid="action" value="commit" >
      <mm:import externid="chosenimage" vartype="String" jspvar="filename" required="true" />
<%
    Transaction trans=cloud.getTransaction(transactionID);
    Node node = trans.getNode(nodeID);

    // read the bytestream from the file
    java.io.File file = new java.io.File(filename);
    java.io.FileInputStream fis = new java.io.FileInputStream(file);
    byte[] bytes = new byte[(int)file.length()];	//Create a bytearray with a length the size of the filelength.
    fis.read(bytes);	//Read up to ba.length bytes of data from this inputstream into the bytearray ba.
    fis.close();

    node.setByteValue(fieldname,bytes);
    if (!"new".equals(currentState)) {
      currentState="edit";
      state.put("state",currentState);
    }
%>
    </mm:compare>
<%@include file="nextfield.jsp" %>
  </mm:present>
</mm:cloud>

