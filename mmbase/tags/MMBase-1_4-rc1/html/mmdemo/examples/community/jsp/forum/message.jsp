<%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm"%>
<%@page import="org.mmbase.bridge.*"%>
<%@page import="java.util.*"%>
<html>
<head>
<title>Forum Demo : Message</title>
</head>
<body>
<blockquote>
<mm:cloud>
<%
    Node channelnode = cloud.getNodeByAlias("Forum");
    int channelnr=channelnode.getNumber();        
    Module community=LocalContext.getCloudContext().getModule("communityprc");
    String chatter = (String)session.getAttribute("chattername");
%>
<p><a href="forum.jsp">Terug naar het forum</a></p>
<%
    String thread = request.getParameter("thread");
    String msg = request.getParameter("msg");
    Node msgnode=cloud.getNode(msg);
%>
<p><strong><%=msgnode.getValue("subject")%></strong></p> 
<% String name=(String)msgnode.getValue("getinfovalue(name)");
   if (name!=null)  {
      String email=(String)msgnode.getValue("getinfovalue(email)");
      if ((email!=null) && (!"".equals(email))) {
           name="<a href=\"mailto:"+email+"\">"+name+"</a>";
      }
%>
Ingevuld door <%=name%> 
     <% try {
             long timestamp = Long.parseLong(msgnode.getStringValue("timestamp"))/1000;
     %> op
         <%=LocalContext.getCloudContext().getModule("info").getInfo("TIME-"+timestamp+"-DAY")%>
         <%=LocalContext.getCloudContext().getModule("info").getInfo("TIME-"+timestamp+"-DUTCH-MONTH")%>
         <%=LocalContext.getCloudContext().getModule("info").getInfo("TIME-"+timestamp+"-YEAR")%>
         <%=LocalContext.getCloudContext().getModule("info").getInfo("TIME-"+timestamp+"-TIME")%>
        <% } catch(Exception e) {}  %>   
<%  } %>
</p>

<p><%=msgnode.getValue("html(body)")%></p>

<%
    Hashtable params=new Hashtable();
    params.put("NODE",msg);
    params.put("MAXDEPTH","0");
    params.put("TYPE","message");
    params.put("SORTFIELDS","sequence");
    params.put("SORTDIRS","UP");
    Vector v=new Vector();
    v.add("number");
    v.add("subject");
    v.add("timestamp");
    v.add("getinfovalue(name)");

    params.put("FIELDS",v);
    NodeList ls= community.getList("TREE",params);
    if(ls.size()>0) {
%>
<p>Reacties:</p>
<ul>
<%
     for(NodeIterator nodes=ls.nodeIterator(); nodes.hasNext();) {
        Node node = nodes.nextNode();
%>
     <li><a href="message.jsp?channel=<%=channelnr%>&amp;msg=<%=node.getValue("number")%>&amp;thread=<%=node.getValue("number")%>">
     <%=node.getValue("subject")%></a>
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
<%   }
%></ul><%
    }
    if (chatter!=null) {
%>

<hr />
<p>Reageer op dit bericht:</p>
<form method="post" action="postmessage.jsp">
<input type="hidden" name="channel" value="<%=channelnr%>" />
<input type="hidden" name="thread" value="<%=thread%>" />
<input type="hidden" name="parent" value="<%=msg%>" />

<input type="text" name="subject" size="80" maxlength="80" value="<%=msgnode.getValue("subject")%>" />
<textarea name="body" cols="80" rows="20" wrap="on"> </textarea><br />
<input type="submit" name="action" value="post message">
</form>
<%  } %>

</blockquote>
</mm:cloud>
</body></html>
