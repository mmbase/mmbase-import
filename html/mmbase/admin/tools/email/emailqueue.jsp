<%@ taglib uri="http://www.mmbase.org/mmbase-taglib-2.0" prefix="mm" 
%><%@page import="org.mmbase.bridge.*,java.util.Hashtable,org.mmbase.applications.email.*" 
%><%@ taglib uri="http://www.mmbase.org/mmbase-taglib-2.0" prefix="mm" %>
<mm:cloud rank="administrator" loginpage="login.jsp" jspvar="cloud">
<div
  class="mm_c mm_c_core mm_c_b_email ${requestScope.componentClassName}"
  id="${requestScope.componentId}">

  <h3>${mm:string(requestScope['org.mmbase.framework.state'].renderer.block.title)}</h3>
  <table summary="queued email" border="0" cellspacing="0" cellpadding="3">
    <caption>
      ${mm:string(requestScope['org.mmbase.framework.state'].renderer.block.description)}
    </caption>
    <tr>
      <th>second to mail</th>
      <th>to</th>
      <th>from</th>
      <th>subject</th>
    </tr>
    <%
        NodeManager email=null;
        try {
           email = cloud.getNodeManager("email");
        } catch (NotFoundException e) { 
    %> 
      <tr>
        <th>NodeManager Email NotFoundException</th><td><%= e %> </td>
      </tr>
    <% } %>
    <%--
    Hashtable params = new Hashtable();
    params.put("MAX", "1000");
    params.put("ITEMS", "5");
    NodeList msgs = email.getList("MEMTASKS", params);
    for (int i = 0; i < msgs.size(); i++) {
        Node msg = msgs.getNode(i);
    %>
        <tr>
          <td><%=msg.getValue("item2")%></td>
          <td><%=msg.getValue("item3")%></td>
          <td><%=msg.getValue("item4")%></td>
          <td>
            <mm:link page='<%="fullmail.jsp?msg="+msg.getValue("item1")%>'><a href="${_}"><%= msg.getValue("item5") %></a></mm:link>
          </td>
        </tr>
    <% } --%>
  </table>
  
  <ul>
    <li>
    
    </li>
  </ul>

  <p>
    <mm:link page="email">
      <a href="${_}"><mm:link page="/mmbase/style/images/back.png"><img src="${_}" alt="back" /></mm:link></a>
      <a href="${_}">Return to Email Monitor</a>
    </mm:link>
  </p>

</div>
</mm:cloud>
