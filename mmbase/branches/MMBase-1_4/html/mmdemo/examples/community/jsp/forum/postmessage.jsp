<%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm"%>
<%@page import="org.mmbase.bridge.*"%>
<%@page import="java.util.*"%>
<%
   String chatter = (String)session.getAttribute("chattername");
   if(chatter==null) {
      response.sendRedirect("forum.jsp");
      return;
   }
   String email = (String)session.getAttribute("chatteremail");
   
%>
<html>
<head>
<title>Forum Demo: Post Message</title>
</head>
<body>
<mm:cloud>
<blockquote>
<%
    String thread = request.getParameter("thread");
    Node channelnode = cloud.getNodeByAlias("Forum");
    int channelnr=channelnode.getNumber();        
    String parent = request.getParameter("parent");
    Module community=LocalContext.getCloudContext().getModule("communityprc");

    NodeManager channel = cloud.getNodeManager("channel");
    NodeManager msgs = cloud.getNodeManager("message");

    Node msg=msgs.createNode();
    msg.setValue("subject", request.getParameter("subject"));
    msg.setValue("body", request.getParameter("body"));
    msg.setValue("thread", ""+thread);
    msg.setValue("sequence", channel.getInfo(channelnr+"-NEWSEQ"));
    String info="name=\""+chatter+"\"";
    if ((email!=null) && !"".equals(email)) {
      info +=" email=\""+email+"\"";
    }
    msg.setValue("info",info);
    msg.commit();

    Node parentnode=cloud.getNode(parent);
    RelationManager relman=cloud.getRelationManager(parentnode.getNodeManager().getName(),"message","parent");
    Relation rel=relman.createRelation(parentnode,msg);
    rel.commit();

%>
<p><a href="forum.jsp">Terug naar het forum</a></p>
<p>Bericht geplaatst:</p>
<p><strong><%=msg.getValue("subject")%></strong></p>
<p><%=msg.getValue("html(body)")%></p>
</blockquote>
</mm:cloud>
</body></html>