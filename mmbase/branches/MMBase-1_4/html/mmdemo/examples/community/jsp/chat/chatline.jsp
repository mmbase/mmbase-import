<%@ taglib uri="http://www.mmbase.org/mmbase-taglib-0.8" prefix="mm" %>
<%@page import="org.mmbase.bridge.*" %>
<%@page import="java.util.*" %>
<html>
<script language="JavaScript">
 top.chatloader.call();

<mm:cloud>
<%
    Module community= LocalContext.getCloudContext().getModule("communityprc");
    Node channelNode = cloud.getNodeByAlias("Chat");
    int channelnr = channelNode.getNumber();
    String chatter=(String)session.getAttribute("chattername");
    Node chatternode=(Node) session.getAttribute("chatter");
    String thread = request.getParameter("thread");
    String parent = request.getParameter("parent");
    community.getInfo("CHANNEL-"+channelnr+"-STILLACTIVE-"+chatternode.getNumber());
    if (thread!=null) {
        String body=request.getParameter("body");
        if (body.equals("/who")) {

            Hashtable paramswho=new Hashtable();
            paramswho.put("CHANNEL",""+channelnr);
            Vector v=new Vector();
            v.add("username");
            v.add("email");
            paramswho.put("FIELDS",v);
            NodeList ls= community.getList("WHO",paramswho,request,response);
            int counter=0;
            for(NodeIterator nodes=ls.nodeIterator(); nodes.hasNext();) {
                Node who = nodes.nextNode();
%>
    top.chatbox.document.write("<%=who.getValue("username")%> (<%=who.getValue("email")%>)<br />");
<%
            }
        } else {
            if (body.startsWith("/nick ")) {
                String chattmpname=body.substring(6).trim();
                session.setAttribute("chattername",chattmpname);
                body="/me heet vanaf nu : "+chattmpname;
            }
            Hashtable params=new Hashtable();
            params.put("MESSAGE-BODY",body);
            params.put("MESSAGE-CHANNEL",""+channelnr);
            if (chatternode!=null) {
                params.put("MESSAGE-CHATTER",""+chatternode.getNumber());
            }
            params.put("MESSAGE-CHATTERNAME",chatter);
            community.process("MESSAGE-POST",thread,params,request,response);
        }
    }
%>
</script>
<body onLoad="document.chatlineform.body.focus();">
<a name="post"></a>
<form name="chatlineform" method="post" action="chatline.jsp">
<input type="hidden" name="channel" value="<%=channelnr%>" />
<input type="hidden" name="thread" value="<%=channelnr%>" />
<input type="hidden" name="parent" value="<%=channelnr%>" />
<input name="body" size="80" /><input type="submit" name="action" value="OK">
<a href="logoff.jsp" target="_top">Log off</a>
</form>
</mm:cloud>
</body></html>
