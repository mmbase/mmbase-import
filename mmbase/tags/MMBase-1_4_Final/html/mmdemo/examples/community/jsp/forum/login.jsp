<%@ taglib uri="http://www.mmbase.org/mmbase-taglib-0.8" prefix="mm"%>
<%@page import="org.mmbase.bridge.*"%>
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
	 NodeList rels = chatternode.getRelatedNodes("people");
	 if (rels.size()>0) {
            Node peoplenode=rels.getNode(0);
            chatter=peoplenode.getValue("firstname")+" "+peoplenode.getValue("lastname");
            email=peoplenode.getStringValue("email");
         } else {
  	    chatter=logon;
            email=chatternode.getStringValue("email");
         }
         session.setAttribute("chatter",chatternode);
         session.setAttribute("chattername",chatter);
         if (email!=null) {
             session.setAttribute("chatteremail",email);
         } else {
             session.removeAttribute("chatteremail");
         }
         Cookie cookie=new Cookie("mmbase_chatter",""+chatternode.getNumber());
         cookie.setMaxAge(60*60*24*7);
         response.addCookie(cookie);
       }
    }
%>
<html>
<head>
<title>Forum Demo: Login</title>
</head>
<body>
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
<p>U kunt nu berichten toevoegen aan <a href="forum.jsp">het MMBase Forum</a></p>
<% } %>
</blockquote>
</mm:cloud>
</body></html>
