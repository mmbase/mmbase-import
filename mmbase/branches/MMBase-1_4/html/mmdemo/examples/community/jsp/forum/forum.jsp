<%@ taglib uri="http://www.mmbase.org/mmbase-taglib-0.8" prefix="mm"%>
<%@page import="org.mmbase.bridge.*"%>
<%@page import="java.util.*"%>
<html>
<head>
<title>Forum Demo</title>
</head>
<body>
<mm:cloud>
<%
    Module community= LocalContext.getCloudContext().getModule("communityprc");
    Node channelNode = cloud.getNodeByAlias("Forum");
    int channelnr = channelNode.getNumber();
%>
<blockquote>
<h2><%=channelNode.getValue("name")%></h2>
<%
    String chatter=(String)session.getAttribute("chattername");
    String email="";
    if (chatter==null) {
      Node chatternode=null;
      Cookie[] cookies = request.getCookies();
      for(int i=0; i<cookies.length; i++) {
	 Cookie cookie = cookies[i];
         if ("mmbase_chatter".equals(cookie.getName())) {
           try {
              String chatterid=cookie.getValue();
              chatternode=cloud.getNode(chatterid);
              NodeList rels= chatternode.getRelatedNodes("people");
              if (rels.size()>0) {
                chatter=rels.getNode(0).getStringValue("firstname")+" "+rels.getNode(0).getStringValue("lastname");
                email=rels.getNode(0).getStringValue("email");
              } else {
                chatter=chatternode.getStringValue("username");
                email=chatternode.getStringValue("email");
              }
           } catch (Exception e) {} 
         }
      }
      if (chatter!=null) {
  	session.putValue("chatter",chatternode);
  	session.putValue("chattername",chatter);
        if (email!=null) {
       	  session.putValue("chatteremail",email);
        }
      }
    }

    if (chatter==null) {
%>
<form action="login.jsp" method="post">
<p>Loginnaam: <input type="text" name="login" size="10">
Wachtwoord: <input type="password" name="pwd" size="10">
<input type="submit" value="Aanmelden">
</p>
</form>
<%  } else {
%>
<p><a href="#post">Plaats een bericht</a> als : <%=chatter%>  of <a href="logoff.jsp">log uit</a></p>
<%  }

    Hashtable params=new Hashtable();
    params.put("NODE",""+channelnr);
    params.put("TYPE","message");
    params.put("SORTFIELDS","sequence");
    params.put("MAXCOUNT","1150");
    params.put("SORTDIRS","UP");
    Vector v=new Vector();
    v.add("number");
    v.add("listhead");
    v.add("depth");
    v.add("listtail");
    v.add("subject");
    v.add("timestamp");
    v.add("replycount");
    v.add("getinfovalue(name)");

    params.put("FIELDS",v);
    NodeList ls= community.getList("TREE",params);

    for(NodeIterator nodes=ls.nodeIterator(); nodes.hasNext();) {
        Node node = nodes.nextNode();
%>
     <%=node.getValue("listhead")%>
     <li><a href="message.jsp?channel=<%=channelnr%>&amp;msg=<%=node.getValue("number")%>&amp;thread=<%=node.getValue("number")%>">
     <%=node.getValue("subject")%></a>
        <% if (!"0".equals(node.getStringValue("replycount"))) { %> (<%=node.getValue("replycount")%>) <% } %>
	<%=node.getValue("getinfovalue(name)")%>
	<% try {
             long timestamp = Long.parseLong(node.getStringValue("timestamp"))/1000; %>
	<small>-
         <%=LocalContext.getCloudContext().getModule("info").getInfo("TIME-"+timestamp+"-DAY")%>
         <%=LocalContext.getCloudContext().getModule("info").getInfo("TIME-"+timestamp+"-DUTCH-MONTH")%>
         <%=LocalContext.getCloudContext().getModule("info").getInfo("TIME-"+timestamp+"-YEAR")%>
         <%=LocalContext.getCloudContext().getModule("info").getInfo("TIME-"+timestamp+"-TIME")%>
        </small>
        <% } catch(Exception e) {}  %>
     </li>
     <%=node.getValue("listtail")%>
<%  }
%>
</p>
<% if (chatter!=null) { %>
<hr />
<a name="post"></a>
<p>Plaats een nieuw bericht:</p>
<form method="post" action="postmessage.jsp">
<input type="hidden" name="channel" value="<%=channelnr%>" />
<input type="hidden" name="thread" value="<%=channelnr%>" />
<input type="hidden" name="parent" value="<%=channelnr%>" />
<input type="text" name="subject" size="80" maxlength="80" />
<textarea name="body" cols="80" rows="20" wrap="on"> </textarea><br />
<input type="submit" name="action" value="post message">
</form>
<% } %>
</blockquote>
</mm:cloud>
</body>
</html>
