<%@ taglib uri="http://www.mmbase.org/mmbase-taglib-0.8" prefix="mm" %>
<%@ taglib uri="http://www.mmbase.org/mmcommunity-taglib-1.0" prefix="mmcommunity" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml/DTD/transitional.dtd">
<%@page import="org.mmbase.bridge.*" %>
<%@page import="java.util.*" %>
<mm:cloud>
<mm:import externid="channel" required="true" />
<%
    String chatter=null;
    String chattername=null;
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
         NodeList rels = chatternode.getRelatedNodes("people");
         if (rels.size()>0) {
            Node peoplenode=rels.getNode(0);
            chattername=peoplenode.getValue("firstname")+" "+peoplenode.getValue("lastname");
         } else {
            chattername=logon;
         }
         chatter=""+chatternode.getNumber();
         session.setAttribute("chatter",chatter);
         session.setAttribute("chattername",chattername);
       }
    }
%>
<html><body>
<blockquote>
<% if (chatter==null) { %>
<strong>Uw opgegeven loginnaam en/of wachtwoord zijn incorrect.</strong>
<form action="login.jsp" method="post">
<p>Loginnaam: <input type="text" name="login" size="10">
Wachtwoord: <input type="password" name="pwd" size="10">
<input type="submit" value="Aanmelden">
</p>
</form>
<% } else { %>
<mmcommunity:connection channel="${channel}" user="<%=chatter%>" action="join" />
<p>U bent nu ingelogd als <%=chattername%></p>
<p>U kunt nu berichten toevoegen aan <a href="<mm:url page="chats.jsp" referids="channel" />">de MMBase chat</a></p>
<% } %>
</blockquote>
</mm:cloud>
</body></html>