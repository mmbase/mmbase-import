<%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" 
%><%@ taglib uri="http://www.mmbase.org/mmcommunity-taglib-1.0" prefix="mmcommunity" 
%><html><mm:cloud><body>
<mm:import externid="chatter" from="session" required="true">2607</mm:import>
<mm:import externid="chattername" from="session" required="true">test</mm:import>
<mm:import externid="startnode" from="parameters"  />
<script language="JavaScript">
<mm:notpresent referid="startnode">  
     lastnode=0;
    <mmcommunity:tree thread="Chat" max="20" directions="UP" fields="sequence" >
      <mm:remove referid="startnode" />
      <mm:import id="startnode"><mm:field name="sequence" /></mm:import>
    </mmcommunity:tree>    
</mm:notpresent>
<mm:present referid="startnode"> lastnode=<mm:write referid="startnode"/>;</mm:present>
<mmcommunity:tree thread="Chat" startaftersequence="${startnode}" directions="DOWN"     fields="sequence,timestampsec,body,info" >
    time = new Date(<mm:field name="timestampsec" /> * 1000);
    minut = time.getMinutes();
    top.chatbox.document.write(time.getHours() +  ":" + (minut < 10 ? "0" : "") + minut + "  <em><mm:field name="getinfovalue(name)" />:</em> <mm:field name="html(body)" /><br />");
    lastnode=<mm:field name="sequence" />+1;
</mmcommunity:tree>
    window.setInterval("call()", 5000, "JavaScript");
    top.chatbox.scrollBy(0, top.chatbox.innerHeight);
    function call() {
        location="chatloader.jsp?startnode="+lastnode;
    }
</script></mm:cloud></body></html>
