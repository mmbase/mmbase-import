<%@ taglib uri="http://www.mmbase.org/mmbase-taglib-0.8" prefix="mm" %>
<%@page import="org.mmbase.bridge.*" %>
<%@page import="java.util.*" %>
<html>
<head>
</head>
<body>
<mm:cloud>
<%
    Module community= LocalContext.getCloudContext().getModule("communityprc");
    Node channelNode = cloud.getNodeByAlias("Chat");
    int channelnr = channelNode.getNumber();
    String startnode=request.getParameter("startnode");
    String chatter=(String)session.getAttribute("chattername");
    Node chatternode=(Node) session.getAttribute("chatter");
    Hashtable params=new Hashtable();
    params.put("NODE",""+channelnr);
    params.put("TYPE","temporary message");
    if (startnode!=null) {
        params.put("STARTAFTERSEQUENCE",startnode);
        params.put("DBDIR","DOWN");
    } else {
        Hashtable params2=new Hashtable();
        params2.put("MESSAGE-BODY","/me zit nu op de mmbase chat.");
        params2.put("MESSAGE-CHANNEL",""+channelnr);
        if (chatternode!=null) {
            params2.put("MESSAGE-CHATTER",""+chatternode.getNumber());
        }
        params2.put("MESSAGE-CHATTERNAME",chatter);
        community.process("MESSAGE-POST",""+channelnr,params2,request,response);

        params.put("MAXCOUNT","1");
        params.put("DBDIR","UP");
    }
    Vector v=new Vector();
    v.add("sequence");
    v.add("timestamp");
    v.add("html(body)");
    v.add("getinfovalue(name)");
    params.put("FIELDS",v);
    NodeList ls= community.getList("TREE",params,request,response);

    String lastnodenr=startnode;
%>
<script language="JavaScript">
<%
    int counter=0;
    for(NodeIterator nodes=ls.nodeIterator(); nodes.hasNext();) {
        Node node = nodes.nextNode();
        lastnodenr=""+(node.getIntValue("sequence")+1);
        counter++;

        String body=node.getStringValue("html(body)");
        if (body.startsWith("/me ")) {

%>
    top.chatbox.document.write("<%=node.getIntValue("sequence")%> : <em><%=node.getValue("getinfovalue(name)")%> <%=body.substring(3)%></em><br />");
<%

        } else {
%>
    top.chatbox.document.write("<%=node.getIntValue("sequence")%> : <%=node.getValue("getinfovalue(name)")%> : <%=body%><br />");
<%      }
    }
    if (lastnodenr==null) {
%>
    function call() {
        location="chatloader.jsp";
    }
<%  } else {
%>
    lastnode="<%=lastnodenr%>";
    function call() {
        location="chatloader.jsp?startnode="+lastnode;
    }
<%  }
%>
    window.setInterval("call()", 5000, "JavaScript");
    top.chatbox.scrollBy(0,top.chatbox.innerHeight);
</script>

</mm:cloud>
</body></html>
