<%@ taglib uri="http://www.mmbase.org/mmbase-taglib-0.8" prefix="mm" %>
<%@ taglib uri="http://www.mmbase.org/mmcommunity-taglib-1.0" prefix="mmcommunity" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml/DTD/transitional.dtd">
<html xmlns="http://www.w3.org/TR/xhtml">
<head>
</head>
<body>
<mm:cloud>
<mm:import externid="channel" required="true" />
<mm:import externid="chatter" from="session" />
<mm:import externid="chattername" from="session" />
<mm:import externid="startnode" from="parameters" />
<script language="JavaScript">
     lastnode=<mm:write referid="startnode"/>+0;
<mm:notpresent referid="startnode">
    <mmcommunity:post>
        <mm:setfield name="username"><mm:write referid="chattername"/></mm:setfield>
        <mm:setfield name="user"><mm:write referid="chatter"/></mm:setfield>
        <mm:setfield name="channel"><mm:write referid="channel"/></mm:setfield>
        <mm:setfield name="body">/me has joined MMBase chat</mm:setfield>
    </mmcommunity:post>
    <mmcommunity:tree thread="${channel}" max="1" directions="UP" fields="sequence,body,info" >
      top.chatbox.document.write("<mm:field name="sequence" /> | <em><mm:field name="getinfovalue(name)" /> <mm:field name="html(body)" /></em><br />");
      lastnode=<mm:field name="sequence" />+1;
    </mmcommunity:tree>
</mm:notpresent>

<mm:present referid="startnode">
<mmcommunity:tree thread="${channel}"
    startaftersequence="${startnode}" directions="DOWN" fields="sequence,body,info" >
    top.chatbox.document.write("<mm:field name="sequence" /> | <em><mm:field name="getinfovalue(name)" /> : <mm:field name="html(body)" /></em><br />");
    lastnode=<mm:field name="sequence" />+1;
</mmcommunity:tree>
</mm:present>
    window.setInterval("call()", 5000, "JavaScript");
    top.chatbox.scrollBy(0,top.chatbox.innerHeight);

    function call() {
        location="chatloader.jsp?startnode="+lastnode+"&channel=<mm:write referid="channel"/>";
    }
</script>

</mm:cloud>
</body></html>
