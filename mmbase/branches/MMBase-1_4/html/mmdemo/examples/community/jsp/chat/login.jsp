<%@ taglib uri="http://www.mmbase.org/mmbase-taglib-0.8" prefix="mm" %>
<%@page import="org.mmbase.bridge.*" %>
<%@page import="java.util.*" %>
<mm:cloud>
<%
    String chatter=null;
    String email="";
    String logon=request.getParameter("login");
    String pwd=request.getParameter("pwd");
    if ((logon!=null) && (!"".equals(logon))) {
       NodeManager chatters=cloud.getNodeManager("chatter");
       NodeList ls = chatters.getList("username='"+logon+"' and password='"+pwd+"'",null,"");
       if(ls.size()>0) {
         Node chatternode=ls.getNode(0);
         Module community= LocalContext.getCloudContext().getModule("communityprc");
         Node channelNode = cloud.getNodeByAlias("Chat");
         int channelnr = channelNode.getNumber();
         community.getInfo("CHANNEL-"+channelnr+"-JOIN-"+chatternode.getNumber());
         NodeList rels = chatternode.getRelatedNodes("people");
         if (rels.size()>0) {
            Node peoplenode=rels.getNode(0);
            chatter=peoplenode.getValue("firstname")+" "+peoplenode.getValue("lastname");
         } else {
            chatter=logon;
         }
         session.setAttribute("chatter",chatternode);
         session.setAttribute("chattername",chatter);
       }
    }
%>
<html><body>
<blockquote>
<% if (chatter==null) { %>
<strong>Uw opgegegeven loginnaam en/of wachtwoord zijn incorrect.</strong>
<form action="login.jsp" method="post">
<p>Loginnaam: <input type="text" name="login" size="10">
Wachtwoord: <input type="password" name="pwd" size="10">
<input type="submit" value="Aanmelden">
</p>
</form>
<% } else { %>
<p>U bent nu ingelogd als <%=chatter%></p>
<p>U kunt nu berichten toevoegen aan <a href="chats.jsp">de MMBase chat</a></p>
<% } %>
</blockquote>
</mm:cloud>
</body></html>